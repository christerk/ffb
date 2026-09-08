package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.ReRollOptions;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.dialog.DialogReRollModifierChoiceParameter;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.server.util.ReRollRequest;

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

class ReRollModifierChoiceDialogParameterFactoryTest {

	private static final int ROLL = 3;
	private static final int MINIMUM_ROLL = 4;

	private ReRollRequest request;
	private final List<ModifierChoiceOption> options =
		Collections.singletonList(new ModifierChoiceOption(Collections.emptyList(), 1, 3));

	@BeforeEach
	void setUp() {
		Player<?> player = mock(Player.class);
		when(player.getId()).thenReturn("playerId");
		request = ReRollRequest.forPlayer(null, player, ReRolledActions.DODGE, MINIMUM_ROLL).build();
	}

	@Test
	void keepsAllPropertiesForSkillsThatCannotBeUsedEveryTurn() {
		Skill reRollSkill = skill(SkillUsageType.ONCE_PER_GAME);
		ReRollOptions reRollOptions = new ReRollOptions(
			Arrays.asList(ReRollProperty.TRR, ReRollProperty.LONER, ReRollProperty.PRO), reRollSkill);

		DialogReRollModifierChoiceParameter parameter = create(reRollOptions, true);

		assertTrue(parameter.hasProperty(ReRollProperty.TRR));
		assertTrue(parameter.hasProperty(ReRollProperty.LONER));
		assertTrue(parameter.hasProperty(ReRollProperty.PRO));
		assertEquals(reRollSkill, parameter.getReRollSkill());
		assertEquals(ROLL, parameter.getRoll());
		assertEquals(MINIMUM_ROLL, parameter.getMinimumRoll());
		assertEquals(options, parameter.getModifierOptions());
	}

	@Test
	void stripsTeamReRollPropertiesForSkillsUsableEveryTurn() {
		ReRollOptions reRollOptions = new ReRollOptions(
			Arrays.asList(ReRollProperty.TRR, ReRollProperty.MASCOT, ReRollProperty.BRILLIANT_COACHING,
				ReRollProperty.PUMP_UP_THE_CROWD, ReRollProperty.SHOW_STAR, ReRollProperty.LONER, ReRollProperty.PRO),
			skill(SkillUsageType.REGULAR));

		DialogReRollModifierChoiceParameter parameter = create(reRollOptions, true);

		assertTrue(parameter.hasProperty(ReRollProperty.PRO));
		assertFalse(parameter.hasProperty(ReRollProperty.TRR));
		assertFalse(parameter.hasProperty(ReRollProperty.MASCOT));
		assertFalse(parameter.hasProperty(ReRollProperty.BRILLIANT_COACHING));
		assertFalse(parameter.hasProperty(ReRollProperty.PUMP_UP_THE_CROWD));
		assertFalse(parameter.hasProperty(ReRollProperty.SHOW_STAR));
		assertFalse(parameter.hasProperty(ReRollProperty.LONER));
	}

	@Test
	void stripsTeamReRollPropertiesForSkillsUsableOncePerTurn() {
		ReRollOptions reRollOptions = new ReRollOptions(Collections.singletonList(ReRollProperty.TRR),
			skill(SkillUsageType.ONCE_PER_TURN));

		DialogReRollModifierChoiceParameter parameter = create(reRollOptions, true);

		assertFalse(parameter.hasProperty(ReRollProperty.TRR));
	}

	@Test
	void omitsReRollsWhenTheyAreNotAllowed() {
		ReRollOptions reRollOptions = new ReRollOptions(Collections.singletonList(ReRollProperty.TRR),
			skill(SkillUsageType.ONCE_PER_GAME));

		DialogReRollModifierChoiceParameter parameter = create(reRollOptions, false);

		assertFalse(parameter.hasProperty(ReRollProperty.TRR));
		assertNull(parameter.getReRollSkill());
		assertEquals(options, parameter.getModifierOptions());
	}

	@Test
	void showsNoDialogWithoutOptionsAndReRolls() {
		ReRollOptions reRollOptions = new ReRollOptions(Collections.singletonList(ReRollProperty.LONER), null);

		assertNull(new ReRollModifierChoiceDialogParameterFactory(ROLL, Collections.emptyList(), true)
			.create(request, reRollOptions));
	}

	private Skill skill(SkillUsageType usageType) {
		Skill skill = mock(Skill.class);
		when(skill.getSkillUsageType()).thenReturn(usageType);
		return skill;
	}

	private DialogReRollModifierChoiceParameter create(ReRollOptions reRollOptions, boolean reRollAllowed) {
		IDialogParameter parameter = new ReRollModifierChoiceDialogParameterFactory(ROLL, options, reRollAllowed)
			.create(request, reRollOptions);
		return (DialogReRollModifierChoiceParameter) parameter;
	}
}
