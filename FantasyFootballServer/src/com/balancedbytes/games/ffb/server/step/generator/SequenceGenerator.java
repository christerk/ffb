package com.balancedbytes.games.ffb.server.step.generator;

import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.server.GameState;

public abstract class SequenceGenerator<T extends SequenceGenerator.SequenceParams> implements INamedObject {

	private final Type type;

	protected SequenceGenerator(Type type) {
		this.type = type;
	}

	@Override
	public String getName() {
		return "Generator." + type.name();
	};

	public abstract void pushSequence(T params);

	public enum Type {
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
