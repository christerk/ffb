package com.fumbbl.ffb.factory;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ModifierAggregator;
import com.fumbbl.ffb.modifiers.ModifierCollection;
import com.fumbbl.ffb.modifiers.ModifierContext;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.modifiers.RollModifier;
import com.fumbbl.ffb.util.Scanner;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilDisturbingPresence;
import com.fumbbl.ffb.util.UtilPlayer;

public abstract class GenerifiedModifierFactory<
	C extends ModifierContext,
	V extends RollModifier<C>,
	R extends ModifierCollection<C, V>
	> implements IRollModifierFactory<V> {

	@Override
	public void initialize(Game game) {
		getScanner()
			.getSubclasses(game.getOptions()).stream().findFirst()
			.ifPresent(this::setModifierCollection);
		modifierAggregator = game.getModifierAggregator();
	}

	protected ModifierAggregator modifierAggregator;

	protected abstract Scanner<R> getScanner();

	protected abstract R getModifierCollection();

	protected abstract void setModifierCollection(R modifierCollection);

	private Optional<V> getDisturbingPresenceModifier(Game pGame, Player<?> pPlayer) {
		int disturbingPresences = UtilDisturbingPresence.findOpposingDisturbingPresences(pGame, pPlayer);
		return getModifierCollection().getModifiers(ModifierType.DISTURBING_PRESENCE).stream()
			.filter(modifier -> modifier.getMultiplier() == disturbingPresences)
			.findFirst();
	}

	protected int numberOfTacklezones(C context) {
		return UtilPlayer.findTacklezones(context.getGame(), context.getPlayer());
	}

	protected Optional<V> getTacklezoneModifier(C context) {
		int tacklezones = numberOfTacklezones(context);
		return getModifierCollection().getModifiers(ModifierType.TACKLEZONE).stream()
			.filter(modifier -> modifier.getMultiplier() == tacklezones)
			.findFirst();
	}

	private Set<V> getSkillModifiers(C context) {
		Set<V> result = new HashSet<>();

		for (Skill skill : context.getPlayer().getSkillsIncludingTemporaryOnes()) {
			for (V modifier : getModifier(skill)) {
				if (modifier.getType() != ModifierType.DEPENDS_ON_SUM_OF_OTHERS && modifier.appliesToContext(skill, context)) {
					result.add(modifier);
				}
			}
		}
		return result;
	}

	protected abstract Collection<V> getModifier(Skill skill);

	protected abstract Optional<V> checkClass(RollModifier<?> modifier);

	public Set<V> findModifiers(C context) {
		Set<V> modifiers = getSkillModifiers(context);
		Arrays.stream(UtilCards.findAllActiveCards(context.getGame()))
			.flatMap((Function<Card, Stream<RollModifier<?>>>) card -> card.rollModifiers().stream())
			.map(this::checkClass)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.filter(modifier -> modifier.appliesToContext(null, context))
			.forEach(modifiers::add);

		getModifierCollection().getModifiers(ModifierType.REGULAR).stream()
			.filter(passModifier -> passModifier.appliesToContext(null, context))
			.forEach(modifiers::add);

		if (isAffectedByTackleZones(context)) {
			getTacklezoneModifier(context).ifPresent(modifiers::add);
		}

		if (isAffectedByDisturbingPresence(context)) {
			getDisturbingPresenceModifier(context.getGame(), context.getPlayer()).ifPresent(modifiers::add);
		}

		return modifiers;
	}

	protected abstract boolean isAffectedByDisturbingPresence(C context);

	protected abstract boolean isAffectedByTackleZones(C context);
}
