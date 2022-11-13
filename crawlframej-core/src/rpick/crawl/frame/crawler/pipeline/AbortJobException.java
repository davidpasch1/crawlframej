package rpick.crawl.frame.crawler.pipeline;

import toolkit.err.ErrorTools;

/**
 * Signals that processing failed on this job only.
 * 
 * <b>Note:</b> may contain suppressed exceptions.
 */
@SuppressWarnings("serial")
public class AbortJobException extends Exception {

	// invariant: cause is an Exception
	public AbortJobException(Exception cause) {
		super(cause);
	}
	
	public AbortJobException(AbortJobException cause) {
		super(cause.getCause());
		ErrorTools.addSuppressed(this, cause);
	}
	
	public void addSuppressed(AbortJobException e) {
		addSuppressed(e.getCause());
		ErrorTools.addSuppressed(this, e);
	}
}