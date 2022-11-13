package tests.rpick.crawl.frame.stages;

import static org.junit.Assert.*;
import static tests.framework.rpick.crawl.frame.Utils.*;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import rpick.crawl.frame.crawler.AbortCrawlException;
import rpick.crawl.frame.crawler.CrawlJob;
import rpick.crawl.frame.crawler.pipeline.AbortJobException;
import rpick.crawl.frame.stages.ProcessingStage;
import rpick.crawl.frame.stages.ProcessingStage.ErrorHandler;
import tests.framework.rpick.crawl.frame.CrawlJobDummy;
import tests.framework.rpick.crawl.frame.ProcessingStageDummy;
import tests.framework.rpick.crawl.frame.ProcessingStageDummy.ProcessingFunction;
import tests.framework.rpick.crawl.frame.ProcessingStageDummy.SomeException;

/**
 * Tests stage of a pipeline. 
 *  
 * <pre>
 * under test: stage
 * scope: processing of one job only
 * input: job, stage input (assum: isValid)
 * basic tests: one stage only
 * complex tests: multi-stage pipeline
 * use case tests: verification of intended behavior in use cases
 * </pre>
 */
public class TestAbstractProcessingStage {

	/* Rationale:
	 * - broken pipeline: not interesting test case
	 * - complex tests build upon basic tests
	 */

	
	/////////////////////////////////////////////////////////////////////////////
	// Test Methods
	//
	
	/**
	 * Processing is successful.
	 */
	@Test
	public void test_process_OneStage_Normal() {

		ProcessingStage s_00 = createOneStageWithResult("s_00", "result");
		CrawlJob job = new CrawlJobDummy(URI.create("http://toto.com/index.html"));
		
		try {

			s_00.process(job, "result");

			assertJobResult("result", job, "s_00");
			
			return;

		} catch (AbortJobException e) {
			// fall-through
			
		} catch (AbortCrawlException e) {
			// fall-through
		}
		
		fail("unexpected exception");
	}
	
	/**
	 * Processing ends in {@link AbortJobException}.
	 */
	@Test
	public void test_process_OneStage_Error_JobException() {

		ProcessingStage s_00 = createOneStageWithError("s_00", "e_00");
		CrawlJob job = new CrawlJobDummy(URI.create("http://toto.com/index.html"));
		
		try {

			s_00.process(job, "result");

		} catch (AbortJobException e) {
			
			assertEquals("e_00", e.getCause().getMessage());
			assertEquals(0, e.getSuppressed().length);
			
			assertJobResultException("e_00", job, "s_00");
			
			return;
			
		} catch (AbortCrawlException e) {
			// fall-through
		}
		
		fail("expected JobException");
	}
	
	/**
	 * Processing ends in {@link AbortCrawlException}.
	 */
	@Test
	public void test_process_OneStage_Error_CrawlException() {

		ProcessingStage s_00 = createOneStageWithError("s_00", "e_00");
		s_00.setErrorHandler(e -> {throw new AbortCrawlException(e);});
		
		CrawlJob job = new CrawlJobDummy(URI.create("http://toto.com/index.html"));
		
		try {

			s_00.process(job, "result");

		} catch (AbortCrawlException e) {
			
			assertEquals("e_00", e.getCause().getMessage());
			assertEquals(0, e.getSuppressed().length);
			
			assertJobResultException("e_00", job, "s_00");
			
			return;
			
		} catch (AbortJobException e) {
			// fall-through
		}
		
		fail("expected CrawlException");
	}
	
