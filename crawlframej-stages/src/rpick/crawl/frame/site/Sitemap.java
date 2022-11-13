package rpick.crawl.frame.site;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Sitemap.
 * 
 * <p>See <a href="www.sitemaps.org">Sitemaps standard</a>.
 */
public class Sitemap {

	private List<Location> locations = new ArrayList<>();

	
	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//	

	/**
	 * Location elements in sitemap.
	 * 
	 * @return location elements in sitemap
	 */
	public List<Location> getLocations() {
		return locations;
	}

	/**
	 * Adds location element to sitemap.
	 * 
	 * @param location location element in sitemap
	 */
	public void addLocation(Location location) {
		locations.add(location);
	}

	
	/////////////////////////////////////////////////////////////////////////////
	// Public Classes
	//	
	
	/**
	 * Location element in a sitemap.
	 * 
	 * <p><b>Note:</b> By standard only the URL is mandatory, all other fields may be null.
	 */
	public static class Location {
		
		private URI url;
		private LocalDate lastModifiedDate;
		private String changeFrequency;
		private Double priority;

		
		/////////////////////////////////////////////////////////////////////////////
		// Public Methods
		//		
		
		/**
		 * URL.
		 * 
		 * <p><b>Note:</b> By standard all URLs must belong to the same site eg {@code example.com}
		 * 
		 * @return valid URL; not null
		 */
		public URI getUrl() {
			return url;
		}
		
		/**
		 * URL.
		 * 
		 * @param url URL; not null
		 */
		public void setUrl(URI url) {
			this.url = url;
		}
		
		/**
		 * Last modified date.
		 * 
		 * @return last modified date; may be null
		 */
		public LocalDate getLastModifiedDate() {
			return lastModifiedDate;
		}
		
		/**
		 * Last modified date.
		 * 
		 * <p><b>Note:</b> Does not conform to standard as it should be W3C Datetime format (<a href="https://www.w3.org/TR/NOTE-datetime">Date and Time Formats</a>).
		 * 
		 * @param lastModifiedDate last modified date; may be null
		 */
		public void setLastModifiedDate(LocalDate lastModifiedDate) {
			this.lastModifiedDate = lastModifiedDate;
		}
		
		/**
		 * Change frequency.
		 * 
		 * <p><b>Note:</b> Should be an enum (see Sitemaps standard for possible values).
		 * 
		 * @return changeFrequency change frequency; may be null
		 */
		public String getChangeFrequency() {
			return changeFrequency;
		}
		
		/**
		 * Change frequency.
		 * 
		 * @param changeFrequency change frequency; may be null
		 */
		public void setChangeFrequency(String changeFrequency) {
			this.changeFrequency = changeFrequency;
		}
		
		/**
		 * Priority.
		 * 
		 * @return priority; may be null
		 */
		public Double getPriority() {
			return priority;
		}
		
		/**
		 * Priority.
		 * 
		 * @param priority priority; may be null
		 */
		public void setPriority(Double priority) {
			this.priority = priority;
		} 
	}
}
