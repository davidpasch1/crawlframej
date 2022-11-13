package rpick.crawl.frame.stages;

import rpick.crawl.frame.crawler.AbortCrawlException;
import rpick.crawl.frame.crawler.pipeline.AbortJobException;
import rpick.crawl.frame.crawler.pipeline.FetchingStageBase;
import rpick.crawl.frame.fetcher.FetchResult;

/**
 * Fetching stage on the middle-layer (see Specification).
 */
public interface FetchingStage extends FetchingStageBase {
	
	/* Rationale:
	 * - interface that contains what is needed by middle layer
	 * - here dependency on lower layers is allowed
	 */
	
	public void setErrorHandler(ErrorHandler errorHandler);
	
	public ErrorHandler getErrorHandler();
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Public inner classes
	//
	
	/**
	 * Specifies how to handle errors that occur during processing in a stage.
	 */
	@FunctionalInterface
	public interface ErrorHandler {
		
		public void handle(FetchResult result) throws AbortCrawlException, AbortJobException;
	}
}
