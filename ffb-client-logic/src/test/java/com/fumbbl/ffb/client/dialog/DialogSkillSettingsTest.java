package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.CommonPropertyValue;
import com.fumbbl.ffb.client.ClientLayout;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.client.StyleProvider;
import com.fumbbl.ffb.client.UiDimensionProvider;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.ui.SideBarComponent;
import com.fumbbl.ffb.client.ui.TurnDiceStatusComponent;
import com.fumbbl.ffb.client.ui.menu.GameMenuBar;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JComboBox;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import org.junit.jupiter.api.Test;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JDesktopPane;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.ToolTipManager;
import javax.swing.border.LineBorder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DialogSkillSettingsTest {

	@Test
	void requiredDialogHighlightsUnsetSettingsAndEnablesSaveOnlyWhenComplete() throws Exception {
		FantasyFootballClient client = client();
		DialogSkillSettings dialog = new DialogSkillSettings(client, settingsEntriesWithAllOptions(), true);
		assertFalse(ToolTipManager.sharedInstance().isLightWeightPopupEnabled());

		Map<CommonProperty, JComboBox<String>> settingBoxes = field(dialog, "settingBoxes");
		Map<CommonProperty, JLabel> settingLabels = field(dialog, "settingLabels");
		Map<CommonProperty, JLabel> settingHelpLabels = field(dialog, "settingHelpLabels");
		JComboBox<String> reRollBox = settingBoxes.get(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN);
		JComboBox<String> dodgeBox = settingBoxes.get(CommonProperty.SETTING_DODGE_BLOCK);
		JLabel reRollLabel = settingLabels.get(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN);
		JLabel dodgeLabel = settingLabels.get(CommonProperty.SETTING_DODGE_BLOCK);
		JLabel reRollHelp = settingHelpLabels.get(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN);
		JLabel dodgeHelp = settingHelpLabels.get(CommonProperty.SETTING_DODGE_BLOCK);
		JButton saveButton = field(dialog, "saveButton");
		JButton cancelButton = field(dialog, "cancelButton");

		assertFalse(saveButton.isEnabled());
		assertTrue(reRollBox.getSelectedIndex() < 0);
		assertTrue(dodgeBox.getSelectedIndex() < 0);
		assertTrue(reRollBox.getBorder() instanceof LineBorder);
		assertEquals(java.awt.Color.RED, ((LineBorder) reRollBox.getBorder()).getLineColor());
		assertTrue(dodgeBox.getBorder() instanceof LineBorder);
		assertEquals(java.awt.Color.RED, ((LineBorder) dodgeBox.getBorder()).getLineColor());
		assertFalse(reRollLabel.getBorder() instanceof LineBorder);
		JPanel contentPanel = (JPanel) dialog.getContentPane().getComponent(0);
		JPanel settingsPanel = (JPanel) contentPanel.getComponent(0);
		assertEquals(10, settingsPanel.getBorder().getBorderInsets(settingsPanel).left);
		assertEquals(10, settingsPanel.getBorder().getBorderInsets(settingsPanel).top);
		GridBagLayout tableLayout = (GridBagLayout) settingsPanel.getLayout();
		assertEquals(5, tableLayout.getConstraints(reRollLabel).insets.right);
		assertEquals(0, tableLayout.getConstraints(reRollLabel).gridx);
		assertEquals(1, tableLayout.getConstraints(reRollBox).gridx);
		assertEquals(1, tableLayout.getConstraints(dodgeBox).gridx);
		assertEquals(2, tableLayout.getConstraints(reRollHelp).gridx);
		assertEquals(2, tableLayout.getConstraints(dodgeHelp).gridx);
		assertTrue(reRollHelp.getIcon() != null);
		assertTrue(dodgeHelp.getIcon() != null);
		assertTrue(reRollHelp.getToolTipText().contains("Always"));
		assertTrue(reRollHelp.getToolTipText().contains("When not hitting an opponent"));
		assertTrue(reRollHelp.getToolTipText().contains("When hitting Team-mate"));
		assertTrue(reRollHelp.getToolTipText().contains("Never"));
		assertTrue(dodgeHelp.getToolTipText().contains("Always"));
		assertTrue(dodgeHelp.getToolTipText().contains("Widezone & Chainpush"));
		assertTrue(dodgeHelp.getToolTipText().contains("Sideline & Chainpush"));
		assertTrue(dodgeHelp.getToolTipText().contains("Never"));
		assertTrue(dodgeHelp.getToolTipText().contains("forced chain push"));
		assertTrue(dodgeHelp.getToolTipText().contains("wide-zone row or a four-square-deep endzone area"));
		assertFalse(contains(dialog.getContentPane(), cancelButton));

		reRollBox.setSelectedIndex(0);
		assertFalse(saveButton.isEnabled());
		dodgeBox.setSelectedIndex(0);
		assertTrue(saveButton.isEnabled());
		assertFalse(reRollBox.getBorder() instanceof LineBorder);
		assertFalse(dodgeBox.getBorder() instanceof LineBorder);

		saveButton.doClick();
		assertTrue(dialog.isSaved());
		assertEquals(CommonPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_ALWAYS,
			dialog.getSetting(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN));
		assertEquals(CommonPropertyValue.SETTING_DODGE_BLOCK_ALWAYS,
			dialog.getSetting(CommonProperty.SETTING_DODGE_BLOCK));
	}

	@Test
	void requiredDialogBlocksAndRestoresBackgroundInteraction() throws Exception {
		FantasyFootballClient client = client();
		UserInterface userInterface = client.getUserInterface();
		JDesktopPane desktop = new JDesktopPane();
		JPanel background = new JPanel();
		JButton backgroundButton = new JButton(new PitchDimensionProvider(
			new LayoutSettings(ClientLayout.LANDSCAPE, 1.0)), "Background");
		background.add(backgroundButton);
		desktop.add(background);
		JMenuBar menuBar = new JMenuBar();
		JMenuItem menuItem = new JMenuItem("Menu");
		menuBar.add(menuItem);
		JRootPane rootPane = new JRootPane();
		rootPane.setJMenuBar(menuBar);
		rootPane.setContentPane(desktop);
		when(userInterface.getDesktop()).thenReturn(desktop);
		when(userInterface.getRootPane()).thenReturn(rootPane);

		DialogSkillSettings dialog = new DialogSkillSettings(client, settingsEntries(
			CommonPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_ALWAYS,
			CommonPropertyValue.SETTING_DODGE_BLOCK_ALWAYS), true);
		Map<CommonProperty, JComboBox<String>> settingBoxes = field(dialog, "settingBoxes");
		JComboBox<String> dialogBox = settingBoxes.get(CommonProperty.SETTING_DODGE_BLOCK);

		dialog.showDialog(null);
		assertFalse(backgroundButton.isEnabled());
		assertFalse(menuItem.isEnabled());
		assertTrue(dialogBox.isEnabled());

		dialog.hideDialog();
		assertTrue(backgroundButton.isEnabled());
		assertTrue(menuItem.isEnabled());
		org.mockito.Mockito.verify(userInterface).beginBackgroundInteractionBlock();
		org.mockito.Mockito.verify(userInterface).endBackgroundInteractionBlock();
	}

	@Test
	void endTurnControlIgnoresClicksWhileRequiredDialogIsOpen() {
		FantasyFootballClient client = mock(FantasyFootballClient.class);
		UserInterface userInterface = mock(UserInterface.class);
		SideBarComponent sideBar = mock(SideBarComponent.class);
		LayoutSettings settings = new LayoutSettings(ClientLayout.LANDSCAPE, 1.0);
		when(client.getUserInterface()).thenReturn(userInterface);
		when(sideBar.getClient()).thenReturn(client);
		when(userInterface.isBackgroundInteractionBlocked()).thenReturn(true);
		TurnDiceStatusComponent component = new TurnDiceStatusComponent(sideBar,
			new UiDimensionProvider(settings), new StyleProvider());

		component.mouseReleased(new MouseEvent(new JPanel(), MouseEvent.MOUSE_RELEASED,
			System.currentTimeMillis(), 0, 0, 0, 1, false));

		verify(client, never()).getGame();
	}

	@Test
	void requiredDialogClosesImmediatelyWhenAllSettingsAreConfigured() {
		FantasyFootballClient client = client();
		UserInterface userInterface = client.getUserInterface();
		JDesktopPane desktop = new JDesktopPane();
		JRootPane rootPane = new JRootPane();
		rootPane.setContentPane(desktop);
		when(userInterface.getDesktop()).thenReturn(desktop);
		when(userInterface.getRootPane()).thenReturn(rootPane);
		String reRollSetting = CommonPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_ALWAYS;
		String dodgeSetting = CommonPropertyValue.SETTING_DODGE_BLOCK_ALWAYS;
		when(client.getProperty(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN)).thenReturn(reRollSetting);
		when(client.getProperty(CommonProperty.SETTING_DODGE_BLOCK)).thenReturn(dodgeSetting);
		DialogSkillSettings dialog = new DialogSkillSettings(client, settingsEntries(reRollSetting, dodgeSetting), true);
		AtomicInteger shownEvents = new AtomicInteger();
		dialog.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentShown(java.awt.event.ComponentEvent event) {
				shownEvents.incrementAndGet();
			}
		});

		dialog.showDialog(closedDialog -> closedDialog.hideDialog());

		assertFalse(dialog.isVisible());
		assertEquals(0, shownEvents.get());
		assertEquals(0, desktop.getComponentCount());
		org.mockito.Mockito.verify(userInterface, never()).beginBackgroundInteractionBlock();
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

	private Map<CommonProperty, Map<String, String>> settingsEntries(String reRollSetting, String dodgeSetting) {
		Map<CommonProperty, Map<String, String>> entries = new LinkedHashMap<>();
		entries.put(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN, entries(reRollSetting));
		entries.put(CommonProperty.SETTING_DODGE_BLOCK, entries(dodgeSetting));
		return entries;
	}

	private Map<CommonProperty, Map<String, String>> settingsEntriesWithAllOptions() {
		Map<CommonProperty, Map<String, String>> entries = new LinkedHashMap<>();
		Map<String, String> reRollEntries = new LinkedHashMap<>();
		reRollEntries.put(CommonPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_ALWAYS, "Always");
		reRollEntries.put(CommonPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_NO_OPPONENT, "When not hitting an opponent");
		reRollEntries.put(CommonPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_TEAM_MATE, "When hitting Team-mate");
		reRollEntries.put(CommonPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_NEVER, "Never");
		entries.put(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN, reRollEntries);

		Map<String, String> dodgeEntries = new LinkedHashMap<>();
		dodgeEntries.put(CommonPropertyValue.SETTING_DODGE_BLOCK_ALWAYS, "Always");
		dodgeEntries.put(CommonPropertyValue.SETTING_DODGE_BLOCK_WIDE_ZONE, "Widezone & Chainpush");
		dodgeEntries.put(CommonPropertyValue.SETTING_DODGE_BLOCK_SITUATIONAL, "Sideline & Chainpush");
		dodgeEntries.put(CommonPropertyValue.SETTING_DODGE_BLOCK_NEVER, "Never");
		entries.put(CommonProperty.SETTING_DODGE_BLOCK, dodgeEntries);
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
