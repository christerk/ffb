package com.fumbbl.ffb.server.injury.injuryType;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.IInjuryContextModification;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.injury.context.InjuryContextForModification;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.injury.modification.InjuryContextModification;
import com.fumbbl.ffb.server.step.IStep;

import java.util.Optional;

public abstract class ModificationAwareInjuryTypeServer<T extends InjuryType> extends InjuryTypeServer<T> {
	ModificationAwareInjuryTypeServer(T injuryType) {
		super(injuryType);
	}

	@Override
	public final void handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
	                               Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, FieldCoordinate fromCoordinate, InjuryContext pOldInjuryContext,
	                               ApothecaryMode pApothecaryMode) {

		Optional<IInjuryContextModification> modification = pAttacker.getUnusedInjuryModification(injuryType);

		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();

		armourRoll(game, gameState, diceRoller, pAttacker, pDefender, diceInterpreter, injuryContext);

		if (modification.isPresent()) {
			boolean armourWasBroken = injuryContext.isArmorBroken();
			boolean modified = ((InjuryContextModification) modification.get()).modifyArmour(gameState, injuryContext, injuryType);

			if (modified) {
				InjuryContextForModification alternateInjuryContext = injuryContext.getAlternateInjuryContext();
				if (armourWasBroken) {
					alternateInjuryContext.clearArmorModifiers();
					armourRoll(game, gameState, diceRoller, pAttacker, pDefender, diceInterpreter, alternateInjuryContext);
				}

				injury(game, gameState, diceRoller, pAttacker, pDefender, modification, alternateInjuryContext);

			}
		}

		injury(game, gameState, diceRoller, pAttacker, pDefender, modification, injuryContext);

	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private void injury(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender, Optional<IInjuryContextModification> modification, InjuryContext currentInjuryContext) {
		if (currentInjuryContext.isArmorBroken()) {
			injuryRoll(game, gameState, diceRoller, pAttacker, pDefender, currentInjuryContext);

			if (modification.isPresent()) {
				boolean modified = ((InjuryContextModification) modification.get()).modifyInjury(currentInjuryContext, gameState);
				if (modified) {
					setInjury(pDefender, gameState, diceRoller, currentInjuryContext.getAlternateInjuryContext());
				}
			}

		} else {
			savedByArmour(currentInjuryContext);
		}
	}

	protected void savedByArmour(InjuryContext currentInjuryContext) {
		currentInjuryContext.setInjury(new PlayerState(PlayerState.PRONE));
	}

	protected abstract void injuryRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender, InjuryContext currentInjuryContext);

	protected abstract void armourRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender, DiceInterpreter diceInterpreter, InjuryContext currentInjuryContext);
}
