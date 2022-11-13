package rpick.crawl.frame.stages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rpick.crawl.frame.common.CrawlerConfig;
import rpick.crawl.frame.crawler.AbortCrawlException;
import rpick.crawl.frame.crawler.BrokenPipelineException;
import rpick.crawl.frame.crawler.CrawlJob;
import rpick.crawl.frame.crawler.pipeline.AbortJobException;
import rpick.crawl.frame.crawler.pipeline.ProcessingStageBase;
import rpick.logging.Log4jManager;
import rpick.logging.Logger;
import toolkit.function.SupplierEx2;
import toolkit.net.mime.HttpContentType;
import toolkit.net.mime.MediaType;
import toolkit.net.mime.MimeFileExtMap;
import toolkit.time.Timing;

public abstract class AbstractProcessingStage implements ProcessingStage {
		
	private final Logger log;
	private final Timing timing = new Timing();
	
	private List<ProcessingStageBase> nextStages = new ArrayList<ProcessingStageBase>();
	private String identifier = this.getClass().getName();
	private ErrorHandler errorHandler = exception -> {throw new AbortJobException(exception);};
	

	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//

	public AbstractProcessingStage(org.apache.logging.log4j.Logger log) {
		this.log = Log4jManager.create(log);
	}
	

	/////////////////////////////////////////////////////////////////////////////
	// ProcessingStage
	//
	
	/**
	 * <b>Default:</b> Class name.
	 */
	@Override
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	@Override
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * <p><b>Default:</b> throws {@link AbortJobException}.
	 */	
	@Override
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}
	
	@Override
	public void setNextStages(Collection<ProcessingStageBase> successors) {
		nextStages.clear();
		nextStages.addAll(successors);
	}

	@Override
	public void setNextStage(ProcessingStageBase successor) {
		nextStages.clear();
		nextStages.add(successor);
	}
	
	@Override
	public void process(CrawlJob job, Object input) 
		throws 
			AbortJobException, 
			AbortCrawlException, 
			BrokenPipelineException {
		
		Object result;
		try {
		
			result = timing.elapsed((SupplierEx2<
					Object,
					StageProcessingException,
					BrokenPipelineException
				>)
				() -> {
					return processData(job, input);
				});
			log.perfDebug("Elapsed: " + timing.getTime() + "ms");
			
			if (result == null) {
				
				log.debug("Terminal stage: Skipping successive stages.");
				return;
			}

		} catch (StageProcessingException e) {
			
			Exception cause = (Exception) e.getCause();
			job.setResult(identifier, cause);
			errorHandler.handle(cause);
			return;
		}
		
		
		AbortJobException jobException = null;		// collects job exceptions from succeeding stages
		AbortCrawlException crawlException = null;	// collects crawl exceptions from succeeding stages
		
		for (ProcessingStageBase stage : nextStages)
		{
			try {
				
				stage.process(job, result);
				
			} catch (AbortJobException e) {
				
				if (jobException == null)
					jobException = new AbortJobException(e);
				else
					jobException.addSuppressed(e);
				
			} catch (AbortCrawlException e) {
				
				if (crawlException == null)
					crawlException = new AbortCrawlException(e);
				else
					crawlException.addSuppressed(e);				
			}
		}
		
		// there was at least one crawl exception and possibly job exceptions
		if (crawlException != null) {
			
			if (jobException != null)
				crawlException.addSuppressed(jobException);
			
			throw crawlException;
		}
		// there have been only job exceptions
		else if (jobException != null) {
			
			throw jobException;
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Protected Methods
	//
	
	/**
	 * Processes the input data.
	 * 
	 * <p><b>Note</b>: Optionally stores the result in the job. 
	 * Does <i>not</i> store the {@link StageProcessingException} in the job as this is done by caller automatically.
	 * 
	 * <p><b>Note</b>: Program errors are <i>not</i> conveyed as {@link StageProcessingException},
	 * but as ordinary {@link RuntimeException}.
	 * 
	 * @param job crawl job that is currently being processed
	 * @param input input data
	 * @return result of processing; null if terminal stage
	 */
	protected abstract Object processData(CrawlJob job, Object input) 
		throws 
			StageProcessingException, 
			BrokenPipelineException;
	
	// ISSUE: content_type: Stage acceptance
	/**
	 * Whether content is accepted by stage.
	 * 
	 * <p>Content is accepted, iff it's media type is supported 
	 * or in the case of absent media type, it's extension belongs to the supported media type.  
	 * 
	 * <p><b>Note:</b> Media type takes precedence over file extension.
	 * <p><b>Note:</b> Content with neither a media type nor a file extension is rejected.
	 * 
	 * @param contentType content's type
	 * @param supportedMediaType supported media type
	 * @return whether content is accepted by stage
	 */
	protected boolean isAccepted(
			ContentType contentType, 
			MediaType supportedMediaType) {

		HttpContentType httpContentType  = contentType.getHttpContentType();
		MediaType mediaType = httpContentType != null ? httpContentType.getMediaType() : null;
		String fileExt = contentType.getFileExt();
		MimeFileExtMap map = CrawlerConfig.get().getMimeFileExtMap();
		List<String> supportedFileExts = map.getFileExts(supportedMediaType);
		boolean isSupportedContent = 
				supportedMediaType.equals(mediaType)
				|| (fileExt != null && supportedFileExts.contains(fileExt));
		if (!isSupportedContent) {
			log.debug("Rejected unsupported content: " + mediaType + "; file ext: " + fileExt);
			return false;
		}
		
		return true;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Protected inner classes
	//
	
	/**
	 * If processing of input failed.
	 * 
	 * <p><b>Note:</b> Wrapper for tunneling concrete {@link Exception} or {@link RuntimeException}
	 * that occurred during processing of a job in stage.
	 */
	@SuppressWarnings("serial")
	protected class StageProcessingException extends Exception {

		public StageProcessingException(Exception cause) {
			super(cause);
		}
	}
}