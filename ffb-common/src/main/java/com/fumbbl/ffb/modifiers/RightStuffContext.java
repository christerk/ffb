package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.KickTeamMateRange;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RightStuffContext implements ModifierContext {
	private final Game game;
	private final Player<?> player;
	private final KickTeamMateRange ktmRange;
	private final PassResult passResult;
	private final Set<Skill> selectedSkills;

	public RightStuffContext(Game game, Player<?> player, PassResult passResult) {
		this(game, player, passResult, Collections.emptySet());
	}

	public RightStuffContext(Game game, Player<?> player, PassResult passResult, Set<Skill> selectedSkills) {
		this.game = game;
		this.player = player;
		this.passResult = passResult;
		this.ktmRange = null;
		this.selectedSkills = selectedSkills == null ? Collections.emptySet() : new HashSet<>(selectedSkills);
	}

	public RightStuffContext(Game game, Player<?> player, KickTeamMateRange ktmRange) {
		this.game = game;
		this.player = player;
		this.ktmRange = ktmRange;
		this.passResult = null;
		this.selectedSkills = Collections.emptySet();
	}

	@Override
	public boolean isSkillSelected(Skill skill) {
		return selectedSkills.contains(skill);
	}

	@Override
	public Game getGame() {
		return game;
	}

	@Override
	public Player<?> getPlayer() {
		return player;
	}

	public KickTeamMateRange getKtmRange() {
		return ktmRange;
	}

	public PassResult getPassResult() {
		return passResult;
	}
}
