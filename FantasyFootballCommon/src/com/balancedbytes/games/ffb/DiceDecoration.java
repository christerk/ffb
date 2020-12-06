package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class DiceDecoration implements IJsonSerializable {

	private FieldCoordinate fCoordinate;
	private int fNrOfDice;

	public DiceDecoration() {
		super();
	}

	public DiceDecoration(FieldCoordinate pCoordinate, int pNrOfDice) {
		fCoordinate = pCoordinate;
		fNrOfDice = pNrOfDice;
	}

	public FieldCoordinate getCoordinate() {
		return fCoordinate;
	}

	public int getNrOfDice() {
		return fNrOfDice;
	}

	public int hashCode() {
		return getCoordinate().hashCode();
	}

	public boolean equals(Object pObj) {
		return ((pObj instanceof DiceDecoration) && getCoordinate().equals(((DiceDecoration) pObj).getCoordinate()));
	}

	// transformation

	public DiceDecoration transform() {
		return new DiceDecoration(getCoordinate().transform(), getNrOfDice());
	}

	public static DiceDecoration transform(DiceDecoration pDiceDecoration) {
		return (pDiceDecoration != null) ? pDiceDecoration.transform() : null;
	}

	public static DiceDecoration[] transform(DiceDecoration[] pDiceDecorations) {
		DiceDecoration[] transformedDiceDecorations = new DiceDecoration[0];
		if (ArrayTool.isProvided(pDiceDecorations)) {
			transformedDiceDecorations = new DiceDecoration[pDiceDecorations.length];
			for (int i = 0; i < transformedDiceDecorations.length; i++) {
				transformedDiceDecorations[i] = transform(pDiceDecorations[i]);
			}
		}
		return transformedDiceDecorations;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.COORDINATE.addTo(jsonObject, fCoordinate);
		IJsonOption.NR_OF_DICE.addTo(jsonObject, fNrOfDice);
		return jsonObject;
	}

	public DiceDecoration initFrom(JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fCoordinate = IJsonOption.COORDINATE.getFrom(jsonObject);
		fNrOfDice = IJsonOption.NR_OF_DICE.getFrom(jsonObject);
		return this;
	}

}
