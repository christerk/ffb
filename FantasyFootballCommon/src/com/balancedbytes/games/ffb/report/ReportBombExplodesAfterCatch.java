package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

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
	public ReportBombExplodesAfterCatch initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		roll = IJsonOption.ROLL.getFrom(game, jsonObject);
		catcherId = IJsonOption.CATCHER_ID.getFrom(game, jsonObject);
		explodes = IJsonOption.EXPLODES.getFrom(game, jsonObject);
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
