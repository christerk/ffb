package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.report.ReportId;

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
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}
