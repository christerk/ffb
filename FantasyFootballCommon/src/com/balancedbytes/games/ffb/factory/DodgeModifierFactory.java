package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.modifiers.DodgeContext;
import com.balancedbytes.games.ffb.modifiers.DodgeModifier;
import com.balancedbytes.games.ffb.modifiers.DodgeModifierCollection;
import com.balancedbytes.games.ffb.modifiers.ModifierType;
import com.balancedbytes.games.ffb.modifiers.RollModifier;
import com.balancedbytes.games.ffb.util.Scanner;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
