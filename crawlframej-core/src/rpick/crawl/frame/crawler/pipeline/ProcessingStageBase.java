package rpick.crawl.frame.crawler.pipeline;

import java.util.Collection;

import rpick.crawl.frame.crawler.AbortCrawlException;
import rpick.crawl.frame.crawler.BrokenPipelineException;
import rpick.crawl.frame.crawler.CrawlJob;

/**
 * Processing stage of a crawler pipeline.
 * Stages are organized as a tree.
 */
public interface ProcessingStageBase extends Stage {

	/* Rationale:
	 * - decouples crawler top layer from lower layer eg parser, writer
	 * - minimal interface that only contains what is needed by top layer
	 */
	
	/**
	 * Sets succeeding stages in processing pipeline.
	 * 
	 * @param successors succeeding stages
	 */
	public void setNextStages(Collection<ProcessingStageBase> successors);
	
	/**
	 * Sets succeeding stage in processing pipeline.
	 * 
	 * @param successor succeeding stage
	 */	
	public void setNextStage(ProcessingStageBase successor);
	
	/* Rationale:
	 * - point: in the case of a JobException sibling stages get a chance to process the job
	 *   reason: use_case: parallel stages parser and writer
	 *     => write fetched page to disk even if parsing fails 
	 */
	
	/**
	 * Processes input for given crawl job.

	 * <p><b>Error handling</b>:
	 * <ul>
	 *   <li>Original exception stored as job's result.</li>
	 *   <li>If a succeeding stage throws a {@link AbortJobException}, the other sibling stages still get a chance to process the job.
	 *       For the case of a {@link AbortCrawlException} this behavior is currently left undefined.</li>
	 *   <li>All exceptions that occur in the succeeding stages are accumulated in the eventual exception as suppressed exceptions with the cause set (see {@link Throwable}).
	 * </ul>
	 * 
	 * @param job crawl job that is currently being processed
	 * @param input input from preceding stage
	 */
	public void process(CrawlJob job, Object input) 
					throws 
						AbortJobException, 
						AbortCrawlException, 
						BrokenPipelineException;
}
