package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.SkillChoiceMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.ArrayList;
import java.util.List;

public class DialogSelectSkillParameter implements IDialogParameter {

	private final List<Skill> skills = new ArrayList<>();
	private String playerId;
	private SkillChoiceMode skillChoiceMode;

	public DialogSelectSkillParameter() {
	}

	public DialogSelectSkillParameter(String playerId, List<Skill> skills, SkillChoiceMode skillChoiceMode) {
		this.playerId = playerId;
		this.skills.addAll(skills);
		this.skillChoiceMode = skillChoiceMode;
	}

	@Override
	public DialogId getId() {
		return DialogId.SELECT_SKILL;
	}

	public String getPlayerId() {
		return playerId;
	}

	public List<Skill> getSkills() {
		return skills;
	}

	public SkillChoiceMode getSkillChoiceMode() {
		return skillChoiceMode;
	}

	@Override
	public IDialogParameter transform() {
		return new DialogSelectSkillParameter(playerId, skills, skillChoiceMode);
	}

	@Override
	public IDialogParameter initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		playerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		JsonArray skillArray = IJsonOption.SKILL_ARRAY.getFrom(game, jsonObject);
		for (int i = 0; i < skillArray.size(); i++) {
			SkillFactory skillFactory = game.getFactory(FactoryType.Factory.SKILL);
			skills.add((Skill) UtilJson.toEnumWithName(skillFactory, skillArray.get(i)));
		}

		skillChoiceMode = SkillChoiceMode.valueOf(IJsonOption.SKILL_CHOICE_MODE.getFrom(game, jsonObject));
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		JsonArray skillArray = new JsonArray();
		for (Skill skill : skills) {
			skillArray.add(UtilJson.toJsonValue(skill));
		}
		IJsonOption.SKILL_ARRAY.addTo(jsonObject, skillArray);
		IJsonOption.SKILL_CHOICE_MODE.addTo(jsonObject, skillChoiceMode.name());
		return jsonObject;
	}
}
