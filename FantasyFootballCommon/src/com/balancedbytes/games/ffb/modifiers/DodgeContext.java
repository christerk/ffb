package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;

public class DodgeContext implements ModifierContext {
	private final ActingPlayer actingPlayer;
	private final FieldCoordinate sourceCoordinate, targetCoordinate;
	private final Game game;
	private final boolean useBreakTackle;

	public DodgeContext(Game game, ActingPlayer actingPlayer, FieldCoordinate sourceCoordinate, FieldCoordinate targetCoordinate) {
		this(game, actingPlayer, sourceCoordinate, targetCoordinate, false);
	}

	public DodgeContext(Game game, ActingPlayer actingPlayer, FieldCoordinate sourceCoordinate, FieldCoordinate targetCoordinate, boolean useBreakTackle) {
		this.sourceCoordinate = sourceCoordinate;
		this.actingPlayer = actingPlayer;
		this.targetCoordinate = targetCoordinate;
		this.game = game;
		this.useBreakTackle = useBreakTackle;
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

	@Override
	public Game getGame() {
		return game;
	}

	@Override
	public Player<?> getPlayer() {
		return actingPlayer.getPlayer();
	}
}
