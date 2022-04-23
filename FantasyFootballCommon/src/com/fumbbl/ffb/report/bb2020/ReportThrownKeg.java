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
public class ReportThrownKeg implements IReport {

	private String playerId, targetPlayerId;
	private int roll;

	private boolean success, fumble;

	public ReportThrownKeg() {
	}

	public ReportThrownKeg(String playerId, String targetPlayerId, int roll, boolean success, boolean fumble) {
		this.playerId = playerId;
		this.targetPlayerId = targetPlayerId;
		this.roll = roll;
		this.success = success;
		this.fumble = fumble;
	}

	public String getPlayerId() {
		return playerId;
	}

	public int getRoll() {
		return roll;
	}

	public String getTargetPlayerId() {
		return targetPlayerId;
	}

	public boolean isSuccess() {
		return success;
	}

	public boolean isFumble() {
		return fumble;
	}

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.ROLL.addTo(jsonObject, roll);
		IJsonOption.TARGET_PLAYER_ID.addTo(jsonObject, targetPlayerId);
		IJsonOption.SUCCESSFUL.addTo(jsonObject, success);
		IJsonOption.FUMBLE.addTo(jsonObject, fumble);
		return jsonObject;
	}

	public ReportThrownKeg initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		targetPlayerId = IJsonOption.TARGET_PLAYER_ID.getFrom(source, jsonObject);
		roll = IJsonOption.ROLL.getFrom(source, jsonObject);
		success = IJsonOption.SUCCESSFUL.getFrom(source, jsonObject);
		fumble = IJsonOption.FUMBLE.getFrom(source, jsonObject);
		return this;
	}


	@Override
	public ReportId getId() {
		return ReportId.THROWN_KEG;
	}

	@Override
	public IReport transform(IFactorySource source) {
		return new ReportThrownKeg(playerId, targetPlayerId, roll, success, fumble);
	}
}
