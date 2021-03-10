package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ReportFreePettyCash implements IReport {

	private String fTeamId;
	private int fGold;

	public ReportFreePettyCash() {
		super();
	}

	public ReportFreePettyCash(String pTeamId, int pGold) {
		fTeamId = pTeamId;
		fGold = pGold;
	}

	public ReportId getId() {
		return ReportId.FREE_PETTY_CASH;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public int getGold() {
		return fGold;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportFreePettyCash(getTeamId(), getGold());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
		IJsonOption.GOLD.addTo(jsonObject, fGold);
		return jsonObject;
	}

	public ReportFreePettyCash initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		fGold = IJsonOption.GOLD.getFrom(game, jsonObject);
		return this;
	}

}
