package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportPassBlock extends NoDiceReport {

	private String fTeamId;
	private boolean fPassBlockAvailable;

	public ReportPassBlock() {
		super();
	}

	public ReportPassBlock(String pTeamId, boolean pPassBlockAvailable) {
		fTeamId = pTeamId;
		fPassBlockAvailable = pPassBlockAvailable;
	}

	public ReportId getId() {
		return ReportId.PASS_BLOCK;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public boolean isPassBlockAvailable() {
		return fPassBlockAvailable;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportPassBlock(getTeamId(), isPassBlockAvailable());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
		IJsonOption.PASS_BLOCK_AVAILABLE.addTo(jsonObject, fPassBlockAvailable);
		return jsonObject;
	}

	public ReportPassBlock initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		fPassBlockAvailable = IJsonOption.PASS_BLOCK_AVAILABLE.getFrom(source, jsonObject);
		return this;
	}

}
