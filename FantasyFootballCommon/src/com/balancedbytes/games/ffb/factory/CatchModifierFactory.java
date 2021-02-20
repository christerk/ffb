package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.IRollModifier;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.modifiers.CatchContext;
import com.balancedbytes.games.ffb.modifiers.CatchModifier;
import com.balancedbytes.games.ffb.modifiers.CatchModifierCollection;
import com.balancedbytes.games.ffb.util.Scanner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.CATCH_MODIFIER)
@RulesCollection(Rules.COMMON)
public class CatchModifierFactory extends GenerifiedModifierFactory<CatchContext, CatchModifier, CatchModifierCollection> {

	private CatchModifierCollection catchModifiers;

	@Override
	protected Collection<CatchModifier> getModifier(Skill skill) {
		return skill.getCatchModifiers();
	}

	@Override
	protected Set<CatchModifier> findModifiersInternal(CatchContext context) {

		Set<CatchModifier> catchModifiers = new HashSet<>();
		Player<?> player = context.getPlayer();
		Game game = context.getGame();

		getModifierCollection().getOtherModifiers().stream()
			.filter(catchModifier -> catchModifier.appliesToContext(context))
					.forEach(catchModifiers::add);

		if (!player.hasSkillWithProperty(NamedProperties.ignoreTacklezonesWhenCatching)) {
			getTacklezoneModifier(game, player).ifPresent(catchModifiers::add);
		}

		getDisturbingPresenceModifier(game, player).ifPresent(catchModifiers::add);

		return catchModifiers;
	}

	@Override
	protected Optional<CatchModifier> checkClass(IRollModifier<?> modifier) {
		return modifier instanceof  CatchModifier ? Optional.of((CatchModifier) modifier) : Optional.empty();
	}

	@Override
	protected Scanner<CatchModifierCollection> getScanner() {
		return new Scanner<>(CatchModifierCollection.class);
	}

	@Override
	protected CatchModifierCollection getModifierCollection() {
		return catchModifiers;
	}

	@Override
	protected void setModifierCollection(CatchModifierCollection modifierCollection) {
		catchModifiers = modifierCollection;
	}

	@Override
	public CatchModifier forName(String name) {
		return Stream.concat(
			catchModifiers.getAllModifiers().stream(),
			modifierAggregator.getCatchModifiers().stream())
			.filter(modifier -> modifier.getName().equals(name))
			.findFirst()
			.orElse(null);
	}
}
