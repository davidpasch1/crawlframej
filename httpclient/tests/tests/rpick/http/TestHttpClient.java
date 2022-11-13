package tests.rpick.http;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import rpick.http.HttpClient;
import rpick.http.HttpResponse;
import rpick.http.ServerFilteredException;
import rpick.http.ServerIOException;


/**
 * Run test with localhost: 
 * - systemctl start apache2
 * - systemctl status apache2 
 */
public class TestHttpClient {

	private static final String USER_AGENT = "TestUserAgent";
	
	private static final URI URL_LOCAL = URI.create("http://localhost");
	private static final URI URL_404 = URI.create("http://localhost/gako");
	private static final URI URL_INVALID = URI.create("http://invalidinvalidinvalid.com/");
	private static final URI URL_MEDIUM = URI.create("https://david-pasch.medium.com/ration-aa77d519a46d");
	private static final URI URL_MEDIUM_REDIRECT = URI.create("https://medium.com/@twainrichardson");
	private static final URI URL_MEDIUM_REDIRECT_TAHAALI = URI.create("https://medium.com/@tahaali-k");

	
	/////////////////////////////////////////////////////////////////////////////
	// Test Initialization
	//
	
	@BeforeClass
	public static void onlyOnce() {
		
	}
	
	/////////////////////////////////////////////////////////////////////////////
	// Test Methods
	//	
	
	@Test
	@Ignore("Manual test")
	public void testGet_Normal_Local() {
		
		try {

			HttpResponse response = testGet(URL_LOCAL);
			
			assertHttpCode(200, response);
		}
		catch(Throwable e) {
			fail(e.getMessage());
			return;
		}
	}
	
	@Test
	@Ignore("Manual test")
	public void testGet_Error_404() {
		
		try {
			
			HttpResponse response = testGet(URL_404);
			
			assertHttpCode(404, response);
		}
		catch(Throwable e) {
			fail(e.getMessage());
			return;
		}
	}	

	@Test
	@Ignore("Manual test")
	public void testGet_Error_Exception() {
		
		try {
			testGet(URL_INVALID);
			fail("should be invalid URL.");
		}
		catch(IOException e) {
			e.printStackTrace();
			return;
		}
		catch(Throwable e) {
			fail(e.getMessage());
			return;
		}
	}	
	
	/* 
		https://medium.com/@tahaali-k
		https://tahaali-k.medium.com/
		https://tahaali-k.medium.com/are-you-passionate-about-electronics-perhaps-consider-a-career-as-a-robotics-engineer-e9da3288bff5?source=user_profile---------0-------------------------------
		https://medium.com/adaptiv-future/are-you-passionate-about-electronics-perhaps-consider-a-career-as-a-robotics-engineer-e9da3288bff5
	*/
	
	@Test
	@Ignore("Manual test")
	public void testGet_Normal_Medium() {
		
		try {
			testGet(URL_MEDIUM);
		}
		catch(Throwable e) {
			fail(e.getMessage());
			return;
		}
	}
	
	@Test
	@Ignore("Manual test")
	public void testGet_Normal_Medium_GetRedirectUrl() {
		
		try {
			testGet(URL_MEDIUM_REDIRECT_TAHAALI);
		}
		catch(Throwable e) {
			fail(e.getMessage());
			return;
		}
	}
	
	@Test
	@Ignore("Manual test")
	public void testGet_Normal_Medium_NotFollowRedirect() {
		
		try {
			
			HttpResponse response = testGet(URL_MEDIUM_REDIRECT, false);
			
			assertHttpCode(301, response);
		}
		catch(Throwable e) {
			fail(e.getMessage());
			return;
		}
	}
	
	@Test
	@Ignore("Manual test")
	public void testGet_Normal_Medium_FollowRedirect() {
		
		try {
			
			HttpResponse response = testGet(URL_MEDIUM_REDIRECT, true);
			
			assertHttpCode(200, response);
		}
		catch(Throwable e) {
			fail(e.getMessage());
			return;
		}
	}

	@Test
	@Ignore("Manual test")
	public void testGet_Normal_Medium_FollowRedirect_FilterServer() {
		
		try {
			
			testGet(
				URL_MEDIUM_REDIRECT, 
				true,
				Collections.singletonList("twainrichardson.com"));
		
			fail();
		}
		catch(ServerFilteredException e) {
			// success
		}
		catch(Throwable e) {
			fail(e.toString());
			return;
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Helpers
	//	

	private void assertHttpCode(int httpCode, HttpResponse response) {
		assertEquals(httpCode, response.getCode());
	}

	private HttpResponse testGet(URI url) throws IOException {
		return testGet(url, true);
	}
	
	private HttpResponse testGet(URI url, boolean redirect) throws IOException {
		
		try {
		
			return testGet(url, redirect, Collections.emptyList());

		} catch (ServerIOException e) {
			throw (IOException)e.getCause();
			
		} catch (ServerFilteredException e) {
			throw new RuntimeException(e);
		}
	}
	
	private HttpResponse testGet(URI url, boolean redirect, List<String> filteredServers) 
			throws 
				ServerIOException, 
				ServerFilteredException {
		
		HttpClient client = new HttpClient(USER_AGENT, redirect);
		
		HttpResponse response = client.get(url, filteredServers);
		System.out.println(response);
		
		return response;
	}
}