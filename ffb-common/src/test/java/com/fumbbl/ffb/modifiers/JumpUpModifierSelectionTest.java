package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.skill.bb2025.special.ConsummateProfessional;
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

class JumpUpModifierSelectionTest {
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
		JumpUpModifier modifier = skill.getJumpUpModifiers().iterator().next();
		assertTrue(modifier.isOptional());
		assertFalse(modifier.appliesToContext(skill, new JumpUpContext(actingPlayer, game)));
		assertFalse(modifier.appliesToContext(skill, new JumpUpContext(actingPlayer, game, null)));
		Set<Skill> selected = new HashSet<>(Collections.singleton(skill));
		JumpUpContext context = new JumpUpContext(actingPlayer, game, selected);
		selected.clear();
		assertTrue(modifier.appliesToContext(skill, context));
		assertEquals(-1, modifier.getModifier());
		assertEquals(ModifierType.REGULAR, modifier.getType());
	}

	@Test
	void availabilityHonoursActingPlayerUsage() {
		assertEquals(Collections.singletonList(skill), service.availableSkills(game, player,
			selected -> new JumpUpContext(actingPlayer, game, selected), Skill::getJumpUpModifiers));
		when(actingPlayer.isSkillUsed(skill)).thenReturn(true);
		assertTrue(service.availableSkills(game, player,
			selected -> new JumpUpContext(actingPlayer, game, selected), Skill::getJumpUpModifiers).isEmpty());
	}

	@Test
	void noSkillMeansNoOptionalModifiers() {
		when(player.getSkillsIncludingTemporaryOnes()).thenReturn(Collections.emptySet());
		assertTrue(service.availableSkills(game, player,
			selected -> new JumpUpContext(actingPlayer, game, selected), Skill::getJumpUpModifiers).isEmpty());
	}

	@Test
	void existingModifiersAndOlderProfessionalRemainUnchanged() {
		assertFalse(new JumpUpModifier("old", -1, ModifierType.REGULAR).isOptional());
		assertEquals(-1, new com.fumbbl.ffb.modifiers.mixed.JumpUpModifierCollection()
			.getModifiers().iterator().next().getModifier());
		assertEquals(-2, new com.fumbbl.ffb.modifiers.bb2016.JumpUpModifierCollection()
			.getModifiers().iterator().next().getModifier());
		com.fumbbl.ffb.skill.bb2020.special.ConsummateProfessional oldSkill =
			new com.fumbbl.ffb.skill.bb2020.special.ConsummateProfessional();
		oldSkill.postConstruct();
		assertTrue(oldSkill.getJumpUpModifiers().isEmpty());
	}

	@Test
	void jumpUpModifierDoesNotEnableUnimplementedAgilityRolls() {
		assertTrue(skill.getJumpModifiers().isEmpty());
		assertTrue(skill.getCatchModifiers().isEmpty());
		assertTrue(skill.getInterceptionModifiers().isEmpty());
	}
}
