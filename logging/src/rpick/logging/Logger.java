package rpick.logging;

// TODO OPTIONAL logging make use of DEBUG1
/**
 * Logger.
 */
public interface Logger {

	public void trace(Object msg);
	
	/**
	 * Intermediate level between DEBUG and TRACE. 
	 */
	public void debug1(Object msg);
	
	public void debug(Object msg);
	public void info(Object msg);
	public void warn(Object msg);
	public void error(Object msg);
	public void error(String msg, Throwable t);
	
	/**
	 * Log for performance debugging.
	 * 
	 * Intermediate level between DEBUG and TRACE. 
	 */
	public void perfDebug1(Object msg);
	
	/**
	 * Log for performance debugging.
	 */
	public void perfDebug(Object msg);
	
	/**
	 * Log to INFO and console.
	 */
	public void infoCout(Object msg);
}
