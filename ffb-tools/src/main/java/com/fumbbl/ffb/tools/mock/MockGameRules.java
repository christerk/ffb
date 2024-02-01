package com.fumbbl.ffb.tools.mock;

import com.fumbbl.ffb.FactoryManager;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.GameRules;

public class MockGameRules extends GameRules {

	private MockSkillFactory factory = new MockSkillFactory();

	public MockGameRules(IFactorySource applicationSource, FactoryManager manager) {
		super(applicationSource, manager);
	}

	@Override
	public SkillFactory getSkillFactory() {
		return factory;
	}

	@Override
	public SkillFactory getFactory(FactoryType.Factory factory) {
		return this.factory;
	}
}
