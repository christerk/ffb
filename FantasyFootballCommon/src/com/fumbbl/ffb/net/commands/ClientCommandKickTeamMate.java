package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandKickTeamMate extends ClientCommand implements ICommandWithActingPlayer {

	private String fKickedPlayerId;
	private String fActingPlayerId;
	private int fNumDice;

	public ClientCommandKickTeamMate() {
		super();
	}

	public ClientCommandKickTeamMate(String pActingPlayerId, String pKickedPlayerId, int pNumDice) {
		fActingPlayerId = pActingPlayerId;
		fKickedPlayerId = pKickedPlayerId;
		fNumDice = pNumDice;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_KICK_TEAM_MATE;
	}

	public String getActingPlayerId() {
		return fActingPlayerId;
	}

	public String getKickedPlayerId() {
		return fKickedPlayerId;
	}

	public int getNumDice() {
		return fNumDice;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.ACTING_PLAYER_ID.addTo(jsonObject, fActingPlayerId);
		IJsonOption.KICKED_PLAYER_ID.addTo(jsonObject, fKickedPlayerId);
		IJsonOption.NR_OF_DICE.addTo(jsonObject, fNumDice);
		return jsonObject;
	}

	public ClientCommandKickTeamMate initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fActingPlayerId = IJsonOption.ACTING_PLAYER_ID.getFrom(source, jsonObject);
		fKickedPlayerId = IJsonOption.KICKED_PLAYER_ID.getFrom(source, jsonObject);
		fNumDice = IJsonOption.NR_OF_DICE.getFrom(source, jsonObject);
		return this;
	}

}
