package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.json.IJsonOption;

/**
 * 
 * @author Kalimar
 */
public abstract class DialogWithoutParameter implements IDialogParameter {

	public DialogWithoutParameter() {
		super();
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		return jsonObject;
	}

}
