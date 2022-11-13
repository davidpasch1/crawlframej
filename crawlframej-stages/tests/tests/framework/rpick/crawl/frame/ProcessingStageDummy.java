package tests.framework.rpick.crawl.frame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import rpick.crawl.frame.crawler.BrokenPipelineException;
import rpick.crawl.frame.crawler.CrawlJob;
import rpick.crawl.frame.stages.AbstractProcessingStage;

/**
 * Produces a predetermined result depending on current job.
 */
public class ProcessingStageDummy extends AbstractProcessingStage {

	private static final Logger log = LogManager.getLogger(ProcessingStageDummy.class);
	
	private ProcessingFunction processingFunction = (job, input) -> null;
	
	public ProcessingStageDummy(String id) {
		super(log);
		setIdentifier(id);
	}
	
	public void setProcessingFunction(ProcessingFunction fct) {
		processingFunction = fct;
	}

	@Override
	protected Object processData(CrawlJob job, Object input) 
		throws 
			StageProcessingException,
			BrokenPipelineException {

		try { 
			
			return processingFunction.processValidatedInput(job, input);
		
		} catch (Exception e) {
			throw new StageProcessingException(e);
		}
	}
	
	@FunctionalInterface
	public interface ProcessingFunction {
		
		public Object processValidatedInput(CrawlJob job, Object input)
			throws 
				SomeException,
				BrokenPipelineException;
	}
	
	@SuppressWarnings("serial")
	public static class SomeException extends Exception {

		public SomeException(String message) {
			super(message);
		}
	}
}
