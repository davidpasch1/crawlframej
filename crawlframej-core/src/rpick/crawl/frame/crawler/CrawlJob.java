package rpick.crawl.frame.crawler;

import java.net.URI;
import java.util.List;

/**
 * Serves as token in pipeline.
 * Conveys parameters and results for and from the individual stages.
 */
public interface CrawlJob {
	
	/* Rationale:
	 * - crawl job decoupled from stages
	 *   => adding a stage does not affect this class
	 * - info: about type of job
	 * - stage passed as String rather than as Stage to make user aware that behavior depends on stage's identifier 
	 */
	
	/**
	 * <p><b>Note</b>: URL may change, eg due to redirections.
	 * 
	 * @param url  new URL representing this job
	 */
	public void setUrl(URI url);
	
	/**
	 * <p><b>Note</b>: subject to change in course of processing.
	 * 
	 * @return URL represented by this job
	 */
	public URI getUrl();
	
	/**
	 * @return old URLs that represented this job, starting with the oldest, ie the original one 
	 */
	public List<URI> getOldUrls();
	
	/**
	 * @param stage stage identifier
	 * @return parameters
	 * @throws BrokenPipelineException if stage parameters missing
	 */
	public Object getStageParams(String stage) throws BrokenPipelineException;
	
	/**
	 * @param stage identifier of stage that has produced result
	 * @param result result of a stage for this job
	 * @throws BrokenPipelineException if result has already been set for this stage
	 */
	public void setResult(String stage, Object result) throws BrokenPipelineException;
	
	/**
	 * @param stage stage identifier
	 * @return result of a stage for this job; null if not present
	 */
	public Object getResult(String stage);
	
}
