package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class DialogWizardSpellParameter extends DialogWithoutParameter {

	public DialogWizardSpellParameter() {
		super();
	}

	public DialogId getId() {
		return DialogId.WIZARD_SPELL;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogWizardSpellParameter();
	}

	// JSON serialization

	public DialogWizardSpellParameter initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		return this;
	}

}
