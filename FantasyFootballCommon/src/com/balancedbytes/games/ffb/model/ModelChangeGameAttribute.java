package com.balancedbytes.games.ffb.model;


import java.util.Date;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.GameOptionValue;
import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.xml.UtilXml;

/**
 * 
 * @author Kalimar
 */
public class ModelChangeGameAttribute implements IModelChange {
  
  private static final String _XML_TAG_FIELD_COORDINATE = "fieldCoordinate";
  private static final String _XML_ATTRIBUTE_CHANGE = "change";
  private static final String _XML_ATTRIBUTE_VALUE = "value";
  private static final String _XML_ATTRIBUTE_X = "x";
  private static final String _XML_ATTRIBUTE_Y = "y";

  private CommandGameAttributeChange fChange;
  private Object fValue;
  
  protected ModelChangeGameAttribute() {
    super();
  }
  
  public ModelChangeGameAttribute(CommandGameAttributeChange pChange, Object pValue) {
    if (pChange == null) {
      throw new IllegalArgumentException("Parameter change must not be null.");
    }
    fChange = pChange;
    getChange().getAttributeType().checkValueType(pValue);
    fValue = pValue;
  }
  
  public ModelChangeId getId() {
    return ModelChangeId.GAME_ATTRIBUTE_CHANGE;
  }
  
  public CommandGameAttributeChange getChange() {
    return fChange;
  }
  
  public Object getValue() {
    return fValue;
  }
  
  public void applyTo(Game pGame) {
    boolean trackingChanges = pGame.isTrackingChanges();
    pGame.setTrackingChanges(false);
    switch (getChange()) {
      case SET_ID:
        pGame.setId((Long) getValue());
        break;
      case SET_STARTED:
        pGame.setStarted((Date) getValue());
        break;
      case SET_FINISHED:
        pGame.setFinished((Date) getValue());
        break;
      case SET_HALF:
        pGame.setHalf((Byte) getValue());
        break;
      case SET_TURN_MODE:
        pGame.setTurnMode((TurnMode) getValue());
        break;
      case SET_PASS_COORDINATE:
        pGame.setPassCoordinate((FieldCoordinate) getValue());
        break;
      case SET_HOME_PLAYING:
        pGame.setHomePlaying((Boolean) getValue());
        break;
      case SET_HOME_FIRST_OFFENSE:
        pGame.setHomeFirstOffense((Boolean) getValue());
        break;
      case SET_SETUP_OFFENSE:
        pGame.setSetupOffense((Boolean) getValue());
        break;
      case SET_WAITING_FOR_OPPONENT:
        pGame.setWaitingForOpponent((Boolean) getValue());
        break;
      case SET_DIALOG_PARAMETER:
        pGame.setDialogParameter((IDialogParameter) getValue());
        break;
      case SET_DEFENDER_ID:
        pGame.setDefenderId((String) getValue());
        break;
      case SET_DEFENDER_ACTION:
        pGame.setDefenderAction((PlayerAction) getValue());
        break;
      case SET_TIMEOUT_POSSIBLE:
        pGame.setTimeoutPossible((Boolean) getValue());
        break;
      case SET_TIMEOUT_ENFORCED:
        pGame.setTimeoutEnforced((Boolean) getValue());
        break;
      case SET_CONCESSION_POSSIBLE:
        pGame.setConcessionPossible((Boolean) getValue());
        break;
      case SET_TESTING:
        pGame.setTesting((Boolean) getValue());
        break;
      case SET_SCHEDULED:
        pGame.setScheduled((Date) getValue());
        break;
      case SET_THROWER_ID:
      	pGame.setThrowerId((String) getValue());
      	break;
      case SET_THROWER_ACTION:
      	pGame.setThrowerAction((PlayerAction) getValue());
      	break;
      case ADD_OPTION:
      	pGame.getOptions().addOption((GameOptionValue) getValue());
      	break;
      default:
        throw new IllegalStateException("Unhandled change " + getChange() + ".");
    }
    pGame.setTrackingChanges(trackingChanges);
  }
 
  // transformation
  
  public IModelChange transform() {
    switch (getChange()) {
      case SET_PASS_COORDINATE:
        return new ModelChangeGameAttribute(getChange(), FieldCoordinate.transform((FieldCoordinate) getValue()));
      case SET_HOME_PLAYING:
      case SET_HOME_FIRST_OFFENSE:
        return new ModelChangeGameAttribute(getChange(), !((Boolean) getValue()));
      case SET_DIALOG_PARAMETER:
        if (getValue() != null) {
          return new ModelChangeGameAttribute(getChange(), ((IDialogParameter) getValue()).transform());
        } else {
          return new ModelChangeGameAttribute(getChange(), getValue());
        }
      default:
        return new ModelChangeGameAttribute(getChange(), getValue());
    }
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    if (getChange() != null) {
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_CHANGE, getChange().getName());
      getChange().getAttributeType().addXmlAttribute(attributes, _XML_ATTRIBUTE_VALUE, getValue());
    }
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    if ((getChange() != null) && (getValue() != null)) {
      switch (getChange().getAttributeType()) {
        case FIELD_COORDINATE:
          attributes = new AttributesImpl();
          UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_X, ((FieldCoordinate) getValue()).getX());
          UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_Y, ((FieldCoordinate) getValue()).getY());
          UtilXml.addEmptyElement(pHandler, _XML_TAG_FIELD_COORDINATE, attributes);
          break;
        case DIALOG_PARAMETER:
          ((IDialogParameter) getValue()).addToXml(pHandler);
          break;
        case GAME_OPTION:
          ((GameOptionValue) getValue()).addToXml(pHandler);
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
    getChange().getAttributeType().addTo(pByteList, getValue());
  }
  
  public int initFrom(ByteArray pByteArray) {
    ModelChangeId changeId = ModelChangeId.fromId(pByteArray.getByte());
    if (getId() != changeId) {
      throw new IllegalStateException("Wrong change id. Expected " + getId().getName() + " received " + ((changeId != null) ? changeId.getName() : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fChange = CommandGameAttributeChange.fromId(pByteArray.getByte());
    if (getChange() == null) {
      throw new IllegalStateException("Attribute change must not be null.");
    }
    fValue = getChange().getAttributeType().initFrom(pByteArray);
    return byteArraySerializationVersion;
  }
  
}
