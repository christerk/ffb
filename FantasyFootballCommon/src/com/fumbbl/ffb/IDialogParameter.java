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

	DialogId getId();

	IDialogParameter transform();

	// overrides IJsonSerializable
	IDialogParameter initFrom(IFactorySource source, JsonValue jsonValue);

	// overrides IJsonSerializable
	JsonObject toJsonValue();

}
