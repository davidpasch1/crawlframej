package rpick.http;

/**
 * If server got filtered.
 */
@SuppressWarnings("serial")
public class ServerFilteredException extends Exception {

	public ServerFilteredException(String host) {
		super(host);
	}
}
