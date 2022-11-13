package rpick.crawl.frame.stages;

import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;

/**
 * Textual content stored as <code>String</code>.
 */
public class StringContent implements TextContent {

	private final ContentType contentType;
	private final String text;
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//
	
	/**
	 * Constructor.
	 * 
	 * @param contentType content's type
	 * @param text content
	 */

	public StringContent(
			ContentType contentType,
			String text
		) {
		
		this.contentType = contentType;
		this.text = text;
	}
	
	/////////////////////////////////////////////////////////////////////////////
	// TextContent
	//
	
	@Override
	public ContentType getContentType() {
		return contentType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return binary representation using UTF-8
	 */
	@Override
	public byte[] getBytes() {
		return StringUtils.getBytes(text, StandardCharsets.UTF_8);
	}
	
	@Override
	public String getText() {
		return text;
	}
}
