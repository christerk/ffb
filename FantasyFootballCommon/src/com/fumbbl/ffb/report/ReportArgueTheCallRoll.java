package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportArgueTheCallRoll implements IReport {

	private String fPlayerId;
	private boolean fSuccessful;
	private boolean fCoachBanned;
	private boolean staysOnPitch;
	private int fRoll;

	public ReportArgueTheCallRoll() {
		super();
	}

	public ReportArgueTheCallRoll(String playerId, boolean successful, boolean coachBanned, int roll, boolean staysOnPitch) {
		fPlayerId = playerId;
		fSuccessful = successful;
		fCoachBanned = coachBanned;
		fRoll = roll;
		this.staysOnPitch = staysOnPitch;
	}

	public ReportId getId() {
		return ReportId.ARGUE_THE_CALL;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public boolean isSuccessful() {
		return fSuccessful;
	}

	public boolean isCoachBanned() {
		return fCoachBanned;
	}

	public int getRoll() {
		return fRoll;
	}

	public boolean isStaysOnPitch() {
		return staysOnPitch;
	}
// transformation

	public IReport transform(IFactorySource source) {
		return new ReportArgueTheCallRoll(getPlayerId(), isSuccessful(), isCoachBanned(), getRoll(), staysOnPitch);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.SUCCESSFUL.addTo(jsonObject, fSuccessful);
		IJsonOption.COACH_BANNED.addTo(jsonObject, fCoachBanned);
		IJsonOption.ROLL.addTo(jsonObject, fRoll);
		IJsonOption.STAYS_ON_PITCH.addTo(jsonObject, staysOnPitch);
		return jsonObject;
	}

	public ReportArgueTheCallRoll initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		fSuccessful = IJsonOption.SUCCESSFUL.getFrom(game, jsonObject);
		fCoachBanned = IJsonOption.COACH_BANNED.getFrom(game, jsonObject);
		fRoll = IJsonOption.ROLL.getFrom(game, jsonObject);
		staysOnPitch = IJsonOption.STAYS_ON_PITCH.getFrom(game, jsonObject);
		return this;
	}

}
