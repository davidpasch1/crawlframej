package rpick.crawl.frame.crawler;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import toolkit.files.FileTools;

/**
 * Creates jobs for crawling disk files.
 */
public class DiskCrawlStrategy extends AbstractDownloadCrawlStrategy {

	private static final Logger log = LogManager.getLogger(DiskCrawlStrategy.class);
	
	private final String[] inFileExts;
	

	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//
	
	/**
	 * Constructor.
	 * 
	 * <p><b>Note:</b> File extensions are case-sensitive (as it is normal in Linux).
	 * 
	 * @param inFileExts accepted input file extensions
	 */
	public DiskCrawlStrategy(String[] inFileExts) {
		this.inFileExts = inFileExts;
	}

	/**
	 * Recursively traverses input directory and creates crawl jobs.
	 * 
	 * @param inDirPath input directory
	 */
	public void createJobs(Path inDirPath) throws IOException {
		
		List<Path> files = FileTools.listFiles(inDirPath);
		jobs = files.stream()
				.filter(file -> FileTools.isFileExt(file, inFileExts))
				.map(file -> new UniformCrawlJob(file.toUri()))
				.collect(Collectors.toList());
		
		log.debug("Added " + jobs.size() + " jobs to queue.");
	}
}
