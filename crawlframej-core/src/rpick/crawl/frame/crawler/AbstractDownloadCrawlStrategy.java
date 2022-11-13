package rpick.crawl.frame.crawler;

import java.util.List;

/**
 * Base for crawling strategies which only download content without exploration of the web.
 * 
 * <p><b>Note:</b> Implementation base class.
 */
public class AbstractDownloadCrawlStrategy implements CrawlStrategy {

	protected List<CrawlJob> jobs = null;

	
	/////////////////////////////////////////////////////////////////////////////
	// CrawlStrategy
	//
	
	@Override
	public boolean hasJobs() {
		return jobs != null;
	}

	@Override
	public List<CrawlJob> getNextJobs() {
		List<CrawlJob> x = jobs;
		jobs = null;
		return x;
	}

	/**
	 * <p><b>Note:</b> Does nothing.
	 */
	@Override
	public void createJobs(CrawlJob job, Object content) {
		// nop
	}
}
