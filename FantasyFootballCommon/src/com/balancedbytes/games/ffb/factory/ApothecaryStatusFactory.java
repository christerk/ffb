package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.ApothecaryStatus;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.GameOptions;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.apothecaryStatus)
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
	public void initialize(Rules rules, GameOptions options) {
		// TODO Auto-generated method stub
		
	}

}
