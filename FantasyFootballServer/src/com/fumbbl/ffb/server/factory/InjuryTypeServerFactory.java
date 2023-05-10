package com.fumbbl.ffb.server.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.factory.INamedObjectFactory;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InjuryTypeConstants;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeServer;
import com.fumbbl.ffb.util.Scanner;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.INJURY_TYPE_SERVER)
@RulesCollection(Rules.COMMON)
public class InjuryTypeServerFactory implements INamedObjectFactory {

	private final Map<String, InjuryType> injuryTypes;
	private final Map<Class<? extends InjuryType>, InjuryType> injuryTypeMap;

	private final Set<Class<InjuryTypeServer>> serverTypes = new HashSet<>();

	public InjuryTypeServerFactory() {
		injuryTypes = new HashMap<>();
		injuryTypeMap = new HashMap<>();

		try {
			Field[] fields = InjuryTypeConstants.class.getFields();
			for (Field field : fields) {
				int modifiers = field.getModifiers();
				if (Modifier.isStatic(modifiers) && InjuryType.class.isAssignableFrom(field.getType())) {
					addInjuryType((InjuryType) field.get(null));
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new FantasyFootballException("Could not initialize injury type factory", e);
		}
	}

	private void addInjuryType(InjuryType injuryType) {
		injuryTypes.put(injuryType.getName().toLowerCase(), injuryType);
		injuryTypeMap.put(injuryType.getClass(), injuryType);
	}

	public InjuryTypeServer<?> forName(String name) {
		if (name != null && injuryTypes.containsKey(name.toLowerCase())) {
			return serverType(injuryTypes.get(name.toLowerCase()));
		}

		throw new IllegalArgumentException("InjuryType '" + name + "' is unknown");
	}

	public InjuryTypeServer<?> forClass(Class<? extends InjuryType> c) {
		return serverType(injuryTypeMap.get(c));
	}

	private InjuryTypeServer<?> serverType(InjuryType injuryType) {
		//noinspection rawtypes
		Optional<Class<InjuryTypeServer>> typeClazz = serverTypes.stream()
			.filter(clazz -> ((ParameterizedType) clazz.getTypeParameters()[0]).getRawType()
				.equals(injuryType.getClass())).findFirst();

		if (typeClazz.isPresent()) {
			try {
				return typeClazz.get().getConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				throw new FantasyFootballException(e);
			}
		}

		return null;
	}

	@Override
	public void initialize(Game game) {
		serverTypes.addAll(new Scanner<>(InjuryTypeServer.class).getSubclasses());
	}
}
