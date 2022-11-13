package rpick.crawl.frame.stages;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.Pair;

import rpick.crawl.frame.crawler.AbortCrawlException;
import rpick.crawl.frame.crawler.CrawlJob;
import rpick.crawl.frame.crawler.pipeline.AbortJobException;
import rpick.crawl.frame.fetcher.DiskFetchResult;
import rpick.crawl.frame.fetcher.DiskFetcher;
import rpick.crawl.frame.fetcher.FetchResult;
import toolkit.time.Timing;

/**
 * Fetches content from disk.
 */
public class DiskFetchingStage extends AbstractFetchingStage {

	private static final Logger log = LogManager.getLogger(DiskFetchingStage.class);
	
	private final Timing timing = new Timing();
	private final Queue<CrawlJob> pendingJobs = new ArrayDeque<>();
	private final DiskFetcher diskFetcher = new DiskFetcher();
	private boolean isFetcherRunning = false;
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//
	
	public DiskFetchingStage() {
		super(new DefaultErrorHandler());
	}

	@Override
	public void startUp() {
		isFetcherRunning = true;
	}
	
	@Override
	public boolean hasPendingJobs() throws IllegalStateException {

		if (!isFetcherRunning())
			throw new IllegalStateException("Fetcher has not been started.");

		return !pendingJobs.isEmpty();
	}
	
	@Override
	public void enqueue(List<CrawlJob> jobs)
			throws 
				IllegalStateException,
				InterruptedException {

		if (!isFetcherRunning())
			throw new IllegalStateException("Fetcher has not been started.");

		pendingJobs.addAll(jobs);
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// AbstractFetchingStage
	//
	
	@Override
	protected boolean isFetcherRunning() {
		return isFetcherRunning;
	}
	
	@Override
	protected Pair<CrawlJob, FetchResult> takeFetchedJobAndResult() throws InterruptedException {
	
		CrawlJob job = pendingJobs.remove();
		FetchResult result = timing.elapsed((Supplier<
				FetchResult	
			>)
			() -> {
				return diskFetcher.read(job.getUrl());
			});
		log.info("Fetching Elapsed: " + timing.getTime() + "ms");

		return Pair.with(job, result);
	}

	
	/////////////////////////////////////////////////////////////////////////////
	// Private Methods
	//
	

	/////////////////////////////////////////////////////////////////////////////
	// Public inner classes
	//

	/**
	 * Default error handler for fetching stage.
	 * An exception during fetching is not understood as a fatal error ({@link AbortJobException}).
	 */
	public static class DefaultErrorHandler implements ErrorHandler {

		@Override
		public void handle(FetchResult result) throws AbortCrawlException, AbortJobException {
			
			if (!(result instanceof DiskFetchResult))
				throw new IllegalArgumentException(result.getClass().toString());
				
			DiskFetchResult diskResult = (DiskFetchResult) result;
			
			Exception exception = diskResult.getException();
			if (exception != null) {
				if (exception instanceof RuntimeException)
					throw (RuntimeException) exception;
				throw new AbortJobException(exception);
			}
		}
	}
}
