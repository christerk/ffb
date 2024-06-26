package com.fumbbl.ffb.report.bb2020;

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
@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportShowStarReRoll extends NoDiceReport {

	private String player;

	public ReportShowStarReRoll() {
		super();
	}

	public ReportShowStarReRoll(String player) {
		this.player = player;
	}

	public ReportId getId() {
		return ReportId.SHOW_STAR_RE_ROLL;
	}

	public String getPlayerId() {
		return player;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportShowStarReRoll(player);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, player);
		return jsonObject;
	}

	public ReportShowStarReRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		player = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		return this;
	}

}
