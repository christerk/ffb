package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class DialogPassBlockParameter extends DialogWithoutParameter {

	public DialogPassBlockParameter() {
		super();
	}

	public DialogId getId() {
		return DialogId.PASS_BLOCK;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogPassBlockParameter();
	}

	// JSON serialization

	public DialogPassBlockParameter initFrom(JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
		return this;
	}

}
