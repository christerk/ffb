package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.KickTeamMateRange;
import com.fumbbl.ffb.model.Player;

public class RightStuffContext implements ModifierContext {
	private final Game game;
	private final Player<?> player;
	private final KickTeamMateRange ktmRange;
	private final PassResult passResult;

	public RightStuffContext(Game game, Player<?> player, PassResult passResult) {
		this.game = game;
		this.player = player;
		this.passResult = passResult;
		this.ktmRange = null;
	}

	public RightStuffContext(Game game, Player<?> player, KickTeamMateRange ktmRange) {
		this.game = game;
		this.player = player;
		this.ktmRange = ktmRange;
		this.passResult = null;
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
