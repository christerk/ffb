package com.fumbbl.ffb.report.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportCloudBurster implements IReport {

	private String throwerId, interceptorId, throwerTeamId;

	public ReportCloudBurster(String throwerId, String interceptorId, String throwerTeamId) {
		this.throwerId = throwerId;
		this.interceptorId = interceptorId;
		this.throwerTeamId = throwerTeamId;
	}

	public ReportCloudBurster() {
	}

	@Override
	public ReportCloudBurster initFrom(IFactorySource game, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		throwerId = IJsonOption.THROWER_ID.getFrom(game, jsonObject);
		interceptorId = IJsonOption.INTERCEPTOR_ID.getFrom(game, jsonObject);
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.THROWER_ID.addTo(jsonObject, throwerId);
		IJsonOption.INTERCEPTOR_ID.addTo(jsonObject, interceptorId);
		return jsonObject;
	}

	@Override
	public ReportId getId() {
		return ReportId.CLOUD_BURSTER;
	}

	@Override
	public IReport transform(IFactorySource source) {
		return new ReportCloudBurster(getThrowerId(), getInterceptorId(), getThrowerTeamId());
	}

	public String getThrowerTeamId() {
		return throwerTeamId;
	}

	public void setThrowerTeamId(String throwerTeamId) {
		this.throwerTeamId = throwerTeamId;
	}

	public String getThrowerId() {
		return throwerId;
	}

	public void setThrowerId(String throwerId) {
		this.throwerId = throwerId;
	}

	public String getInterceptorId() {
		return interceptorId;
	}

	public void setInterceptorId(String interceptorId) {
		this.interceptorId = interceptorId;
	}
}
