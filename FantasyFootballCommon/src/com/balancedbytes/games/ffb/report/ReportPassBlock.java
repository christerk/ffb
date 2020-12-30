package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ReportPassBlock implements IReport {

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

	public IReport transform(Game game) {
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

	public ReportPassBlock initFrom(Game game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		fPassBlockAvailable = IJsonOption.PASS_BLOCK_AVAILABLE.getFrom(game, jsonObject);
		return this;
	}

}
