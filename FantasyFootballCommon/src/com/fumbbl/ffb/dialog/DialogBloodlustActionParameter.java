package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;


public class DialogBloodlustActionParameter extends DialogWithoutParameter {

	private boolean changeToMove;

	public DialogBloodlustActionParameter(boolean changeToMove) {
		super();
		this.changeToMove = changeToMove;
	}

	public DialogBloodlustActionParameter() {
		super();
	}

	public DialogId getId() {
		return DialogId.BLOODLUST_ACTION;
	}

	public boolean isChangeToMove() {
		return changeToMove;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogBloodlustActionParameter(changeToMove);
	}

	// JSON serialization

	public DialogBloodlustActionParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		changeToMove = IJsonOption.CHANGE_TO_MOVE.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.CHANGE_TO_MOVE.addTo(jsonObject, changeToMove);
		return jsonObject;
	}
}
