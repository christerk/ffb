package com.balancedbytes.games.ffb.model;

import java.util.Date;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.BloodSpot;
import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardFactory;
import com.balancedbytes.games.ffb.DiceDecoration;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldMarker;
import com.balancedbytes.games.ffb.GameOptionValue;
import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.Inducement;
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
import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogIdFactory;
import com.balancedbytes.games.ffb.util.DateTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.UtilXml;


/**
 * 
 * @author Kalimar
 */
public enum ModelAttributeType {
  
  NULL(1, "null", true),
  BOOLEAN(2, "boolean", false),
  BYTE(3, "byte", false),
  STRING(4, "string", true),
  PLAYER_ACTION(5, "playerAction", true),
  SKILL(6, "skill", true),
  LONG(7, "long", false),
  DATE(8, "date", true),
  TURN_MODE(9, "turnMode", true),
  FIELD_COORDINATE(10, "fieldCoordinate", true),
  DIALOG_ID(11, "dialogId", true),
  DIALOG_PARAMETER(12, "dialogParameter", true),
  // 13 obsolete (was: DEFENDER_ACTION)
  INTEGER(14, "integer", false),
  PLAYER_STATE(15, "playerState", true),
  SERIOUS_INJURY(16, "seriousInjury", true),
  SEND_TO_BOX_REASON(17, "sendToBoxReason", true),
  BLOOD_SPOT(18, "bloodSpot", true),
  TRACK_NUMBER(19, "trackNumber", true),
  PUSHBACK_SQUARE(20, "pushbackSquare", true),
  MOVE_SQUARE(21, "moveSquare", true),
  WEATHER(22, "weather", true),
  RANGE_RULER(23, "rangeRuler", true),
  DICE_DECORATION(24, "diceDecoration", true),
  INDUCEMENT(25, "inducement", true),
  FIELD_MARKER(26, "fieldMarker", true),
  PLAYER_MARKER(27, "playerMarker", true),
  GAME_OPTION(28, "gameOption", false),
  CARD(29, "card", false);
  
  private int fId;
  private String fName;
  private boolean fNullAllowed;
  
  private ModelAttributeType(int pValue, String pName, boolean pNullAllowed) {
    fId = pValue;
    fName = pName;
    fNullAllowed = pNullAllowed;
  }

  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
    
  public boolean isNullAllowed() {
    return fNullAllowed;
  }
  
  public static ModelAttributeType fromId(int pId) {
    for (ModelAttributeType type : values()) {
      if (type.getId() == pId) {
        return type;
      }
    }
    return null;
  }
    
  public static ModelAttributeType fromName(String pName) {
    for (ModelAttributeType type : values()) {
      if (type.getName().equalsIgnoreCase(pName)) {
        return type;
      }
    }
    return null;
  }
  
  public void checkValueType(Object pValue) {
    boolean typeOk = true;
    if (pValue == null) {
      if (!isNullAllowed()) {
        throw new IllegalArgumentException("Parameter value must not be null.");
      }
    } else {
      switch (this) {
        case NULL:
          typeOk = false;
          break;
        case BOOLEAN:
          typeOk = (pValue instanceof Boolean);
          break;
        case BYTE:
          typeOk = (pValue instanceof Byte);
          break;
        case INTEGER:
          typeOk = (pValue instanceof Integer);
          break;
        case STRING:
          typeOk = (pValue instanceof String);
          break;
        case PLAYER_ACTION:
          typeOk = (pValue instanceof PlayerAction);
          break;
        case SKILL:
          typeOk = (pValue instanceof Skill);
          break;
        case LONG:
          typeOk = (pValue instanceof Long);
          break;
        case DATE:
          typeOk = (pValue instanceof Date);
          break;
        case TURN_MODE:
          typeOk = (pValue instanceof TurnMode);
          break;
        case FIELD_COORDINATE:
          typeOk = (pValue instanceof FieldCoordinate);
          break;
        case DIALOG_ID:
          typeOk = (pValue instanceof DialogId);
          break;
        case DIALOG_PARAMETER:
          typeOk = (pValue instanceof IDialogParameter);
          break;
        case PLAYER_STATE:
          typeOk = (pValue instanceof PlayerState);
          break;
        case SERIOUS_INJURY:
          typeOk = (pValue instanceof SeriousInjury);
          break;
        case SEND_TO_BOX_REASON:
          typeOk = (pValue instanceof SendToBoxReason);
          break;
        case BLOOD_SPOT:
          typeOk = (pValue instanceof BloodSpot);
          break;
        case TRACK_NUMBER:
          typeOk = (pValue instanceof TrackNumber);
          break;
        case PUSHBACK_SQUARE:
          typeOk = (pValue instanceof PushbackSquare);
          break;
        case MOVE_SQUARE:
          typeOk = (pValue instanceof MoveSquare);
          break;
        case WEATHER:
          typeOk = (pValue instanceof Weather);
          break;
        case RANGE_RULER:
          typeOk = (pValue instanceof RangeRuler);
          break;
        case DICE_DECORATION:
          typeOk = (pValue instanceof DiceDecoration);
          break;
        case INDUCEMENT:
          typeOk = (pValue instanceof Inducement);
          break;
        case FIELD_MARKER:
          typeOk = (pValue instanceof FieldMarker);
          break;
        case PLAYER_MARKER:
          typeOk = (pValue instanceof PlayerMarker);
          break;
        case GAME_OPTION:
        	typeOk = (pValue instanceof GameOptionValue);
        	break;
        case CARD:
        	typeOk = (pValue instanceof Card);
        	break;
        default:
          throw new IllegalStateException("Unknown type " + this + ".");
      }
    }
    if (!typeOk) {
      throw new IllegalArgumentException("Parameter value is not of type " + getName() + ".");
    }
  }
  
