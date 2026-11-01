package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.factory.MechanicsFactory;
import com.fumbbl.ffb.factory.RightStuffModifierFactory;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.mechanics.bb2025.AgilityMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameRules;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.modifiers.RightStuffContext;
import com.fumbbl.ffb.modifiers.RightStuffModifier;
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

class RightStuffModifierSelectionServiceTest {
	private final RightStuffModifierSelectionService service = new RightStuffModifierSelectionService();
	private final ConsummateProfessional skill = new ConsummateProfessional();
	private final Set<RightStuffModifier> automaticModifiers = new HashSet<>();
	private Game game;
	private Player<?> player;

	@BeforeEach
	void setUp() {
		skill.postConstruct();
		player = mock(Player.class);
		when(player.getSkillsIncludingTemporaryOnes()).thenReturn(Collections.singleton(skill));
		when(player.getAgilityWithModifiers()).thenReturn(3);
		ActingPlayer actingPlayer = mock(ActingPlayer.class);
		RightStuffModifierFactory factory = mock(RightStuffModifierFactory.class);
		when(factory.findModifiers(any(RightStuffContext.class))).thenAnswer(invocation -> {
			RightStuffContext context = invocation.getArgument(0);
			Set<RightStuffModifier> modifiers = new HashSet<>(automaticModifiers);
			skill.getRightStuffModifiers().stream().filter(modifier -> modifier.appliesToContext(skill, context))
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
		when(game.<RightStuffModifierFactory>getFactory(Factory.RIGHT_STUFF_MODIFIER)).thenReturn(factory);
	}

	@Test
	void professionalRescuesLandingWithCorrectPreviewAndBonus() {
		List<ModifierChoiceOption> options = service.findOptions(game, player, PassResult.ACCURATE, 2);
		assertEquals(1, options.size());
		assertEquals(Collections.singletonList(skill), options.get(0).getSkills());
		assertEquals(-1, options.get(0).getTotalModifier());
		assertEquals(2, options.get(0).getMinimumRoll());
	}

	@Test
	void naturalOneHasNoActionableOptionButRetainsRerollPreview() {
		assertTrue(service.findOptions(game, player, PassResult.ACCURATE, 1).isEmpty());
		assertEquals(2, service.findCombinations(game, player, PassResult.ACCURATE).get(0).getMinimumRoll());
	}

	@Test
	void subparThrowAndTacklezonesCanMakeModifierInsufficient() {
		automaticModifiers.add(new RightStuffModifier("Subpar Throw", 1, ModifierType.REGULAR));
		automaticModifiers.add(new RightStuffModifier("1 Tacklezone", 1, ModifierType.TACKLEZONE));
		assertTrue(service.findOptions(game, player, PassResult.INACCURATE, 3).isEmpty());
		assertEquals(4, service.findCombinations(game, player, PassResult.INACCURATE).get(0).getMinimumRoll());
		assertEquals(1, service.findOptions(game, player, PassResult.INACCURATE, 4).size());
	}

	@Test
	void usedProfessionalHasNeitherOptionsNorCombinations() {
		when(player.isUsed(skill)).thenReturn(true);
		assertTrue(service.findOptions(game, player, PassResult.ACCURATE, 2).isEmpty());
		assertTrue(service.findCombinations(game, player, PassResult.ACCURATE).isEmpty());
	}
}
