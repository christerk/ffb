package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public interface IRollModifier extends INamedObject {

	int getModifier();

	boolean isModifierIncluded();

	String getReportString();
}
