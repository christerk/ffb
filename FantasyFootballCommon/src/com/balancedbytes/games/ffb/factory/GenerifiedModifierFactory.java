package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.IRollModifier;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.modifiers.ModifierAggregator;
import com.balancedbytes.games.ffb.modifiers.ModifierCollection;
import com.balancedbytes.games.ffb.modifiers.ModifierContext;
import com.balancedbytes.games.ffb.util.Scanner;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilDisturbingPresence;
import com.balancedbytes.games.ffb.util.UtilPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class GenerifiedModifierFactory<
	C extends ModifierContext,
	V extends IRollModifier<C>,
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

	protected abstract void setModifierCollection(R registry);

	protected Optional<V> getDisturbingPresenceModifier(Game pGame, Player<?> pPlayer) {
		int disturbingPresences = UtilDisturbingPresence.findOpposingDisturbingPresences(pGame, pPlayer);
		return getModifierCollection().getDisturbingPresenceModifiers().stream()
			.filter(modifier -> modifier.getMultiplier() == disturbingPresences)
			.findFirst();
	}

	protected Optional<V> getTacklezoneModifier(Game pGame, Player<?> pPlayer) {
		int tacklezones = UtilPlayer.findTacklezones(pGame, pPlayer);
		return getModifierCollection().getTacklezoneModifiers().stream()
			.filter(modifier -> modifier.getMultiplier() == tacklezones)
			.findFirst();
	}

	public List<V> sort(Set<V> modifierSet) {
		List<V> modifiers = new ArrayList<>(modifierSet);
		modifiers.sort(Comparator.comparing(V::getName));
		return modifiers;
	}

	protected Collection<V> getModifiers(C context) {
		Set<V> result = new HashSet<>();

		for (Skill skill : context.getPlayer().getSkills()) {
			for (V modifier : getModifier(skill)) {
				if (modifier.appliesToContext(context)) {
					result.add(modifier);
				}
			}
		}
		return result;
	}

	protected abstract Collection<V> getModifier(Skill skill);

	protected abstract Set<V> findModifiersInternal(C context);

	protected abstract Optional<V> checkClass(IRollModifier<?> modifier);

	public Set<V> findModifiers(C context) {
		Set<V> modifiers = findModifiersInternal(context);
		Arrays.stream(UtilCards.findAllActiveCards(context.getGame()))
			.flatMap((Function<Card, Stream<IRollModifier<?>>>) card -> card.modifiers().stream())
			.map(this::checkClass)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.filter(modifier -> modifier.appliesToContext(context))
			.forEach(modifiers::add);
		modifiers.addAll(getModifiers(context));

		return modifiers;
	}
}
