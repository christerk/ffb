package com.fumbbl.ffb.server.injury.injuryType;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.factory.InjuryModifierFactory;
import com.fumbbl.ffb.injury.DropJump;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class InjuryTypeDropJump extends InjuryTypeServer<DropJump> {

	private final Player<?> divingTackler;

	public InjuryTypeDropJump() {
		this(null);
	}

	public InjuryTypeDropJump(Player<?> divingTackler) {
		super(new DropJump());
		this.divingTackler = divingTackler;
	}


	@Override
	public void handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
													 Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, FieldCoordinate vacatedFromCoordinate, InjuryContext pOldInjuryContext,
													 ApothecaryMode pApothecaryMode) {

		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();

		if (!injuryContext.isArmorBroken()) {
			injuryContext.setArmorRoll(diceRoller.rollArmour());
			if (UtilCards.hasUnusedSkillWithProperty(pDefender, NamedProperties.ignoresArmourModifiersFromSkills)) {
				injuryContext.addArmorModifiers(pDefender.getSkillWithProperty(NamedProperties.ignoresArmourModifiersFromSkills).getArmorModifiers());
			} else {
				Optional.ofNullable(pDefender.getSkillWithProperty(NamedProperties.blocksLikeChainsaw))
					.ifPresent(skill -> skill.getArmorModifiers().forEach(injuryContext::addArmorModifier));
			}
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
		}

		Skill avOrInjModifierSkill = null;

		FieldCoordinate fromCoordiante = vacatedFromCoordinate == null ? game.getFieldModel().getPlayerCoordinate(pDefender) : vacatedFromCoordinate;

		Set<Player<?>> players = Arrays.stream(UtilPlayer.findAdjacentPlayersWithTacklezones(game, game.getOtherTeam(pDefender.getTeam()), fromCoordiante, false))
			.collect(Collectors.toSet());

		if (vacatedFromCoordinate != null) {
			Player<?> shadowingOrDtPlayer = game.getFieldModel().getPlayer(vacatedFromCoordinate);

			if (shadowingOrDtPlayer != null) {
				players.add(shadowingOrDtPlayer);
			}
		}

		if (!UtilCards.hasUnusedSkillWithProperty(pDefender, NamedProperties.ignoresArmourModifiersFromSkills)) {
			avOrInjModifierSkill = players.stream().map(player -> player.getSkillWithProperty(NamedProperties.affectsEitherArmourOrInjuryOnJump))
				.filter(Objects::nonNull).findFirst().orElseGet(() -> {

					if (divingTackler != null) {
						return divingTackler.getSkillWithProperty(NamedProperties.affectsEitherArmourOrInjuryOnDodge);
					}

					return null;
				});
		}

		if (!injuryContext.isArmorBroken() && avOrInjModifierSkill != null) {
			avOrInjModifierSkill.getArmorModifiers().forEach(injuryContext::addArmorModifier);
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			avOrInjModifierSkill = null;
		}

		if (injuryContext.isArmorBroken()) {
			injuryContext.setInjuryRoll(diceRoller.rollInjury());
			InjuryModifierFactory factory = game.getFactory(FactoryType.Factory.INJURY_MODIFIER);
			factory.findInjuryModifiers(game, injuryContext, pAttacker,
				pDefender, isStab(), isFoul(), isVomitLike()).forEach(injuryModifier -> injuryContext.addInjuryModifier(injuryModifier));
			if (avOrInjModifierSkill != null) {
				avOrInjModifierSkill.getInjuryModifiers().forEach(injuryContext::addInjuryModifier);
			}
			setInjury(pDefender, gameState, diceRoller);

		} else {
			injuryContext.setInjury(new PlayerState(PlayerState.PRONE));
		}

	}
}
