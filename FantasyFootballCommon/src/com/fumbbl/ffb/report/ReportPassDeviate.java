package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportPassDeviate extends NoDiceReport {

	private FieldCoordinate fBallCoordinateEnd;
	private Direction fScatterDirection;
	private int fRollScatterDirection;
	private int fRollScatterDistance;
	private boolean ttm;

	public ReportPassDeviate() {
		super();
	}

	public ReportPassDeviate(FieldCoordinate pBallCoordinateEnd, Direction pScatterDirection,
	                         int pRollScatterDirection, int pRollScatterDistance, boolean ttm) {
		fBallCoordinateEnd = pBallCoordinateEnd;
		fScatterDirection = pScatterDirection;
		fRollScatterDirection = pRollScatterDirection;
		fRollScatterDistance = pRollScatterDistance;
		this.ttm = ttm;
	}

	public ReportId getId() {
		return ReportId.PASS_DEVIATE;
	}

	public FieldCoordinate getBallCoordinateEnd() {
		return fBallCoordinateEnd;
	}

	public Direction getScatterDirection() {
		return fScatterDirection;
	}

	public int getRollScatterDirection() {
		return fRollScatterDirection;
	}

	public int getRollScatterDistance() {
		return fRollScatterDistance;
	}

	public boolean isTtm() {
		return ttm;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportPassDeviate(FieldCoordinate.transform(getBallCoordinateEnd()),
			getScatterDirection().transform(), getRollScatterDirection(), getRollScatterDistance(), ttm);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.BALL_COORDINATE_END.addTo(jsonObject, fBallCoordinateEnd);
		IJsonOption.SCATTER_DIRECTION.addTo(jsonObject, fScatterDirection);
		IJsonOption.ROLL_SCATTER_DIRECTION.addTo(jsonObject, fRollScatterDirection);
		IJsonOption.ROLL_SCATTER_DISTANCE.addTo(jsonObject, fRollScatterDistance);
		IJsonOption.THROW_TEAM_MATE.addTo(jsonObject, ttm);
		return jsonObject;
	}

	public ReportPassDeviate initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fBallCoordinateEnd = IJsonOption.BALL_COORDINATE_END.getFrom(source, jsonObject);
		fScatterDirection = (Direction) IJsonOption.SCATTER_DIRECTION.getFrom(source, jsonObject);
		fRollScatterDirection = IJsonOption.ROLL_SCATTER_DIRECTION.getFrom(source, jsonObject);
		fRollScatterDistance = IJsonOption.ROLL_SCATTER_DISTANCE.getFrom(source, jsonObject);
		ttm = IJsonOption.THROW_TEAM_MATE.getFrom(source, jsonObject);
		return this;
	}

}
