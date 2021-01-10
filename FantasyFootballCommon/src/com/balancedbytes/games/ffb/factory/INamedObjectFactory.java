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

	INamedObject forName(String pName);

	void initialize(Game game);
	
	default String getKey() {
		FactoryType a = this.getClass().getAnnotation(FactoryType.class);
		return a.value().name();
	}

	default void register(Collection<T> items) {}
}
