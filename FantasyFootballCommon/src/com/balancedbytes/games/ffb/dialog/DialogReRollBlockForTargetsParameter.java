package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogReRollBlockForTargetsParameter implements IDialogParameter {

	private String playerId;
	private List<String> targetIds = new ArrayList<>();
	private Map<String, List<Integer>> blockRolls = new HashMap<>();
	private Map<String, Boolean> choices = new HashMap<>();
	private List<String> reRollAvailableAgainst = new ArrayList<>();
	private boolean proReRollAvailable, teamReRollAvailable;

	public DialogReRollBlockForTargetsParameter() {
		super();
	}

	public DialogReRollBlockForTargetsParameter(String playerId, List<String> targetIds, Map<String, List<Integer>> blockRolls,
	                                            List<String> reRollAvailableAgainst, Map<String, Boolean> choices, boolean proReRollAvailable, boolean teamReRollAvailable) {
		this.targetIds = targetIds;
		this.reRollAvailableAgainst = reRollAvailableAgainst;
		this.proReRollAvailable = proReRollAvailable;
		this.blockRolls = blockRolls;
		this.playerId = playerId;
		this.teamReRollAvailable = teamReRollAvailable;
		this.choices = choices;
	}

	public DialogId getId() {
		return DialogId.RE_ROLL_BLOCK_FOR_TARGETS;
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

	public Map<String, List<Integer>> getBlockRolls() {
		return blockRolls;
	}

	public String getPlayerId() {
		return playerId;
	}

	public boolean isTeamReRollAvailable() {
		return teamReRollAvailable;
	}

	public Map<String, Boolean> getChoices() {
		return choices;
	}
	// transformation

	public IDialogParameter transform() {
		return new DialogReRollBlockForTargetsParameter(getPlayerId(), getTargetIds(), getBlockRolls(), getReRollAvailableAgainst(), choices, isProReRollAvailable(), teamReRollAvailable);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_IDS.addTo(jsonObject, targetIds);
		IJsonOption.RE_ROLL_AVAILABLE_AGAINST.addTo(jsonObject, reRollAvailableAgainst);
		IJsonOption.PRO_RE_ROLL_OPTION.addTo(jsonObject, proReRollAvailable);
		IJsonOption.TEAM_RE_ROLL_OPTION.addTo(jsonObject, teamReRollAvailable);
		IJsonOption.BLOCK_ROLLS_FOR_TARGETS.addTo(jsonObject, blockRolls);
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.ARE_OWN_CHOICES.addTo(jsonObject, choices);
		return jsonObject;
	}

	public DialogReRollBlockForTargetsParameter initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		targetIds = Arrays.asList(IJsonOption.PLAYER_IDS.getFrom(game, jsonObject));
		reRollAvailableAgainst = Arrays.asList(IJsonOption.RE_ROLL_AVAILABLE_AGAINST.getFrom(game, jsonObject));
		proReRollAvailable = IJsonOption.PRO_RE_ROLL_OPTION.getFrom(game, jsonObject);
		teamReRollAvailable = IJsonOption.TEAM_RE_ROLL_OPTION.getFrom(game, jsonObject);
		blockRolls = IJsonOption.BLOCK_ROLLS_FOR_TARGETS.getFrom(game, jsonObject);
		playerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		choices = IJsonOption.ARE_OWN_CHOICES.getFrom(game, jsonObject);
		return this;
	}

}
