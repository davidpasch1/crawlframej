package rpick.crawl.frame.site;

import static rpick.crawl.frame.site.internal.SitemapParserUtils.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;
import org.jsoup.select.NodeVisitor;

/**
 * Basic parser for sitemap files.
 * 
 * <p><b>Limitations</b>: 
 * <ul>
 *   <li>Does not support full W3C Datetime format (https://www.w3.org/TR/NOTE-datetime).</li>
 * </ul>
 * 
 * <p>See {@link Sitemap} for further information.
 */
public class SitemapParser {

	/* Rationale:
	 * - JSoup for parsing:
	 *   - only xml parsing functionality needed
	 *   - can deal with broken xml
	 *   - visitor_pattern
	 */
	
	private static final Logger log = LogManager.getLogger(SitemapParser.class);
	
	// NOTE: Standard: <quote>Sitemap file must be UTF-8 encoded.</quote>.
	private static final String SITEMAPS_STANDARD_CHARSET = "utf-8";

	
	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//
	
	/**
	 * Parses sitemap from file.
	 * 
	 * @param file sitemap as XML UTF-8 file, potentially broken
	 * @return sitemap, most fields may be null (see {@link Sitemap})
	 * @throws NoSuchFileException
	 * @throws IOException
	 */
	public Sitemap parse(Path file) throws IOException {
		
		InputStream in = new BufferedInputStream(Files.newInputStream(file));
		
		Document doc = Jsoup.parse(in, SITEMAPS_STANDARD_CHARSET, "", Parser.xmlParser());

		SitemapVisitor visitor = new SitemapVisitor(new SitemapBuilder());
		doc.traverse(visitor);
		
		return visitor.getSitemap();
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Private Classes
	//
	
	private static class SitemapVisitor implements NodeVisitor {

		// NOTE: Tags defined by sitemaps standard.
		private static final Set<String> knownTags = new HashSet<>(Arrays.asList(new String[] {
				"#root",
				"urlset",
				"url",
				"loc",
				"lastmod",
				"changefreq",
				"priority"
			}));
		
		private SitemapBuilder builder;
		
		
		/////////////////////////////////////////////////////////////////////////////
		// Public Methods
		//
		
		
		public SitemapVisitor(SitemapBuilder builder) {
			this.builder = builder;
		}

		public Sitemap getSitemap() {
			return builder.getSitemap();
		}
		
		@Override
		public void head(Node node, int level) {

			if (node instanceof Element) {
				
				Element element = (Element) node;
				String tag = element.tagName().toLowerCase().trim();
				log.trace("head: " + tag + " level: " + level);
				if (!knownTags.contains(tag)) {
					log.warn("Unknown tag: " + tag);
					return;
				}
				
				switch(tag) {
					case "#root":
						return;
					case "urlset":
						log.info("xmlns: " + element.attr("xmlns"));
						break;
					case "url":
						builder.newLocation();
						break;
					default:
						if (builder.hasLocation())
							builder.setLocationProperty(tag, element.text());						
				}
			}
		}

		@Override
		public void tail(Node node, int level) {
			
			if (node instanceof Element) {
				
				Element element = (Element) node;
				String tag = element.tagName().toLowerCase().trim();
				log.trace("tail: " + tag + " level: " + level);
				if (!knownTags.contains(tag)
					|| "#root".equals(tag))
					return;
				
				if ("url".equals(tag)) {
					builder.locationCompleted();
				}
			}
		}
	}
	
	/**
	 * Builder for sitemaps.
	 * 
	 * <p><b>Note:</b> Constructs a valid sitemap even in case of broken XML.
	 * 
	 * <p><b>Postconditions:</b>
	 * <ul>
	 *   <li>Sitemap contains only locations with valid URLs</li>
	 *   <li>Unknown tags are ignored</li>
	 * </ul>
	 */
	private static class SitemapBuilder {
		
		private Sitemap sitemap = new Sitemap();
		private Sitemap.Location location = null;
		private String url = "";
		
		
		/////////////////////////////////////////////////////////////////////////////
		// Public Methods
		//
		
		public Sitemap getSitemap() {
			return sitemap;
		}
		
		public void newLocation() {
			location = new Sitemap.Location();
		}
		
		public boolean hasLocation() {
			return location != null;
		}
		
		public void locationCompleted() {
			
			if (hasLocation()) {
				
				if (location.getUrl() != null)
					sitemap.addLocation(location);
				else
					log.info("Sitemap location dropped: " + url);
			}
			
			location = null;
		}
		
		public void setLocationProperty(String name, String value) {
					
			switch(name) {
				case "loc":
					url = value;
					location.setUrl(toUrl(value));
					break;
				case "lastmod":
					location.setLastModifiedDate(toLocalDate(value));
					break;
				case "changefreq":
					location.setChangeFrequency(value);
					break;
				case "priority":
					location.setPriority(toDouble(value));
					break;
				default:
					log.warn("Unknown location property: " + name);
			}
		}
	}
}
