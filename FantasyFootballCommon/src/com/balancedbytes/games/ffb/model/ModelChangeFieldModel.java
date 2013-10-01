package com.balancedbytes.games.ffb.model;


import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.BloodSpot;
import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.DiceDecoration;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldMarker;
import com.balancedbytes.games.ffb.MoveSquare;
import com.balancedbytes.games.ffb.PlayerMarker;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.PushbackSquare;
import com.balancedbytes.games.ffb.RangeRuler;
import com.balancedbytes.games.ffb.TrackNumber;
import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.xml.IXmlWriteable;
import com.balancedbytes.games.ffb.xml.UtilXml;

/**
 * 
 * @author Kalimar
 */
public class ModelChangeFieldModel implements IModelChange {
  
  private static final String _XML_ATTRIBUTE_CHANGE = "change";
  private static final String _XML_ATTRIBUTE_VALUE = "value";
  private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";
  private static final String _XML_ATTRIBUTE_X = "x";
  private static final String _XML_ATTRIBUTE_Y = "y";

  private CommandFieldModelChange fChange;
  private Object fValue1;
  private Object fValue2;
  
  protected ModelChangeFieldModel() {
    super();
  }

  public ModelChangeFieldModel(CommandFieldModelChange pChange, Object pValue1) {
    this(pChange, pValue1, null);
  }
  
  public ModelChangeFieldModel(CommandFieldModelChange pChange, Object pValue1, Object pValue2) {
    if (pChange == null) {
      throw new IllegalArgumentException("Parameter change must not be null.");
    }
    fChange = pChange;
    getChange().getAttributeType1().checkValueType(pValue1);
    fValue1 = pValue1;
    getChange().getAttributeType2().checkValueType(pValue2);
    fValue2 = pValue2;
  }
  
  public ModelChangeId getId() {
    return ModelChangeId.FIELD_MODEL_CHANGE;
  }
  
  public CommandFieldModelChange getChange() {
    return fChange;
  }
  
  public Object getValue1() {
    return fValue1;
  }
  
  public Object getValue2() {
    return fValue2;
  }
  
  public void applyTo(Game pGame) {
    boolean trackingChanges = pGame.isTrackingChanges();
    pGame.setTrackingChanges(false);
    FieldModel fieldModel = pGame.getFieldModel();
    switch (getChange()) {
      case REMOVE_PLAYER:
        fieldModel.remove(pGame.getPlayerById((String) getValue1()));
        break;
      case SET_PLAYER_COOORDINATE:
        fieldModel.setPlayerCoordinate(pGame.getPlayerById((String) getValue1()), (FieldCoordinate) getValue2());
        break;
      case SET_PLAYER_STATE:
        fieldModel.setPlayerState(pGame.getPlayerById((String) getValue1()), (PlayerState) getValue2());
        break;
      case SET_BALL_COORDINATE:
        fieldModel.setBallCoordinate((FieldCoordinate) getValue1());
        break;
      case SET_BOMB_COORDINATE:
        fieldModel.setBombCoordinate((FieldCoordinate) getValue1());
        break;
      case SET_BALL_MOVING:
        fieldModel.setBallMoving((Boolean) getValue1());
        break;
      case SET_BOMB_MOVING:
        fieldModel.setBombMoving((Boolean) getValue1());
        break;
      case SET_BALL_IN_PLAY:
        fieldModel.setBallInPlay((Boolean) getValue1());
        break;
      case ADD_BLOOD_SPOT:
        fieldModel.add((BloodSpot) getValue1());
        break;
      case ADD_TRACK_NUMBER:
        fieldModel.add((TrackNumber) getValue1());
        break;
      case REMOVE_TRACK_NUMBER:
        fieldModel.remove((TrackNumber) getValue1());
        break;
      case ADD_PUSHBACK_SQUARE:
        fieldModel.add((PushbackSquare) getValue1());
        break;
      case REMOVE_PUSHBACK_SQUARE:
        fieldModel.remove((PushbackSquare) getValue1());
        break;
      case ADD_MOVE_SQUARE:
        fieldModel.add((MoveSquare) getValue1());
        break;
      case REMOVE_MOVE_SQUARE:
        fieldModel.remove((MoveSquare) getValue1());
        break;
      case ADD_DICE_DECORATION:
        fieldModel.add((DiceDecoration) getValue1());
        break;
      case REMOVE_DICE_DECORATION:
        fieldModel.remove((DiceDecoration) getValue1());
        break;
      case ADD_FIELD_MARKER:
        fieldModel.add((FieldMarker) getValue1());
        break;
      case REMOVE_FIELD_MARKER:
        fieldModel.remove((FieldMarker) getValue1());
        break;
      case ADD_PLAYER_MARKER:
        fieldModel.add((PlayerMarker) getValue1());
        break;
      case REMOVE_PLAYER_MARKER:
        fieldModel.remove((PlayerMarker) getValue1());
        break;
      case SET_WEATHER:
        fieldModel.setWeather((Weather) getValue1());
        break;
      case SET_RANGE_RULER:
        fieldModel.setRangeRuler((RangeRuler) getValue1());
        break;
      case ADD_CARD:
      	fieldModel.addCard(pGame.getPlayerById((String) getValue1()), (Card) getValue2());
      	break;
      case REMOVE_CARD:
      	fieldModel.removeCard(pGame.getPlayerById((String) getValue1()), (Card) getValue2());
      	break;
      default:
        throw new IllegalStateException("Unhandled change " + getChange() + ".");
    }
    pGame.setTrackingChanges(trackingChanges);
  }
 
