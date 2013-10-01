package com.balancedbytes.games.ffb.net.commands;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.xml.UtilXml;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandMove extends NetCommand implements ICommandWithActingPlayer {

  private static final String _XML_TAG_COORDINATE_FROM = "coordinateFrom";
  private static final String _XML_TAG_COORDINATE_TO = "coordinateTo";
  private static final String _XML_ATTRIBUTE_X = "x";
  private static final String _XML_ATTRIBUTE_Y = "y";
  private static final String _XML_ATTRIBUTE_ACTING_PLAYER_ID = "actingPlayerId";

  private String fActingPlayerId;
  private FieldCoordinate fCoordinateFrom;
  private List<FieldCoordinate> fCoordinatesTo;

  public ClientCommandMove() {
    fCoordinatesTo = new ArrayList<FieldCoordinate>();
  }

  public ClientCommandMove(String pActingPlayerId, FieldCoordinate pCoordinateFrom, FieldCoordinate[] pCoordinatesTo) {
    this();
    fActingPlayerId = pActingPlayerId;
    fCoordinateFrom = pCoordinateFrom;
    add(pCoordinatesTo);
  }

  public NetCommandId getId() {
    return NetCommandId.CLIENT_MOVE;
  }

  public String getActingPlayerId() {
    return fActingPlayerId;
  }

  private void add(FieldCoordinate pCoordinate) {
    if (pCoordinate != null) {
      fCoordinatesTo.add(pCoordinate);
    }
  }

  private void add(FieldCoordinate[] pCoordinates) {
    if (ArrayTool.isProvided(pCoordinates)) {
      for (FieldCoordinate coordinate : pCoordinates) {
        add(coordinate);
      }
    }
  }

  public FieldCoordinate[] getCoordinatesTo() {
    return fCoordinatesTo.toArray(new FieldCoordinate[fCoordinatesTo.size()]);
  }

  public FieldCoordinate getCoordinateFrom() {
    return fCoordinateFrom;
  }

  // XML serialization

  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ACTING_PLAYER_ID, getActingPlayerId());
    UtilXml.startElement(pHandler, getId().getName(), attributes);
    if (getCoordinateFrom() != null) {
      attributes = new AttributesImpl();
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_X, getCoordinateFrom().getX());
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_Y, getCoordinateFrom().getY());
      UtilXml.addEmptyElement(pHandler, _XML_TAG_COORDINATE_FROM, attributes);
    }
    FieldCoordinate[] coordinatesTo = getCoordinatesTo();
    if (ArrayTool.isProvided(coordinatesTo)) {
      for (FieldCoordinate coordinate : coordinatesTo) {
        attributes = new AttributesImpl();
        UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_X, coordinate.getX());
        UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_Y, coordinate.getY());
        UtilXml.addEmptyElement(pHandler, _XML_TAG_COORDINATE_TO, attributes);
      }
    }
    UtilXml.endElement(pHandler, getId().getName());
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  // ByteArray serialization

  public int getByteArraySerializationVersion() {
    return 2;
  }

  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getActingPlayerId());
    pByteList.addFieldCoordinate(getCoordinateFrom());
    FieldCoordinate[] coordinatesTo = getCoordinatesTo();
    pByteList.addByte((byte) coordinatesTo.length);
    for (FieldCoordinate coordinate : coordinatesTo) {
      pByteList.addFieldCoordinate(coordinate);
    }
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    if (byteArraySerializationVersion > 1) {
      fActingPlayerId = pByteArray.getString();
      fCoordinateFrom = pByteArray.getFieldCoordinate();
    }
    int nrOfCoordinates = pByteArray.getByte();
    for (int i = 0; i < nrOfCoordinates; i++) {
      add(pByteArray.getFieldCoordinate());
    }
    return byteArraySerializationVersion;
  }

}
