package tests.framework.rpick.crawl.frame;

import java.util.List;

import rpick.crawl.frame.crawler.AbstractDownloadCrawlStrategy;
import rpick.crawl.frame.crawler.CrawlJob;

public class CrawlStrategyDummy extends AbstractDownloadCrawlStrategy {

	
	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//

	public void setJobs(List<CrawlJob> jobs) {
		this.jobs = jobs;
	}
}
