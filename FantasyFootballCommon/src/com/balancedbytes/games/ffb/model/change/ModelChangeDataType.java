package com.balancedbytes.games.ffb.model.change;

import java.util.Date;

import com.balancedbytes.games.ffb.BloodSpot;
import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardFactory;
import com.balancedbytes.games.ffb.DiceDecoration;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldMarker;
import com.balancedbytes.games.ffb.GameOption;
import com.balancedbytes.games.ffb.GameOptionFactory;
import com.balancedbytes.games.ffb.GameOptionValue;
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
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogIdFactory;
import com.balancedbytes.games.ffb.dialog.DialogParameterFactory;
import com.balancedbytes.games.ffb.json.UtilJson;
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
  
  // ByteArray serialization
  
  public void addTo(ByteList pByteList, Object pValue) {
    switch (this) {
      case NULL:
        break;
      case BOOLEAN:
        pByteList.addBoolean((Boolean) pValue);
        break;
      case INTEGER:
        pByteList.addInt((Integer) pValue);
        break;
      case STRING:
        pByteList.addString((String) pValue);
        break;
      case PLAYER_ACTION:
        pByteList.addByte((byte) ((pValue != null) ? ((PlayerAction) pValue).getId() : 0));
        break;
      case SKILL:
        pByteList.addByte((byte) ((pValue != null) ? ((Skill) pValue).getId() : 0));
        break;
      case LONG:
        pByteList.addLong((Long) pValue);
        break;
      case DATE:
        pByteList.addLong((long) ((pValue != null) ? ((Date) pValue).getTime() : 0));
        break;
      case TURN_MODE:
        pByteList.addByte((byte) ((pValue != null) ? ((TurnMode) pValue).getId() : 0));
        break;
      case FIELD_COORDINATE:
        pByteList.addFieldCoordinate((FieldCoordinate) pValue);
        break;
      case DIALOG_ID:
        pByteList.addByte((byte) ((pValue != null) ? ((DialogId) pValue).getId() : 0));
        break;
      case DIALOG_PARAMETER:
        if (pValue != null) {
          pByteList.addByte((byte) ((IDialogParameter) pValue).getId().getId());
          ((IDialogParameter) pValue).addTo(pByteList);
        } else {
          pByteList.addByte((byte) 0);
        }
        break;
      case PLAYER_STATE:
        pByteList.addSmallInt((pValue != null) ? ((PlayerState) pValue).getId() : 0);
        break;
      case SERIOUS_INJURY:
        pByteList.addByte((byte) ((pValue != null) ? ((SeriousInjury) pValue).getId() : 0));
        break;
      case SEND_TO_BOX_REASON:
        pByteList.addByte((byte) ((pValue != null) ? ((SendToBoxReason) pValue).getId() : 0));
        break;
      case BLOOD_SPOT:
        if (pValue != null) {
          pByteList.addBoolean(true);
          ((BloodSpot) pValue).addTo(pByteList);
        } else {
          pByteList.addBoolean(false);
        }
        break;
      case TRACK_NUMBER:
        if (pValue != null) {
          pByteList.addBoolean(true);
          ((TrackNumber) pValue).addTo(pByteList);
        } else {
          pByteList.addBoolean(false);
        }
        break;
      case PUSHBACK_SQUARE:
        if (pValue != null) {
          pByteList.addBoolean(true);
          ((PushbackSquare) pValue).addTo(pByteList);
        } else {
          pByteList.addBoolean(false);
        }
        break;
      case MOVE_SQUARE:
        if (pValue != null) {
          pByteList.addBoolean(true);
          ((MoveSquare) pValue).addTo(pByteList);
        } else {
          pByteList.addBoolean(false);
        }
        break;
      case WEATHER:
        pByteList.addByte((byte) ((pValue != null) ? ((Weather) pValue).getId() : 0));
        break;
      case CARD:
        pByteList.addSmallInt((pValue != null) ? ((Card) pValue).getId() : 0);
        break;
      case RANGE_RULER:
        if (pValue != null) {
          pByteList.addBoolean(true);
          ((RangeRuler) pValue).addTo(pByteList);
        } else {
          pByteList.addBoolean(false);
        }
        break;
      case DICE_DECORATION:
        if (pValue != null) {
          pByteList.addBoolean(true);
          ((DiceDecoration) pValue).addTo(pByteList);
        } else {
          pByteList.addBoolean(false);
        }
        break;
      case INDUCEMENT:
        if (pValue != null) {
          pByteList.addBoolean(true);
          ((Inducement) pValue).addTo(pByteList);
        } else {
          pByteList.addBoolean(false);
        }
        break;
      case FIELD_MARKER:
        if (pValue != null) {
          pByteList.addBoolean(true);
          ((FieldMarker) pValue).addTo(pByteList);
        } else {
          pByteList.addBoolean(false);
        }
        break;
      case PLAYER_MARKER:
        if (pValue != null) {
          pByteList.addBoolean(true);
          ((PlayerMarker) pValue).addTo(pByteList);
        } else {
          pByteList.addBoolean(false);
        }
        break;
      case GAME_OPTION:
        if (pValue != null) {
          pByteList.addBoolean(true);
          ((GameOptionValue) pValue).addTo(pByteList);
        } else {
          pByteList.addBoolean(false);
        }
        break;
      case LEADER_STATE:
        pByteList.addByte((byte) ((pValue != null) ? ((LeaderState) pValue).getId() : 0));
        break;
      default:
        throw new IllegalStateException("Unhandled type " + this + ".");
    }
  }
  
  public Object initFrom(ByteArray pByteArray) {
    switch (this) {
      case NULL:
        return null;
      case BOOLEAN:
        return pByteArray.getBoolean();
      case INTEGER:
        return pByteArray.getInt();
      case STRING:
        return pByteArray.getString();
      case PLAYER_ACTION:
        return new PlayerActionFactory().forId(pByteArray.getByte());
      case SKILL:
        return new SkillFactory().forId(pByteArray.getByte());
      case LONG:
        return pByteArray.getLong();
      case DATE:
        long time = pByteArray.getLong();
        if (time != 0) {
          return (new Date(time));
        } else {
          return null;
        }
      case TURN_MODE:
        return new TurnModeFactory().forId(pByteArray.getByte());
      case FIELD_COORDINATE:
        return pByteArray.getFieldCoordinate();
      case DIALOG_ID:
        return new DialogIdFactory().forId(pByteArray.getByte());
      case DIALOG_PARAMETER:
        IDialogParameter dialogParameter = null;
        DialogId dialogId = new DialogIdFactory().forId(pByteArray.getByte());
        if (dialogId != null) {
          dialogParameter = dialogId.createDialogParameter();
          if (dialogParameter != null) {
            dialogParameter.initFrom(pByteArray);
          }
        }
        return dialogParameter;
      case PLAYER_STATE:
        return new PlayerState(pByteArray.getSmallInt());
      case SERIOUS_INJURY:
        return new SeriousInjuryFactory().forId(pByteArray.getByte());
      case SEND_TO_BOX_REASON:
        return new SendToBoxReasonFactory().forId(pByteArray.getByte());
      case BLOOD_SPOT:
        BloodSpot bloodSpot = null;
        if (pByteArray.getBoolean()) {
          bloodSpot = new BloodSpot();
          bloodSpot.initFrom(pByteArray);
        }
        return bloodSpot;
      case TRACK_NUMBER:
        TrackNumber trackNumber = null;
        if (pByteArray.getBoolean()) {
          trackNumber = new TrackNumber();
          trackNumber.initFrom(pByteArray);
        }
        return trackNumber;
      case PUSHBACK_SQUARE:
        PushbackSquare pushbackSquare = null;
        if (pByteArray.getBoolean()) {
          pushbackSquare = new PushbackSquare();
          pushbackSquare.initFrom(pByteArray);
        }
        return pushbackSquare;
      case MOVE_SQUARE:
        MoveSquare moveSquare = null;
        if (pByteArray.getBoolean()) {
          moveSquare = new MoveSquare();
          moveSquare.initFrom(pByteArray);
        }
        return moveSquare;
      case WEATHER:
        return new WeatherFactory().forId(pByteArray.getByte());
      case CARD:
        return new CardFactory().forId(pByteArray.getSmallInt());
      case RANGE_RULER:
        RangeRuler rangeRuler = null;
        if (pByteArray.getBoolean()) {
          rangeRuler = new RangeRuler();
          rangeRuler.initFrom(pByteArray);
        }
        return rangeRuler;
      case DICE_DECORATION:
        DiceDecoration diceDecoration = null;
        if (pByteArray.getBoolean()) {
          diceDecoration = new DiceDecoration();
          diceDecoration.initFrom(pByteArray);
        }
        return diceDecoration;
      case INDUCEMENT:
        Inducement inducement = null;
        if (pByteArray.getBoolean()) {
          inducement = new Inducement();
          inducement.initFrom(pByteArray);
        }
        return inducement;
      case FIELD_MARKER:
        FieldMarker fieldMarker = null;
        if (pByteArray.getBoolean()) {
          fieldMarker = new FieldMarker();
          fieldMarker.initFrom(pByteArray);
        }
        return fieldMarker;
      case PLAYER_MARKER:
        PlayerMarker playerMarker = null;
        if (pByteArray.getBoolean()) {
          playerMarker = new PlayerMarker();
          playerMarker.initFrom(pByteArray);
        }
        return playerMarker;
      case GAME_OPTION:
        GameOptionValue gameOption = null;
        if (pByteArray.getBoolean()) {
          gameOption = new GameOptionValue();
          gameOption.initFrom(pByteArray);
        }
        return gameOption;
      case LEADER_STATE:
        return new LeaderStateFactory().forId(pByteArray.getByte());
      default:
        throw new IllegalStateException("Unhandled type " + this + ".");
    }
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
        return UtilJson.toJsonValue((GameOption) pValue);
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
        return UtilJson.toEnumWithName(new GameOptionFactory(), pJsonValue);
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
