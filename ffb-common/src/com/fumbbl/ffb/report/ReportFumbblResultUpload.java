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
public class ReportFumbblResultUpload extends NoDiceReport {

	private boolean fSuccessful;
	private String fUploadStatus;

	public ReportFumbblResultUpload() {
		super();
	}

	public ReportFumbblResultUpload(boolean pSuccessful, String pStatus) {
		fSuccessful = pSuccessful;
		fUploadStatus = pStatus;
	}

	public ReportId getId() {
		return ReportId.FUMBBL_RESULT_UPLOAD;
	}

	public boolean isSuccessful() {
		return fSuccessful;
	}

	public String getUploadStatus() {
		return fUploadStatus;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportFumbblResultUpload(isSuccessful(), getUploadStatus());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.SUCCESSFUL.addTo(jsonObject, fSuccessful);
		IJsonOption.UPLOAD_STATUS.addTo(jsonObject, fUploadStatus);
		return jsonObject;
	}

	public ReportFumbblResultUpload initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fSuccessful = IJsonOption.SUCCESSFUL.getFrom(source, jsonObject);
		fUploadStatus = IJsonOption.UPLOAD_STATUS.getFrom(source, jsonObject);
		return this;
	}

}
