package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

public class DialogKickOffResultParameter implements IDialogParameter {

	private String fTeamId;

	public DialogKickOffResultParameter() {
	}

	public DialogKickOffResultParameter(String pTeamId) {
		fTeamId = pTeamId;
	}

	public DialogId getId() {
		return DialogId.KICK_OFF_RESULT;
	}

	public String getTeamId() {
		return fTeamId;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogKickOffResultParameter(getTeamId());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
		return jsonObject;
	}

	public DialogKickOffResultParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		return this;
	}

}
