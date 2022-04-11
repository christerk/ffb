package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

public class DialogBriberyAndCorruptionParameter implements IDialogParameter {

	private String teamId;

	public DialogBriberyAndCorruptionParameter() {
	}

	public DialogBriberyAndCorruptionParameter(String teamId) {
		this.teamId = teamId;
	}

	@Override
	public DialogId getId() {
		return DialogId.BRIBERY_AND_CORRUPTION_RE_ROLL;
	}

	public String getTeamId() {
		return teamId;
	}

	@Override
	public IDialogParameter transform() {
		return new DialogBriberyAndCorruptionParameter(teamId);
	}

	@Override
	public IDialogParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		teamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		return jsonObject;
	}
}
