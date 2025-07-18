package com.fumbbl.ffb.marking;

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
public class PlayerMarker implements IJsonSerializable {

	private String fPlayerId;
	private String fHomeText;
	private String fAwayText;

	public PlayerMarker() {
		super();
	}

	public PlayerMarker(String pPlayerId) {
		fPlayerId = pPlayerId;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public void setHomeText(String pHomeText) {
		fHomeText = pHomeText;
	}

	public String getHomeText() {
		return fHomeText;
	}

	public void setAwayText(String pAwayText) {
		fAwayText = pAwayText;
	}

	public String getAwayText() {
		return fAwayText;
	}

	public int hashCode() {
		return getPlayerId().hashCode();
	}

	public boolean equals(Object pObj) {
		return ((pObj instanceof PlayerMarker) && getPlayerId().equals(((PlayerMarker) pObj).getPlayerId()));
	}

	// Transformation

	public PlayerMarker transform() {
		PlayerMarker transformedMarker = new PlayerMarker(getPlayerId());
		transformedMarker.setAwayText(getHomeText());
		transformedMarker.setHomeText(getAwayText());
		return transformedMarker;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.HOME_TEXT.addTo(jsonObject, fHomeText);
		IJsonOption.AWAY_TEXT.addTo(jsonObject, fAwayText);
		return jsonObject;
	}

	public PlayerMarker initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		fHomeText = IJsonOption.HOME_TEXT.getFrom(source, jsonObject);
		fAwayText = IJsonOption.AWAY_TEXT.getFrom(source, jsonObject);
		return this;
	}

}
