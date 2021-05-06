package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.kickoff.KickoffResult;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * 
 * @author Kalimar
 */
public class DialogKickoffResultParameter implements IDialogParameter {

	private KickoffResult fKickoffResult;

	public DialogKickoffResultParameter() {
		super();
	}

	public DialogKickoffResultParameter(KickoffResult pKickoffResult) {
		fKickoffResult = pKickoffResult;
	}

	public DialogId getId() {
		return DialogId.KICKOFF_RESULT;
	}

	public KickoffResult getKickoffResult() {
		return fKickoffResult;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogKickoffResultParameter(getKickoffResult());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.KICKOFF_RESULT.addTo(jsonObject, fKickoffResult);
		return jsonObject;
	}

	public DialogKickoffResultParameter initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		fKickoffResult = (KickoffResult) IJsonOption.KICKOFF_RESULT.getFrom(game, jsonObject);
		return this;
	}

}
