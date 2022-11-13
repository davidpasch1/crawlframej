package tests.framework.rpick.crawl.frame;

import java.net.URI;

import rpick.http.HttpResponse;
import toolkit.net.mime.MediaType;

public class HttpResponseDummy implements HttpResponse {

	private URI finalUrl = URI.create("https://blogg.com/auth");
	private int code = 200;
	private String reasonPhrase = "OK";
	private String contentType = MediaType.MEDIA_TYPE_HTML.toString();
	private byte[] body = null;
	
	
	/////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//

	public HttpResponseDummy() {
	}
	
	public HttpResponseDummy(int code, String reasonPhrase) {
		this.code = code;
		this.reasonPhrase = reasonPhrase;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setReasonPhrase(String reasonPhrase) {
		this.reasonPhrase = reasonPhrase;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}
	
	/////////////////////////////////////////////////////////////////////////////
	// HttpResponse
	//
	
	@Override
	public int getCode() {
		return code;
	}

	@Override
	public String getReasonPhrase() {
		return reasonPhrase;
	}

	@Override
	public String getStatusLine() {
		return code + " " + reasonPhrase;
	}
	
	@Override
	public String getContentType() {
		return contentType;
	}
	
	@Override
	public byte[] getBody() {
		return body;
	}

	@Override
	public String toHeadString() {
		return code + " " + reasonPhrase;
	}

	@Override
	public URI getFinalUrl() {
		return finalUrl;
	}
}
