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
	private int roll;

	public ReportTrapDoor() {
	}

	public ReportTrapDoor(String playerId, int roll, boolean escaped) {
		this.playerId = playerId;
		this.escaped = escaped;
		this.roll = roll;
	}

	public String getPlayerId() {
		return playerId;
	}

	public boolean isEscaped() {
		return escaped;
	}

	public int getRoll() {
		return roll;
	}

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.ESCAPED.addTo(jsonObject, escaped);
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.ROLL.addTo(jsonObject, roll);
		return jsonObject;
	}

	public ReportTrapDoor initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		escaped = IJsonOption.ESCAPED.getFrom(source, jsonObject);
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		roll = IJsonOption.ROLL.getFrom(source, jsonObject);
		return this;
	}


	@Override
	public ReportId getId() {
		return ReportId.TRAP_DOOR;
	}

	@Override
	public IReport transform(IFactorySource source) {
		return new ReportTrapDoor(playerId, roll, escaped);
	}
}
