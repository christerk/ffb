package com.fumbbl.ffb;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.FactoryType.FactoryContext;
import com.fumbbl.ffb.factory.ILoggingFacade;
import com.fumbbl.ffb.factory.INamedObjectFactory;
import com.fumbbl.ffb.model.GameOptions;
import com.fumbbl.ffb.util.Scanner;

@SuppressWarnings("rawtypes")
public class FactoryManager {
	public FactoryManager() {

	}

	public Map<Factory, INamedObjectFactory> getFactoriesForContext(FactoryContext context, ILoggingFacade logger) {
		Map<Factory, INamedObjectFactory> factories = new HashMap<>();
		Scanner<INamedObjectFactory> scanner = new Scanner<>(INamedObjectFactory.class);

		for (Class<INamedObjectFactory> factoryClass : scanner.getClassesImplementing()) {
			for (Annotation a : factoryClass.getAnnotations()) {
				if (a instanceof FactoryType) {
					Factory factoryType = ((FactoryType) a).value();
					try {
						Constructor<INamedObjectFactory> constructor = factoryClass.getConstructor();
						INamedObjectFactory factory = constructor.newInstance();
						if (factoryType.context == context) {
							factories.put(factoryType, factory);
						}
					} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException |
									 IllegalArgumentException
									 | InvocationTargetException e) {
						logger.logWithOutGameId(e);
					}
				}
			}
		}

		return factories;
	}

	public Map<Factory, INamedObjectFactory<?>> getFactoriesForContext(FactoryContext context, GameOptions gameOptions) {
		Map<Factory, INamedObjectFactory<?>> factories = new HashMap<>();
		Scanner<INamedObjectFactory> scanner = new Scanner<>(INamedObjectFactory.class);

		for (INamedObjectFactory factory : scanner.getInstancesImplementing(gameOptions)) {
			for (Annotation a : factory.getClass().getAnnotations()) {
				if (a instanceof FactoryType) {
					Factory factoryType = ((FactoryType) a).value();
					if (factoryType.context == context) {
						factories.put(factoryType, factory);
					}
				}
			}
		}

		return factories;
	}
}
