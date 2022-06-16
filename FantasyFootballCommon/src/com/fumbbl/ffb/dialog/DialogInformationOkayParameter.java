package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

public class DialogInformationOkayParameter implements IDialogParameter {

	private String title;
	private String[] messages;

	public DialogInformationOkayParameter() {
	}

	public DialogInformationOkayParameter(String title, String message) {
		this(title, new String[]{message});
	}

	public DialogInformationOkayParameter(String title, String[] messages) {
		this.title = title;
		this.messages = messages;
	}

	public String getTitle() {
		return title;
	}

	public String[] getMessages() {
		return messages;
	}

	public DialogId getId() {
		return DialogId.INFORMATION_OKAY;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogInformationOkayParameter(title, messages);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.MESSAGE_ARRAY.addTo(jsonObject, messages);
		IJsonOption.TEXT.addTo(jsonObject, title);
		return jsonObject;
	}

	public DialogInformationOkayParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		title = IJsonOption.TEXT.getFrom(source, jsonObject);
		messages = IJsonOption.MESSAGE_ARRAY.getFrom(source, jsonObject);
		return this;
	}

}
