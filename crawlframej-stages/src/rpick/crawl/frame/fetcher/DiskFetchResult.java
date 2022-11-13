package rpick.crawl.frame.fetcher;

/**
 * Result of a fetch from disk.
 * 
 * <p><b>Invariant:</b> Either the result is the read content or an error, but not both. 
 * 
 * <p><b>Paradigm:</b> Immutable.
 */
public class DiskFetchResult implements FetchResult {

	// Invariant: only one of these fields is != null
	private final byte[] content;
	private final Exception exception;
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//
	
	public DiskFetchResult(byte[] content) {
		this.content = content;
		exception = null;
	}
	
	public DiskFetchResult(Exception e) {
		content = null;
		exception = e;
	}

	/**
	 * Read content.
	 * 
	 * @return read content; may be null
	 */
	public byte[] getContent() {
		return content;
	}	

	/**
	 * Exception in case of error.
	 * 
	 * @return exception in case of error; may be null
	 */
	public Exception getException() {
		return exception;
	}
}
