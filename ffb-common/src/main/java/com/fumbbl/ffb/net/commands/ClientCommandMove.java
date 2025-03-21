package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.util.ArrayTool;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kalimar
 */
public class ClientCommandMove extends ClientCommand implements ICommandWithActingPlayer {

	private String fActingPlayerId;
	private FieldCoordinate fCoordinateFrom;
	private final List<FieldCoordinate> fCoordinatesTo;
	private String ballAndChainRrSetting;

	public ClientCommandMove() {
		fCoordinatesTo = new ArrayList<>();
	}

	public ClientCommandMove(String pActingPlayerId, FieldCoordinate pCoordinateFrom, FieldCoordinate[] pCoordinatesTo, String ballAndChainRrSetting) {
		this();
		fActingPlayerId = pActingPlayerId;
		fCoordinateFrom = pCoordinateFrom;
		addCoordinatesTo(pCoordinatesTo);
		this.ballAndChainRrSetting = ballAndChainRrSetting;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_MOVE;
	}

	public String getActingPlayerId() {
		return fActingPlayerId;
	}

	private void addCoordinateTo(FieldCoordinate pCoordinateTo) {
		if (pCoordinateTo != null) {
			fCoordinatesTo.add(pCoordinateTo);
		}
	}

	private void addCoordinatesTo(FieldCoordinate[] pCoordinatesTo) {
		if (ArrayTool.isProvided(pCoordinatesTo)) {
			for (FieldCoordinate coordinate : pCoordinatesTo) {
				addCoordinateTo(coordinate);
			}
		}
	}

	public FieldCoordinate[] getCoordinatesTo() {
		return fCoordinatesTo.toArray(new FieldCoordinate[0]);
	}

	public FieldCoordinate getCoordinateFrom() {
		return fCoordinateFrom;
	}

	public String getBallAndChainRrSetting() {
		return ballAndChainRrSetting;
	}
// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.ACTING_PLAYER_ID.addTo(jsonObject, fActingPlayerId);
		IJsonOption.COORDINATE_FROM.addTo(jsonObject, fCoordinateFrom);
		IJsonOption.COORDINATES_TO.addTo(jsonObject, fCoordinatesTo);
		IJsonOption.BALL_AND_CHAIN_RE_ROLL_SETTING.addTo(jsonObject, ballAndChainRrSetting);
		return jsonObject;
	}

	public ClientCommandMove initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fActingPlayerId = IJsonOption.ACTING_PLAYER_ID.getFrom(source, jsonObject);
		fCoordinateFrom = IJsonOption.COORDINATE_FROM.getFrom(source, jsonObject);
		addCoordinatesTo(IJsonOption.COORDINATES_TO.getFrom(source, jsonObject));
		ballAndChainRrSetting = IJsonOption.BALL_AND_CHAIN_RE_ROLL_SETTING.getFrom(source, jsonObject);
		return this;
	}

}
