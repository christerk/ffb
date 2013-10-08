package com.balancedbytes.games.ffb.model.change.old;


import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.Inducement;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.TurnData;
import com.balancedbytes.games.ffb.xml.IXmlWriteable;
import com.balancedbytes.games.ffb.xml.UtilXml;

/**
 * 
 * @author Kalimar
 */
public class ModelChangeTurnData implements IModelChange {
  
  private static final String _XML_ATTRIBUTE_CHANGE = "change";
  private static final String _XML_ATTRIBUTE_HOME_DATA = "homeData";
  private static final String _XML_ATTRIBUTE_VALUE = "value";
  
  private CommandTurnDataChange fChange;
  private boolean fHomeData;
  private Object fValue;

  protected ModelChangeTurnData() {
    super();
  }
  
  public ModelChangeTurnData(CommandTurnDataChange pChange, boolean pHomeData, Object pValue) {
    if (pChange == null) {
      throw new IllegalArgumentException("Parameter change must not be null.");
    }
    fChange = pChange;
    fHomeData = pHomeData;
    getChange().getAttributeType().checkValueType(pValue);
    fValue = pValue;
  }
  
  public ModelChangeIdOld getId() {
    return ModelChangeIdOld.TURN_DATA_CHANGE;
  }
  
  public CommandTurnDataChange getChange() {
    return fChange;
  }
  
  public boolean isHomeData() {
    return fHomeData;
  }
  
  public Object getValue() {
    return fValue;
  }
  
  public void applyTo(Game pGame) {
    boolean trackingChanges = pGame.isTrackingChanges();
    pGame.setTrackingChanges(false);
    TurnData turnData = isHomeData() ? pGame.getTurnDataHome() : pGame.getTurnDataAway();
    switch (getChange()) {
      case SET_TURN_NR:
        turnData.setTurnNr((Byte) getValue());
        break;
      case SET_RE_ROLLS:
        turnData.setReRolls((Byte) getValue());
        break;
      case SET_APOTHECARIES:
        turnData.setApothecaries((Byte) getValue());
        break;
      case SET_BLITZ_USED:
        turnData.setBlitzUsed((Boolean) getValue());
        break;
      case SET_FOUL_USED:
        turnData.setFoulUsed((Boolean) getValue());
        break;
      case SET_RE_ROLL_USED:
        turnData.setReRollUsed((Boolean) getValue());
        break;
      case SET_HAND_OVER_USED:
        turnData.setHandOverUsed((Boolean) getValue());
        break;
      case SET_PASS_USED:
        turnData.setPassUsed((Boolean) getValue());
        break;
      case ADD_INDUCEMENT:
        turnData.getInducementSet().addInducement((Inducement) getValue());
        break;
      case REMOVE_INDUCEMENT:
        turnData.getInducementSet().removeInducement((Inducement) getValue());
        break;
      case SET_FIRST_TURN_AFTER_KICKOFF:
        turnData.setFirstTurnAfterKickoff((Boolean) getValue());
        break;
      case SET_TURN_STARTED:
      	turnData.setTurnStarted((Boolean) getValue());
      	break;
      case ACTIVATE_CARD:
      	turnData.getInducementSet().activateCard((Card) getValue());
      	break;
      case DEACTIVATE_CARD:
      	turnData.getInducementSet().deactivateCard((Card) getValue());
      	break;
      case ADD_AVAILABLE_CARD:
      	turnData.getInducementSet().addAvailableCard((Card) getValue());
      	break;
      case REMOVE_AVAILABLE_CARD:
      	turnData.getInducementSet().removeAvailableCard((Card) getValue());
      	break;
      default:
        throw new IllegalStateException("Unhandled change " + getChange() + ".");
    }
    pGame.setTrackingChanges(trackingChanges);
  }
 
  // transformation
  
  public IModelChange transform() {
    return new ModelChangeTurnData(getChange(), !isHomeData(), getValue());
  }
  
  // XML serialization
    
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    if (getChange() != null) {
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_CHANGE, getChange().getName());
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_HOME_DATA, isHomeData());
      getChange().getAttributeType().addXmlAttribute(attributes, _XML_ATTRIBUTE_VALUE, getValue());
    }
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    if (CommandTurnDataChange.ADD_INDUCEMENT.equals(getChange())
      || CommandTurnDataChange.REMOVE_INDUCEMENT.equals(getChange())) {
      ((IXmlWriteable) getValue()).addToXml(pHandler);
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
    pByteList.addBoolean(isHomeData());
    getChange().getAttributeType().addTo(pByteList, getValue());
  }
  
  public int initFrom(ByteArray pByteArray) {
    ModelChangeIdOld changeId = ModelChangeIdOld.fromId(pByteArray.getByte());
    if (getId() != changeId) {
      throw new IllegalStateException("Wrong change id. Expected " + getId().getName() + " received " + ((changeId != null) ? changeId.getName() : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fChange = CommandTurnDataChange.fromId(pByteArray.getByte());
    fHomeData = pByteArray.getBoolean();
    if (getChange() == null) {
      throw new IllegalStateException("Attribute change must not be null.");
    }
    fValue = getChange().getAttributeType().initFrom(pByteArray);
    return byteArraySerializationVersion;
  }
  
}
