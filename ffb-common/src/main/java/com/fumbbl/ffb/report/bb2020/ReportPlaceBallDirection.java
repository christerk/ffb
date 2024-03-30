package com.fumbbl.ffb.report.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.NoDiceReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportPlaceBallDirection extends NoDiceReport {

	private String playerId;
	private Direction direction;

	public ReportPlaceBallDirection() {
	}

	public ReportPlaceBallDirection(String playerId, Direction direction) {
		this.playerId = playerId;
		this.direction = direction;
	}

	public Direction getDirection() {
		return direction;
	}

	public String getPlayerId() {
		return playerId;
	}

	@Override
	public ReportPlaceBallDirection initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		direction = (Direction) IJsonOption.DIRECTION.getFrom(source, jsonObject);
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.DIRECTION.addTo(jsonObject, direction);
		return jsonObject;
	}

	@Override
	public ReportId getId() {
		return ReportId.PLACE_BALL_DIRECTION;
	}

	@Override
	public IReport transform(IFactorySource source) {
		return new ReportPlaceBallDirection(playerId, direction.transform());
	}
}
