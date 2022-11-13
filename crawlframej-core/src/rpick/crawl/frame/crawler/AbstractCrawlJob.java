package rpick.crawl.frame.crawler;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractCrawlJob implements CrawlJob {

	private static final Logger log = LogManager.getLogger(AbstractCrawlJob.class);
	
	private URI url;
	private List<URI> oldUrls = new ArrayList<>();

	/**
	 * this.class -> (Stage -> (CrawlJob -> parameters))
	 */
	private static final Map<String, Map<String, Function<CrawlJob,Object> > > stageParams = 
			new HashMap<String, Map<String, Function<CrawlJob,Object> > >();
	
	// Rationale: association between job and result
	/**
	 * Stage -> result.
	 */
	private final Map<String, Object> results = new LinkedHashMap<String, Object>();
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//
	
	@Override
	public void setUrl(URI url) {
		oldUrls.add(url);
		this.url = url;
		log.debug("Job's URL changed: " + url);
	}
	
	@Override
	public URI getUrl() {
		return url;
	}
	
	@Override
	public List<URI> getOldUrls() {
		return oldUrls;
	}
	
	@Override
	public Object getStageParams(String stage) throws BrokenPipelineException {
		
		String type = this.getClass().getName();
		if (!stageParams.containsKey(type))
			throw new BrokenPipelineException(stage, "Missing parameters for stage.");
		
		Map<String, Function<CrawlJob, Object>> stage2params = stageParams.get(type);
		if (!stage2params.containsKey(stage))
			throw new BrokenPipelineException(stage, "Missing parameters for stage.");
		
		return stage2params.get(stage).apply(this);
	}
		
	@Override
	public void setResult(String stage, Object result) throws BrokenPipelineException {
		
		if (results.containsKey(stage))
			throw new BrokenPipelineException(stage, "Repeated entry of stage.");

		results.put(stage, result);
	}
	
	@Override
	public Object getResult(String stage) {
		return results.get(stage);
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Protected Methods
	//

	/**
	 * @param url page to be fetched
	 */
	protected AbstractCrawlJob(URI url) {
		this.url = url;
	}
	
	/**
	 * Sets stage parameters for derived class.
	 * 
	 * <p>The parameter is computed by a mapping <code>(stage, job) -> params</code>.
	 * 
	 * @param type derived class
	 * @param stage stage identifier
	 * @param params stage parameters
	 */
	protected static void setStageParams(
			Class<? extends AbstractCrawlJob> type, 
			String stage, 
			Function<CrawlJob,Object> params) 
				throws BrokenPipelineException {

		String typeName = type.getName();
		if (!stageParams.containsKey(typeName)) {
		
			stageParams.put(typeName, new HashMap<>(Collections.singletonMap(stage, params)));
		
		} else {
			
			Map<String, Function<CrawlJob, Object>> stage2params = stageParams.get(typeName);
			if (stage2params.containsKey(stage))
				throw new BrokenPipelineException(stage, "Duplicate parameters for stage.");
			
			stage2params.put(stage, params);
		}
	}
}
