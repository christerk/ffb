package com.fumbbl.ffb.report.bb2016;

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

@RulesCollection(RulesCollection.Rules.BB2016)
public class ReportNervesOfSteel extends NoDiceReport {

	public String playerId;
	public String ballAction;

	public ReportNervesOfSteel() {
	}

	public ReportNervesOfSteel(String playerId, String doWithTheBall) {
		this.playerId = playerId;
		this.ballAction = doWithTheBall;
	}
	
	@Override
	public Object initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		ballAction = IJsonOption.BALL_ACTION.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.BALL_ACTION.addTo(jsonObject, ballAction);
		return jsonObject;
	}

	@Override
	public ReportId getId() {
		return ReportId.NERVES_OF_STEEL;
	}

	@Override
	public IReport transform(IFactorySource source) {
		return new ReportNervesOfSteel(playerId, ballAction);
	}

	public String getPlayerId() {
		return playerId;
	}

	public String getBallAction() {
		return ballAction;
	}

}
