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

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportTwoForOne extends NoDiceReport {

	private String playerId, partnerId;
	private boolean used;

	public ReportTwoForOne() {
		super();
	}

	public ReportTwoForOne(String playerId, String partnerId, boolean used) {
		this.playerId = playerId;
		this.partnerId = partnerId;
		this.used = used;
	}

	public ReportId getId() {
		return ReportId.TWO_FOR_ONE;
	}

	public String getPlayerId() {
		return playerId;
	}

	public boolean isUsed() {
		return used;
	}

	public String getPartnerId() {
		return partnerId;
	}
// transformation

	public IReport transform(IFactorySource source) {
		return new ReportTwoForOne(getPlayerId(), partnerId, used);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.USED.addTo(jsonObject, used);
		IJsonOption.PARTNER_ID.addTo(jsonObject, partnerId);
		return jsonObject;
	}

	public ReportTwoForOne initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		used = IJsonOption.USED.getFrom(source, jsonObject);
		partnerId = IJsonOption.PARTNER_ID.getFrom(source, jsonObject);
		return this;
	}

}
