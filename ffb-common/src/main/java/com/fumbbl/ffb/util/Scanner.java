package com.fumbbl.ffb.util;

import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.IKeyedItem;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.GameOptions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Scanner<T extends IKeyedItem> {
	private final RawScanner<T> rawScanner;

	private final Function<Class<T>, T> defaultGenerator = (clazz) -> {
		try {
			Constructor<?> constructor = clazz.getConstructor();
			//noinspection unchecked
			return (T) constructor.newInstance();
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException |
						 IllegalArgumentException
						 | InvocationTargetException e) {
			throw new FantasyFootballException("Error creating instance of class " + clazz, e);
		}
	};

	public Scanner(Class<T> cls) {
		rawScanner = new RawScanner<>(cls);
	}

	/**
	 * Return class objects of all classes implementing interface T.
	 */
	public Set<Class<T>> getClassesImplementing() {
		Set<Class<T>> result = new HashSet<>();
		for (Class<T> cls : rawScanner.getClassesImplementing()) {
			for (Annotation a : cls.getAnnotations()) {
				if (a instanceof RulesCollection) {
					result.add(cls);
				}
			}
		}
		return result;

	}

	/**
	 * Return class objects of all classes extending T.
	 */
	public Set<Class<T>> getSubclasses() {
		return rawScanner.getSubclasses();
	}

	/**
	 * Return all values of all enums implementing interface T valid for the rules version in options.
	 */
	public Set<T> getEnumValues(GameOptions options) {
		Set<Class<T>> classes = filterClassesForRulesVersion(options, rawScanner.getClassesImplementing());

		Set<T> result = new HashSet<>();
		for (Class<T> cls : classes) {
			if (cls.isEnum()) {
				result.addAll(Arrays.asList(cls.getEnumConstants()));
			}
		}
		return result;
	}

	/**
	 * Return instances of all classes extending T.
	 */
	public Set<T> getSubclassInstances() {
		Set<Class<T>> classes = rawScanner.getSubclasses();
		Map<Object, T> result = new HashMap<>();
		for (Class<T> cls : classes) {
			addInstance(defaultGenerator.apply(cls), result);
		}
		return new HashSet<>(result.values());
	}

	/**
	 * Return instances of all classes extending T valid for the rules version in options.
	 */
	public Collection<T> getSubclassInstances(GameOptions options) {
		Set<Class<T>> classes = rawScanner.getSubclasses();

		return collectInstancesForRuleVersion(options, classes, defaultGenerator);
	}

	/**
	 * Return instances of all classes implementing interface T.
	 */
	public Collection<T> getInstancesImplementing() {
		Set<Class<T>> classes = rawScanner.getClassesImplementing();

		return collectInstances(classes);
	}

	/**
	 * Return instances of all classes implementing interface T valid for the rules version in options.
	 */
	public Collection<T> getInstancesImplementing(GameOptions options) {
		return getInstancesImplementing(options, defaultGenerator);
	}

	/**
	 * Return instances of all classes implementing interface T valid for the rules version in options.
	 * @param instanceGenerator generator to invoke constructors with arguments
	 */
	public Collection<T> getInstancesImplementing(GameOptions options, Function<Class<T>, T> instanceGenerator) {
		Set<Class<T>> classes = rawScanner.getClassesImplementing();
		return collectInstancesForRuleVersion(options, classes, instanceGenerator);
	}

	private Collection<T> collectInstancesForRuleVersion(GameOptions options, Set<Class<T>> classes, Function<Class<T>, T> instanceGenerator) {
		Map<Object, T> result = new HashMap<>();
		Set<Class<T>> foundClasses = filterClassesForRulesVersion(options, classes);

		getInstancesForMostRecentRules(foundClasses, instanceGenerator).forEach(cls -> addInstance(cls, result));

		return result.values();
	}

	private Set<Class<T>> filterClassesForRulesVersion(GameOptions options, Set<Class<T>> classes) {
		Set<Class<T>> foundClasses = new HashSet<>();
		for (Class<T> cls : classes) {
			boolean hasRulesAnnotation = false;
			for (Annotation a : cls.getAnnotations()) {
				if (a instanceof RulesCollection) {
					hasRulesAnnotation = true;
					Rules rule = ((RulesCollection) a).value();
					if (options.getRulesVersion().isOrExtends(rule)) {
						foundClasses.add(cls);
					}
				}
			}
			if (!hasRulesAnnotation) {
				throw new FantasyFootballException("Missing annotations on scanned class " + cls.getName() + ".");
			}
		}
		return foundClasses;
	}

	private Set<T> getInstancesForMostRecentRules(Set<Class<T>> unfiltered, Function<Class<T>, T> instanceGenerator) {
		return unfiltered.stream().map(instanceGenerator)
			.collect(Collectors.groupingBy(IKeyedItem::getKey)).values().stream().map(classGroup -> classGroup.stream()
				.max(Comparator.comparing(instance ->
					instance.getClass().getAnnotation(RulesCollection.class).value().getHierarchyLevel())
				).orElseThrow(() -> new FantasyFootballException("No classes found in group.")))
			.collect(Collectors.toSet());
	}

	private Collection<T> collectInstances(Set<Class<T>> classes) {
		Map<Object, T> result = new HashMap<>();
		for (Class<T> cls : classes) {
			addInstance(defaultGenerator.apply(cls), result);
		}
		return result.values();
	}

	private void addInstance(T instance, Map<Object, T> result) {
		Object key = instance.getKey();
		if (result.containsKey(key)) {
			System.out.println(instance.getClass().getCanonicalName());
			System.out.println(result.get(key).getClass().getCanonicalName());
			throw new FantasyFootballException("Duplicate implementation found when scanning: " + key);
		}
		result.put(instance.getKey(), instance);
	}
}
