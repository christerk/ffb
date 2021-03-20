package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kalimar
 */
public class ClientCommandBlitzMove extends ClientCommand implements ICommandWithActingPlayer {

	private String fActingPlayerId;
	private FieldCoordinate fCoordinateFrom;
	private final List<FieldCoordinate> fCoordinatesTo;

	public ClientCommandBlitzMove() {
		fCoordinatesTo = new ArrayList<>();
	}

	public ClientCommandBlitzMove(String pActingPlayerId, FieldCoordinate pCoordinateFrom, FieldCoordinate[] pCoordinatesTo) {
		this();
		fActingPlayerId = pActingPlayerId;
		fCoordinateFrom = pCoordinateFrom;
		addCoordinatesTo(pCoordinatesTo);
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_BLITZ_MOVE;
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
		return fCoordinatesTo.toArray(new FieldCoordinate[fCoordinatesTo.size()]);
	}

	public FieldCoordinate getCoordinateFrom() {
		return fCoordinateFrom;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.ACTING_PLAYER_ID.addTo(jsonObject, fActingPlayerId);
		IJsonOption.COORDINATE_FROM.addTo(jsonObject, fCoordinateFrom);
		IJsonOption.COORDINATES_TO.addTo(jsonObject, fCoordinatesTo);
		return jsonObject;
	}

	public ClientCommandBlitzMove initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fActingPlayerId = IJsonOption.ACTING_PLAYER_ID.getFrom(game, jsonObject);
		fCoordinateFrom = IJsonOption.COORDINATE_FROM.getFrom(game, jsonObject);
		addCoordinatesTo(IJsonOption.COORDINATES_TO.getFrom(game, jsonObject));
		return this;
	}

}
