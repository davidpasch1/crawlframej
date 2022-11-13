package rpick.crawl.frame.stages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import rpick.crawl.frame.crawler.BrokenPipelineException;
import rpick.crawl.frame.crawler.CrawlJob;

import rpick.crawl.frame.fetcher.DiskFetchResult;
import toolkit.err.ErrorMessages;
import toolkit.net.WebTools;

/**
 * Extracts content that has been read from disk for further processing.
 */
public class DiskContentExtractorStage extends AbstractProcessingStage {

	private static final Logger log = LogManager.getLogger(DiskContentExtractorStage.class);

	
	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//

	public DiskContentExtractorStage() {
		super(log);
	}

	
	/////////////////////////////////////////////////////////////////////////////
	// AbstractProcessingStage
	//

	/**
	 * {@inheritDoc}
	 * 
	 * @param input input of type {@link DiskFetchResult}
	 * @return content that has been read from disk ({@link BinaryContent}); may be null
	 */
	@Override
	protected Object processData(CrawlJob job, Object input) throws StageProcessingException, BrokenPipelineException {
	
		if (!(input instanceof DiskFetchResult))
			throw new BrokenPipelineException(
					this,
					"Invalid stage input: "
					+ ErrorMessages.getTypeMismatchString(
						input.getClass(),
						DiskFetchResult.class));
		DiskFetchResult result = (DiskFetchResult) input;
		
		byte[] content = result.getContent();
		Exception exception = result.getException();
		
		if (content != null)
			// ISSUE: content_type: Plain pass-through
			return new BinaryContent(
				new ContentType(
					null, 
					WebTools.getFileExt(job.getUrl())),
				content);
		else if (exception != null)
			log.debug(exception);
		else
			log.error("Broken fetch result: without response and exception.");
		
		return null;
	}	
}
