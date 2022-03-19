package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportBombExplodesAfterCatch implements IReport {
	private String catcherId;
	private boolean explodes;
	private int roll;

	public ReportBombExplodesAfterCatch() {
	}

	public ReportBombExplodesAfterCatch(String catcherId, boolean explodes, int roll) {
		this.catcherId = catcherId;
		this.explodes = explodes;
		this.roll = roll;
	}

	@Override
	public ReportBombExplodesAfterCatch initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		roll = IJsonOption.ROLL.getFrom(source, jsonObject);
		catcherId = IJsonOption.CATCHER_ID.getFrom(source, jsonObject);
		explodes = IJsonOption.EXPLODES.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.ROLL.addTo(jsonObject, roll);
		IJsonOption.CATCHER_ID.addTo(jsonObject, catcherId);
		IJsonOption.EXPLODES.addTo(jsonObject, explodes);
		return jsonObject;
	}

	@Override
	public ReportId getId() {
		return ReportId.BOMB_EXPLODES_AFTER_CATCH;
	}

	@Override
	public IReport transform(IFactorySource source) {
		return new ReportBombExplodesAfterCatch(catcherId, explodes, roll);
	}

	public String getCatcherId() {
		return catcherId;
	}

	public boolean explodes() {
		return explodes;
	}

	public int getRoll() {
		return roll;
	}
}
