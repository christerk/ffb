package com.fumbbl.ffb.client.ui.menu.game;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.Component;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.StyleProvider;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.overlay.sketch.ClientSketchManager;
import com.fumbbl.ffb.client.ui.strategies.click.ClickStrategy;
import com.fumbbl.ffb.client.ui.strategies.click.ClickStrategyRegistry;
import com.fumbbl.ffb.client.ui.swing.JMenu;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.ui.swing.JRadioButtonMenuItem;
import com.fumbbl.ffb.model.change.ModelChange;
import com.fumbbl.ffb.model.change.ModelChangeId;
import com.fumbbl.ffb.model.sketch.SketchState;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReplayMenu extends GameModeMenu {
	private JMenu joinedCoachesMenu;
	private final Set<JMenuItem> transferMenuItems;
	private final Set<JRadioButtonMenuItem> sketchAllowedMenuItems;
	private final Set<JRadioButtonMenuItem> sketchHiddenMenuItems;
	private final Set<JRadioButtonMenuItem> sketchPreventedMenuItems;
	private JRadioButtonMenuItem customSketchCursor;
	private JRadioButtonMenuItem defaultSketchCursor;
	private String currentControllingCoach;
	private final List<String> currentCoaches;
	private final Set<String> hiddenCoaches;
	private final Set<String> preventedCoaches;
	private final ClientSketchManager sketchManager;
	private final ClickStrategyRegistry clickStrategyRegistry;
	private Set<ClickStrategyMenuItem> startSketchItems;
	private Set<ClickStrategyMenuItem> addPointItems;
	private Set<ClickStrategyMenuItem> endSketchItems;

	public ReplayMenu(FantasyFootballClient client, DimensionProvider dimensionProvider, ClientCommunication communication,
										StyleProvider styleProvider, LayoutSettings layoutSettings, ClientSketchManager sketchManager,
										ClickStrategyRegistry clickStrategyRegistry) {
		super("Replay", client, dimensionProvider, communication, styleProvider, layoutSettings);
		setMnemonic(KeyEvent.VK_R);
		this.sketchManager = sketchManager;
		this.clickStrategyRegistry = clickStrategyRegistry;
		transferMenuItems = new HashSet<>();
		sketchAllowedMenuItems = new HashSet<>();
		sketchHiddenMenuItems = new HashSet<>();
		sketchPreventedMenuItems = new HashSet<>();
		hiddenCoaches = new HashSet<>();
		preventedCoaches = new HashSet<>();
		currentControllingCoach = "";
		currentCoaches = new ArrayList<>();
	}

	@Override
	protected void createSpecificMenuItems() {
		createJoinedCoachesMenu();
		createCursorMenu();
		createClickBehaviorMenu();
	}

	private void createClickBehaviorMenu() {
		// Create Click Behavior menu and submenus
		JMenu clickBehaviorMenu = new JMenu(dimensionProvider, "Click Behavior");
		JMenu startSketchMenu = new JMenu(dimensionProvider, "Start Sketch");
		JMenu addPointMenu = new JMenu(dimensionProvider, "Add Point");
		JMenu endSketchMenu = new JMenu(dimensionProvider, "End Sketch");

		startSketchItems = new HashSet<>();
		addPointItems = new HashSet<>();
		endSketchItems = new HashSet<>();

		// Use local ButtonGroups
		ButtonGroup startSketchGroup = new ButtonGroup();
		ButtonGroup addPointGroup = new ButtonGroup();
		ButtonGroup endSketchGroup = new ButtonGroup();

		for (ClickStrategy strategy : clickStrategyRegistry.getStrategies()) {
			ClickStrategyMenuItem startItem = new ClickStrategyMenuItem(dimensionProvider, strategy);
			startItem.addActionListener(this);
			startSketchMenu.add(startItem);
			startSketchItems.add(startItem);
			startSketchGroup.add(startItem);

			ClickStrategyMenuItem addItem = new ClickStrategyMenuItem(dimensionProvider, strategy);
			addItem.addActionListener(this);
			addPointMenu.add(addItem);
			addPointItems.add(addItem);
			addPointGroup.add(addItem);

			ClickStrategyMenuItem endItem = new ClickStrategyMenuItem(dimensionProvider, strategy);
			endItem.addActionListener(this);
			endSketchMenu.add(endItem);
			endSketchItems.add(endItem);
			endSketchGroup.add(endItem);
		}

		clickBehaviorMenu.add(startSketchMenu);
		clickBehaviorMenu.add(addPointMenu);
		clickBehaviorMenu.add(endSketchMenu);
		add(clickBehaviorMenu);
	}

	@Override
	public void subClassRefresh() {
		updateJoinedCoachesMenu();
		refreshClickSettingsMenuItems();
	}

	private void refreshClickSettingsMenuItems() {
		// Get current settings from client properties
		String startSketchKey = client.getProperty(CommonProperty.SETTING_CLICK_START_SKETCH);
		String addPointKey = client.getProperty(CommonProperty.SETTING_CLICK_ADD_POINT);
		String endSketchKey = client.getProperty(CommonProperty.SETTING_CLICK_END_SKETCH);

		if (startSketchKey == null) {
			startSketchKey = IClientPropertyValue.SETTING_CLICK_LEFT;
			client.setProperty(CommonProperty.SETTING_CLICK_START_SKETCH, startSketchKey);
		}
		if (addPointKey == null) {
			addPointKey = IClientPropertyValue.SETTING_CLICK_LEFT;
			client.setProperty(CommonProperty.SETTING_CLICK_ADD_POINT, addPointKey);
		}
		if (endSketchKey == null) {
			endSketchKey = IClientPropertyValue.SETTING_CLICK_DOUBLE;
			client.setProperty(CommonProperty.SETTING_CLICK_END_SKETCH, endSketchKey);
		}

		// For startSketchItems, all are enabled
		for (ClickStrategyMenuItem item : startSketchItems) {
			item.setEnabled(true);
			item.setSelected(item.getClickStrategy().getKey().equals(startSketchKey));
		}

		// For addPointItems, disable the one selected for end
		for (ClickStrategyMenuItem item : addPointItems) {
			boolean isSelected = item.getClickStrategy().getKey().equals(addPointKey);
			boolean isDisabled = item.getClickStrategy().getKey().equals(endSketchKey);
			item.setEnabled(!isDisabled);
			item.setSelected(isSelected);
		}

		// For endSketchItems, disable the one selected for addPoint
		for (ClickStrategyMenuItem item : endSketchItems) {
			boolean isSelected = item.getClickStrategy().getKey().equals(endSketchKey);
			boolean isDisabled = item.getClickStrategy().getKey().equals(addPointKey);
			item.setEnabled(!isDisabled);
			item.setSelected(isSelected);
		}
	}

	@SuppressWarnings("SuspiciousMethodCalls")
	@Override
	public void subClassPerform(ActionEvent event) {
		Object source = event.getSource();

		if (transferMenuItems.contains(source)) {
			JMenuItem item = (JMenuItem) source;
			client.getCommunication().sendTransferReplayControl(item.getName());
		} else if (sketchAllowedMenuItems.contains(source)) {
			JRadioButtonMenuItem item = (JRadioButtonMenuItem) source;
			String coach = item.getName();
			if (sketchManager.isCoachPreventedFromSketching(coach)) {
				client.getCommunication().sendPreventFromSketching(coach, false);
			} else {
				sketchManager.showSketches(coach);
				SketchState sketchState = new SketchState(sketchManager.getAllSketches());
				ModelChange modelChange = new ModelChange(ModelChangeId.SKETCH_UPDATE, null, sketchState);
				client.getGame().notifyObservers(modelChange);
				this.updateJoinedCoachesMenu();
			}
		} else if (sketchHiddenMenuItems.contains(source)) {
			JRadioButtonMenuItem item = (JRadioButtonMenuItem) source;
			String coach = item.getName();
			sketchManager.hideSketches(coach);
			SketchState sketchState = new SketchState(sketchManager.getAllSketches());
			ModelChange modelChange = new ModelChange(ModelChangeId.SKETCH_UPDATE, null, sketchState);
			client.getGame().notifyObservers(modelChange);
			this.updateJoinedCoachesMenu();
		} else if (sketchPreventedMenuItems.contains(source)) {
			JRadioButtonMenuItem item = (JRadioButtonMenuItem) source;
			String coach = item.getName();
			if (!sketchManager.isCoachPreventedFromSketching(coach)) {
				client.getCommunication().sendPreventFromSketching(coach, true);
			}
		} else if (source == customSketchCursor) {
			client.setProperty(CommonProperty.SETTING_SKETCH_CURSOR, IClientPropertyValue.SETTING_SKETCH_CURSOR_ON);
			client.saveUserSettings(true);
		} else if (source == defaultSketchCursor) {
			client.setProperty(CommonProperty.SETTING_SKETCH_CURSOR, IClientPropertyValue.SETTING_SKETCH_CURSOR_OFF);
			client.saveUserSettings(true);
		} else if (startSketchItems.contains(source)) {
			ClickStrategyMenuItem item = (ClickStrategyMenuItem) source;
			client.setProperty(CommonProperty.SETTING_CLICK_START_SKETCH, item.getClickStrategy().getKey());
			client.saveUserSettings(false);
		} else if (addPointItems.contains(source)) {
			ClickStrategyMenuItem item = (ClickStrategyMenuItem) source;
			client.setProperty(CommonProperty.SETTING_CLICK_ADD_POINT, item.getClickStrategy().getKey());
			client.saveUserSettings(false);
		} else if (endSketchItems.contains(source)) {
			ClickStrategyMenuItem item = (ClickStrategyMenuItem) source;
			client.setProperty(CommonProperty.SETTING_CLICK_END_SKETCH, item.getClickStrategy().getKey());
			client.saveUserSettings(false);
		}
	}

	private void updateJoinedCoachesMenu() {
		String controllingCoach = client.getClientData().getCoachControllingReplay();

		List<String> coaches = new ArrayList<>(client.getClientData().getSpectators());
		coaches.sort(String::compareTo);

		if (coaches.equals(currentCoaches) &&
			(!StringTool.isProvided(controllingCoach) || currentControllingCoach.equals(controllingCoach)) &&
			sketchManager.preventedCoaches().equals(preventedCoaches) &&
			sketchManager.hiddenCoaches().equals(hiddenCoaches)) {
			return;
		}

		currentControllingCoach = controllingCoach;
		currentCoaches.clear();
		currentCoaches.addAll(coaches);
		joinedCoachesMenu.removeAll();
		transferMenuItems.clear();
		sketchAllowedMenuItems.clear();
		sketchHiddenMenuItems.clear();
		sketchPreventedMenuItems.clear();
		preventedCoaches.clear();
		preventedCoaches.addAll(sketchManager.preventedCoaches());
		hiddenCoaches.clear();
		hiddenCoaches.addAll(sketchManager.hiddenCoaches());

		String clientCoach = client.getParameters().getCoach();
		boolean clientHasControl = clientCoach.equals(controllingCoach);

		Dimension dimension = dimensionProvider.unscaledDimension(Component.MENU_IMAGE_ICON);
		ImageIcon ballIcon = loadIcon(dimension, IIconProperty.GAME_BALL);
		ImageIcon allowedIcon = loadIcon(dimension, IIconProperty.MENU_SKETCH_ALLOWED);
		ImageIcon hiddenIcon = loadIcon(dimension, IIconProperty.MENU_SKETCH_HIDDEN);
		ImageIcon preventedIcon = loadIcon(dimension, IIconProperty.MENU_SKETCH_PREVENTED);

		for (String coach : coaches) {
			boolean joinedCoachHasControl = coach.equals(controllingCoach);
			ImageIcon icon = determineCoachIcon(coach, joinedCoachHasControl, ballIcon,
				hiddenIcon, preventedIcon, allowedIcon);

			createCoachMenu(coach, icon, clientHasControl);
		}

		joinedCoachesMenu.addSeparator();
		ImageIcon icon = determineCoachIcon(clientCoach, clientHasControl, ballIcon,
			hiddenIcon, preventedIcon, allowedIcon);
		JMenuItem joinedSelf = new JMenuItem(dimensionProvider, clientCoach, icon);
		joinedCoachesMenu.add(joinedSelf);
	}

	private void createJoinedCoachesMenu() {
		joinedCoachesMenu = new JMenu(dimensionProvider, "Joined Coaches");
		joinedCoachesMenu.setMnemonic(KeyEvent.VK_J);
		add(joinedCoachesMenu);
	}

	private void createCursorMenu() {
		JMenu cursorMenu = new JMenu(dimensionProvider, "Cursor");
		ButtonGroup cursorGroup = new ButtonGroup();

		customSketchCursor = new JRadioButtonMenuItem(dimensionProvider, "Pen");
		customSketchCursor.addActionListener(this);
		cursorMenu.add(customSketchCursor);
		cursorGroup.add(customSketchCursor);

		defaultSketchCursor = new JRadioButtonMenuItem(dimensionProvider, "System Default");
		defaultSketchCursor.addActionListener(this);
		cursorMenu.add(defaultSketchCursor);
		cursorGroup.add(defaultSketchCursor);

		add(cursorMenu);
	}

	private void createCoachMenu(String coach, ImageIcon icon, boolean clientHasControl) {
		JMenu coachMenu = new JMenu(dimensionProvider, coach, icon);
		ButtonGroup group = new ButtonGroup();

		if (clientHasControl) {
			JMenuItem transferItem = new JMenuItem(dimensionProvider, "Transfer Control");
			coachMenu.add(transferItem);
			transferItem.setName(coach);
			group.add(transferItem);
			transferMenuItems.add(transferItem);
			transferItem.addActionListener(this);
		}

		createSketchingSubmenu(coachMenu, coach, group, clientHasControl);
		joinedCoachesMenu.add(coachMenu);
	}

	private void createSketchingSubmenu(JMenu coachMenu, String coach, ButtonGroup group, boolean clientHasControl) {
		JMenu sketchMenu = new JMenu(dimensionProvider, "Sketching");
		coachMenu.add(sketchMenu);
		group.add(sketchMenu);

		ButtonGroup sketchGroup = new ButtonGroup();
		addSketchingMenuItems(sketchMenu, sketchGroup, coach, clientHasControl);
	}

	private void addSketchingMenuItems(JMenu sketchMenu, ButtonGroup sketchGroup, String coach, boolean clientHasControl) {
		String allowLabel = determineAllowLabel(coach);
		addSketchMenuItem(sketchMenu, sketchGroup, allowLabel, coach, sketchAllowedMenuItems,
			!sketchManager.displaySketches(coach) &&
				(!sketchManager.isCoachPreventedFromSketching(coach) || clientHasControl),
			sketchManager.displaySketches(coach));

		addSketchMenuItem(sketchMenu, sketchGroup,
			sketchManager.areSketchesHidden(coach) ? "Hidden" : "Hide",
			coach, sketchHiddenMenuItems,
			!sketchManager.areSketchesHidden(coach) &&
				!sketchManager.isCoachPreventedFromSketching(coach),
			sketchManager.areSketchesHidden(coach));

		addSketchMenuItem(sketchMenu, sketchGroup,
			sketchManager.isCoachPreventedFromSketching(coach) ? "Blocked" : "Block",
			coach, sketchPreventedMenuItems,
			clientHasControl && !sketchManager.isCoachPreventedFromSketching(coach),
			sketchManager.isCoachPreventedFromSketching(coach));
	}

	private String determineAllowLabel(String coach) {
		if (sketchManager.isCoachPreventedFromSketching(coach)) {
			return "Unblock";
		}
		if (sketchManager.areSketchesHidden(coach)) {
			return "Show";
		}
		return "Showing";
	}

	private void addSketchMenuItem(JMenu menu, ButtonGroup group, String label, String coach,
																 Set<JRadioButtonMenuItem> items, boolean enabled, boolean selected) {
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(dimensionProvider, label);
		menu.add(item);
		group.add(item);
		item.setEnabled(enabled);
		item.setSelected(selected);
		item.setName(coach);
		item.addActionListener(this);
		items.add(item);
	}

	private ImageIcon loadIcon(Dimension dimension, String iconProperty) {
		if (client.getUserInterface() == null || client.getUserInterface().getIconCache() == null) {
			return null;
		}
		Image image = client.getUserInterface().getIconCache()
			.getIconByProperty(iconProperty, dimensionProvider)
			.getScaledInstance(dimension.width, dimension.height, 0);
		return new ImageIcon(image);
	}

	private ImageIcon determineCoachIcon(String coach, boolean hasControl, ImageIcon ballIcon,
																			 ImageIcon hiddenIcon, ImageIcon preventedIcon, ImageIcon allowedIcon) {
		if (hasControl) {
			return ballIcon;
		}
		if (sketchManager.isCoachPreventedFromSketching(coach)) {
			return preventedIcon;
		}
		if (sketchManager.areSketchesHidden(coach)) {
			return hiddenIcon;
		}
		return allowedIcon;
	}

	// Inner class for menu item using ClickStrategy
	private static class ClickStrategyMenuItem extends JRadioButtonMenuItem {
		private final ClickStrategy strategy;

		public ClickStrategyMenuItem(DimensionProvider dimensionProvider, ClickStrategy strategy) {
			super(dimensionProvider, strategy.getMenuLabel());
			this.strategy = strategy;
		}

		public ClickStrategy getClickStrategy() {
			return strategy;
		}
	}
}
