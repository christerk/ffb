package com.balancedbytes.games.ffb.model.change.old;


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
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.change.ModelChange;
import com.balancedbytes.games.ffb.model.change.ModelChangeId;

/**
 * 
 * @author Kalimar
 */
public class ModelChangeFieldModel implements IModelChange {
  
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
  
  public ModelChangeIdOld getId() {
    return ModelChangeIdOld.FIELD_MODEL_CHANGE;
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
  }

  public ModelChange convert() {
    switch (getChange()) {
      case REMOVE_PLAYER:
        return new ModelChange(ModelChangeId.FIELD_MODEL_REMOVE_PLAYER, (String) getValue1(), null);
      case SET_PLAYER_COOORDINATE:
        return new ModelChange(ModelChangeId.FIELD_MODEL_SET_PLAYER_COORDINATE, (String) getValue1(), getValue2());
      case SET_PLAYER_STATE:
        return new ModelChange(ModelChangeId.FIELD_MODEL_SET_PLAYER_STATE, (String) getValue1(), getValue2());
      case SET_BALL_COORDINATE:
        return new ModelChange(ModelChangeId.FIELD_MODEL_SET_BALL_COORDINATE, null, getValue1());
      case SET_BOMB_COORDINATE:
        return new ModelChange(ModelChangeId.FIELD_MODEL_SET_BOMB_COORDINATE, null, getValue1());
      case SET_BALL_MOVING:
        return new ModelChange(ModelChangeId.FIELD_MODEL_SET_BALL_MOVING, null, getValue1());
      case SET_BOMB_MOVING:
        return new ModelChange(ModelChangeId.FIELD_MODEL_SET_BOMB_MOVING, null, getValue1());
      case SET_BALL_IN_PLAY:
        return new ModelChange(ModelChangeId.FIELD_MODEL_SET_BALL_IN_PLAY, null, getValue1());
      case ADD_BLOOD_SPOT:
        return new ModelChange(ModelChangeId.FIELD_MODEL_ADD_BLOOD_SPOT, null, getValue1());
      case ADD_TRACK_NUMBER:
        return new ModelChange(ModelChangeId.FIELD_MODEL_ADD_TRACK_NUMBER, null, getValue1());
      case REMOVE_TRACK_NUMBER:
        return new ModelChange(ModelChangeId.FIELD_MODEL_REMOVE_TRACK_NUMBER, null, getValue1());
      case ADD_PUSHBACK_SQUARE:
        return new ModelChange(ModelChangeId.FIELD_MODEL_ADD_PUSHBACK_SQUARE, null, getValue1());
      case REMOVE_PUSHBACK_SQUARE:
        return new ModelChange(ModelChangeId.FIELD_MODEL_REMOVE_PUSHBACK_SQUARE, null, getValue1());
      case ADD_MOVE_SQUARE:
        return new ModelChange(ModelChangeId.FIELD_MODEL_ADD_MOVE_SQUARE, null, getValue1());
      case REMOVE_MOVE_SQUARE:
        return new ModelChange(ModelChangeId.FIELD_MODEL_REMOVE_MOVE_SQUARE, null, getValue1());
      case ADD_DICE_DECORATION:
        return new ModelChange(ModelChangeId.FIELD_MODEL_ADD_DICE_DECORATION, null, getValue1());
      case REMOVE_DICE_DECORATION:
        return new ModelChange(ModelChangeId.FIELD_MODEL_REMOVE_DICE_DECORATION, null, getValue1());
      case ADD_FIELD_MARKER:
        return new ModelChange(ModelChangeId.FIELD_MODEL_ADD_FIELD_MARKER, null, getValue1());
      case REMOVE_FIELD_MARKER:
        return new ModelChange(ModelChangeId.FIELD_MODEL_REMOVE_FIELD_MARKER, null, getValue1());
      case ADD_PLAYER_MARKER:
        return new ModelChange(ModelChangeId.FIELD_MODEL_ADD_PLAYER_MARKER, null, getValue1());
      case REMOVE_PLAYER_MARKER:
        return new ModelChange(ModelChangeId.FIELD_MODEL_REMOVE_PLAYER_MARKER, null, getValue1());
      case SET_WEATHER:
        return new ModelChange(ModelChangeId.FIELD_MODEL_SET_WEATHER, null, getValue1());
      case SET_RANGE_RULER:
        return new ModelChange(ModelChangeId.FIELD_MODEL_SET_RANGE_RULER, null, getValue1());
      case ADD_CARD:
        return new ModelChange(ModelChangeId.FIELD_MODEL_ADD_CARD, (String) getValue1(), getValue2());
      case REMOVE_CARD:
        return new ModelChange(ModelChangeId.FIELD_MODEL_REMOVE_CARD, (String) getValue1(), getValue2());
      default:
        throw new IllegalStateException("Unhandled change " + getChange() + ".");
    }
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
  
  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
    ModelChangeIdOld changeId = ModelChangeIdOld.fromId(pByteArray.getByte());
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
    
}
