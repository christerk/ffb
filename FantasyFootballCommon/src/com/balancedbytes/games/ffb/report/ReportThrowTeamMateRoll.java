package com.balancedbytes.games.ffb.report;

import java.util.List;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.CatchModifier;
import com.balancedbytes.games.ffb.DodgeModifier;
import com.balancedbytes.games.ffb.GazeModifier;
import com.balancedbytes.games.ffb.GoForItModifier;
import com.balancedbytes.games.ffb.IRollModifier;
import com.balancedbytes.games.ffb.InterceptionModifier;
import com.balancedbytes.games.ffb.LeapModifier;
import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PassingDistance;
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
public class ReportThrowTeamMateRoll extends ReportSkillRoll {
  
  private static final String _XML_ATTRIBUTE_THROWN_PLAYER_ID = "thrownPlayerId";
  private static final String _XML_ATTRIBUTE_PASSING_DISTANCE = "passingDistance";  
  
  private String fThrownPlayerId;
  private PassingDistance fPassingDistance;
  private ReportId fId;
  private String fPlayerId;
  private boolean fSuccessful;
  private int fRoll;
  private int fMinimumRoll;
  private List<IRollModifier> fModifiers;
  private boolean fReRolled;
  
  public ReportThrowTeamMateRoll() {    
    super(ReportId.THROW_TEAM_MATE_ROLL);
  }

  public ReportThrowTeamMateRoll(String pThrowerId, String pThrownPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll, PassingDistance pPassingDistance, PassModifier[] pPassModifiers, boolean pReRolled) {
    super(ReportId.THROW_TEAM_MATE_ROLL, pThrowerId, pSuccessful, pRoll, pMinimumRoll, pPassModifiers, pReRolled);
    fThrownPlayerId = pThrownPlayerId;
    fPassingDistance = pPassingDistance;
  }

  public String getThrownPlayerId() {
    return fThrownPlayerId;
  }
  
  public PassingDistance getPassingDistance() {
    return fPassingDistance;
  }
  
  // transformation
  
  public IReport transform() {
    PassModifier[] transformedPassModifiers;
    IRollModifier[] rollModifiers = getModifiers();
    if (ArrayTool.isProvided(rollModifiers)) {
      transformedPassModifiers = new PassModifier[rollModifiers.length];
      for (int i = 0; i < transformedPassModifiers.length; i++) {
        transformedPassModifiers[i] = (PassModifier) rollModifiers[i];
      }
    } else {
      transformedPassModifiers = new PassModifier[0];
    }
    return new ReportThrowTeamMateRoll(getPlayerId(), getThrownPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), getPassingDistance(), transformedPassModifiers, isReRolled());
  }
  
  // XML serialization
  
  protected void addXmlAttributes(AttributesImpl pAttributes) {
    super.addXmlAttributes(pAttributes);
    UtilXml.addAttribute(pAttributes, _XML_ATTRIBUTE_THROWN_PLAYER_ID, getThrownPlayerId());
    UtilXml.addAttribute(pAttributes, _XML_ATTRIBUTE_PASSING_DISTANCE, (getPassingDistance() != null) ? getPassingDistance().getName() : null);
  }
  
  // ByteArray serialization
  
  @Override
  public int getByteArraySerializationVersion() {
    return 1;
  }

  @Override
  public void addTo(ByteList pByteList) {
    addCommonPartTo(pByteList, getByteArraySerializationVersion());
    pByteList.addString(getThrownPlayerId());
    pByteList.addByte((byte) ((getPassingDistance() != null) ? getPassingDistance().getId() : 0));
  }

  @Override
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = initCommonPartFrom(pByteArray);
    fThrownPlayerId = pByteArray.getString();
    fPassingDistance = PassingDistance.fromId(pByteArray.getByte());
    return byteArraySerializationVersion;
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

  protected int initCommonPartFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
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
