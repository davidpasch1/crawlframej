package tests.rpick.crawl.frame.site;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import rpick.crawl.frame.site.Sitemap;
import rpick.crawl.frame.site.Website;
import toolkit.net.Domain;


public class TestWebsite {

	private static final String SITEMAP_LOCATION = "https://medium.com/";
	private static final Path SITEMAP_TEST_LOC_URL_FILEPATH = Paths.get("test_data/site/sitemap_test_loc_url.xml");
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Test Methods
	//
	
	@Test
	public void test_readSitemap_Normal_Filtering() {
		
		Website site = new Website(new Domain("medium.com"));
		
		try {
		
			Sitemap sitemap = site.readSitemap(SITEMAP_TEST_LOC_URL_FILEPATH, SITEMAP_LOCATION);
			
			assertEquals(1, sitemap.getLocations().size());
			assertEquals("https://medium.com/denman-aca2238b7a1a", sitemap.getLocations().get(0).getUrl().toString());
		
		} catch (IOException e) {
			fail("expected success: " + e.getMessage());
		}
	}
	
	@Test
	public void test_readSitemap_Normal_NoFiltering() {
		
		Website site = new Website(new Domain("medium.com"));
		
		try {
		
			Sitemap sitemap = site.readSitemap(SITEMAP_TEST_LOC_URL_FILEPATH, null);
			
			assertEquals(9, sitemap.getLocations().size());
		
		} catch (IOException e) {
			fail("expected success: " + e.getMessage());
		}
	}
}
