package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InjuryTypeConstants;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.INJURY_TYPE)
@RulesCollection(Rules.COMMON)
public class InjuryTypeFactory implements INamedObjectFactory {

	private final Map<String, InjuryType> injuryTypes;
	private final Map<Class<? extends InjuryType>, InjuryType> injuryTypeMap;

	public InjuryTypeFactory() {
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

	public Collection<InjuryType> getInjuryTypes() {
		return injuryTypes.values();
	}

	private void addInjuryType(InjuryType injuryType) {
		injuryTypes.put(injuryType.getName().toLowerCase(), injuryType);
		injuryTypeMap.put(injuryType.getClass(), injuryType);
	}

	public InjuryType forName(String name) {
		if (name != null && injuryTypes.containsKey(name.toLowerCase())) {
			return injuryTypes.get(name.toLowerCase());
		}

		throw new IllegalArgumentException("InjuryType '" + name + "' is unknown");
	}

	public InjuryType forClass(Class<? extends InjuryType> c) {
		return injuryTypeMap.get(c);
	}

	@Override
	public void initialize(Game game) {
	}
}
