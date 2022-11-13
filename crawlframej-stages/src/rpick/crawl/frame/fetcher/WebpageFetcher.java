package rpick.crawl.frame.fetcher;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import toolkit.err.ConfigException;
import toolkit.function.RunnableEx;
import toolkit.time.Timing;
import rpick.http.HttpClient;
import rpick.http.HttpResponse;
import rpick.http.ServerFilteredException;
import rpick.http.ServerIOException;

/**
 * Politely fetches webpages.
 * 
 * @author David Pasch
 */
public class WebpageFetcher implements Runnable {

	private static final Logger log = LogManager.getLogger(WebpageFetcher.class);
	
	private static BlockingQueue<URI> fetchJobsQueue;
	private static BlockingQueue<FetchResult> fetchResultsQueue;
	
	private final Timing timing = new Timing();
	private final List<String> filteredServers = new ArrayList<>();
	
	private HttpClient client;
	private long crawlDelayMillis;
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//
	
	/**
	 * @param jobsQueue queue of URLs to be fetched
	 * @param resultsQueue queue of fetch results
	 * @param settings settings for fetching
	 */
	public WebpageFetcher(
			BlockingQueue<URI> jobsQueue, 
			BlockingQueue<FetchResult> resultsQueue,
			Settings settings) {
		
		fetchJobsQueue = jobsQueue;
		fetchResultsQueue = resultsQueue;
		
		String userAgent = settings.getUserAgent();
		if (StringUtils.isBlank(userAgent)) {
			
			log.error("userAgent not set.");
			throw new ConfigException("userAgent");
		}
		log.debug("userAgent: " + userAgent);

		boolean redirect = settings.isFollowRedirects();
		log.debug("followRedirects: " + redirect);
		
		client = new HttpClient(userAgent, redirect);
		
		crawlDelayMillis = settings.getCrawlDelay().toMillis();
		log.debug("crawlDelayMillis: " + crawlDelayMillis);
	}
	

	/////////////////////////////////////////////////////////////////////////////
	// Private Methods
	//
	
	/**
	 * <p><b>Note:</b> All exceptions are swallowed (except for {@link InterruptedException}).
	 * 
	 * <p>The following exceptions may be passed as a result:
	 * 
	 * <ul>
	 *   <li>{@link ServerIOException}</li>
	 *   <li>{@link ServerFilteredException}</li>
	 * </ul>
	 */
	private void processFetchJob() throws InterruptedException {

		log.debug("Waiting for next job...");
		URI url = fetchJobsQueue.take();
		
		log.info("Fetching: " + url);
		FetchResult result = timing.elapsed((Supplier<
				FetchResult	
			>)
			() -> {
				return fetchWebpage(url);
			});
		log.info("Fetching Elapsed: " + timing.getTime() + "ms");
		log.debug("Fetched and delivering: " + url);
		if (fetchResultsQueue.remainingCapacity() == 0)
			log.warn("Fetch result queue full.");
		fetchResultsQueue.put(result);
		
		log.debug("Delaying...");
		timing.elapsed((RunnableEx<
					InterruptedException
				>)
				() -> Thread.sleep(crawlDelayMillis)
			);
		log.trace("Delaying Elapsed: "+ timing.getTime() + "ms");
	}
	
	private FetchResult fetchWebpage(URI url) {
		
		try {
			
			HttpResponse response = client.get(url, filteredServers);
			
			return new WebFetchResult(response);
			
		} catch (ServerIOException e) {

			filteredServers.add(e.getHost());
			
			return new WebFetchResult(e);
		
		} catch (Exception e) {
			
			return new WebFetchResult(e);
		}
	}


	/////////////////////////////////////////////////////////////////////////////
	// Runnable
	//

	@Override
	public void run() {
		for (;;)
			try {
				
				processFetchJob();
				
			} catch (InterruptedException e) {
				
				log.info("Exit due to interruption.");
				return;
			}
	}

	
	/////////////////////////////////////////////////////////////////////////////
	// Public Inner Classes
	//

	/**
	 * Settings for fetching. 
	 */
	public static class Settings {
		
		private static final long CRAWL_DELAY = 5000;
		
		private String userAgent = null;
		private Duration crawlDelay = Duration.ofMillis(CRAWL_DELAY);
		private boolean isFollowRedirects = true;

		
		/////////////////////////////////////////////////////////////////////////////
		// Public Methods
		//

		/**
		 * HTTP User agent.
		 * 
		 * @return HTTP User agent
		 */
		public String getUserAgent() {
			return userAgent;
		}

		/**
		 * HTTP User agent.
		 * 
		 * @param userAgent HTTP User agent
		 */
		public void setUserAgent(String userAgent) {
			this.userAgent = userAgent;
		}

		/**
		 * Crawl delay.
		 * 
		 * <p><b>Note:</b> Hard-coded to Nutch default of 5s.
		 * 
		 * @return crawl delay
		 */
		public Duration getCrawlDelay() {
			return crawlDelay;
		}
		
		/**
		 * Whether to follow HTTP redirects.
		 * 
		 * <p><b>Default:</b> {@code true}.
		 * 
		 * @return whether to follow HTTP redirects
		 */
		public boolean isFollowRedirects() {
			return isFollowRedirects;
		}

		/**
		 * Whether to follow HTTP redirects.
		 * 
		 * @param followRedirects whether to follow HTTP redirects
		 */
		public void setFollowRedirects(boolean followRedirects) {
			this.isFollowRedirects = followRedirects;
		}
		
		public String toPrettyString() {
			
			return String.format(
					"User agent: %s\n"
					+ "Crawl delay: %dms\n"
					+ "Follow redirects: %s"
					, userAgent		
					, crawlDelay.toMillis()
					, isFollowRedirects
				);
		}
	}
}
