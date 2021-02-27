package com.balancedbytes.games.ffb.server.InjuryType;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.InjuryModifiers;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.factory.InjuryModifierFactory;
import com.balancedbytes.games.ffb.injury.PilingOnInjury;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.modifiers.ArmorModifier;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.UtilGameOption;
import com.balancedbytes.games.ffb.server.DiceRoller;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.util.UtilCards;

import java.util.Arrays;
import java.util.Optional;

public class InjuryTypePilingOnInjury extends InjuryTypeServer<PilingOnInjury> {
	public InjuryTypePilingOnInjury() {
		super(new PilingOnInjury());
	}

	@Override
	public InjuryContext handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
			Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext,
			ApothecaryMode pApothecaryMode) {

		if (pOldInjuryContext != null) {
			for (ArmorModifier armorModifier : pOldInjuryContext.getArmorModifiers()) {
				injuryContext.addArmorModifier(armorModifier);
			}
		}

		if (!injuryContext.isArmorBroken()) {
			injuryContext.setArmorBroken(true);
		}

		if (injuryContext.isArmorBroken()) {
			injuryContext.setInjuryRoll(diceRoller.rollInjury());
			injuryContext.addInjuryModifier(new InjuryModifierFactory().getNigglingInjuryModifier(pDefender));

			if (!UtilGameOption.isOptionEnabled(game, GameOptionId.PILING_ON_DOES_NOT_STACK)) {
				Optional<Skill> availableSkill = UtilCards.findSkillsProvidedByCardsAndEffects(game, pAttacker).stream()
					.filter(skill -> skill.hasSkillProperty(NamedProperties.affectsEitherArmourOrInjuryOnBlock)).findFirst();

				availableSkill.ifPresent(skill -> {
					if (Arrays.stream(injuryContext.getArmorModifiers())
						.noneMatch(modifier -> modifier.getRegisteredTo().isPresent() && modifier.getRegisteredTo().get().equals(skill))) {
						injuryContext.addInjuryModifier(InjuryModifiers.MIGHTY_BLOW);
					}
				});
			}

			setInjury(pDefender, gameState, diceRoller);
		} else {
			injuryContext.setInjury(new PlayerState(PlayerState.PRONE));
		}

		return injuryContext;
	}
}