package com.balancedbytes.games.ffb.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FactoryType.Factory;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.factory.INamedObjectFactory;
import com.balancedbytes.games.ffb.factory.SkillFactory;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.util.Scanner;

public class GameRules {

	private HashMap<Factory, INamedObjectFactory> factories;
	
	public GameRules(GameOptions options) {
		factories = new HashMap<>();
		
		String rulesVersion = options.getOptionWithDefault(GameOptionId.RULESVERSION).getValueAsString();
		
		Scanner<INamedObjectFactory> scanner = new Scanner<>(INamedObjectFactory.class);
		for (Class<INamedObjectFactory> factoryClass : scanner.getClassesImplementing()) {
			for (Annotation a : factoryClass.getAnnotations()) {
				if (a instanceof FactoryType) {
					
					try {
						Constructor<INamedObjectFactory> constructor = factoryClass.getConstructor();
						INamedObjectFactory factory = constructor.newInstance();
						factories.put(((FactoryType)a).value(), factory);
					} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						e.printStackTrace();
					}
					
				}
			}
		}

		RulesCollection.Rules rules = Rules.BB2020;
		try {
			RulesCollection.Rules.valueOf(rulesVersion);
		} catch (IllegalArgumentException e) {
		}
		
		for (INamedObjectFactory factory : factories.values()) {
			factory.initialize(rules, options);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends INamedObjectFactory> T getFactory(Factory factory) {
		return (T) factories.get(factory);
	}
	
	public SkillFactory getSkillFactory() {
		return this.<SkillFactory>getFactory(Factory.SKILL);
	}
}
