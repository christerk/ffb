package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SeriousInjury;
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
public class ClientCommandApothecaryChoice extends ClientCommand {

	private String fPlayerId;
	private PlayerState fPlayerState;
	private SeriousInjury fSeriousInjury;

	public ClientCommandApothecaryChoice() {
		super();
	}

	public ClientCommandApothecaryChoice(String pPlayerId, PlayerState pPlayerState, SeriousInjury pSeriousInjury) {
		fPlayerId = pPlayerId;
		fPlayerState = pPlayerState;
		fSeriousInjury = pSeriousInjury;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_APOTHECARY_CHOICE;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public PlayerState getPlayerState() {
		return fPlayerState;
	}

	public SeriousInjury getSeriousInjury() {
		return fSeriousInjury;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.PLAYER_STATE.addTo(jsonObject, fPlayerState);
		IJsonOption.SERIOUS_INJURY.addTo(jsonObject, fSeriousInjury);
		return jsonObject;
	}

	public ClientCommandApothecaryChoice initFrom(Game game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		fPlayerState = IJsonOption.PLAYER_STATE.getFrom(game, jsonObject);
		fSeriousInjury = (SeriousInjury) IJsonOption.SERIOUS_INJURY.getFrom(game, jsonObject);
		return this;
	}

}
