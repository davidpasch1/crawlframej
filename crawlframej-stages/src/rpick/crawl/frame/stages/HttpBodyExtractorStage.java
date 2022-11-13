package rpick.crawl.frame.stages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import rpick.crawl.frame.crawler.BrokenPipelineException;
import rpick.crawl.frame.crawler.CrawlJob;
import rpick.crawl.frame.fetcher.WebFetchResult;
import rpick.http.HttpResponse;
import toolkit.err.ErrorMessages;
import toolkit.net.WebTools;
import toolkit.net.mime.HttpContentType;

/**
 * Extracts HTTP Response body for further processing.
 */
public class HttpBodyExtractorStage extends AbstractProcessingStage {

	private static final Logger log = LogManager.getLogger(HttpBodyExtractorStage.class);
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//

	public HttpBodyExtractorStage() {
		super(log);
	}

	
	/////////////////////////////////////////////////////////////////////////////
	// AbstractProcessingStage
	//

	/**
	 * {@inheritDoc}
	 * 
	 * <p><b>Side-effect:</b> Updates job's URL to final URL in case of redirections.
	 * 
	 * <p><b>Note:</b> Rejects resources without HTTP Content-Type or body.
	 * 
	 * @param job job, whose URL may be updated
	 * @param input input of type {@link WebFetchResult}
	 * @return HTTP Response body ({@link BinaryContent}); maybe null
	 */
	@Override
	protected Object processData(CrawlJob job, Object input) throws StageProcessingException, BrokenPipelineException {
		
		if (!(input instanceof WebFetchResult))
			throw new BrokenPipelineException(
					this,
					"Invalid stage input: "
					+ ErrorMessages.getTypeMismatchString(
						input.getClass(),
						WebFetchResult.class));
		WebFetchResult result = (WebFetchResult) input;
		HttpResponse httpResponse = result.getHttpResponse();
		Exception exception = result.getException();
		
		if (httpResponse != null) {
			
			log.debug(httpResponse.toHeadString());
			
			if (!job.getUrl().equals(httpResponse.getFinalUrl()))
				job.setUrl(httpResponse.getFinalUrl());
			
			String httpContentType = httpResponse.getContentType();
			if (httpContentType == null) {
				log.warn("Rejected resource due to missing HTTP Content-Type.");
				return null;
			}
			ContentType contentType = new ContentType(
					new HttpContentType(httpContentType),
					WebTools.getFileExt(job.getUrl()));

			if (httpResponse.getBody() == null) {
				log.warn("Rejected resource due to missing body.");
				return null;
			}
			
			// ISSUE: content_type: Plain pass-through
			return new BinaryContent(
				contentType,
				httpResponse.getBody());
			
		} else if (exception != null)
			log.debug(exception);
		else
			log.error("Broken fetch result: without response and exception.");
		
		return null;
	}
}
