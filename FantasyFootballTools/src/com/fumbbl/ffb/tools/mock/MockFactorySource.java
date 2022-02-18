package com.fumbbl.ffb.tools.mock;

import com.fumbbl.ffb.FactoryManager;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.INamedObjectFactory;

public class MockFactorySource implements IFactorySource {
	@Override
	public FactoryManager getFactoryManager() {
		return null;
	}

	@Override
	public FactoryType.FactoryContext getContext() {
		return null;
	}

	@Override
	public IFactorySource forContext(FactoryType.FactoryContext context) {
		return null;
	}

	@Override
	public <T extends INamedObjectFactory> T getFactory(FactoryType.Factory factory) {
		return null;
	}

	@Override
	public void logError(long gameId, String message) {

	}

	@Override
	public void logDebug(long gameId, String message) {

	}

	@Override
	public void logWithOutGameId(Throwable throwable) {

	}
}
