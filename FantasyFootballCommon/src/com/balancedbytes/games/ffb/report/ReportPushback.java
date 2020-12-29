package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.PushbackMode;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ReportPushback implements IReport {

	private String fDefenderId;
	private PushbackMode fPushbackMode;

	public ReportPushback() {
		super();
	}

	public ReportPushback(String pDefenderId, PushbackMode pMode) {
		this();
		fDefenderId = pDefenderId;
		fPushbackMode = pMode;
	}

	public ReportId getId() {
		return ReportId.PUSHBACK;
	}

	public String getDefenderId() {
		return fDefenderId;
	}

	public PushbackMode getPushbackMode() {
		return fPushbackMode;
	}

	// transformation

	public IReport transform() {
		return new ReportPushback(getDefenderId(), getPushbackMode());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.DEFENDER_ID.addTo(jsonObject, fDefenderId);
		IJsonOption.PUSHBACK_MODE.addTo(jsonObject, fPushbackMode);
		return jsonObject;
	}

	public ReportPushback initFrom(Game game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fDefenderId = IJsonOption.DEFENDER_ID.getFrom(game, jsonObject);
		fPushbackMode = (PushbackMode) IJsonOption.PUSHBACK_MODE.getFrom(game, jsonObject);
		return this;
	}

}
