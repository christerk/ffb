package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ClientCommandWizardSpell extends ClientCommand {
  
  private SpecialEffect fWizardSpell;
  private FieldCoordinate fTargetCoordinate;
  
  public ClientCommandWizardSpell() {
    super();
  }

  public ClientCommandWizardSpell(SpecialEffect pWizardSpell) {
  	fWizardSpell = pWizardSpell;
  }

  public ClientCommandWizardSpell(SpecialEffect pWizardSpell, FieldCoordinate pTargetCoordinate) {
  	this(pWizardSpell);
  	fTargetCoordinate = pTargetCoordinate;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_WIZARD_SPELL;
  }
  
  public SpecialEffect getWizardSpell() {
		return fWizardSpell;
	}
  
  public FieldCoordinate getTargetCoordinate() {
		return fTargetCoordinate;
	}

  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IJsonOption.WIZARD_SPELL.addTo(jsonObject, fWizardSpell);
    IJsonOption.TARGET_COORDINATE.addTo(jsonObject, fTargetCoordinate);
    return jsonObject;
  }
  
  public ClientCommandWizardSpell initFrom(JsonValue jsonValue) {
    super.initFrom(jsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
    fWizardSpell = (SpecialEffect) IJsonOption.WIZARD_SPELL.getFrom(jsonObject);
    fTargetCoordinate = IJsonOption.TARGET_COORDINATE.getFrom(jsonObject);
    return this;
  }
      
}
