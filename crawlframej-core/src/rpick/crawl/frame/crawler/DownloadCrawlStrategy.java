package rpick.crawl.frame.crawler;

import java.net.URI;
import java.util.List;

/**
 * Encapsulates set of URLs for downloading.
 */
public class DownloadCrawlStrategy extends AbstractDownloadCrawlStrategy {

	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//

	public void createJobs(List<URI> urls) {
		jobs = UniformCrawlJob.createJobs(urls);
	}
}
