package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.server.GameState;

public abstract class SequenceGenerator<T extends SequenceGenerator.SequenceParams> implements INamedObject {

	private final Type type;

	public SequenceGenerator(Type type) {
		this.type = type;
	}

	@Override
	public String getName() {
		return type.name();
	}

	public abstract void pushSequence(T params);

	public enum Type {
		AutoGazeZoat, BalefulHex, BlitzBlock, BlitzMove, BlackInk, Block, Bomb, CatchOfTheDay, Card, EndGame, EndPlayerAction, EndTurn, Foul,
		FuriousOutburst, Inducement, KickTeamMate, Kickoff, LookIntoMyEyes,
		MultiBlock, Move, Pass, PileDriver, Punt,
		QuickBite, RaidingParty, RiotousRookies, ScatterPlayer, Select, SelectBlitzTarget, SelectGazeTarget,
		SpecialEffect, SpikedBallApo, StartGame, ThenIStartedBlastin, ThrowARock, ThrowKeg, ThrowTeamMate, Treacherous, Wizard
	}

	public static class SequenceParams {
		private final GameState gameState;

		public SequenceParams(GameState gameState) {
			this.gameState = gameState;
		}

		public GameState getGameState() {
			return gameState;
		}
	}
}
