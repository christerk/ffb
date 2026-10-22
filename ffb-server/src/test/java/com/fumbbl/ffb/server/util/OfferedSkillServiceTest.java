package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.dialog.DialogReRollModifierChoiceParameter;
import com.fumbbl.ffb.dialog.DialogReRollParameter;
import com.fumbbl.ffb.dialog.DialogReRollPropertiesParameter;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OfferedSkillServiceTest {

	private final OfferedSkillService service = new OfferedSkillService();
	private Game game;
	private ActingPlayer actingPlayer;
	private Skill skill;
	private Skill otherSkill;

	@BeforeEach
	void setUp() {
		game = mock(Game.class);
		actingPlayer = mock(ActingPlayer.class);
		when(game.getActingPlayer()).thenReturn(actingPlayer);
		skill = mock(Skill.class);
		otherSkill = mock(Skill.class);
	}

	@Test
	void nothingIsOfferedWithoutDialog() {
		assertFalse(service.isOffered(game, skill));
	}

	@Test
	void nullSkillIsNeverOffered() {
		when(game.getDialogParameter()).thenReturn(new DialogSkillUseParameter("playerId", skill, 0));
		assertFalse(service.isOffered(game, null));
	}

	@Test
	void skillUseDialogOffersSkillAndModifyingSkill() {
		when(game.getDialogParameter()).thenReturn(new DialogSkillUseParameter("playerId", skill, 0, otherSkill));

		assertTrue(service.isOffered(game, skill));
		assertTrue(service.isOffered(game, otherSkill));
		assertFalse(service.isOffered(game, mock(Skill.class)));
	}

	@Test
	void reRollDialogOffersReRollSkillAndModifyingSkill() {
		when(game.getDialogParameter()).thenReturn(new DialogReRollParameter("playerId", ReRolledActions.CATCH, 3,
			false, false, false, skill, null, otherSkill, null, null, null));

		assertTrue(service.isOffered(game, skill));
		assertTrue(service.isOffered(game, otherSkill));
		assertFalse(service.isOffered(game, mock(Skill.class)));
	}

	@Test
	void reRollPropertiesDialogOffersReRollSkillAndModifyingSkill() {
		when(game.getDialogParameter()).thenReturn(new DialogReRollPropertiesParameter("playerId", ReRolledActions.CATCH,
			3, Collections.emptyList(), false, skill, otherSkill, null, null, null));

		assertTrue(service.isOffered(game, skill));
		assertTrue(service.isOffered(game, otherSkill));
		assertFalse(service.isOffered(game, mock(Skill.class)));
	}

	@Test
	void reRollModifierChoiceDialogOffersReRollSkill() {
		when(game.getDialogParameter()).thenReturn(new DialogReRollModifierChoiceParameter("playerId",
			ReRolledActions.CATCH, 3, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), false,
			skill, null, null, null));

		assertTrue(service.isOffered(game, skill));
		assertFalse(service.isOffered(game, otherSkill));
	}

	@Test
	void offeringPlayerIsTheDialogPlayer() {
		when(game.getDialogParameter()).thenReturn(new DialogReRollPropertiesParameter("playerId", ReRolledActions.CATCH,
			3, Collections.emptyList(), false, skill, null, null, null, null));

		assertEquals("playerId", service.offeringPlayerId(game, skill));
		assertNull(service.offeringPlayerId(game, otherSkill));
	}

	@Test
	void noOfferingPlayerWithoutDialog() {
		assertNull(service.offeringPlayerId(game, skill));
	}

	@Test
	void modifierOptionMustMatchAnOfferedOptionExactly() {
		modifierChoiceDialog(Arrays.asList(skill, otherSkill));

		assertTrue(service.isOfferedModifierOption(game, "playerId", ReRolledActions.JUMP, 2,
			Arrays.asList(otherSkill, skill)));
		assertFalse(service.isOfferedModifierOption(game, "playerId", ReRolledActions.JUMP, 2,
			Collections.singletonList(skill)));
		assertFalse(service.isOfferedModifierOption(game, "playerId", ReRolledActions.JUMP, 2,
			Arrays.asList(skill, otherSkill, mock(Skill.class))));
	}

	@Test
	void modifierOptionRequiresMatchingDialog() {
		modifierChoiceDialog(Collections.singletonList(skill));
		List<Skill> skills = Collections.singletonList(skill);

		assertTrue(service.isOfferedModifierOption(game, "playerId", ReRolledActions.JUMP, 2, skills));
		assertFalse(service.isOfferedModifierOption(game, "otherPlayerId", ReRolledActions.JUMP, 2, skills));
		assertFalse(service.isOfferedModifierOption(game, "playerId", ReRolledActions.DODGE, 2, skills));
		assertFalse(service.isOfferedModifierOption(game, "playerId", ReRolledActions.JUMP, 3, skills));
		assertFalse(service.isOfferedModifierOption(game, null, ReRolledActions.JUMP, 2, skills));
	}

	@Test
	void modifierOptionRejectsEmptyAndDuplicateSelections() {
		modifierChoiceDialog(Collections.singletonList(skill));

		assertFalse(service.isOfferedModifierOption(game, "playerId", ReRolledActions.JUMP, 2, Collections.emptyList()));
		assertFalse(service.isOfferedModifierOption(game, "playerId", ReRolledActions.JUMP, 2, null));
		assertFalse(service.isOfferedModifierOption(game, "playerId", ReRolledActions.JUMP, 2,
			Arrays.asList(skill, skill)));
	}

	@Test
	void noModifierOptionWithoutModifierChoiceDialog() {
		when(game.getDialogParameter()).thenReturn(new DialogSkillUseParameter("playerId", skill, 0));

		assertFalse(service.isOfferedModifierOption(game, "playerId", ReRolledActions.JUMP, 2,
			Collections.singletonList(skill)));
	}

	private void modifierChoiceDialog(List<Skill> skills) {
		DialogReRollModifierChoiceParameter parameter = new DialogReRollModifierChoiceParameter("playerId",
			ReRolledActions.JUMP, 4,
			2, Collections.singletonList(new ModifierChoiceOption(skills, 1, 3)), Collections.emptyList(),
			Collections.emptyList(), false, null, null, null, null);
		when(game.getDialogParameter()).thenReturn(parameter);
	}

	@Test
	void playerWithoutSkillHasNoUnusedSkill() {
		Player<?> player = player();
		when(player.getSkillsIncludingTemporaryOnes()).thenReturn(Collections.emptySet());

		assertFalse(service.hasUnused(game, player, skill));
		assertFalse(service.hasUnused(game, null, skill));
	}

	@Test
	void usageOfActingPlayerIsTakenFromActivation() {
		Player<?> player = player();
		when(actingPlayer.getPlayer()).thenAnswer(invocation -> player);

		when(actingPlayer.isSkillUsed(skill)).thenReturn(false);
		assertTrue(service.hasUnused(game, player, skill));

		when(actingPlayer.isSkillUsed(skill)).thenReturn(true);
		assertFalse(service.hasUnused(game, player, skill));
	}

	@Test
	void usageOfOtherPlayerIsTakenFromPlayer() {
		Player<?> player = player();

		when(player.isUsed(skill)).thenReturn(false);
		assertTrue(service.hasUnused(game, player, skill));

		when(player.isUsed(skill)).thenReturn(true);
		assertFalse(service.hasUnused(game, player, skill));
	}

	private Player<?> player() {
		Player<?> player = mock(Player.class);
		when(player.getSkillsIncludingTemporaryOnes()).thenReturn(Collections.singleton(skill));
		return player;
	}
}
