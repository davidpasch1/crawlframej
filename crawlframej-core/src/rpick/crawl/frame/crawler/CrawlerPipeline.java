package rpick.crawl.frame.crawler;

import static toolkit.err.ErrorTools.flatten;

import rpick.crawl.frame.crawler.pipeline.AbortJobException;
import rpick.crawl.frame.crawler.pipeline.FetchingStageBase;

import rpick.crawl.frame.crawler.pipeline.ProcessingStageBase;
import rpick.logging.Log4jManager;
import rpick.logging.Logger;
import toolkit.time.Timing;
import toolkit.function.RunnableEx3;

/**
 * Crawler's pipeline for processing crawl jobs.
 * 
 * <p>It consists of a fetching stage followed by processing stages.
 * 
 * <p>An example for a pipeline are the stages:
 * <ol>
 * 	 <li>fetch</li>
 * 	 <li>parse</li>
 * 	 <li>create jobs</li>
 * 	 <li>generate HTML</li>
 * 	 <li>write</li>
 * </ol>
 * 
 * @see CrawlJob
 * @see FetchingStageBase
 * @see ProcessingStageBase
 * 
 * @author David Pasch 
 */
public class CrawlerPipeline {

	/* Rationale:
	 * - crawlframe: top-layer is self-contained
	 * - knowledge: 
	 *   - pipeline: agnostic of stages
	 *   - crawlframe: agnostic of data, untyped
	 * - stages: middle-layer decouples top-layer from implementing subsystems eg fetcher, parser, writer
	 * - stages: generic except for parser
	 */
	
	// TODO OPTIONAL logging uses stage identifier
	
	private static final Logger log = Log4jManager.create(CrawlerPipeline.class);
		
	private final Timing timing = new Timing();
	
	private boolean isPipelineRunning = false;
	
	private final FetchingStageBase fetchingStage;
	private final ProcessingStageBase processingStagesRoot;
	private final CrawlStrategy crawlStrategy;
	
	private JobErrorHandler errorHandler = (e, log) -> false;
	

	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//

	/**
	 * @param fetchingStage fetching stage
	 * @param processingStagesRoot processing stages
	 * @param crawlStrategy crawling strategy
	 */
	public CrawlerPipeline(
			FetchingStageBase fetchingStage, 
			ProcessingStageBase processingStagesRoot,
			CrawlStrategy crawlStrategy) {

		this.fetchingStage = fetchingStage;
		this.processingStagesRoot = processingStagesRoot;
		this.crawlStrategy = crawlStrategy;
	}
	
	/**
	 * @param fetchingStage fetching stage
	 * @param processingStagesRoot processing stages
	 * @param errorHandler error handler for job exceptions
	 */
	public CrawlerPipeline(
			FetchingStageBase fetchingStage, 
			ProcessingStageBase processingStagesRoot,
			CrawlStrategy crawlStrategy,
			JobErrorHandler errorHandler) {
		
		this(fetchingStage, processingStagesRoot, crawlStrategy);
		this.errorHandler = errorHandler;
	}
	
	/**
	 * Starts pipeline.
	 * 
	 * <p><b>Note:</b> Can be safely called repeatedly.
	 */
	public void startUp() {
		
		if (isPipelineRunning)
			return;
		
		log.debug("Pipeline starting...");
		
		fetchingStage.startUp();
		
		isPipelineRunning = true;
	}
	
	/**
	 * Processes crawl jobs.
	 * 
	 * @throws IllegalStateException if pipeline has not been started
	 */
	public void processJobs() 
			throws 
				IllegalStateException, 
				AbortCrawlException, 
				BrokenPipelineException {
		
		if (!isPipelineRunning)
			throw new IllegalStateException("Pipeline has not been started.");
		
		try {
			
			while (crawlStrategy.hasJobs()) {
				
				fetchingStage.enqueue(crawlStrategy.getNextJobs());
				
				while (fetchingStage.hasPendingJobs()) {
					
					try {
		
						log.debug("Waiting for next job...");
						CrawlJob fetchedJob = fetchingStage.takeFetchedCrawlJob();
						Object result = fetchedJob.getResult(fetchingStage.getIdentifier());
					
						log.debug("Processing: " + fetchedJob.getUrl());
						long time = timing.elapsed((RunnableEx3<
								AbortJobException,
								AbortCrawlException,
								BrokenPipelineException
								>)
								() -> processingStagesRoot.process(fetchedJob, result));
						log.info("Processing Elapsed: " + time + "ms");
						log.debug("Processed: " + fetchedJob.getUrl());
					
					} catch (AbortJobException e) {
	
						log.info("Aborted job.");
						for (Throwable t : flatten(e))
							if (!errorHandler.handle(t, log)) {
								if (!(t instanceof Exception)
									|| t instanceof RuntimeException)
									log.error("Unexpected error during processing of job.", t);
								// an ordinary Exception
								else
									log.error(t.toString());
							}
		
						// continue with next job
					}
				}
			}
			
		} catch (InterruptedException e) {
			throw new AbortCrawlException(e);
		}
		
		log.info("All jobs processed.");
	}
	

	/////////////////////////////////////////////////////////////////////////////
	// Public inner classes
	//
	
	/**
	 * Specifies how to handle job exceptions that are swallowed during processing.
	 */
	@FunctionalInterface
	public interface JobErrorHandler {
		
		/* Rationale:
		 * - separation of concerns
		 */
		
		/**
		 * Handles a job exception that occurred in the pipeline.
		 * 
		 * @param t exception that occurred
		 * @param log logger
		 * @return whether exception has been handled
		 */
		public boolean handle(Throwable t, Logger log);
	}
}
