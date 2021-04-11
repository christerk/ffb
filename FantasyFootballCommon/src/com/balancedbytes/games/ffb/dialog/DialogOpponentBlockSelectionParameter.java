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

public class DialogOpponentBlockSelectionParameter implements IDialogParameter {

	private String teamId;
	private List<String> targetIds = new ArrayList<>();
	private Map<String, List<Integer>> blockRolls = new HashMap<>();

	public DialogOpponentBlockSelectionParameter() {
		super();
	}

	public DialogOpponentBlockSelectionParameter(String teamId, List<String> targetIds, Map<String, List<Integer>> blockRolls) {
		this.targetIds = targetIds;
		this.blockRolls = blockRolls;
		this.teamId = teamId;
	}

	public DialogId getId() {
		return DialogId.OPPONENT_BLOCK_SELECTION;
	}

	public List<String> getTargetIds() {
		return targetIds;
	}

	public Map<String, List<Integer>> getBlockRolls() {
		return blockRolls;
	}

	public String getTeamId() {
		return teamId;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogOpponentBlockSelectionParameter(getTeamId(), getTargetIds(), getBlockRolls());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_IDS.addTo(jsonObject, targetIds);
		IJsonOption.BLOCK_ROLLS_FOR_TARGETS.addTo(jsonObject, blockRolls);
		IJsonOption.PLAYER_ID.addTo(jsonObject, teamId);
		return jsonObject;
	}

	public DialogOpponentBlockSelectionParameter initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		targetIds = Arrays.asList(IJsonOption.PLAYER_IDS.getFrom(game, jsonObject));
		blockRolls = IJsonOption.BLOCK_ROLLS_FOR_TARGETS.getFrom(game, jsonObject);
		teamId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		return this;
	}

}
