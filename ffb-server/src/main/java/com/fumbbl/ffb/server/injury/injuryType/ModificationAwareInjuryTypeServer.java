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

import java.util.Collections;
import java.util.List;
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

		Optional<IInjuryContextModification> modification = Optional.empty();
		if (pAttacker != null) {
			modification = pAttacker.getUnusedInjuryModification(injuryType).filter(mod -> !mod.appliesToDefender());
		} else if (injuryType.isChainsaw() || injuryType.isVomitLike()) {
			modification = pDefender.getUnusedInjuryModification(injuryType).filter(mod -> !mod.appliesToDefender());
		}

		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();

		armourRoll(game, gameState, diceRoller, pAttacker, pDefender, pDefenderCoordinate, fromCoordinate, diceInterpreter,
			injuryContext, true);

		boolean modified = false;

		if (modification.isPresent()) {
			modified = ((InjuryContextModification<? extends ModificationParams>) modification.get())
				.modifyArmour(gameState, injuryContext, injuryType);
		}

		if (!modified && injuryContext.isArmorBroken()) {
			Optional<IInjuryContextModification> defenderModification = pDefender.getUnusedInjuryModification(injuryType)
				.filter(mod -> mod.appliesToDefender());

			if (defenderModification.isPresent()) {
				modification = defenderModification;
				modified = ((InjuryContextModification<? extends ModificationParams>) modification.get())
					.modifyArmour(gameState, injuryContext, injuryType);
			}
		}

		if (modified) {
			ModifiedInjuryContext alternateInjuryContext = injuryContext.getModifiedInjuryContext();
			alternateInjuryContext.setArmorBroken(false);
			armourRoll(game, gameState, diceRoller, pAttacker, pDefender, pDefenderCoordinate, fromCoordinate,
				diceInterpreter, alternateInjuryContext, false);

			injury(game, gameState, diceRoller, pAttacker, pDefender, pDefenderCoordinate, fromCoordinate, Optional.empty(),
				alternateInjuryContext);
		}

		injury(game, gameState, diceRoller, pAttacker, pDefender, pDefenderCoordinate, fromCoordinate, modification,
			injuryContext);

	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private void injury(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender,
			FieldCoordinate pDefenderCoordinate, FieldCoordinate fromCoordinate,
			Optional<IInjuryContextModification> modification, InjuryContext currentInjuryContext) {
		if (currentInjuryContext.isArmorBroken()) {
			injuryRoll(game, gameState, diceRoller, pAttacker, pDefender, pDefenderCoordinate, fromCoordinate, currentInjuryContext);

			if (modification.isPresent()) {
				boolean modified = ((InjuryContextModification<? extends ModificationParams>) modification.get()).modifyInjury(gameState, currentInjuryContext, injuryType);
				if (modified) {
					setInjury(pDefender, gameState, diceRoller, currentInjuryContext.getModifiedInjuryContext());
				}
			}

		} else {
			savedByArmour(currentInjuryContext);
		}
	}

	/**
	 * BB2025 injury chains: base armour evaluation of the given context, the dice are taken from the given roller.
	 */
	public void evaluateArmour(Game game, GameState gameState, DiceRoller diceRoller, Player<?> attacker,
		Player<?> defender, FieldCoordinate defenderCoordinate, FieldCoordinate fromCoordinate, InjuryContext context) {
		InjuryContext original = injuryContext;
		injuryContext = context;
		try {
			armourRoll(game, gameState, diceRoller, attacker, defender, defenderCoordinate, fromCoordinate,
				DiceInterpreter.getInstance(), context, true);
		} finally {
			injuryContext = original;
		}
	}

	/**
	 * BB2025 injury chains: base injury evaluation of the given context with broken armour.
	 */
	public void evaluateInjury(Game game, GameState gameState, DiceRoller diceRoller, Player<?> attacker,
		Player<?> defender, FieldCoordinate defenderCoordinate, FieldCoordinate fromCoordinate, InjuryContext context) {
		InjuryContext original = injuryContext;
		injuryContext = context;
		try {
			injuryRoll(game, gameState, diceRoller, attacker, defender, defenderCoordinate, fromCoordinate, context);
		} finally {
			injuryContext = original;
		}
	}

	/**
	 * BB2025 injury chains: interprets the injury roll of the context again, e.g. after skill modifiers were added.
	 */
	public void reinterpretInjury(Player<?> defender, GameState gameState, DiceRoller diceRoller, InjuryContext context) {
		setInjury(defender, gameState, diceRoller, context);
	}

	/**
	 * BB2025 injury chains: players without attacker role that may modify the injury with an optional skill and
	 * become the attacker by doing so, e.g. Arm Bar.
	 */
	public List<Player<?>> optionalSkillAttackers(Game game, Player<?> defender, FieldCoordinate fromCoordinate) {
		return Collections.emptyList();
	}

	/**
	 * BB2025 injury chains: whether the owner of an applied optional skill becomes the attacker of the injury.
	 */
	public boolean optionalSkillOwnerBecomesAttacker() {
		return false;
	}

	public void applySavedByArmour(InjuryContext context) {
		savedByArmour(context);
	}

	protected void savedByArmour(InjuryContext injuryContext) {
		injuryContext.setInjury(new PlayerState(PlayerState.PRONE));
	}

	protected abstract void injuryRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker,
		Player<?> pDefender, FieldCoordinate pDefenderCoordinate, FieldCoordinate fromCoordinate, InjuryContext injuryContext);

	protected abstract void armourRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker,
		Player<?> pDefender, FieldCoordinate pDefenderCoordinate, FieldCoordinate fromCoordinate,
		DiceInterpreter diceInterpreter, InjuryContext injuryContext, boolean roll);
}
