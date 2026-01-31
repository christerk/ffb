package com.fumbbl.ffb.report.bb2025;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.NoDiceReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportChompRemoved extends NoDiceReport {
	private String player;
	private boolean successful;

	@SuppressWarnings("unused")
	public ReportChompRemoved() {
	}

	public ReportChompRemoved(String player, boolean successful) {
		this.player = player;
		this.successful = successful;
	}

	@Override
	public ReportId getId() {
		return ReportId.CHOMP_REMOVED;
	}

	@Override
	public ReportChompRemoved transform(IFactorySource source) {
		return new ReportChompRemoved(player, successful);
	}

	public String getPlayer() {
		return player;
	}

	public boolean isSuccessful() {
		return successful;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, player);
		IJsonOption.SUCCESSFUL.addTo(jsonObject, successful);
		return jsonObject;
	}

	@Override
	public ReportChompRemoved initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));

		successful = IJsonOption.SUCCESSFUL.getFrom(source, UtilJson.toJsonObject(jsonValue));
		player = IJsonOption.PLAYER_ID.getFrom(source, UtilJson.toJsonObject(jsonValue));
		return this;
	}
}
