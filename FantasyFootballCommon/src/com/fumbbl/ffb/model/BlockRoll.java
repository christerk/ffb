package com.fumbbl.ffb.model;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.BlockResult;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.factory.BlockResultFactory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;

import java.util.Arrays;
import java.util.Objects;

public class BlockRoll implements IJsonSerializable {
	private String targetId;
	private PlayerState oldPlayerState;
	private boolean successFulDauntless, ownChoice;
	private int nrOfDice, id;
	private int[] blockRoll;
	private int selectedIndex = -1, brawlerOptions, brawlerCount;

	public BlockRoll() {
	}

	public BlockRoll(String targetId, PlayerState oldPlayerState, int id) {
		this.targetId = targetId;
		this.oldPlayerState = oldPlayerState;
		this.id = id;
	}

	public int getId() {
		return id;
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

	public void setBlockRoll(Game game, int[] blockRoll) {
		this.blockRoll = blockRoll;
		BlockResultFactory factory = game.getFactory(FactoryType.Factory.BLOCK_RESULT);
		brawlerOptions = (int) Arrays.stream(blockRoll).mapToObj(factory::forRoll).filter(blockResult -> blockResult == BlockResult.BOTH_DOWN).count();
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

	public PlayerState getOldPlayerState() {
		return oldPlayerState;
	}

	public void setBrawlerOptions(int brawlerOptions) {
		this.brawlerOptions = brawlerOptions;
	}

	public int getBrawlerOptions() {
		return brawlerOptions;
	}

	public void setBrawlerCount(int brawlerCount) {
		this.brawlerCount = brawlerCount;
	}

	public int getBrawlerCount() {
		return brawlerCount;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BlockRoll blockRoll1 = (BlockRoll) o;
		return successFulDauntless == blockRoll1.successFulDauntless && ownChoice == blockRoll1.ownChoice && nrOfDice == blockRoll1.nrOfDice && id == blockRoll1.id && selectedIndex == blockRoll1.selectedIndex && brawlerOptions == blockRoll1.brawlerOptions && brawlerCount == blockRoll1.brawlerCount && Objects.equals(targetId, blockRoll1.targetId) && Objects.equals(oldPlayerState, blockRoll1.oldPlayerState) && Arrays.equals(blockRoll, blockRoll1.blockRoll);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(targetId, oldPlayerState, successFulDauntless, ownChoice, nrOfDice, id, selectedIndex, brawlerOptions, brawlerCount);
		result = 31 * result + Arrays.hashCode(blockRoll);
		return result;
	}

	@Override
	public BlockRoll initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		targetId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		successFulDauntless = IJsonOption.SUCCESSFUL_DAUNTLESS.getFrom(game, jsonObject);
		nrOfDice = IJsonOption.NR_OF_DICE.getFrom(game, jsonObject);
		blockRoll = IJsonOption.BLOCK_ROLL.getFrom(game, jsonObject);
		selectedIndex = IJsonOption.SELECTED_INDEX.getFrom(game, jsonObject);
		ownChoice = IJsonOption.IS_OWN_CHOICE.getFrom(game, jsonObject);
		oldPlayerState = IJsonOption.PLAYER_STATE_OLD.getFrom(game, jsonObject);
		id = IJsonOption.BLOCK_ROLL_ID.getFrom(game, jsonObject);
		brawlerOptions = IJsonOption.BRAWLER_OPTIONS.getFrom(game, jsonObject);
		brawlerCount = IJsonOption.BRAWLER_COUNT.getFrom(game, jsonObject);
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.PLAYER_ID.addTo(jsonObject, targetId);
		IJsonOption.SUCCESSFUL_DAUNTLESS.addTo(jsonObject, successFulDauntless);
		IJsonOption.NR_OF_DICE.addTo(jsonObject, nrOfDice);
		IJsonOption.BLOCK_ROLL.addTo(jsonObject, blockRoll);
		IJsonOption.SELECTED_INDEX.addTo(jsonObject, selectedIndex);
		IJsonOption.IS_OWN_CHOICE.addTo(jsonObject, ownChoice);
		IJsonOption.PLAYER_STATE_OLD.addTo(jsonObject, oldPlayerState);
		IJsonOption.BLOCK_ROLL_ID.addTo(jsonObject, id);
		IJsonOption.BRAWLER_OPTIONS.addTo(jsonObject, brawlerOptions);
		IJsonOption.BRAWLER_COUNT.addTo(jsonObject, brawlerCount);
		return jsonObject;
	}
}
