package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;



/**
 * 
 * @author Kalimar
 */
public class ReportDauntlessRoll extends ReportSkillRoll {
  
  private int fStrength;

  public ReportDauntlessRoll() {
    super(ReportId.DAUNTLESS_ROLL);
  }

  public ReportDauntlessRoll(String pPlayerId, boolean pSuccessful, int pStrength, int pRoll, int pMinimumRoll, boolean pReRolled) {    
    super(ReportId.DAUNTLESS_ROLL, pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled);
    fStrength = pStrength;
  }
  
  public ReportId getId() {
    return ReportId.DAUNTLESS_ROLL;
  }

  public int getStrength() {
    return fStrength;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportDauntlessRoll(getPlayerId(), isSuccessful(), getStrength(), getRoll(), getMinimumRoll(), isReRolled());
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }

  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getPlayerId());
    pByteList.addBoolean(isSuccessful());
    pByteList.addByte((byte) getRoll());
    pByteList.addByte((byte) getMinimumRoll());
    pByteList.addByte((byte) 0);  // nr of modifiers
    pByteList.addBoolean(isReRolled());
    pByteList.addByte((byte) getStrength());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = initCommonPartFrom(pByteArray);
    fStrength = pByteArray.getByte();
    return byteArraySerializationVersion;
  }

  protected int initCommonPartFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fSuccessful = pByteArray.getBoolean();
    fRoll = pByteArray.getByte();
    fMinimumRoll = pByteArray.getByte();
    pByteArray.getByte();  // nr of modifiers
    fReRolled = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
    
}
