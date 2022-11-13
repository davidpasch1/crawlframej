package rpick.logging;

/**
 * Implementation of logger based on log4j.
 */
public class Log4jLogger implements Logger {

	private static final org.apache.logging.log4j.Level debug1Level = 
			org.apache.logging.log4j.Level.getLevel("DEBUG1");
	private static final org.apache.logging.log4j.Marker perfMarker = 
			org.apache.logging.log4j.MarkerManager.getMarker("PerfDebug");
	
	private final org.apache.logging.log4j.Logger log4jLogger;
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Logger
	//
	
	@Override
	public void trace(Object msg) {
		log4jLogger.trace(msg);
	}
	
	@Override
	public void debug(Object msg) {
		log4jLogger.debug(msg);
	}
	
	@Override
	public void debug1(Object msg) {
		log4jLogger.log(debug1Level, msg);
	}

	@Override
	public void info(Object msg) {
		log4jLogger.info(msg);
	}

	@Override
	public void warn(Object msg) {
		log4jLogger.warn(msg);
	}

	@Override
	public void error(Object msg) {
		log4jLogger.error(msg);
	}

	@Override
	public void error(String msg, Throwable t) {
		log4jLogger.error(msg, t);
	}
	
	@Override
	public void perfDebug(Object msg) {
		log4jLogger.debug(perfMarker, msg);		
	}
	
	@Override
	public void perfDebug1(Object msg) {
		log4jLogger.log(debug1Level, perfMarker, msg);		
	}
	
	@Override
	public void infoCout(Object msg) {
		log4jLogger.info(msg);
		System.out.println(msg);
		
	}

	
	/////////////////////////////////////////////////////////////////////////////
	// Package Methods
	//
	
	Log4jLogger(Class<?> clazz) {
		log4jLogger = org.apache.logging.log4j.LogManager.getLogger(clazz); 
	}

	Log4jLogger(org.apache.logging.log4j.Logger logger) {
		log4jLogger = logger;
	}
}
