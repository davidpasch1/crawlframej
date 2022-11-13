package rpick.crawl.frame.stages;

/**
 * Textual content.
 * 
 * <p><b>Important:</b> Implemented by all classes that represent MIME type <code>text</code>.
 * Therefore, a consumer of content with MIME type <code>text</code> can safely assume that it can be casted to <code>TextContent</code>.    
 * 
 * <p><b>Paradigm:</b> Immutable.
 */
public interface TextContent extends Content {

	public String getText();
}
