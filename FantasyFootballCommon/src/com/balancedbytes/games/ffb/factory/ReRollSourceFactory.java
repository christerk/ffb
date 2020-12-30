package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRollSources;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.GameOptions;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.RE_ROLL_SOURCE)
@RulesCollection(Rules.COMMON)
public class ReRollSourceFactory implements INamedObjectFactory {

	static ReRollSources reRollSources = new ReRollSources();

	public ReRollSourceFactory() {
		reRollSources = new ReRollSources();
	}

	public ReRollSource forName(String pName) {
		return reRollSources.values().get(pName.toLowerCase());
	}

	@Override
	public void initialize(GameOptions options) {
		// TODO Auto-generated method stub
		
	}

}
