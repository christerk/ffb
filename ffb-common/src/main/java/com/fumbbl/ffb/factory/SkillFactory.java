package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.ISkillBehaviour;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.PlayerStatKey;
import com.fumbbl.ffb.util.Scanner;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.SKILL)
@RulesCollection(Rules.COMMON)
public class SkillFactory implements INamedObjectFactory<Skill> {
	private final Map<String, Skill> skills;
	private final Map<Class<? extends Skill>, Skill> skillMap;

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

	public Skill forStatKey(PlayerStatKey key) {
		return forName("+" + key.name());
	}

	public Skill forClass(Class<? extends Skill> c) {
		return skillMap.get(c);
	}

	public Collection<ISkillBehaviour<? extends Skill>> getBehaviours() {
		Collection<ISkillBehaviour<? extends Skill>> result = new HashSet<>();
		skills.values().forEach(s -> {
			ISkillBehaviour<? extends Skill> behaviour = s.getSkillBehaviour();
			if (behaviour != null) {
				result.add(behaviour);
			}
		});
		return result;
	}

	@Override
	public void initialize(Game game) {
		Scanner<Skill> scanner = new Scanner<>(Skill.class);

		scanner.getSubclasses(game.getOptions()).forEach(this::addSkill);
		skills.values().forEach(Skill::postConstruct);
	}
}
