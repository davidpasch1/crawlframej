package rpick.crawl.frame.fetcher;

import rpick.http.HttpResponse;

/**
 * Result of a webpage fetch.
 * 
 * <p><b>Invariant:</b> Either the result is a HTTP response or an error, but not both. 
 * 
 * <p><b>Paradigm:</b> Immutable.
 */
public class WebFetchResult implements FetchResult {

	// Invariant: only one of these fields is != null
	private final HttpResponse httpResponse;
	private final Exception exception;
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//
	
	public WebFetchResult(HttpResponse response) {
		httpResponse = response;
		exception = null;
	}
	
	public WebFetchResult(Exception e) {
		httpResponse = null;
		exception = e;
	}
	
	/**
	 * HTTP response.
	 * 
	 * @return HTTP response; may be null
	 */
	public HttpResponse getHttpResponse() {
		return httpResponse;
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
