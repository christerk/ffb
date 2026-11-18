package com.fumbbl.ffb.report.bb2025;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.NoDiceReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportSquigRage extends NoDiceReport {

	public enum Outcome {
		MUST_BLOCK, KNOCKS_DOWN_TEAM_MATE, DISTRACTED
	}

	private String playerId;
	private String teamMateId;
	private Outcome outcome;

	@SuppressWarnings("unused")
	public ReportSquigRage() {
	}

	public ReportSquigRage(String playerId, Outcome outcome, String teamMateId) {
		this.playerId = playerId;
		this.outcome = outcome;
		this.teamMateId = teamMateId;
	}

	@Override
	public ReportId getId() {
		return ReportId.SQUIG_RAGE;
	}

	@Override
	public ReportSquigRage transform(IFactorySource source) {
		return new ReportSquigRage(playerId, outcome, teamMateId);
	}

	public String getPlayerId() {
		return playerId;
	}

	public String getTeamMateId() {
		return teamMateId;
	}

	public Outcome getOutcome() {
		return outcome;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.DEFENDER_ID.addTo(jsonObject, teamMateId);
		IJsonOption.STATUS.addTo(jsonObject, outcome != null ? outcome.name() : null);
		return jsonObject;
	}

	@Override
	public ReportSquigRage initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		teamMateId = IJsonOption.DEFENDER_ID.getFrom(source, jsonObject);
		String status = IJsonOption.STATUS.getFrom(source, jsonObject);
		outcome = status != null ? Outcome.valueOf(status) : null;
		return this;
	}
}
