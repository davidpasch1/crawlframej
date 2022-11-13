package tests.rpick.crawl.frame.site;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.junit.Test;

import rpick.crawl.frame.site.Sitemap;
import rpick.crawl.frame.site.SitemapParser;
import rpick.crawl.frame.site.Sitemap.Location;

// TODO POSTPONED make medium independent
public class TestSitemapParser {
	
	private static final Path SITEMAP_NORMAL_NOLOC_FILEPATH = Paths.get("test_data/site/sitemap_normal_noloc.xml");
	private static final Path SITEMAP_NORMAL_ONELOC_FILEPATH = Paths.get("test_data/site/sitemap_normal_oneloc.xml");
	private static final Path SITEMAP_NORMAL_TWOLOC_FILEPATH = Paths.get("test_data/site/sitemap_normal_twoloc.xml");
	private static final Path SITEMAP_NORMAL_NO_OPTIONAL_FILEPATH = Paths.get("test_data/site/sitemap_normal_no-optional.xml");
	private static final Path SITEMAP_NORMAL_UNKNOWNTAG_FILEPATH = Paths.get("test_data/site/sitemap_normal_unknowntag.xml");
	private static final Path SITEMAP_ERROR_DATAFORMAT_FILEPATH = Paths.get("test_data/site/sitemap_error_dataformat.xml");
	private static final Path SITEMAP_REAL_FILEPATH = Paths.get("test_data/site/sitemap_medium_edited.xml");
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Test Methods
	//
	
	@Test
	public void test_parse_Normal_NoLoc() {
		
		SitemapParser parser = new SitemapParser();
		
		try {
		
			Sitemap sitemap = parser.parse(SITEMAP_NORMAL_NOLOC_FILEPATH);
			
			assertEquals(0, sitemap.getLocations().size());
		
		} catch (IOException e) {
			fail("expected success: " + e.getMessage());
		}
	}
	
	@Test
	public void test_parse_Normal_OneLoc() {
		
		SitemapParser parser = new SitemapParser();
		
		try {
		
			Sitemap sitemap = parser.parse(SITEMAP_NORMAL_ONELOC_FILEPATH);
			
			assertEquals(1, sitemap.getLocations().size());
			assertDenman(sitemap.getLocations().get(0));
		
		} catch (IOException e) {
			fail("expected success: " + e.getMessage());
		}
	}
	
	@Test
	public void test_parse_Normal_TwoLoc() {
		
		SitemapParser parser = new SitemapParser();
		
		try {
		
			Sitemap sitemap = parser.parse(SITEMAP_NORMAL_TWOLOC_FILEPATH);
			
			assertEquals(2, sitemap.getLocations().size());
			assertDenman(sitemap.getLocations().get(0));
			assertSuperman(sitemap.getLocations().get(1));
		
		} catch (IOException e) {
			fail("expected success: " + e.getMessage());
		}
	}
	
	@Test
	public void test_parse_Normal_NoOptional() {
		
		SitemapParser parser = new SitemapParser();
		
		try {
		
			Sitemap sitemap = parser.parse(SITEMAP_NORMAL_NO_OPTIONAL_FILEPATH);
			
			assertEquals(1, sitemap.getLocations().size());

			Sitemap.Location location = sitemap.getLocations().get(0);
			assertEquals("https://medium.com/denman-aca2238b7a1a", location.getUrl().toString());
			assertNull(location.getLastModifiedDate());
			assertNull(location.getChangeFrequency());
			assertNull(location.getPriority());
		
		} catch (IOException e) {
			fail("expected success: " + e.getMessage());
		}
	}
	
	@Test
	public void test_parse_Normal_UnknownTag() {
		
		SitemapParser parser = new SitemapParser();
		
		try {
		
			Sitemap sitemap = parser.parse(SITEMAP_NORMAL_UNKNOWNTAG_FILEPATH);
			
			assertEquals(1, sitemap.getLocations().size());
			assertDenman(sitemap.getLocations().get(0));
		
		} catch (IOException e) {
			fail("expected success: " + e.getMessage());
		}
	}
		
	/**
	 * Data of invalid format.
	 */
	@Test
	public void test_parse_Error_DataFormat() {
		
		SitemapParser parser = new SitemapParser();
		
		try {
		
			Sitemap sitemap = parser.parse(SITEMAP_ERROR_DATAFORMAT_FILEPATH);
			
			assertEquals(1, sitemap.getLocations().size());
			
			Sitemap.Location location = sitemap.getLocations().get(0);
			assertEquals("https://medium.com/denman-aca2238b7a1a", location.getUrl().toString());
			assertNull(location.getLastModifiedDate());
			assertNull(location.getPriority());
		
		} catch (IOException e) {
			fail("expected success: " + e.getMessage());
		}
	}
	
	@Test
	public void test_parse_Normal_Real() {
		
		SitemapParser parser = new SitemapParser();
		
		try {
		
			Sitemap sitemap = parser.parse(SITEMAP_REAL_FILEPATH);
			
			assertEquals(14, sitemap.getLocations().size());
		
		} catch (IOException e) {
			fail("expected success: " + e.getMessage());
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Private Methods
	//
	
	private void assertDenman(Location location) {
	
		assertDenman("https://medium.com/denman-aca2238b7a1a", location);
	}
	
	private void assertDenman(String expectedUrl, Location location) {

		assertEquals(expectedUrl, location.getUrl().toString());
		LocalDate lastModifiedDate = location.getLastModifiedDate();
		assertEquals("2021-02-25", lastModifiedDate != null ? lastModifiedDate.toString() : "");
		assertEquals("monthly", location.getChangeFrequency());
		assertEquals(0.7, location.getPriority(), 0.0);
	}

	private void assertSuperman(Location location) {
		
		assertEquals("https://medium.com/superman-bca2238b7a1a", location.getUrl().toString());
		LocalDate lastModifiedDate = location.getLastModifiedDate();
		assertEquals("2020-02-20", lastModifiedDate != null ? lastModifiedDate.toString() : "");
		assertEquals("weekly", location.getChangeFrequency());
		assertEquals(1.0, location.getPriority(), 0.0);
	}
}
