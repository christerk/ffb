package com.balancedbytes.games.ffb.report;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.IRollModifier;
import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PassingDistance;
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
      
}
