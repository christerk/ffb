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
public class DialogCoinChoiceParameter extends DialogWithoutParameter {

	public DialogCoinChoiceParameter() {
		super();
	}

	public DialogId getId() {
		return DialogId.COIN_CHOICE;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogCoinChoiceParameter();
	}

	// JSON serialization

	public DialogCoinChoiceParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		return this;
	}

}
