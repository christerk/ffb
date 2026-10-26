package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.skill.bb2025.special.ConsummateProfessional;
import com.fumbbl.ffb.skill.common.ExtraArms;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PickupModifierSelectionTest {
	private final OptionalRollModifierService service = new OptionalRollModifierService();
	private final ConsummateProfessional skill = new ConsummateProfessional();
	private Game game;
	private Player<?> player;
	private ActingPlayer actingPlayer;

	@BeforeEach
	void setUp() {
		skill.postConstruct();
		game = mock(Game.class);
		player = mock(Player.class);
		actingPlayer = mock(ActingPlayer.class);
		when(game.getActingPlayer()).thenReturn(actingPlayer);
		when(actingPlayer.getPlayer()).thenAnswer(invocation -> player);
		when(player.getSkillsIncludingTemporaryOnes()).thenReturn(Collections.singleton(skill));
	}

	@Test
	void professionalIsOptInAndContextCopiesSelection() {
		PickupModifier modifier = skill.getPickupModifiers().iterator().next();
		assertTrue(modifier.isOptional());
		assertFalse(modifier.appliesToContext(skill, new PickupContext(game, player)));
		assertFalse(modifier.appliesToContext(skill, new PickupContext(game, player, null)));
		Set<Skill> selected = new HashSet<>(Collections.singleton(skill));
		PickupContext context = new PickupContext(game, player, selected);
		selected.clear();
		assertTrue(modifier.appliesToContext(skill, context));
		assertEquals(-1, modifier.getModifier());
	}

	@Test
	void availabilityHonoursActingPlayerUsage() {
		assertEquals(Collections.singletonList(skill), service.availableSkills(game, player,
			selected -> new PickupContext(game, player, selected), Skill::getPickupModifiers));
		when(actingPlayer.isSkillUsed(skill)).thenReturn(true);
		assertTrue(service.availableSkills(game, player,
			selected -> new PickupContext(game, player, selected), Skill::getPickupModifiers).isEmpty());
	}

	@Test
	void availabilityHonoursNonActingPlayerUsage() {
		when(actingPlayer.getPlayer()).thenReturn(null);
		assertEquals(Collections.singletonList(skill), service.availableSkills(game, player,
			selected -> new PickupContext(game, player, selected), Skill::getPickupModifiers));
		when(player.isUsed(skill)).thenReturn(true);
		assertTrue(service.availableSkills(game, player,
			selected -> new PickupContext(game, player, selected), Skill::getPickupModifiers).isEmpty());
	}

	@Test
	void unlimitedExtraArmsAndExistingConstructorsRemainAutomatic() {
		ExtraArms extraArms = new ExtraArms();
		extraArms.postConstruct();
		for (PickupModifier modifier : extraArms.getPickupModifiers()) {
			assertFalse(modifier.isOptional());
			assertTrue(modifier.appliesToContext(extraArms, new PickupContext(game, player)));
		}
		assertFalse(new PickupModifier("old", -1, ModifierType.REGULAR).isOptional());
	}

	@Test
	void olderProfessionalDoesNotAcquirePickupModifier() {
		com.fumbbl.ffb.skill.bb2020.special.ConsummateProfessional oldSkill =
			new com.fumbbl.ffb.skill.bb2020.special.ConsummateProfessional();
		oldSkill.postConstruct();
		assertTrue(oldSkill.getPickupModifiers().isEmpty());
	}
}
