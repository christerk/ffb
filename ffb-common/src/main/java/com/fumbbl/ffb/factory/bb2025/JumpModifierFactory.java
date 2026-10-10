package com.fumbbl.ffb.factory.bb2025;

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
import com.fumbbl.ffb.modifiers.JumpContext;
import com.fumbbl.ffb.modifiers.JumpModifier;
import com.fumbbl.ffb.modifiers.JumpModifierCollection;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.modifiers.OptionalRollModifierService;
import com.fumbbl.ffb.modifiers.RollModifier;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.Scanner;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Jump modifiers of the BB2025 ruleset. The skills ignoring jump modifiers (e.g. Pogo) are free and unlimited, so
 * they are applied automatically unless the context declares them optional, which happens when the coach may want to
 * fail the jump on purpose. A context without declared modifiers, e.g. the move square preview, gets the roll the
 * player would end up with after declaring everything that is available to them.
 */
@FactoryType(FactoryType.Factory.JUMP_MODIFIER)
@RulesCollection(Rules.BB2025)
public class JumpModifierFactory extends com.fumbbl.ffb.factory.JumpModifierFactory {

	private JumpModifierCollection jumpModifierCollection;

	public JumpModifier forName(String name) {
		return Stream.concat(
				jumpModifierCollection.getModifiers().stream(),
				modifierAggregator.getJumpModifiers().stream())
			.filter(modifier -> modifier.getName().equals(name))
			.findFirst()
			.orElse(null);
	}

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
		if (!context.areModifiersDeclared()) {
			return findUndeclaredModifiers(context);
		}

		Set<JumpModifier> modifiers = new HashSet<>();
		if (!appliesFreeSkillEffect(context,
			Optional.ofNullable(context.getPlayer().getSkillWithProperty(NamedProperties.ignoreTacklezonesWhenJumping)))) {
			Optional<JumpModifier> tacklezoneModifier = getTacklezoneModifier(context);
			tacklezoneModifier.ifPresent(modifiers::add);
		}

		if (!appliesFreeSkillEffect(context,
			UtilCards.getSkillToCancelProperty(context.getPlayer(), NamedProperties.makesJumpingHarder))) {
			prehensileTailModifier(findNumberOfPrehensileTails(context.getGame(), context.getFrom()))
				.ifPresent(modifiers::add);
		}

		modifiers.addAll(super.findModifiers(context));

		int sum = modifiers.stream().mapToInt(JumpModifier::getModifier).sum();
		context.setAccumulatedModifiers(sum + context.getAccumulatedModifiers());
		context.addModifierCount(modifiers.size());
		for (Skill skill : context.getPlayer().getSkills()) {
			skill.getJumpModifiers().stream()
				.filter(modifier -> modifier.getType() == ModifierType.DEPENDS_ON_SUM_OF_OTHERS
					&& modifier.appliesToContext(skill, context))
				.forEach(modifiers::add);
		}
		return modifiers;
	}

	/**
	 * Nothing has been declared yet, so the modifiers are those of the roll the player would end up with: every
	 * optional modifier they still have available is applied and an opponent who could declare Diving Tackle does so.
	 */
	private Set<JumpModifier> findUndeclaredModifiers(JumpContext context) {
		Game game = context.getGame();
		Player<?> player = context.getPlayer();
		FieldCoordinate from = context.getFrom(), to = context.getTo();
		Set<Skill> availableSkills = new HashSet<>(new OptionalRollModifierService().availableSkills(game, player,
			skills -> new JumpContext(game, player, from, to, skills), Skill::getJumpModifiers));
		JumpContext declaredContext = new JumpContext(game, player, from, to, availableSkills);
		Set<JumpModifier> divingTackleModifiers = divingTackleModifiers(game, player, from, to);
		// Leap depends on the sum of the other modifiers, so Diving Tackle has to be accumulated before they are found
		divingTackleModifiers.forEach(modifier -> declaredContext.addModifierValue(modifier.getModifier()));
		Set<JumpModifier> modifiers = findModifiers(declaredContext);
		modifiers.addAll(divingTackleModifiers);
		return modifiers;
	}

	/**
	 * @return the modifiers an opponent could still add with Diving Tackle, empty when the jumping player is immune
	 */
	private Set<JumpModifier> divingTackleModifiers(Game game, Player<?> player, FieldCoordinate from,
		FieldCoordinate to) {
		if (UtilCards.hasSkillToCancelProperty(player, NamedProperties.canAttemptToTackleJumpingPlayer)) {
			return new HashSet<>();
		}
		Player<?>[] divingTacklers =
			UtilPlayer.findEligibleDivingTacklers(game, from, to, NamedProperties.canAttemptToTackleJumpingPlayer);
		if (!ArrayTool.isProvided(divingTacklers)) {
			return new HashSet<>();
		}
		return divingTacklers[0].getSkillsIncludingTemporaryOnes().stream()
			.filter(skill -> skill.hasSkillProperty(NamedProperties.canAttemptToTackleJumpingPlayer))
			.findFirst().map(skill -> new HashSet<>(skill.getJumpModifiers())).orElseGet(HashSet::new);
	}

	/**
	 * @return whether the effect of a free skill applies, which requires the skill to be selected when the context
	 * declares the free modifiers optional
	 */
	private boolean appliesFreeSkillEffect(JumpContext context, Optional<Skill> skill) {
		return skill.isPresent() && (!context.areFreeModifiersOptional() || context.isSkillSelected(skill.get()));
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

		int fromZones =
			UtilPlayer.findAdjacentPlayersWithTacklezones(context.getGame(), otherTeam, context.getFrom(), false).length;
		int toZones =
			UtilPlayer.findAdjacentPlayersWithTacklezones(context.getGame(), otherTeam, context.getTo(), false).length;

		return Math.max(fromZones, toZones);
	}
}
