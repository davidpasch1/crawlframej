//////////////////////////////////////////////
// SYNC: this is a copy.
// SYNC: update from project documentation.

/**
 * <p>A simple framework for a web-crawler.</p>
 * 
 * 
 * <h2>Paradigm</h2>
 * 
 * <p>Essentialism.</p>
 * <p>Simplicity over features.</p>
 * <p>Non-greedy approach to web-crawling.</p>
 * 
 * 
 * <h2>Scope</h2>
 * 
 * <p>A framework that may serve as a foundation for a basic scraping tool or a focused web-crawler. 
 * As such it aims to be simple and comprehensive, but still powerful enough to support different 
 * application scenarios. It is <em>not</em> intended for a full-web search engine.</p>
 * 
 * <p><span class="def">Note:</span> Fetched data is handed out by the framework <em>as-is</em>.
 * Consequently, the implementor of the framework must take care of any necessary cleaning of the data 
 * (eg invalid characters in HTML).</p>
 *  
 * 
 * <h2>Limitations</h2>
 * 
 * <ul>
 *   <li><em>A crawl is limited to one website only.</em>
 *       Of course, you can invoke the program multiple times to crawl several sites. 
 *       The reason for this limitation is that the framework is intended for a focused web-crawler, and not for a full-web crawler.</li>
 *   <li><em>Currently, only text content (webpages) is supported.</em>
 *       This may be changed in the future.</li>
 *   <li><em>The crawling framework has not been tested with non UTF-8 pages.</em></li>
 *   <li><em>The disk writer stage always outputs page as UTF-8 regardless of the original encoding.</em></li>
 *   <li><em>The framework has not been tested with non-ASCII URLs.</em>
 *       Note, that according to the standard for URLs (<a href="https://www.ietf.org/rfc/rfc1738.txt">RFC1738</a>) non-ASCII URLs are invalid. 
 *       However, the framework should work with URLs that were originally non-ASCII and have been encoded to ASCII according to the standard.</li>
 *   <li><em>The crawl delay is fixed.</em>
 *       That's because it is a simple solution and there is not much gain in making it adaptive.</li>
 * </ul>
 * 
 * 
 * <h2>Features</h2>
 * 
 * <ul>
 *   <li>Multithreaded design by running the fetching of webpages in a separate thread. As a consequence, the throughput is determined by the network communication.</li>
 *   <li>Flexible pipeline structure through assembly of pipeline with stages.</li>
 *   <li>Parameterization of stages wrt stage, job type, and job.</li>
 *   <li>Accumulation of stage results during the processing of a job.</li>
 *   <li>Custom error handling.</li>
 *   <li>On error abortion of individual job or of entire crawl.</li>
 *   <li>Complete error reporting through accumulation of exceptions throughout the pipeline.</li>
 *   <li>Ready-to-use implementations of various stages for basic needs.</li>
 *   <li>Sample code included for demo of framework.</li>
 * </ul>
 * 
 * 
 * <h2>Contents</h2>
 * 
 * <ul>
 *   <li>The core of the framework.</li>
 *   <li>Collection of stages for the fetcher and the processing pipeline.</li>
 *   <li>Demo of a general crawler and a downloader.</li>
 * </ul>
 * 
 * 
 * <h2>Requirements</h2>
 * 
 * <ul>
 *   <li>Java&nbsp;8</li>
 *   <li>JUnit&nbsp;5</li>
 * </ul>
 * 
 * @see rpick.crawl.frame.crawler
 * 
 * @author David Pasch
 */
package rpick.crawl.frame;