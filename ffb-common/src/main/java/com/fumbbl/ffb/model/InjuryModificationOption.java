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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * One option offered to a coach to modify an armour or injury roll: a single skill or a combination of modifier
 * skills that are applied together. The description only states visible results, e.g. "armour broken" or
 * "re-roll armour", never the hidden result of a re-roll.
 */
public class InjuryModificationOption implements IJsonSerializable {

	private final List<Skill> skills = new ArrayList<>();
	private final List<String> playerIds = new ArrayList<>();
	private String label;
	private String description;

	public InjuryModificationOption() {
	}

	public InjuryModificationOption(List<Skill> skills, List<String> playerIds, String description) {
		if (skills != null) {
			this.skills.addAll(skills);
		}
		if (playerIds != null) {
			this.playerIds.addAll(playerIds);
		}
		this.label = this.skills.stream().map(Skill::getName).collect(Collectors.joining(" + "));
		this.description = description;
	}

	public List<Skill> getSkills() {
		return skills;
	}

	/**
	 * @return the owner of each skill, same order as {@link #getSkills()}
	 */
	public List<String> getPlayerIds() {
		return playerIds;
	}

	public String getLabel() {
		return label;
	}

	public String getDescription() {
		return description;
	}

	public boolean hasSameSkills(Collection<Skill> otherSkills) {
		return otherSkills != null && otherSkills.size() == skills.size()
			&& new HashSet<>(otherSkills).equals(new HashSet<>(skills));
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		JsonArray skillArray = new JsonArray();
		for (Skill skill : skills) {
			skillArray.add(UtilJson.toJsonValue(skill));
		}
		IJsonOption.SKILL_ARRAY.addTo(jsonObject, skillArray);
		IJsonOption.PLAYER_IDS.addTo(jsonObject, playerIds);
		IJsonOption.NAME.addTo(jsonObject, label);
		IJsonOption.TEXT.addTo(jsonObject, description);
		return jsonObject;
	}

	@Override
	public InjuryModificationOption initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		skills.clear();
		JsonArray skillArray = IJsonOption.SKILL_ARRAY.getFrom(source, jsonObject);
		if (skillArray != null) {
			SkillFactory skillFactory = source.getFactory(FactoryType.Factory.SKILL);
			for (int i = 0; i < skillArray.size(); i++) {
				skills.add((Skill) UtilJson.toEnumWithName(skillFactory, skillArray.get(i)));
			}
		}
		playerIds.clear();
		String[] ids = IJsonOption.PLAYER_IDS.getFrom(source, jsonObject);
		if (ids != null) {
			playerIds.addAll(Arrays.asList(ids));
		}
		label = IJsonOption.NAME.getFrom(source, jsonObject);
		description = IJsonOption.TEXT.getFrom(source, jsonObject);
		return this;
	}
}
