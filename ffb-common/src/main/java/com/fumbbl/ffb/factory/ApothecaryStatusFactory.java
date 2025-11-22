package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.ApothecaryStatus;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.APOTHECARY_STATUS)
@RulesCollection(Rules.COMMON)
public class ApothecaryStatusFactory implements INamedObjectFactory {

	public ApothecaryStatus forName(String pName) {
		for (ApothecaryStatus status : ApothecaryStatus.values()) {
			if (status.getName().equalsIgnoreCase(pName)) {
				return status;
			}
		}
		return null;
	}

	@Override
	public void initialize(Game game) {
	}

}
