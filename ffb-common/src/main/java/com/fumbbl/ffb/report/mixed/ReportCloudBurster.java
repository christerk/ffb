package com.fumbbl.ffb.report.mixed;

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

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportCloudBurster extends NoDiceReport {

	private String throwerId, interceptorId, throwerTeamId;

	public ReportCloudBurster(String throwerId, String interceptorId, String throwerTeamId) {
		this.throwerId = throwerId;
		this.interceptorId = interceptorId;
		this.throwerTeamId = throwerTeamId;
	}

	public ReportCloudBurster() {
	}

	@Override
	public ReportCloudBurster initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		throwerId = IJsonOption.THROWER_ID.getFrom(source, jsonObject);
		interceptorId = IJsonOption.INTERCEPTOR_ID.getFrom(source, jsonObject);
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
