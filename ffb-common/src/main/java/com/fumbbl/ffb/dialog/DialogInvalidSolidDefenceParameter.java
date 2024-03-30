package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

public class DialogInvalidSolidDefenceParameter implements IDialogParameter {

	private String teamId;
	private int amount, limit;

	public DialogInvalidSolidDefenceParameter() {
	}

	public DialogInvalidSolidDefenceParameter(String teamId, int amount, int limit) {
		this.teamId = teamId;
		this.amount = amount;
		this.limit = limit;
	}

	public String getTeamId() {
		return teamId;
	}

	public int getAmount() {
		return amount;
	}

	public int getLimit() {
		return limit;
	}

	@Override
	public DialogId getId() {
		return DialogId.INVALID_SOLID_DEFENCE;
	}

	@Override
	public IDialogParameter transform() {
		return new DialogInvalidSolidDefenceParameter(teamId, amount, limit);
	}

	@Override
	public IDialogParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		teamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		amount = IJsonOption.NR_OF_PLAYERS.getFrom(source, jsonObject);
		limit = IJsonOption.NR_OF_PLAYERS_ALLOWED.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		IJsonOption.NR_OF_PLAYERS.addTo(jsonObject, amount);
		IJsonOption.NR_OF_PLAYERS_ALLOWED.addTo(jsonObject, limit);
		return jsonObject;
	}
}
