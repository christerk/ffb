package com.fumbbl.ffb;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.util.ArrayTool;

/**
 * 
 * @author Kalimar
 */
public final class MoveSquare implements IJsonSerializable {

	private FieldCoordinate fCoordinate;
	private int fMinimumRollDodge;
	private int fMinimumRollGoForIt;

	public MoveSquare() {
		super();
	}

	public MoveSquare(FieldCoordinate pCoordinate, int pMinimumRollDodge, int pMinimumRollGoForIt) {
		if (pCoordinate == null) {
			throw new IllegalArgumentException("Parameter coordinate must not be null.");
		}
		fCoordinate = pCoordinate;
		fMinimumRollDodge = pMinimumRollDodge;
		fMinimumRollGoForIt = pMinimumRollGoForIt;
	}

	public FieldCoordinate getCoordinate() {
		return fCoordinate;
	}

	public int getMinimumRollDodge() {
		return fMinimumRollDodge;
	}

	public boolean isDodging() {
		return (getMinimumRollDodge() > 0);
	}

	public int getMinimumRollGoForIt() {
		return fMinimumRollGoForIt;
	}

	public boolean isGoingForIt() {
		return (getMinimumRollGoForIt() > 0);
	}

	public int hashCode() {
		return getCoordinate().hashCode();
	}

	public boolean equals(Object pObj) {
		return ((pObj instanceof MoveSquare) && getCoordinate().equals(((MoveSquare) pObj).getCoordinate()));
	}

	// transformation

	public MoveSquare transform() {
		return new MoveSquare(getCoordinate().transform(), getMinimumRollDodge(), getMinimumRollGoForIt());
	}

	public static MoveSquare transform(MoveSquare pMoveSquare) {
		return (pMoveSquare != null) ? pMoveSquare.transform() : null;
	}

	public static MoveSquare[] transform(MoveSquare[] pMoveSquares) {
		MoveSquare[] transformedMoveSquares = new MoveSquare[0];
		if (ArrayTool.isProvided(pMoveSquares)) {
			transformedMoveSquares = new MoveSquare[pMoveSquares.length];
			for (int i = 0; i < transformedMoveSquares.length; i++) {
				transformedMoveSquares[i] = transform(pMoveSquares[i]);
			}
		}
		return transformedMoveSquares;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.COORDINATE.addTo(jsonObject, fCoordinate);
		IJsonOption.MINIMUM_ROLL_DODGE.addTo(jsonObject, fMinimumRollDodge);
		IJsonOption.MINIMUM_ROLL_GFI.addTo(jsonObject, fMinimumRollGoForIt);
		return jsonObject;
	}

	public MoveSquare initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fCoordinate = IJsonOption.COORDINATE.getFrom(source, jsonObject);
		fMinimumRollDodge = IJsonOption.MINIMUM_ROLL_DODGE.getFrom(source, jsonObject);
		fMinimumRollGoForIt = IJsonOption.MINIMUM_ROLL_GFI.getFrom(source, jsonObject);
		return this;
	}

	public enum Kind {
		RUSH, DODGE, RUSH_DODGE, MOVE
	}
}
