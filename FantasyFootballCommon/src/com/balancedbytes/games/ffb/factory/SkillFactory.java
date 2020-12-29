package com.balancedbytes.games.ffb.factory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.FactoryType.Factory;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.util.Scanner;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.skill)
public class SkillFactory implements INamedObjectFactory {
	private Map<String, Skill> skills;
	private Map<Class<? extends Skill>, Skill> skillMap;

	public SkillFactory() {
		skills = new HashMap<>();
		skillMap = new HashMap<>();
	}

	public Collection<Skill> getSkills() {
		return skills.values();
	}

	private void addSkill(Skill skill) {
		skills.put(skill.getName().toLowerCase(), skill);
		skillMap.put(skill.getClass(), skill);
	}

	public Skill forName(String name) {
		name = name.toLowerCase();
		if (skills.containsKey(name)) {
			return skills.get(name);
		}

		if ("Ball & Chain".equalsIgnoreCase(name) || "Ball &amp; Chain".equalsIgnoreCase(name)) {
			return skills.get("ball and chain");
		}
		return null;
	}

	public Skill forClass(Class<? extends Skill> c) {
		return skillMap.get(c);
	}

	@Override
	public void initialize(Rules rules, GameOptions options) {
		Scanner<Skill> scanner = new Scanner<Skill>(Skill.class);
		
		for (Class<Skill> skillClass : scanner.getSubclasses()) {
			for (Annotation a : skillClass.getAnnotations()) {
				if (a instanceof RulesCollection) {
					Rules skillRule = ((RulesCollection)a).value();
					if (skillRule.matches(rules)) {
						addSkill(skillClass);
					}
				}
			}
		}
		
		for (Skill skill: skills.values()) {
			skill.postConstruct();
		}
		
	}

	private void addSkill(Class<Skill> skillClass) {
		try {
			Constructor<Skill> constructor = skillClass.getConstructor();
			Skill skill = constructor.newInstance();
			addSkill(skill);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
