package com.balancedbytes.games.ffb.report;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.SpecialEffectFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.xml.UtilXml;



/**
 * 
 * @author Kalimar
 */
public class ReportWizardUse implements IReport {
  
  private static final String _XML_ATTRIBUTE_TEAM_ID = "teamId";
  private static final String _XML_ATTRIBUTE_WIZARD_SPELL = "wizardSpell";
  
  private String fTeamId;
  private SpecialEffect fWizardSpell;
  
  public ReportWizardUse() {
    super();
  }

  public ReportWizardUse(String pTeamId, SpecialEffect pWizardSpell) {
    fTeamId = pTeamId;
    fWizardSpell = pWizardSpell;
  }
  
  public ReportId getId() {
    return ReportId.WIZARD_USE;
  }
  
  public String getTeamId() {
    return fTeamId;
  }
  
  public SpecialEffect getWizardSpell() {
		return fWizardSpell;
	}
    
  // transformation
  
  public IReport transform() {
    return new ReportWizardUse(getTeamId(), getWizardSpell());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_ID, getTeamId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_WIZARD_SPELL, (getWizardSpell() != null) ? getWizardSpell().getName() : null);
    UtilXml.addEmptyElement(pHandler, XML_TAG, attributes);
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
    pByteList.addString(getTeamId());
    pByteList.addByte((byte) ((getWizardSpell() != null) ? getWizardSpell().getId() : 0));
  }

  public int initFrom(ByteArray pByteArray) {
    ReportId reportId = ReportId.fromId(pByteArray.getSmallInt());
    if (getId() != reportId) {
      throw new IllegalStateException("Wrong report id. Expected " + getId().getName() + " received " + ((reportId != null) ? reportId.getName() : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fTeamId = pByteArray.getString();
    fWizardSpell = new SpecialEffectFactory().forId(pByteArray.getByte());
    return byteArraySerializationVersion;
  }
    
}
