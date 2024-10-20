package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.Arrays;
import java.util.List;

/**
 * @author Kalimar
 */
public class DialogReRollParameter implements IDialogParameter {

	private String fPlayerId, defaultValueKey;
	private ReRolledAction fReRolledAction;
	private int fMinimumRoll;
	private boolean fTeamReRollOption;
	private boolean fProReRollOption;
	private boolean fFumble;
	private ReRollSource singleUseReRollSource;
	private Skill reRollSkill, modifyingSkill;
	private List<String> messages;

	private CommonProperty menuProperty;

	public DialogReRollParameter() {
		super();
	}

	public DialogReRollParameter(String pPlayerId, ReRolledAction pReRolledAction, int pMinimumRoll,
															 boolean pTeamReRollOption, boolean pProReRollOption, boolean pFumble,
															 Skill reRollSkill, ReRollSource singleUseReRollSource, Skill modifyingSkill,
															 CommonProperty menuProperty, String defaultValueKey, List<String> messages) {
		fPlayerId = pPlayerId;
		fReRolledAction = pReRolledAction;
		fMinimumRoll = pMinimumRoll;
		fTeamReRollOption = pTeamReRollOption;
		fProReRollOption = pProReRollOption;
		fFumble = pFumble;
		this.reRollSkill = reRollSkill;
		this.singleUseReRollSource = singleUseReRollSource;
		this.modifyingSkill = modifyingSkill;
		this.menuProperty = menuProperty;
		this.defaultValueKey = defaultValueKey;
		this.messages = messages;
	}

	public DialogId getId() {
		return DialogId.RE_ROLL;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public ReRolledAction getReRolledAction() {
		return fReRolledAction;
	}

	public int getMinimumRoll() {
		return fMinimumRoll;
	}

	public boolean isTeamReRollOption() {
		return fTeamReRollOption;
	}

	public boolean isProReRollOption() {
		return fProReRollOption;
	}

	public boolean isFumble() {
		return fFumble;
	}

	public Skill getReRollSkill() {
		return reRollSkill;
	}

	public ReRollSource getSingleUseReRollSource() {
		return singleUseReRollSource;
	}

	public Skill getModifyingSkill() {
		return modifyingSkill;
	}

	public CommonProperty getMenuProperty() {
		return menuProperty;
	}

	public String getDefaultValueKey() {
		return defaultValueKey;
	}

	public List<String> getMessages() {
		return messages;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogReRollParameter(getPlayerId(), getReRolledAction(), getMinimumRoll(), isTeamReRollOption(),
			isProReRollOption(), isFumble(), reRollSkill, singleUseReRollSource, modifyingSkill, menuProperty, defaultValueKey, messages);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.RE_ROLLED_ACTION.addTo(jsonObject, fReRolledAction);
		IJsonOption.MINIMUM_ROLL.addTo(jsonObject, fMinimumRoll);
		IJsonOption.TEAM_RE_ROLL_OPTION.addTo(jsonObject, fTeamReRollOption);
		IJsonOption.PRO_RE_ROLL_OPTION.addTo(jsonObject, fProReRollOption);
		IJsonOption.RE_ROLL_SOURCE_SINGLE_USE.addTo(jsonObject, singleUseReRollSource);
		IJsonOption.FUMBLE.addTo(jsonObject, fFumble);
		IJsonOption.SKILL.addTo(jsonObject, reRollSkill);
		IJsonOption.MODIFYING_SKILL.addTo(jsonObject, modifyingSkill);
		IJsonOption.DEFAULT_VALUE_KEY.addTo(jsonObject, defaultValueKey);
		if (menuProperty != null) {
			IJsonOption.MENU_PROPERTY.addTo(jsonObject, menuProperty.getKey());
		}
		if (messages != null) {
			IJsonOption.MESSAGE_ARRAY.addTo(jsonObject, messages);
		}
		return jsonObject;
	}

	public DialogReRollParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		fReRolledAction = (ReRolledAction) IJsonOption.RE_ROLLED_ACTION.getFrom(source, jsonObject);
		fMinimumRoll = IJsonOption.MINIMUM_ROLL.getFrom(source, jsonObject);
		fTeamReRollOption = IJsonOption.TEAM_RE_ROLL_OPTION.getFrom(source, jsonObject);
		fProReRollOption = IJsonOption.PRO_RE_ROLL_OPTION.getFrom(source, jsonObject);
		singleUseReRollSource = (ReRollSource) IJsonOption.RE_ROLL_SOURCE_SINGLE_USE.getFrom(source, jsonObject);
		fFumble = IJsonOption.FUMBLE.getFrom(source, jsonObject);
		reRollSkill = (Skill) IJsonOption.SKILL.getFrom(source, jsonObject);
		modifyingSkill = (Skill) IJsonOption.MODIFYING_SKILL.getFrom(source, jsonObject);
		if (IJsonOption.MENU_PROPERTY.isDefinedIn(jsonObject)) {
			menuProperty = CommonProperty.forKey(IJsonOption.MENU_PROPERTY.getFrom(source, jsonObject));
		}
		defaultValueKey = IJsonOption.DEFAULT_VALUE_KEY.getFrom(source, jsonObject);
		if (IJsonOption.MESSAGE_ARRAY.isDefinedIn(jsonObject)) {
			messages = Arrays.asList(IJsonOption.MESSAGE_ARRAY.getFrom(source, jsonObject));
		}
		return this;
	}

}
