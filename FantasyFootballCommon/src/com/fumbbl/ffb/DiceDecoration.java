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

	public DiceDecoration initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fCoordinate = IJsonOption.COORDINATE.getFrom(game, jsonObject);
		fNrOfDice = IJsonOption.NR_OF_DICE.getFrom(game, jsonObject);
		return this;
	}

}
