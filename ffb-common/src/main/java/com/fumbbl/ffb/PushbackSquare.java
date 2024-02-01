package com.fumbbl.ffb;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;

/**
 * 
 * @author Kalimar
 */
public final class PushbackSquare implements IJsonSerializable {

	private FieldCoordinate fCoordinate;
	private Direction fDirection;
	private boolean fSelected;
	private boolean fLocked;
	private boolean fHomeChoice;

	public PushbackSquare() {
		super();
	}

	public PushbackSquare(FieldCoordinate pCoordinate, Direction pDirection, boolean pHomeChoice) {
		if (pCoordinate == null) {
			throw new IllegalArgumentException("Parameter coordinate must not be null.");
		}
		fCoordinate = pCoordinate;
		fDirection = pDirection;
		fHomeChoice = pHomeChoice;
		fLocked = false;
	}

	public FieldCoordinate getCoordinate() {
		return fCoordinate;
	}

	public Direction getDirection() {
		return fDirection;
	}

	public boolean isSelected() {
		return fSelected;
	}

	public void setSelected(boolean pSelected) {
		fSelected = pSelected;
	}

	public void setLocked(boolean pLocked) {
		fLocked = pLocked;
	}

	public boolean isLocked() {
		return fLocked;
	}

	public void setHomeChoice(boolean pHomeChoice) {
		fHomeChoice = pHomeChoice;
	}

	public boolean isHomeChoice() {
		return fHomeChoice;
	}

	public PushbackSquare transform() {
		FieldCoordinate transformedCoordinate = getCoordinate().transform();
		Direction transformedDirection = getDirection().transform();
		PushbackSquare transformedPushback = new PushbackSquare(transformedCoordinate, transformedDirection,
				!isHomeChoice());
		transformedPushback.setSelected(isSelected());
		transformedPushback.setLocked(isLocked());
		return transformedPushback;
	}

	public static PushbackSquare transform(PushbackSquare pPushbackSquare) {
		return (pPushbackSquare != null) ? pPushbackSquare.transform() : null;
	}

	public int hashCode() {
		return getCoordinate().hashCode();
	}

	public boolean equals(Object pObj) {
		return ((pObj instanceof PushbackSquare) && getCoordinate().equals(((PushbackSquare) pObj).getCoordinate()));
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.COORDINATE.addTo(jsonObject, fCoordinate);
		IJsonOption.DIRECTION.addTo(jsonObject, fDirection);
		IJsonOption.SELECTED.addTo(jsonObject, fSelected);
		IJsonOption.LOCKED.addTo(jsonObject, fLocked);
		IJsonOption.HOME_CHOICE.addTo(jsonObject, fHomeChoice);
		return jsonObject;
	}

	public PushbackSquare initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fCoordinate = IJsonOption.COORDINATE.getFrom(source, jsonObject);
		fDirection = (Direction) IJsonOption.DIRECTION.getFrom(source, jsonObject);
		fSelected = IJsonOption.SELECTED.getFrom(source, jsonObject);
		fLocked = IJsonOption.LOCKED.getFrom(source, jsonObject);
		fHomeChoice = IJsonOption.HOME_CHOICE.getFrom(source, jsonObject);
		return this;
	}

}
