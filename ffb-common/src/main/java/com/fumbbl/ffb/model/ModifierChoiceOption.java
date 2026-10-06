package com.fumbbl.ffb.model;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * One combination of optional modifier skills that would make a roll succeed.
 */
public class ModifierChoiceOption implements IJsonSerializable {

	private final List<Skill> skills = new ArrayList<>();
	private int totalModifier;
	private int minimumRoll;
	private String label;

	public ModifierChoiceOption() {
	}

	public ModifierChoiceOption(List<Skill> skills, int totalModifier, int minimumRoll) {
		if (skills != null) {
			this.skills.addAll(skills);
		}
		this.totalModifier = totalModifier;
		this.minimumRoll = minimumRoll;
		this.label = this.skills.stream().map(Skill::getName).collect(Collectors.joining(" + "));
	}

	public List<Skill> getSkills() {
		return skills;
	}

	public int getTotalModifier() {
		return totalModifier;
	}

	/**
	 * @return the roll that would have been needed with the skills of this option applied
	 */
	public int getMinimumRoll() {
		return minimumRoll;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		JsonArray skillArray = new JsonArray();
		for (Skill skill : skills) {
			skillArray.add(UtilJson.toJsonValue(skill));
		}
		IJsonOption.SKILL_ARRAY.addTo(jsonObject, skillArray);
		IJsonOption.MODIFIER.addTo(jsonObject, totalModifier);
		IJsonOption.MINIMUM_ROLL.addTo(jsonObject, minimumRoll);
		IJsonOption.NAME.addTo(jsonObject, label);
		return jsonObject;
	}

	@Override
	public ModifierChoiceOption initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		skills.clear();
		JsonArray skillArray = IJsonOption.SKILL_ARRAY.getFrom(source, jsonObject);
		if (skillArray != null) {
			SkillFactory skillFactory = source.getFactory(FactoryType.Factory.SKILL);
			for (int i = 0; i < skillArray.size(); i++) {
				skills.add((Skill) UtilJson.toEnumWithName(skillFactory, skillArray.get(i)));
			}
		}
		totalModifier = IJsonOption.MODIFIER.getFrom(source, jsonObject);
		minimumRoll = IJsonOption.MINIMUM_ROLL.getFrom(source, jsonObject);
		label = IJsonOption.NAME.getFrom(source, jsonObject);
		return this;
	}
}
