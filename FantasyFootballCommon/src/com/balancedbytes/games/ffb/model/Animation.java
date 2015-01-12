package com.balancedbytes.games.ffb.model;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class Animation implements IJsonSerializable {
  
  private AnimationType fAnimationType;
  private String fThrownPlayerId;
  private boolean fWithBall;
  private FieldCoordinate fStartCoordinate;
  private FieldCoordinate fEndCoordinate;
  private FieldCoordinate fInterceptorCoordinate;
  private Card fCard;
  
  public Animation() {
    super();
  }

  public Animation(AnimationType pAnimationType) {
  	this(pAnimationType, null, null, null, null, false, null);
  }

  public Animation(Card pCard) {
    this(AnimationType.CARD, pCard, null, null, null, false, null);
  }

  public Animation(AnimationType pAnimationType, FieldCoordinate pCoordinate) {
  	this(pAnimationType, null, pCoordinate, null, null, false, null);
  }
  
  public Animation(FieldCoordinate pStartCoordinate, FieldCoordinate pEndCoordinate, String pThrownPlayerId, boolean pWithBall) {
    this(AnimationType.THROW_TEAM_MATE, null, pStartCoordinate, pEndCoordinate, pThrownPlayerId, pWithBall, null);
  }

  public Animation(AnimationType pAnimationType, FieldCoordinate pStartCoordinate, FieldCoordinate pEndCoordinate, FieldCoordinate pInterceptorCoordinate) {
    this(pAnimationType, null, pStartCoordinate, pEndCoordinate, null, false, pInterceptorCoordinate);
  }
  
  private Animation(AnimationType pAnimationType, Card pCard, FieldCoordinate pStartCoordinate, FieldCoordinate pEndCoordinate, String pThrownPlayerId, boolean pWithBall, FieldCoordinate pInterceptorCoordinate) {
    fAnimationType = pAnimationType;
    fCard = pCard;
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
  
  public Card getCard() {
    return fCard;
  }
  
  // transformation
  
  public Animation transform() {
    return new Animation(getAnimationType(), getCard(), FieldCoordinate.transform(getStartCoordinate()), FieldCoordinate.transform(getEndCoordinate()), getThrownPlayerId(), isWithBall(), FieldCoordinate.transform(getInterceptorCoordinate()));
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
    IJsonOption.CARD.addTo(jsonObject, fCard);
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
    fCard = (Card) IJsonOption.CARD.getFrom(jsonObject);
    return this;
  }
  
}
