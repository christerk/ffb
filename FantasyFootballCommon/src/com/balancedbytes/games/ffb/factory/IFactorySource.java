package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryManager;
import com.balancedbytes.games.ffb.FactoryType.Factory;
import com.balancedbytes.games.ffb.FactoryType.FactoryContext;

public interface IFactorySource {

	FactoryManager getFactoryManager();
	FactoryContext getContext();
	IFactorySource forContext(FactoryContext context);

	<T extends INamedObjectFactory> T getFactory(Factory factory);
}