  public boolean addXmlAttribute(AttributesImpl pXmlAttributes, String pXmlAttributeName, Object pValue) {
    boolean handled = true;
    switch (this) {
      case NULL:
        break;
      case BOOLEAN:
      case BYTE:
      case INTEGER:
      case STRING:
      case LONG:
        UtilXml.addAttribute(pXmlAttributes, pXmlAttributeName, (pValue != null) ? pValue.toString() : null);
        break;
      case PLAYER_ACTION:
        UtilXml.addAttribute(pXmlAttributes, pXmlAttributeName, (pValue != null) ? ((PlayerAction) pValue).getName() : null);
        break;
      case SKILL:
        UtilXml.addAttribute(pXmlAttributes, pXmlAttributeName, (pValue != null) ? ((Skill) pValue).getName() : null);
        break;
      case DATE:
        if (pValue != null) {
          UtilXml.addAttribute(pXmlAttributes, pXmlAttributeName, DateTool.formatTimestamp((Date) pValue));
        }
        break;
      case TURN_MODE:
        UtilXml.addAttribute(pXmlAttributes, pXmlAttributeName, (pValue != null) ? ((TurnMode) pValue).getName() : null);
        break;
      case DIALOG_ID:
        UtilXml.addAttribute(pXmlAttributes, pXmlAttributeName, (pValue != null) ? ((DialogId) pValue).getName() : null);
        break;
      case PLAYER_STATE:
        UtilXml.addAttribute(pXmlAttributes, pXmlAttributeName, Integer.toString((pValue != null) ? ((PlayerState) pValue).getId() : 0));
        break;
      case SERIOUS_INJURY:
        UtilXml.addAttribute(pXmlAttributes, pXmlAttributeName, (pValue != null) ? ((SeriousInjury) pValue).getName() : null);
        break;
      case SEND_TO_BOX_REASON:
        UtilXml.addAttribute(pXmlAttributes, pXmlAttributeName, (pValue != null) ? ((SendToBoxReason) pValue).getName() : null);
        break;
      case WEATHER:
        UtilXml.addAttribute(pXmlAttributes, pXmlAttributeName, (pValue != null) ? ((Weather) pValue).getName() : null);
        break;
      case CARD:
      	UtilXml.addAttribute(pXmlAttributes, pXmlAttributeName, (pValue != null) ? ((Card) pValue).getName() : null);
      	break;
      default:
        handled = false;
        break;
    }
    return handled;
  }

  public Object getValueFromXmlAttribute(String pXmlAttributeName, Attributes pXmlAttributes) {
    String valueString = UtilXml.getStringAttribute(pXmlAttributes, pXmlAttributeName);
    switch (this) {
      case BOOLEAN:
        return new Boolean(valueString);
      case BYTE:
        return new Byte(valueString);
      case INTEGER:
        return new Integer(valueString);
      case PLAYER_ACTION:
        return new PlayerActionFactory().forName(valueString);
      case SKILL:
        return new SkillFactory().forName(valueString);
      case LONG:
        return new Long(valueString);
      case STRING:
        return valueString;
      case TURN_MODE:
        return TurnMode.fromName(valueString);
      case DATE:
        if (StringTool.isProvided(valueString)) {
          return DateTool.parseTimestamp(valueString);
        } else {
          return null;
        }
      case DIALOG_ID:
        return new DialogIdFactory().forName(valueString);
      case PLAYER_STATE:
        return new PlayerState(Integer.parseInt(valueString));
      case SERIOUS_INJURY:
        return new SeriousInjuryFactory().forName(valueString);
      case SEND_TO_BOX_REASON:
        return new SendToBoxReasonFactory().forName(valueString);
      case WEATHER:
        return Weather.fromName(valueString);
      case CARD:
      	return new CardFactory().forName(valueString);
      default:
        return null;
    }
  }

  public void addTo(ByteList pByteList, Object pValue) {
    switch (this) {
      case NULL:
        break;
      case BOOLEAN:
        pByteList.addBoolean((Boolean) pValue);
        break;
      case BYTE:
        pByteList.addByte((Byte) pValue);
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
      case BYTE:
        return pByteArray.getByte();
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
        return TurnMode.fromId(pByteArray.getByte());
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
        return Weather.fromId(pByteArray.getByte());
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
      default:
        throw new IllegalStateException("Unhandled type " + this + ".");
    }
  }

}
