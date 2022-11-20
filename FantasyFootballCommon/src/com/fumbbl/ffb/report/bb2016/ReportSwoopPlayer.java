package com.fumbbl.ffb.report.bb2016;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.DirectionFactory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.NoDiceReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;
import com.fumbbl.ffb.util.ArrayTool;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public class ReportSwoopPlayer extends NoDiceReport {

	private FieldCoordinate fStartCoordinate;
	private FieldCoordinate fEndCoordinate;
	private final List<Direction> fDirections;
	private final List<Integer> fRolls;

	public ReportSwoopPlayer() {
		fDirections = new ArrayList<>();
		fRolls = new ArrayList<>();
	}

	public ReportSwoopPlayer(FieldCoordinate pStartCoordinate, FieldCoordinate pEndCoordinate, Direction[] pDirections,
			int[] pRolls) {
		this();
		fStartCoordinate = pStartCoordinate;
		fEndCoordinate = pEndCoordinate;
		addDirections(pDirections);
		addRolls(pRolls);
	}

	public ReportId getId() {
		return ReportId.SWOOP_PLAYER;
	}

	public FieldCoordinate getStartCoordinate() {
		return fStartCoordinate;
	}

	public FieldCoordinate getEndCoordinate() {
		return fEndCoordinate;
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

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportSwoopPlayer(FieldCoordinate.transform(getStartCoordinate()),
				FieldCoordinate.transform(getEndCoordinate()), source.<DirectionFactory>getFactory(Factory.DIRECTION).transform(getDirections()), getRolls());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.START_COORDINATE.addTo(jsonObject, fStartCoordinate);
		IJsonOption.END_COORDINATE.addTo(jsonObject, fEndCoordinate);
		JsonArray directionArray = new JsonArray();
		for (Direction direction : getDirections()) {
			directionArray.add(UtilJson.toJsonValue(direction));
		}
		IJsonOption.DIRECTION_ARRAY.addTo(jsonObject, directionArray);
		IJsonOption.ROLLS.addTo(jsonObject, fRolls);
		return jsonObject;
	}

	public ReportSwoopPlayer initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fStartCoordinate = IJsonOption.START_COORDINATE.getFrom(source, jsonObject);
		fEndCoordinate = IJsonOption.END_COORDINATE.getFrom(source, jsonObject);
		JsonArray directionArray = IJsonOption.DIRECTION_ARRAY.getFrom(source, jsonObject);
		if (directionArray != null) {
			for (int i = 0; i < directionArray.size(); i++) {
				addDirection((Direction) UtilJson.toEnumWithName(source.<DirectionFactory>getFactory(Factory.DIRECTION), directionArray.get(i)));
			}
		}
		addRolls(IJsonOption.ROLLS.getFrom(source, jsonObject));
		return this;
	}

}
