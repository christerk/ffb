package com.balancedbytes.games.ffb;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.xml.IXmlReadable;
import com.balancedbytes.games.ffb.xml.IXmlSerializable;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public final class MoveSquare implements IXmlSerializable, IByteArraySerializable {

  public static final String XML_TAG = "move-square";
  
  private static final String _XML_ATTRIBUTE_X = "x";
  private static final String _XML_ATTRIBUTE_Y = "y";
  private static final String _XML_ATTRIBUTE_MINIMUM_ROLL_DODGE = "minimumRollDodge";
  private static final String _XML_ATTRIBUTE_MINIMUM_ROLL_GO_FOR_IT = "minimumRollGoForIt";  

  private static final String _XML_TAG_COORDINATE = "coordinate";
  
  private FieldCoordinate fCoordinate;
  private int fMinimumRollDodge;
  private int fMinimumRollGoForIt;
  
  public MoveSquare() {
    super();
  }
  
  public MoveSquare(FieldCoordinate pCoordinate, int pMinimumRollDodge, int pMinimumRollGoForIt) {
    if (pCoordinate == null) {
      throw new IllegalArgumentException("Parameter coordinate must not be null.");
    }
    fCoordinate = pCoordinate;
    fMinimumRollDodge = pMinimumRollDodge;
    fMinimumRollGoForIt = pMinimumRollGoForIt;
  }
  
  public FieldCoordinate getCoordinate() {
    return fCoordinate;
  }
  
  public int getMinimumRollDodge() {
    return fMinimumRollDodge;
  }
  
  public boolean isDodging() {
    return (getMinimumRollDodge() > 0);
  }
  
  public int getMinimumRollGoForIt() {
    return fMinimumRollGoForIt;
  }
  
  public boolean isGoingForIt() {
    return (getMinimumRollGoForIt() > 0);
  }
  
  public int hashCode() {
    return getCoordinate().hashCode();
  }
  
  public boolean equals(Object pObj) {
    return ((pObj instanceof MoveSquare) && getCoordinate().equals(((MoveSquare) pObj).getCoordinate()));
  }
  
  // transformation
  
  public MoveSquare transform() {
    return new MoveSquare(getCoordinate().transform(), getMinimumRollDodge(), getMinimumRollGoForIt());
  }
  
  public static MoveSquare transform(MoveSquare pMoveSquare) {
    return (pMoveSquare != null) ? pMoveSquare.transform() : null;
  }
  
  public static MoveSquare[] transform(MoveSquare[] pMoveSquares) {
    MoveSquare[] transformedMoveSquares = new MoveSquare[0];
    if (ArrayTool.isProvided(pMoveSquares)) {
      transformedMoveSquares = new MoveSquare[pMoveSquares.length];
      for (int i = 0; i < transformedMoveSquares.length; i++) {
        transformedMoveSquares[i] = transform(pMoveSquares[i]);
      }
    }
    return transformedMoveSquares;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MINIMUM_ROLL_DODGE, getMinimumRollDodge());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MINIMUM_ROLL_GO_FOR_IT, getMinimumRollGoForIt());
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
      fMinimumRollDodge = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_MINIMUM_ROLL_GO_FOR_IT);
      fMinimumRollGoForIt = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_MINIMUM_ROLL_DODGE);
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
    pByteList.addByte((byte) getMinimumRollDodge());
    pByteList.addByte((byte) getMinimumRollGoForIt());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fCoordinate = pByteArray.getFieldCoordinate();
    fMinimumRollDodge = pByteArray.getByte();
    fMinimumRollGoForIt = pByteArray.getByte();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.COORDINATE.addTo(jsonObject, fCoordinate);
    IJsonOption.MINIMUM_ROLL_DODGE.addTo(jsonObject, fMinimumRollDodge);
    IJsonOption.MINIMUM_ROLL_GFI.addTo(jsonObject, fMinimumRollGoForIt);
    return jsonObject;
  }
  
  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.asJsonObject(pJsonValue);
    fCoordinate = IJsonOption.COORDINATE.getFrom(jsonObject);
    fMinimumRollDodge = IJsonOption.MINIMUM_ROLL_DODGE.getFrom(jsonObject);
    fMinimumRollGoForIt = IJsonOption.MINIMUM_ROLL_GFI.getFrom(jsonObject);
  }
  
}
