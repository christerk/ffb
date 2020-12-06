package com.balancedbytes.games.ffb.server;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.INamedObjectFactory;

/**
 * 
 * @author Kalimar
 */
public class ApothecaryModeFactory implements INamedObjectFactory {

	public ApothecaryMode forName(String pName) {
		for (ApothecaryMode mode : ApothecaryMode.values()) {
			if (mode.getName().equalsIgnoreCase(pName)) {
				return mode;
			}
		}
		return null;
	}

}
