package rpick.crawl.frame.crawler;

import java.util.List;

/**
 * Strategy used to direct crawl, ie it decides which URLs to fetch next.
 * 
 * <p><b>Note</b>: Central point for job generation.
 */
public interface CrawlStrategy {

	/* Rationale:
	 * - decision: how to crawl site
	 * - purpose: 
	 *   - job generation 
	 *   - direct the crawl
	 * - goals:
	 *   - support for different strategies for exploring the web
	 *   - support for different representation of jobs
	 *   - support for simple pipeline without web exploration eg downloader, disk scanning parser
	 * - rationale:
	 *   - exchangeable crawling strategy for different application scenarios, strategies for web exploration
	 *   - by being the central point for job generation, all URLs pass here through.
	 *     this allows, for instance, for system_wide URL checking.
	 */
	
	/**
	 * @return whether there are more crawl jobs
	 */
	public boolean hasJobs();
	
	/**
	 * Retrieves next jobs to be processed.
	 * 
	 * <p><b>Warning:</b> It is expected that the jobs will be processed in the order provided!!!
	 * <p><b>Side-effect:</b> Jobs are removed from the queue (hand-out jobs and forget).
	 * 
	 * @return next set of crawl jobs that are to be processed
	 */
	public List<CrawlJob> getNextJobs();
	
	/**
	 * Creates new jobs based on result of the current crawl job.
	 * 
	 * @param job crawl job
	 * @param content content that belongs to the crawl job
	 */
	public void createJobs(CrawlJob job, Object content);
}
