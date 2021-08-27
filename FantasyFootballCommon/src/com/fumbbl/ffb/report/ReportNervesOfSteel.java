package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportNervesOfSteel implements IReport {

	public String playerId;
	public String ballAction;

	public ReportNervesOfSteel(){}

	public ReportNervesOfSteel(String playerId, String doWithTheBall) {
		this.playerId = playerId;
		this.ballAction = doWithTheBall;
	}
	
	@Override
	public Object initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		playerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		ballAction = IJsonOption.BALL_ACTION.getFrom(game, jsonObject);
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
