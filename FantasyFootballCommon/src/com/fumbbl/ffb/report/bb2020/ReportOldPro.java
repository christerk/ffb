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
public class ReportOldPro implements IReport {

	private String playerId;
	private int oldValue, newValue;
	private boolean selfInflicted;

	public ReportOldPro() {
	}

	public ReportOldPro(String playerId, int oldValue, int newValue, boolean selfInflicted) {
		this.playerId = playerId;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.selfInflicted = selfInflicted;
	}

	public String getPlayerId() {
		return playerId;
	}

	public int getOldValue() {
		return oldValue;
	}

	public int getNewValue() {
		return newValue;
	}

	public boolean isSelfInflicted() {
		return selfInflicted;
	}

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.OLD_ROLL.addTo(jsonObject, oldValue);
		IJsonOption.ROLL.addTo(jsonObject, newValue);
		IJsonOption.SELF_INFLICTED.addTo(jsonObject, selfInflicted);
		return jsonObject;
	}

	public ReportOldPro initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		playerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		oldValue = IJsonOption.OLD_ROLL.getFrom(game, jsonObject);
		newValue = IJsonOption.ROLL.getFrom(game, jsonObject);
		selfInflicted = IJsonOption.SELF_INFLICTED.getFrom(game, jsonObject);
		return this;
	}

	@Override
	public ReportId getId() {
		return ReportId.OLD_PRO;
	}

	@Override
	public IReport transform(IFactorySource source) {
		return new ReportOldPro(playerId, oldValue, newValue, selfInflicted);
	}
}
