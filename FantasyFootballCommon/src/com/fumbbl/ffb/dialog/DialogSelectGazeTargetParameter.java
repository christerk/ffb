package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

public class DialogSelectGazeTargetParameter implements IDialogParameter {

	@Override
	public DialogId getId() {
		return DialogId.SELECT_GAZE_TARGET;
	}

	public DialogSelectGazeTargetParameter() {
	}

	@Override
	public IDialogParameter transform() {
		return new DialogSelectGazeTargetParameter();
	}


	@Override
	public IDialogParameter initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		return jsonObject;
	}
}
