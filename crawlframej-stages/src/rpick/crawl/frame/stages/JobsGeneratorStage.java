package rpick.crawl.frame.stages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import rpick.crawl.frame.crawler.BrokenPipelineException;
import rpick.crawl.frame.crawler.CrawlJob;
import rpick.crawl.frame.crawler.CrawlStrategy;


/**
 * Generates jobs according to crawling strategy, and feeds the fetching stage.
 * 
 * @see CrawlStrategy
 * @see FetchingStage
 */
public class JobsGeneratorStage extends AbstractProcessingStage {

	private static final Logger log = LogManager.getLogger(JobsGeneratorStage.class);
	
	private CrawlStrategy crawlStrategy;

	
	/////////////////////////////////////////////////////////////////////////////
	// Constructor
	//
	
	/**
	 * @param crawlStrategy crawling strategy used to direct crawl
	 */
	public JobsGeneratorStage(CrawlStrategy crawlStrategy) {
		super(log);
		this.crawlStrategy = crawlStrategy;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// AbstractProcessingStage
	//
	
	/**
	 * {@inheritDoc}
	 *
	 * <p><b>Note:</b> Parameters are passed on to crawling strategy (see {@link CrawlStrategy#createJobs(CrawlJob, Object)}).
	 */
	@Override
	protected Object processData(CrawlJob job, Object input) throws StageProcessingException, BrokenPipelineException {
		
		try {
		
			crawlStrategy.createJobs(job, input);
		
		} catch (Exception e) {
			throw new StageProcessingException(e);
		}
		
		// terminal stage
		return null;
	}	
}
