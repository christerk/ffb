package com.fumbbl.ffb.report.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportSwoopPlayer implements IReport {

	private FieldCoordinate startCoordinate;
	private FieldCoordinate endCoordinate;
	private Direction direction;
	private int distance;

	public ReportSwoopPlayer() {
	}

	public ReportSwoopPlayer(FieldCoordinate startCoordinate, FieldCoordinate endCoordinate, Direction direction, int distance) {
		this.startCoordinate = startCoordinate;
		this.endCoordinate = endCoordinate;
		this.direction = direction;
		this.distance = distance;
	}

	public ReportId getId() {
		return ReportId.SWOOP_PLAYER;
	}

	public FieldCoordinate getStartCoordinate() {
		return startCoordinate;
	}

	public FieldCoordinate getEndCoordinate() {
		return endCoordinate;
	}

	public Direction getDirection() {
		return direction;
	}

	public int getDistance() {
		return distance;
	}

// transformation

	public IReport transform(IFactorySource source) {
		return new ReportSwoopPlayer(FieldCoordinate.transform(getStartCoordinate()),
				FieldCoordinate.transform(getEndCoordinate()), direction.transform(), distance);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.START_COORDINATE.addTo(jsonObject, startCoordinate);
		IJsonOption.END_COORDINATE.addTo(jsonObject, endCoordinate);
		IJsonOption.DISTANCE.addTo(jsonObject, distance);
		IJsonOption.SCATTER_DIRECTION.addTo(jsonObject, direction);
		return jsonObject;
	}

	public ReportSwoopPlayer initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		startCoordinate = IJsonOption.START_COORDINATE.getFrom(source, jsonObject);
		endCoordinate = IJsonOption.END_COORDINATE.getFrom(source, jsonObject);
		distance = IJsonOption.DISTANCE.getFrom(source, jsonObject);
		direction = (Direction) IJsonOption.SCATTER_DIRECTION.getFrom(source, jsonObject);
		return this;
	}

}
