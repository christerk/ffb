package com.fumbbl.ffb.model;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.ReRollSourceFactory;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class BlockRoll implements IJsonSerializable {
	private String targetId;
	private PlayerState oldPlayerState;
	private boolean successFulDauntless, ownChoice, doubleTargetStrength;
	private int nrOfDice, id, proIndex;
	private int[] blockRoll, reRollDiceIndexes = new int[0];
	private int selectedIndex = -1;
	private final Set<ReRollSource> reRollSources = new HashSet<>();

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

	public boolean isDoubleTargetStrength() {
		return doubleTargetStrength;
	}

	public void setDoubleTargetStrength(boolean doubleTargetStrength) {
		this.doubleTargetStrength = doubleTargetStrength;
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

	public Set<ReRollSource> getReRollSources() {
		return reRollSources;
	}

	public void add(ReRollSource reRollSource) {
		reRollSources.add(reRollSource);
	}

	public void remove(ReRollSource reRollSource) {
		reRollSources.remove(reRollSource);
	}

	public void clearReRollSources() {
		reRollSources.clear();
	}

	public boolean has(ReRollSource reRollSource) {
		return reRollSources.contains(reRollSource);
	}

	public boolean hasReRollsLeft() {
		return !reRollSources.isEmpty();
	}

	public void setReRollDiceIndexes(int[] reRollDiceIndexes) {
		this.reRollDiceIndexes = reRollDiceIndexes;
	}

	public boolean indexWasReRolled(int index) {
		return Arrays.stream(this.reRollDiceIndexes).anyMatch(i -> i == index);
	}

	public int[] getReRollDiceIndexes() {
		return reRollDiceIndexes;
	}

	public int getProIndex() {
		return proIndex;
	}

	public void setProIndex(int proIndex) {
		this.proIndex = proIndex;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BlockRoll blockRoll1 = (BlockRoll) o;
		return successFulDauntless == blockRoll1.successFulDauntless && ownChoice == blockRoll1.ownChoice && nrOfDice == blockRoll1.nrOfDice && id == blockRoll1.id && proIndex == blockRoll1.proIndex && selectedIndex == blockRoll1.selectedIndex && Objects.equals(targetId, blockRoll1.targetId) && Objects.equals(oldPlayerState, blockRoll1.oldPlayerState) && Arrays.equals(blockRoll, blockRoll1.blockRoll) && Arrays.equals(reRollDiceIndexes, blockRoll1.reRollDiceIndexes) && Objects.equals(reRollSources, blockRoll1.reRollSources);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(targetId, oldPlayerState, successFulDauntless, ownChoice, nrOfDice, id, proIndex, selectedIndex, reRollSources);
		result = 31 * result + Arrays.hashCode(blockRoll);
		result = 31 * result + Arrays.hashCode(reRollDiceIndexes);
		return result;
	}

	@Override
	public BlockRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		targetId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		successFulDauntless = IJsonOption.SUCCESSFUL_DAUNTLESS.getFrom(source, jsonObject);
		nrOfDice = IJsonOption.NR_OF_DICE.getFrom(source, jsonObject);
		blockRoll = IJsonOption.BLOCK_ROLL.getFrom(source, jsonObject);
		selectedIndex = IJsonOption.SELECTED_INDEX.getFrom(source, jsonObject);
		ownChoice = IJsonOption.IS_OWN_CHOICE.getFrom(source, jsonObject);
		oldPlayerState = IJsonOption.PLAYER_STATE_OLD.getFrom(source, jsonObject);
		id = IJsonOption.BLOCK_ROLL_ID.getFrom(source, jsonObject);
		JsonArray sourcesArray = IJsonOption.RE_ROLL_SOURCES.getFrom(source, jsonObject);
		if (sourcesArray != null) {
			ReRollSourceFactory factory = source.getFactory(FactoryType.Factory.RE_ROLL_SOURCE);
			sourcesArray.values().stream()
				.map(value -> (ReRollSource) UtilJson.toEnumWithName(factory, value))
				.forEach(reRollSources::add);
		}
		reRollDiceIndexes = IJsonOption.RE_ROLLED_DICE_INDEXES.getFrom(source, jsonObject);
		proIndex = IJsonOption.PRO_INDEX.getFrom(source, jsonObject);
		doubleTargetStrength = toPrimitive(IJsonOption.DOUBLE_TARGET_STRENGTH.getFrom(source, jsonObject));
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
		JsonArray sourcesArray = new JsonArray();
		reRollSources.stream().map(UtilJson::toJsonValue).forEach(sourcesArray::add);
		IJsonOption.RE_ROLL_SOURCES.addTo(jsonObject, sourcesArray);
		IJsonOption.RE_ROLLED_DICE_INDEXES.addTo(jsonObject, reRollDiceIndexes);
		IJsonOption.PRO_INDEX.addTo(jsonObject, proIndex);
		IJsonOption.DOUBLE_TARGET_STRENGTH.addTo(jsonObject, doubleTargetStrength);
		return jsonObject;
	}

	private boolean toPrimitive(Boolean bool) {
		return bool != null && bool;
	}

}
