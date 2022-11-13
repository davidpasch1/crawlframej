package rpick.crawl.frame.stages;

import toolkit.net.mime.HttpContentType;

/**
 * Content's type.
 * 
 * <p>The type of some content is represented differently in the www and on the file system.
 * This class unifies the two representations.
 * 
 * <p><b>Note:</b> It is possible that both, the HTTP Content Type and the file extension, are null.
 *   Consequently, in such a case nothing is known about the content's type. 
 * 
 * <p><b>Paradigm:</b> Immutable.
 */
public class ContentType {

	private final HttpContentType httpContentType;
	private final String fileExt;
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//

	/**
	 * Constructor.
	 * 
	 * @param type HTTP Content Type; may be null
	 * @param ext file extension; may be null
	 */
	public ContentType(HttpContentType type, String ext) {
		httpContentType = type;
		fileExt = ext;
	}
	
	/**
	 * HTTP Content Type.
	 * 
	 * @return HTTP Content Type; may be null
	 */
	public HttpContentType getHttpContentType() {
		return httpContentType;
	}

	/**
	 * File extension.
	 * 
	 * @return file extension; may be null
	 */
	public String getFileExt() {
		return fileExt;
	}
}
