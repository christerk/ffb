package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

public class DialogKickoffReturnParameter extends DialogWithoutParameter {

	public DialogKickoffReturnParameter() {
		super();
	}

	public DialogId getId() {
		return DialogId.KICKOFF_RETURN;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogKickoffReturnParameter();
	}

	// JSON serialization

	public DialogKickoffReturnParameter initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		return this;
	}

}
