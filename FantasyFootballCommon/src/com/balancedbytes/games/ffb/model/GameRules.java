package com.balancedbytes.games.ffb.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FactoryType.Factory;
import com.balancedbytes.games.ffb.factory.INamedObjectFactory;
import com.balancedbytes.games.ffb.factory.SkillFactory;
import com.balancedbytes.games.ffb.util.Scanner;

public class GameRules {

	private HashMap<Factory, INamedObjectFactory> factories;
	
	public GameRules(GameOptions options) {
		factories = new HashMap<>();
		
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

		for (INamedObjectFactory factory : factories.values()) {
			factory.initialize(options);
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
