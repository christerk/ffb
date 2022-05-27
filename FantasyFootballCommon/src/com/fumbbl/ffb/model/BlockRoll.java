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
import com.fumbbl.ffb.model.skill.Skill;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BlockRoll implements IJsonSerializable {
	private String targetId;
	private PlayerState oldPlayerState;
	private boolean successFulDauntless, ownChoice, doubleTargetStrength, addBlockDie;
	private int nrOfDice, id, proIndex;
	private int[] blockRoll, reRollDiceIndexes = new int[0];
	private int selectedIndex = -1;
	private final Set<ReRollSource> reRollSources = new HashSet<>();
	private Skill addDieSkill;

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

	public boolean isAddBlockDie() {
		return addBlockDie;
	}

	public void setAddBlockDie(boolean addBlockDie) {
		this.addBlockDie = addBlockDie;
	}

	public Skill getAddDieSkill() {
		return addDieSkill;
	}

	public void setAddDieSkill(Skill addDieSkill) {
		this.addDieSkill = addDieSkill;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BlockRoll blockRoll1 = (BlockRoll) o;

		if (successFulDauntless != blockRoll1.successFulDauntless) return false;
		if (ownChoice != blockRoll1.ownChoice) return false;
		if (doubleTargetStrength != blockRoll1.doubleTargetStrength) return false;
		if (addBlockDie != blockRoll1.addBlockDie) return false;
		if (nrOfDice != blockRoll1.nrOfDice) return false;
		if (id != blockRoll1.id) return false;
		if (proIndex != blockRoll1.proIndex) return false;
		if (selectedIndex != blockRoll1.selectedIndex) return false;
		if (targetId != null ? !targetId.equals(blockRoll1.targetId) : blockRoll1.targetId != null) return false;
		if (oldPlayerState != null ? !oldPlayerState.equals(blockRoll1.oldPlayerState) : blockRoll1.oldPlayerState != null)
			return false;
		if (!Arrays.equals(blockRoll, blockRoll1.blockRoll)) return false;
		if (!Arrays.equals(reRollDiceIndexes, blockRoll1.reRollDiceIndexes)) return false;
		if (!reRollSources.equals(blockRoll1.reRollSources))
			return false;
		return addDieSkill != null ? addDieSkill.equals(blockRoll1.addDieSkill) : blockRoll1.addDieSkill == null;
	}

	@Override
	public int hashCode() {
		int result = targetId != null ? targetId.hashCode() : 0;
		result = 31 * result + (oldPlayerState != null ? oldPlayerState.hashCode() : 0);
		result = 31 * result + (successFulDauntless ? 1 : 0);
		result = 31 * result + (ownChoice ? 1 : 0);
		result = 31 * result + (doubleTargetStrength ? 1 : 0);
		result = 31 * result + (addBlockDie ? 1 : 0);
		result = 31 * result + nrOfDice;
		result = 31 * result + id;
		result = 31 * result + proIndex;
		result = 31 * result + Arrays.hashCode(blockRoll);
		result = 31 * result + Arrays.hashCode(reRollDiceIndexes);
		result = 31 * result + selectedIndex;
		result = 31 * result + reRollSources.hashCode();
		result = 31 * result + (addDieSkill != null ? addDieSkill.hashCode() : 0);
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
		addBlockDie = toPrimitive(IJsonOption.ADD_BLOCK_DIE.getFrom(source, jsonObject));
		if (IJsonOption.ADD_BLOCK_DIE.isDefinedIn(jsonObject)) {
			addDieSkill = (Skill) IJsonOption.ADD_BLOCK_DIE_SKILL.getFrom(source, jsonObject);
		}
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
		IJsonOption.ADD_BLOCK_DIE.addTo(jsonObject, addBlockDie);
		IJsonOption.ADD_BLOCK_DIE_SKILL.addTo(jsonObject, addDieSkill);
		return jsonObject;
	}

	private boolean toPrimitive(Boolean bool) {
		return bool != null && bool;
	}

}
