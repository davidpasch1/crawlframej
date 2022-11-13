package tests.rpick.crawl.frame.stages;

import static org.junit.Assert.*;
import static tests.framework.rpick.crawl.frame.Utils.*;

import java.net.URI;
import java.util.Collections;

import org.junit.BeforeClass;
import org.junit.Test;

import rpick.crawl.frame.crawler.AbortCrawlException;
import rpick.crawl.frame.crawler.CrawlJob;
import rpick.crawl.frame.crawler.pipeline.AbortJobException;
import rpick.crawl.frame.stages.FetchingStage;
import rpick.crawl.frame.stages.WebFetchingStage;
import tests.framework.rpick.crawl.frame.CrawlJobDummy;
import tests.framework.rpick.crawl.frame.FetchingStageDummy;

/**
 * Tests fetching stage. 
 *  
 * <pre>
 * under test: fetching stage
 * scope: one job processed by fetching stage
 * tests: implementation
 * use case tests: behavior of fetching stage wrt use case
 * </pre>
 */
public class TestAbstractFetchingStage {

	/* Rationale:
	 * - broken pipeline: not interesting test case
	 */
	
	@BeforeClass
	public static void onlyOnce() {
		initFetchingStageParams("fetcher");
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Test Methods
	//
	
	/**
	 * Tests setting of result in fetching stage.
	 */
	@Test
	public void test_takeFetchedCrawlJob_Normal_setResult() {
		
		String id = "fetcher";
		FetchingStage fetchingStage = createFetchingStage(id);

		CrawlJob fetchedJob;
		try {

			fetchingStage.startUp();
			
			CrawlJobDummy job = new CrawlJobDummy(URI.create("http://toto.com/index.html"));
			fetchingStage.enqueue(Collections.singletonList(job));
			
			fetchedJob = fetchingStage.takeFetchedCrawlJob();
			
		} catch (Exception e) {
			fail("expected success: " + e.getMessage());
			return;
		}
		
		assertJobFetchResult(200, fetchedJob, id);
	}
	
	/**
	 * Error in {@link FetchingStage} of type {@link AbortJobException}.
	 */
	@Test
	public void test_takeFetchedCrawlJob_Error_JobException() {
		
		String id = "fetcher";
		FetchingStage fetchingStage = createFetchingStage(id);
		
		CrawlJobDummy job_0 = new CrawlJobDummy(URI.create("http://toto.com/index.html"));
		job_0.setHttpCode(404);

		try {

			fetchingStage.startUp();

			fetchingStage.enqueue(Collections.singletonList(job_0));
			
			fetchingStage.takeFetchedCrawlJob();
			
		} catch (AbortJobException e) {
			
			assertJobFetchResult(404, job_0, id);
			return;

		} catch (Exception e) {
			// fall-through
		}
		
		fail("expected JobException");
	}
	
	/**
	 * Error in {@link FetchingStage} of type {@link AbortCrawlException}.
	 */
	@Test
	public void test_takeFetchedCrawlJob_Error_CrawlException() {
		
		String id = "fetcher";
		FetchingStage fetchingStage = createFetchingStage(id);
		
		CrawlJobDummy job_0 = new CrawlJobDummy(URI.create("http://toto.com/index.html"));
		job_0.setHttpCode(500);
		
		try {
			
			fetchingStage.startUp();
			
			fetchingStage.enqueue(Collections.singletonList(job_0));
			
			fetchingStage.takeFetchedCrawlJob();
			
		} catch (AbortCrawlException e) {
			
			assertJobFetchResult(500, job_0, id);
			return;

		} catch (Exception e) {
			// fall-through
		}
		
		fail("expected CrawlException");
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Test Methods: Use Case
	//
	
	@Test
	public void test_takeFetchedCrawlJob_Error_404() {
		
		String id = "fetcher";
		FetchingStage fetchingStage = new FetchingStageDummy(
				id, 
				new WebFetchingStage.DefaultErrorHandler());
		
		CrawlJobDummy job_0 = new CrawlJobDummy(URI.create("http://toto.com/index.html"));
		job_0.setHttpCode(404);

		try {
			
			fetchingStage.startUp();
			
			fetchingStage.enqueue(Collections.singletonList(job_0));
			
			fetchingStage.takeFetchedCrawlJob();
			
		} catch (AbortJobException e) {
			
			assertJobFetchResult(404, job_0, id);
			return;

		} catch (Exception e) {
			// fall-through
		}
		
		fail("expected JobException");	
	}
	
	@Test
	public void test_takeFetchedCrawlJob_Error_500() {
		
		String id = "fetcher";
		FetchingStage fetchingStage = new FetchingStageDummy(
				id,
				new WebFetchingStage.DefaultErrorHandler());
		
		CrawlJobDummy job_0 = new CrawlJobDummy(URI.create("http://toto.com/index.html"));
		job_0.setHttpCode(500);

		try {
			
			fetchingStage.startUp();
			
			fetchingStage.enqueue(Collections.singletonList(job_0));
		
			fetchingStage.takeFetchedCrawlJob();
			
		} catch (AbortCrawlException e) {
			
			assertJobFetchResult(500, job_0, id);
			return;

		} catch (Exception e) {
			// fall-through
		}
		
		fail("expected CrawlException");
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Private Methods
	//

}
