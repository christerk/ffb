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
public class ReportPenaltyShootout implements IReport {

	private int rollHome, rollAway, scoreHome, scoreAway;
	private String winningCoach, rollCount, winningTeam;

	public ReportPenaltyShootout() {
		super();
	}

	public ReportPenaltyShootout(int rollHome, int scoreHome, int rollAway, int scoreAway, String winningCoach, String rollCount, String winningTeam) {
		this.rollHome = rollHome;
		this.rollAway = rollAway;
		this.winningCoach = winningCoach;
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

	public String getWinningCoach() {
		return winningCoach;
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

	public String getWinningTeam() {
		return winningTeam;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportPenaltyShootout(getRollAway(), scoreAway, getRollHome(), scoreHome, winningCoach, rollCount, winningTeam);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.ROLL_HOME.addTo(jsonObject, rollHome);
		IJsonOption.ROLL_AWAY.addTo(jsonObject, rollAway);
		IJsonOption.COACH.addTo(jsonObject, winningCoach);
		IJsonOption.ROLL_COUNT.addTo(jsonObject, rollCount);
		IJsonOption.PENALTY_SCORE_HOME.addTo(jsonObject, scoreHome);
		IJsonOption.PENALTY_SCORE_AWAY.addTo(jsonObject, scoreAway);
		IJsonOption.TEAM_ID.addTo(jsonObject, winningTeam);
		return jsonObject;
	}

	public ReportPenaltyShootout initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		rollHome = IJsonOption.ROLL_HOME.getFrom(game, jsonObject);
		rollAway = IJsonOption.ROLL_AWAY.getFrom(game, jsonObject);
		winningCoach = IJsonOption.COACH.getFrom(game, jsonObject);
		rollCount = IJsonOption.ROLL_COUNT.getFrom(game, jsonObject);
		scoreHome = IJsonOption.PENALTY_SCORE_HOME.getFrom(game, jsonObject);
		scoreAway = IJsonOption.PENALTY_SCORE_AWAY.getFrom(game, jsonObject);
		winningTeam = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		return this;
	}

}
