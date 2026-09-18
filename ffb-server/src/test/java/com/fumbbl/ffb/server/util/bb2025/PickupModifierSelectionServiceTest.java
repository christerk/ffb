package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.factory.MechanicsFactory;
import com.fumbbl.ffb.factory.PickupModifierFactory;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.bb2025.AgilityMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameRules;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.modifiers.PickupContext;
import com.fumbbl.ffb.modifiers.PickupModifier;
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

class PickupModifierSelectionServiceTest {
	private final PickupModifierSelectionService service = new PickupModifierSelectionService();
	private final ConsummateProfessional skill = new ConsummateProfessional();
	private final Set<PickupModifier> automaticModifiers = new HashSet<>();
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
		PickupModifierFactory factory = mock(PickupModifierFactory.class);
		when(factory.findModifiers(any(PickupContext.class))).thenAnswer(invocation -> {
			PickupContext context = invocation.getArgument(0);
			Set<PickupModifier> modifiers = new HashSet<>(automaticModifiers);
			skill.getPickupModifiers().stream().filter(modifier -> modifier.appliesToContext(skill, context))
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
		when(game.<PickupModifierFactory>getFactory(Factory.PICKUP_MODIFIER)).thenReturn(factory);
	}

	@Test
	void professionalRescuesPickupWithCorrectPreviewAndBonus() {
		List<ModifierChoiceOption> options = service.findOptions(game, player, 2);
		assertEquals(1, options.size());
		assertEquals(Collections.singletonList(skill), options.get(0).getSkills());
		assertEquals(-1, options.get(0).getTotalModifier());
		assertEquals(2, options.get(0).getMinimumRoll());
	}

	@Test
	void naturalOneHasNoActionableOptionButRetainsRerollPreview() {
		assertTrue(service.findOptions(game, player, 1).isEmpty());
		assertEquals(2, service.findCombinations(game, player).get(0).getMinimumRoll());
	}

	@Test
	void rainAndTacklezonesCanMakeModifierInsufficient() {
		automaticModifiers.add(new PickupModifier("Rain", 1, ModifierType.REGULAR));
		automaticModifiers.add(new PickupModifier("Tacklezone", 1, ModifierType.TACKLEZONE));
		assertTrue(service.findOptions(game, player, 3).isEmpty());
		assertEquals(4, service.findCombinations(game, player).get(0).getMinimumRoll());
		assertEquals(1, service.findOptions(game, player, 4).size());
	}

	@Test
	void usedProfessionalHasNeitherOptionsNorCombinations() {
		when(actingPlayer.isSkillUsed(skill)).thenReturn(true);
		assertTrue(service.findOptions(game, player, 2).isEmpty());
		assertTrue(service.findCombinations(game, player).isEmpty());
	}

	@Test
	void extraArmsIsIncludedWithoutBeingAnOptionalChoice() {
		automaticModifiers.add(new PickupModifier("Extra Arms", -1, ModifierType.REGULAR));
		automaticModifiers.add(new PickupModifier("Rain", 1, ModifierType.REGULAR));
		assertEquals(2, service.findOptions(game, player, 2).get(0).getMinimumRoll());
		assertEquals(Collections.singletonList(skill), service.findOptions(game, player, 2).get(0).getSkills());
	}
}
