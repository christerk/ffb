package com.fumbbl.ffb.server.injury.modification;

import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.server.GameState;

public class OldProModificationParams extends ModificationParams {

	private boolean selfInflicted, spottedFoul;
	private int oldValue, replaceIndex;

	public OldProModificationParams(GameState gameState, ModifiedInjuryContext newContext, InjuryType injuryType) {
		super(gameState, newContext, injuryType);
	}

	public boolean isSelfInflicted() {
		return selfInflicted;
	}

	public void setSelfInflicted(boolean selfInflicted) {
		this.selfInflicted = selfInflicted;
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
