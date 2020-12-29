package com.balancedbytes.games.ffb.server.request;

import com.balancedbytes.games.ffb.model.Game;

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
