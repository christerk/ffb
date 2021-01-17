package com.balancedbytes.games.ffb.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.IKeyedItem;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.GameOptions;

public class Scanner<T extends IKeyedItem> {
	private RawScanner<T> rawScanner;


	public Scanner(Class<T> cls) {
		rawScanner = new RawScanner<T>(cls);
	}

	public Collection<T> getSubclasses(GameOptions options) {
		Set<Class<T>> classes = rawScanner.getSubclasses();

		return filterClasses(options, classes);
	}

	public Collection<T> getClassesImplementing(GameOptions options) {
		Set<Class<T>> classes = rawScanner.getClassesImplementing();

		return filterClasses(options, classes);
	}

	private Collection<T> filterClasses(GameOptions options, Set<Class<T>> classes) {
		Map<Object,T> result = new HashMap<>();
		for (Class<T> cls : classes) {
			boolean hasRulesAnnotation = false;
			for (Annotation a : cls.getAnnotations()) {
				if (a instanceof RulesCollection) {
					hasRulesAnnotation = true;
					Rules rule = ((RulesCollection)a).value();
					if (rule.matches(options.getRulesVersion())) {

						try {
						Constructor ctr = cls.getConstructor();
						@SuppressWarnings("unchecked")
						T instance = (T)ctr.newInstance();
						Object key = instance.getKey();

						if (result.containsKey(key)) {
							throw new FantasyFootballException("Duplicate implementation found when scanning.");
						}

						result.put(instance.getKey(), instance);
						} catch (NoSuchMethodException|InstantiationException|IllegalAccessException|InvocationTargetException e) {
							throw new FantasyFootballException("Error initializing scanned class.", e);
						}
					}
				}
			}
			if (!hasRulesAnnotation) {
				throw new FantasyFootballException("Missing annotations on scanned class "+cls.getName()+".");
			}
		}
		return result.values();
	}

	public Set<Class<T>> getClassesImplementing() {
		Set<Class<T>> result = new HashSet<>();
		for (Class<T> cls : rawScanner.getClassesImplementing()) {
			for (Annotation a : cls.getAnnotations()) {
				if (a instanceof RulesCollection) {
					Rules rule = ((RulesCollection)a).value();
					if (rule.matches(rule)) {
						result.add(cls);
					}
				}
			}
		}
		return result;

	}

}
