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
public class DiceDecoration implements IXmlSerializable, IByteArraySerializable {

  public static final String XML_TAG = "dice-decoration";
  
  private static final String _XML_ATTRIBUTE_X = "x";
  private static final String _XML_ATTRIBUTE_Y = "y";
  private static final String _XML_ATTRIBUTE_NR_OF_DICE = "nrOfDice";

  private static final String _XML_TAG_COORDINATE = "coordinate";
  
  private FieldCoordinate fCoordinate;
  private int fNrOfDice;

  public DiceDecoration() {
    super();
  }
  
  public DiceDecoration(FieldCoordinate pCoordinate, int pNrOfDice) {
    fCoordinate = pCoordinate;
    fNrOfDice = pNrOfDice;
  }
  
  public FieldCoordinate getCoordinate() {
    return fCoordinate;
  }
  
  public int getNrOfDice() {
    return fNrOfDice;
  }
  
  public int hashCode() {
    return getCoordinate().hashCode();
  }
  
  public boolean equals(Object pObj) {
    return (
      (pObj instanceof DiceDecoration)
      && getCoordinate().equals(((DiceDecoration) pObj).getCoordinate())
    );
  }
    
  // transformation
  
  public DiceDecoration transform() {
    return new DiceDecoration(getCoordinate().transform(), getNrOfDice());
  }
  
  public static DiceDecoration transform(DiceDecoration pDiceDecoration) {
    return (pDiceDecoration != null) ? pDiceDecoration.transform() : null;
  }
  
  public static DiceDecoration[] transform(DiceDecoration[] pDiceDecorations) {
    DiceDecoration[] transformedDiceDecorations = new DiceDecoration[0];
    if (ArrayTool.isProvided(pDiceDecorations)) {
      transformedDiceDecorations = new DiceDecoration[pDiceDecorations.length];
      for (int i = 0; i < transformedDiceDecorations.length; i++) {
        transformedDiceDecorations[i] = transform(pDiceDecorations[i]);
      }
    }
    return transformedDiceDecorations;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_NR_OF_DICE, getNrOfDice());
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
      fNrOfDice = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_NR_OF_DICE);
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
    pByteList.addByte((byte) getNrOfDice());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt(); 
    fCoordinate = pByteArray.getFieldCoordinate();
    fNrOfDice = pByteArray.getByte();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.COORDINATE.addTo(jsonObject, fCoordinate);
    IJsonOption.NR_OF_DICE.addTo(jsonObject, fNrOfDice);
    return jsonObject;
  }
  
  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.asJsonObject(pJsonValue);
    fCoordinate = IJsonOption.COORDINATE.getFrom(jsonObject);
    fNrOfDice = IJsonOption.NR_OF_DICE.getFrom(jsonObject);
  }

}
