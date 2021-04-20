package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.KickTeamMateRange;
import com.fumbbl.ffb.model.Player;

public class RightStuffContext implements ModifierContext {
	private final Game game;
	private final Player<?> player;
	private final KickTeamMateRange ktmRange;

	public RightStuffContext(Game game, Player<?> player, KickTeamMateRange ktmRange) {
		this.game = game;
		this.player = player;
		this.ktmRange = ktmRange;
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
}