	/**
	 * All stages are successful.
	 */
	@Test
	public void test_process_MultiStage_Normal() {
	
		Map<String, ProcessingFunction> fcts = new HashMap<>();
		fcts.put("s_00", (job, input) -> {
			job.setResult("s_00", input);
			return input;
		});
		fcts.put("s_10", (job, input) -> {
			job.setResult("s_10", input);
			return input;
		});
		fcts.put("s_11", (job, input) -> {
			job.setResult("s_11", input);
			return input;
		});
		fcts.put("s_12", (job, input) -> {
			job.setResult("s_12", input);
			return input;
		});
		
		ProcessingStage s_00 = createProcessingStages(fcts, Collections.emptyMap());
		
		CrawlJob job = new CrawlJobDummy(URI.create("http://toto.com/index.html"));
		String input = "inPut";
		
		try {

			s_00.process(job, input);

			assertJobResult(input, job, "s_00");
			assertJobResult(input, job, "s_10");
			assertJobResult(input, job, "s_11");
			assertJobResult(input, job, "s_12");
			
			return;

		} catch (AbortJobException e) {
			// fall-through
			
		} catch (AbortCrawlException e) {
			// fall-through
		}
		
		fail("expected success");
	}
	
	/**
	 * Tests the behavior wrt to the succeeding stages and the accumulation of their {@link AbortJobException}.
	 */
	@Test
	public void test_process_MultiStage_Error_JobException() {
	
		Map<String, ProcessingFunction> fcts = new HashMap<>();
		fcts.put("s_00", (job, input) -> input);
		fcts.put("s_10", (job, input) -> {throw new SomeException("e_10");});
		fcts.put("s_11", (job, input) -> {
			job.setResult("s_11", input);
			return input;
		});
		fcts.put("s_12", (job, input) -> {throw new SomeException("e_12");});
		
		Map<String, ErrorHandler> errHandlers = new HashMap<>();
		errHandlers.put("s_10", e -> {throw new AbortJobException(e);});
		errHandlers.put("s_12", e -> {throw new AbortJobException(e);});
		
		ProcessingStage s_00 = createProcessingStages(fcts, errHandlers);
		
		CrawlJob job = new CrawlJobDummy(URI.create("http://toto.com/index.html"));
		String input = "inPut";
		
		try {

			s_00.process(job, input);

		} catch (AbortJobException e) {
			
			assertEquals("e_10", e.getCause().getMessage());
			assertEquals(1, e.getSuppressed().length);
			assertEquals("e_12", e.getSuppressed()[0].getMessage());
			
			assertJobResultException("e_10", job, "s_10");
			assertJobResult(input, job, "s_11");
			assertJobResultException("e_12", job, "s_12");
			
			return;
			
		} catch (AbortCrawlException e) {
			// fall-through
		}
		
		fail("expected JobException");
	}

	/**
	 * Tests the behavior wrt to the succeeding stages and the accumulation of their {@link AbortJobException} and {@link AbortCrawlException}.
	 */
	@Test
	public void test_process_MultiStage_Error_JobException_CrawlException() {
	
		Map<String, ProcessingFunction> fcts = new HashMap<>();
		fcts.put("s_00", (job, input) -> input);
		fcts.put("s_10", (job, input) -> {throw new SomeException("e_10");});
		fcts.put("s_11", (job, input) -> {throw new SomeException("e_11");});
		fcts.put("s_12", (job, input) -> {
			job.setResult("s_12", input);
			return input;
		});
		
		Map<String, ErrorHandler> errHandlers = new HashMap<>();
		errHandlers.put("s_10", e -> {throw new AbortJobException(e);});
		errHandlers.put("s_11", e -> {throw new AbortCrawlException(e);});
		
		ProcessingStage s_00 = createProcessingStages(fcts, errHandlers);
		
		CrawlJob job = new CrawlJobDummy(URI.create("http://toto.com/index.html"));
		String input = "inPut";
		
		try {

			s_00.process(job, input);

		} catch (AbortCrawlException e) {
			
			assertEquals("e_11", e.getCause().getMessage());
			assertEquals(1, e.getSuppressed().length);
			assertEquals("e_10", e.getSuppressed()[0].getMessage());
			
			assertJobResultException("e_10", job, "s_10");
			assertJobResultException("e_11", job, "s_11");
			assertJobResult(input, job, "s_12");
			
			return;
			
		} catch (AbortJobException e) {
			// fall-through
		}
		
		fail("expected CrawlException");
	}
	
