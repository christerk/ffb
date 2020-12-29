package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.FieldCoordinate;
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
public class ClientCommandKickoff extends ClientCommand {

	private FieldCoordinate fBallCoordinate;

	public ClientCommandKickoff() {
		super();
	}

	public ClientCommandKickoff(FieldCoordinate pBallCoordinate) {
		fBallCoordinate = pBallCoordinate;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_KICKOFF;
	}

	public FieldCoordinate getBallCoordinate() {
		return fBallCoordinate;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.BALL_COORDINATE.addTo(jsonObject, fBallCoordinate);
		return jsonObject;
	}

	public ClientCommandKickoff initFrom(Game game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fBallCoordinate = IJsonOption.BALL_COORDINATE.getFrom(game, jsonObject);
		return this;
	}

}
