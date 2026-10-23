package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CatchContext implements ModifierContext {
	private final Player<?> player;
	private final CatchScatterThrowInMode catchMode;
	private final Game game;
	private final boolean usingBlastIt;
	private final Set<Skill> selectedSkills;

	public CatchContext(Game game, Player<?> pPlayer, CatchScatterThrowInMode pCatchMode, Boolean usingBlastIt) {
		this(game, pPlayer, pCatchMode, usingBlastIt, Collections.emptySet());
	}

	public CatchContext(Game game, Player<?> pPlayer, CatchScatterThrowInMode pCatchMode, Boolean usingBlastIt,
	                    Set<Skill> selectedSkills) {
		this.player = pPlayer;
		this.catchMode = pCatchMode;
		this.game = game;
		this.usingBlastIt = usingBlastIt != null && usingBlastIt;
		this.selectedSkills = selectedSkills == null ? Collections.emptySet() : new HashSet<>(selectedSkills);
	}

	public Game getGame() {
		return game;
	}

	public Player<?> getPlayer() {
		return player;
	}

	public CatchScatterThrowInMode getCatchMode() {
		return catchMode;
	}

	public boolean getUsingBlastIt() {
		return usingBlastIt;
	}

	@Override
	public boolean isSkillSelected(Skill skill) {
		return selectedSkills.contains(skill);
	}
}
