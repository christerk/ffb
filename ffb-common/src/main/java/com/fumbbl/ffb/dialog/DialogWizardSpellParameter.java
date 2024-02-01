package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * @author Kalimar
 */
public class DialogWizardSpellParameter extends DialogWithoutParameter {

	private String teamId;

	public DialogWizardSpellParameter() {
		super();
	}

	public DialogWizardSpellParameter(String teamId) {
		this.teamId = teamId;
	}

	public DialogId getId() {
		return DialogId.WIZARD_SPELL;
	}

	public String getTeamId() {
		return teamId;
	}
// transformation

	public IDialogParameter transform() {
		return new DialogWizardSpellParameter(teamId);
	}

	// JSON serialization

	public DialogWizardSpellParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		teamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);

		return jsonObject;
	}
}
