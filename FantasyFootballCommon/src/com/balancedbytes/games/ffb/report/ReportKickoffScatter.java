package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.Direction;
import com.balancedbytes.games.ffb.DirectionFactory;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public class ReportKickoffScatter implements IReport {
  
  private FieldCoordinate fBallCoordinateEnd;
  private Direction fScatterDirection;
  private int fRollScatterDirection;
  private int fRollScatterDistance;
  
  public ReportKickoffScatter() {
    super();
  }
  
  public ReportKickoffScatter(
    FieldCoordinate pBallCoordinateEnd,
    Direction pScatterDirection,
    int pRollScatterDirection,
    int pRollScatterDistance
  ) {
    fBallCoordinateEnd = pBallCoordinateEnd;
    fScatterDirection = pScatterDirection;
    fRollScatterDirection = pRollScatterDirection;
    fRollScatterDistance = pRollScatterDistance;
  }
  
  public ReportId getId() {
    return ReportId.KICKOFF_SCATTER;
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
  
  // transformation
  
  public IReport transform() {
    return new ReportKickoffScatter(FieldCoordinate.transform(getBallCoordinateEnd()), new DirectionFactory().transform(getScatterDirection()), getRollScatterDirection(), getRollScatterDistance());
  }
  
  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fBallCoordinateEnd = pByteArray.getFieldCoordinate();
    fScatterDirection = new DirectionFactory().forId(pByteArray.getByte());
    fRollScatterDirection = pByteArray.getByte();
    fRollScatterDistance = pByteArray.getByte();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.BALL_COORDINATE_END.addTo(jsonObject, fBallCoordinateEnd);
    IJsonOption.SCATTER_DIRECTION.addTo(jsonObject, fScatterDirection);
    IJsonOption.ROLL_SCATTER_DIRECTION.addTo(jsonObject, fRollScatterDirection);
    IJsonOption.ROLL_SCATTER_DISTANCE.addTo(jsonObject, fRollScatterDistance);
    return jsonObject;
  }
  
  public ReportKickoffScatter initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fBallCoordinateEnd = IJsonOption.BALL_COORDINATE_END.getFrom(jsonObject);
    fScatterDirection = (Direction) IJsonOption.SCATTER_DIRECTION.getFrom(jsonObject);
    fRollScatterDirection = IJsonOption.ROLL_SCATTER_DIRECTION.getFrom(jsonObject);
    fRollScatterDistance = IJsonOption.ROLL_SCATTER_DISTANCE.getFrom(jsonObject);
    return this;
  }

}
