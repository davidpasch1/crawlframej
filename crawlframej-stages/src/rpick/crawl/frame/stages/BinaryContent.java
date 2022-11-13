package rpick.crawl.frame.stages;

/**
 * Binary content.
 */
public class BinaryContent implements Content {

	private final ContentType contentType;
	private final byte[] bytes;
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//
	
	/**
	 * Constructor.
	 * 
	 * @param contentType content's type
	 * @param content content
	 */
	public BinaryContent(
			ContentType contentType,
			byte[] content
		) {
		
		this.contentType = contentType;
		this.bytes = content;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Content
	//
	
	@Override
	public ContentType getContentType() {
		return contentType;
	}

	@Override
	public byte[] getBytes() {
		return bytes;
	}
}
