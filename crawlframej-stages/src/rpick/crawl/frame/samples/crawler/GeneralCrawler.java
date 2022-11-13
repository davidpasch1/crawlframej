package rpick.crawl.frame.samples.crawler;

import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import rpick.crawl.frame.crawler.AbortCrawlException;
import rpick.crawl.frame.crawler.CrawlerBase;
import rpick.crawl.frame.crawler.CrawlerPipeline;
import rpick.crawl.frame.fetcher.WebpageFetcher;
import rpick.crawl.frame.stages.DiskWriterStage;
import rpick.crawl.frame.stages.FetchingStage;
import rpick.crawl.frame.stages.HttpBodyExtractorStage;
import rpick.crawl.frame.stages.JobsGeneratorStage;
import rpick.crawl.frame.stages.ProcessingStage;
import rpick.crawl.frame.stages.WebFetchingStage;

/**
 * Crawler that fetches webpages in a BFS manner and stores them on disk.
 */
public class GeneralCrawler extends CrawlerBase {

	@SuppressWarnings("unused")
	private static final Logger log = LogManager.getLogger(GeneralCrawler.class);
	private static final String RAW_DIR = "raw";
	
	private final FetchingStage fetchingStage;
	private final GeneralCrawlStrategy crawlStrategy = new GeneralCrawlStrategy();
	private final CrawlerPipeline pipeline;
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//
	
	/**
	 * @param outPath output path
	 */
	public GeneralCrawler(
			WebpageFetcher.Settings settings,
			Path outPath) {
		
		fetchingStage = new WebFetchingStage(settings);
		
		pipeline = new CrawlerPipeline(
				fetchingStage, 
				createProcessingStages(outPath),
				crawlStrategy);
		
		configureJobsStageParams();
	}
	
	/**
	 * Starts crawl.
	 * 
	 * @param urls URLs to start with
	 * @throws Exception on error during processing
	 */
	public void crawl(List<URI> urls) throws Exception {
		
		crawlStrategy.createJobs(urls);
		
		try {
		
			crawlJobs();
			
		} catch (AbortCrawlException e) {
			// ASSUMPTION: only writer may throw an exception
			throw (Exception)e.getCause();
		}
	}

	
	/////////////////////////////////////////////////////////////////////////////
	// CrawlerBase
	//
	
	@Override
	protected CrawlerPipeline getPipeline() {
		return pipeline;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Private Methods
	//
	
	/* Pipeline:
	 * 
	 * fetcher --- body_extractor --- parser --- jobs_generator
	 *                            |-- writer
	 */
	private ProcessingStage createProcessingStages(Path outPath) {
		
		HttpBodyExtractorStage httpBodyStage = new HttpBodyExtractorStage();
		GeneralParserStage parserStage = new GeneralParserStage();
		JobsGeneratorStage jobsgenStage = new JobsGeneratorStage(crawlStrategy);
		DiskWriterStage writerStage = new DiskWriterStage(outPath.resolve(RAW_DIR), true);
		
		httpBodyStage.setNextStages(
				Arrays.asList(
					parserStage,
					writerStage));
		
		parserStage.setNextStage(jobsgenStage);
		
		return httpBodyStage;
	}
	
	private void configureJobsStageParams() {
		
	}
}
