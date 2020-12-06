package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class PushbackModeFactory implements INamedObjectFactory {

	public PushbackMode forName(String pName) {
		for (PushbackMode mode : PushbackMode.values()) {
			if (mode.getName().equalsIgnoreCase(pName)) {
				return mode;
			}
		}
		return null;
	}

}
