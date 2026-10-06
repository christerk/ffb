package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JumpContext implements ModifierContext {
	private final Game game;
	private final Player<?> player;
	private final FieldCoordinate from, to;
	private final Set<Skill> selectedSkills;
	private final boolean freeModifiersOptional;
	private int accumulatedModifiers, modifierCount;

	public JumpContext(Game game, Player<?> player, FieldCoordinate from, FieldCoordinate to) {
		this(game, player, from, to, Collections.emptySet(), false);
	}

	public JumpContext(Game game, Player<?> player, FieldCoordinate from, FieldCoordinate to, Set<Skill> selectedSkills) {
		this(game, player, from, to, selectedSkills, false);
	}

	/**
	 * @param freeModifiersOptional when true the modifiers that are free and unlimited (e.g. Leap, Very Long Legs or
	 *                              Pogo) only apply when they are part of the selected skills, so that the coach can
	 *                              decline them to fail the jump on purpose
	 */
	public JumpContext(Game game, Player<?> player, FieldCoordinate from, FieldCoordinate to, Set<Skill> selectedSkills,
		boolean freeModifiersOptional) {
		this.game = game;
		this.player = player;
		this.from = from;
		this.to = to;
		this.selectedSkills = selectedSkills == null ? Collections.emptySet() : new HashSet<>(selectedSkills);
		this.freeModifiersOptional = freeModifiersOptional;
	}

	@Override
	public boolean isSkillSelected(Skill skill) {
		return selectedSkills.contains(skill);
	}

	/**
	 * @return whether the free and unlimited jump modifiers have to be selected explicitly
	 */
	public boolean areFreeModifiersOptional() {
		return freeModifiersOptional;
	}

	@Override
	public Game getGame() {
		return game;
	}

	@Override
	public Player<?> getPlayer() {
		return player;
	}

	public FieldCoordinate getFrom() {
		return from;
	}

	public FieldCoordinate getTo() {
		return to;
	}

	public int getAccumulatedModifiers() {
		return accumulatedModifiers;
	}

	public void setAccumulatedModifiers(int accumulatedModifiers) {
		this.accumulatedModifiers = accumulatedModifiers;
	}

	public void addModifierValue(int value) {
		accumulatedModifiers += value;
		modifierCount++;
	}

	public void addModifierCount(int count) {
		modifierCount+=count;
	}

	public int getModifierCount() {
		return modifierCount;
	}
}

