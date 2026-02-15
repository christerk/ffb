package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.BlockRollProperties;

import java.util.List;
import java.util.stream.Collectors;

public class DialogReRollBlockForTargetsPropertiesParameter implements IDialogParameter {

	private String playerId;
	private List<BlockRollProperties> blockRolls;

	public DialogReRollBlockForTargetsPropertiesParameter() {
		super();
	}

	public DialogReRollBlockForTargetsPropertiesParameter(String playerId, List<BlockRollProperties> blockRolls) {
		this.playerId = playerId;
		this.blockRolls = blockRolls;
	}

	public DialogId getId() {
		return DialogId.RE_ROLL_BLOCK_FOR_TARGETS_PROPERTIES;
	}

	public List<BlockRollProperties> getBlockRolls() {
		return blockRolls;
	}

	public String getPlayerId() {
		return playerId;
	}

// transformation

	public IDialogParameter transform() {
		return new DialogReRollBlockForTargetsPropertiesParameter(getPlayerId(), getBlockRolls());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		JsonArray array = new JsonArray();
		blockRolls.forEach(roll -> array.add(roll.toJsonValue()));
		IJsonOption.BLOCK_ROLLS.addTo(jsonObject, array);
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		return jsonObject;
	}

	public DialogReRollBlockForTargetsPropertiesParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		JsonArray array =  IJsonOption.BLOCK_ROLLS.getFrom(source, jsonObject);
		blockRolls = array.values().stream().map(roll -> new BlockRollProperties().initFrom(source, roll)).collect(Collectors.toList());
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		return this;
	}

}
