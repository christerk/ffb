package com.fumbbl.ffb.client.ui.menu.game;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.client.Component;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.StyleProvider;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.overlay.sketch.ClientSketchManager;
import com.fumbbl.ffb.client.ui.strategies.click.ClickStrategy;
import com.fumbbl.ffb.client.ui.swing.JMenu;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.ui.swing.JRadioButtonMenuItem;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.model.change.ModelChange;
import com.fumbbl.ffb.model.change.ModelChangeId;
import com.fumbbl.ffb.model.sketch.SketchState;
import com.fumbbl.ffb.util.Scanner;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ReplayMenu extends GameModeMenu {
	private JMenu joinedCoachesMenu;
	private final Set<JMenuItem> transferMenuItems;
	private final Set<JRadioButtonMenuItem> sketchAllowedMenuItems;
	private final Set<JRadioButtonMenuItem> sketchHiddenMenuItems;
	private final Set<JRadioButtonMenuItem> sketchPreventedMenuItems;
	private JRadioButtonMenuItem customSketchCursor;
	private JRadioButtonMenuItem defaultSketchCursor;
	private String currentControllingCoach;
	private final Set<String> hiddenCoaches;
	private final Set<String> preventedCoaches;
	private final ClientSketchManager sketchManager;
	private final List<ClickStrategy> clickStrategies;

	public ReplayMenu(FantasyFootballClient client, DimensionProvider dimensionProvider, ClientCommunication communication,
										StyleProvider styleProvider, LayoutSettings layoutSettings, ClientSketchManager sketchManager) {
		super("Replay", client, dimensionProvider, communication, styleProvider, layoutSettings);
		setMnemonic(KeyEvent.VK_R);
		this.sketchManager = sketchManager;
		transferMenuItems = new HashSet<>();
		sketchAllowedMenuItems = new HashSet<>();
		sketchHiddenMenuItems = new HashSet<>();
		sketchPreventedMenuItems = new HashSet<>();
		hiddenCoaches = new HashSet<>();
		preventedCoaches = new HashSet<>();
		currentControllingCoach = "";

		// Dynamically load all ClickStrategy implementations using Scanner
		Scanner<ClickStrategy> scanner = new Scanner<>(ClickStrategy.class);
		this.clickStrategies = new ArrayList<>(scanner.getSubclassInstances());
		this.clickStrategies.sort(Comparator.comparingInt(ClickStrategy::getOrder));
	}

	@Override
	protected void createSpecificMenuItems() {
		createJoinedCoachesMenu();
		createCursorMenu();
	}

	@Override
	public void subClassRefresh() {
		updateJoinedCoachesMenu();
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
		}
	}

	private void updateJoinedCoachesMenu() {
		String controllingCoach = client.getClientData().getCoachControllingReplay();
		List<String> previousCoaches = transferMenuItems.stream()
			.map(JMenuItem::getName)
			.sorted()
			.collect(Collectors.toList());

		List<String> coaches = new ArrayList<>(client.getClientData().getSpectators());
		if (coaches.equals(previousCoaches) &&
			(!StringTool.isProvided(controllingCoach) || currentControllingCoach.equals(controllingCoach)) &&
			sketchManager.preventedCoaches().equals(preventedCoaches) &&
			sketchManager.hiddenCoaches().equals(hiddenCoaches)) {
			return;
		}

		currentControllingCoach = controllingCoach;
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

		coaches.sort(String::compareTo);

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
	private class ClickStrategyMenuItem extends com.fumbbl.ffb.client.ui.swing.JMenuItem {
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
