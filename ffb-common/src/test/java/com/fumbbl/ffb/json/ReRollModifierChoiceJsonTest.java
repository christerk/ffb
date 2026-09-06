package com.fumbbl.ffb.json;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.dialog.DialogReRollModifierChoiceParameter;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.DialogIdFactory;
import com.fumbbl.ffb.factory.ReRollPropertyFactory;
import com.fumbbl.ffb.factory.ReRolledActionFactory;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.factory.application.NetCommandIdFactory;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandReRollModifierChoice;
import com.fumbbl.ffb.skill.bb2025.BreakTackle;
import com.fumbbl.ffb.skill.bb2025.special.ConsummateProfessional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Round trip tests for the JSON payloads of the optional dodge modifier selection.
 */
class ReRollModifierChoiceJsonTest {

	private IFactorySource source;
	private BreakTackle breakTackle;
	private ConsummateProfessional consummateProfessional;

	@BeforeEach
	void setUp() {
		breakTackle = new BreakTackle();
		breakTackle.postConstruct();
		consummateProfessional = new ConsummateProfessional();
		consummateProfessional.postConstruct();

		List<Skill> skills = Arrays.asList(breakTackle, consummateProfessional);
		SkillFactory skillFactory = mock(SkillFactory.class);
		when(skillFactory.forName(anyString())).then(invocation -> skills.stream()
			.filter(skill -> skill.getName().equalsIgnoreCase(invocation.getArgument(0))).findFirst().orElse(null));

		source = mock(IFactorySource.class);
		when(source.forContext(any())).thenReturn(source);
		when(source.<SkillFactory>getFactory(Factory.SKILL)).thenReturn(skillFactory);
		when(source.<ReRolledActionFactory>getFactory(Factory.RE_ROLLED_ACTION)).thenReturn(new ReRolledActionFactory());
		when(source.<DialogIdFactory>getFactory(Factory.DIALOG_ID)).thenReturn(new DialogIdFactory());
		when(source.<NetCommandIdFactory>getFactory(Factory.NET_COMMAND_ID)).thenReturn(new NetCommandIdFactory());
		when(source.<ReRollPropertyFactory>getFactory(Factory.RE_ROLL_PROPERTY)).thenReturn(new ReRollPropertyFactory());
	}

	@Test
	void dodgeModifierOptionSurvivesRoundTrip() {
		ModifierChoiceOption option = new ModifierChoiceOption(Arrays.asList(breakTackle, consummateProfessional), -2, 3);

		ModifierChoiceOption restored = new ModifierChoiceOption().initFrom(source, option.toJsonValue());

		assertEquals(Arrays.asList(breakTackle, consummateProfessional), restored.getSkills());
		assertEquals(-2, restored.getTotalModifier());
		assertEquals(3, restored.getMinimumRoll());
		assertEquals("Break Tackle + Consummate Professional", restored.getLabel());
	}

	@Test
	void dialogParameterSurvivesRoundTrip() {
		ModifierChoiceOption option = new ModifierChoiceOption(Collections.singletonList(breakTackle), -1, 4);
		DialogReRollModifierChoiceParameter parameter =
			new DialogReRollModifierChoiceParameter("playerId", ReRolledActions.DODGE, 5, 4,
				Collections.singletonList(option), Arrays.asList(ReRollProperty.TRR, ReRollProperty.PRO), true,
				consummateProfessional, CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN, "someKey",
				Collections.singletonList("a message"));

		DialogReRollModifierChoiceParameter restored =
			new DialogReRollModifierChoiceParameter().initFrom(source, parameter.toJsonValue());

		assertEquals(DialogId.RE_ROLL_MODIFIER_CHOICE, restored.getId());
		assertEquals("playerId", restored.getPlayerId());
		assertEquals(ReRolledActions.DODGE, restored.getReRolledAction());
		assertEquals(5, restored.getMinimumRoll());
		assertEquals(4, restored.getRoll());
		assertTrue(restored.isFumble());
		assertEquals(consummateProfessional, restored.getReRollSkill());
		assertEquals(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN, restored.getMenuProperty());
		assertEquals("someKey", restored.getDefaultValueKey());
		assertEquals(Collections.singletonList("a message"), restored.getMessages());
		assertTrue(restored.hasProperty(ReRollProperty.TRR));
		assertTrue(restored.hasProperty(ReRollProperty.PRO));
		assertFalse(restored.hasProperty(ReRollProperty.LONER));
		assertEquals(Collections.singletonList("Break Tackle"),
			restored.getModifierOptions().stream().map(ModifierChoiceOption::getLabel).collect(Collectors.toList()));
		assertEquals(4, restored.getModifierOptions().get(0).getMinimumRoll());
	}

	@Test
	void clientCommandSurvivesRoundTrip() {
		ClientCommandReRollModifierChoice command = new ClientCommandReRollModifierChoice("playerId",
			Arrays.asList(breakTackle, consummateProfessional), ReRolledActions.DODGE);

		ClientCommandReRollModifierChoice restored =
			new ClientCommandReRollModifierChoice().initFrom(source, command.toJsonValue());

		assertEquals("playerId", restored.getPlayerId());
		assertEquals(Arrays.asList(breakTackle, consummateProfessional), restored.getSkills());
		assertEquals(ReRolledActions.DODGE, restored.getReRolledAction());
	}
}
