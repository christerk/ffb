package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class DialogSelectBlitzTargetParameter implements IDialogParameter {

	@Override
	public DialogId getId() {
		return DialogId.SELECT_BLITZ_TARGET;
	}

	public DialogSelectBlitzTargetParameter() {
	}

	@Override
	public IDialogParameter transform() {
		return new DialogSelectBlitzTargetParameter();
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
