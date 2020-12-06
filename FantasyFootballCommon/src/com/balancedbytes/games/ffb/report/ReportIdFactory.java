package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.INamedObjectFactory;

/**
 * 
 * @author Kalimar
 */
public class ReportIdFactory implements INamedObjectFactory {

	public ReportId forName(String pName) {
		for (ReportId mode : ReportId.values()) {
			if (mode.getName().equalsIgnoreCase(pName)) {
				return mode;
			}
		}
		return null;
	}

}
