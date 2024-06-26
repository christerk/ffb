package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryManager;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.FactoryType.FactoryContext;

public interface IFactorySource {

	FactoryManager getFactoryManager();

	FactoryContext getContext();

	IFactorySource forContext(FactoryContext context);

	<T extends INamedObjectFactory> T getFactory(Factory factory);

	void logError(long gameId, String message);

	void logDebug(long gameId, String message);

	void logWithOutGameId(Throwable throwable);
}
