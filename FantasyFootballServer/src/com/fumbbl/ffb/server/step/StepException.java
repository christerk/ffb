package com.fumbbl.ffb.server.step;

/**
 * 
 * @author Kalimar
 */
public class StepException extends RuntimeException {

	public StepException(String pMessage) {
		super(pMessage);
	}

	public StepException(String pMessage, Throwable pCause) {
		super(pMessage, pCause);
	}

}
