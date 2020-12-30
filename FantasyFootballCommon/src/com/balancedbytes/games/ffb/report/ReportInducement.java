package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ReportInducement implements IReport {

	private String fTeamId;
	private InducementType fInducementType;
	private int fValue;

	public ReportInducement() {
		super();
	}

	public ReportInducement(String pTeamId, InducementType pType, int pValue) {
		fTeamId = pTeamId;
		fInducementType = pType;
		fValue = pValue;
	}

	public ReportId getId() {
		return ReportId.INDUCEMENT;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public InducementType getInducementType() {
		return fInducementType;
	}

	public int getValue() {
		return fValue;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportInducement(getTeamId(), getInducementType(), getValue());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
		IJsonOption.INDUCEMENT_TYPE.addTo(jsonObject, fInducementType);
		IJsonOption.VALUE.addTo(jsonObject, fValue);
		return jsonObject;
	}

	public ReportInducement initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		fInducementType = (InducementType) IJsonOption.INDUCEMENT_TYPE.getFrom(game, jsonObject);
		fValue = IJsonOption.VALUE.getFrom(game, jsonObject);
		return this;
	}

}
