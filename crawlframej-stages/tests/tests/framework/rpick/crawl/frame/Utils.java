package tests.framework.rpick.crawl.frame;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import rpick.http.*;
import tests.framework.rpick.crawl.frame.ProcessingStageDummy.ProcessingFunction;
import tests.framework.rpick.crawl.frame.ProcessingStageDummy.SomeException;
import rpick.crawl.frame.crawler.AbortCrawlException;
import rpick.crawl.frame.crawler.CrawlJob;
import rpick.crawl.frame.crawler.pipeline.AbortJobException;
import rpick.crawl.frame.fetcher.WebFetchResult;
import rpick.crawl.frame.stages.FetchingStage;
import rpick.crawl.frame.stages.ProcessingStage;
import rpick.crawl.frame.stages.ProcessingStage.ErrorHandler;

public class Utils {

	private static boolean isInitFetchingStageParams = false;
	

	/////////////////////////////////////////////////////////////////////////////
	// General Tests
	//
	
	public static void initFetchingStageParams(String id) {

		if (isInitFetchingStageParams)
			return;
		
		CrawlJobDummy.setStageParams(
			id, 
			job -> {
				int httpCode = ((CrawlJobDummy)job).getHttpCode();
				return httpCode != 200 ? 
					new WebFetchResult(new HttpResponseDummy(httpCode, "File Not Here >:->")) 
					: new WebFetchResult(new HttpResponseDummy()); 
			});
		
		isInitFetchingStageParams = true;
	}
	
	public static FetchingStage createFetchingStage(String id) {
		
		FetchingStage fetchingStage = new FetchingStageDummy(
				id,
				fetchRes -> {
					WebFetchResult webResult = (WebFetchResult) fetchRes;
					HttpResponse httpResponse = webResult.getHttpResponse();
					int code = httpResponse != null ? httpResponse.getCode() : -1;
					if (code == 404)
						throw new AbortJobException(new Exception("e_fetch"));
					else if (code == 500)
						throw new AbortCrawlException(new Exception("e_fetch"));
				});
		
		return fetchingStage;
	}
	
	public static ProcessingStage createOneStageWithError(String id, String exceptionMsg) {
		
		ProcessingStageDummy stage = new ProcessingStageDummy(id);
		stage.setProcessingFunction((job, input) -> {throw new SomeException(exceptionMsg);});
		
		return stage;
	}
	
	public static ProcessingStage createOneStageWithErrorOnJob(String id, URI url, String exceptionMsg, String result) {
		
		ProcessingStageDummy stage = new ProcessingStageDummy(id);
		stage.setProcessingFunction((job, input) -> {
			if (job.getUrl().equals(url))
				throw new SomeException(exceptionMsg);
			else {
				job.setResult(id, result);
				return result;
			}
		});
		
		return stage;
	}
	
	public static ProcessingStage createOneStageWithResult(String id, String result) {
		
		ProcessingStageDummy stage = new ProcessingStageDummy(id);
		stage.setProcessingFunction((job, input) -> {
			job.setResult(id, result);
			return result;
		});
		
		return stage;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Use Case Tests
	//
	
	/**
	 * Creates and configures pipeline for the use case when parsing fails.
	 * 
	 * @see createUseCaseProcessingStages
	 */
	public static ProcessingStage createUseCaseErrorProcessingStages() {

		Map<String, ProcessingFunction> fcts = new HashMap<>();
		fcts.put("parser", (job, input) -> {throw new SomeException("e_parser");});
		
		return createUseCaseProcessingStages(fcts, Collections.emptyMap());
	}
	
	/**
	 * Asserts job results for the use case when parsing fails.
	 * 
	 * @see createUseCaseErrorProcessingStages
	 */
	public static void assertUseCaseError(CrawlJob job) {
	
		assertEquals("filtered", job.getResult("http_filter"));
		assertJobResultException("e_parser", job, "parser");  // parser failed
		assertNull(job.getResult("jobsgen"));
		assertEquals("written", job.getResult("writer"));  // writer executed

	}
	
	/** 
	 * Pipeline for use case:
	 * <pre>
	 * http_filter --- parser --- jobsgen
	 *             \-- writer
	 * </pre>
	 */
	public static ProcessingStage createUseCaseProcessingStages(
			Map<String, ProcessingFunction> fcts, 
			Map<String, ErrorHandler> errHandlers) {
	
		
		ProcessingStageDummy http_filter = new ProcessingStageDummy("http_filter");
		ProcessingStageDummy parser = new ProcessingStageDummy("parser");
		ProcessingStageDummy writer = new ProcessingStageDummy("writer");
		ProcessingStageDummy jobsgen = new ProcessingStageDummy("jobsgen");
		
		http_filter.setNextStages(Arrays.asList(parser, writer));
		parser.setNextStage(jobsgen);
		
		ErrorHandler defaultErrHandler = e -> {throw new AbortJobException(e);};
		ErrorHandler crawlErrHandler = e -> {throw new AbortCrawlException(e);};
		http_filter.setErrorHandler(errHandlers.getOrDefault("http_filter", defaultErrHandler));
		parser.setErrorHandler(errHandlers.getOrDefault("parser", defaultErrHandler));
		writer.setErrorHandler(errHandlers.getOrDefault("writer", crawlErrHandler));
		jobsgen.setErrorHandler(errHandlers.getOrDefault("jobsgen", defaultErrHandler));
		
		http_filter.setProcessingFunction(fcts.getOrDefault("http_filter", (job, input) -> {
			job.setResult("http_filter", "filtered");
			return input;	
		}));
		parser.setProcessingFunction(fcts.getOrDefault("parser", (job, input) -> {
			job.setResult("parser", "parsed");
			return input;	
		}));
		writer.setProcessingFunction(fcts.getOrDefault("writer", (job, input) -> {
			job.setResult("writer", "written");
			return null;	
		}));
		jobsgen.setProcessingFunction(fcts.getOrDefault("jobsgen", (job, input) -> {
			job.setResult("jobsgen", "newjobs");
			return null;	
		}));
		
		return http_filter;
	}

	
	/////////////////////////////////////////////////////////////////////////////
	// Basic Utils
	//
	
	/**
	 * Asserts result on job for a stage. 
	 */
	public static void assertJobResult(String expected, CrawlJob job, String stage) {
		assertEquals(expected, job.getResult(stage));
	}
	
	/**
	 * Asserts that there is no result on job for a stage. 
	 */
	public static void assertJobNoResult(CrawlJob job, String stage) {
		assertNull(job.getResult(stage));
	}
	
	/**
	 * Asserts that exception has been recorded for a stage.
	 */
	public static void assertJobResultException(String expectedMsg, CrawlJob job, String stage) {

		assertTrue(job.getResult(stage) instanceof Exception);
		assertEquals(expectedMsg, ((Exception)job.getResult(stage)).getMessage());		
	}
	
	public static void assertJobFetchResult(int expectedCode, CrawlJob job, String stage) {
		
		HttpResponse httpResponse = ((WebFetchResult)job.getResult(stage)).getHttpResponse();
		int code = httpResponse != null ? httpResponse.getCode() : -1;
		assertEquals(expectedCode, code);
	}
}
