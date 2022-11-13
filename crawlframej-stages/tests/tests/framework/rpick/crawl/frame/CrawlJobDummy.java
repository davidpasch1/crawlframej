package tests.framework.rpick.crawl.frame;

import java.net.URI;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import rpick.crawl.frame.crawler.AbstractCrawlJob;
import rpick.crawl.frame.crawler.BrokenPipelineException;
import rpick.crawl.frame.crawler.CrawlJob;

public class CrawlJobDummy extends AbstractCrawlJob {

	private int httpCode = 200;
	
	public CrawlJobDummy(URI url) {
		super(url);
	}
	
	public static List<CrawlJob> createJobs(List<URI> urls) {
		return urls.stream()
				.map(url -> new CrawlJobDummy(url))
				.collect(Collectors.toList());
	}
	
	public int getHttpCode() {
		return httpCode;
	}

	public void setHttpCode(int httpCode) {
		this.httpCode = httpCode;
	}

	public static void setStageParams(String stage, Function<CrawlJob,Object> params) throws BrokenPipelineException {
		AbstractCrawlJob.setStageParams(CrawlJobDummy.class, stage, params);
	}
}
