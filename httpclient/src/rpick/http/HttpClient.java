package rpick.http;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.protocol.RedirectLocations;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Timeout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO POSTPONED: release of httpclient's resources?
/**
 * Encapsulates the implementation of the HTTP client.
 * 
 * Note: logging of request with log4j2.xml.
 *       alternative: HttpRequestInterceptor (non-fluent).
 *       alternative: Java 11 HTTP client.
 */
public class HttpClient {
	
	private static final String SERVER_FILTERED_MESSAGE = "server filtered";

	/**
	 * User Agent used by Firefox.
	 */
	public static final String USER_AGENT_FIREFOX = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:84.0) Gecko/20100101 Firefox/84.0";
	
	private static final Logger log = LogManager.getLogger(HttpClient.class);

	private static final Timeout CONNECTION_TIMEOUT_MILLIS = Timeout.ofMilliseconds(10000);
	private static final Timeout CONNECTION_REQUEST_TIMEOUT_MILLIS = Timeout.ofMilliseconds(10000);
	private static final Timeout RESPONSE_TIMEOUT_MILLIS = Timeout.ofMilliseconds(10000);

	private String userAgent;
	private boolean redirect;
	private final org.apache.hc.client5.http.classic.HttpClient httpClient;

	private List<String> filteredServers = Collections.emptyList();
	private URI originalUrl;

	
	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//	
	
	/**
	 * Client which automatically follows redirects.
	 * 
	 * @param userAgent HTTP Request header User-Agent
	 */
	public HttpClient(String userAgent) {
		this(userAgent, true);
	}		

	/**
	 * @param userAgent HTTP Request header User-Agent
	 * @param redirect whether redirects are to be followed
	 */
	public HttpClient(String userAgent, boolean redirect) {

		this.userAgent = userAgent;
		this.redirect = redirect;
		
		RequestConfig cfg = RequestConfig.custom()
				.setConnectTimeout(CONNECTION_TIMEOUT_MILLIS)
				.setResponseTimeout(RESPONSE_TIMEOUT_MILLIS)
				.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT_MILLIS)
				.build();

		HttpRequestInterceptor requestInterceptor = new HttpRequestInterceptor() {
	         @Override
	        public void process(HttpRequest request, EntityDetails entity, HttpContext ctxt)
        		throws HttpException, IOException {
	        	
				 HttpClientContext context = (HttpClientContext) ctxt;
				
				 URI url = getCurrentUrl(context);
				 log.trace("Current URL: " + url);
				
				 String host = url.getHost();
				 if (filteredServers.contains(host)) {
					 log.debug("Server filtered: " + host);
					 throw new RuntimeException(SERVER_FILTERED_MESSAGE);
				 }
	        }
        };
		
		HttpClientBuilder clientBuilder = HttpClientBuilder.create()
				.setUserAgent(userAgent)
				.setDefaultRequestConfig(cfg)
				.addRequestInterceptorLast(requestInterceptor);
		
		httpClient = redirect ? 
				clientBuilder.build() 
				: clientBuilder.disableRedirectHandling().build();
	}

	/**
	 * Performs an HTTP GET method.
	 * 
	 * @param url URL to be retrieved
	 * @return HTTP response
	 */
	public HttpResponse get(URI url) throws ServerIOException {
		
		try {
		
			return get(url, Collections.emptyList());

		} catch (ServerFilteredException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Performs an HTTP GET method.
	 * Aborts request if server is filtered.
	 * 
	 * @param url URL to be retrieved
	 * @param filteredServers filter list of servers
	 * @return HTTP response
	 */
	public HttpResponse get(URI url, List<String> filteredServers) 
			throws 
				ServerIOException, 
				ServerFilteredException {
		
		this.filteredServers = filteredServers;
		originalUrl = url;
		
		log.debug("Get: " + url);
		
		HttpClientContext context = HttpClientContext.create();
		try {
		
			HttpClientResponse response = httpClient.execute(
				new HttpGet(url),
				context,
				new HttpClientResponseHandler<HttpClientResponse>() {
					// Note: caller takes care of release of ClassicHttpResponse
			    	@Override
			    	public HttpClientResponse handleResponse(ClassicHttpResponse response) throws ParseException, IOException {			    					    		
			    		return new HttpClientResponse(url, response);
			    	}
				});
	
			if (redirect) {
			
				response.setFinalUrl(getCurrentUrl(context));
				log.info("Final URL: " + response.getFinalUrl());
			}
			
			return response;

		} catch (RuntimeException e) {

			if (SERVER_FILTERED_MESSAGE.equals(e.getMessage()))
				throw new ServerFilteredException(getCurrentUrl(context).getHost());
			else
				throw e;
			
		} catch (IOException e) {

			throw new ServerIOException(getCurrentUrl(context).getHost(), e);
		}
	}

	
	/////////////////////////////////////////////////////////////////////////////
	// Private Methods
	//	

	/**
	 * NOT USED, for reference only.
	 */
	@SuppressWarnings("unused")
	private HttpResponse fluent_get(URI url) throws Exception {
		
	    HttpResponse httpResponse = 
			Request.get(url)
				.userAgent(userAgent)
			    .connectTimeout(CONNECTION_TIMEOUT_MILLIS)
			    .responseTimeout(RESPONSE_TIMEOUT_MILLIS)
		    	.execute()
			    .handleResponse(new HttpClientResponseHandler<HttpResponse>() {
			    	@Override
			    	public HttpResponse handleResponse(ClassicHttpResponse response) throws ParseException, IOException {			    					    		
			    		return new HttpClientResponse(url, response);
			    	}
			    });

		return httpResponse;
	}
		
	private URI getCurrentUrl(HttpClientContext context) {
		 
		RedirectLocations locs = context.getRedirectLocations();
		return locs != null && locs.size() > 0 ?
			locs.get(locs.size() - 1)
			: originalUrl;
	}
}
