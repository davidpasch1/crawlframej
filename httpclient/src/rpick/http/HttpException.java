package rpick.http;

/**
 * Failed HTTP method.
 */
@SuppressWarnings("serial")
public class HttpException extends Exception {

	private HttpResponse httpResponse;

	
	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//
	
	/**
	 * @param response HTTP response that caused this exception 
	 */
	public HttpException(HttpResponse response) {
		super(response.getStatusLine());
		httpResponse = response;
	}

	public HttpResponse getHttpResponse() {
		return httpResponse;
	}
}
