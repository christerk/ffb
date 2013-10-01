package com.balancedbytes.games.ffb.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.CatchModifier;
import com.balancedbytes.games.ffb.DodgeModifier;
import com.balancedbytes.games.ffb.GazeModifier;
import com.balancedbytes.games.ffb.GoForItModifier;
import com.balancedbytes.games.ffb.IRollModifier;
import com.balancedbytes.games.ffb.InterceptionModifier;
import com.balancedbytes.games.ffb.LeapModifier;
import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PickupModifier;
import com.balancedbytes.games.ffb.RightStuffModifier;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.xml.UtilXml;



/**
 * 
 * @author Kalimar
 */
public class ReportSkillRoll implements IReport {
  
  private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";
  private static final String _XML_ATTRIBUTE_SUCCESSFUL = "successful";
  private static final String _XML_ATTRIBUTE_ROLL = "roll";
  private static final String _XML_ATTRIBUTE_MINIMUM_ROLL = "minimumRoll";
  private static final String _XML_ATTRIBUTE_RE_ROLLED = "reRolled";

  private static final String _XML_TAG_MODIFIER_LIST = "modifierList";
  private static final String _XML_TAG_MODIFIER = "modifier";
  
  private ReportId fId;
  private String fPlayerId;
  private boolean fSuccessful;
  private int fRoll;
  private int fMinimumRoll;
  private List<IRollModifier> fModifiers;
  private boolean fReRolled;
  
  public ReportSkillRoll(ReportId pId) {
    fId = pId;
    fModifiers = new ArrayList<IRollModifier>();
  }

  public ReportSkillRoll(ReportId pId, String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll, boolean pReRolled) {
    this(pId, pPlayerId, pSuccessful, pRoll, pMinimumRoll, null, pReRolled);
  }
  
  public ReportSkillRoll(ReportId pId, String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll, IRollModifier[] pRollModifiers, boolean pReRolled) {
    this(pId);
    fPlayerId = pPlayerId;
    fSuccessful = pSuccessful;
    fRoll = pRoll;
    fMinimumRoll = pMinimumRoll;
    fReRolled = pReRolled;
    add(pRollModifiers);
  }
  
  public ReportId getId() {
    return fId;
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

  public IRollModifier[] getModifiers() {
    return fModifiers.toArray(new IRollModifier[fModifiers.size()]);
  }
  
  private void add(IRollModifier pModifier) {
    if (pModifier != null) {
      fModifiers.add(pModifier);
    }
  }
  
  private void add(IRollModifier[] pModifiers) {
    if (ArrayTool.isProvided(pModifiers)) {
      for (IRollModifier modifier : pModifiers) {
        add(modifier);
      }
    }
  }
  
  public boolean hasModifier(IRollModifier pModifier) {
    return fModifiers.contains(pModifier);
  }
  
  public boolean isReRolled() {
    return fReRolled;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportSkillRoll(getId(), getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), getModifiers(), isReRolled());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    addXmlAttributes(attributes);
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    IRollModifier[] rollModifiers = getModifiers();
    if (ArrayTool.isProvided(rollModifiers)) {
      UtilXml.startElement(pHandler, _XML_TAG_MODIFIER_LIST);
      for (IRollModifier rollModifier : rollModifiers) {
        UtilXml.addValueElement(pHandler, _XML_TAG_MODIFIER, (rollModifier != null) ? rollModifier.getName() : null);
      }
      UtilXml.endElement(pHandler, _XML_TAG_MODIFIER_LIST);
    }
    UtilXml.endElement(pHandler, XML_TAG);
  }
  
  protected void addXmlAttributes(AttributesImpl pAttributes) {
    UtilXml.addAttribute(pAttributes, XML_ATTRIBUTE_ID, (getId() != null) ? getId().getName() : null);
    UtilXml.addAttribute(pAttributes, _XML_ATTRIBUTE_PLAYER_ID, getPlayerId());
    UtilXml.addAttribute(pAttributes, _XML_ATTRIBUTE_SUCCESSFUL, isSuccessful());
    UtilXml.addAttribute(pAttributes, _XML_ATTRIBUTE_ROLL, getRoll());
    UtilXml.addAttribute(pAttributes, _XML_ATTRIBUTE_MINIMUM_ROLL, getMinimumRoll());
    UtilXml.addAttribute(pAttributes, _XML_ATTRIBUTE_RE_ROLLED, isReRolled());
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    addCommonPartTo(pByteList, getByteArraySerializationVersion());
  }
  
  protected void addCommonPartTo(ByteList pByteList, int pByteArraySerializationVersion) {
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(pByteArraySerializationVersion);
    pByteList.addString(getPlayerId());
    pByteList.addBoolean(isSuccessful());
    pByteList.addByte((byte) getRoll());
    pByteList.addByte((byte) getMinimumRoll());
    IRollModifier[] modifiers = getModifiers();
    pByteList.addByte((byte) modifiers.length);
    if (ArrayTool.isProvided(modifiers)) {
      for (IRollModifier modifier : modifiers) {
        pByteList.addByte((byte) modifier.getId()); 
      }
    }
    pByteList.addBoolean(isReRolled());
  }

  public int initFrom(ByteArray pByteArray) {
    return initCommonPartFrom(pByteArray);
  }
    
  protected int initCommonPartFrom(ByteArray pByteArray) {
    ReportId reportId = ReportId.fromId(pByteArray.getSmallInt());
    if (getId() != reportId) {
      throw new IllegalStateException("Wrong report id. Expected " + getId().getName() + " received " + ((reportId != null) ? reportId.getName() : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fSuccessful = pByteArray.getBoolean();
    fRoll = pByteArray.getByte();
    fMinimumRoll = pByteArray.getByte();
    int nrOfModifiers = pByteArray.getByte();
    for (int i = 0; i < nrOfModifiers; i++) {
      int modifierId = pByteArray.getByte();
      switch (getId()) {
        case CATCH_ROLL:
          add(CatchModifier.fromId(modifierId));
          break;
        case DODGE_ROLL:
          add(DodgeModifier.fromId(modifierId));
          break;
        case GO_FOR_IT_ROLL:
          add(GoForItModifier.fromId(modifierId));
          break;
        case INTERCEPTION_ROLL:
          add(InterceptionModifier.fromId(modifierId));
          break;
        case LEAP_ROLL:
          add(LeapModifier.fromId(modifierId));
          break;
        case PASS_ROLL:
        case THROW_TEAM_MATE_ROLL:
          add(PassModifier.fromId(modifierId));
          break;
        case PICK_UP_ROLL:
          add(PickupModifier.fromId(modifierId));
          break;
        case RIGHT_STUFF_ROLL:
          add(RightStuffModifier.fromId(modifierId));
          break;
        case HYPNOTIC_GAZE_ROLL:
          add(GazeModifier.fromId(modifierId));
          break;
        default:
        	break;
      }
    }
    fReRolled = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }

}
