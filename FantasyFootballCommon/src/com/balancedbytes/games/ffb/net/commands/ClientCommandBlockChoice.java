package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandBlockChoice extends ClientCommand {

	private int fDiceIndex;

	public ClientCommandBlockChoice() {
		super();
	}

	public ClientCommandBlockChoice(int pDiceIndex) {
		fDiceIndex = pDiceIndex;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_BLOCK_CHOICE;
	}

	public int getDiceIndex() {
		return fDiceIndex;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.DICE_INDEX.addTo(jsonObject, fDiceIndex);
		return jsonObject;
	}

	public ClientCommandBlockChoice initFrom(Game game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fDiceIndex = IJsonOption.DICE_INDEX.getFrom(game, jsonObject);
		return this;
	}

}
