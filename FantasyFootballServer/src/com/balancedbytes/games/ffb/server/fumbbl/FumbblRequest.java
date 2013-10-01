package com.balancedbytes.games.ffb.server.fumbbl;



/**
 * 
 * @author Kalimar
 */
public abstract class FumbblRequest {
	
	private String fRequestUrl;
  
  public abstract void process(FumbblRequestProcessor pRequestProcessor);
  
  public String getRequestUrl() {
		return fRequestUrl;
	}
  
  public void setRequestUrl(String pRequestUrl) {
  	fRequestUrl = pRequestUrl;
  }
  
}