	/**
	 * Tests the behavior wrt to the succeeding stages and the accumulation of their {@link AbortCrawlException}.
	 */
	@Test
	public void test_process_MultiStage_Error_CrawlException() {
	
		Map<String, ProcessingFunction> fcts = new HashMap<>();
		fcts.put("s_00", (job, input) -> input);
		fcts.put("s_10", (job, input) -> {throw new SomeException("e_10");});
		fcts.put("s_11", (job, input) -> {throw new SomeException("e_11");});
		fcts.put("s_12", (job, input) -> {
			job.setResult("s_12", input);
			return input;
		});
		
		Map<String, ErrorHandler> errHandlers = new HashMap<>();
		errHandlers.put("s_10", e -> {throw new AbortCrawlException(e);});
		errHandlers.put("s_11", e -> {throw new AbortCrawlException(e);});
		
		ProcessingStage s_00 = createProcessingStages(fcts, errHandlers);
		
		CrawlJob job = new CrawlJobDummy(URI.create("http://toto.com/index.html"));
		String input = "inPut";
		
		try {

			s_00.process(job, input);

		} catch (AbortCrawlException e) {
			
			assertEquals("e_10", e.getCause().getMessage());
			assertEquals(1, e.getSuppressed().length);
			assertEquals("e_11", e.getSuppressed()[0].getMessage());
			
			assertJobResultException("e_10", job, "s_10");
			assertJobResultException("e_11", job, "s_11");
			assertJobResult(input, job, "s_12");
			
			return;
			
		} catch (AbortJobException e) {
			// fall-through
		}
		
		fail("expected CrawlException");
	}

	
	/////////////////////////////////////////////////////////////////////////////
	// Test Methods: Use Case
	//
	
	/**
	 * Tests invocation of writer stage in the case that the parser stage failed.
	 */
	@Test
	public void test_process_UseCase_Error_parserfail() {
	
		ProcessingStage http_filter = createUseCaseErrorProcessingStages();
		
		CrawlJob job = new CrawlJobDummy(URI.create("http://toto.com/index.html"));
		String input = "inPut";
		
		try {

			http_filter.process(job, input);

		} catch (AbortJobException e) {
			
			assertEquals("e_parser", e.getCause().getMessage());
			assertEquals(0, e.getSuppressed().length);
			
			assertUseCaseError(job);
			
			return;
			
		} catch (AbortCrawlException e) {
			// fall-through
		}
		
		fail("expected JobException");
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Private Methods
	//
	
	/** 
	 * Pipeline:
	 * <pre>
	 * s_00 --- s_10
	 *      |-- s_11
	 *      |-- s_12
	 * </pre>
	 */
	private static ProcessingStage createProcessingStages(
			Map<String, ProcessingFunction> fcts, 
			Map<String, ErrorHandler> errHandlers) {

		
		ProcessingStageDummy s_00 = new ProcessingStageDummy("s_00");
		ProcessingStageDummy s_10 = new ProcessingStageDummy("s_10");
		ProcessingStageDummy s_11 = new ProcessingStageDummy("s_11");
		ProcessingStageDummy s_12 = new ProcessingStageDummy("s_12");
		
		s_00.setNextStages(Arrays.asList(s_10, s_11, s_12));
		
		ErrorHandler defaultErrHandler = e -> {throw new AbortJobException(e);};
		s_00.setErrorHandler(errHandlers.getOrDefault("s_00", defaultErrHandler));
		s_10.setErrorHandler(errHandlers.getOrDefault("s_10", defaultErrHandler));
		s_11.setErrorHandler(errHandlers.getOrDefault("s_11", defaultErrHandler));
		s_12.setErrorHandler(errHandlers.getOrDefault("s_12", defaultErrHandler));
		
		s_00.setProcessingFunction(fcts.get("s_00"));
		s_10.setProcessingFunction(fcts.get("s_10"));
		s_11.setProcessingFunction(fcts.get("s_11"));
		s_12.setProcessingFunction(fcts.get("s_12"));
		
		return s_00;
	}

}
