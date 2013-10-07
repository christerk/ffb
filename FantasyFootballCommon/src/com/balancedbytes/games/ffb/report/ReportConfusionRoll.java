package com.balancedbytes.games.ffb.report;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.xml.UtilXml;



/**
 * 
 * @author Kalimar
 */
public class ReportConfusionRoll extends ReportSkillRoll {
  
  private static final String _XML_ATTRIBUTE_CONFUSION_SKILL = "confusionSkill";

  private Skill fConfusionSkill;

  public ReportConfusionRoll() {
    super(ReportId.CONFUSION_ROLL);
  }
  
  public ReportConfusionRoll(String pPlayerId, Skill pConfusionSkill, boolean pSuccessful, int pRoll, int pMinimumRoll, boolean pReRolled) {    
    super(ReportId.CONFUSION_ROLL, pPlayerId, pSuccessful, pRoll, pMinimumRoll, null, pReRolled);
    fConfusionSkill = pConfusionSkill;
  }
  
  public Skill getConfusionSkill() {
    return fConfusionSkill;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportConfusionRoll(getPlayerId(), getConfusionSkill(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled());
  }
  
  // XML serialization
  
  protected void addXmlAttributes(AttributesImpl pAttributes) {
    super.addXmlAttributes(pAttributes);
    UtilXml.addAttribute(pAttributes, _XML_ATTRIBUTE_CONFUSION_SKILL, (getConfusionSkill() != null) ? getConfusionSkill().getName() : null);
  }
  
  // ByteArray serialization
  
  @Override
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
    addCommonPartTo(pByteList, getByteArraySerializationVersion());
    pByteList.addByte((byte) ((getConfusionSkill() != null) ? getConfusionSkill().getId() : 0));
  }

  @Override
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = initCommonPartFrom(pByteArray);
    fConfusionSkill = new SkillFactory().forId(pByteArray.getByte());
    return byteArraySerializationVersion;
  }
    
}
