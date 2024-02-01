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
public class KnockoutRecovery implements IJsonSerializable {

	private String fPlayerId;
	private boolean fRecovering;
	private int fRoll;
	private int fBloodweiserBabes;
	private String reRollReason;

	public KnockoutRecovery() {
		super();
	}

	public KnockoutRecovery(String pPlayerId, boolean pRecovering, int pRoll, int pBloodweiserBabes, String reRollReason) {
		fPlayerId = pPlayerId;
		fRecovering = pRecovering;
		fRoll = pRoll;
		fBloodweiserBabes = pBloodweiserBabes;
		this.reRollReason = reRollReason;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public boolean isRecovering() {
		return fRecovering;
	}

	public int getRoll() {
		return fRoll;
	}

	public int getBloodweiserBabes() {
		return fBloodweiserBabes;
	}

	public String getReRollReason() {
		return reRollReason;
	}
// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.RECOVERING.addTo(jsonObject, fRecovering);
		IJsonOption.ROLL.addTo(jsonObject, fRoll);
		IJsonOption.BLOODWEISER_KEGS.addTo(jsonObject, fBloodweiserBabes);
		IJsonOption.REASON.addTo(jsonObject, reRollReason);
		return jsonObject;
	}

	public KnockoutRecovery initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		fRecovering = IJsonOption.RECOVERING.getFrom(source, jsonObject);
		fRoll = IJsonOption.ROLL.getFrom(source, jsonObject);
		fBloodweiserBabes = IJsonOption.BLOODWEISER_KEGS.getFrom(source, jsonObject);
		reRollReason = IJsonOption.REASON.getFrom(source, jsonObject);
		return this;
	}

}
