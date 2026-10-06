package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.client.ClientLayout;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.dialog.DialogReRollModifierChoiceParameter;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.skill.bb2025.special.ConsummateProfessional;

import org.junit.jupiter.api.Test;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class DialogReRollModifierChoiceKeyboardTest {

	@Test
	void unavailableRerollShortcutsLeaveModifierChoiceOpen() {
		DialogReRollModifierChoice dialog = dialog(Collections.emptyList());
		IDialogCloseListener listener = mock(IDialogCloseListener.class);
		doReturn(listener).when(dialog).getCloseListener();

		for (int key : new int[]{KeyEvent.VK_T, KeyEvent.VK_F, KeyEvent.VK_P, KeyEvent.VK_S}) {
			release(dialog, key);
		}

		verifyNoInteractions(listener);
		assertNull(dialog.getReRollSource());
	}

	@Test
	void modifierShortcutStillSelectsTheSkill() {
		DialogReRollModifierChoice dialog = dialog(Collections.emptyList());
		IDialogCloseListener listener = mock(IDialogCloseListener.class);
		doReturn(listener).when(dialog).getCloseListener();

		release(dialog, KeyEvent.VK_1);

		verify(listener).dialogClosed(dialog);
		assertTrue(dialog.isUseModifiers());
		assertEquals("Consummate Professional", dialog.getSelectedOption().getSkills().get(0).getName());
	}

	@Test
	void declineShortcutStillClosesWithoutReroll() {
		DialogReRollModifierChoice dialog = dialog(Collections.emptyList());
		IDialogCloseListener listener = mock(IDialogCloseListener.class);
		doReturn(listener).when(dialog).getCloseListener();

		release(dialog, KeyEvent.VK_N);

		verify(listener).dialogClosed(dialog);
		assertNull(dialog.getReRollSource());
	}

	@Test
	void offeredTeamRerollShortcutStillWorks() {
		DialogReRollModifierChoice dialog = dialog(Collections.singletonList(ReRollProperty.TRR));
		IDialogCloseListener listener = mock(IDialogCloseListener.class);
		doReturn(listener).when(dialog).getCloseListener();

		release(dialog, KeyEvent.VK_T);

		verify(listener).dialogClosed(dialog);
		assertEquals(ReRollSources.TEAM_RE_ROLL, dialog.getReRollSource());
	}

	private DialogReRollModifierChoice dialog(List<ReRollProperty> properties) {
		FantasyFootballClient client = mock(FantasyFootballClient.class, RETURNS_DEEP_STUBS);
		when(client.getUserInterface().getPitchDimensionProvider()).thenReturn(
			new PitchDimensionProvider(new LayoutSettings(ClientLayout.LANDSCAPE, 1.0)));
		when(client.getUserInterface().getIconCache().getIconByProperty(any(), any()))
			.thenReturn(new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB));
		Skill skill = new ConsummateProfessional();
		skill.postConstruct();
		List<ModifierChoiceOption> options = Collections.singletonList(
			new ModifierChoiceOption(Collections.singletonList(skill), -1, 2));
		DialogReRollModifierChoiceParameter parameter = new DialogReRollModifierChoiceParameter(
			"player", ReRolledActions.JUMP_UP, 3, 2, options, options, properties,
			false, null, null, null, Collections.emptyList());
		return spy(new DialogReRollModifierChoice(client, parameter));
	}

	private void release(DialogReRollModifierChoice dialog, int key) {
		dialog.keyReleased(new KeyEvent(dialog, KeyEvent.KEY_RELEASED, 0, 0, key, KeyEvent.CHAR_UNDEFINED));
	}
}
