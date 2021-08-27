package com.fumbbl.ffb.report.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportOfficiousRefRoll implements IReport {

	private int roll;
	private String playerId;

	public ReportOfficiousRefRoll() {
	}

	public ReportOfficiousRefRoll(int roll, String playerId) {
		this.roll = roll;
		this.playerId = playerId;
	}

	public int getRoll() {
		return roll;
	}

	public String getPlayerId() {
		return playerId;
	}

	public ReportId getId() {
		return ReportId.OFFICIOUS_REF_ROLL;
	}


	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportOfficiousRefRoll(roll, playerId);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.ROLL.addTo(jsonObject, roll);
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		return jsonObject;
	}

	public ReportOfficiousRefRoll initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		roll = IJsonOption.ROLL.getFrom(game, jsonObject);
		playerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		return this;
	}

}
