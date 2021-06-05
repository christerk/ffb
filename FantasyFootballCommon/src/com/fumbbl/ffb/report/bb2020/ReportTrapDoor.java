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
public class ReportTrapDoor implements IReport {

	private String playerId;
	private boolean escaped;

	public ReportTrapDoor() {
	}

	public ReportTrapDoor(String playerId, boolean escaped) {
		this.playerId = playerId;
		this.escaped = escaped;
	}

	public String getPlayerId() {
		return playerId;
	}

	public boolean isEscaped() {
		return escaped;
	}

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.ESCAPED.addTo(jsonObject, escaped);
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		return jsonObject;
	}

	public ReportTrapDoor initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		escaped = IJsonOption.ESCAPED.getFrom(game, jsonObject);
		playerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		return this;
	}


	@Override
	public ReportId getId() {
		return ReportId.TRAP_DOOR;
	}

	@Override
	public IReport transform(IFactorySource source) {
		return new ReportTrapDoor(playerId, escaped);
	}
}
