package com.balancedbytes.games.ffb.model.change.old;


import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.TeamResult;

/**
 * 
 * @author Kalimar
 */
public class ModelChangeTeamResult implements IModelChange {
  
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
  
  public ModelChangeIdOld getId() {
    return ModelChangeIdOld.TEAM_RESULT_CHANGE;
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
  }
 
  // transformation
  
  public IModelChange transform() {
    return new ModelChangeTeamResult(getChange(), !isHomeData(), getValue());
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
    fChange = CommandTeamResultChange.fromId(pByteArray.getByte());
    fHomeData = pByteArray.getBoolean();
    if (getChange() == null) {
      throw new IllegalStateException("Attribute change must not be null.");
    }
    fValue = getChange().getAttributeType().initFrom(pByteArray);
    return byteArraySerializationVersion;
  }
  
}
