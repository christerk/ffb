package com.fumbbl.ffb.report.mixed;

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

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportPenaltyShootout extends NoDiceReport {

	private int rollHome, rollAway, scoreHome, scoreAway;
	private String rollCount, winningTeam;
	private Boolean homeTeamWonPenalty;

	public ReportPenaltyShootout() {
		super();
	}

	public ReportPenaltyShootout(int rollHome, int scoreHome, int rollAway, int scoreAway, Boolean homeTeamWonPenalty, String rollCount, String winningTeam) {
		this.rollHome = rollHome;
		this.rollAway = rollAway;
		this.homeTeamWonPenalty = homeTeamWonPenalty;
		this.rollCount = rollCount;
		this.scoreAway = scoreAway;
		this.scoreHome = scoreHome;
		this.winningTeam = winningTeam;
	}

	public ReportId getId() {
		return ReportId.PENALTY_SHOOTOUT;
	}

	public int getRollHome() {
		return rollHome;
	}

	public int getRollAway() {
		return rollAway;
	}

	public String getRollCount() {
		return rollCount;
	}

	public int getScoreHome() {
		return scoreHome;
	}

	public int getScoreAway() {
		return scoreAway;
	}

	public Boolean getHomeTeamWonPenalty() {
		return homeTeamWonPenalty;
	}

	public String getWinningTeam() {
		return winningTeam;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportPenaltyShootout(getRollAway(), scoreAway, getRollHome(), scoreHome, homeTeamWonPenalty == null ? null : !homeTeamWonPenalty, rollCount, winningTeam);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.ROLL_HOME.addTo(jsonObject, rollHome);
		IJsonOption.ROLL_AWAY.addTo(jsonObject, rollAway);
		IJsonOption.HOME_TEAM.addTo(jsonObject, homeTeamWonPenalty);
		IJsonOption.ROLL_COUNT.addTo(jsonObject, rollCount);
		IJsonOption.PENALTY_SCORE_HOME.addTo(jsonObject, scoreHome);
		IJsonOption.PENALTY_SCORE_AWAY.addTo(jsonObject, scoreAway);
		IJsonOption.TEAM_ID.addTo(jsonObject, winningTeam);
		return jsonObject;
	}

	public ReportPenaltyShootout initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		rollHome = IJsonOption.ROLL_HOME.getFrom(source, jsonObject);
		rollAway = IJsonOption.ROLL_AWAY.getFrom(source, jsonObject);
		homeTeamWonPenalty = IJsonOption.HOME_TEAM.getFrom(source, jsonObject);
		rollCount = IJsonOption.ROLL_COUNT.getFrom(source, jsonObject);
		scoreHome = IJsonOption.PENALTY_SCORE_HOME.getFrom(source, jsonObject);
		scoreAway = IJsonOption.PENALTY_SCORE_AWAY.getFrom(source, jsonObject);
		winningTeam = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		return this;
	}

}
