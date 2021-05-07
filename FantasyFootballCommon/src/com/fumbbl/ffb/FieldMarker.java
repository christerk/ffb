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
public class FieldMarker implements IJsonSerializable {

	private FieldCoordinate coordinate;
	private String homeText;
	private String awayText;

	public FieldMarker() {
		super();
	}

	public FieldMarker(FieldCoordinate pCoordinate) {
		coordinate = pCoordinate;
	}

	public FieldMarker(FieldCoordinate coordinate, String homeText, String awayText) {
		this.coordinate = coordinate;
		this.homeText = homeText;
		this.awayText = awayText;
	}

	public FieldCoordinate getCoordinate() {
		return coordinate;
	}

	public void setHomeText(String pHomeText) {
		homeText = pHomeText;
	}

	public String getHomeText() {
		return homeText;
	}

	public void setAwayText(String pAwayText) {
		awayText = pAwayText;
	}

	public String getAwayText() {
		return awayText;
	}

	public int hashCode() {
		return getCoordinate().hashCode();
	}

	public boolean equals(Object pObj) {
		return ((pObj instanceof FieldMarker) && getCoordinate().equals(((FieldMarker) pObj).getCoordinate()));
	}

	// Transformation

	public FieldMarker transform() {
		FieldMarker transformedMarker = new FieldMarker(getCoordinate().transform());
		transformedMarker.setAwayText(getHomeText());
		transformedMarker.setHomeText(getAwayText());
		return transformedMarker;
	}

	public static FieldMarker transform(FieldMarker pFieldMarker) {
		return (pFieldMarker != null) ? pFieldMarker.transform() : null;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.COORDINATE.addTo(jsonObject, coordinate);
		IJsonOption.HOME_TEXT.addTo(jsonObject, homeText);
		IJsonOption.AWAY_TEXT.addTo(jsonObject, awayText);
		return jsonObject;
	}

	public FieldMarker initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		coordinate = IJsonOption.COORDINATE.getFrom(game, jsonObject);
		homeText = IJsonOption.HOME_TEXT.getFrom(game, jsonObject);
		awayText = IJsonOption.AWAY_TEXT.getFrom(game, jsonObject);
		return this;
	}

}
