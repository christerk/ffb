package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * 
 * @author Kalimar
 */
public class DialogWinningsReRollParameter implements IDialogParameter {

	private String fTeamId;
	private int fOldRoll;

	public DialogWinningsReRollParameter() {
		super();
	}

	public DialogWinningsReRollParameter(String pTeamId, int pOldRoll) {
		fTeamId = pTeamId;
		fOldRoll = pOldRoll;
	}

	public DialogId getId() {
		return DialogId.WINNINGS_RE_ROLL;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public int getOldRoll() {
		return fOldRoll;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogWinningsReRollParameter(getTeamId(), getOldRoll());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
		IJsonOption.OLD_ROLL.addTo(jsonObject, fOldRoll);
		return jsonObject;
	}

	public DialogWinningsReRollParameter initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		fOldRoll = IJsonOption.OLD_ROLL.getFrom(game, jsonObject);
		return this;
	}

}
