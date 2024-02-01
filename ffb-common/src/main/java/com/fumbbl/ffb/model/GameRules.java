package com.fumbbl.ffb.model;

import com.fumbbl.ffb.FactoryManager;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.FactoryType.FactoryContext;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.INamedObjectFactory;
import com.fumbbl.ffb.factory.SkillFactory;

import java.util.Map;

public class GameRules implements IFactorySource {

	private Map<Factory, INamedObjectFactory> factories;
	private final FactoryManager manager;
	private final IFactorySource applicationSource;

	public GameRules(IFactorySource applicationSource, FactoryManager manager) {
		this.manager = manager;
		this.applicationSource = applicationSource;
	}

	public void initialize(Game game) {
		factories = manager.getFactoriesForContext(getContext(), game.getOptions());
		for (INamedObjectFactory factory : factories.values()) {
			factory.initialize(game);
		}
	}

	public SkillFactory getSkillFactory() {
		return this.getFactory(Factory.SKILL);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends INamedObjectFactory> T getFactory(Factory factory) {
		return (T) factories.get(factory);
	}

	@Override
	public void logError(long gameId, String message) {
		applicationSource.logError(gameId, message);
	}

	@Override
	public void logDebug(long gameId, String message) {
		applicationSource.logDebug(gameId, message);
	}

	@Override
	public void logWithOutGameId(Throwable throwable) {
		applicationSource.logWithOutGameId(throwable);
	}

	@Override
	public FactoryContext getContext() {
		return FactoryContext.GAME;
	}

	@Override
	public FactoryManager getFactoryManager() {
		return manager;
	}

	@Override
	public IFactorySource forContext(FactoryContext context) {
		if (context == getContext()) {
			return this;
		}
		return applicationSource;
	}
}
