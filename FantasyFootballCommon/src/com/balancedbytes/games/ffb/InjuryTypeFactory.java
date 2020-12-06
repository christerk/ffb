package com.balancedbytes.games.ffb;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Hashtable;

import com.balancedbytes.games.ffb.model.InjuryTypeConstants;

/**
 * 
 * @author Kalimar
 */
public class InjuryTypeFactory implements INamedObjectFactory {

	private Hashtable<String, InjuryType> injuryTypes;
	private Hashtable<Class<? extends InjuryType>, InjuryType> injuryTypeMap;

	public InjuryTypeFactory() {
		injuryTypes = new Hashtable<String, InjuryType>();
		injuryTypeMap = new Hashtable<Class<? extends InjuryType>, InjuryType>();

		try {
			Field[] fields = InjuryTypeConstants.class.getFields();
			for (Field field : fields) {
				int modifiers = field.getModifiers();
				if (Modifier.isStatic(modifiers) && InjuryType.class.isAssignableFrom(field.getType())) {
					addInjuryType((InjuryType) field.get(null));
				}
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		if (injuryTypes.containsKey(name)) {
			return injuryTypes.get(name.toLowerCase());
		}

		return null;
	}

	public InjuryType forClass(Class<? extends InjuryType> c) {
		return injuryTypeMap.get(c);
	}
}
