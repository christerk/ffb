package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.factory.CatchModifierFactory;
import com.fumbbl.ffb.factory.MechanicsFactory;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.bb2025.AgilityMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameRules;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.modifiers.CatchContext;
import com.fumbbl.ffb.modifiers.CatchModifier;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.skill.bb2025.special.ConsummateProfessional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CatchModifierSelectionServiceTest {
	private final CatchModifierSelectionService service = new CatchModifierSelectionService();
	private final ConsummateProfessional skill = new ConsummateProfessional();
	private final Set<CatchModifier> automaticModifiers = new HashSet<>();
	private Game game;
	private Player<?> player;
	private ActingPlayer actingPlayer;

	@BeforeEach
	void setUp() {
		skill.postConstruct();
		player = mock(Player.class);
		when(player.getSkillsIncludingTemporaryOnes()).thenReturn(Collections.singleton(skill));
		when(player.getAgilityWithModifiers()).thenReturn(3);
		actingPlayer = mock(ActingPlayer.class);
		when(actingPlayer.getPlayer()).thenAnswer(invocation -> player);
		CatchModifierFactory factory = mock(CatchModifierFactory.class);
		when(factory.findModifiers(any(CatchContext.class))).thenAnswer(invocation -> {
			CatchContext context = invocation.getArgument(0);
			Set<CatchModifier> modifiers = new HashSet<>(automaticModifiers);
			skill.getCatchModifiers().stream().filter(modifier -> modifier.appliesToContext(skill, context))
				.forEach(modifiers::add);
			return modifiers;
		});
		MechanicsFactory mechanics = mock(MechanicsFactory.class);
		when(mechanics.forName(Mechanic.Type.AGILITY.name())).thenReturn(new AgilityMechanic());
		GameRules rules = mock(GameRules.class);
		when(rules.<MechanicsFactory>getFactory(Factory.MECHANIC)).thenReturn(mechanics);
		game = mock(Game.class);
		when(game.getRules()).thenReturn(rules);
		when(game.getActingPlayer()).thenReturn(actingPlayer);
		when(game.<CatchModifierFactory>getFactory(Factory.CATCH_MODIFIER)).thenReturn(factory);
	}

	private List<ModifierChoiceOption> options(int roll) {
		return service.findOptions(game, player, CatchScatterThrowInMode.CATCH_SCATTER, null, roll);
	}

	private List<ModifierChoiceOption> combinations() {
		return service.findCombinations(game, player, CatchScatterThrowInMode.CATCH_SCATTER, null);
	}

	@Test
	void professionalRescuesCatchWithCorrectPreviewAndBonus() {
		List<ModifierChoiceOption> choices = options(2);
		assertEquals(1, choices.size());
		assertEquals(Collections.singletonList(skill), choices.get(0).getSkills());
		assertEquals(-1, choices.get(0).getTotalModifier());
		assertEquals(2, choices.get(0).getMinimumRoll());
	}

	@Test
	void naturalOneHasNoActionableOptionButRetainsPreview() {
		assertTrue(options(1).isEmpty());
		assertEquals(2, combinations().get(0).getMinimumRoll());
	}

	@Test
	void disturbingPresenceCanMakeModifierInsufficient() {
		automaticModifiers.add(new CatchModifier("Disturbing Presence", 1, ModifierType.DISTURBING_PRESENCE));
		automaticModifiers.add(new CatchModifier("Tacklezone", 1, ModifierType.TACKLEZONE));
		assertTrue(options(3).isEmpty());
		assertEquals(4, combinations().get(0).getMinimumRoll());
		assertEquals(1, options(4).size());
	}

	@Test
	void usedProfessionalHasNeitherOptionsNorCombinations() {
		when(actingPlayer.isSkillUsed(skill)).thenReturn(true);
		assertTrue(options(2).isEmpty());
		assertTrue(combinations().isEmpty());
	}

	@Test
	void automaticModifiersAreIncludedWithoutBeingAnOptionalChoice() {
		automaticModifiers.add(new CatchModifier("Extra Arms", -1, ModifierType.REGULAR));
		automaticModifiers.add(new CatchModifier("Disturbing Presence", 1, ModifierType.DISTURBING_PRESENCE));
		assertEquals(2, options(2).get(0).getMinimumRoll());
		assertEquals(Collections.singletonList(skill), options(2).get(0).getSkills());
	}
}
