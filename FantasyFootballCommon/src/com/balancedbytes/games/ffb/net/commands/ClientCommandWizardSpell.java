package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.SpecialEffectFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ClientCommandWizardSpell extends NetCommand {
  
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

  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) ((getWizardSpell() != null) ? getWizardSpell().getId() : 0));
    pByteList.addFieldCoordinate(getTargetCoordinate());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fWizardSpell = new SpecialEffectFactory().forId(pByteArray.getByte());
    fTargetCoordinate = pByteArray.getFieldCoordinate();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.WIZARD_SPELL.addTo(jsonObject, fWizardSpell);
    IJsonOption.TARGET_COORDINATE.addTo(jsonObject, fTargetCoordinate);
    return jsonObject;
  }
  
  public ClientCommandWizardSpell initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fWizardSpell = (SpecialEffect) IJsonOption.WIZARD_SPELL.getFrom(jsonObject);
    fTargetCoordinate = IJsonOption.TARGET_COORDINATE.getFrom(jsonObject);
    return this;
  }
      
}
