package com.fumbbl.ffb.model;

import java.util.Map;

import com.fumbbl.ffb.FactoryManager;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.FactoryType.FactoryContext;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.INamedObjectFactory;
import com.fumbbl.ffb.factory.SkillFactory;

public class GameRules implements IFactorySource {

	private Map<Factory, INamedObjectFactory> factories;
	private FactoryManager manager;
	private IFactorySource applicationSource;
	
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
		return this.<SkillFactory>getFactory(Factory.SKILL);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends INamedObjectFactory> T getFactory(Factory factory) {
		return (T) factories.get(factory);
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
