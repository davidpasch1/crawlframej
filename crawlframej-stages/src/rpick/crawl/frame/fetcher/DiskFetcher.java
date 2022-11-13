package rpick.crawl.frame.fetcher;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Reads content from disk.
 */
public class DiskFetcher {

	private static final Logger log = LogManager.getLogger(DiskFetcher.class);
	
	/**
	 * Reads file resource from disk.
	 * 
	 * <p><b>Note:</b> All exceptions are swallowed.
	 * 
	 * @param url URL of file resource
	 * @return result of fetch
	 */
	public DiskFetchResult read(URI url) {

		log.info("Reading: " + url);
		try {
			
			byte[] content = Files.readAllBytes(Paths.get(url));
			
			return new DiskFetchResult(content);
		
		} catch (Exception e) {
			
			return new DiskFetchResult(e);
		}
	}
}
