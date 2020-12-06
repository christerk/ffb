package com.balancedbytes.games.ffb.server;

/**
 * 
 * @author Kalimar
 */
public class IdGenerator {

	private long fLastId;

	public IdGenerator(long pLastId) {
		fLastId = pLastId;
	}

	public synchronized long generateId() {
		return ++fLastId;
	}

	public synchronized long lastId() {
		return fLastId;
	}

}
