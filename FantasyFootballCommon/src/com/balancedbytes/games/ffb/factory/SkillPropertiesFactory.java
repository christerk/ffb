package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.property.ISkillProperty;
import com.balancedbytes.games.ffb.model.property.NamedProperties;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@FactoryType(FactoryType.Factory.SKILL_PROPERTIES)
@RulesCollection(RulesCollection.Rules.COMMON)
public class SkillPropertiesFactory implements INamedObjectFactory<ISkillProperty> {
	Map<String, ISkillProperty> properties = new HashMap<>();

	@Override
	public ISkillProperty forName(String name) {
		return properties.get(name);
	}

	@Override
	public void initialize(Game game) {
		Arrays.stream(NamedProperties.class.getDeclaredFields())
			.filter(field ->
				Modifier.isStatic(field.getModifiers())
					&& field.getType() == ISkillProperty.class)
			.forEach(field -> {
				ISkillProperty property;
				try {
					property = (ISkillProperty) field.get(null);
				} catch (IllegalAccessException e) {
					throw new FantasyFootballException("Could not initialize NamedPropertiesFactory for field: " + field.getName());
				}
				properties.put(property.getName(), property);
			});
	}
}
