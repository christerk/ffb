package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;

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
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}
