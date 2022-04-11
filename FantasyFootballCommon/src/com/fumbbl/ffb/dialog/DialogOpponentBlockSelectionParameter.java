package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.BlockRoll;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DialogOpponentBlockSelectionParameter implements IDialogParameter {

	private String teamId;
	private List<BlockRoll> blockRolls = new ArrayList<>();

	public DialogOpponentBlockSelectionParameter() {
		super();
	}

	public DialogOpponentBlockSelectionParameter(String teamId, List<BlockRoll> blockRolls) {
		this.blockRolls = blockRolls;
		this.teamId = teamId;
	}

	public DialogId getId() {
		return DialogId.OPPONENT_BLOCK_SELECTION;
	}

	public List<BlockRoll> getBlockRolls() {
		return blockRolls;
	}

	public String getTeamId() {
		return teamId;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogOpponentBlockSelectionParameter(getTeamId(), getBlockRolls());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		JsonArray array = new JsonArray();
		blockRolls.forEach(roll -> array.add(roll.toJsonValue()));
		IJsonOption.BLOCK_ROLLS.addTo(jsonObject, array);
		IJsonOption.PLAYER_ID.addTo(jsonObject, teamId);
		return jsonObject;
	}

	public DialogOpponentBlockSelectionParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		JsonArray array =  IJsonOption.BLOCK_ROLLS.getFrom(source, jsonObject);
		blockRolls = array.values().stream().map(roll -> new BlockRoll().initFrom(source, roll)).collect(Collectors.toList());
		teamId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		return this;
	}

}
