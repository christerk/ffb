package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.report.ReportId;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.REPORT_ID)
@RulesCollection(Rules.COMMON)
public class ReportIdFactory implements INamedObjectFactory {

	public ReportId forName(String pName) {
		for (ReportId mode : ReportId.values()) {
			if (mode.getName().equalsIgnoreCase(pName)) {
				return mode;
			}
		}
		return null;
	}

	@Override
	public void initialize(GameOptions options) {
		// TODO Auto-generated method stub
		
	}

}
