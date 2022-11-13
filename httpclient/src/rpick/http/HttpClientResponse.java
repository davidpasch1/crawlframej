package rpick.http;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * HTTP Response from {@link HttpClient}.
 */
public class HttpClientResponse implements HttpResponse {

	/* Rationale:
	 * - Adaptor pattern to break direct dependency of upper layers on implementation
	 * ! Do not store the original HTTP response object (ClassicHttpResponse)!!!
	 *   to allow release of resources
	 */
	
	private static final Logger log = LogManager.getLogger(HttpClientResponse.class);
	
	private URI finalUrl;
	private final int code;
	private final String reasonPhrase;
	private final String contentType;
	private final byte[] body;
	private final String statusLine;
	private final List<String> headers;



	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//

	public HttpClientResponse(URI url, ClassicHttpResponse httpResponse) throws ParseException, IOException {
		
		finalUrl = url;
		// Note: Do not store the argument (httpResponse)!!!
		code = httpResponse.getCode();
		reasonPhrase = httpResponse.getReasonPhrase();
		
		if (httpResponse.getEntity() != null) {
			
			HttpEntity entity = httpResponse.getEntity();
			contentType = entity.getContentType();
			log.debug("Content-Type: " + contentType);  // text/html; charset=utf-8
			// HTTP Content-Encoding: in the case original data has been manipulated by server eg compressed
			// HTTP request is auto sent with eg Accept-Encoding: gzip,deflate
			// decompression automatic!!!
			log.debug("Content-Encoding: " + entity.getContentEncoding());  // null
//			body = EntityUtils.toString(entity);
			body = EntityUtils.toByteArray(entity);
		}
		else {
			contentType = null;
			body = null;
		}
		
		statusLine = httpResponse.toString();
		Header[] httpHeaders = httpResponse.getHeaders();
		headers = Arrays.asList(httpHeaders).stream().map(h -> h.toString()).collect(Collectors.toList());
	}

	public void setFinalUrl(URI url) {
		finalUrl = url;
	}
	
	public String toString() {
		
		StringBuilder builder = new StringBuilder(toHeadString());
		
		if (body != null)
			builder.append(body);
		
		return builder.toString();
	}

	
	/////////////////////////////////////////////////////////////////////////////
	// HttpResponse
	//

	@Override
	public URI getFinalUrl() {
		return finalUrl;
	}

	@Override
	public int getCode() {
		return code;
	}
	
	@Override
	public String getReasonPhrase() {
		return reasonPhrase;
	}
	
	@Override
	public String getStatusLine() {
		return statusLine;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public byte[] getBody() {
		return body;
	}
	
	@Override
	public String toHeadString() {

		StringBuilder builder = new StringBuilder();
		
		builder.append(statusLine).append('\n');
		
		for(String header : headers) {
			builder.append(header).append('\n');
		}
		
		return builder.toString();
	}
}
