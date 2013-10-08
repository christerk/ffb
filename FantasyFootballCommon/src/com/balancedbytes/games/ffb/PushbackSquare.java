package com.balancedbytes.games.ffb;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.xml.IXmlReadable;
import com.balancedbytes.games.ffb.xml.IXmlSerializable;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public final class PushbackSquare implements IXmlSerializable, IByteArraySerializable {

  public static final String XML_TAG = "pushback-square";
  
  private static final String _XML_ATTRIBUTE_X = "x";
  private static final String _XML_ATTRIBUTE_Y = "y";
  private static final String _XML_ATTRIBUTE_DIRECTION = "direction";
  private static final String _XML_ATTRIBUTE_SELECTED = "selected";
  private static final String _XML_ATTRIBUTE_LOCKED = "locked";
  private static final String _XML_ATTRIBUTE_HOME_CHOICE = "homeChoice";
  
  private static final String _XML_TAG_COORDINATE = "coordinate";

  private FieldCoordinate fCoordinate;
  private Direction fDirection;
  private boolean fSelected;
  private boolean fLocked;
  private boolean fHomeChoice;

  public PushbackSquare() {
    super();
  }
  
  public PushbackSquare(FieldCoordinate pCoordinate, Direction pDirection, boolean pHomeChoice) {
    if (pCoordinate == null) {
      throw new IllegalArgumentException("Parameter coordinate must not be null.");
    }
    fCoordinate = pCoordinate;
    fDirection = pDirection;
    fHomeChoice = pHomeChoice;
    fLocked = false;
  }
  
  public FieldCoordinate getCoordinate() {
    return fCoordinate;
  }
  
  public Direction getDirection() {
    return fDirection;
  }
  
  public boolean isSelected() {
    return fSelected;
  }
  
  public void setSelected(boolean pSelected) {
    fSelected = pSelected;
  }

  public void setLocked(boolean pLocked) {
    fLocked = pLocked;
  }

  public boolean isLocked() {
    return fLocked;
  }
  
  public void setHomeChoice(boolean pHomeChoice) {
    fHomeChoice = pHomeChoice;
  }
  
  public boolean isHomeChoice() {
    return fHomeChoice;
  }
    
  public PushbackSquare transform() {
    FieldCoordinate transformedCoordinate = getCoordinate().transform();
    Direction transformedDirection = new DirectionFactory().transform(getDirection());
    PushbackSquare transformedPushback = new PushbackSquare(transformedCoordinate, transformedDirection, !isHomeChoice());
    transformedPushback.setSelected(isSelected());
    transformedPushback.setLocked(isLocked());
    return transformedPushback;
  }
  
  public static PushbackSquare transform(PushbackSquare pPushbackSquare) {
    return (pPushbackSquare != null) ? pPushbackSquare.transform() : null;
  }
  
  public int hashCode() {
    return getCoordinate().hashCode();
  }
  
  public boolean equals(Object pObj) {
    return (
      (pObj instanceof PushbackSquare)
      && getCoordinate().equals(((PushbackSquare) pObj).getCoordinate())
    );
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_DIRECTION, (getDirection() != null) ? getDirection().getName() : null);
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SELECTED, isSelected());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_LOCKED, isLocked());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_HOME_CHOICE, isHomeChoice());
  	UtilXml.startElement(pHandler, XML_TAG, attributes);
  	
  	if (getCoordinate() != null) {
  		attributes = new AttributesImpl();
  		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_X, getCoordinate().getX());
  		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_Y, getCoordinate().getY());
  		UtilXml.startElement(pHandler, _XML_TAG_COORDINATE, attributes);
  		UtilXml.endElement(pHandler, _XML_TAG_COORDINATE);
  	}
  	
  	UtilXml.endElement(pHandler, XML_TAG);
  	
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  public IXmlReadable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
  	IXmlReadable xmlElement = this;
    if (XML_TAG.equals(pXmlTag)) {
      fDirection = new DirectionFactory().forName(UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_DIRECTION));
      fSelected = UtilXml.getBooleanAttribute(pXmlAttributes, _XML_ATTRIBUTE_SELECTED);
      fLocked = UtilXml.getBooleanAttribute(pXmlAttributes, _XML_ATTRIBUTE_LOCKED);
      fHomeChoice = UtilXml.getBooleanAttribute(pXmlAttributes, _XML_ATTRIBUTE_HOME_CHOICE);
    }
    if (_XML_TAG_COORDINATE.equals(pXmlTag)) {
      int x = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_X);
      int y = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_Y);
      fCoordinate = new FieldCoordinate(x, y);
    }
    return xmlElement;
  }
  
  public boolean endXmlElement(String pXmlTag, String pValue) {
    return XML_TAG.equals(pXmlTag);
  }
  
  // ByteArray serialization

  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addFieldCoordinate(getCoordinate());
    pByteList.addByte((byte) getDirection().getId());
    pByteList.addBoolean(isSelected());
    pByteList.addBoolean(isLocked());
    pByteList.addBoolean(isHomeChoice());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fCoordinate = pByteArray.getFieldCoordinate();
    fDirection = new DirectionFactory().forId(pByteArray.getByte());
    fSelected = pByteArray.getBoolean();
    fLocked = pByteArray.getBoolean();
    fHomeChoice = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.COORDINATE.addTo(jsonObject, fCoordinate);
    IJsonOption.DIRECTION.addTo(jsonObject, fDirection);
    IJsonOption.SELECTED.addTo(jsonObject, fSelected);
    IJsonOption.LOCKED.addTo(jsonObject, fLocked);
    IJsonOption.HOME_CHOICE.addTo(jsonObject, fHomeChoice);
    return jsonObject;
  }
  
  public PushbackSquare initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fCoordinate = IJsonOption.COORDINATE.getFrom(jsonObject);
    fDirection = (Direction) IJsonOption.DIRECTION.getFrom(jsonObject);
    fSelected = IJsonOption.SELECTED.getFrom(jsonObject);
    fLocked = IJsonOption.LOCKED.getFrom(jsonObject);
    fHomeChoice = IJsonOption.HOME_CHOICE.getFrom(jsonObject);
    return this;
  }
  
}
