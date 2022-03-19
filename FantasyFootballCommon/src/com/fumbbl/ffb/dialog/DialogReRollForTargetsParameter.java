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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogReRollForTargetsParameter implements IDialogParameter {

	private String playerId;
	private List<String> targetIds = new ArrayList<>();
	private Map<String, Integer> minimumRolls = new HashMap<>();
	private ReRolledAction reRolledAction;
	private List<String> reRollAvailableAgainst = new ArrayList<>();
	private boolean proReRollAvailable, teamReRollAvailable, consummateAvailable;
	private Skill reRollSkill;
	private ReRollSource singleUseReRollSource;

	public DialogReRollForTargetsParameter() {
		super();
	}

	public DialogReRollForTargetsParameter(String playerId, List<String> targetIds, ReRolledAction reRolledAction, Map<String, Integer> minimumRolls,
																				 List<String> reRollAvailableAgainst, boolean proReRollAvailable, boolean teamReRollAvailable,
																				 Skill reRollSkill, ReRollSource singleUseReRollSource, boolean consummateAvailable) {
		this.targetIds = targetIds;
		this.reRolledAction = reRolledAction;
		this.reRollAvailableAgainst = reRollAvailableAgainst;
		this.proReRollAvailable = proReRollAvailable;
		this.minimumRolls = minimumRolls;
		this.playerId = playerId;
		this.teamReRollAvailable = teamReRollAvailable;
		this.reRollSkill = reRollSkill;
		this.singleUseReRollSource = singleUseReRollSource;
		this.consummateAvailable = consummateAvailable;
	}

	public DialogId getId() {
		return DialogId.RE_ROLL_FOR_TARGETS;
	}

	public List<String> getTargetIds() {
		return targetIds;
	}

	public List<String> getReRollAvailableAgainst() {
		return reRollAvailableAgainst;
	}

	public boolean isProReRollAvailable() {
		return proReRollAvailable;
	}

	public ReRolledAction getReRolledAction() {
		return reRolledAction;
	}

	public Map<String, Integer> getMinimumRolls() {
		return minimumRolls;
	}

	public String getPlayerId() {
		return playerId;
	}

	public boolean isTeamReRollAvailable() {
		return teamReRollAvailable;
	}

	public Skill getReRollSkill() {
		return reRollSkill;
	}

	public ReRollSource getSingleUseReRollSource() {
		return singleUseReRollSource;
	}

	public boolean isConsummateAvailable() {
		return consummateAvailable;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogReRollForTargetsParameter(getPlayerId(), getTargetIds(), getReRolledAction(),
			getMinimumRolls(), getReRollAvailableAgainst(), isProReRollAvailable(), teamReRollAvailable, reRollSkill,
			singleUseReRollSource, consummateAvailable);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_IDS.addTo(jsonObject, targetIds);
		IJsonOption.RE_ROLLED_ACTION.addTo(jsonObject, reRolledAction);
		IJsonOption.RE_ROLL_AVAILABLE_AGAINST.addTo(jsonObject, reRollAvailableAgainst);
		IJsonOption.PRO_RE_ROLL_OPTION.addTo(jsonObject, proReRollAvailable);
		IJsonOption.TEAM_RE_ROLL_OPTION.addTo(jsonObject, teamReRollAvailable);
		IJsonOption.MINIMUM_ROLLS.addTo(jsonObject, minimumRolls);
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.SKILL.addTo(jsonObject, reRollSkill);
		IJsonOption.RE_ROLL_SOURCE_SINGLE_USE.addTo(jsonObject, singleUseReRollSource);
		IJsonOption.CONSUMMATE_OPTION.addTo(jsonObject, consummateAvailable);
		return jsonObject;
	}

	public DialogReRollForTargetsParameter initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		targetIds = Arrays.asList(IJsonOption.PLAYER_IDS.getFrom(game, jsonObject));
		reRolledAction = (ReRolledAction) IJsonOption.RE_ROLLED_ACTION.getFrom(game, jsonObject);
		reRollAvailableAgainst = Arrays.asList(IJsonOption.RE_ROLL_AVAILABLE_AGAINST.getFrom(game, jsonObject));
		proReRollAvailable = IJsonOption.PRO_RE_ROLL_OPTION.getFrom(game, jsonObject);
		teamReRollAvailable = IJsonOption.TEAM_RE_ROLL_OPTION.getFrom(game, jsonObject);
		minimumRolls = IJsonOption.MINIMUM_ROLLS.getFrom(game, jsonObject);
		playerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		reRollSkill = (Skill) IJsonOption.SKILL.getFrom(game, jsonObject);
		singleUseReRollSource = (ReRollSource) IJsonOption.RE_ROLL_SOURCE_SINGLE_USE.getFrom(game, jsonObject);
		if (IJsonOption.CONSUMMATE_OPTION.isDefinedIn(jsonObject)) {
			consummateAvailable = IJsonOption.CONSUMMATE_OPTION.getFrom(game, jsonObject);
		}
		return this;
	}

}
