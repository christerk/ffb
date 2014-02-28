package com.balancedbytes.games.ffb.model.change;

import java.util.Date;

import com.balancedbytes.games.ffb.BloodSpot;
import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardFactory;
import com.balancedbytes.games.ffb.DiceDecoration;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldMarker;
import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.IEnumWithId;
import com.balancedbytes.games.ffb.IEnumWithName;
import com.balancedbytes.games.ffb.Inducement;
import com.balancedbytes.games.ffb.LeaderState;
import com.balancedbytes.games.ffb.LeaderStateFactory;
import com.balancedbytes.games.ffb.MoveSquare;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerActionFactory;
import com.balancedbytes.games.ffb.PlayerMarker;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.PushbackSquare;
import com.balancedbytes.games.ffb.RangeRuler;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.SendToBoxReasonFactory;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.SeriousInjuryFactory;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillFactory;
import com.balancedbytes.games.ffb.TrackNumber;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.TurnModeFactory;
import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.WeatherFactory;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogIdFactory;
import com.balancedbytes.games.ffb.dialog.DialogParameterFactory;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.old.GameOptionFactoryOld;
import com.balancedbytes.games.ffb.old.GameOptionValueOld;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public enum ModelChangeDataType implements IEnumWithId, IEnumWithName {
  
  NULL(1, "null"),
  BOOLEAN(2, "boolean"),
  STRING(3, "string"),
  PLAYER_ACTION(4, "playerAction"),
  SKILL(5, "skill"),
  LONG(6, "long"),
  DATE(7, "date"),
  TURN_MODE(8, "turnMode"),
  FIELD_COORDINATE(9, "fieldCoordinate"),
  DIALOG_ID(10, "dialogId"),
  DIALOG_PARAMETER(11, "dialogParameter"),
  INTEGER(12, "integer"),
  PLAYER_STATE(13, "playerState"),
  SERIOUS_INJURY(14, "seriousInjury"),
  SEND_TO_BOX_REASON(15, "sendToBoxReason"),
  BLOOD_SPOT(16, "bloodSpot"),
  TRACK_NUMBER(17, "trackNumber"),
  PUSHBACK_SQUARE(18, "pushbackSquare"),
  MOVE_SQUARE(19, "moveSquare"),
  WEATHER(20, "weather"),
  RANGE_RULER(21, "rangeRuler"),
  DICE_DECORATION(22, "diceDecoration"),
  INDUCEMENT(23, "inducement"),
  FIELD_MARKER(24, "fieldMarker"),
  PLAYER_MARKER(25, "playerMarker"),
  GAME_OPTION(26, "gameOption"),
  CARD(27, "card"),
  LEADER_STATE(28, "leaderState");
  
  private int fId;
  private String fName;
  
  private ModelChangeDataType(int pValue, String pName) {
    fId = pValue;
    fName = pName;
  }

  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue(Object pValue) {
    if (pValue == null) {
      return JsonValue.NULL;
    }
    switch (this) {
      case BLOOD_SPOT:
        return ((BloodSpot) pValue).toJsonValue();
      case BOOLEAN:
        return JsonValue.valueOf((Boolean) pValue);
      case CARD:
        return UtilJson.toJsonValue((Card) pValue);
      case DATE:
        return UtilJson.toJsonValue((Date) pValue); 
      case DIALOG_ID:
        return UtilJson.toJsonValue((DialogId) pValue);
      case DIALOG_PARAMETER:
        return ((IDialogParameter) pValue).toJsonValue();
      case DICE_DECORATION:
        return ((DiceDecoration) pValue).toJsonValue();
      case FIELD_COORDINATE:
        return UtilJson.toJsonValue((FieldCoordinate) pValue);
      case FIELD_MARKER:
        return ((FieldMarker) pValue).toJsonValue();
      case GAME_OPTION:
        return ((GameOptionValueOld) pValue).toJsonValue();
      case INDUCEMENT:
        return ((Inducement) pValue).toJsonValue();
      case INTEGER:
        return JsonValue.valueOf((Integer) pValue);
      case LEADER_STATE:
        return UtilJson.toJsonValue((LeaderState) pValue);
      case LONG:
        return JsonValue.valueOf((Long) pValue);
      case MOVE_SQUARE:
        return ((MoveSquare) pValue).toJsonValue();
      case NULL:
        return null;
      case PLAYER_ACTION:
        return UtilJson.toJsonValue((PlayerAction) pValue);
      case PLAYER_MARKER:
        return ((PlayerMarker) pValue).toJsonValue();
      case PLAYER_STATE:
        return UtilJson.toJsonValue((PlayerState) pValue);
      case PUSHBACK_SQUARE:
        return ((PushbackSquare) pValue).toJsonValue();
      case RANGE_RULER:
        return ((RangeRuler) pValue).toJsonValue();
      case SEND_TO_BOX_REASON:
        return UtilJson.toJsonValue((SendToBoxReason) pValue);
      case SERIOUS_INJURY:
        return UtilJson.toJsonValue((SeriousInjury) pValue);
      case SKILL:
        return UtilJson.toJsonValue((Skill) pValue);
      case STRING:
        return JsonValue.valueOf((String) pValue);
      case TRACK_NUMBER:
        return ((TrackNumber) pValue).toJsonValue();
      case TURN_MODE:
        return UtilJson.toJsonValue((TurnMode) pValue);
      case WEATHER:
        return UtilJson.toJsonValue((Weather) pValue);
      default:
        throw new IllegalStateException("Unknown type " + this + ".");
    }
  }
  
  public Object fromJsonValue(JsonValue pJsonValue) {
    if ((pJsonValue == null) || pJsonValue.isNull()) {
      return null;
    }
    switch (this) {
      case BLOOD_SPOT:
        return new BloodSpot().initFrom(pJsonValue);
      case BOOLEAN:
        return pJsonValue.asBoolean();
      case CARD:
        return UtilJson.toEnumWithName(new CardFactory(), pJsonValue);
      case DATE:
        return UtilJson.toDate(pJsonValue); 
      case DIALOG_ID:
        return UtilJson.toEnumWithName(new DialogIdFactory(), pJsonValue);
      case DIALOG_PARAMETER:
        return new DialogParameterFactory().forJsonValue(pJsonValue);
      case DICE_DECORATION:
        return new DiceDecoration().initFrom(pJsonValue);
      case FIELD_COORDINATE:
        return UtilJson.toFieldCoordinate(pJsonValue);
      case FIELD_MARKER:
        return new FieldMarker().initFrom(pJsonValue);
      case GAME_OPTION:
        return UtilJson.toEnumWithName(new GameOptionFactoryOld(), pJsonValue);
      case INDUCEMENT:
        return new Inducement().initFrom(pJsonValue);
      case INTEGER:
        return pJsonValue.asInt();
      case LEADER_STATE:
        return UtilJson.toEnumWithName(new LeaderStateFactory(), pJsonValue);
      case LONG:
        return pJsonValue.asLong();
      case MOVE_SQUARE:
        return new MoveSquare().initFrom(pJsonValue);
      case NULL:
        return null;
      case PLAYER_ACTION:
        return UtilJson.toEnumWithName(new PlayerActionFactory(), pJsonValue);
      case PLAYER_MARKER:
        return new PlayerMarker().initFrom(pJsonValue);
      case PLAYER_STATE:
        return UtilJson.toPlayerState(pJsonValue);
      case PUSHBACK_SQUARE:
        return new PushbackSquare().initFrom(pJsonValue);
      case RANGE_RULER:
        return new RangeRuler().initFrom(pJsonValue);
      case SEND_TO_BOX_REASON:
        return UtilJson.toEnumWithName(new SendToBoxReasonFactory(), pJsonValue);
      case SERIOUS_INJURY:
        return UtilJson.toEnumWithName(new SeriousInjuryFactory(), pJsonValue);
      case SKILL:
        return UtilJson.toEnumWithName(new SkillFactory(), pJsonValue);
      case STRING:
        return pJsonValue.asString();
      case TRACK_NUMBER:
        return new TrackNumber().initFrom(pJsonValue);
      case TURN_MODE:
        return UtilJson.toEnumWithName(new TurnModeFactory(), pJsonValue);
      case WEATHER:
        return UtilJson.toEnumWithName(new WeatherFactory(), pJsonValue);
      default:
        throw new IllegalStateException("Unknown type " + this + ".");
    }
  }

}
