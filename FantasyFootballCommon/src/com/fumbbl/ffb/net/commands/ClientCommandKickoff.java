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

	public ClientCommandKickoff initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fBallCoordinate = IJsonOption.BALL_COORDINATE.getFrom(source, jsonObject);
		return this;
	}

}