  // transformation
  
  public IModelChange transform() {
    switch (getChange()) {
      case SET_PLAYER_COOORDINATE:
        return new ModelChangeFieldModel(getChange(), getValue1(), FieldCoordinate.transform((FieldCoordinate) getValue2()));
      case SET_BALL_COORDINATE:
        return new ModelChangeFieldModel(getChange(), FieldCoordinate.transform((FieldCoordinate) getValue1()), getValue2());
      case SET_BOMB_COORDINATE:
        return new ModelChangeFieldModel(getChange(), FieldCoordinate.transform((FieldCoordinate) getValue1()), getValue2());
      case ADD_BLOOD_SPOT:
        return new ModelChangeFieldModel(getChange(), BloodSpot.transform((BloodSpot) getValue1()), getValue2());
      case ADD_TRACK_NUMBER:
      case REMOVE_TRACK_NUMBER:
        return new ModelChangeFieldModel(getChange(), TrackNumber.transform((TrackNumber) getValue1()), getValue2());
      case ADD_PUSHBACK_SQUARE:
      case REMOVE_PUSHBACK_SQUARE:
        return new ModelChangeFieldModel(getChange(), PushbackSquare.transform((PushbackSquare) getValue1()), getValue2());
      case ADD_MOVE_SQUARE:
      case REMOVE_MOVE_SQUARE:
        return new ModelChangeFieldModel(getChange(), MoveSquare.transform((MoveSquare) getValue1()), getValue2());
      case ADD_DICE_DECORATION:
      case REMOVE_DICE_DECORATION:
        return new ModelChangeFieldModel(getChange(), DiceDecoration.transform((DiceDecoration) getValue1()), getValue2());
      case ADD_FIELD_MARKER:
      case REMOVE_FIELD_MARKER:
        return new ModelChangeFieldModel(getChange(), FieldMarker.transform((FieldMarker) getValue1()), getValue2());
      case ADD_PLAYER_MARKER:
      case REMOVE_PLAYER_MARKER:
        return new ModelChangeFieldModel(getChange(), PlayerMarker.transform((PlayerMarker) getValue1()), getValue2());
      case SET_RANGE_RULER:
        return new ModelChangeFieldModel(getChange(), RangeRuler.transform((RangeRuler) getValue1()), getValue2());
      default:
        return new ModelChangeFieldModel(getChange(), getValue1(), getValue2());
    }
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    if (getChange() != null) {
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_CHANGE, getChange().getName());
      switch (getChange()) {
        case REMOVE_PLAYER:
        case SET_BOMB_MOVING:
        case SET_BALL_MOVING:
        case SET_BALL_IN_PLAY:
        case SET_WEATHER:
          getChange().getAttributeType1().addXmlAttribute(attributes, _XML_ATTRIBUTE_VALUE, getValue1());
          break;
        case SET_PLAYER_COOORDINATE:
          getChange().getAttributeType1().addXmlAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getValue1());
          addFieldCoordinateXmlAttributes(attributes, (FieldCoordinate) getValue2());
          break;
        case SET_PLAYER_STATE:
          getChange().getAttributeType1().addXmlAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getValue1());
          getChange().getAttributeType2().addXmlAttribute(attributes, _XML_ATTRIBUTE_VALUE, getValue2());
          break;
        case SET_BALL_COORDINATE:
          addFieldCoordinateXmlAttributes(attributes, (FieldCoordinate) getValue1());
          break;
        case SET_BOMB_COORDINATE:
          addFieldCoordinateXmlAttributes(attributes, (FieldCoordinate) getValue1());
          break;
        case ADD_CARD:
        case REMOVE_CARD:
          getChange().getAttributeType1().addXmlAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getValue1());
          getChange().getAttributeType2().addXmlAttribute(attributes, _XML_ATTRIBUTE_VALUE, getValue2());
          break;
        default:
          break;
      }
    }
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    if (getChange() != null) {
      switch (getChange()) {
        case ADD_BLOOD_SPOT:
        case ADD_TRACK_NUMBER:
        case REMOVE_TRACK_NUMBER:
        case ADD_PUSHBACK_SQUARE:
        case REMOVE_PUSHBACK_SQUARE:
        case ADD_MOVE_SQUARE:
        case REMOVE_MOVE_SQUARE:
        case ADD_DICE_DECORATION:
        case REMOVE_DICE_DECORATION:
        case ADD_FIELD_MARKER:
        case REMOVE_FIELD_MARKER:
        case ADD_PLAYER_MARKER:
        case REMOVE_PLAYER_MARKER:
        case SET_RANGE_RULER:
          ((IXmlWriteable) getValue1()).addToXml(pHandler);
          break;
        default:
          break;
      }
    }
    UtilXml.endElement(pHandler, XML_TAG);
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addByte((byte) getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) getChange().getId());
    getChange().getAttributeType1().addTo(pByteList, getValue1());
    getChange().getAttributeType2().addTo(pByteList, getValue2());
  }
  
  public int initFrom(ByteArray pByteArray) {
    ModelChangeId changeId = ModelChangeId.fromId(pByteArray.getByte());
    if (getId() != changeId) {
      throw new IllegalStateException("Wrong change id. Expected " + getId().getName() + " received " + ((changeId != null) ? changeId.getName() : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fChange = CommandFieldModelChange.fromId(pByteArray.getByte());
    if (getChange() == null) {
      throw new IllegalStateException("Attribute change must not be null.");
    }
    fValue1 = getChange().getAttributeType1().initFrom(pByteArray);
    fValue2 = getChange().getAttributeType2().initFrom(pByteArray);
    return byteArraySerializationVersion;
  }
  
  // Helper methods
  
  private void addFieldCoordinateXmlAttributes(AttributesImpl pAttributes, FieldCoordinate pFieldCoordinate) {
    if (pFieldCoordinate != null) {
      UtilXml.addAttribute(pAttributes, _XML_ATTRIBUTE_X, pFieldCoordinate.getX());
      UtilXml.addAttribute(pAttributes, _XML_ATTRIBUTE_Y, pFieldCoordinate.getY());
    }
  }
   
}
