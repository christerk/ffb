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

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportReferee implements IReport {

	private boolean fFoulingPlayerBanned, underScrutiny;

	public ReportReferee() {
		super();
	}

	public ReportReferee(boolean pFoulingPlayerBanned, boolean underScrutiny) {
		fFoulingPlayerBanned = pFoulingPlayerBanned;
		this.underScrutiny = underScrutiny;
	}

	public ReportId getId() {
		return ReportId.REFEREE;
	}

	public boolean isFoulingPlayerBanned() {
		return fFoulingPlayerBanned;
	}

	public boolean isUnderScrutiny() {
		return underScrutiny;
	}
// transformation

	public IReport transform(IFactorySource source) {
		return new ReportReferee(isFoulingPlayerBanned(), underScrutiny);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.FOULING_PLAYER_BANNED.addTo(jsonObject, fFoulingPlayerBanned);
		IJsonOption.UNDER_SCRUTINY.addTo(jsonObject, underScrutiny);
		return jsonObject;
	}

	public ReportReferee initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fFoulingPlayerBanned = IJsonOption.FOULING_PLAYER_BANNED.getFrom(game, jsonObject);
		underScrutiny = IJsonOption.UNDER_SCRUTINY.getFrom(game, jsonObject);
		return this;
	}

}
