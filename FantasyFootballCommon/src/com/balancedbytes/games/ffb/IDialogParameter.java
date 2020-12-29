package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.model.Game;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public interface IDialogParameter extends IJsonSerializable {

	public DialogId getId();

	public IDialogParameter transform();

	// overrides IJsonSerializable
	public IDialogParameter initFrom(Game game, JsonValue pJsonValue);

	// overrides IJsonSerializable
	public JsonObject toJsonValue();

}
