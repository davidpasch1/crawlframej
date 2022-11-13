package rpick.logging;

/**
 * Factory for loggers.
 * 
 * Implementation based on log4j.
 */
public class Log4jManager {
	
	public static Logger create(Class<?> clazz) {
		return new Log4jLogger(clazz);
	}
	
	public static Logger create(org.apache.logging.log4j.Logger log4jLogger) {
		return new Log4jLogger(log4jLogger);
	}
}
