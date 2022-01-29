package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * @author Kalimar
 */
public class DialogConfirmEndBlitzActionParameter implements IDialogParameter {

	private String fTeamId;

	public DialogConfirmEndBlitzActionParameter() {
	}

	public DialogConfirmEndBlitzActionParameter(String teamId) {
		this();
		setTeamId(teamId);
	}

	public DialogId getId() {
		return DialogId.CONFIRM_END_BLITZ_ACTION;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public void setTeamId(String teamId) {
		fTeamId = teamId;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogConfirmEndBlitzActionParameter(getTeamId());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, getTeamId());
		return jsonObject;
	}

	public DialogConfirmEndBlitzActionParameter initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		setTeamId(IJsonOption.TEAM_ID.getFrom(game, jsonObject));
		return this;
	}

}
