package tests.rpick.crawl.frame.crawler;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.function.Function;

import org.junit.Test;

import rpick.crawl.frame.crawler.AbstractCrawlJob;
import rpick.crawl.frame.crawler.BrokenPipelineException;
import rpick.crawl.frame.crawler.CrawlJob;
import rpick.crawl.frame.crawler.UniformCrawlJob;

/**
 * Tests crawl job. 
 *  
 * <pre>
 * under test: crawl job
 * scope: code of crawl job only
 * tests: passing of stage parameters
 * use case tests: different job types
 * </pre>
 */
public class TestAbstractCrawlJob {

	/**
	 * Tests setting and retrieval of stage parameters when there is only one type of job.
	 */
	@Test
	public void test_StageParams_Normal_OneJobType() {
		
		String s_0 = "s_0";
		UniformCrawlJob.setStageParams(s_0, job -> job.getUrl());
		
		UniformCrawlJob job = new UniformCrawlJob(URI.create("http://toto.com/index.html"));
		
		Object par = job.getStageParams(s_0);
		
		assertEquals(job.getUrl(), par);
	}

	/**
	 * Tests setting and retrieval of stage parameters when there are multiple types of jobs.
	 */
	@Test
	public void test_StageParams_Normal_TwoJobTypes() {
		
		String s_0 = "s_0";
		String par_one = "par_one";
		String par_two = "par_two";

		CrawlJobOne.setStageParams(s_0, job -> par_one);
		CrawlJobTwo.setStageParams(s_0, job -> par_two);
		
		CrawlJobOne job_0 = new CrawlJobOne(URI.create("http://toto.com/index.html"));
		CrawlJobTwo job_1 = new CrawlJobTwo(URI.create("http://toto.com/index2.html"));
		
		Object par_0 = job_0.getStageParams(s_0);
		Object par_1 = job_1.getStageParams(s_0);
		
		assertEquals(par_one, par_0);
		assertEquals(par_two, par_1);
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Private Inner Classes
	//
	
	private static class CrawlJobOne extends AbstractCrawlJob {

		public CrawlJobOne(URI url) {
			super(url);
		}
		
		public static void setStageParams(String stage, Function<CrawlJob,Object> params) throws BrokenPipelineException {
			AbstractCrawlJob.setStageParams(CrawlJobOne.class, stage, params);
		}
	}
	
	private static class CrawlJobTwo extends AbstractCrawlJob {

		public CrawlJobTwo(URI url) {
			super(url);
		}
		
		public static void setStageParams(String stage, Function<CrawlJob,Object> params) throws BrokenPipelineException {
			AbstractCrawlJob.setStageParams(CrawlJobTwo.class, stage, params);
		}
	}
}
