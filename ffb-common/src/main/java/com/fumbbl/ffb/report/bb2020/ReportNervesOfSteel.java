package com.fumbbl.ffb.report.bb2020;

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

@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportNervesOfSteel extends NoDiceReport {

	private String playerId;
	private String ballAction;
	private boolean bomb;

	public ReportNervesOfSteel() {
	}

	public ReportNervesOfSteel(String playerId, String doWithTheBall) {
		this(playerId, doWithTheBall, false);
	}

	public ReportNervesOfSteel(String playerId, boolean bomb) {
		this(playerId, null, bomb);
	}

	private ReportNervesOfSteel(String playerId, String doWithTheBall, boolean bomb) {
		this.playerId = playerId;
		this.ballAction = doWithTheBall;
		this.bomb = bomb;
	}
	
	@Override
	public Object initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		ballAction = IJsonOption.BALL_ACTION.getFrom(source, jsonObject);
		if (IJsonOption.BOMB.isDefinedIn(jsonObject)) {
			bomb = IJsonOption.BOMB.getFrom(source, jsonObject);
		}
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.BALL_ACTION.addTo(jsonObject, ballAction);
		IJsonOption.BOMB.addTo(jsonObject, bomb);
		return jsonObject;
	}

	@Override
	public ReportId getId() {
		return ReportId.NERVES_OF_STEEL;
	}

	@Override
	public IReport transform(IFactorySource source) {
		return new ReportNervesOfSteel(playerId, ballAction, bomb);
	}

	public String getPlayerId() {
		return playerId;
	}

	public String getBallAction() {
		return ballAction;
	}

	public boolean isBomb() {
		return bomb;
	}
}
