package com.fumbbl.ffb.report.mixed;

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
@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportOldPro extends NoDiceReport {

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

	public ReportOldPro initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		oldValue = IJsonOption.OLD_ROLL.getFrom(source, jsonObject);
		newValue = IJsonOption.ROLL.getFrom(source, jsonObject);
		selfInflicted = IJsonOption.SELF_INFLICTED.getFrom(source, jsonObject);
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
