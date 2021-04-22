package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

public class DialogUseChainsawParameter implements IDialogParameter {

	private String teamId;

	public DialogUseChainsawParameter() {
	}

	public DialogUseChainsawParameter(String teamId) {
		this.teamId = teamId;
	}

	@Override
	public DialogId getId() {
		return DialogId.USE_CHAINSAW;
	}

	public String getTeamId() {
		return teamId;
	}

	@Override
	public IDialogParameter transform() {
		return new DialogUseChainsawParameter(teamId);
	}

	@Override
	public IDialogParameter initFrom(IFactorySource game, JsonValue jsonValue) {
		teamId = IJsonOption.TEAM_ID.getFrom(game, UtilJson.toJsonObject(jsonValue));
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		return jsonObject;
	}
}
