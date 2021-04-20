package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

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

	public ClientCommandTouchback initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fBallCoordinate = IJsonOption.BALL_COORDINATE.getFrom(game, jsonObject);
		return this;
	}

}
