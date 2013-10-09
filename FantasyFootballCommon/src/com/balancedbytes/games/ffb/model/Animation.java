package com.balancedbytes.games.ffb.model;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.FieldCoordinate;
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
public class Animation implements IXmlSerializable, IByteArraySerializable {
  
  public static final String XML_TAG = "animation";
  
  private static final String _XML_ATTRIBUTE_TYPE = "type";
  private static final String _XML_ATTRIBUTE_THROWN_PLAYER_ID = "thrownPlayerId";
  private static final String _XML_ATTRIBUTE_WITH_BALL = "withBall";
  private static final String _XML_ATTRIBUTE_X = "x";
  private static final String _XML_ATTRIBUTE_Y = "y";

  private static final String _XML_TAG_START_COORDINATE = "startCoordinate";
  private static final String _XML_TAG_END_COORDINATE = "endCoordinate";
  private static final String _XML_TAG_INTERCEPTOR_COORDINATE = "interceptorCoordinate";
  
  private AnimationType fAnimationType;
  private String fThrownPlayerId;
  private boolean fWithBall;
  private FieldCoordinate fStartCoordinate;
  private FieldCoordinate fEndCoordinate;
  private FieldCoordinate fInterceptorCoordinate;
  
  public Animation() {
    super();
  }

  public Animation(AnimationType pAnimationType) {
  	this(pAnimationType, null, null, null, false, null);
  }

  public Animation(AnimationType pAnimationType, FieldCoordinate pCoordinate) {
  	this(pAnimationType, pCoordinate, null, null, false, null);
  }
  
  public Animation(FieldCoordinate pStartCoordinate, FieldCoordinate pEndCoordinate, String pThrownPlayerId, boolean pWithBall) {
    this(AnimationType.THROW_TEAM_MATE, pStartCoordinate, pEndCoordinate, pThrownPlayerId, pWithBall, null);
  }

  public Animation(AnimationType pAnimationType, FieldCoordinate pStartCoordinate, FieldCoordinate pEndCoordinate, FieldCoordinate pInterceptorCoordinate) {
    this(pAnimationType, pStartCoordinate, pEndCoordinate, null, false, pInterceptorCoordinate);
  }
  
  private Animation(AnimationType pAnimationType, FieldCoordinate pStartCoordinate, FieldCoordinate pEndCoordinate, String pThrownPlayerId, boolean pWithBall, FieldCoordinate pInterceptorCoordinate) {
    fAnimationType = pAnimationType;
    fThrownPlayerId = pThrownPlayerId;
    fWithBall = pWithBall;
    fStartCoordinate = pStartCoordinate;
    fEndCoordinate = pEndCoordinate;
    fInterceptorCoordinate = pInterceptorCoordinate;
  }
  
  public AnimationType getAnimationType() {
		return fAnimationType;
	}

  public String getThrownPlayerId() {
    return fThrownPlayerId;
  }
  
  public boolean isWithBall() {
    return fWithBall;
  }
  
  public FieldCoordinate getStartCoordinate() {
    return fStartCoordinate;
  }
  
  public FieldCoordinate getEndCoordinate() {
    return fEndCoordinate;
  }
  
  public FieldCoordinate getInterceptorCoordinate() {
    return fInterceptorCoordinate;
  }
  
  // transformation
  
