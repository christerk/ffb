package com.fumbbl.ffb;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonSerializable;

/**
 * 
 * @author Kalimar
 */
public interface IDialogParameter extends IJsonSerializable {

	public DialogId getId();

	public IDialogParameter transform();

	// overrides IJsonSerializable
	public IDialogParameter initFrom(IFactorySource game, JsonValue pJsonValue);

	// overrides IJsonSerializable
	public JsonObject toJsonValue();

}
