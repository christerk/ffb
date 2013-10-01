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
public class BloodSpot implements IXmlSerializable, IByteArraySerializable {
  
  public static final String XML_TAG = "bloodSpot";
  
  private static final String _XML_ATTRIBUTE_X = "x";
  private static final String _XML_ATTRIBUTE_Y = "y";
  private static final String _XML_ATTRIBUTE_INJURY = "injury";
  
  private static final String _XML_TAG_COORDINATE = "coordinate";
  
  private PlayerState fInjury;
  private FieldCoordinate fCoordinate;
  private transient String fIconProperty;

  public BloodSpot() {
    super();
  }
  
  public BloodSpot(FieldCoordinate pCoordinate, PlayerState pInjury) {
    fInjury = pInjury;
    fCoordinate = pCoordinate;
  }
  
  public PlayerState getInjury() {
    return fInjury;
  }

  public FieldCoordinate getCoordinate() {
    return fCoordinate;
  }
  
  public void setIconProperty(String pIconProperty) {
    fIconProperty = pIconProperty;
  }
  
  public String getIconProperty() {
    return fIconProperty;
  }
  
  public BloodSpot transform() {
  	BloodSpot transformedBloodspot = new BloodSpot(getCoordinate().transform(), getInjury());
    transformedBloodspot.setIconProperty(getIconProperty());
    return transformedBloodspot;
  }
  
  public static BloodSpot transform(BloodSpot pBloodspot) {
    return (pBloodspot != null) ? pBloodspot.transform() : null;
  }

  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_INJURY, (getInjury() != null) ? getInjury().getId() : 0);
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
      fInjury = new PlayerState(UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_INJURY));
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
    pByteList.addSmallInt((getInjury() != null) ? getInjury().getId() : 0);
    pByteList.addFieldCoordinate(getCoordinate());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fInjury = new PlayerState(pByteArray.getSmallInt());
    fCoordinate = pByteArray.getFieldCoordinate();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.INJURY.addTo(jsonObject, fInjury);
    IJsonOption.COORDINATE.addTo(jsonObject, fCoordinate);
    return jsonObject;
  }
  
  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.asJsonObject(pJsonValue);
    fInjury = IJsonOption.INJURY.getFrom(jsonObject);
    fCoordinate = IJsonOption.COORDINATE.getFrom(jsonObject);
  }
      
}
