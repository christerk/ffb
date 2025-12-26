package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

public class DialogPickUpChoiceParameter extends DialogWithoutParameter {

	public DialogPickUpChoiceParameter() {
		super();
	}

	public DialogId getId() {
		return DialogId.PICK_UP_CHOICE;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogPickUpChoiceParameter();
	}

	// JSON serialization

	public DialogPickUpChoiceParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		return this;
	}

}
