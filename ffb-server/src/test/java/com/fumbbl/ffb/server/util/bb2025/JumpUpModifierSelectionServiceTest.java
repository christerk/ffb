package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.factory.JumpUpModifierFactory;
import com.fumbbl.ffb.factory.MechanicsFactory;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.bb2025.AgilityMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameRules;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.modifiers.JumpUpContext;
import com.fumbbl.ffb.modifiers.JumpUpModifier;
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

class JumpUpModifierSelectionServiceTest {
	private final JumpUpModifierSelectionService service = new JumpUpModifierSelectionService();
	private final ConsummateProfessional skill = new ConsummateProfessional();
	private Game game;
	private Player<?> player;
	private ActingPlayer actingPlayer;

	@BeforeEach
	void setUp() {
		skill.postConstruct();
		player = mock(Player.class);
		when(player.getSkillsIncludingTemporaryOnes()).thenReturn(Collections.singleton(skill));
		when(player.getAgilityWithModifiers()).thenReturn(4);
		actingPlayer = mock(ActingPlayer.class);
		when(actingPlayer.getPlayer()).thenAnswer(invocation -> player);
		JumpUpModifierFactory factory = mock(JumpUpModifierFactory.class);
		when(factory.findModifiers(any(JumpUpContext.class))).thenAnswer(invocation -> {
			JumpUpContext context = invocation.getArgument(0);
			Set<JumpUpModifier> modifiers = new HashSet<>();
			modifiers.add(new JumpUpModifier("Jump Up", -1, ModifierType.REGULAR));
			skill.getJumpUpModifiers().stream().filter(modifier -> modifier.appliesToContext(skill, context))
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
		when(game.<JumpUpModifierFactory>getFactory(Factory.JUMP_UP_MODIFIER)).thenReturn(factory);
	}

	@Test
	void professionalRescuesJumpUpWithCorrectPreviewAndBonus() {
		List<ModifierChoiceOption> options = service.findOptions(game, 2);
		assertEquals(1, options.size());
		assertEquals(Collections.singletonList(skill), options.get(0).getSkills());
		assertEquals("Consummate Professional", options.get(0).getLabel());
		assertEquals(-1, options.get(0).getTotalModifier());
		assertEquals(2, options.get(0).getMinimumRoll());
	}

	@Test
	void naturalOneCannotBeRescuedEvenWithTwoPlusTarget() {
		assertTrue(service.findOptions(game, 1).isEmpty());
		assertEquals(2, service.findCombinations(game).get(0).getMinimumRoll());
	}

	@Test
	void insufficientModifierHasNoActionableOption() {
		when(player.getAgilityWithModifiers()).thenReturn(5);
		assertTrue(service.findOptions(game, 2).isEmpty());
		assertEquals(3, service.findCombinations(game).get(0).getMinimumRoll());
		assertEquals(1, service.findOptions(game, 3).size());
	}

	@Test
	void usedProfessionalHasNeitherOptionsNorCombinations() {
		when(actingPlayer.isSkillUsed(skill)).thenReturn(true);
		assertTrue(service.findOptions(game, 2).isEmpty());
		assertTrue(service.findCombinations(game).isEmpty());
	}
}
