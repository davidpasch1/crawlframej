package tests.framework.rpick.crawl.frame;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import org.javatuples.Pair;

import rpick.crawl.frame.crawler.CrawlJob;
import rpick.crawl.frame.fetcher.FetchResult;
import rpick.crawl.frame.stages.AbstractFetchingStage;

/**
 * Dummy for fetching stage.
 * Returns {@link FetchResult} from job's stage parameters.
 */
public class FetchingStageDummy extends AbstractFetchingStage {

	private Queue<CrawlJob> jobQueue = new ArrayDeque<>();
	private boolean isFetcherRunning = false;
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//
	
	public FetchingStageDummy(
			String id,
			ErrorHandler defaultErrorHandler) {
		
		super(defaultErrorHandler);
		setIdentifier(id);
	}

	@Override
	public void startUp() {
		isFetcherRunning = true;
	}

	@Override
	public boolean hasPendingJobs() {

		if (!isFetcherRunning())
			throw new IllegalStateException("Fetcher has not been started.");

		return !jobQueue.isEmpty();
	}

	@Override
	public void enqueue(List<CrawlJob> jobs)
			throws 
				IllegalStateException,
				InterruptedException {
		
		if (!isFetcherRunning())
			throw new IllegalStateException("Fetcher has not been started.");

		jobQueue.addAll(jobs);
	}

	@Override
	protected Pair<CrawlJob, FetchResult> takeFetchedJobAndResult() throws InterruptedException {

		CrawlJob fetchedJob = jobQueue.remove();
		FetchResult result = (FetchResult) fetchedJob.getStageParams(getIdentifier());
		
		return Pair.with(fetchedJob, result);
	}

	@Override
	protected boolean isFetcherRunning() {
		return isFetcherRunning;
	}
}
