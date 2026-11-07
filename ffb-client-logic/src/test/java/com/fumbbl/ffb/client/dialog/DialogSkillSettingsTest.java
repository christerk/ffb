package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.CommonPropertyValue;
import com.fumbbl.ffb.client.ClientLayout;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.client.UiDimensionProvider;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.ui.menu.GameMenuBar;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JComboBox;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import org.junit.jupiter.api.Test;

import java.awt.Dimension;
import java.awt.Component;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DialogSkillSettingsTest {

	@Test
	void requiredDialogHighlightsUnsetSettingsAndEnablesSaveOnlyWhenComplete() throws Exception {
		FantasyFootballClient client = client();
		DialogSkillSettings dialog = new DialogSkillSettings(client, entries(
			CommonPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_ALWAYS), entries(
			CommonPropertyValue.SETTING_DODGE_BLOCK_ALWAYS), true);

		JComboBox<String> reRollBox = field(dialog, "reRollBallAndChainBox");
		JComboBox<String> dodgeBox = field(dialog, "dodgeBlockBox");
		JButton saveButton = field(dialog, "saveButton");
		JButton cancelButton = field(dialog, "cancelButton");
		JLabel reRollLabel = field(dialog, "reRollBallAndChainLabel");
		JLabel dodgeLabel = field(dialog, "dodgeBlockLabel");

		assertFalse(saveButton.isEnabled());
		assertTrue(reRollBox.getSelectedIndex() < 0);
		assertTrue(dodgeBox.getSelectedIndex() < 0);
		assertTrue(reRollLabel.isOpaque());
		assertTrue(dodgeLabel.isOpaque());
		assertFalse(contains(dialog.getContentPane(), cancelButton));

		reRollBox.setSelectedIndex(0);
		assertFalse(saveButton.isEnabled());
		dodgeBox.setSelectedIndex(0);
		assertTrue(saveButton.isEnabled());
		assertFalse(reRollLabel.isOpaque());
		assertFalse(dodgeLabel.isOpaque());
	}

	private FantasyFootballClient client() {
		FantasyFootballClient client = mock(FantasyFootballClient.class, RETURNS_DEEP_STUBS);
		UserInterface userInterface = mock(UserInterface.class, RETURNS_DEEP_STUBS);
		GameMenuBar menuBar = mock(GameMenuBar.class);
		LayoutSettings settings = new LayoutSettings(ClientLayout.LANDSCAPE, 1.0);
		when(client.getUserInterface()).thenReturn(userInterface);
		when(userInterface.getGameMenuBar()).thenReturn(menuBar);
		when(userInterface.getPitchDimensionProvider()).thenReturn(new PitchDimensionProvider(settings));
		when(userInterface.getUiDimensionProvider()).thenReturn(new UiDimensionProvider(settings));
		when(userInterface.getSize()).thenReturn(new Dimension(1000, 800));
		when(menuBar.menuName(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN)).thenReturn("Ask for Whirling Dervish");
		when(menuBar.menuName(CommonProperty.SETTING_DODGE_BLOCK)).thenReturn("Ask for Dodge on Stumble");
		return client;
	}

	private Map<String, String> entries(String value) {
		Map<String, String> entries = new LinkedHashMap<>();
		entries.put(value, "Always");
		return entries;
	}

	private boolean contains(java.awt.Container container, Component expected) {
		if (container == expected) {
			return true;
		}
		for (Component child : container.getComponents()) {
			if (child == expected || child instanceof java.awt.Container && contains((java.awt.Container) child, expected)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private <T> T field(Object target, String name) throws ReflectiveOperationException {
		Field field = target.getClass().getDeclaredField(name);
		field.setAccessible(true);
		return (T) field.get(target);
	}
}
