package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportThrowIn implements IReport {

	private Direction fDirection;
	private int fDirectionRoll;
	private int[] fDistanceRoll;

	public ReportThrowIn() {
		super();
	}

	public ReportThrowIn(Direction pDirection, int pDirectionRoll, int[] pDistanceRoll) {
		fDirection = pDirection;
		fDirectionRoll = pDirectionRoll;
		fDistanceRoll = pDistanceRoll;
	}

	public ReportId getId() {
		return ReportId.THROW_IN;
	}

	public Direction getDirection() {
		return fDirection;
	}

	public int getDirectionRoll() {
		return fDirectionRoll;
	}

	public int[] getDistanceRoll() {
		return fDistanceRoll;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportThrowIn(getDirection().transform(), getDirectionRoll(), getDistanceRoll());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.DIRECTION.addTo(jsonObject, fDirection);
		IJsonOption.DIRECTION_ROLL.addTo(jsonObject, fDirectionRoll);
		IJsonOption.DISTANCE_ROLL.addTo(jsonObject, fDistanceRoll);
		return jsonObject;
	}

	public ReportThrowIn initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fDirection = (Direction) IJsonOption.DIRECTION.getFrom(source, jsonObject);
		fDirectionRoll = IJsonOption.DIRECTION_ROLL.getFrom(source, jsonObject);
		fDistanceRoll = IJsonOption.DISTANCE_ROLL.getFrom(source, jsonObject);
		return this;
	}

}
