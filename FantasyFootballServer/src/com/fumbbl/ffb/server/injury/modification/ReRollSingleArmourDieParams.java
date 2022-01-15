package com.fumbbl.ffb.server.injury.modification;

import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.server.GameState;

public class ReRollSingleArmourDieParams extends ModificationParams {

	private boolean spottedFoul;
	private int oldValue;
	private int replaceIndex;

	public ReRollSingleArmourDieParams(GameState gameState, ModifiedInjuryContext newContext, InjuryType injuryType) {
		super(gameState, newContext, injuryType);
	}

	public boolean isSpottedFoul() {
		return spottedFoul;
	}

	public void setSpottedFoul(boolean spottedFoul) {
		this.spottedFoul = spottedFoul;
	}

	public int getOldValue() {
		return oldValue;
	}

	public void setOldValue(int oldValue) {
		this.oldValue = oldValue;
	}

	public int getReplaceIndex() {
		return replaceIndex;
	}

	public void setReplaceIndex(int replaceIndex) {
		this.replaceIndex = replaceIndex;
	}
}
