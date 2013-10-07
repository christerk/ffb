package com.balancedbytes.games.ffb.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.ArmorModifier;
import com.balancedbytes.games.ffb.InjuryModifier;
import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.SeriousInjuryFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.xml.UtilXml;


/**
 * 
 * @author Kalimar
 */
public class ReportInjury implements IReport {

  private static final String _XML_ATTRIBUTE_ATTACKER_ID = "attackerId";
  private static final String _XML_ATTRIBUTE_DEFENDER_ID = "defenderId";
  private static final String _XML_ATTRIBUTE_INJURY = "injury";
  private static final String _XML_ATTRIBUTE_INJURY_DECAY = "injuryDecay";
  private static final String _XML_ATTRIBUTE_INJURY_TYPE = "injuryType";
  private static final String _XML_ATTRIBUTE_ARMOR_BROKEN = "armorBroken";
  private static final String _XML_ATTRIBUTE_ARMOR_ROLL = "armorRoll";
  private static final String _XML_ATTRIBUTE_INJURY_ROLL = "injuryRoll";
  private static final String _XML_ATTRIBUTE_CASUALTY_ROLL = "casualtyRoll";
  private static final String _XML_ATTRIBUTE_SERIOUS_INJURY = "seriousInjury";
  private static final String _XML_ATTRIBUTE_CASUALTY_ROLL_DECAY = "casualtyRollDecay";
  private static final String _XML_ATTRIBUTE_SERIOUS_INJURY_DECAY = "seriousInjuryDecay";

  private static final String _XML_TAG_ARMOR_MODIFIER_LIST = "armorModifierList";
  private static final String _XML_TAG_ARMOR_MODIFIER = "armorModifier";
  private static final String _XML_TAG_INJURY_MODIFIER_LIST = "injuryModifierList";
  private static final String _XML_TAG_INJURY_MODIFIER = "injuryModifier";
  
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
    
  // XML serialization

  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_DEFENDER_ID, getDefenderId());
    String injuryTypeName = (getInjuryType() != null) ? getInjuryType().getName() : null;
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_INJURY_TYPE, injuryTypeName);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ARMOR_BROKEN, isArmorBroken());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ARMOR_ROLL, getArmorRoll());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_INJURY_ROLL, getInjuryRoll());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_CASUALTY_ROLL, getCasualtyRoll());
    String seriousInjuryName = (getSeriousInjury() != null) ? getSeriousInjury().getName() : null;
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SERIOUS_INJURY, seriousInjuryName);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_CASUALTY_ROLL_DECAY, getCasualtyRollDecay());
    String seriousInjuryDecayName = (getSeriousInjuryDecay() != null) ? getSeriousInjuryDecay().getName() : null;
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SERIOUS_INJURY_DECAY, seriousInjuryDecayName);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_INJURY, (getInjury() != null) ? getInjury().getId() : 0);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_INJURY_DECAY, (getInjuryDecay() != null) ? getInjuryDecay().getId() : 0);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ATTACKER_ID, getAttackerId());
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    ArmorModifier[] armorModifiers = getArmorModifiers();
    if (ArrayTool.isProvided(armorModifiers)) {
      UtilXml.startElement(pHandler, _XML_TAG_ARMOR_MODIFIER_LIST);
      for (ArmorModifier armorModifier : armorModifiers) {
        String armorModifierName = (armorModifier != null) ? armorModifier.getName() : null;
        UtilXml.addValueElement(pHandler, _XML_TAG_ARMOR_MODIFIER, armorModifierName);
      }
      UtilXml.endElement(pHandler, _XML_TAG_ARMOR_MODIFIER_LIST);
    }
    InjuryModifier[] injuryModifiers = getInjuryModifiers();
    if (ArrayTool.isProvided(injuryModifiers)) {
      UtilXml.startElement(pHandler, _XML_TAG_INJURY_MODIFIER_LIST);
      for (InjuryModifier injuryModifier : injuryModifiers) {
        String injuryModifierName = (injuryModifier != null) ? injuryModifier.getName() : null;
        UtilXml.addValueElement(pHandler, _XML_TAG_INJURY_MODIFIER, injuryModifierName);
      }
      UtilXml.endElement(pHandler, _XML_TAG_INJURY_MODIFIER_LIST);
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
    SeriousInjuryFactory seriousInjuryFactory = new SeriousInjuryFactory();
    ReportId reportId = ReportId.fromId(pByteArray.getSmallInt());
    if (getId() != reportId) {
      throw new IllegalStateException("Wrong report id. Expected " + getId().getName() + " received " + ((reportId != null) ? reportId.getName() : "null"));
    }
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
    fSeriousInjury = seriousInjuryFactory.forId(pByteArray.getByte());
    fCasualtyRollDecay = pByteArray.getByteArrayAsIntArray();
    fSeriousInjuryDecay = seriousInjuryFactory.forId(pByteArray.getByte());
    fInjury = new PlayerState(pByteArray.getSmallInt());
    fInjuryDecay = new PlayerState(pByteArray.getSmallInt());
    fAttackerId = pByteArray.getString();
    return byteArraySerializationVersion;
  }
  
}
