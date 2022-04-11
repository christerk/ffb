package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.BlockTarget;
import com.fumbbl.ffb.server.GameState;

import java.util.ArrayList;
import java.util.List;

@RulesCollection(RulesCollection.Rules.COMMON)
public abstract class Select extends SequenceGenerator<Select.SequenceParams> {

	public Select() {
		super(Type.Select);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final List<BlockTarget> blockTargets;
		private final boolean updatePersistence;

		public SequenceParams(GameState gameState, boolean updatePersistence) {
			this(gameState, updatePersistence, new ArrayList<>());
		}

		public SequenceParams(GameState gameState, boolean updatePersistence, List<BlockTarget> blockTargets) {
			super(gameState);
			this.updatePersistence = updatePersistence;
			this.blockTargets = blockTargets;
		}

		public boolean isUpdatePersistence() {
			return updatePersistence;
		}

		public List<BlockTarget> getBlockTargets() {
			return blockTargets;
		}
	}
}
