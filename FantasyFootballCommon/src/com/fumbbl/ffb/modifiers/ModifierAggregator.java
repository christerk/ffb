package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.modifiers.bb2020.CasualtyModifier;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModifierAggregator {
	private SkillFactory skillFactory;
	private Game game;

	public void init(Game game) {
		this.game = game;
		this.skillFactory = game.getFactory(FactoryType.Factory.SKILL);

	}

	public Set<CatchModifier> getCatchModifiers() {
		return Stream.concat(
			skillFactory.getSkills().stream().flatMap(skill -> skill.getCatchModifiers().stream()),
			Arrays.stream(UtilCards.findAllActiveCards(game))
				.flatMap(card -> card.rollModifiers().stream())
				.filter(modifier -> modifier instanceof CatchModifier)
				.map(modifier -> (CatchModifier)modifier))
			.collect(Collectors.toSet());
	}

	public Set<InterceptionModifier> getInterceptionModifiers() {
		return Stream.concat(
			skillFactory.getSkills().stream().flatMap(skill -> skill.getInterceptionModifiers().stream()),
			Arrays.stream(UtilCards.findAllActiveCards(game))
				.flatMap(card -> card.rollModifiers().stream())
				.filter(modifier -> modifier instanceof InterceptionModifier)
				.map(modifier -> (InterceptionModifier)modifier))
			.collect(Collectors.toSet());
	}

	public Set<PassModifier> getPassModifiers() {
		return Stream.concat(
			skillFactory.getSkills().stream().flatMap(skill -> skill.getPassModifiers().stream()),
			Arrays.stream(UtilCards.findAllActiveCards(game))
				.flatMap(card -> card.rollModifiers().stream())
				.filter(modifier -> modifier instanceof PassModifier)
				.map(modifier -> (PassModifier)modifier))
			.collect(Collectors.toSet());
	}

	public Set<DodgeModifier> getDodgeModifiers() {
		return Stream.concat(
			skillFactory.getSkills().stream().flatMap(skill -> skill.getDodgeModifiers().stream()),
			Arrays.stream(UtilCards.findAllActiveCards(game))
				.flatMap(card -> card.rollModifiers().stream())
				.filter(modifier -> modifier instanceof DodgeModifier)
				.map(modifier -> (DodgeModifier)modifier))
			.collect(Collectors.toSet());
	}

	public Set<PickupModifier> getPickupModifiers() {
		return Stream.concat(
			skillFactory.getSkills().stream().flatMap(skill -> skill.getPickupModifiers().stream()),
			Arrays.stream(UtilCards.findAllActiveCards(game))
				.flatMap(card -> card.rollModifiers().stream())
				.filter(modifier -> modifier instanceof PickupModifier)
				.map(modifier -> (PickupModifier)modifier))
			.collect(Collectors.toSet());
	}

	public Set<JumpModifier> getJumpModifiers() {
		return Stream.concat(
			skillFactory.getSkills().stream().flatMap(skill -> skill.getJumpModifiers().stream()),
			Arrays.stream(UtilCards.findAllActiveCards(game))
				.flatMap(card -> card.rollModifiers().stream())
				.filter(modifier -> modifier instanceof JumpModifier)
				.map(modifier -> (JumpModifier) modifier))
			.collect(Collectors.toSet());
	}

	public Set<JumpUpModifier> getJumpUpModifiers() {
		return Stream.concat(
			skillFactory.getSkills().stream().flatMap(skill -> skill.getJumpUpModifiers().stream()),
			Arrays.stream(UtilCards.findAllActiveCards(game))
				.flatMap(card -> card.rollModifiers().stream())
				.filter(modifier -> modifier instanceof JumpUpModifier)
				.map(modifier -> (JumpUpModifier) modifier))
			.collect(Collectors.toSet());
	}

	public Set<GazeModifier> getGazeModifiers() {
		return Stream.concat(
			skillFactory.getSkills().stream().flatMap(skill -> skill.getGazeModifiers().stream()),
			Arrays.stream(UtilCards.findAllActiveCards(game))
				.flatMap(card -> card.rollModifiers().stream())
				.filter(modifier -> modifier instanceof GazeModifier)
				.map(modifier -> (GazeModifier) modifier))
			.collect(Collectors.toSet());
	}

	public Set<GoForItModifier> getGoForItModifiers() {
		return Stream.concat(
			skillFactory.getSkills().stream().flatMap(skill -> skill.getGoForItModifiers().stream()),
			Arrays.stream(UtilCards.findAllActiveCards(game))
				.flatMap(card -> card.rollModifiers().stream())
				.filter(modifier -> modifier instanceof GoForItModifier)
				.map(modifier -> (GoForItModifier)modifier))
			.collect(Collectors.toSet());
	}

	public Set<RightStuffModifier> getRightStuffModifiers() {
		return Stream.concat(
			skillFactory.getSkills().stream().flatMap(skill -> skill.getRightStuffModifiers().stream()),
			Arrays.stream(UtilCards.findAllActiveCards(game))
				.flatMap(card -> card.rollModifiers().stream())
				.filter(modifier -> modifier instanceof RightStuffModifier)
				.map(modifier -> (RightStuffModifier)modifier))
			.collect(Collectors.toSet());
	}

	public Set<ArmorModifier> getArmourModifiers() {
		return Stream.concat(
			skillFactory.getSkills().stream().flatMap(skill -> skill.getArmorModifiers().stream()),
			Arrays.stream(UtilCards.findAllActiveCards(game))
				.flatMap(card -> card.armourModifiers().stream()))
			.collect(Collectors.toSet());
	}

	public Set<InjuryModifier> getInjuryModifiers() {
		return Stream.concat(
			skillFactory.getSkills().stream().flatMap(skill -> skill.getInjuryModifiers().stream()),
			Arrays.stream(UtilCards.findAllActiveCards(game))
				.flatMap(card -> card.injuryModifiers().stream()))
			.collect(Collectors.toSet());
	}

	public Set<CasualtyModifier> getCasualtyModifiers() {
		return Stream.concat(
			skillFactory.getSkills().stream().flatMap(skill -> skill.getCasualtyModifiers().stream()),
			Arrays.stream(UtilCards.findAllActiveCards(game))
				.flatMap(card -> card.casualtyModifiers().stream()))
			.collect(Collectors.toSet());
	}
}
