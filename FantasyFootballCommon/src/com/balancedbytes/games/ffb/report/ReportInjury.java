package com.balancedbytes.games.ffb.report;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.ArmorModifier;
import com.balancedbytes.games.ffb.InjuryModifier;
import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.SeriousInjuryFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.util.ArrayTool;


/**
 * 
 * @author Kalimar
 */
public class ReportInjury implements IReport {

  private String fAttackerId;
  private String fDefenderId;
  private InjuryType fInjuryType;
  private boolean fArmorBroken;
  private List<ArmorModifier> fArmorModifiers;
  private int[] fArmorRoll;
  private List<InjuryModifier> fInjuryModifiers;
  private int[] fInjuryRoll;
  private int[] fCasualtyRoll;
  private SeriousInjury fSeriousInjury;
  private int[] fCasualtyRollDecay;
  private SeriousInjury fSeriousInjuryDecay;
  private PlayerState fInjury;
  private PlayerState fInjuryDecay;
  
  public ReportInjury() {
    fArmorModifiers = new ArrayList<ArmorModifier>();
    fInjuryModifiers = new ArrayList<InjuryModifier>();
  }
  
  public ReportInjury(
    String pDefenderId,
    InjuryType pInjuryType,
    boolean pArmorBroken,
    ArmorModifier[] pArmorModifiers,
    int[] pArmorRoll,
    InjuryModifier[] pInjuryModifiers,
    int[] pInjuryRoll,
    int[] pCasualtyRoll,
    SeriousInjury pSeriousInjury,
    int[] pCasualtyRollDecay,
    SeriousInjury pSeriousInjuryDecay,
    PlayerState pInjury,
    PlayerState pInjuryDecay,
    String pAttackerId
  ) {
    this();
    fDefenderId = pDefenderId;
    fInjuryType = pInjuryType;
    fArmorBroken = pArmorBroken;
    add(pArmorModifiers);
    fArmorRoll = pArmorRoll;
    add(pInjuryModifiers);
    fInjuryRoll = pInjuryRoll;
    fCasualtyRoll = pCasualtyRoll;
    fSeriousInjury = pSeriousInjury;
    fCasualtyRollDecay = pCasualtyRollDecay;
    fSeriousInjuryDecay = pSeriousInjuryDecay;
    fInjury = pInjury;
    fInjuryDecay = pInjuryDecay;
    fAttackerId = pAttackerId;
  }
  
  public ReportId getId() {
    return ReportId.INJURY;
  }

  public String getDefenderId() {
    return fDefenderId;
  }
  
  public InjuryType getInjuryType() {
    return fInjuryType;
  }

  public boolean isArmorBroken() {
    return fArmorBroken;
  }
  
  public ArmorModifier[] getArmorModifiers() {
    return fArmorModifiers.toArray(new ArmorModifier[fArmorModifiers.size()]);
  }
  
  private void add(ArmorModifier pArmorModifier) {
    if (pArmorModifier != null) {
      fArmorModifiers.add(pArmorModifier);
    }
  }
  
  private void add(ArmorModifier[] pArmorModifiers) {
    if (ArrayTool.isProvided(pArmorModifiers)) {
      for (ArmorModifier armorModifier : pArmorModifiers) {
        add(armorModifier);
      }
    }
  }
  
  public int[] getArmorRoll() {
    return fArmorRoll;
  }
  
  public InjuryModifier[] getInjuryModifiers() {
    return fInjuryModifiers.toArray(new InjuryModifier[fInjuryModifiers.size()]);
  }
  
  private void add(InjuryModifier pInjuryModifier) {
    if (pInjuryModifier != null) {
      fInjuryModifiers.add(pInjuryModifier);
    }
  }
  
  private void add(InjuryModifier[] pInjuryModifiers) {
    if (ArrayTool.isProvided(pInjuryModifiers)) {
      for (InjuryModifier injuryModifier : pInjuryModifiers) {
        add(injuryModifier);
      }
    }
  }
  
