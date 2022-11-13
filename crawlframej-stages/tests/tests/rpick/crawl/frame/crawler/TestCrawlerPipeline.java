package tests.rpick.crawl.frame.crawler;

import static org.junit.Assert.fail;
import static tests.framework.rpick.crawl.frame.Utils.assertJobFetchResult;
import static tests.framework.rpick.crawl.frame.Utils.assertJobNoResult;
import static tests.framework.rpick.crawl.frame.Utils.assertJobResult;
import static tests.framework.rpick.crawl.frame.Utils.assertJobResultException;
import static tests.framework.rpick.crawl.frame.Utils.assertUseCaseError;
import static tests.framework.rpick.crawl.frame.Utils.createFetchingStage;
import static tests.framework.rpick.crawl.frame.Utils.createOneStageWithErrorOnJob;
import static tests.framework.rpick.crawl.frame.Utils.createOneStageWithResult;
import static tests.framework.rpick.crawl.frame.Utils.createUseCaseErrorProcessingStages;
import static tests.framework.rpick.crawl.frame.Utils.initFetchingStageParams;

import java.net.URI;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import rpick.crawl.frame.crawler.AbortCrawlException;
import rpick.crawl.frame.crawler.CrawlerPipeline;
import rpick.crawl.frame.crawler.pipeline.AbortJobException;
import rpick.crawl.frame.stages.FetchingStage;
import rpick.crawl.frame.stages.ProcessingStage;
import tests.framework.rpick.crawl.frame.CrawlJobDummy;
import tests.framework.rpick.crawl.frame.CrawlStrategyDummy;

/**
 * Tests pipeline. 
 *  
 * <pre>
 * under test: pipeline, abstract impl of fetching and processing stages
 * scope: code of pipeline and abstract stages only
 * input: jobs
 * tests: errors originating from stages
 * use case tests: behavior of pipeline wrt use case
 * </pre>
 */
public class TestCrawlerPipeline {

	/* Rationale:
	 * - test not in core: testing just the pipeline class is not interesting
	 * - broken pipeline: not interesting test case
	 * - tests build upon stage tests:
	 *   assum: proper treatment of error within processing stages
	 */
	
	private final CrawlStrategyDummy crawlStrategy = new CrawlStrategyDummy();
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Test Initialization
	//
	
