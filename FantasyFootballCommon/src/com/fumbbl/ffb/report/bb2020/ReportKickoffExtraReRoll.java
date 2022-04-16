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
public class ReportKickoffExtraReRoll implements IReport {

	private int rollHome;
	private int rollAway;
	private String teamId;

	public ReportKickoffExtraReRoll() {
		super();
	}

	public ReportKickoffExtraReRoll(int rollHome, int rollAway, String teamId) {
		this.rollHome = rollHome;
		this.rollAway = rollAway;
		this.teamId = teamId;
	}

	public ReportId getId() {
		return ReportId.KICKOFF_EXTRA_RE_ROLL;
	}

	public int getRollHome() {
		return rollHome;
	}

	public int getRollAway() {
		return rollAway;
	}

	public String getTeamId() {
		return teamId;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportKickoffExtraReRoll(getRollAway(), getRollHome(), teamId);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		IJsonOption.ROLL_HOME.addTo(jsonObject, rollHome);
		IJsonOption.ROLL_AWAY.addTo(jsonObject, rollAway);
		return jsonObject;
	}

	public ReportKickoffExtraReRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		rollHome = IJsonOption.ROLL_HOME.getFrom(source, jsonObject);
		rollAway = IJsonOption.ROLL_AWAY.getFrom(source, jsonObject);
		teamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		return this;
	}

}
