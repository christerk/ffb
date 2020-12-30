package com.balancedbytes.games.ffb.factory;

import java.util.Collection;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.IKeyedItem;
import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public interface INamedObjectFactory<T> extends IKeyedItem {

	public INamedObject forName(String pName);

	public void initialize(Game game);
	
	default Object getKey() {
		FactoryType a = this.getClass().getAnnotation(FactoryType.class);
		return a;
	}

	default void register(Collection<T> items) {}
}
