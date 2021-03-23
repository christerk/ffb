package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.FactoryType.Factory;
import com.balancedbytes.games.ffb.FactoryType.FactoryContext;
import com.balancedbytes.games.ffb.factory.INamedObjectFactory;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.util.Scanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class FactoryManager {
	public FactoryManager() {
		
	}
	
	public Map<Factory, INamedObjectFactory> getFactoriesForContext(FactoryContext context) {
		Map<Factory, INamedObjectFactory> factories = new HashMap<>();
		Scanner<INamedObjectFactory> scanner = new Scanner<>(INamedObjectFactory.class);

		for (Class<INamedObjectFactory> factoryClass : scanner.getClassesImplementing()) {
			for (Annotation a : factoryClass.getAnnotations()) {
				if (a instanceof FactoryType) {
					Factory factoryType = ((FactoryType)a).value();
					try {
						Constructor<INamedObjectFactory> constructor = factoryClass.getConstructor();
						INamedObjectFactory factory = constructor.newInstance();
						if (factoryType.context == context) {
							factories.put(factoryType, factory);
						}
					} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return factories;
	}

	public Map<Factory, INamedObjectFactory> getFactoriesForContext(FactoryContext context, GameOptions gameOptions) {
		Map<Factory, INamedObjectFactory> factories = new HashMap<>();
		Scanner<INamedObjectFactory> scanner = new Scanner<>(INamedObjectFactory.class);

		for (Class<INamedObjectFactory> factoryClass : scanner.getClassesImplementing(gameOptions)) {
			for (Annotation a : factoryClass.getAnnotations()) {
				if (a instanceof FactoryType) {
					Factory factoryType = ((FactoryType)a).value();
					try {
						Constructor<INamedObjectFactory> constructor = factoryClass.getConstructor();
						INamedObjectFactory factory = constructor.newInstance();
						if (factoryType.context == context) {
							factories.put(factoryType, factory);
						}
					} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return factories;
	}
}
