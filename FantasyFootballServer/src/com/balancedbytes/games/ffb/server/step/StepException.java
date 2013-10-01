package com.balancedbytes.games.ffb.server.step;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class StepException extends RuntimeException {

	public StepException(String pMessage) {
		super(pMessage);
	}

	public StepException(String pMessage, Throwable pCause) {
		super(pMessage, pCause);
	}

}
