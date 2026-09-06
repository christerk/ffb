package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DodgeContext implements ModifierContext {
	private final ActingPlayer actingPlayer;
	private final FieldCoordinate sourceCoordinate, targetCoordinate;
	private final Game game;
	private final boolean useBreakTackle;
	private final Set<Skill> selectedSkills;

	public DodgeContext(Game game, ActingPlayer actingPlayer, FieldCoordinate sourceCoordinate, FieldCoordinate targetCoordinate) {
		this(game, actingPlayer, sourceCoordinate, targetCoordinate, false);
	}

	public DodgeContext(Game game, ActingPlayer actingPlayer, FieldCoordinate sourceCoordinate, FieldCoordinate targetCoordinate, boolean useBreakTackle) {
		this(game, actingPlayer, sourceCoordinate, targetCoordinate, useBreakTackle, Collections.emptySet());
	}

	public DodgeContext(Game game, ActingPlayer actingPlayer, FieldCoordinate sourceCoordinate,
											FieldCoordinate targetCoordinate, Set<Skill> selectedSkills) {
		this(game, actingPlayer, sourceCoordinate, targetCoordinate, false, selectedSkills);
	}

	public DodgeContext(Game game, ActingPlayer actingPlayer, FieldCoordinate sourceCoordinate,
											FieldCoordinate targetCoordinate, boolean useBreakTackle, Set<Skill> selectedSkills) {
		this.sourceCoordinate = sourceCoordinate;
		this.actingPlayer = actingPlayer;
		this.targetCoordinate = targetCoordinate;
		this.game = game;
		this.useBreakTackle = useBreakTackle;
		this.selectedSkills = selectedSkills == null ? Collections.emptySet() : new HashSet<>(selectedSkills);
	}

	public ActingPlayer getActingPlayer() {
		return actingPlayer;
	}

	public FieldCoordinate getSourceCoordinate() {
		return sourceCoordinate;
	}

	public FieldCoordinate getTargetCoordinate() {
		return targetCoordinate;
	}

	public boolean isUseBreakTackle() {
		return useBreakTackle;
	}

	public boolean isSkillSelected(Skill skill) {
		return selectedSkills.contains(skill);
	}

	@Override
	public Game getGame() {
		return game;
	}

	@Override
	public Player<?> getPlayer() {
		return actingPlayer.getPlayer();
	}
}
