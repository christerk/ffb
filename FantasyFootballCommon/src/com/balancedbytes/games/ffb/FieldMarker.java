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
public class FieldMarker implements IXmlSerializable, IByteArraySerializable {
  
  public static final String XML_TAG = "fieldMarker";
  
  private static final String _XML_TAG_COORDINATE = "coordinate";
  private static final String _XML_ATTRIBUTE_X = "x";
  private static final String _XML_ATTRIBUTE_Y = "y";
  
  private static final String _XML_ATTRIBUTE_HOME_TEXT = "homeText";
  private static final String _XML_ATTRIBUTE_AWAY_TEXT = "awayText";  

  private FieldCoordinate fCoordinate;
  private String fHomeText;
  private String fAwayText;
  
  public FieldMarker() {
    super();
  }

  public FieldMarker(FieldCoordinate pCoordinate) {
    fCoordinate = pCoordinate;
  }
  
  public FieldCoordinate getCoordinate() {
    return fCoordinate;
  }
  
  public void setHomeText(String pHomeText) {
    fHomeText = pHomeText;
  }
  
  public String getHomeText() {
    return fHomeText;
  }
  
  public void setAwayText(String pAwayText) {
    fAwayText = pAwayText;
  }
  
  public String getAwayText() {
    return fAwayText;
  }
  
  public int hashCode() {
    return getCoordinate().hashCode();
  }
  
  public boolean equals(Object pObj) {
    return ((pObj instanceof FieldMarker) && getCoordinate().equals(((FieldMarker) pObj).getCoordinate()));
  }
  
  // Transformation
  
  public FieldMarker transform() {
    FieldMarker transformedMarker = new FieldMarker(getCoordinate().transform());
    transformedMarker.setAwayText(getHomeText());
    transformedMarker.setHomeText(getAwayText());
    return transformedMarker;
  }
  
  public static FieldMarker transform(FieldMarker pFieldMarker) {
    return (pFieldMarker != null) ? pFieldMarker.transform() : null;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_HOME_TEXT, getHomeText());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_AWAY_TEXT, getAwayText());
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
      fHomeText = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_HOME_TEXT);
      fAwayText = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_AWAY_TEXT);
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
    pByteList.addString(getHomeText());
    pByteList.addString(getAwayText());
  }
    
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fCoordinate = pByteArray.getFieldCoordinate();
    fHomeText = pByteArray.getString();
    fAwayText = pByteArray.getString();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.COORDINATE.addTo(jsonObject, fCoordinate);
    IJsonOption.HOME_TEXT.addTo(jsonObject, fHomeText);
    IJsonOption.AWAY_TEXT.addTo(jsonObject, fAwayText);
    return jsonObject;
  }
  
  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.asJsonObject(pJsonValue);
    fCoordinate = IJsonOption.COORDINATE.getFrom(jsonObject);
    fHomeText = IJsonOption.HOME_TEXT.getFrom(jsonObject);
    fAwayText = IJsonOption.AWAY_TEXT.getFrom(jsonObject);
  }
  
}
