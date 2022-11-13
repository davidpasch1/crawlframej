package rpick.crawl.frame.crawler.pipeline;

/**
 * Stage in a crawler pipeline.
 */
public interface Stage {
	
	/**
	 * Unique stage identifier in the pipeline.
	 * 
	 * @param identifier unique stage identifier in the pipeline
	 */
	public void setIdentifier(String identifier);
	
	/**
	 * Unique stage identifier in the pipeline.
	 * 
	 * @return unique stage identifier in the pipeline
	 */
	public String getIdentifier();
}
