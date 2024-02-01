package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

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
	public IDialogParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		return jsonObject;
	}
}
