package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;

@RulesCollection(RulesCollection.Rules.COMMON)
public abstract class Select extends SequenceGenerator<Select.SequenceParams> {

	public Select() {
		super(Type.Select);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final boolean updatePersistence;

		public SequenceParams(GameState gameState, boolean updatePersistence) {
			super(gameState);
			this.updatePersistence = updatePersistence;
		}

		public boolean isUpdatePersistence() {
			return updatePersistence;
		}
	}
}
