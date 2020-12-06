package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandTouchback extends ClientCommand {

	private FieldCoordinate fBallCoordinate;

	public ClientCommandTouchback() {
		super();
	}

	public ClientCommandTouchback(FieldCoordinate pBallCoordinate) {
		fBallCoordinate = pBallCoordinate;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_TOUCHBACK;
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

	public ClientCommandTouchback initFrom(JsonValue jsonValue) {
		super.initFrom(jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fBallCoordinate = IJsonOption.BALL_COORDINATE.getFrom(jsonObject);
		return this;
	}

}
