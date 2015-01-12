package com.balancedbytes.games.ffb.report;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.Direction;
import com.balancedbytes.games.ffb.DirectionFactory;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public class ReportScatterPlayer implements IReport {
  
  private FieldCoordinate fStartCoordinate;
  private FieldCoordinate fEndCoordinate;
  private List<Direction> fDirections;
  private List<Integer> fRolls;
  
  public ReportScatterPlayer() {
    fDirections = new ArrayList<Direction>();
    fRolls = new ArrayList<Integer>();
  }
  
  public ReportScatterPlayer(FieldCoordinate pStartCoordinate, FieldCoordinate pEndCoordinate, Direction[] pDirections, int[] pRolls) {
    this();
    fStartCoordinate = pStartCoordinate;
    fEndCoordinate = pEndCoordinate;
    addDirections(pDirections);
    addRolls(pRolls);
  }

  public ReportId getId() {
    return ReportId.SCATTER_PLAYER;
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
  
  public IReport transform() {
    return new ReportScatterPlayer(FieldCoordinate.transform(getStartCoordinate()), FieldCoordinate.transform(getEndCoordinate()), new DirectionFactory().transform(getDirections()), getRolls());
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
  
  public ReportScatterPlayer initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fStartCoordinate = IJsonOption.START_COORDINATE.getFrom(jsonObject);
    fEndCoordinate = IJsonOption.END_COORDINATE.getFrom(jsonObject);
    JsonArray directionArray = IJsonOption.DIRECTION_ARRAY.getFrom(jsonObject);
    if (directionArray != null) {
      for (int i = 0; i < directionArray.size(); i++) {
        addDirection((Direction) UtilJson.toEnumWithName(new DirectionFactory(), directionArray.get(i)));
      }
    }
    addRolls(IJsonOption.ROLLS.getFrom(jsonObject));
    return this;
  }
  
}
