package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.DirectionFactory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.util.ArrayTool;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportScatterBall extends NoDiceReport {

	private final List<Direction> fDirections;
	private final List<Integer> fRolls;
	private boolean fGustOfWind;

	public ReportScatterBall() {
		fDirections = new ArrayList<>();
		fRolls = new ArrayList<>();
	}

	public ReportScatterBall(Direction[] pDirections, int[] pRolls, boolean pGustOfWind) {
		this();
		addDirections(pDirections);
		addRolls(pRolls);
		fGustOfWind = pGustOfWind;
	}

	public ReportId getId() {
		return ReportId.SCATTER_BALL;
	}

	public Direction[] getDirections() {
		return fDirections.toArray(new Direction[fDirections.size()]);
	}

	private void addDirection(Direction pDirection) {
		if (pDirection != null) {
			fDirections.add(pDirection);
		}
	}

	private void addDirections(Direction[] pDirections) {
		if (ArrayTool.isProvided(pDirections)) {
			for (Direction direction : pDirections) {
				addDirection(direction);
			}
		}
	}

	public int[] getRolls() {
		int[] rolls = new int[fDirections.size()];
		for (int i = 0; i < rolls.length; i++) {
			rolls[i] = fRolls.get(i);
		}
		return rolls;
	}

	private void addRoll(int pRoll) {
		fRolls.add(pRoll);
	}

	private void addRolls(int[] pRolls) {
		if (ArrayTool.isProvided(pRolls)) {
			for (int roll : pRolls) {
				addRoll(roll);
			}
		}
	}

	public boolean isGustOfWind() {
		return fGustOfWind;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportScatterBall(source.<DirectionFactory>getFactory(Factory.DIRECTION).transform(getDirections()), getRolls(), isGustOfWind());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		JsonArray directionArray = new JsonArray();
		for (Direction direction : getDirections()) {
			directionArray.add(UtilJson.toJsonValue(direction));
		}
		IJsonOption.DIRECTION_ARRAY.addTo(jsonObject, directionArray);
		IJsonOption.ROLLS.addTo(jsonObject, fRolls);
		IJsonOption.GUST_OF_WIND.addTo(jsonObject, fGustOfWind);
		return jsonObject;
	}

	public ReportScatterBall initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		JsonArray directionArray = IJsonOption.DIRECTION_ARRAY.getFrom(source, jsonObject);
		if (directionArray != null) {
			for (int i = 0; i < directionArray.size(); i++) {
				addDirection((Direction) UtilJson.toEnumWithName(source.<DirectionFactory>getFactory(Factory.DIRECTION), directionArray.get(i)));
			}
		}
		addRolls(IJsonOption.ROLLS.getFrom(source, jsonObject));
		fGustOfWind = IJsonOption.GUST_OF_WIND.getFrom(source, jsonObject);
		return this;
	}

}
