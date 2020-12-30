package com.balancedbytes.games.ffb.factory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.util.Scanner;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.SKILL)
@RulesCollection(Rules.COMMON)
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
	public void initialize(GameOptions options) {
		Scanner<Skill> scanner = new Scanner<Skill>(Skill.class);
		
		scanner.getSubclasses(options).forEach(s -> addSkill(s));
		skills.values().forEach(s -> s.postConstruct());
	}
}
