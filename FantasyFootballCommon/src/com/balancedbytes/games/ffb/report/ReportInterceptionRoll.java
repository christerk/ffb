package com.balancedbytes.games.ffb.report;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.InterceptionModifier;
import com.balancedbytes.games.ffb.InterceptionModifierFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.util.ArrayTool;

/**
 * 
 * @author Kalimar
 */
public class ReportInterceptionRoll implements IReport {

  private String fPlayerId;
  private boolean fSuccessful;
  private int fRoll;
  private int fMinimumRoll;
  private boolean fReRolled;
  private boolean fBomb;
  private List<InterceptionModifier> fModifiers;

  public ReportInterceptionRoll() {
    fModifiers = new ArrayList<InterceptionModifier>();
  }

  public ReportInterceptionRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll, boolean pReRolled, boolean pBomb, InterceptionModifier[] pModifiers) {
    this();
    fPlayerId = pPlayerId;
    fSuccessful = pSuccessful;
    fRoll = pRoll;
    fMinimumRoll = pMinimumRoll;
    fReRolled = pReRolled;
    fBomb = pBomb;
    add(pModifiers);
  }

  public ReportId getId() {
    return ReportId.INTERCEPTION_ROLL;
  }

  public String getPlayerId() {
    return fPlayerId;
  }

  public boolean isSuccessful() {
    return fSuccessful;
  }

  public int getRoll() {
    return fRoll;
  }

  public int getMinimumRoll() {
    return fMinimumRoll;
  }

  public InterceptionModifier[] getModifiers() {
    return fModifiers.toArray(new InterceptionModifier[fModifiers.size()]);
  }

  private void add(InterceptionModifier pModifier) {
    if (pModifier != null) {
      fModifiers.add(pModifier);
    }
  }

  private void add(InterceptionModifier[] pModifiers) {
    if (ArrayTool.isProvided(pModifiers)) {
      for (InterceptionModifier modifier : pModifiers) {
        add(modifier);
      }
    }
  }

  public boolean hasModifier(InterceptionModifier pModifier) {
    return fModifiers.contains(pModifier);
  }

  public boolean isReRolled() {
    return fReRolled;
  }

  public boolean isBomb() {
    return fBomb;
  }

  // transformation

  public IReport transform() {
    return new ReportInterceptionRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(), isBomb(), getModifiers());
  }

  // ByteArray serialization

  public int getByteArraySerializationVersion() {
    return 2;
  }

  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getPlayerId());
    pByteList.addBoolean(isSuccessful());
    pByteList.addByte((byte) getRoll());
    pByteList.addByte((byte) getMinimumRoll());
    InterceptionModifier[] modifiers = getModifiers();
    pByteList.addByte((byte) modifiers.length);
    if (ArrayTool.isProvided(modifiers)) {
      for (InterceptionModifier modifier : modifiers) {
        pByteList.addByte((byte) modifier.getId()); 
      }
    }
    pByteList.addBoolean(isReRolled());
    pByteList.addBoolean(isBomb());
  }

  public int initFrom(ByteArray pByteArray) {
    InterceptionModifierFactory modifierFactory = new InterceptionModifierFactory();
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion1 = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fSuccessful = pByteArray.getBoolean();
    fRoll = pByteArray.getByte();
    fMinimumRoll = pByteArray.getByte();
    int nrOfModifiers = pByteArray.getByte();
    for (int i = 0; i < nrOfModifiers; i++) {
      add(modifierFactory.forId(pByteArray.getByte()));
    }
    fReRolled = pByteArray.getBoolean();
    int byteArraySerializationVersion = byteArraySerializationVersion1;
    if (byteArraySerializationVersion > 1) {
      fBomb = pByteArray.getBoolean();
    }
    return byteArraySerializationVersion;
  }

}
