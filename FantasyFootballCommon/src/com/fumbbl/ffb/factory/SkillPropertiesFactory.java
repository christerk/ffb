package com.fumbbl.ffb.factory;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;

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
