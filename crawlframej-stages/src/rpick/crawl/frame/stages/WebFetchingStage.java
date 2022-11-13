package rpick.crawl.frame.stages;

import java.net.URI;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.Pair;

import rpick.crawl.frame.crawler.AbortCrawlException;
import rpick.crawl.frame.crawler.CrawlJob;
import rpick.crawl.frame.crawler.pipeline.AbortJobException;
import rpick.crawl.frame.fetcher.FetchResult;
import rpick.crawl.frame.fetcher.WebFetchResult;
import rpick.crawl.frame.fetcher.WebpageFetcher;
import rpick.http.HttpException;
import rpick.http.HttpResponse;
import rpick.http.ServerFilteredException;
import rpick.http.ServerIOException;

/**
 * Fetches webpages.
 * 
 * <p><b>Note</b>: Stage runs in parallel to the rest of the pipeline.
 */
public class WebFetchingStage extends AbstractFetchingStage {

	/* Rationale:
	 * - decouples crawler subsystem from fetcher on lower layer
	 * - thread: InterruptedException: triggered dependent on VM implementation => always possible => must be handled
	 */
	
	private static final Logger log = LogManager.getLogger(WebFetchingStage.class);

	private static final int FETCH_JOBS_QUEUE_SIZE = 10000;
	private static final int FETCH_RESULTS_QUEUE_SIZE = 10000;
	
	/**
	 * URLs to be fetched.
	 */
	private final BlockingQueue<URI> fetchJobsQueue = new ArrayBlockingQueue<>(FETCH_JOBS_QUEUE_SIZE);

	private final BlockingQueue<FetchResult> fetchResultsQueue = new ArrayBlockingQueue<>(FETCH_RESULTS_QUEUE_SIZE);
	private final Queue<CrawlJob> pendingJobs = new ArrayDeque<>();
	private boolean isFetcherRunning = false;
	private final WebpageFetcher.Settings settings;
	

	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//
	
	/**
	 * @param settings settings for fetching
	 */
	public WebFetchingStage(WebpageFetcher.Settings settings) {
		
		super(new DefaultErrorHandler());
		this.settings = settings;
	}

	@Override
	public void startUp() {
	
		if (isFetcherRunning)
			return;
		
		WebpageFetcher fetcher = new WebpageFetcher(fetchJobsQueue, fetchResultsQueue, settings);
		Thread fetcherThread = new Thread(fetcher, "fetcher");
		fetcherThread.setDaemon(true);
		fetcherThread.start();
		
		isFetcherRunning = true;
		log.info("Fetcher thread running.");
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
		fillFetchJobsQueue(jobs);
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
		
		FetchResult result = fetchResultsQueue.take();
		CrawlJob fetchedJob = pendingJobs.remove();
		
		return Pair.with(fetchedJob, result);
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Private Methods
	//
	
	private void fillFetchJobsQueue(List<CrawlJob> jobs) throws InterruptedException {
		
		for (CrawlJob job : jobs) {
			
			if (fetchJobsQueue.remainingCapacity() == 0)
				log.warn("Fetch queue full.");
			
			fetchJobsQueue.put(job.getUrl());
		}
		log.debug("Added " + jobs.size() + " new jobs to fetch queue.");
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Public inner classes
	//
	
	/**
	 * Default error handler for fetching stage which expects HTTP status codes 2xx and turns all other codes into 
	 * {@link AbortJobException} or {@link AbortCrawlException}.
	 * An exception during fetching is understood as a fatal error ({@link AbortCrawlException}). 
	 */
	public static class DefaultErrorHandler implements ErrorHandler {

		@Override
		public void handle(FetchResult result) throws AbortCrawlException, AbortJobException {
			
			if (!(result instanceof WebFetchResult))
				throw new IllegalArgumentException(result.getClass().toString());
				
			WebFetchResult webResult = (WebFetchResult) result;
			HttpResponse httpResponse = webResult.getHttpResponse();
			Exception exception = webResult.getException();
			
			if (httpResponse != null) {
				
				/* HTTP status codes:
				 * Hypertext Transfer Protocol -- HTTP/1.1
				 * https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
				 */
				switch(httpResponse.getCode() / 100) {
					case 1:
						// Informational 1xx
						// This class of status code indicates a provisional response, consisting only of the Status-Line and optional headers, and is terminated by an empty line.
						throw new AbortJobException(new HttpException(httpResponse));
					case 2:
						// Successful 2xx
						// This class of status code indicates that the client's request was successfully received, understood, and accepted.
						return;
					case 3:
						// Redirection 3xx
						// This class of status code indicates that further action needs to be taken by the user agent in order to fulfill the request.
						throw new AbortJobException(new HttpException(httpResponse));
					case 4:
						// Client Error 4xx
						// The 4xx class of status code is intended for cases in which the client seems to have erred.
						throw new AbortJobException(new HttpException(httpResponse));
					case 5:
						// Server Error 5xx
						// Response status codes beginning with the digit "5" indicate cases in which the server is aware that it has erred or is incapable of performing the request.
						throw new AbortCrawlException(new HttpException(httpResponse));
					default:
						// Don't abort entire crawl, because of broken server.
						throw new AbortJobException(new Exception("Illegal HTTP code: " + httpResponse.getCode()));
				}
				
			} else if (exception != null) {
				
				// Entire crawl not aborted just because one site is down.
				if (exception instanceof ServerIOException
					|| exception instanceof ServerFilteredException)
					throw new AbortJobException(exception);
				else if (exception instanceof RuntimeException)
					throw (RuntimeException) exception;
				
				throw new AbortCrawlException(exception);
				
			} else
				// Program error.
				throw new RuntimeException("Broken fetch result.");
		}
	}
}
