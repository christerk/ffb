package com.fumbbl.ffb.server.mechanic;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.ReRollPropertyFactory;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.IServerJsonOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReRollOptions implements IJsonSerializable {
	private List<ReRollProperty> properties;
	private Skill reRollSkill;
	private boolean canActuallyReRoll;

	public ReRollOptions() {
	}

	public ReRollOptions(List<ReRollProperty> properties, Skill reRollSkill) {
		this.properties = properties;
		this.reRollSkill = reRollSkill;
		this.canActuallyReRoll = properties.stream().anyMatch(ReRollProperty::isActualReRoll) || reRollSkill != null;
	}

	public List<ReRollProperty> getProperties() {
		return properties;
	}

	public Skill getReRollSkill() {
		return reRollSkill;
	}

	public boolean canActuallyReRoll() {
		return canActuallyReRoll;
	}

	@Override
	public ReRollOptions initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		ReRollPropertyFactory factory = source.getFactory(FactoryType.Factory.RE_ROLL_PROPERTY);
		properties = new ArrayList<>();
		String[] propertyNames = IJsonOption.RE_ROLL_PROPERTIES.getFrom(source, jsonObject);
		if (propertyNames != null) {
			properties.addAll(
				Arrays.stream(propertyNames).map(factory::forName).collect(Collectors.toList())
			);
		}
		reRollSkill = (Skill) IServerJsonOption.RE_ROLL_SKILL.getFrom(source, jsonObject);
		canActuallyReRoll = properties.stream().anyMatch(ReRollProperty::isActualReRoll) || reRollSkill != null;
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		if (properties != null) {
			List<String> propertyNames = properties.stream().map(ReRollProperty::getName).collect(Collectors.toList());
			IJsonOption.RE_ROLL_PROPERTIES.addTo(jsonObject, propertyNames);
		}
		IServerJsonOption.RE_ROLL_SKILL.addTo(jsonObject, reRollSkill);
		return jsonObject;
	}
}
