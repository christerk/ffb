package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;

public class JumpContext implements ModifierContext {
	private final Game game;
	private final Player<?> player;
	private final FieldCoordinate from, to;
	private int accumulatedModifiers;

	public JumpContext(Game game, Player<?> player, FieldCoordinate from, FieldCoordinate to) {
		this.game = game;
		this.player = player;
		this.from = from;
		this.to = to;
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

	public void addModififerValue(int value) {
		accumulatedModifiers += value;
	}
}
