package com.fumbbl.ffb.modifiers;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.skill.Skill;

public class StatBasedRollModifier extends RollModifier implements IJsonSerializable {

	private String name;

	private int value;

	public StatBasedRollModifier() {
	}

	public StatBasedRollModifier(String name, int value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	@Override
	public int getModifier() {
		return value;
	}

	@Override
	public boolean isModifierIncluded() {
		return false;
	}

	@Override
	public String getReportString() {
		return name;
	}

	@Override
	public ModifierType getType() {
		return ModifierType.STAT_BASED;
	}

	@Override
	public boolean appliesToContext(Skill skill, ModifierContext context) {
		return false;
	}

	@Override
	public StatBasedRollModifier initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);

		name = IJsonOption.NAME.getFrom(source, jsonObject);
		value = IJsonOption.VALUE.getFrom(source, jsonObject);

		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NAME.addTo(jsonObject, name);
		IJsonOption.VALUE.addTo(jsonObject, value);
		return jsonObject;
	}
}
