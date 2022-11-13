package rpick.crawl.frame.crawler;

import rpick.crawl.frame.crawler.pipeline.AbortJobException;
import toolkit.err.ErrorTools;

/**
 * Signals that the entire crawl failed.
 * 
 * <b>Note:</b> may contain suppressed exceptions.
 */
@SuppressWarnings("serial")
public class AbortCrawlException extends Exception {

	/**
	 * <b>Invariant</b>: The cause is an {@code Exception}.
	 * 
	 * @param cause
	 */
	public AbortCrawlException(Exception cause) {
		super(cause);
	}
	
	public AbortCrawlException(AbortCrawlException cause) {
		super(cause.getCause());
		ErrorTools.addSuppressed(this, cause);
	}
	
	public void addSuppressed(AbortCrawlException e) {
		addSuppressed(e.getCause());
		ErrorTools.addSuppressed(this, e);
	}
	
	// note: could be a generic, but currently overkill 
	public void addSuppressed(AbortJobException e) {
		addSuppressed(e.getCause());
		ErrorTools.addSuppressed(this, e);		
	}
}