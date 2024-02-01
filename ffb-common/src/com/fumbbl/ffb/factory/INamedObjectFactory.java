package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.IKeyedItem;
import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.model.Game;

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
}
