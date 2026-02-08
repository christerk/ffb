package com.fumbbl.ffb.report.bb2025;

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

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportPuntDirection extends NoDiceReport {

	private Direction direction;
	private int directionRoll;
	private String playerId;
	private boolean outOfBounds;

	@SuppressWarnings("unused")
	public ReportPuntDirection() {
		super();
	}

	public ReportPuntDirection(Direction pDirection, int pDirectionRoll, String playerId, boolean outOfBounds) {
		direction = pDirection;
		directionRoll = pDirectionRoll;
		this.playerId = playerId;
		this.outOfBounds = outOfBounds;
	}

	public ReportId getId() {
		return ReportId.PUNT_DIRECTION_ROLL;
	}

	public Direction getDirection() {
		return direction;
	}

	public int getDirectionRoll() {
		return directionRoll;
	}

	public String getPlayerId() {
		return playerId;
	}

	public boolean isOutOfBounds() {
		return outOfBounds;
	}
// transformation

	public IReport transform(IFactorySource source) {
		return new ReportPuntDirection(getDirection().transform(), getDirectionRoll(), playerId, outOfBounds);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.DIRECTION.addTo(jsonObject, direction);
		IJsonOption.DIRECTION_ROLL.addTo(jsonObject, directionRoll);
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.OUT_OF_BOUNDS.addTo(jsonObject, outOfBounds);
		return jsonObject;
	}

	public ReportPuntDirection initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		direction = (Direction) IJsonOption.DIRECTION.getFrom(source, jsonObject);
		directionRoll = IJsonOption.DIRECTION_ROLL.getFrom(source, jsonObject);
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		outOfBounds = IJsonOption.OUT_OF_BOUNDS.getFrom(source, jsonObject);
		return this;
	}

}
