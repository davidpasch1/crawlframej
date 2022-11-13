package rpick.crawl.frame.samples.crawler;

import static toolkit.net.mime.MediaType.MEDIA_TYPE_HTML;

import java.net.URI;
import java.nio.file.Paths;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import rpick.crawl.frame.common.CrawlerConfig;
import rpick.crawl.frame.fetcher.WebpageFetcher;
import rpick.crawl.frame.fetcher.WebpageFetcher.Settings;
import toolkit.java_old.Java_8;
import toolkit.net.mime.MediaType;
import toolkit.net.mime.MimeFileExtMap;

/**
 * Simulates a general crawler.
 * 
 * <p><b>Setup</b>: before running the demo, you have to specify the following in the code:
 * <ul>
 * 	<li>URLs to fetch (in {@link CrawlerDemo}, {@link GeneralCrawlStrategy})</li>
 * 	<li>Output path for fetched webpages (in {@link CrawlerDemo}).</li>
 * </ul>
 * <br>
 * <b>Restriction</b>: URLs must belong to same website.
 */
public class CrawlerDemo {
	
	private static final Logger log = LogManager.getLogger(CrawlerDemo.class);

	private static final String USER_AGENT = "Sniffing Agent";
	
	/**
	 * Specify the output path here eg /home/myname/test_out/crawlerdemo/
	 */
	public static final String OUT_PATH = null;
	
	/**
	 * Specify the seed URL here eg http://localhost/index.html
	 */
	public static final String SEED_URL = null;

	
	public static void main(String[] args) {
	
		CrawlerConfig config = CrawlerConfig.get();
		MimeFileExtMap mimeFileExtMap = createSampleMimeFileExtMap();
		config.setMimeFileExtMap(mimeFileExtMap);
		
		WebpageFetcher.Settings settings = new Settings();
		settings.setUserAgent(USER_AGENT);
		
		log.info("Saving pages to: " + OUT_PATH);
		GeneralCrawler crawler = new GeneralCrawler(
				settings,
				Paths.get(OUT_PATH)
			);
		
		try {
		
			crawler.crawl(Collections.singletonList(URI.create(SEED_URL)));
		
		} catch (Exception e) {

			log.error("Crawl aborted.");
			e.printStackTrace();
		}
	}
	

	/////////////////////////////////////////////////////////////////////////////
	// Private Methods
	//

	@SuppressWarnings("deprecation")
	private static MimeFileExtMap createSampleMimeFileExtMap() {

		MimeFileExtMap map = new MimeFileExtMap();
		map.addMapping(MEDIA_TYPE_HTML, Java_8.listOf("html", "htm"));
		map.addMapping(new MediaType("image/jpeg"), Java_8.listOf("jpg", "jpeg"));

		return map;
	}
}