	@BeforeClass
	public static void onlyOnce() {
		initFetchingStageParams("fetcher");
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Test Methods
	//
	
	/**
	 * Processing is successful.
	 */
	@Test
	public void test_process_Normal() {
		
		FetchingStage fetchingStage = createFetchingStage("fetcher");
		ProcessingStage s_00 = createOneStageWithResult("s_00", "result");

		CrawlerPipeline pipeline = new CrawlerPipeline(
				fetchingStage, 
				s_00,
				crawlStrategy);
		
		pipeline.startUp();
		
		CrawlJobDummy job_0 = new CrawlJobDummy(URI.create("http://toto.com/index.html"));
		CrawlJobDummy job_1 = new CrawlJobDummy(URI.create("http://toto.com/index2.html"));
		crawlStrategy.setJobs(Arrays.asList(job_0, job_1));
		
		try {
		
			pipeline.processJobs();
			
			assertJobFetchResult(200, job_0, "fetcher");
			assertJobResult("result", job_0, "s_00");
			assertJobFetchResult(200, job_1, "fetcher");
			assertJobResult("result", job_1, "s_00");

		} catch (Exception e) {
			fail("expected success: " + e.getMessage());
		}
	}
	
	/**
	 * Error in {@link FetchingStage} of type {@link AbortJobException}.
	 */
	@Test
	public void test_process_Error_FetchingStage_JobException() {
		
		FetchingStage fetchingStage = createFetchingStage("fetcher");
		ProcessingStage s_00 = createOneStageWithResult("s_00", "result");

		CrawlerPipeline pipeline = new CrawlerPipeline(
				fetchingStage, 
				s_00,
				crawlStrategy);
		
		pipeline.startUp();
		
		CrawlJobDummy job_0 = new CrawlJobDummy(URI.create("http://toto.com/index.html"));
		job_0.setHttpCode(404);
		CrawlJobDummy job_1 = new CrawlJobDummy(URI.create("http://toto.com/index2.html"));
		crawlStrategy.setJobs(Arrays.asList(job_0, job_1));
		
		try {
		
			pipeline.processJobs();
			
			assertJobFetchResult(404, job_0, "fetcher");
			assertJobFetchResult(200, job_1, "fetcher");
			assertJobResult("result", job_1, "s_00");

		} catch (Exception e) {
			fail("expected success: " + e.getMessage());
		}
	}
	
	/**
	 * Error in {@link FetchingStage} of type {@link AbortCrawlException}.
	 */
	@Test
	public void test_process_Error_FetchingStage_CrawlException() {
		
		FetchingStage fetchingStage = createFetchingStage("fetcher");
		ProcessingStage s_00 = createOneStageWithResult("s_00", "result");

		CrawlerPipeline pipeline = new CrawlerPipeline(
				fetchingStage, 
				s_00,
				crawlStrategy);
		
		pipeline.startUp();
		
		CrawlJobDummy job_0 = new CrawlJobDummy(URI.create("http://toto.com/index.html"));
		job_0.setHttpCode(500);
		CrawlJobDummy job_1 = new CrawlJobDummy(URI.create("http://toto.com/index2.html"));
		crawlStrategy.setJobs(Arrays.asList(job_0, job_1));
		
		try {
		
			pipeline.processJobs();

		} catch (AbortCrawlException e) {

			assertJobFetchResult(500, job_0, "fetcher");
			assertJobNoResult(job_1, "fetcher");
			return;
		}
		
		fail("expected AbortCrawlException");
	}
	
	/**
	 * Error in a {@link ProcessingStage} of type {@link AbortJobException}.
	 */
	@Test
	public void test_process_Error_ProcessingStage_JobException() {
		
		FetchingStage fetchingStage = createFetchingStage("fetcher");

		ProcessingStage s_00 = createOneStageWithErrorOnJob("s_00", URI.create("http://toto.com/index.html"), "e_00", "result");

		CrawlerPipeline pipeline = new CrawlerPipeline(
				fetchingStage, 
				s_00,
				crawlStrategy);
		
		pipeline.startUp();
		
		CrawlJobDummy job_0 = new CrawlJobDummy(URI.create("http://toto.com/index.html"));
		CrawlJobDummy job_1 = new CrawlJobDummy(URI.create("http://toto.com/index2.html"));
		crawlStrategy.setJobs(Arrays.asList(job_0, job_1));
		
		try {
		
			pipeline.processJobs();
			
			assertJobResultException("e_00", job_0, "s_00");
			assertJobResult("result", job_1, "s_00");

		} catch (Exception e) {
			fail("expected success: " + e.getMessage());
		}
	}
	
	/**
	 * Error in a {@link ProcessingStage} of type {@link AbortCrawlException}.
	 */
	@Test
	public void test_process_Error_ProcessingStage_CrawlException() {
		
		FetchingStage fetchingStage = createFetchingStage("fetcher");

		ProcessingStage s_00 = createOneStageWithErrorOnJob("s_00", URI.create("http://toto.com/index.html"), "e_00", "result");
		s_00.setErrorHandler(e -> {throw new AbortCrawlException(e);});

		CrawlerPipeline pipeline = new CrawlerPipeline(
				fetchingStage, 
				s_00,
				crawlStrategy);
		
		pipeline.startUp();
		
		CrawlJobDummy job_0 = new CrawlJobDummy(URI.create("http://toto.com/index.html"));
		CrawlJobDummy job_1 = new CrawlJobDummy(URI.create("http://toto.com/index2.html"));
		crawlStrategy.setJobs(Arrays.asList(job_0, job_1));
		
		try {
		
			pipeline.processJobs();
			
		} catch (AbortCrawlException e) {
			
			assertJobResultException("e_00", job_0, "s_00");
			assertJobNoResult(job_1, "s_00");
			return;
		}

		fail("expected AbortCrawlException");
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Test Methods: Use Case
	//

	@Test
	public void test_process_UseCase_Error_parserfail() {
		
		FetchingStage fetchingStage = createFetchingStage("fetcher");
		ProcessingStage http_filter = createUseCaseErrorProcessingStages();
		
		CrawlerPipeline pipeline = new CrawlerPipeline(
				fetchingStage, 
				http_filter,
				crawlStrategy);
		
		pipeline.startUp();
		
		CrawlJobDummy job_0 = new CrawlJobDummy(URI.create("http://toto.com/index.html"));
		CrawlJobDummy job_1 = new CrawlJobDummy(URI.create("http://toto.com/index2.html"));
		crawlStrategy.setJobs(Arrays.asList(job_0, job_1));
		
		try {
		
			pipeline.processJobs();
			
			assertUseCaseError(job_0);
			assertUseCaseError(job_1);

		} catch (Throwable e) {
			fail("expected success");
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Private Methods
	//
	
}
