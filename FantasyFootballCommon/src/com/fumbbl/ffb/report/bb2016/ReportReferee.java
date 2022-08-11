package com.fumbbl.ffb.report.bb2016;

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

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public class ReportReferee extends NoDiceReport {

	private boolean fFoulingPlayerBanned;

	public ReportReferee() {
		super();
	}

	public ReportReferee(boolean pFoulingPlayerBanned) {
		fFoulingPlayerBanned = pFoulingPlayerBanned;
	}

	public ReportId getId() {
		return ReportId.REFEREE;
	}

	public boolean isFoulingPlayerBanned() {
		return fFoulingPlayerBanned;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportReferee(isFoulingPlayerBanned());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.FOULING_PLAYER_BANNED.addTo(jsonObject, fFoulingPlayerBanned);
		return jsonObject;
	}

	public ReportReferee initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fFoulingPlayerBanned = IJsonOption.FOULING_PLAYER_BANNED.getFrom(source, jsonObject);
		return this;
	}

}
