package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryManager;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.FactoryType.FactoryContext;

public interface IFactorySource extends ILoggingFacade {

	FactoryManager getFactoryManager();

	FactoryContext getContext();

	IFactorySource forContext(FactoryContext context);

	@SuppressWarnings("rawtypes")
	<T extends INamedObjectFactory> T getFactory(Factory factory);

}
