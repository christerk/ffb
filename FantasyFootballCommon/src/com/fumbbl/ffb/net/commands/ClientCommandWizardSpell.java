package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

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

	public ClientCommandWizardSpell initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fWizardSpell = (SpecialEffect) IJsonOption.WIZARD_SPELL.getFrom(source, jsonObject);
		fTargetCoordinate = IJsonOption.TARGET_COORDINATE.getFrom(source, jsonObject);
		return this;
	}

}
