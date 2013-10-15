package com.balancedbytes.games.ffb.model;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class Animation implements IByteArraySerializable, IJsonSerializable {
  
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
