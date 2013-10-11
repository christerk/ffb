package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.KickoffResult;
import com.balancedbytes.games.ffb.KickoffResultFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;



/**
 * 
 * @author Kalimar
 */
public class ReportKickoffExtraReRoll implements IReport {

  private KickoffResult fKickoffResult;
  private int fRollHome;
  private boolean fHomeGainsReRoll;
  private int fRollAway;
  private boolean fAwayGainsReRoll;
  
  public ReportKickoffExtraReRoll() {
    super();
  }
  
  public ReportKickoffExtraReRoll(KickoffResult pKickoffResult, int pRollHome, boolean pHomeGainsReRoll, int pRollAway, boolean pAwayGainsReRoll) {
    fKickoffResult = pKickoffResult;
    fRollHome = pRollHome;
    fHomeGainsReRoll = pHomeGainsReRoll;
    fRollAway = pRollAway;
    fAwayGainsReRoll = pAwayGainsReRoll;
  }
  
  public ReportId getId() {
    return ReportId.KICKOFF_EXTRA_REROLL;
  }
  
  public KickoffResult getKickoffResult() {
    return fKickoffResult;
  }
  
  public int getRollHome() {
    return fRollHome;
  }
  
  public boolean isHomeGainsReRoll() {
    return fHomeGainsReRoll;
  }
  
  public int getRollAway() {
    return fRollAway;
  }
  
  public boolean isAwayGainsReRoll() {
    return fAwayGainsReRoll;
  }
   
  // transformation
  
  public IReport transform() {
    return new ReportKickoffExtraReRoll(getKickoffResult(), getRollAway(), isAwayGainsReRoll(), getRollHome(), isHomeGainsReRoll());
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) ((getKickoffResult() != null) ? getKickoffResult().getId() : 0));
    pByteList.addByte((byte) getRollHome());
    pByteList.addBoolean(isHomeGainsReRoll());
    pByteList.addByte((byte) getRollAway());
    pByteList.addBoolean(isAwayGainsReRoll());
  }

  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fKickoffResult = new KickoffResultFactory().forId((int) pByteArray.getByte());
    fRollHome = pByteArray.getByte();
    fHomeGainsReRoll = pByteArray.getBoolean();
    fRollAway = pByteArray.getByte();
    fAwayGainsReRoll = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
  
}
