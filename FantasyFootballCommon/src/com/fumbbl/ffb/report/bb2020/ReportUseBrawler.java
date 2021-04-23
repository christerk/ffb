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
public class ReportUseBrawler implements IReport {

	private int brawlerCount;
	private String playerId;

	public ReportUseBrawler() {
	}

	public ReportUseBrawler(String playerId, int brawlerCount) {
		this.playerId = playerId;
		this.brawlerCount = brawlerCount;
	}

	public int getBrawlerCount() {
		return brawlerCount;
	}

	public String getPlayerId() {
		return playerId;
	}

	@Override
	public ReportUseBrawler initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		brawlerCount = IJsonOption.BRAWLER_COUNT.getFrom(game, jsonObject);
		playerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.BRAWLER_COUNT.addTo(jsonObject, brawlerCount);
		return jsonObject;
	}

	@Override
	public ReportId getId() {
		return ReportId.USE_BRAWLER;
	}

	@Override
	public IReport transform(IFactorySource source) {
		return new ReportUseBrawler(playerId, brawlerCount);
	}
}
