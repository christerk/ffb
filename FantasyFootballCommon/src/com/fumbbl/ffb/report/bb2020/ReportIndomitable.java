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

@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportIndomitable implements IReport {

	private String playerId;
	private String defenderId;

	public ReportIndomitable() {
	}

	public ReportIndomitable(String playerId, String defenderId) {
		this.playerId = playerId;
		this.defenderId = defenderId;
	}

	public String getPlayerId() {
		return playerId;
	}

	public String getDefenderId() {
		return defenderId;
	}

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.DEFENDER_ID.addTo(jsonObject, defenderId);
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		return jsonObject;
	}

	public ReportIndomitable initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		defenderId = IJsonOption.DEFENDER_ID.getFrom(game, jsonObject);
		playerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		return this;
	}

	@Override
	public ReportId getId() {
		return ReportId.INDOMITABLE;
	}

	@Override
	public IReport transform(IFactorySource source) {
		return new ReportIndomitable(playerId, defenderId);
	}
}
