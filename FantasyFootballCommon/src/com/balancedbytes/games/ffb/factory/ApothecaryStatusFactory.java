package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.ApothecaryStatus;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Game;

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
		// TODO Auto-generated method stub
		
	}

}