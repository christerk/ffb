package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.bb2020.InjuryDescription;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DialogUseMortuaryAssistantsParameter implements IDialogParameter {

	private String teamId;
	private List<InjuryDescription> injuryDescriptions = new ArrayList<>();
	private int maxMortuaryAssistants;

	public DialogUseMortuaryAssistantsParameter() {
		super();
	}

	public DialogUseMortuaryAssistantsParameter(String teamId, List<InjuryDescription> injuryDescriptions, int maxMortuaryAssistants) {
		this.teamId = teamId;
		this.injuryDescriptions = injuryDescriptions;
		this.maxMortuaryAssistants = maxMortuaryAssistants;
	}

	public DialogId getId() {
		return DialogId.USE_MORTUARY_ASSISTANTS;
	}

	public List<InjuryDescription> getInjuryDescriptions() {
		return injuryDescriptions;
	}

	public String getTeamId() {
		return teamId;
	}

	public int getMaxMortuaryAssistants() {
		return maxMortuaryAssistants;
	}
	// transformation

	public IDialogParameter transform() {
		return new DialogUseMortuaryAssistantsParameter(teamId, injuryDescriptions, maxMortuaryAssistants);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		JsonArray jsonArray = new JsonArray();
		injuryDescriptions.stream().map(InjuryDescription::toJsonValue).forEach(jsonArray::add);
		IJsonOption.INJURY_DESCRIPTIONS.addTo(jsonObject, jsonArray);
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		IJsonOption.MAX_SELECTS.addTo(jsonObject, maxMortuaryAssistants);
		return jsonObject;
	}

	public DialogUseMortuaryAssistantsParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		JsonArray jsonArray = IJsonOption.INJURY_DESCRIPTIONS.getFrom(source, jsonObject);
		if (jsonArray != null) {
			injuryDescriptions.addAll(jsonArray.values().stream().map(value -> new InjuryDescription().initFrom(source, value)).collect(Collectors.toList()));
		}
		teamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		maxMortuaryAssistants = IJsonOption.MAX_SELECTS.getFrom(source, jsonObject);
		return this;
	}

}
