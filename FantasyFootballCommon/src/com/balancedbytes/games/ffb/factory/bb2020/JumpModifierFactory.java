package com.balancedbytes.games.ffb.factory.bb2020;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.modifiers.JumpContext;
import com.balancedbytes.games.ffb.modifiers.JumpModifier;
import com.balancedbytes.games.ffb.modifiers.JumpModifierCollection;
import com.balancedbytes.games.ffb.modifiers.ModifierType;
import com.balancedbytes.games.ffb.modifiers.RollModifier;
import com.balancedbytes.games.ffb.util.Scanner;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.JUMP_MODIFIER)
@RulesCollection(Rules.BB2020)
public class JumpModifierFactory extends com.balancedbytes.games.ffb.factory.JumpModifierFactory {

	private JumpModifierCollection jumpModifierCollection;

	public JumpModifier forName(String name) {
		return Stream.concat(
				jumpModifierCollection.getModifiers().stream(),
				modifierAggregator.getJumpModifiers().stream())
				.filter(modifier -> modifier.getName().equals(name))
				.findFirst()
				.orElse(null);	}


	@Override
	protected Scanner<JumpModifierCollection> getScanner() {
		return new Scanner<>(JumpModifierCollection.class);
	}

	@Override
	protected JumpModifierCollection getModifierCollection() {
		return jumpModifierCollection;
	}

	@Override
	protected void setModifierCollection(JumpModifierCollection modifierCollection) {
		this.jumpModifierCollection = modifierCollection;
	}

	@Override
	protected Collection<JumpModifier> getModifier(Skill skill) {
		return skill.getJumpModifiers();
	}

	@Override
	protected Optional<JumpModifier> checkClass(RollModifier<?> modifier) {
		return modifier instanceof JumpModifier ? Optional.of((JumpModifier) modifier) : Optional.empty();
	}

	@Override
	protected boolean isAffectedByDisturbingPresence(JumpContext context) {
		return false;
	}

	@Override
	protected boolean isAffectedByTackleZones(JumpContext context) {
		return false;
	}

	@Override
	public Set<JumpModifier> findModifiers(JumpContext context) {
		Set<JumpModifier> modifiers = new HashSet<>();
		Optional<JumpModifier> tacklezoneModifier = getTacklezoneModifier(context);
		tacklezoneModifier.ifPresent(modifiers::add);

		prehensileTailModifier(findNumberOfPrehensileTails(context.getGame(), context.getFrom()))
			.ifPresent(modifiers::add);

		modifiers.addAll(super.findModifiers(context));

		int sum = modifiers.stream().mapToInt(JumpModifier::getModifier).sum();
		context.setAccumulatedModifiers(sum);
		for (Skill skill: context.getPlayer().getSkills()) {
			skill.getJumpModifiers().stream()
				.filter(modifier -> modifier.getType() == ModifierType.DEPENDS_ON_SUM_OF_OTHERS
					&& modifier.appliesToContext(skill, context))
				.forEach(modifiers::add);
		}
		return modifiers;
	}

	private int findNumberOfPrehensileTails(Game pGame, FieldCoordinate pCoordinateFrom) {
		ActingPlayer actingPlayer = pGame.getActingPlayer();
		Team otherTeam = UtilPlayer.findOtherTeam(pGame, actingPlayer.getPlayer());
		int nrOfPrehensileTails = 0;
		Player<?>[] opponents = UtilPlayer.findAdjacentPlayersWithTacklezones(pGame, otherTeam, pCoordinateFrom, true);
		for (Player<?> opponent : opponents) {
			if (UtilCards.hasSkillWithProperty(opponent, NamedProperties.makesJumpingHarder)) {
				nrOfPrehensileTails++;
			}
		}
		return nrOfPrehensileTails;
	}

	private Optional<JumpModifier> prehensileTailModifier(int number) {
		return jumpModifierCollection.getModifiers(ModifierType.PREHENSILE_TAIL).stream()
			.filter(modifier -> modifier.getMultiplier() == number)
			.findFirst();
	}

	@Override
	protected int numberOfTacklezones(JumpContext context) {
		Team otherTeam = UtilPlayer.findOtherTeam(context.getGame(), context.getPlayer());

		int fromZones = UtilPlayer.findAdjacentPlayersWithTacklezones(context.getGame(), otherTeam, context.getFrom(), false).length;
		int toZones = UtilPlayer.findAdjacentPlayersWithTacklezones(context.getGame(), otherTeam, context.getTo(), false).length;

		return Math.max(fromZones, toZones);
	}
}
