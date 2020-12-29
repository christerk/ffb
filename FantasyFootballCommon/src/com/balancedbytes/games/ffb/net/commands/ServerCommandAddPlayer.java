package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.model.RosterPlayer;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandAddPlayer extends ServerCommand {

	private String fTeamId;
	private RosterPlayer fPlayer;
	private PlayerState fPlayerState;
	private SendToBoxReason fSendToBoxReason;
	private int fSendToBoxTurn;
	private int fSendToBoxHalf;

	public ServerCommandAddPlayer() {
		super();
	}

	public ServerCommandAddPlayer(String pTeamId, RosterPlayer pPlayer, PlayerState pPlayerState,
			PlayerResult pPlayerResult) {
		this();
		if (pPlayer == null) {
			throw new IllegalArgumentException("Parameter player must not be null.");
		}
		fTeamId = pTeamId;
		fPlayer = pPlayer;
		fPlayerState = pPlayerState;
		if (pPlayerResult != null) {
			fSendToBoxReason = pPlayerResult.getSendToBoxReason();
			fSendToBoxTurn = pPlayerResult.getSendToBoxTurn();
			fSendToBoxHalf = pPlayerResult.getSendToBoxHalf();
		}
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_ADD_PLAYER;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public RosterPlayer getPlayer() {
		return fPlayer;
	}

	public PlayerState getPlayerState() {
		return fPlayerState;
	}

	public SendToBoxReason getSendToBoxReason() {
		return fSendToBoxReason;
	}

	public int getSendToBoxHalf() {
		return fSendToBoxHalf;
	}

	public int getSendToBoxTurn() {
		return fSendToBoxTurn;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
		IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
		if (fPlayer != null) {
			IJsonOption.PLAYER.addTo(jsonObject, fPlayer.toJsonValue());
		}
		IJsonOption.PLAYER_STATE.addTo(jsonObject, fPlayerState);
		IJsonOption.SEND_TO_BOX_REASON.addTo(jsonObject, fSendToBoxReason);
		IJsonOption.SEND_TO_BOX_TURN.addTo(jsonObject, fSendToBoxTurn);
		IJsonOption.SEND_TO_BOX_HALF.addTo(jsonObject, fSendToBoxHalf);
		return jsonObject;
	}

	public ServerCommandAddPlayer initFrom(Game game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(game, jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(game, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		JsonObject playerObject = IJsonOption.PLAYER.getFrom(game, jsonObject);
		if (playerObject != null) {
			fPlayer = new RosterPlayer().initFrom(game, playerObject);
		}
		fPlayerState = IJsonOption.PLAYER_STATE.getFrom(game, jsonObject);
		fSendToBoxReason = (SendToBoxReason) IJsonOption.SEND_TO_BOX_REASON.getFrom(game, jsonObject);
		fSendToBoxTurn = IJsonOption.SEND_TO_BOX_TURN.getFrom(game, jsonObject);
		fSendToBoxHalf = IJsonOption.SEND_TO_BOX_HALF.getFrom(game, jsonObject);
		return this;
	}

}
