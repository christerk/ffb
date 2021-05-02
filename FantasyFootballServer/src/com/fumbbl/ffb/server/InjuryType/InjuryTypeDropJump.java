package com.fumbbl.ffb.server.InjuryType;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.InjuryContext;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.factory.InjuryModifierFactory;
import com.fumbbl.ffb.injury.DropJump;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class InjuryTypeDropJump extends InjuryTypeServer<DropJump> {
	public InjuryTypeDropJump() {
		super(new DropJump());
	}

	@Override
	public InjuryContext handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
	                                  Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, FieldCoordinate fromCoordinate, InjuryContext pOldInjuryContext,
	                                  ApothecaryMode pApothecaryMode) {

		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();

		if (!injuryContext.isArmorBroken()) {
			injuryContext.setArmorRoll(diceRoller.rollArmour());
			Optional.ofNullable(pDefender.getSkillWithProperty(NamedProperties.blocksLikeChainsaw))
				.ifPresent(skill -> skill.getArmorModifiers().forEach(injuryContext::addArmorModifier));
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
		}

		Skill avOrInjModifierSkill = null;

		if (fromCoordinate != null) {
			Set<Player<?>> players = Arrays.stream(UtilPlayer.findAdjacentBlockablePlayers(game, game.getOtherTeam(pDefender.getTeam()), fromCoordinate))
				.collect(Collectors.toSet());

			Player<?> shadowingOrDtPlayer = game.getFieldModel().getPlayer(fromCoordinate);

			if (shadowingOrDtPlayer != null) {
				players.add(shadowingOrDtPlayer);
			}

			avOrInjModifierSkill = players.stream().map(player -> player.getSkillWithProperty(NamedProperties.affectsEitherArmourOrInjuryOnJump))
				.filter(Objects::nonNull).findFirst().orElse(null);
		}

		if (!injuryContext.isArmorBroken() && avOrInjModifierSkill != null) {
			avOrInjModifierSkill.getArmorModifiers().forEach(injuryContext::addArmorModifier);
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			avOrInjModifierSkill = null;
		}

		if (injuryContext.isArmorBroken()) {
			injuryContext.setInjuryRoll(diceRoller.rollInjury());
			injuryContext.addInjuryModifier(((InjuryModifierFactory)game.getFactory(FactoryType.Factory.INJURY_MODIFIER)).getNigglingInjuryModifier(pDefender));
			if (avOrInjModifierSkill != null) {
				avOrInjModifierSkill.getInjuryModifiers().forEach(injuryContext::addInjuryModifier);
			}
			setInjury(pDefender, gameState, diceRoller);

		} else {
			injuryContext.setInjury(new PlayerState(PlayerState.PRONE));
		}

		return injuryContext;
	}
}