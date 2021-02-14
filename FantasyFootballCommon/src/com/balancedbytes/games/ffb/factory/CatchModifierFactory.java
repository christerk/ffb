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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.CATCH_MODIFIER)
@RulesCollection(Rules.COMMON)
public class CatchModifierFactory extends GenerifiedModifierFactory<CatchModifierKey, CatchContext,
	CatchModifierFactory.CatchModifierCalculationInput, CatchModifier, CatchModifierRegistry> {

	private CatchModifierRegistry catchModifiers;

	private final CatchModifier dummy = new CatchModifier(CatchModifierKey.DUMMY, 0, false, false);

	public CatchModifier forName(String pName) {
		return forKey(CatchModifierKey.from(pName));
	}

	@Override
	protected Collection<CatchModifierKey> getModifierKeys(Skill skill) {
		return skill.getCatchModifiers();
	}

	@Override
	protected Set<CatchModifier> gameModifiers(Game game) {
		return game.activeModifiers(CatchModifier.class);
	}

	@Override
	protected Set<CatchModifier> findModifiersInternal(CatchModifierCalculationInput input) {

		Set<CatchModifier> catchModifiers = new HashSet<>();
		Player<?> player = input.getPlayer();
		Game game = input.getGame();

		switch (input.getCatchScatterThrowInMode()) {
			case CATCH_ACCURATE_PASS:
			case CATCH_ACCURATE_BOMB:
				catchModifiers.add(forKey(CatchModifierKey.ACCURATE));
				break;
			case CATCH_ACCURATE_BOMB_EMPTY_SQUARE:
			case CATCH_ACCURATE_PASS_EMPTY_SQUARE:
				if (player.hasSkillWithProperty(NamedProperties.addBonusForAccuratePass)) {
					catchModifiers.add(forKey(CatchModifierKey.ACCURATE));
				}
				break;
			case CATCH_HAND_OFF:
				catchModifiers.add(forKey(CatchModifierKey.HAND_OFF));
				break;
			case DEFLECTED:
			case DEFLECTED_BOMB:
				catchModifiers.add(forKey(CatchModifierKey.DEFLECTED));
				break;
			case CATCH_BOMB:
			case CATCH_SCATTER:
				catchModifiers.add(forKey(CatchModifierKey.INACCURATE));
				break;
		}


		catchModifiers.addAll(activeModifiers(game, CatchModifier.class));
		if (!player.hasSkillWithProperty(NamedProperties.ignoreTacklezonesWhenCatching)) {
			getTacklezoneModifier(game, player).ifPresent(catchModifiers::add);
		}

		getDisturbingPresenceModifier(game, player).ifPresent(catchModifiers::add);

		return catchModifiers;
	}

	@Override
	protected Scanner<CatchModifierRegistry> getScanner() {
		return new Scanner<>(CatchModifierRegistry.class);
	}

	@Override
	protected CatchModifierRegistry getRegistry() {
		return catchModifiers;
	}

	@Override
	protected void setRegistry(CatchModifierRegistry registry) {
		catchModifiers = registry;
	}

	@Override
	protected CatchModifier getDummy() {
		return dummy;
	}

	public static class CatchModifierCalculationInput extends GenerifiedModifierFactory.ModifierCalculationInput<CatchContext> {
		private final CatchScatterThrowInMode catchScatterThrowInMode;

		public CatchModifierCalculationInput(Game game, Player<?> player, CatchScatterThrowInMode catchScatterThrowInMode) {
			super(game, player);
			this.catchScatterThrowInMode = catchScatterThrowInMode;
		}

		public CatchScatterThrowInMode getCatchScatterThrowInMode() {
			return catchScatterThrowInMode;
		}

		@Override
		public CatchContext getContext() {
			return new CatchContext(getPlayer(), catchScatterThrowInMode);
		}
	}
}
