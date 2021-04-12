package com.balancedbytes.games.ffb.model;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class BlockRoll implements IJsonSerializable {
	private String targetId;
	private boolean successFulDauntless, ownChoice;
	private int nrOfDice;
	private int[] blockRoll;
	private int selectedIndex = -1;

	public BlockRoll() {
	}

	public BlockRoll(String targetId) {
		this.targetId = targetId;
	}

	public String getTargetId() {
		return targetId;
	}

	public boolean isSuccessFulDauntless() {
		return successFulDauntless;
	}

	public void setSuccessFulDauntless(boolean successFulDauntless) {
		this.successFulDauntless = successFulDauntless;
	}

	public int getNrOfDice() {
		return nrOfDice;
	}

	public void setNrOfDice(int nrOfDice) {
		this.nrOfDice = nrOfDice;
	}

	public int[] getBlockRoll() {
		return blockRoll;
	}

	public void setBlockRoll(int[] blockRoll) {
		this.blockRoll = blockRoll;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public boolean isOwnChoice() {
		return ownChoice;
	}

	public void setOwnChoice(boolean ownChoice) {
		this.ownChoice = ownChoice;
	}

	public boolean needsSelection() {
		return selectedIndex < 0;
	}

	@Override
	public BlockRoll initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		targetId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		successFulDauntless = IJsonOption.SUCCESSFUL_DAUNTLESS.getFrom(game, jsonObject);
		nrOfDice = IJsonOption.NR_OF_DICE.getFrom(game, jsonObject);
		blockRoll = IJsonOption.BLOCK_ROLL.getFrom(game, jsonObject);
		selectedIndex = IJsonOption.NUMBER.getFrom(game, jsonObject);
		ownChoice = IJsonOption.IS_OWN_CHOICE.getFrom(game, jsonObject);
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.PLAYER_ID.addTo(jsonObject, targetId);
		IJsonOption.SUCCESSFUL_DAUNTLESS.addTo(jsonObject, successFulDauntless);
		IJsonOption.NR_OF_DICE.addTo(jsonObject, nrOfDice);
		IJsonOption.BLOCK_ROLL.addTo(jsonObject, blockRoll);
		IJsonOption.NUMBER.addTo(jsonObject, selectedIndex);
		IJsonOption.IS_OWN_CHOICE.addTo(jsonObject, ownChoice);
		return jsonObject;
	}
}
