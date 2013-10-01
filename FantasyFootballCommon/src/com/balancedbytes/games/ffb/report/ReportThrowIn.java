package com.balancedbytes.games.ffb.report;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.Direction;
import com.balancedbytes.games.ffb.DirectionFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.xml.UtilXml;


/**
 * 
 * @author Kalimar
 */
public class ReportThrowIn implements IReport {
  
  private static final String _XML_ATTRIBUTE_DIRECTION = "direction";
  private static final String _XML_ATTRIBUTE_DIRECTION_ROLL = "directionRoll";
  private static final String _XML_ATTRIBUTE_DISTANCE_ROLL = "distanceRoll";
  
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
  
  public IReport transform() {
    return new ReportThrowIn(new DirectionFactory().transform(getDirection()), getDirectionRoll(), getDistanceRoll());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_DIRECTION, (getDirection() != null) ? getDirection().getName() : null);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_DIRECTION_ROLL, getDirectionRoll());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_DISTANCE_ROLL, getDistanceRoll());
    UtilXml.addEmptyElement(pHandler, XML_TAG, attributes);
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
    pByteList.addByte((byte) getDirection().getId());
    pByteList.addByte((byte) getDirectionRoll());
    pByteList.addByteArray(getDistanceRoll());
  }
  
  public int initFrom(ByteArray pByteArray) {
    ReportId reportId = ReportId.fromId(pByteArray.getSmallInt());
    if (getId() != reportId) {
      throw new IllegalStateException("Wrong report id. Expected " + getId().getName() + " received " + ((reportId != null) ? reportId.getName() : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fDirection = new DirectionFactory().forId(pByteArray.getByte());
    fDirectionRoll = pByteArray.getByte();
    fDistanceRoll = pByteArray.getByteArrayAsIntArray();
    return byteArraySerializationVersion;
  }
    
}
