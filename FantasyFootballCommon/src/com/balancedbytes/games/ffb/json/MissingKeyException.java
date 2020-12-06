package com.balancedbytes.games.ffb.json;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class MissingKeyException extends RuntimeException {

	public MissingKeyException(String pMessage) {
		super(pMessage);
	}

	public MissingKeyException(Throwable pCause) {
		super(pCause);
	}

	public MissingKeyException(String pMessage, Throwable pCause) {
		super(pMessage, pCause);
	}

}
