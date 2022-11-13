package rpick.crawl.frame.stages;

/**
 * Content.
 * 
 * <p><b>Paradigm:</b> Immutable.
 */
public interface Content {

	/**
	 * Content's type.
	 * 
	 * @return content's type
	 */
	public ContentType getContentType();

	/** 
	 * Binary representation.
	 * 
	 * @return binary representation
	 */
	public byte[] getBytes();
}
