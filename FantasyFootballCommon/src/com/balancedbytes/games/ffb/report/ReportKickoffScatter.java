package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.Direction;
import com.balancedbytes.games.ffb.DirectionFactory;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;


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
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addFieldCoordinate(getBallCoordinateEnd());
    pByteList.addByte((byte) getScatterDirection().getId());
    pByteList.addByte((byte) getRollScatterDirection());
    pByteList.addByte((byte) getRollScatterDistance());
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fBallCoordinateEnd = pByteArray.getFieldCoordinate();
    fScatterDirection = new DirectionFactory().forId(pByteArray.getByte());
    fRollScatterDirection = pByteArray.getByte();
    fRollScatterDistance = pByteArray.getByte();
    return byteArraySerializationVersion;
  }

}
