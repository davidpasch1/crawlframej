package rpick.crawl.frame.site;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import toolkit.net.Domain;

/**
 * Website.
 * 
 * <p><b>Limitations</b>: 
 * <ul>
 *   <li>Does not support hosting of sitemap on other hosts.</li>
 * </ul>
 */
public class Website {
	
	private static final Logger log = LogManager.getLogger(Website.class);
	
	private final SitemapParser sitemapParser = new SitemapParser();
	private final Domain domain;
	

	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//

	/**
	 * @param domain website's domain
	 */
	public Website(Domain domain) {
		this.domain = domain;
	}
	
	/**
	 * Domain.
	 * 
	 * @return domain
	 */
	public Domain getDomain() {
		return domain;
	}

	//	NOTE: URL filtering: by standard URL in sitemap file should be the entire string prefix up to the filename
	//	      however, in practice the paths of the URLs in the sitemap file can be anything
	//	NOTE: trailing '/' in order to disallow potential port
	/**
	 * Reads sitemap from file and optionally filters contained URLs.
	 * 
	 * @param file sitemap file
	 * @param sitemapLocation location of sitemap used for filtering URLs, must end with slash ('/'); may be null
	 * @throws IllegalArgumentException on invalid sitemap URL
	 */
	public Sitemap readSitemap(Path file, String sitemapLocation) throws IOException {
		
		Sitemap sitemap = sitemapParser.parse(file);
		log.info("Read sitemap from file: " + file);
		
		return sitemapLocation == null ? sitemap : filtered(sitemap, sitemapLocation);
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Private Methods
	//
	
	private Sitemap filtered(Sitemap sitemap, String sitemapLocation) {
		
		Sitemap filteredMap = new Sitemap();

		sitemap.getLocations().stream()
			.filter(loc -> loc.getUrl().toString().startsWith(sitemapLocation))
			.forEach(loc -> filteredMap.addLocation(loc));
	
		return filteredMap;
	}
}
