package rpick.crawl.frame.stages;

import rpick.crawl.frame.crawler.AbortCrawlException;
import rpick.crawl.frame.crawler.BrokenPipelineException;
import rpick.crawl.frame.crawler.CrawlJob;
import rpick.crawl.frame.crawler.pipeline.AbortJobException;
import rpick.crawl.frame.crawler.pipeline.ProcessingStageBase;

/**
 * Processing stage on the middle-layer (see Specification).
 */

public interface ProcessingStage extends ProcessingStageBase {

	/* Rationale:
	 * - interface that contains what is needed by middle layer
	 * - here dependency on lower layers is allowed
	 */
	
	public void setErrorHandler(ErrorHandler errorHandler);
	
	/**
	 * {@inheritDoc}
	 * 
	 * <b>Note</b>: Error handler determines wrapping exception that gets eventually thrown.
	 */
	@Override
	public void process(CrawlJob job, Object input) 
			throws 
				AbortJobException, 
				AbortCrawlException, 
				BrokenPipelineException;
	
	/////////////////////////////////////////////////////////////////////////////
	// Public inner classes
	//
	
	/**
	 * Specifies how to handle errors that occur during processing in a stage.
	 */
	@FunctionalInterface
	public interface ErrorHandler {
		
		/* Rationale:
		 * - decision: response to error in pipeline
		 */
		
		/**
		 * Handles exception that occurred during processing in a stage.
		 * 
		 * @param e  exception that occurred
		 * @return processing result if no exception gets thrown
		 */
		public Object handle(Exception e) throws AbortJobException, AbortCrawlException;
	}
}
