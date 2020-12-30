package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.INamedObject;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.IKeyedItem;
import com.balancedbytes.games.ffb.model.GameOptions;

/**
 * 
 * @author Kalimar
 */
public interface INamedObjectFactory extends IKeyedItem {

	public INamedObject forName(String pName);

	public void initialize(GameOptions options);
	
	default Object getKey() {
		FactoryType a = this.getClass().getAnnotation(FactoryType.class);
		return a;
	}
}
