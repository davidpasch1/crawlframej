package rpick.crawl.frame.samples.downloader;

import static toolkit.net.mime.MediaType.MEDIA_TYPE_HTML;

import java.net.URI;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import rpick.crawl.frame.common.CrawlerConfig;
import rpick.crawl.frame.fetcher.WebpageFetcher;
import rpick.crawl.frame.fetcher.WebpageFetcher.Settings;
import toolkit.java_old.Java_8;
import toolkit.net.mime.MediaType;
import toolkit.net.mime.MimeFileExtMap;

/**
 * Downloads web content.
 * 
 * <p><b>Setup</b>: before running the demo, you have to specify the following in the code:
 * <ul>
 * 	<li>User Agent</li>
 * 	<li>URLs to download</li>
 * 	<li>Output path for downloaded webpages</li>
 * </ul>
 * <br>
 * <b>Restriction</b>: Can download from one website only.
 */
public class DownloaderDemo {

	private static final Logger log = LogManager.getLogger(DownloaderDemo.class);
	
	/**
	 * Specify the User Agent here eg MyAgent
	 */
	private static final String USER_AGENT = "Sniffing Agent";

	/**
	 * Specify the output path here eg /home/myname/test_out/downloaderdemo/
	 */
	public static final String OUT_PATH = null;
	
	/**
	 * Specify the URLs to be downloaded here eg http://localhost/index.html
	 */
	public static final String[] URLS = new String[] {
	};

	
	public static void main(String[] args) {
		
		CrawlerConfig config = CrawlerConfig.get();
		MimeFileExtMap mimeFileExtMap = createSampleMimeFileExtMap();
		config.setMimeFileExtMap(mimeFileExtMap);

		WebpageFetcher.Settings settings = new Settings();
		settings.setUserAgent(USER_AGENT);

		log.info("Downloading to: " + OUT_PATH);
		Downloader downloader = new Downloader(
				settings,
				Paths.get(OUT_PATH)
			);
		
		try {
		
			List<URI> urls = Arrays.asList(URLS).stream().map(url -> URI.create(url)).collect(Collectors.toList());
			downloader.download(urls);
		
		} catch (Exception e) {

			log.error("Download aborted.");
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