  public int[] getInjuryRoll() {
    return fInjuryRoll;
  }
  
  public int[] getCasualtyRoll() {
    return fCasualtyRoll;
  }
  
  public PlayerState getInjury() {
    return fInjury;
  }
  
  public PlayerState getInjuryDecay() {
    return fInjuryDecay;
  }
  
  public SeriousInjury getSeriousInjury() {
    return fSeriousInjury;
  }
  
  public int[] getCasualtyRollDecay() {
    return fCasualtyRollDecay;
  }
  
  public SeriousInjury getSeriousInjuryDecay() {
    return fSeriousInjuryDecay;
  }
  
  public String getAttackerId() {
    return fAttackerId;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportInjury(getDefenderId(), getInjuryType(), isArmorBroken(), getArmorModifiers(), getArmorRoll(), getInjuryModifiers(), getInjuryRoll(), getCasualtyRoll(), getSeriousInjury(), getCasualtyRollDecay(), getSeriousInjuryDecay(), getInjury(), getInjuryDecay(), getAttackerId());
  }
    
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getDefenderId());
    pByteList.addByte((byte) ((getInjuryType() != null) ? getInjuryType().getId() : 0));
    pByteList.addBoolean(isArmorBroken());
    ArmorModifier[] armorModifiers = getArmorModifiers();
    pByteList.addByte((byte) armorModifiers.length);
    for (ArmorModifier armorModifier : armorModifiers) {
      pByteList.addByte((byte) armorModifier.getId()); 
    }
    pByteList.addByteArray(getArmorRoll());
    InjuryModifier[] injuryModifiers = getInjuryModifiers();
    pByteList.addByte((byte) injuryModifiers.length);
    for (InjuryModifier injuryModifier : injuryModifiers) {
      pByteList.addByte((byte) injuryModifier.getId()); 
    }
    pByteList.addByteArray(getInjuryRoll());
    pByteList.addByteArray(getCasualtyRoll());
    pByteList.addByte((byte) ((getSeriousInjury() != null) ? getSeriousInjury().getId() : 0));
    pByteList.addByteArray(getCasualtyRollDecay());
    pByteList.addByte((byte) ((getSeriousInjuryDecay() != null) ? getSeriousInjuryDecay().getId() : 0));
    pByteList.addSmallInt((getInjury() != null) ? getInjury().getId() : 0);
    pByteList.addSmallInt((getInjuryDecay() != null) ? getInjuryDecay().getId() : 0);
    pByteList.addString(getAttackerId());
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fDefenderId = pByteArray.getString();
    fInjuryType = InjuryType.fromId(pByteArray.getByte());
    fArmorBroken = pByteArray.getBoolean();
    int nrOfArmorModifiers = pByteArray.getByte();
    for (int i = 0; i < nrOfArmorModifiers; i++) {
      add(ArmorModifier.fromId(pByteArray.getByte()));
    }
    fArmorRoll = pByteArray.getByteArrayAsIntArray();
    int nrOfInjuryModifiers = pByteArray.getByte();
    for (int i = 0; i < nrOfInjuryModifiers; i++) {
      add(InjuryModifier.fromId(pByteArray.getByte()));
    }
    fInjuryRoll = pByteArray.getByteArrayAsIntArray();
    fCasualtyRoll = pByteArray.getByteArrayAsIntArray();
    fSeriousInjury = new SeriousInjuryFactory().forId(pByteArray.getByte());
    fCasualtyRollDecay = pByteArray.getByteArrayAsIntArray();
    fSeriousInjuryDecay = new SeriousInjuryFactory().forId(pByteArray.getByte());
    fInjury = new PlayerState(pByteArray.getSmallInt());
    fInjuryDecay = new PlayerState(pByteArray.getSmallInt());
    fAttackerId = pByteArray.getString();
    return byteArraySerializationVersion;
  }
  
}
