package tests.rpick.crawl.frame.stages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.Collections;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import toolkit.err.ConfigException;
import rpick.crawl.frame.crawler.AbortCrawlException;
import rpick.crawl.frame.crawler.CrawlJob;
import rpick.crawl.frame.crawler.UniformCrawlJob;
import rpick.crawl.frame.crawler.pipeline.AbortJobException;
import rpick.crawl.frame.fetcher.WebpageFetcher;
import rpick.crawl.frame.fetcher.WebpageFetcher.Settings;
import rpick.crawl.frame.stages.WebFetchingStage;
import rpick.http.HttpException;
import tests.framework.rpick.crawl.frame.Utils;

public class TestWebFetchingStage {

	private static final URI REDIRECT_URL = URI.create("https://medium.com/@twainrichardson");

	
    /////////////////////////////////////////////////////////////////////////////
    // Test Initialization
    //

    @BeforeClass
    public static void onlyOnce() {

    }

	
	/////////////////////////////////////////////////////////////////////////////
	// Test Methods
	//

    @Test
    public void test_invariant_Error_NotStarted() {

    	WebFetchingStage fetchingStage = new WebFetchingStage(new Settings());

    	try {

    		try {
    			
    			fetchingStage.hasPendingJobs();
    			
    		} catch (IllegalStateException e) {
    			// success
    			return;
    		}
    		
    		try {
    			
    			fetchingStage.enqueue(Collections.emptyList());
    			
    		} catch (IllegalStateException e) {
    			// success
    			return;
    		}
    		
    		try {
			
    			fetchingStage.takeFetchedCrawlJob();
			
			} catch (IllegalStateException e) {
				// success
				return;
			}

    	} catch (Exception e) {
    		// fail
    	}
    	
    	fail();
    }

	// TODO POSTPONED test other HTTP codes
	/**
	 * HTTP Response: Redirection.
	 */
    @Test
    @Ignore("Manual test")
    public void test_take_Normal_NotFollowRedirect() {
    	
    	test_cfg_FollowRedirects_False();
	}

    
	/////////////////////////////////////////////////////////////////////////////
	// Test Methods: Input
	//
    
    @Test
    public void test_cfg_UserAgent_NotSet() {

    	try {
    		
			WebFetchingStage fetchingStage = new WebFetchingStage(new Settings());
			
			fetchingStage.startUp();
			
    	} catch (ConfigException e) {
    		
    		// success
    		return;
    	}
    	
    	fail();
    }
    
    @Test
    @Ignore("Manual test")
    public void test_cfg_FollowRedirects_False() {

    	UniformCrawlJob job = new UniformCrawlJob(REDIRECT_URL);

    	WebpageFetcher.Settings settings = new Settings();
		settings.setUserAgent("agent");
		settings.setFollowRedirects(false);
		WebFetchingStage fetchingStage = new WebFetchingStage(settings);
		
		try {

			fetchingStage.startUp();
		
			fetchingStage.enqueue(Collections.singletonList(job));
		
			fetchingStage.takeFetchedCrawlJob();

		} catch (AbortCrawlException e) {
		
			fail(e.getCause().getMessage());

		} catch (AbortJobException e) {

			int code = ((HttpException)e.getCause()).getHttpResponse().getCode();
			assertEquals(301, code);
			return;

		} catch (Exception e) {
			// fall-through
		}
		
		fail("expected AbortJobException.");
	}

	@Test
	@Ignore("Manual test")
	public void test_cfg_FollowRedirects_True() {
		
		UniformCrawlJob job = new UniformCrawlJob(REDIRECT_URL);

    	WebpageFetcher.Settings settings = new Settings();
		settings.setUserAgent("agent");
		settings.setFollowRedirects(true);
		WebFetchingStage fetchingStage = new WebFetchingStage(settings);
		fetchingStage.setIdentifier("fetcher");
		
		try {
			
			fetchingStage.startUp();
			
			fetchingStage.enqueue(Collections.singletonList(job));
		
			CrawlJob fetchedJob = fetchingStage.takeFetchedCrawlJob();

			Utils.assertJobFetchResult(200, fetchedJob, "fetcher");
			
		} catch (AbortCrawlException e) {
		
			fail(e.getCause().getMessage());

		} catch (AbortJobException e) {

			fail(e.getCause().getMessage());

		} catch (Exception e) {
			// fall-through
		}
	}
}
