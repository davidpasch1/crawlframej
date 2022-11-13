package rpick.crawl.frame.site.internal;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import rpick.crawl.frame.site.SitemapParser;

/**
 * Utilities used by {@link SitemapParser}.
 */
public class SitemapParserUtils {

	private static final Logger log = LogManager.getLogger(SitemapParser.class);
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//
	
	/**
	 * Converts to URL.
	 * 
	 * <p><b>Side-effect:</b> Logs a warning on error.
	 * 
	 * @param value URL, possibly invalid
	 * @return null on error
	 */
	public static URI toUrl(String value) {
		
		try {
		
			return new URI(value);
		
		} catch (URISyntaxException e) {
			log.warn("URL broken: " + value + " exception: " + e.getMessage());			
			return null;
		}
	}

	/**
	 * Converts to LocalDate.
	 * 
	 * <p><b>Note:</b> Does not conform to standard as it should be W3C Datetime format (https://www.w3.org/TR/NOTE-datetime).
	 * 
	 * <p><b>Side-effect:</b> Logs a warning on error.
	 *  
	 * @param value date in format YYYY-MM-DD, possibly invalid
	 * @return null on error
	 */
	public static LocalDate toLocalDate(String value) {
		
		try {
			
			return LocalDate.parse(value);
			
		} catch (DateTimeParseException e) {
			log.warn("Date invalid: " + value + " exception: " + e.getMessage());			
			return null;
		}
	}
	
	/**
	 * Converts to Double.
	 * 
	 * <p><b>Side-effect:</b> Logs a warning on error.
	 * 
	 * @param value floating-point number, possibly invalid 
	 * @return null on error
	 */
	public static Double toDouble(String value) {
		
		try {
			
			return Double.parseDouble(value);
			
		} catch (NumberFormatException e) {
			log.warn("Number invalid: " + value + " exception: " + e.getMessage());			
			return null;
		}
	}
}
