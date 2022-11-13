package rpick.crawl.frame.samples.crawler;

import java.net.URI;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import rpick.crawl.frame.crawler.CrawlJob;
import rpick.crawl.frame.crawler.CrawlStrategy;
import rpick.crawl.frame.crawler.UniformCrawlJob;

/**
 * Mocked crawling strategy.
 */
public class GeneralCrawlStrategy implements CrawlStrategy {

	/**
	 * Specify the outlinks here.
	 */
	public static final String[] OUTBOUND_URLS = new String[] {

	};
	
	private final Queue<CrawlJob> jobs = new ArrayDeque<>();
	private CrawlJob firstJob;

	
	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//

	public void createJobs(List<URI> urls) {
		
		jobs.addAll(UniformCrawlJob.createJobs(urls));
		firstJob = jobs.peek();
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// CrawlStrategy
	//

	@Override
	public boolean hasJobs() {
		return !jobs.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return outlinks on first job
	 */
	@Override
	public List<CrawlJob> getNextJobs() {
		List<CrawlJob> x = new ArrayList<CrawlJob>(jobs);
		jobs.clear();
		return x;
	}


	@Override
	public void createJobs(CrawlJob job, Object content) {

		if (job == firstJob) {
			
			List<URI> urls = Arrays.asList(OUTBOUND_URLS).stream().map(url -> URI.create(url)).collect(Collectors.toList());
			jobs.addAll(UniformCrawlJob.createJobs(urls));
		}
	}
}
