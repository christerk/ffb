package com.balancedbytes.games.ffb.model;


import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.xml.UtilXml;

/**
 * 
 * @author Kalimar
 */
public class ModelChangeTeamResult implements IModelChange {
  
  private static final String _XML_ATTRIBUTE_CHANGE = "change";
  private static final String _XML_ATTRIBUTE_HOME_DATA = "homeData";
  private static final String _XML_ATTRIBUTE_VALUE = "value";
  
  private CommandTeamResultChange fChange;
  private boolean fHomeData;
  private Object fValue;

  protected ModelChangeTeamResult() {
    super();
  }
  
  public ModelChangeTeamResult(CommandTeamResultChange pChange, boolean pHomeData, Object pValue) {
    if (pChange == null) {
      throw new IllegalArgumentException("Parameter change must not be null.");
    }
    fChange = pChange;
    fHomeData = pHomeData;
    getChange().getAttributeType().checkValueType(pValue);
    fValue = pValue;
  }
  
  public ModelChangeId getId() {
    return ModelChangeId.TEAM_RESULT_CHANGE;
  }
  
  public CommandTeamResultChange getChange() {
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
    TeamResult teamResult = isHomeData() ? pGame.getGameResult().getTeamResultHome() : pGame.getGameResult().getTeamResultAway();
    switch (getChange()) {
      case SET_SCORE:
        teamResult.setScore((Byte) getValue());
        break;
      case SET_CONCEDED:
        teamResult.setConceded((Boolean) getValue());
        break;
      case SET_SPECTATORS:
        teamResult.setSpectators((Integer) getValue());
        break;
      case SET_FAME:
        teamResult.setFame((Byte) getValue());
        break;
      case SET_WINNINGS:
        teamResult.setWinnings((Integer) getValue());
        break;
      case SET_FAN_FACTOR_MODIFIER:
        teamResult.setFanFactorModifier((Byte) getValue());
        break;
      case SET_BADLY_HURT_SUFFERED:
        teamResult.setBadlyHurtSuffered((Byte) getValue());
        break;
      case SET_SERIOUS_INJURY_SUFFERED:
        teamResult.setSeriousInjurySuffered((Byte) getValue());
        break;
      case SET_RIP_SUFFERED:
        teamResult.setRipSuffered((Byte) getValue());
        break;
      case SET_SPIRALLING_EXPENSES:
        teamResult.setSpirallingExpenses((Integer) getValue());
        break;
      case SET_RAISED_DEAD:
        teamResult.setRaisedDead((Integer) getValue());
        break;
      case SET_PETTY_CASH_TRANSFERRED:
        teamResult.setPettyCashTransferred((Integer) getValue());
        break;
      case SET_PETTY_CASH_USED:
        teamResult.setPettyCashUsed((Integer) getValue());
        break;
      case SET_TEAM_VALUE:
        teamResult.setTeamValue((Integer) getValue());
        break;
      default:
        throw new IllegalStateException("Unhandled change " + getChange() + ".");
    }
    pGame.setTrackingChanges(trackingChanges);
  }
 
  // transformation
  
  public IModelChange transform() {
    return new ModelChangeTeamResult(getChange(), !isHomeData(), getValue());
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
    UtilXml.addEmptyElement(pHandler, XML_TAG, attributes);
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
    ModelChangeId changeId = ModelChangeId.fromId(pByteArray.getByte());
    if (getId() != changeId) {
      throw new IllegalStateException("Wrong change id. Expected " + getId().getName() + " received " + ((changeId != null) ? changeId.getName() : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fChange = CommandTeamResultChange.fromId(pByteArray.getByte());
    fHomeData = pByteArray.getBoolean();
    if (getChange() == null) {
      throw new IllegalStateException("Attribute change must not be null.");
    }
    fValue = getChange().getAttributeType().initFrom(pByteArray);
    return byteArraySerializationVersion;
  }
  
}
