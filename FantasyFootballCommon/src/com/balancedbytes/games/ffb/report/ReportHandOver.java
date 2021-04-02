package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportHandOver implements IReport {

	private String fCatcherId;

	public ReportHandOver() {
		super();
	}

	public ReportHandOver(String pCatcherId) {
		fCatcherId = pCatcherId;
	}

	public ReportId getId() {
		return ReportId.HAND_OVER;
	}

	public String getCatcherId() {
		return fCatcherId;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportHandOver(getCatcherId());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.CATCHER_ID.addTo(jsonObject, fCatcherId);
		return jsonObject;
	}

	public ReportHandOver initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fCatcherId = IJsonOption.CATCHER_ID.getFrom(game, jsonObject);
		return this;
	}

}
