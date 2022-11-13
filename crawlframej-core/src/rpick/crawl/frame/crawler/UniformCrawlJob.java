package rpick.crawl.frame.crawler;

import java.net.URI;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An implementation for the common case that the content (eg webpages) is handled in an indiscriminate way.
 */
public class UniformCrawlJob extends AbstractCrawlJob {

	/**
	 * @param url content to be fetched
	 */
	public UniformCrawlJob(URI url) {
		super(url);
	}

	public static List<CrawlJob> createJobs(List<URI> urls) {
		return urls.stream()
				.map(url -> new UniformCrawlJob(url))
				.collect(Collectors.toList());
	}

	/**
	 * Sets stage parameters.
	 * 
	 * <p>The parameter is computed by a mapping <code>(stage, job) -> params</code>.
	 * 
	 * @param stage stage identifier
	 * @param params stage parameters
	 */
	public static void setStageParams(String stage, Function<CrawlJob,Object> params) throws BrokenPipelineException {
		AbstractCrawlJob.setStageParams(UniformCrawlJob.class, stage, params);
	}
}
