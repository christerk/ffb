package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.modifiers.CatchContext;
import com.balancedbytes.games.ffb.modifiers.CatchModifier;
import com.balancedbytes.games.ffb.modifiers.CatchModifierKey;
import com.balancedbytes.games.ffb.modifiers.CatchModifierRegistry;
import com.balancedbytes.games.ffb.util.Scanner;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilDisturbingPresence;
import com.balancedbytes.games.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.CATCH_MODIFIER)
@RulesCollection(Rules.COMMON)
public class CatchModifierFactory implements IRollModifierFactory<CatchModifier> {

	private CatchModifierRegistry catchModifiers;

	private final CatchModifier dummy = new CatchModifier(CatchModifierKey.DUMMY, 0, false, false);

	public CatchModifier forName(String pName) {
		return forKey(CatchModifierKey.from(pName));
	}

	public CatchModifier forKey(CatchModifierKey key) {
		return catchModifiers.get(key).orElse(dummy);
	}

	public Set<CatchModifier> findCatchModifiers(Game pGame, Player<?> pPlayer, CatchScatterThrowInMode pCatchMode) {

		Set<CatchModifier> catchModifiers = new HashSet<>(pGame.activeModifiers(CatchModifier.class));

		CatchContext context = new CatchContext(pPlayer, pCatchMode);
		catchModifiers.addAll(getCatchModifiers(pPlayer, context));

		if ((CatchScatterThrowInMode.CATCH_ACCURATE_PASS == pCatchMode)
			|| (CatchScatterThrowInMode.CATCH_ACCURATE_BOMB == pCatchMode)) {
			catchModifiers.add(forKey(CatchModifierKey.ACCURATE));
		}

		if ((CatchScatterThrowInMode.CATCH_ACCURATE_PASS_EMPTY_SQUARE == pCatchMode
			|| CatchScatterThrowInMode.CATCH_ACCURATE_BOMB_EMPTY_SQUARE == pCatchMode)
			&& pPlayer.hasSkillWithProperty(NamedProperties.addBonusForAccuratePass)) {
			catchModifiers.add(forKey(CatchModifierKey.ACCURATE));
		}

		if (CatchScatterThrowInMode.CATCH_HAND_OFF == pCatchMode) {
			catchModifiers.add(forKey(CatchModifierKey.HAND_OFF));
		}
		catchModifiers.addAll(activeModifiers(pGame, CatchModifier.class));
		if (!pPlayer.hasSkillWithProperty(NamedProperties.ignoreTacklezonesWhenCatching)) {
			getTacklezoneModifier(pGame, pPlayer).ifPresent(catchModifiers::add);
		}

		getDisturbingPresenceModifier(pGame, pPlayer).ifPresent(catchModifiers::add);

		return catchModifiers;
	}

	public CatchModifier[] toArray(Set<CatchModifier> pCatchModifierSet) {
		if (pCatchModifierSet != null) {
			CatchModifier[] catchModifierArray = pCatchModifierSet.toArray(new CatchModifier[0]);
			Arrays.sort(catchModifierArray, Comparator.comparing(CatchModifier::getName));
			return catchModifierArray;
		} else {
			return new CatchModifier[0];
		}
	}

	private Optional<CatchModifier> getTacklezoneModifier(Game pGame, Player<?> pPlayer) {
		int tacklezones = UtilPlayer.findTacklezones(pGame, pPlayer);
		return catchModifiers.values().stream()
			.filter(modifier -> modifier.isTacklezoneModifier() && (modifier.getModifier() == tacklezones))
			.findFirst();
	}

	private Optional<CatchModifier> getDisturbingPresenceModifier(Game pGame, Player<?> pPlayer) {
		int disturbingPresences = UtilDisturbingPresence.findOpposingDisturbingPresences(pGame, pPlayer);
		return catchModifiers.values().stream()
			.filter(modifier ->modifier.isDisturbingPresenceModifier() && (modifier.getModifier() == disturbingPresences))
			.findFirst();
	}

	@Override
	public void initialize(Game game) {
		new Scanner<>(CatchModifierRegistry.class)
			.getSubclasses(game.getOptions()).stream().findFirst()
			.ifPresent(registry -> catchModifiers = registry);
	}

	private Collection<CatchModifier> getCatchModifiers(Player<?> player, CatchContext context) {
		Set<CatchModifier> result = new HashSet<>();
		for (Skill skill : player.getSkills()) {
			for (CatchModifierKey modifierKey : skill.getCatchModifiers()) {
				CatchModifier modifier = forKey(modifierKey);
				if (modifier.appliesToContext(context)) {
					result.add(modifier);
				}
			}
		}
		return result;
	}
}
