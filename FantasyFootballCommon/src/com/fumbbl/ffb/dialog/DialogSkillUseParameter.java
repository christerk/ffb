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
public class DialogSkillUseParameter implements IDialogParameter {

	private String fPlayerId, menuProperty, defaultValueKey;
	private Skill fSkill, modifyingSkill;
	private int fMinimumRoll;

	public DialogSkillUseParameter() {
		super();
	}

	public DialogSkillUseParameter(String pPlayerId, Skill pSkill, int pMinimumRoll) {
		this(pPlayerId, pSkill, pMinimumRoll, null, null, null);
	}

	public DialogSkillUseParameter(String pPlayerId, Skill pSkill, int pMinimumRoll, String menuProperty, String defaultValueKey) {
		this(pPlayerId, pSkill, pMinimumRoll, null, menuProperty, defaultValueKey);
	}

	public DialogSkillUseParameter(String pPlayerId, Skill pSkill, int pMinimumRoll, Skill modifyingSkill) {
		this(pPlayerId, pSkill, pMinimumRoll, modifyingSkill, null, null);
	}

	public DialogSkillUseParameter(String pPlayerId, Skill pSkill, int pMinimumRoll, Skill modifyingSkill, String menuProperty, String defaultValueKey) {
		fPlayerId = pPlayerId;
		fSkill = pSkill;
		fMinimumRoll = pMinimumRoll;
		this.modifyingSkill = modifyingSkill;
		this.menuProperty = menuProperty;
		this.defaultValueKey = defaultValueKey;
	}

	public DialogId getId() {
		return DialogId.SKILL_USE;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public Skill getSkill() {
		return fSkill;
	}

	public int getMinimumRoll() {
		return fMinimumRoll;
	}

	public Skill getModifyingSkill() {
		return modifyingSkill;
	}

	public String getMenuProperty() {
		return menuProperty;
	}

	public String getDefaultValueKey() {
		return defaultValueKey;
	}

// transformation

	public IDialogParameter transform() {
		return new DialogSkillUseParameter(getPlayerId(), getSkill(), getMinimumRoll(), modifyingSkill, menuProperty, defaultValueKey);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.SKILL.addTo(jsonObject, fSkill);
		IJsonOption.MINIMUM_ROLL.addTo(jsonObject, fMinimumRoll);
		IJsonOption.MODIFYING_SKILL.addTo(jsonObject, modifyingSkill);
		IJsonOption.MENU_PROPERTY.addTo(jsonObject, menuProperty);
		IJsonOption.DEFAULT_VALUE_KEY.addTo(jsonObject, defaultValueKey);
		return jsonObject;
	}

	public DialogSkillUseParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		fSkill = (Skill) IJsonOption.SKILL.getFrom(source, jsonObject);
		fMinimumRoll = IJsonOption.MINIMUM_ROLL.getFrom(source, jsonObject);
		modifyingSkill = (Skill) IJsonOption.MODIFYING_SKILL.getFrom(source, jsonObject);
		menuProperty = IJsonOption.MENU_PROPERTY.getFrom(source, jsonObject);
		defaultValueKey = IJsonOption.DEFAULT_VALUE_KEY.getFrom(source, jsonObject);
		return this;
	}

}
