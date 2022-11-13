package rpick.crawl.frame.common;

import toolkit.net.mime.MimeFileExtMap;

/**
 * Global crawler configuration implemented as singleton.
 * 
 * <p><b>Important</b>: Before startup of system, all mandatory fields must be set.
 * A field is mandatory, unless it is not specified otherwise.</p>
 * 
 * <p><b>Design Pattern:</b> Singleton.
 * 
 * <p><b>Threading:</b> Accessors are thread-safe.
 */
public class CrawlerConfig {
	
	private MimeFileExtMap mimeFileExtMap = null;
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//

	/**
	 * MIME &hyphen; file extension map.
	 * 
	 * @return MIME &hyphen; file extension map
	 */
	public MimeFileExtMap getMimeFileExtMap() {
		return mimeFileExtMap;
	}

	/**
	 * MIME &hyphen; file extension map.
	 * 
	 * @param mimeFileExtMap MIME &hyphen; file extension map
	 */
	public void setMimeFileExtMap(MimeFileExtMap mimeFileExtMap) {
		this.mimeFileExtMap = mimeFileExtMap;
	}
	
	public static CrawlerConfig get() {
		return LazyHolder.INSTANCE;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Private Methods
	//
	
	private CrawlerConfig() {
	}
	
	/**
	 * Implements thread-safe lazy initialization of singleton.
	 * 
	 * <p>Exploits the fact that JVM initializes inner static class only on first access (lazily-and-only-once). 
	 * 
	 * <p>See <a href="https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom">Initialization-on-demand holder idiom</a>.
	 */
	private static class LazyHolder {
		static final CrawlerConfig INSTANCE = new CrawlerConfig();
	}
}
