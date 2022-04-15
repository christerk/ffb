package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
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

	public ReportInducement initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		fInducementType = (InducementType) IJsonOption.INDUCEMENT_TYPE.getFrom(source, jsonObject);
		fValue = IJsonOption.VALUE.getFrom(source, jsonObject);
		return this;
	}

}
