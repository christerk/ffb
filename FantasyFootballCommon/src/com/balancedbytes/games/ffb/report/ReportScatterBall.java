package com.balancedbytes.games.ffb.report;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.Direction;
import com.balancedbytes.games.ffb.DirectionFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
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
public class ReportScatterBall implements IReport {
  
  private List<Direction> fDirections;
  private List<Integer> fRolls;
  private boolean fGustOfWind;
  
  public ReportScatterBall() {
    fDirections = new ArrayList<Direction>();
    fRolls = new ArrayList<Integer>();
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
  
  public IReport transform() {
    return new ReportScatterBall(new DirectionFactory().transform(getDirections()), getRolls(), isGustOfWind());
  }
  
  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    int nrOfDirections = pByteArray.getByte();
    DirectionFactory directionFactory = new DirectionFactory();
    for (int i = 0; i < nrOfDirections; i++) {
      addDirection(directionFactory.forId(pByteArray.getByte()));
    }
    addRolls(pByteArray.getByteArrayAsIntArray());
    fGustOfWind = pByteArray.getBoolean();
    return byteArraySerializationVersion;
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
  
  public ReportScatterBall initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    JsonArray directionArray = IJsonOption.DIRECTION_ARRAY.getFrom(jsonObject);
    if (directionArray != null) {
      for (int i = 0; i < directionArray.size(); i++) {
        addDirection((Direction) UtilJson.toEnumWithName(new DirectionFactory(), directionArray.get(i)));
      }
    }
    addRolls(IJsonOption.ROLLS.getFrom(jsonObject));
    fGustOfWind = IJsonOption.GUST_OF_WIND.getFrom(jsonObject);
    return this;
  }
  
}
