package com.balancedbytes.games.ffb.report;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.Direction;
import com.balancedbytes.games.ffb.DirectionFactory;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.xml.UtilXml;


/**
 * 
 * @author Kalimar
 */
public class ReportKickoffScatter implements IReport {
  
  private static final String _XML_ATTRIBUTE_X = "x";
  private static final String _XML_ATTRIBUTE_Y = "y";
  private static final String _XML_ATTRIBUTE_SCATTER_DIRECTION = "scatterDirection";
  private static final String _XML_ATTRIBUTE_ROLL_SCATTER_DIRECTION = "rollScatterDirection";
  private static final String _XML_ATTRIBUTE_ROLL_SCATTER_DISTANCE = "rollScatterDistance";

  private static final String _XML_TAG_BALL_COORDINATE_END = "ballCoordinateEnd";
  
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
  
  // XML serialization

  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    String scatterDirectionName = (getScatterDirection() != null) ? getScatterDirection().getName() : null;
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SCATTER_DIRECTION, scatterDirectionName);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL_SCATTER_DIRECTION, getRollScatterDirection());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL_SCATTER_DISTANCE, getRollScatterDistance());
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    if (getBallCoordinateEnd() != null) {
      attributes = new AttributesImpl();
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_X, getBallCoordinateEnd().getX());
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_Y, getBallCoordinateEnd().getY());
      UtilXml.addEmptyElement(pHandler, _XML_TAG_BALL_COORDINATE_END, attributes);
    }
    UtilXml.endElement(pHandler, XML_TAG);
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
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
