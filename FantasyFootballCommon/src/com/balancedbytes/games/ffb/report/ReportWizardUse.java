package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.SpecialEffectFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ReportWizardUse implements IReport {
  
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
  
  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fTeamId = pByteArray.getString();
    fWizardSpell = new SpecialEffectFactory().forId(pByteArray.getByte());
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
    IJsonOption.WIZARD_SPELL.addTo(jsonObject, fWizardSpell);
    return jsonObject;
  }
  
  public ReportWizardUse initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fTeamId = IJsonOption.TEAM_ID.getFrom(jsonObject);
    fWizardSpell = (SpecialEffect) IJsonOption.WIZARD_SPELL.getFrom(jsonObject);
    return this;
  }
    
}
