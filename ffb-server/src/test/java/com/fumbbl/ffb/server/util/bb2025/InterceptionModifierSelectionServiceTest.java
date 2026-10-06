package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.factory.InterceptionModifierFactory;
import com.fumbbl.ffb.factory.MechanicsFactory;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.mechanics.bb2025.AgilityMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameRules;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.modifiers.InterceptionContext;
import com.fumbbl.ffb.modifiers.InterceptionModifier;
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

class InterceptionModifierSelectionServiceTest {
	private final InterceptionModifierSelectionService service = new InterceptionModifierSelectionService();
	private final ConsummateProfessional skill = new ConsummateProfessional();
	private final Set<InterceptionModifier> automaticModifiers = new HashSet<>();
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
		// the interceptor is never the acting player, the thrower is
		when(actingPlayer.getPlayer()).thenReturn(mock(Player.class));
		InterceptionModifierFactory factory = mock(InterceptionModifierFactory.class);
		when(factory.findModifiers(any(InterceptionContext.class))).thenAnswer(invocation -> {
			InterceptionContext context = invocation.getArgument(0);
			Set<InterceptionModifier> modifiers = new HashSet<>(automaticModifiers);
			skill.getInterceptionModifiers().stream().filter(modifier -> modifier.appliesToContext(skill, context))
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
		when(game.<InterceptionModifierFactory>getFactory(Factory.INTERCEPTION_MODIFIER)).thenReturn(factory);
	}

	private List<ModifierChoiceOption> options(int roll) {
		return service.findOptions(game, player, PassResult.ACCURATE, false, roll);
	}

	private List<ModifierChoiceOption> combinations() {
		return service.findCombinations(game, player, PassResult.ACCURATE, false);
	}

	@Test
	void professionalRescuesInterceptionWithCorrectPreviewAndBonus() {
		automaticModifiers.add(new InterceptionModifier("Accurate Pass", 3, ModifierType.REGULAR));
		assertTrue(options(4).isEmpty());
		List<ModifierChoiceOption> options = options(5);
		assertEquals(1, options.size());
		assertEquals(Collections.singletonList(skill), options.get(0).getSkills());
		assertEquals(-1, options.get(0).getTotalModifier());
		assertEquals(5, options.get(0).getMinimumRoll());
	}

	@Test
	void naturalOneHasNoActionableOptionButRetainsRerollPreview() {
		assertTrue(options(1).isEmpty());
		assertEquals(2, combinations().get(0).getMinimumRoll());
	}

	@Test
	void tacklezonesCanMakeModifierInsufficient() {
		automaticModifiers.add(new InterceptionModifier("Accurate Pass", 3, ModifierType.REGULAR));
		automaticModifiers.add(new InterceptionModifier("2 Tacklezones", 2, ModifierType.TACKLEZONE));
		assertTrue(options(5).isEmpty());
		assertEquals(7, combinations().get(0).getMinimumRoll());
	}

	@Test
	void usedProfessionalHasNeitherOptionsNorCombinations() {
		when(player.isUsed(skill)).thenReturn(true);
		assertTrue(options(2).isEmpty());
		assertTrue(combinations().isEmpty());
	}

	@Test
	void unlimitedModifiersAreIncludedWithoutBeingAnOptionalChoice() {
		automaticModifiers.add(new InterceptionModifier("Extra Arms", -1, ModifierType.REGULAR));
		automaticModifiers.add(new InterceptionModifier("Accurate Pass", 3, ModifierType.REGULAR));
		List<ModifierChoiceOption> options = options(4);
		assertEquals(1, options.size());
		assertEquals(4, options.get(0).getMinimumRoll());
		assertEquals(Collections.singletonList(skill), options.get(0).getSkills());
	}
}
