package com.balancedbytes.games.ffb.server;

import com.balancedbytes.games.ffb.util.ArrayTool;

/**
 * 
 * @author Kalimar
 */
public enum ServerMode {
  
  STANDALONE("standalone"),
  FUMBBL("fumbbl"),
  STANDALONE_INIT_DB("standaloneInitDb"),
  FUMBBL_INIT_DB("fumbblInitDb");
  
  private String fName;
  
  private ServerMode(String pName) {
    fName = pName;
  }
  
  public String getName() {
    return fName;
  }
  
  public boolean isStandalone() {
  	return (STANDALONE.equals(this) || STANDALONE_INIT_DB.equals(this)); 
  }
  
  public boolean isInitDb() {
  	return (STANDALONE_INIT_DB.equals(this) || FUMBBL_INIT_DB.equals(this)); 
  }

  public static ServerMode fromArguments(String[] pArguments) {
  	if (ArrayTool.isProvided(pArguments)) {
    	if ("fumbbl".equalsIgnoreCase(pArguments[0])) {
    		if (pArguments.length < 2) {
    			return ServerMode.FUMBBL;
    		}
    		if ("initdb".equalsIgnoreCase(pArguments[1])) {
    			return ServerMode.FUMBBL_INIT_DB;
    		}
    	}
    	if ("standalone".equalsIgnoreCase(pArguments[0])) {
    		if (pArguments.length < 2) {
    			return ServerMode.STANDALONE;
    		}
    		if ("initdb".equalsIgnoreCase(pArguments[1])) {
    			return ServerMode.STANDALONE_INIT_DB;
    		}	
    	}
  	}
  	return null;
  }

}
