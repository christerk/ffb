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
public class ReportFactory {

	// JSON serialization

	public IReport forJsonValue(IFactorySource source, JsonValue pJsonValue) {
		if ((pJsonValue == null) || pJsonValue.isNull()) {
			return null;
		}
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		ReportId reportId = (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject);
		return (IReport) reportId.createReport().initFrom(source, jsonObject);
	}

}
