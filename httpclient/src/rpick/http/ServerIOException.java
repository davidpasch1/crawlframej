package rpick.http;

import java.io.IOException;

/**
 * I/O error during communication with server.
 */
@SuppressWarnings("serial")
public class ServerIOException extends Exception {

	private String host;
	
	public ServerIOException(String host, IOException e) {
		
		super("server: " + host + ", error: " + e.getMessage(), e);
		this.host = host;
	}

	public String getHost() {
		return host;
	}
}
