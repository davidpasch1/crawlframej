package rpick.crawl.frame.stages;

import org.javatuples.Pair;

import rpick.crawl.frame.crawler.AbortCrawlException;
import rpick.crawl.frame.crawler.BrokenPipelineException;
import rpick.crawl.frame.crawler.CrawlJob;
import rpick.crawl.frame.crawler.pipeline.AbortJobException;
import rpick.crawl.frame.fetcher.FetchResult;

public abstract class AbstractFetchingStage implements FetchingStage {

	private String identifier = this.getClass().getName();
	private ErrorHandler errorHandler;
	
	
	/////////////////////////////////////////////////////////////////////////////
	// FetchingStage
	//
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p><b>Default:</b> Class name.
	 */
	@Override
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	@Override
	public String getIdentifier() {
		return identifier;
	}
	
	@Override
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	@Override
	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}
	
	@Override
	public CrawlJob takeFetchedCrawlJob() 
			throws 
				IllegalStateException,
				AbortCrawlException, 
				AbortJobException, 
				BrokenPipelineException,
				InterruptedException {
		
		if (!isFetcherRunning())
			throw new IllegalStateException("Fetcher has not been started.");
		
		Pair<CrawlJob, FetchResult> jobAndResult = takeFetchedJobAndResult();
		CrawlJob fetchedJob = jobAndResult.getValue0(); 
		FetchResult result = jobAndResult.getValue1();
		
		fetchedJob.setResult(getIdentifier(), result);

		getErrorHandler().handle(result);
		
		return fetchedJob;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Protected Methods
	//
	
	/**
	 * Whether fetcher is running.
	 * 
	 * @return whether fetcher is running.
	 */
	protected abstract boolean isFetcherRunning();
	
	/**
	 * @param defaultErrorHandler default error handler
	 */
	protected AbstractFetchingStage(ErrorHandler defaultErrorHandler) {
		
		errorHandler = defaultErrorHandler;
	}
	
	/**
	 * @return fetched job together with result
	 */
	protected abstract Pair<CrawlJob, FetchResult> takeFetchedJobAndResult() throws InterruptedException;
}