  public Animation transform() {
    return new Animation(getAnimationType(), FieldCoordinate.transform(getStartCoordinate()), FieldCoordinate.transform(getEndCoordinate()), getThrownPlayerId(), isWithBall(), FieldCoordinate.transform(getInterceptorCoordinate()));
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TYPE, (getAnimationType() != null) ? getAnimationType().getName() : null);
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_THROWN_PLAYER_ID, getThrownPlayerId());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_WITH_BALL, isWithBall());
  	UtilXml.startElement(pHandler, XML_TAG, attributes);
  	
  	if (getStartCoordinate() != null) {
  		attributes = new AttributesImpl();
  		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_X, getStartCoordinate().getX());
  		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_Y, getStartCoordinate().getY());
  		UtilXml.startElement(pHandler, _XML_TAG_START_COORDINATE, attributes);
  		UtilXml.endElement(pHandler, _XML_TAG_START_COORDINATE);
  	}

  	if (getEndCoordinate() != null) {
  		attributes = new AttributesImpl();
  		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_X, getEndCoordinate().getX());
  		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_Y, getEndCoordinate().getY());
  		UtilXml.startElement(pHandler, _XML_TAG_END_COORDINATE, attributes);
  		UtilXml.endElement(pHandler, _XML_TAG_END_COORDINATE);
  	}

  	if (getInterceptorCoordinate() != null) {
  		attributes = new AttributesImpl();
  		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_X, getInterceptorCoordinate().getX());
  		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_Y, getInterceptorCoordinate().getY());
  		UtilXml.startElement(pHandler, _XML_TAG_INTERCEPTOR_COORDINATE, attributes);
  		UtilXml.endElement(pHandler, _XML_TAG_INTERCEPTOR_COORDINATE);
  	}

  	UtilXml.endElement(pHandler, XML_TAG);
  	
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }
  
  public IXmlReadable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
    if (XML_TAG.equals(pXmlTag)) {
    	fAnimationType = new AnimationTypeFactory().forName(UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_TYPE));
      fThrownPlayerId = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_THROWN_PLAYER_ID);
      fWithBall = UtilXml.getBooleanAttribute(pXmlAttributes, _XML_ATTRIBUTE_WITH_BALL);
    }
    if (_XML_TAG_START_COORDINATE.equals(pXmlTag)) {
      int x = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_X);
      int y = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_Y);
      fStartCoordinate = new FieldCoordinate(x, y);
    }
    if (_XML_TAG_END_COORDINATE.equals(pXmlTag)) {
      int x = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_X);
      int y = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_Y);
      fEndCoordinate = new FieldCoordinate(x, y);
    }
    if (_XML_TAG_INTERCEPTOR_COORDINATE.equals(pXmlTag)) {
      int x = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_X);
      int y = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_Y);
      fInterceptorCoordinate = new FieldCoordinate(x, y);
    }
    return this;
  }
  
  public boolean endXmlElement(String pXmlTag, String pValue) {
    return XML_TAG.equals(pXmlTag);
  }

  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 3;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getThrownPlayerId());
    pByteList.addBoolean(isWithBall());
    pByteList.addFieldCoordinate(getStartCoordinate());
    pByteList.addFieldCoordinate(getEndCoordinate());
    pByteList.addFieldCoordinate(getInterceptorCoordinate());
    pByteList.addByte((byte) ((getAnimationType() != null) ? getAnimationType().getId() : 0));
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fThrownPlayerId = pByteArray.getString();
    fWithBall = pByteArray.getBoolean();
    fStartCoordinate = pByteArray.getFieldCoordinate();
    fEndCoordinate = pByteArray.getFieldCoordinate();
    fInterceptorCoordinate = pByteArray.getFieldCoordinate();
    if (byteArraySerializationVersion > 1) {
    	fAnimationType = new AnimationTypeFactory().forId(pByteArray.getByte());
    }
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.THROWN_PLAYER_ID.addTo(jsonObject, fThrownPlayerId);
    IJsonOption.WITH_BALL.addTo(jsonObject, fWithBall);
    IJsonOption.START_COORDINATE.addTo(jsonObject, fStartCoordinate);
    IJsonOption.END_COORDINATE.addTo(jsonObject, fEndCoordinate);
    IJsonOption.INTERCEPTOR_COORDINATE.addTo(jsonObject, fInterceptorCoordinate);
    IJsonOption.ANIMATION_TYPE.addTo(jsonObject, fAnimationType);
    return jsonObject;
  }
  
  public Animation initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fThrownPlayerId = IJsonOption.THROWN_PLAYER_ID.getFrom(jsonObject);
    fWithBall = IJsonOption.WITH_BALL.getFrom(jsonObject);
    fStartCoordinate = IJsonOption.START_COORDINATE.getFrom(jsonObject);
    fEndCoordinate = IJsonOption.END_COORDINATE.getFrom(jsonObject);
    fInterceptorCoordinate = IJsonOption.INTERCEPTOR_COORDINATE.getFrom(jsonObject);
    fAnimationType = (AnimationType) IJsonOption.ANIMATION_TYPE.getFrom(jsonObject);
    return this;
  }
  
}
