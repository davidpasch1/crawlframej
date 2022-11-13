package rpick.http;

import java.net.URI;

/**
 * HTTP Response.
 */
public interface HttpResponse {
	
	/**
	 * Final URL after all redirections.
	 * 
	 * @return final URL after all redirections
	 */
	public URI getFinalUrl();
	
	/**
	 * HTTP status code.
	 * 
	 * @return HTTP status code (non-negative)
	 */
	public int getCode();
	
	/**
	 * HTTP reason phrase.
	 * 
	 * @return HTTP reason phrase
	 */
	public String getReasonPhrase();
	
	/**
	 * HTTP Content-Type.
	 * 
	 * @return HTTP Content-Type; may be null
	 */ 
	public String getContentType();
	
	/**
	 * Body.
	 * 
	 * @return body; may be null
	 */
	public byte[] getBody();
	
	/**
	 * HTTP code with reason phrase.
	 * 
	 * @return HTTP code with reason phrase
	 */
	public String getStatusLine();
	
	/**
	 * Response without content.
	 * 
	 * @return response without content
	 */
	public String toHeadString();
}
