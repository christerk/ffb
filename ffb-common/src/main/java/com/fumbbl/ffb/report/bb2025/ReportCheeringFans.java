package com.fumbbl.ffb.report.bb2025;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.NoDiceReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportCheeringFans extends NoDiceReport {

	int rollHome, rollAway;
	private Set<String> teamIds;

	public ReportCheeringFans() {
		super();
	}

	public ReportCheeringFans(Set<String> teamIds, int rollHome, int rollAway) {
		this.teamIds = teamIds;
		this.rollHome = rollHome;
		this.rollAway = rollAway;
	}

	public ReportId getId() {
		return ReportId.KICKOFF_CHEERING_FANS;
	}

	public int getRollHome() {
		return rollHome;
	}

	public int getRollAway() {
		return rollAway;
	}

	public Set<String> getTeamIds() {
		return teamIds;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportCheeringFans(teamIds, rollAway, rollHome);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_IDS_ADDITIONAL_ASSIST.addTo(jsonObject, teamIds);
		IJsonOption.ROLL_HOME.addTo(jsonObject, rollHome);
		IJsonOption.ROLL_AWAY.addTo(jsonObject, rollAway);
		return jsonObject;
	}

	public ReportCheeringFans initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		teamIds = new HashSet<>(Arrays.asList(IJsonOption.TEAM_IDS_ADDITIONAL_ASSIST.getFrom(source, jsonObject)));
		rollAway = IJsonOption.ROLL_AWAY.getFrom(source, jsonObject);
		rollHome = IJsonOption.ROLL_HOME.getFrom(source, jsonObject);
		return this;
	}

}
