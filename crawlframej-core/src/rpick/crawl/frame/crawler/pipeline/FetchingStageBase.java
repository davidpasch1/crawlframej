package rpick.crawl.frame.crawler.pipeline;

import java.util.List;

import rpick.crawl.frame.crawler.AbortCrawlException;
import rpick.crawl.frame.crawler.BrokenPipelineException;
import rpick.crawl.frame.crawler.CrawlJob;

/**
 * Fetching stage of a crawler pipeline.
 * 
 * <p><b>Invariant:</b> The fetcher has to be started ({@link #startUp()}) before any of its methods can be used.
 */
public interface FetchingStageBase extends Stage {

	/* Rationale:
	 * - decouples crawler top layer from fetcher on lower layer
	 * - minimal interface that only contains what is needed by top layer
	 */
	
	/**
	 * Starts fetcher.
	 * 
	 * <p><b>Important:</b> Must be called before the fetcher can be used.
	 * <p><b>Note:</b> Can be safely called repeatedly.
	 */
	public void startUp();

	/**
	 * Tests whether there are pending crawl jobs.
	 * 
	 * @return whether there are pending crawl jobs
	 * 
	 * @throws IllegalStateException if fetcher has not been started
	 */
	public boolean hasPendingJobs() throws IllegalStateException;
	
	/**
	 * Enqueues crawl jobs for fetching.
	 * They are fetched in the order provided.
	 * 
	 * @param jobs crawl jobs
	 * 
	 * @throws IllegalStateException if fetcher has not been started
	 */
	public void enqueue(List<CrawlJob> jobs) 
			throws 
				IllegalStateException, 
				InterruptedException;
	
	/**
	 * <p><b>Postcondition:</b> adds result to job.
	 * 
	 * @return fetched crawl job with the result set
	 * 
	 * @throws IllegalStateException if fetcher has not been started
	 */
	public CrawlJob takeFetchedCrawlJob() 
		throws 
			IllegalStateException,
			AbortCrawlException, 
			AbortJobException, 
			BrokenPipelineException,
			InterruptedException;
}
