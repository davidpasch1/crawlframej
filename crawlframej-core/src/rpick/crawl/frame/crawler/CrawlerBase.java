package rpick.crawl.frame.crawler;

import toolkit.err.ConfigException;

/**
 * Base for a crawler.   
 */
public abstract class CrawlerBase {
	
	/* Rationale:
	 * - info: what stages comprise pipeline, job types
	 * - init: pipeline is concrete => construction happens in concrete, derived class
	 *         otherwise cyclic init: base class expects constructed pipeline, where derived class has not yet been initialized
	 */

	
	/////////////////////////////////////////////////////////////////////////////
	// Protected Methods
	//
		
	protected abstract CrawlerPipeline getPipeline();
	
	/**
	 * Starts crawl.
	 */
	protected void crawlJobs() 
		throws 
			AbortCrawlException, 
			BrokenPipelineException, 
			ConfigException {
		
		/* Rationale:
		 * - note: generic parameter <CrawlJob> does not make part of signature!
		 * - named 'crawlJobs' to allow subclass to have a 'crawl' method
		 */
		
		CrawlerPipeline pipeline = getPipeline();
		
		pipeline.startUp();
		
		pipeline.processJobs();
		
//		createSummaryPage(pipeline.getCrawlJobs());
	}
}
