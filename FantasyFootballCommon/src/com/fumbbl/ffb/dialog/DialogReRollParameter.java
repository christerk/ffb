package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * 
 * @author Kalimar
 */
public class DialogReRollParameter implements IDialogParameter {

	private String fPlayerId;
	private ReRolledAction fReRolledAction;
	private int fMinimumRoll;
	private boolean fTeamReRollOption;
	private boolean fProReRollOption;
	private boolean fFumble;
	private ReRollSource singleUseReRollSource;
	private Skill reRollSkill;

	public DialogReRollParameter() {
		super();
	}

	public DialogReRollParameter(String pPlayerId, ReRolledAction pReRolledAction, int pMinimumRoll,
															 boolean pTeamReRollOption, boolean pProReRollOption, boolean pFumble,
															 Skill reRollSkill, ReRollSource singleUseReRollSource) {
		fPlayerId = pPlayerId;
		fReRolledAction = pReRolledAction;
		fMinimumRoll = pMinimumRoll;
		fTeamReRollOption = pTeamReRollOption;
		fProReRollOption = pProReRollOption;
		fFumble = pFumble;
		this.reRollSkill = reRollSkill;
		this.singleUseReRollSource = singleUseReRollSource;
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

// transformation

	public IDialogParameter transform() {
		return new DialogReRollParameter(getPlayerId(), getReRolledAction(), getMinimumRoll(), isTeamReRollOption(),
			isProReRollOption(), isFumble(), reRollSkill, singleUseReRollSource);
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
		IJsonOption.RE_ROLL_SOURCE.addTo(jsonObject, singleUseReRollSource);
		IJsonOption.FUMBLE.addTo(jsonObject, fFumble);
		IJsonOption.SKILL.addTo(jsonObject, reRollSkill);
		return jsonObject;
	}

	public DialogReRollParameter initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		fReRolledAction = (ReRolledAction) IJsonOption.RE_ROLLED_ACTION.getFrom(game, jsonObject);
		fMinimumRoll = IJsonOption.MINIMUM_ROLL.getFrom(game, jsonObject);
		fTeamReRollOption = IJsonOption.TEAM_RE_ROLL_OPTION.getFrom(game, jsonObject);
		fProReRollOption = IJsonOption.PRO_RE_ROLL_OPTION.getFrom(game, jsonObject);
		singleUseReRollSource = (ReRollSource) IJsonOption.RE_ROLL_SOURCE.getFrom(game, jsonObject);
		fFumble = IJsonOption.FUMBLE.getFrom(game, jsonObject);
		reRollSkill = (Skill) IJsonOption.SKILL.getFrom(game, jsonObject);
		return this;
	}

}
