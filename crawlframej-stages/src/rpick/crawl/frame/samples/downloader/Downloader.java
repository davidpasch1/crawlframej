package rpick.crawl.frame.samples.downloader;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import rpick.crawl.frame.crawler.AbortCrawlException;
import rpick.crawl.frame.crawler.CrawlerBase;
import rpick.crawl.frame.crawler.CrawlerPipeline;
import rpick.crawl.frame.crawler.DownloadCrawlStrategy;
import rpick.crawl.frame.fetcher.WebpageFetcher;
import rpick.crawl.frame.stages.DiskWriterStage;
import rpick.crawl.frame.stages.FetchingStage;
import rpick.crawl.frame.stages.HttpBodyExtractorStage;
import rpick.crawl.frame.stages.ProcessingStage;
import rpick.crawl.frame.stages.WebFetchingStage;

/**
 * Downloads web content.
 */
public class Downloader extends CrawlerBase {

	@SuppressWarnings("unused")
	private static final Logger log = LogManager.getLogger(Downloader.class);
	private static final String RAW_DIR = "raw";
	
	// TODO POSTPONED convert to local variable; fix all other CrawlerBase subclasses too
	private final FetchingStage fetchingStage;
	
	private final DownloadCrawlStrategy crawlStrategy = new DownloadCrawlStrategy();
	private final CrawlerPipeline pipeline;
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//
	
	/**
	 * @param outPath output path
	 */
	public Downloader(
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
	 * Downloads the webpages.
	 * 
	 * @param urls webpages to download
	 * @throws Exception on error during processing
	 */
	public void download(List<URI> urls) throws Exception {
		
		crawlStrategy.createJobs(urls);
		
		try {
		
			crawlJobs();
			
		} catch (AbortCrawlException e) {
			// there is only one path in the pipeline => single cause
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
	 * fetcher --- body_extractor --- writer
	 */
	private ProcessingStage createProcessingStages(Path outPath) {
		
		HttpBodyExtractorStage httpBodyStage = new HttpBodyExtractorStage();
		DiskWriterStage writerStage = new DiskWriterStage(outPath.resolve(RAW_DIR), true);
		
		httpBodyStage.setNextStage(writerStage);
		
		return httpBodyStage;
	}
	
	private void configureJobsStageParams() {
		
	}
}
