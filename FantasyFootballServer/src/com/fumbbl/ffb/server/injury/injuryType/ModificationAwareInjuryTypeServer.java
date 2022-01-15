package com.fumbbl.ffb.server.injury.injuryType;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.IInjuryContextModification;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.injury.modification.InjuryContextModification;
import com.fumbbl.ffb.server.injury.modification.ModificationParams;
import com.fumbbl.ffb.server.step.IStep;

import java.util.Optional;

/**
 * Abstraction for InjuryTypes that need to handle special abilities modifying armour or injury results
 * <p>
 * Classes extending this one MUST NOT use the injuryContext member in their methods but always the passed instance.
 */
public abstract class ModificationAwareInjuryTypeServer<T extends InjuryType> extends InjuryTypeServer<T> {
	ModificationAwareInjuryTypeServer(T injuryType) {
		super(injuryType);
	}

	@Override
	public final void handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
	                               Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, FieldCoordinate fromCoordinate, InjuryContext pOldInjuryContext,
	                               ApothecaryMode pApothecaryMode) {

		Optional<IInjuryContextModification> modification = pAttacker != null ? pAttacker.getUnusedInjuryModification(injuryType) : Optional.empty();

		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();

		armourRoll(game, gameState, diceRoller, pAttacker, pDefender, diceInterpreter, injuryContext, true);

		if (modification.isPresent()) {
			boolean armourWasBroken = injuryContext.isArmorBroken();
			boolean modified = ((InjuryContextModification<? extends ModificationParams>) modification.get()).modifyArmour(gameState, injuryContext, injuryType);

			if (modified) {
				ModifiedInjuryContext alternateInjuryContext = injuryContext.getModifiedInjuryContext();
				if (armourWasBroken != alternateInjuryContext.isArmorBroken()) {
					alternateInjuryContext.setArmorBroken(false);
					armourRoll(game, gameState, diceRoller, pAttacker, pDefender, diceInterpreter, alternateInjuryContext, false);
				}

				injury(game, gameState, diceRoller, pAttacker, pDefender, modification, alternateInjuryContext);

			}
		}

		injury(game, gameState, diceRoller, pAttacker, pDefender, modification, injuryContext);

	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private void injury(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender,
	                    Optional<IInjuryContextModification> modification, InjuryContext currentInjuryContext) {
		if (currentInjuryContext.isArmorBroken()) {
			injuryRoll(game, gameState, diceRoller, pAttacker, pDefender, currentInjuryContext);

			if (modification.isPresent()) {
				boolean modified = ((InjuryContextModification<? extends ModificationParams>) modification.get()).modifyInjury(currentInjuryContext, gameState);
				if (modified) {
					setInjury(pDefender, gameState, diceRoller, currentInjuryContext.getModifiedInjuryContext());
				}
			}

		} else {
			savedByArmour(currentInjuryContext);
		}
	}

	protected void savedByArmour(InjuryContext injuryContext) {
		injuryContext.setInjury(new PlayerState(PlayerState.PRONE));
	}

	protected abstract void injuryRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender, InjuryContext injuryContext);

	protected abstract void armourRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender, DiceInterpreter diceInterpreter, InjuryContext injuryContext, boolean roll);
}
