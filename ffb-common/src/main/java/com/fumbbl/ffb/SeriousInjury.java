package com.fumbbl.ffb;

public interface SeriousInjury extends INamedObject {

	String getName();

	String getButtonText();

	String getDescription();

	String getRecovery();

	InjuryAttribute getInjuryAttribute();

	boolean isDead();

	boolean isPoison();

	boolean showSiRoll();
}
