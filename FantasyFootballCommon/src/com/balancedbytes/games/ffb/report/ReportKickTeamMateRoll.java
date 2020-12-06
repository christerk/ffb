package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ReportKickTeamMateRoll implements IReport {

	private String fKickingPlayerId;
	private String fKickedPlayerId;
	private int fKickDistance;
	private boolean fSuccessful;
	private boolean fReRolled;
	private int[] fRoll;

	public ReportKickTeamMateRoll() {
		fRoll = new int[2];
	}

	@Override
	public ReportId getId() {
		return ReportId.KICK_TEAM_MATE_ROLL;
	}

	public ReportKickTeamMateRoll(String pKickingPlayerId, String pKickedPlayerId, boolean pSuccessful, int[] pRoll,
			boolean pReRolled, int pKickDistance) {
		fKickingPlayerId = pKickingPlayerId;
		fKickedPlayerId = pKickedPlayerId;
		fKickDistance = pKickDistance;
		fSuccessful = pSuccessful;
		fReRolled = pReRolled;
		fRoll = pRoll;
	}

	public String getKickingPlayerId() {
		return fKickingPlayerId;
	}

	public String getKickedPlayerId() {
		return fKickedPlayerId;
	}

	public int getKickDistance() {
		return fKickDistance;
	}

	public boolean isSuccessful() {
		return fSuccessful;
	}

	public int[] getRoll() {
		return fRoll;
	}

	public boolean isReRolled() {
		return fReRolled;
	}

	// transformation

	public IReport transform() {
		return new ReportKickTeamMateRoll(getKickingPlayerId(), getKickedPlayerId(), isSuccessful(), getRoll(),
				isReRolled(), getKickDistance());
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fKickingPlayerId);
		IJsonOption.DEFENDER_ID.addTo(jsonObject, fKickedPlayerId);
		IJsonOption.DISTANCE.addTo(jsonObject, fKickDistance);
		IJsonOption.SUCCESSFUL.addTo(jsonObject, fSuccessful);
		IJsonOption.ROLLS.addTo(jsonObject, fRoll);
		IJsonOption.RE_ROLLED.addTo(jsonObject, fReRolled);
		return jsonObject;
	}

	@Override
	public ReportKickTeamMateRoll initFrom(JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
		fKickingPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
		fKickedPlayerId = IJsonOption.DEFENDER_ID.getFrom(jsonObject);
		fKickDistance = IJsonOption.DISTANCE.getFrom(jsonObject);
		fSuccessful = IJsonOption.SUCCESSFUL.getFrom(jsonObject);
		fRoll = IJsonOption.ROLLS.getFrom(jsonObject);
		fReRolled = IJsonOption.RE_ROLLED.getFrom(jsonObject);
		return this;
	}

}
