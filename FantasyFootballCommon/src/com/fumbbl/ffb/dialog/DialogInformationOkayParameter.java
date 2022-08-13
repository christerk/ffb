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
	private boolean confirm;

	public DialogInformationOkayParameter() {
	}

	public DialogInformationOkayParameter(String title, String message, boolean confirm) {
		this(title, new String[]{message}, confirm);
	}

	public DialogInformationOkayParameter(String title, String[] messages, boolean confirm) {
		this.title = title;
		this.messages = messages;
		this.confirm = confirm;
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

	public boolean isConfirm() {
		return confirm;
	}
// transformation

	public IDialogParameter transform() {
		return new DialogInformationOkayParameter(title, messages, confirm);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.MESSAGE_ARRAY.addTo(jsonObject, messages);
		IJsonOption.TEXT.addTo(jsonObject, title);
		IJsonOption.CONFIRM.addTo(jsonObject, confirm);
		return jsonObject;
	}

	public DialogInformationOkayParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		title = IJsonOption.TEXT.getFrom(source, jsonObject);
		messages = IJsonOption.MESSAGE_ARRAY.getFrom(source, jsonObject);
		confirm = IJsonOption.CONFIRM.getFrom(source, jsonObject);
		return this;
	}

}
