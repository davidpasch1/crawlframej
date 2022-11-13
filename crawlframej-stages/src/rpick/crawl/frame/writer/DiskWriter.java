package rpick.crawl.frame.writer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import toolkit.files.FileTools;

/**
 * Writes content to disk.
 * 
 * <p><b>Features:</b>
 * <ul>
 *   <li><i>Normalized output:</i> Writes text files as UTF-8.</li>
 * </ul>
 */
public class DiskWriter {
	
	/* Rationale:
	 * - write-norm: normalized output of text as UTF-8
	 *   - original encoding irrelevant, as input text is in UTF-16
	 *   - normalized output of textual content (see disk_writer_stage)
	 */
	
	private static final Logger log = LogManager.getLogger(DiskWriter.class);
	
	private Path outPath;

	
	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//

	/**
	 * @param outPath output path
	 */
	public DiskWriter(Path outPath) {
		
		this.outPath = outPath;
	}
	
	/**
	 * Writes content to disk.
	 * 
	 * <p><b>Note:</b> Skips empty files.
	 * <p><b>Note:</b> Writes file as UTF-8.
	 * 
	 * @param filePath relative file path
	 * @param content content to be written
	 */
	public void write(Path filePath, String content)
			throws 
			IOException, 
			FileAlreadyExistsException {
		
		write(filePath, content.getBytes(StandardCharsets.UTF_8));
	}
	
	/**
	 * Writes content to disk.
	 * 
	 * <p><b>Note</b>: Skips empty files.
	 * 
	 * @param filePath relative file path
	 * @param content content to be written
	 */
	public void write(Path filePath, byte[] content)
			throws 
				IOException, 
				FileAlreadyExistsException {
		
		if (content.length == 0) {
			
			log.debug("Skipped empty file: " + filePath);
			return;
		}
		
		Path outFilePath = Paths.get(outPath.toString(), filePath.toString());

		log.debug("Writing file: " + outFilePath);
		FileTools.write(content, outFilePath);
		log.info("File written: " + outFilePath);
	}
}
