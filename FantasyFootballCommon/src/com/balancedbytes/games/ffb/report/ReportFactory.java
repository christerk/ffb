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
public class ReportFactory {

	// JSON serialization

	public IReport forJsonValue(Game game, JsonValue pJsonValue) {
		if ((pJsonValue == null) || pJsonValue.isNull()) {
			return null;
		}
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		ReportId reportId = (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject);
		return (IReport) reportId.createReport().initFrom(game, jsonObject);
	}

}
