package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class FantasyFootballException extends RuntimeException {

	public FantasyFootballException(String pMessage, Throwable pCause) {
		super(pMessage, pCause);
	}

	public FantasyFootballException(String pMessage) {
		super(pMessage);
	}

	public FantasyFootballException(Throwable pCause) {
		super(pCause);
	}

}
