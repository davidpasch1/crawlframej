package rpick.crawl.frame.crawler;

import rpick.crawl.frame.crawler.pipeline.Stage;

/**
 * If the pipeline is broken.
 * For example, the stage identifiers are not unique or the stages do not form a tree.
 */
@SuppressWarnings("serial")
public class BrokenPipelineException extends RuntimeException {

	public BrokenPipelineException(String msg) {
		super(msg);
	}
	
	public BrokenPipelineException(String stage, String msg) {
		super(stage + ": " + msg);
	}

	public BrokenPipelineException(Stage stage, String msg) {
		super(stage.getIdentifier() + ": " + msg);
	}

	public BrokenPipelineException(Exception cause) {
		super(cause);
	}
}
