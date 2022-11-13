/**
 * The core framework for a web-crawler (top-layer, see Specification).
 * 
 * This package contains the core classes that comprise the framework.
 * 
 * 
 * <h3>Design</h3>
 * 
 * <ul>
 *   <li>The processing stages of the pipeline are organized as a tree.</li>
 *   <li>The crawl job travels through the pipeline as a token. 
 *       As such it conveys parameters and results to the stages.</li>
 * </ul>
 * 
 * 
 * <h3>Core Classes</h3>
 * 
 * <p>The core interfaces and classes are:</p>
 * 
 * <ul>
 *   <li>{@link rpick.crawl.frame.crawler.CrawlerBase},</li>
 *   <li>{@link rpick.crawl.frame.crawler.CrawlJob},</li>
 *   <li>{@link rpick.crawl.frame.crawler.CrawlerPipeline},</li>
 *   <li>{@link rpick.crawl.frame.crawler.CrawlStrategy},</li>
 *   <li>{@link rpick.crawl.frame.crawler.pipeline.FetchingStageBase},</li>
 *   <li>{@link rpick.crawl.frame.crawler.pipeline.ProcessingStageBase}.</li>
 * </ul>
 *
 * 
 * <p>A complete implementation requires custom:</p> 
 * <ul>
 *   <li>{@link rpick.crawl.frame.crawler.CrawlerBase},</li>
 *   <li>{@link rpick.crawl.frame.crawler.CrawlJob} types for specific processing of different kinds of web-pages,</li>
 *   <li>{@link rpick.crawl.frame.crawler.CrawlStrategy} to specify the direction and extend of the crawl,</li>
 *   <li>{@link rpick.crawl.frame.crawler.pipeline.ProcessingStageBase} for assembling the pipeline.</li>
 * </ul>
 * 
 * 
 * @author David Pasch
 */
package rpick.crawl.frame.crawler;
