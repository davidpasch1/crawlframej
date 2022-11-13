package rpick.crawl.frame.samples.crawler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import rpick.crawl.frame.crawler.BrokenPipelineException;
import rpick.crawl.frame.crawler.CrawlJob;

import rpick.crawl.frame.stages.AbstractProcessingStage;

/**
 * Stage that uses a generic parser to parse the HTML content.
 */
public class GeneralParserStage extends AbstractProcessingStage {
	
	private static final Logger log = LogManager.getLogger(GeneralParserStage.class);


	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//
	
	public GeneralParserStage() {
		super(log);
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// AbstractProcessingStage
	//

	@Override
	protected Object processData(CrawlJob job, Object input) throws StageProcessingException, BrokenPipelineException {
		
		return new Object();
	}
}
