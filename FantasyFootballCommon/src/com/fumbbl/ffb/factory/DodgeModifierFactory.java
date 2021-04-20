package com.fumbbl.ffb.factory;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.DodgeContext;
import com.fumbbl.ffb.modifiers.DodgeModifier;
import com.fumbbl.ffb.modifiers.DodgeModifierCollection;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.modifiers.RollModifier;
import com.fumbbl.ffb.util.Scanner;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

/**
 *
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.DODGE_MODIFIER)
@RulesCollection(Rules.COMMON)
public class DodgeModifierFactory extends GenerifiedModifierFactory<DodgeContext, DodgeModifier, DodgeModifierCollection> {

	private DodgeModifierCollection dodgeModifierCollection;

	public DodgeModifier forName(String name) {
		return Stream.concat(
			dodgeModifierCollection.getModifiers().stream(),
			modifierAggregator.getDodgeModifiers().stream())
			.filter(modifier -> modifier.getName().equals(name))
			.findFirst()
			.orElse(null);
	}

	public Set<DodgeModifier> forType(ModifierType type) {
		return Stream.concat(
			modifierAggregator.getDodgeModifiers().stream().filter(dodgeModifier -> dodgeModifier.getType() == type),
			dodgeModifierCollection.getModifiers(type).stream())
			.collect(Collectors.toSet());
	}

	@Override
	public Set<DodgeModifier> findModifiers(DodgeContext context) {
		Set<DodgeModifier> dodgeModifiers = super.findModifiers(context);

		prehensileTailModifier(findNumberOfPrehensileTails(context.getGame(), context.getSourceCoordinate()))
			.ifPresent(dodgeModifiers::add);

		return dodgeModifiers;
	}

	private int findNumberOfPrehensileTails(Game pGame, FieldCoordinate pCoordinateFrom) {
		ActingPlayer actingPlayer = pGame.getActingPlayer();
		Team otherTeam = UtilPlayer.findOtherTeam(pGame, actingPlayer.getPlayer());
		int nrOfPrehensileTails = 0;
		Player<?>[] opponents = UtilPlayer.findAdjacentPlayersWithTacklezones(pGame, otherTeam, pCoordinateFrom, true);
		for (Player<?> opponent : opponents) {
			if (UtilCards.hasSkillWithProperty(opponent, NamedProperties.makesDodgingHarder)) {
				nrOfPrehensileTails++;
			}
		}
		return nrOfPrehensileTails;
	}

	private Optional<DodgeModifier> prehensileTailModifier(int number) {
		return dodgeModifierCollection.getModifiers(ModifierType.PREHENSILE_TAIL).stream()
			.filter(modifier -> modifier.getMultiplier() == number)
			.findFirst();
	}

	@Override
	protected Scanner<DodgeModifierCollection> getScanner() {
		return new Scanner<>(DodgeModifierCollection.class);
	}

	@Override
	protected DodgeModifierCollection getModifierCollection() {
		return dodgeModifierCollection;
	}

	@Override
	protected void setModifierCollection(DodgeModifierCollection modifierCollection) {
		dodgeModifierCollection = modifierCollection;
	}

	@Override
	protected Collection<DodgeModifier> getModifier(Skill skill) {
		return skill.getDodgeModifiers();
	}

	@Override
	protected Optional<DodgeModifier> checkClass(RollModifier<?> modifier) {
		return modifier instanceof DodgeModifier ? Optional.of((DodgeModifier) modifier) : Optional.empty();
	}

	@Override
	protected boolean isAffectedByDisturbingPresence(DodgeContext context) {
		return false;
	}

	@Override
	protected boolean isAffectedByTackleZones(DodgeContext context) {
		return !UtilCards.hasUncanceledSkillWithProperty(context.getPlayer(), NamedProperties.ignoreTacklezonesWhenDodging);
	}

	@Override
	protected int numberOfTacklezones(DodgeContext context) {
		Team team = UtilPlayer.findOtherTeam(context.getGame(), context.getActingPlayer().getPlayer());
		return UtilPlayer.findAdjacentPlayersWithTacklezones(context.getGame(), team, context.getTargetCoordinate(), false).length;
	}
}
