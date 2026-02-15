package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

public class DialogPuntToCrowdParameter extends DialogWithoutParameter {

	public DialogPuntToCrowdParameter() {
		super();
	}

	public DialogId getId() {
		return DialogId.PUNT_TO_CROWD;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogPuntToCrowdParameter();
	}

	// JSON serialization

	public DialogPuntToCrowdParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		return this;
	}

}
