package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.factory.SkillFactory;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.util.UtilCards;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModifierAggregator {
	private final SkillFactory skillFactory;
	private final Game game;

	public ModifierAggregator(Game game) {
		this.game = game;
		this.skillFactory = game.getFactory(FactoryType.Factory.SKILL);
	}

	public Set<CatchModifier> getCatchModifiers() {
		return Stream.concat(
			skillFactory.getSkills().stream().flatMap(skill -> skill.getCatchModifiers().stream()),
			Arrays.stream(UtilCards.findAllActiveCards(game))
				.flatMap(card -> card.modifiers().stream())
				.filter(modifier -> modifier instanceof CatchModifier)
				.map(modifier -> (CatchModifier)modifier))
			.collect(Collectors.toSet());
	}

	public Set<InterceptionModifier> getInterceptionModifiers() {
		return Stream.concat(
			skillFactory.getSkills().stream().flatMap(skill -> skill.getInterceptionModifiers().stream()),
			Arrays.stream(UtilCards.findAllActiveCards(game))
				.flatMap(card -> card.modifiers().stream())
				.filter(modifier -> modifier instanceof InterceptionModifier)
				.map(modifier -> (InterceptionModifier)modifier))
			.collect(Collectors.toSet());
	}

	public Set<PassModifier> getPassModifiers() {
		return Stream.concat(
			skillFactory.getSkills().stream().flatMap(skill -> skill.getPassModifiers().stream()),
			Arrays.stream(UtilCards.findAllActiveCards(game))
				.flatMap(card -> card.modifiers().stream())
				.filter(modifier -> modifier instanceof PassModifier)
				.map(modifier -> (PassModifier)modifier))
			.collect(Collectors.toSet());
	}

	public Set<DodgeModifier> getDodgeModifiers() {
		return Stream.concat(
			skillFactory.getSkills().stream().flatMap(skill -> skill.getDodgeModifiers().stream()),
			Arrays.stream(UtilCards.findAllActiveCards(game))
				.flatMap(card -> card.modifiers().stream())
				.filter(modifier -> modifier instanceof DodgeModifier)
				.map(modifier -> (DodgeModifier)modifier))
			.collect(Collectors.toSet());
	}

	public Set<PickupModifier> getPickupModifiers() {
		return Stream.concat(
			skillFactory.getSkills().stream().flatMap(skill -> skill.getPickupModifiers().stream()),
			Arrays.stream(UtilCards.findAllActiveCards(game))
				.flatMap(card -> card.modifiers().stream())
				.filter(modifier -> modifier instanceof PickupModifier)
				.map(modifier -> (PickupModifier)modifier))
			.collect(Collectors.toSet());
	}

	public Set<LeapModifier> getLeapModifiers() {
		return Stream.concat(
				skillFactory.getSkills().stream().flatMap(skill -> skill.getLeapModifiers().stream()),
				Arrays.stream(UtilCards.findAllActiveCards(game))
						.flatMap(card -> card.modifiers().stream())
						.filter(modifier -> modifier instanceof LeapModifier)
						.map(modifier -> (LeapModifier)modifier))
				.collect(Collectors.toSet());
	}

	public Set<GazeModifier> getGazeModifiers() {
		return Stream.concat(
			skillFactory.getSkills().stream().flatMap(skill -> skill.getGazeModifiers().stream()),
			Arrays.stream(UtilCards.findAllActiveCards(game))
				.flatMap(card -> card.modifiers().stream())
				.filter(modifier -> modifier instanceof GazeModifier)
				.map(modifier -> (GazeModifier)modifier))
			.collect(Collectors.toSet());
	}

	public Set<GoForItModifier> getGoForItModifiers() {
		return Stream.concat(
			skillFactory.getSkills().stream().flatMap(skill -> skill.getGoForItModifiers().stream()),
			Arrays.stream(UtilCards.findAllActiveCards(game))
				.flatMap(card -> card.modifiers().stream())
				.filter(modifier -> modifier instanceof GoForItModifier)
				.map(modifier -> (GoForItModifier)modifier))
			.collect(Collectors.toSet());
	}

	public Set<RightStuffModifier> getRightStuffModifiers() {
		return Stream.concat(
			skillFactory.getSkills().stream().flatMap(skill -> skill.getRightStuffModifiers().stream()),
			Arrays.stream(UtilCards.findAllActiveCards(game))
				.flatMap(card -> card.modifiers().stream())
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
}
