package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.bb2020.InjuryDescription;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DialogUseApothecariesParameter implements IDialogParameter {

	private String teamId;
	private List<InjuryDescription> injuryDescriptions = new ArrayList<>();
	private int maxApos;

	public DialogUseApothecariesParameter() {
		super();
	}

	public DialogUseApothecariesParameter(String teamId, List<InjuryDescription> injuryDescriptions, int maxApos) {
		this.teamId = teamId;
		this.injuryDescriptions = injuryDescriptions;
		this.maxApos = maxApos;
	}

	public DialogId getId() {
		return DialogId.USE_APOTHECARIES;
	}

	public List<InjuryDescription> getInjuryDescriptions() {
		return injuryDescriptions;
	}

	public String getTeamId() {
		return teamId;
	}

	public int getMaxApos() {
		return maxApos;
	}
	// transformation

	public IDialogParameter transform() {
		return new DialogUseApothecariesParameter(teamId, injuryDescriptions, maxApos);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		JsonArray jsonArray = new JsonArray();
		injuryDescriptions.stream().map(InjuryDescription::toJsonValue).forEach(jsonArray::add);
		IJsonOption.INJURY_DESCRIPTIONS.addTo(jsonObject, jsonArray);
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		IJsonOption.MAX_SELECTS.addTo(jsonObject, maxApos);
		return jsonObject;
	}

	public DialogUseApothecariesParameter initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		JsonArray jsonArray = IJsonOption.INJURY_DESCRIPTIONS.getFrom(game, jsonObject);
		if (jsonArray != null) {
			injuryDescriptions.addAll(jsonArray.values().stream().map(value -> new InjuryDescription().initFrom(game, value)).collect(Collectors.toList()));
		}
		teamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		maxApos = IJsonOption.MAX_SELECTS.getFrom(game, jsonObject);
		return this;
	}

}
