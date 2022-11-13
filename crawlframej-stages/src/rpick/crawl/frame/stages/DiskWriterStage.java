package rpick.crawl.frame.stages;

import java.net.URI;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import rpick.crawl.frame.common.CrawlerConfig;
import rpick.crawl.frame.crawler.AbortCrawlException;
import rpick.crawl.frame.crawler.BrokenPipelineException;
import rpick.crawl.frame.crawler.CrawlJob;
import rpick.crawl.frame.crawler.pipeline.AbortJobException;
import rpick.crawl.frame.writer.DiskWriter;
import toolkit.err.ErrorMessages;
import toolkit.net.mime.HttpContentType;
import toolkit.net.mime.MediaType;
import toolkit.net.mime.MimeFileExtMap;
import toolkit.net.mime.MimeTools;

/**
 * Uses a {@link DiskWriter} to write web content to disk.
 * 
 * <p><b>Features:</b>
 * <ul>
 *   <li><i>Normalized output:</i></li>
 *   <ul>
 *     <li>Automatically adds missing file extension.</li>
 *     <li>Normalizes file extensions eg <code>.htm</code> and <code>.html</code> as <code>.html</code>.</li>
 *     <li>Writes text files as UTF-8.</li>
 *   </ul>
 * </ul>
 * 
 * 
 * <h2>Normalization of file extensions</h2>
 * 
 * <p><b>Note:</b> Feature has to be explicitly enabled (see {@link CrawlerConfig}).
 *  
 * <p><b>Notes:</b> 
 * <ul>
 *   <li>Always appends file extension which corresponds to known media type,
 *       even if the input file's extension suggests a different media type.</li>
 *   <li>If no mapping is found for the content's media type,
 *       the original file extension is left untouched.</li>
 * </ul>
 */
public class DiskWriterStage extends AbstractProcessingStage {

	/* Issues:
	 * - maps URL to file path
	 * Rationale:
	 * - use of String (not Path) for file paths: Path does not always preserve chars ('/'),
	 *   thus String is used for paths that are still under construction,
	 *   whereas Path is used for final path
	 * - write-norm: normalized output:
	 *   - file_ext: normalized file extensions
	 *   - text as UTF-8
	 * - write-norm-file_ext:
	 *   - uses available knowledge to normalize extension
	 *   => if media_type unknown, does nothing
	 * - error_handling: duplicate pages allowed, because of possibly multiple references to same page.
	 */

	
	private static final Logger log = LogManager.getLogger(DiskWriterStage.class);
	
	private final DiskWriter diskWriter;
	private final boolean isNormFileExt;

	
	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//
	
	/**
	 * @param outPath output path
	 * @param isNormFileExt whether to normalize file extension of output files
	 */
	public DiskWriterStage(Path outPath, boolean isNormFileExt) {
		super(log);		
		setErrorHandler(exception -> {
			if (exception instanceof FileAlreadyExistsException)
				throw new AbortJobException(exception);
			throw new AbortCrawlException(exception);
		});
		diskWriter = new DiskWriter(outPath);
		this.isNormFileExt = isNormFileExt;
	}

	
	/////////////////////////////////////////////////////////////////////////////
	// AbstractProcessingStage
	//

	/**
	 * {@inheritDoc}
	 * 
	 * @param input input of type {@link Content}
	 */
	@Override
	protected Object processData(CrawlJob job, Object input) 
		throws 
			StageProcessingException, 
			BrokenPipelineException {
		
		if (!(input instanceof Content))
			throw new BrokenPipelineException(
					this,
					"Invalid stage input: "
					+ ErrorMessages.getTypeMismatchString(
						input.getClass(),
						Content.class));
		Content content = (Content) input;

		URI url = job.getUrl();
		HttpContentType httpContentType  = content.getContentType().getHttpContentType();
		MediaType mediaType = httpContentType != null ? httpContentType.getMediaType() : null;
		MimeFileExtMap map = CrawlerConfig.get().getMimeFileExtMap();
		Path filePath = MimeTools.createFilePath(url, mediaType, map);
		if (filePath == null) {
			log.info("Rejected file without filename: " + url);
			return null;
		}
		// ISSUE: content_type: Normalized file extensions
		if (isNormFileExt)
			filePath = Paths.get(MimeTools.normalizeFileExt(filePath.toString(), mediaType, map));
		
		try {
		
			if (content instanceof TextContent) {

				TextContent textContent = (TextContent) content;
				diskWriter.write(filePath, textContent.getText());
			
			} else {
				
				diskWriter.write(filePath, content.getBytes());
			}
		
		} catch (Exception e) {
			
			throw new StageProcessingException(e);
		}
		
		// terminal stage
		return null;
	}

	
	/////////////////////////////////////////////////////////////////////////////
	// Private Methods
	//
	
}
