package com.fumbbl.ffb;

public interface SeriousInjury extends INamedObject {

	String getName();

	String getButtonText();

	String getDescription();

	String getRecovery();

	boolean isLasting();

	InjuryAttribute getInjuryAttribute();

	boolean isDead();

	boolean isPoison();

	boolean showSiRoll();
}
