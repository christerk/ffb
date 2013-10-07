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
public class TrackNumber implements IXmlSerializable, IByteArraySerializable {
  
  public static final String XML_TAG = "trackNumber";
  
  private static final String _XML_ATTRIBUTE_X = "x";
  private static final String _XML_ATTRIBUTE_Y = "y";
  private static final String _XML_ATTRIBUTE_NUMBER = "number";
  
  private static final String _XML_TAG_COORDINATE = "coordinate";

  private FieldCoordinate fCoordinate;
  private int fNumber;
  
  public TrackNumber() {
    super();
  }

  public TrackNumber(FieldCoordinate pCoordinate, int pNumber) {
    fCoordinate = pCoordinate;
    fNumber = pNumber;
  }
  
  public FieldCoordinate getCoordinate() {
    return fCoordinate;
  }
  
  public int getNumber() {
    return fNumber;
  }
  
  public int hashCode() {
    return getCoordinate().hashCode();
  }
  
  public boolean equals(Object pObj) {
    return (
      (pObj instanceof TrackNumber)
      && getCoordinate().equals(((TrackNumber) pObj).getCoordinate())
    );
  }
  
  public TrackNumber transform() {
    return new TrackNumber(getCoordinate().transform(), getNumber());
  }
  
  public static TrackNumber transform(TrackNumber pTrackNumber) {
    return (pTrackNumber != null) ? pTrackNumber.transform() : null;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_NUMBER, getNumber());
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
      fNumber = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_NUMBER);
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
  };
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) getNumber());
    pByteList.addFieldCoordinate(getCoordinate());
  }
    
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fNumber = pByteArray.getByte();
    fCoordinate = pByteArray.getFieldCoordinate();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NUMBER.addTo(jsonObject, fNumber);
    IJsonOption.COORDINATE.addTo(jsonObject, fCoordinate);
    return jsonObject;
  }
  
  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.asJsonObject(pJsonValue);
    fNumber = IJsonOption.NUMBER.getFrom(jsonObject);
    fCoordinate = IJsonOption.COORDINATE.getFrom(jsonObject);
  }
  
}
