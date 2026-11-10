package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.IServerJsonOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Dice of one injury shared by all nodes of its tree. Every entry is rolled lazily on first use with the real dice
 * roller, so the number of dice per injury is bounded and their order is deterministic (depth first build order).
 */
public class InjuryDicePool implements IJsonSerializable {

	private int[] armour;
	private int[] armourReRoll;
	private int[] injury;
	private int[] injuryReRoll;
	private int singleArmourReRoll;
	private final List<Integer> casualtyDice = new ArrayList<>();

	public int[] armour(DiceRoller diceRoller) {
		if (armour == null) {
			armour = diceRoller.rollArmour();
		}
		return copy(armour);
	}

	public int[] armourReRoll(DiceRoller diceRoller) {
		if (armourReRoll == null) {
			armourReRoll = diceRoller.rollArmour();
		}
		return copy(armourReRoll);
	}

	/**
	 * Single die replacing one armour die (Old Pro), 0 until rolled.
	 */
	public int singleArmourReRoll(DiceRoller diceRoller) {
		if (singleArmourReRoll == 0) {
			singleArmourReRoll = diceRoller.rollDice(6);
		}
		return singleArmourReRoll;
	}

	public int[] injury(DiceRoller diceRoller) {
		if (injury == null) {
			injury = diceRoller.rollInjury();
		}
		return copy(injury);
	}

	public int[] injuryReRoll(DiceRoller diceRoller) {
		if (injuryReRoll == null) {
			injuryReRoll = diceRoller.rollInjury();
		}
		return copy(injuryReRoll);
	}

	/**
	 * Casualty dice are requested in a fixed sequence (casualty roll, decay roll), every node restarts at index 0.
	 */
	public int casualtyDie(int index, int type, DiceRoller diceRoller) {
		while (casualtyDice.size() <= index) {
			casualtyDice.add(diceRoller.rollDice(type));
		}
		return casualtyDice.get(index);
	}

	private int[] copy(int[] dice) {
		return dice == null ? null : dice.clone();
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IServerJsonOption.ARMOR_ROLL.addTo(jsonObject, armour);
		IServerJsonOption.ARMOUR_RE_ROLL.addTo(jsonObject, armourReRoll);
		IServerJsonOption.SINGLE_ARMOUR_RE_ROLL.addTo(jsonObject, singleArmourReRoll);
		IServerJsonOption.INJURY_ROLL.addTo(jsonObject, injury);
		IServerJsonOption.INJURY_RE_ROLL.addTo(jsonObject, injuryReRoll);
		JsonArray casualty = new JsonArray();
		casualtyDice.forEach(casualty::add);
		IServerJsonOption.CASUALTY_DICE.addTo(jsonObject, casualty);
		return jsonObject;
	}

	@Override
	public InjuryDicePool initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		armour = IServerJsonOption.ARMOR_ROLL.getFrom(source, jsonObject);
		armourReRoll = IServerJsonOption.ARMOUR_RE_ROLL.getFrom(source, jsonObject);
		singleArmourReRoll = IServerJsonOption.SINGLE_ARMOUR_RE_ROLL.getFrom(source, jsonObject);
		injury = IServerJsonOption.INJURY_ROLL.getFrom(source, jsonObject);
		injuryReRoll = IServerJsonOption.INJURY_RE_ROLL.getFrom(source, jsonObject);
		casualtyDice.clear();
		JsonArray casualty = IServerJsonOption.CASUALTY_DICE.getFrom(source, jsonObject);
		if (casualty != null) {
			casualty.values().forEach(value -> casualtyDice.add(value.asInt()));
		}
		return this;
	}
}
