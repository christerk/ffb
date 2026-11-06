package com.fumbbl.ffb.server.injury.injuryType;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
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
import com.fumbbl.ffb.server.injury.modification.bb2025.chain.OptionalSkillModifierService;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class InjuryTypeDropJump extends ModificationAwareInjuryTypeServer<DropJump> {

	private final Player<?> divingTackler;

	public InjuryTypeDropJump() {
		this(null);
	}

	public InjuryTypeDropJump(Player<?> divingTackler) {
		super(new DropJump());
		this.divingTackler = divingTackler;
	}

	@Override
	protected void armourRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker,
		Player<?> pDefender, FieldCoordinate pDefenderCoordinate, FieldCoordinate vacatedFromCoordinate,
		DiceInterpreter diceInterpreter, InjuryContext injuryContext, boolean roll) {

		if (!injuryContext.isArmorBroken()) {
			if (roll) {
				injuryContext.setArmorRoll(diceRoller.rollArmour());
			}
			if (UtilCards.hasUnusedSkillWithProperty(pDefender, NamedProperties.ignoresArmourModifiersFromSkills)) {
				injuryContext.addArmorModifiers(pDefender.getSkillWithProperty(NamedProperties.ignoresArmourModifiersFromSkills).getArmorModifiers());
			} else {
				Optional.ofNullable(pDefender.getSkillWithProperty(NamedProperties.blocksLikeChainsaw))
					.ifPresent(skill -> skill.getArmorModifiers().forEach(injuryContext::addArmorModifier));
			}
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
		}

		Skill avOrInjModifierSkill = new OptionalSkillModifierService()
			.unlessOptional(game, avOrInjModifierSkill(game, pDefender, vacatedFromCoordinate)).orElse(null);

		if (!injuryContext.isArmorBroken() && avOrInjModifierSkill != null) {
			avOrInjModifierSkill.getArmorModifiers().forEach(injuryContext::addArmorModifier);
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
		}
	}

	@Override
	protected void injuryRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker,
		Player<?> pDefender, FieldCoordinate pDefenderCoordinate, FieldCoordinate vacatedFromCoordinate,
		InjuryContext injuryContext) {

		Skill avOrInjModifierSkill = new OptionalSkillModifierService()
			.unlessOptional(game, avOrInjModifierSkill(game, pDefender, vacatedFromCoordinate)).orElse(null);

		if (avOrInjModifierSkill != null
			&& avOrInjModifierSkill.getArmorModifiers().stream().anyMatch(injuryContext::hasArmorModifier)) {
			avOrInjModifierSkill = null;
		}

		injuryContext.setInjuryRoll(diceRoller.rollInjury());
		InjuryModifierFactory factory = game.getFactory(FactoryType.Factory.INJURY_MODIFIER);
		factory.findInjuryModifiers(game, injuryContext, pAttacker,
			pDefender, isStab(), isFoul(), isVomitLike()).forEach(injuryModifier -> injuryContext.addInjuryModifier(injuryModifier));
		if (avOrInjModifierSkill != null) {
			avOrInjModifierSkill.getInjuryModifiers().forEach(injuryContext::addInjuryModifier);
		}
		setInjury(pDefender, gameState, diceRoller, injuryContext);
	}

	@Override
	public List<Player<?>> optionalSkillAttackers(Game game, Player<?> defender, FieldCoordinate fromCoordinate) {
		return armBarPlayer(game, defender, fromCoordinate)
			.filter(player -> new OptionalSkillModifierService().isOptional(game, armBar(player)))
			.map(player -> Collections.<Player<?>>singletonList(player))
			.orElse(Collections.emptyList());
	}

	private Skill avOrInjModifierSkill(Game game, Player<?> pDefender, FieldCoordinate vacatedFromCoordinate) {
		return armBarPlayer(game, pDefender, vacatedFromCoordinate).map(this::armBar).orElse(null);
	}

	private Skill armBar(Player<?> player) {
		Skill armBar = player.getSkillWithProperty(NamedProperties.affectsEitherArmourOrInjuryOnJump);
		return armBar != null ? armBar : player.getSkillWithProperty(NamedProperties.affectsEitherArmourOrInjuryOnDodge);
	}

	private Optional<Player<?>> armBarPlayer(Game game, Player<?> pDefender, FieldCoordinate vacatedFromCoordinate) {
		if (UtilCards.hasUnusedSkillWithProperty(pDefender, NamedProperties.ignoresArmourModifiersFromSkills)) {
			return Optional.empty();
		}

		FieldCoordinate fromCoordiante = vacatedFromCoordinate == null ? game.getFieldModel().getPlayerCoordinate(pDefender) : vacatedFromCoordinate;

		Set<Player<?>> players = Arrays.stream(UtilPlayer.findAdjacentPlayersWithTacklezones(game, game.getOtherTeam(pDefender.getTeam()), fromCoordiante, false))
			.collect(Collectors.toSet());

		if (vacatedFromCoordinate != null) {
			Player<?> shadowingOrDtPlayer = game.getFieldModel().getPlayer(vacatedFromCoordinate);

			if (shadowingOrDtPlayer != null) {
				players.add(shadowingOrDtPlayer);
			}
		}

		Optional<Player<?>> jumpArmBarPlayer = players.stream()
			.filter(player -> player.getSkillWithProperty(NamedProperties.affectsEitherArmourOrInjuryOnJump) != null)
			.findFirst();
		if (jumpArmBarPlayer.isPresent()) {
			return jumpArmBarPlayer;
		}

		if (divingTackler != null && divingTackler.getSkillWithProperty(NamedProperties.affectsEitherArmourOrInjuryOnDodge) != null) {
			return Optional.of(divingTackler);
		}

		return Optional.empty();
	}
}
