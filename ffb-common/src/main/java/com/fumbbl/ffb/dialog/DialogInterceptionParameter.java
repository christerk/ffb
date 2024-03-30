package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * @author Kalimar
 */
public class DialogInterceptionParameter implements IDialogParameter {

	private String fThrowerId;
	private Skill interceptionSkill;
	private int skillMnemonic;

	public DialogInterceptionParameter() {
		super();
	}

	public DialogInterceptionParameter(String pPlayerId) {
		this(pPlayerId, null, 0);
	}

	public DialogInterceptionParameter(String fThrowerId, Skill interceptionSkill, int skillMnemonic) {
		this.fThrowerId = fThrowerId;
		this.interceptionSkill = interceptionSkill;
		this.skillMnemonic = skillMnemonic;
	}

	public DialogId getId() {
		return DialogId.INTERCEPTION;
	}

	public String getThrowerId() {
		return fThrowerId;
	}

	public Skill getInterceptionSkill() {
		return interceptionSkill;
	}

	public int getSkillMnemonic() {
		return skillMnemonic;
	}
// transformation

	public IDialogParameter transform() {
		return new DialogInterceptionParameter(getThrowerId(), interceptionSkill, skillMnemonic);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.THROWER_ID.addTo(jsonObject, fThrowerId);
		IJsonOption.SKILL.addTo(jsonObject, interceptionSkill);
		IJsonOption.SKILL_MNEMONIC.addTo(jsonObject, skillMnemonic);
		return jsonObject;
	}

	public DialogInterceptionParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		fThrowerId = IJsonOption.THROWER_ID.getFrom(source, jsonObject);
		interceptionSkill = (Skill) IJsonOption.SKILL.getFrom(source, jsonObject);
		if (IJsonOption.SKILL_MNEMONIC.isDefinedIn(jsonObject)) {
			skillMnemonic = IJsonOption.SKILL_MNEMONIC.getFrom(source, jsonObject);
		}
		return this;
	}

}
