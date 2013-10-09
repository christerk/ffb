package com.balancedbytes.games.ffb.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.Direction;
import com.balancedbytes.games.ffb.DirectionFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.xml.UtilXml;


/**
 * 
 * @author Kalimar
 */
public class ReportScatterBall implements IReport {
  
  private static final String _XML_ATTRIBUTE_GUST_OF_WIND = "gustOfWind";
  private static final String _XML_ATTRIBUTE_DIRECTION = "direction";
  private static final String _XML_ATTRIBUTE_ROLL = "rolls";

  private static final String _XML_TAG_SCATTER = "scatter";

  private List<Direction> fDirections;
  private List<Integer> fRolls;
  private boolean fGustOfWind;
  
  public ReportScatterBall() {
    fDirections = new ArrayList<Direction>();
    fRolls = new ArrayList<Integer>();
  }
  
  public ReportScatterBall(Direction[] pDirections, int[] pRolls, boolean pGustOfWind) {
    this();
    add(pDirections);
    add(pRolls);
    fGustOfWind = pGustOfWind;
  }

  public ReportId getId() {
    return ReportId.SCATTER_BALL;
  }
  
  public Direction[] getDirections() {
    return fDirections.toArray(new Direction[fDirections.size()]);
  }
  
  private void add(Direction pDirection) {
    if (pDirection != null) {
      fDirections.add(pDirection);
    }
  }
  
  private void add(Direction[] pDirections) {
    if (ArrayTool.isProvided(pDirections)) {
      for (Direction direction : pDirections) {
        add(direction);
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
  
  private void add(int pRoll) {
    fRolls.add(pRoll);
  }
  
  private void add(int[] pRolls) {
    if (ArrayTool.isProvided(pRolls)) {
      for (int roll : pRolls) {
        add(roll);
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
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_GUST_OF_WIND, isGustOfWind());
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    int[] rolls = getRolls();
    Direction[] directions = getDirections();
    if (ArrayTool.isProvided(directions) && ArrayTool.isProvided(rolls)) {
      for (int i = 0; i < directions.length; i++) {
        attributes = new AttributesImpl();
        UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_DIRECTION, (directions[i] != null) ? directions[i].getName() : null);
        UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL, rolls[i]);
        UtilXml.addEmptyElement(pHandler, _XML_TAG_SCATTER, attributes);
      }
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
    Direction[] directions = getDirections();
    pByteList.addByte((byte) directions.length);
    if (ArrayTool.isProvided(directions)) {
      for (Direction direction : directions) {
        pByteList.addByte((byte) direction.getId());
      }
    }
    pByteList.addByteArray(getRolls());
    pByteList.addBoolean(isGustOfWind());
  }

  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    int nrOfDirections = pByteArray.getByte();
    DirectionFactory directionFactory = new DirectionFactory();
    for (int i = 0; i < nrOfDirections; i++) {
      add(directionFactory.forId(pByteArray.getByte()));
    }
    add(pByteArray.getByteArrayAsIntArray());
    fGustOfWind = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
  
}
