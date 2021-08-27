package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandActingPlayer extends ClientCommand {

	private String fPlayerId;
	private PlayerAction fPlayerAction;
	private boolean jumping;

	public ClientCommandActingPlayer() {
		super();
	}

	public ClientCommandActingPlayer(String pPlayerId, PlayerAction pPlayerAction, boolean jumping) {
		fPlayerId = pPlayerId;
		fPlayerAction = pPlayerAction;
		this.jumping = jumping;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_ACTING_PLAYER;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public PlayerAction getPlayerAction() {
		return fPlayerAction;
	}

	public boolean isJumping() {
		return jumping;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.PLAYER_ACTION.addTo(jsonObject, fPlayerAction);
		IJsonOption.JUMPING.addTo(jsonObject, jumping);
		return jsonObject;
	}

	public ClientCommandActingPlayer initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		fPlayerAction = (PlayerAction) IJsonOption.PLAYER_ACTION.getFrom(game, jsonObject);
		jumping = IJsonOption.JUMPING.getFrom(game, jsonObject);
		return this;
	}

}
