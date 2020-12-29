package com.balancedbytes.games.ffb;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FactoryType {

	public enum Factory {
		animationType, apothecaryMode, apothecaryStatus, blockResult, card, cardEffect,
		cardType, catchModifier, catchScatterThrowInMode, clientMode, clientStateId,
		concedeGameStatus, dialogId, direction, dodgeModifier, gameOptionId, gameStatus,
		gazeModifier, goForItModifier, inducementPhase, inducementType, injuryModifier,
		injuryType, interceptionModifier, kickoffResult, leaderState, leapModifier,
		modelChangeDataType, modelChangeId, netCommandId, passingDistance, passModifier, pickupModifier,
		playerAction, playerChoiceMode, playerGender, playerType, pushbackMode,
		reportId, reRolledAction, reRollSource, rightStuffModifier, sendToBoxReason, seriousInjury, serverStatus,
		skill, skillCategory, skillUse, soundId, specialEffect, stepAction, stepId,
		teamStatus, turnMode, weather
	}
	
	Factory value();

}
