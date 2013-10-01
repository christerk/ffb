package com.balancedbytes.games.ffb.report;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.xml.UtilXml;



/**
 * 
 * @author Kalimar
 */
public class ReportDauntlessRoll extends ReportSkillRoll {
  
  private static final String _XML_ATTRIBUTE_STRENGTH = "strength";

  private int fStrength;

  public ReportDauntlessRoll() {
    super(ReportId.DAUNTLESS_ROLL);
  }

  public ReportDauntlessRoll(String pPlayerId, boolean pSuccessful, int pStrength, int pRoll, int pMinimumRoll, boolean pReRolled) {    
    super(ReportId.DAUNTLESS_ROLL, pPlayerId, pSuccessful, pRoll, pMinimumRoll, null, pReRolled);
    fStrength = pStrength;
  }
  
  public int getStrength() {
    return fStrength;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportDauntlessRoll(getPlayerId(), isSuccessful(), getStrength(), getRoll(), getMinimumRoll(), isReRolled());
  }
  
  // XML serialization
  
  protected void addXmlAttributes(AttributesImpl pAttributes) {
    super.addXmlAttributes(pAttributes);
    UtilXml.addAttribute(pAttributes, _XML_ATTRIBUTE_STRENGTH, getStrength());
  }
  
  // ByteArray serialization
  
  @Override
  public int getByteArraySerializationVersion() {
    return 1;
  }

  @Override
  public void addTo(ByteList pByteList) {
    addCommonPartTo(pByteList, getByteArraySerializationVersion());
    pByteList.addByte((byte) getStrength());
  }

  @Override
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = initCommonPartFrom(pByteArray);
    fStrength = pByteArray.getByte();
    return byteArraySerializationVersion;
  }
    
}
