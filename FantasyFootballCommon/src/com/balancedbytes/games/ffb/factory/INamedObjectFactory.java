package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.GameOptions;

/**
 * 
 * @author Kalimar
 */
public interface INamedObjectFactory {

	public INamedObject forName(String pName);

	public void initialize(Rules rules, GameOptions options);

}
