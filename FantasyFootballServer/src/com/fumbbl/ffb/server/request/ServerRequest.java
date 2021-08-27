package com.fumbbl.ffb.server.request;

/**
 * 
 * @author Kalimar
 */
public abstract class ServerRequest {

	private String fRequestUrl;

	public abstract void process(ServerRequestProcessor pRequestProcessor);

	public String getRequestUrl() {
		return fRequestUrl;
	}

	public void setRequestUrl(String pRequestUrl) {
		fRequestUrl = pRequestUrl;
	}

}
