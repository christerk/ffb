package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.IRollModifier;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.modifiers.ModifierContext;
import com.balancedbytes.games.ffb.modifiers.ModifierCollection;
import com.balancedbytes.games.ffb.util.Scanner;
import com.balancedbytes.games.ffb.util.UtilDisturbingPresence;
import com.balancedbytes.games.ffb.util.UtilPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class GenerifiedModifierFactory<
	C extends ModifierContext,
	I extends GenerifiedModifierFactory.ModifierCalculationInput<C>,
	V extends IRollModifier<C>,
	R extends ModifierCollection<C, V>
	> implements IRollModifierFactory<V> {

	@Override
	public void initialize(Game game) {
		getScanner()
			.getSubclasses(game.getOptions()).stream().findFirst()
			.ifPresent(this::setModifierCollection);
	}

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

	protected Collection<V> getModifierKeys(Player<?> player, C context) {
		Set<V> result = new HashSet<>();

		for (Skill skill : player.getSkills()) {
			for (V modifier : getModifier(skill)) {
				if (modifier.appliesToContext(skill, context)) {
					result.add(modifier);
				}
			}
		}
		return result;
	}

	protected abstract Collection<V> getModifier(Skill skill);

	protected abstract Set<V> gameModifiers(Game game);

	protected abstract Set<V> findModifiersInternal(I input);

	public Set<V> findModifiers(I input) {
		Set<V> modifiers = gameModifiers(input.getGame());

		modifiers.addAll(getModifierKeys(input.getPlayer(), input.getContext()));

		modifiers.addAll(findModifiersInternal(input));

		return modifiers;
	}

	public abstract static class ModifierCalculationInput<C> {
		private final Game game;
		private final Player<?> player;

		public ModifierCalculationInput(Game game, Player<?> player) {
			this.game = game;
			this.player = player;
		}

		public Game getGame() {
			return game;
		}

		public Player<?> getPlayer() {
			return player;
		}

		public abstract C getContext();
	}
}
