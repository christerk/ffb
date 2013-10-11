package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;



/**
 * 
 * @author Kalimar
 */
public class ReportTentaclesShadowingRoll implements IReport {
  
  private Skill fSkill;
  private String fDefenderId;
  private int[] fRoll;
  private boolean fSuccessful;
  private int fMinimumRoll;
  private boolean fReRolled;
  
  public ReportTentaclesShadowingRoll() {
    super();
  }

  public ReportTentaclesShadowingRoll(Skill pSkill, String pDefenderId, int[] pRoll, boolean pSuccessful, int pMinimumRoll, boolean pReRolled) {
    fSkill = pSkill;
    fDefenderId = pDefenderId;
    fRoll = pRoll;
    fSuccessful = pSuccessful;
    fMinimumRoll = pMinimumRoll;
    fReRolled = pReRolled;
  }
  
  public ReportId getId() {
    return ReportId.TENTACLES_SHADOWING_ROLL;
  }

  public Skill getSkill() {
    return fSkill;
  }
  
  public String getDefenderId() {
    return fDefenderId;
  }
  
  public int[] getRoll() {
    return fRoll;
  }
  
  public boolean isSuccessful() {
    return fSuccessful;
  }
  
  public int getMinimumRoll() {
    return fMinimumRoll;
  }
  
  public boolean isReRolled() {
    return fReRolled;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportTentaclesShadowingRoll(getSkill(), getDefenderId(), getRoll(), isSuccessful(), getMinimumRoll(), isReRolled());
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) ((getSkill() != null) ? getSkill().getId() : 0));
    pByteList.addString(getDefenderId());
    pByteList.addByteArray(getRoll());
    pByteList.addBoolean(isSuccessful());
    pByteList.addByte((byte) getMinimumRoll());
    pByteList.addBoolean(isReRolled());
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fSkill = new SkillFactory().forId(pByteArray.getByte());
    fDefenderId = pByteArray.getString();
    fRoll = pByteArray.getByteArrayAsIntArray();
    fSuccessful = pByteArray.getBoolean();
    fMinimumRoll = pByteArray.getByte();
    fReRolled = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
    
}
