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
public class ReportReceiveChoice extends NoDiceReport {

	private String fTeamId;
	private boolean fReceiveChoice;

	public ReportReceiveChoice() {
		super();
	}

	public ReportReceiveChoice(String pTeamId, boolean pChoiceReceive) {
		fTeamId = pTeamId;
		fReceiveChoice = pChoiceReceive;
	}

	public ReportId getId() {
		return ReportId.RECEIVE_CHOICE;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public boolean isReceiveChoice() {
		return fReceiveChoice;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportReceiveChoice(getTeamId(), isReceiveChoice());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
		IJsonOption.RECEIVE_CHOICE.addTo(jsonObject, fReceiveChoice);
		return jsonObject;
	}

	public ReportReceiveChoice initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		fReceiveChoice = IJsonOption.RECEIVE_CHOICE.getFrom(source, jsonObject);
		return this;
	}

}
