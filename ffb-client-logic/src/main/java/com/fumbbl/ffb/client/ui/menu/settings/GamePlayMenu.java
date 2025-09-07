package com.fumbbl.ffb.client.ui.menu.settings;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.StyleProvider;
import com.fumbbl.ffb.client.ui.menu.FfbMenu;
import com.fumbbl.ffb.client.ui.swing.JMenu;
import com.fumbbl.ffb.client.ui.swing.JRadioButtonMenuItem;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;

import javax.swing.ButtonGroup;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.fumbbl.ffb.CommonProperty.SETTING_AUTOMOVE;
import static com.fumbbl.ffb.CommonProperty.SETTING_BLITZ_TARGET_PANEL;
import static com.fumbbl.ffb.CommonProperty.SETTING_GAZE_TARGET_PANEL;
import static com.fumbbl.ffb.CommonProperty.SETTING_MARK_USED_PLAYERS;
import static com.fumbbl.ffb.CommonProperty.SETTING_RANGEGRID;
import static com.fumbbl.ffb.CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN;
import static com.fumbbl.ffb.CommonProperty.SETTING_RIGHT_CLICK_END_ACTION;

public class GamePlayMenu extends FfbMenu {

	private JRadioButtonMenuItem fAutomoveOnMenuItem;
	private JRadioButtonMenuItem fAutomoveOffMenuItem;

	private JRadioButtonMenuItem fBlitzPanelOnMenuItem;
	private JRadioButtonMenuItem fBlitzPanelOffMenuItem;

	private JRadioButtonMenuItem gazePanelOnMenuItem;
	private JRadioButtonMenuItem gazePanelOffMenuItem;

	private JRadioButtonMenuItem rightClickEndActionOnMenuItem;
	private JRadioButtonMenuItem rightClickOpensContextMenuItem;
	private JRadioButtonMenuItem rightClickLegacyModeItem;
	private JRadioButtonMenuItem rightClickEndActionOffMenuItem;

	private JRadioButtonMenuItem fRangeGridAlwaysOnMenuItem;
	private JRadioButtonMenuItem fRangeGridToggleMenuItem;

	private JRadioButtonMenuItem markUsedPlayersDefaultMenuItem;
	private JRadioButtonMenuItem markUsedPlayersCheckIconGreenMenuItem;

	private JRadioButtonMenuItem reRollBallAndChainNeverMenuItem;
	private JRadioButtonMenuItem reRollBallAndChainNoOpponentMenuItem;
	private JRadioButtonMenuItem reRollBallAndChainTeamMateMenuItem;
	private JRadioButtonMenuItem reRollBallAndChainAlwaysMenuItem;

	private JMenu reRollBallAndChainPanelMenu;

	// Player Mode
	private JRadioButtonMenuItem tzPlayerNoneMenuItem;
	private JRadioButtonMenuItem tzPlayerHomeMenuItem;
	private JRadioButtonMenuItem tzPlayerAwayMenuItem;
	private JRadioButtonMenuItem tzPlayerBothMenuItem;
	private JRadioButtonMenuItem tzPlayerPassiveMenuItem;
	private JRadioButtonMenuItem tzPlayerPassiveBothOnSetupMenuItem;

	// Spectator Mode
	private JRadioButtonMenuItem tzSpectatorNoneMenuItem;
	private JRadioButtonMenuItem tzSpectatorHomeMenuItem;
	private JRadioButtonMenuItem tzSpectatorAwayMenuItem;
	private JRadioButtonMenuItem tzSpectatorBothMenuItem;
	private JRadioButtonMenuItem tzSpectatorPassiveMenuItem;
	private JRadioButtonMenuItem tzSpectatorPassiveBothOnSetupMenuItem;

	// No Overlap (global)
	private JRadioButtonMenuItem tzOverlapOffMenuItem;
	private JRadioButtonMenuItem tzOverlapOnMenuItem;

	// Contour (global)
	private JRadioButtonMenuItem tzContourOffMenuItem;
	private JRadioButtonMenuItem tzContourOnMenuItem;

	private Map<CommonProperty, JMenu> exposedMenus;

	protected GamePlayMenu(FantasyFootballClient client, DimensionProvider dimensionProvider, StyleProvider styleProvider, LayoutSettings layoutSettings) {
		super("Game Play", client, dimensionProvider, styleProvider, layoutSettings);
		setMnemonic(KeyEvent.VK_G);
	}

	@Override
	protected void init() {
		exposedMenus = new HashMap<>();

		createAutomoveMenu();
		createBlitzPanelMenu();
		createGazePanelMenu();
		createRightClickMenu();
		createBallAndChainMenu();
		createRangegridMenu();
		createMarkUsedPlayerMenu();
		createTacklezonesMenu();
	}

	@Override
	public boolean refresh() {
		String automoveSetting = client.getProperty(CommonProperty.SETTING_AUTOMOVE);
		fAutomoveOnMenuItem.setSelected(true);
		fAutomoveOffMenuItem.setSelected(IClientPropertyValue.SETTING_AUTOMOVE_OFF.equals(automoveSetting));

		String blitzTargetPanelSetting = client.getProperty(CommonProperty.SETTING_BLITZ_TARGET_PANEL);
		fBlitzPanelOnMenuItem.setSelected(true);
		fBlitzPanelOffMenuItem.setSelected(IClientPropertyValue.SETTING_BLITZ_TARGET_PANEL_OFF.equals(blitzTargetPanelSetting));

		String gazeTargetPanelSetting = client.getProperty(CommonProperty.SETTING_GAZE_TARGET_PANEL);
		gazePanelOnMenuItem.setSelected(true);
		gazePanelOffMenuItem.setSelected(IClientPropertyValue.SETTING_GAZE_TARGET_PANEL_OFF.equals(gazeTargetPanelSetting));

		String rightClickEndActionSetting = client.getProperty(CommonProperty.SETTING_RIGHT_CLICK_END_ACTION);
		rightClickEndActionOffMenuItem.setSelected(true);
		rightClickEndActionOnMenuItem.setSelected(IClientPropertyValue.SETTING_RIGHT_CLICK_END_ACTION_ON.equals(rightClickEndActionSetting));
		rightClickLegacyModeItem.setSelected(IClientPropertyValue.SETTING_RIGHT_CLICK_LEGACY_MODE.equals(rightClickEndActionSetting));
		rightClickOpensContextMenuItem.setSelected(IClientPropertyValue.SETTING_RIGHT_CLICK_OPENS_CONTEXT_MENU.equals(rightClickEndActionSetting));

		String reRollBallAndChainSetting = client.getProperty(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN);
		reRollBallAndChainAlwaysMenuItem.setSelected(true);
		reRollBallAndChainTeamMateMenuItem.setSelected(IClientPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_TEAM_MATE.equals(reRollBallAndChainSetting));
		reRollBallAndChainNoOpponentMenuItem.setSelected(IClientPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_NO_OPPONENT.equals(reRollBallAndChainSetting));
		reRollBallAndChainNeverMenuItem.setSelected(IClientPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_NEVER.equals(reRollBallAndChainSetting));

		String rangeGridSetting = client.getProperty(CommonProperty.SETTING_RANGEGRID);
		fRangeGridToggleMenuItem.setSelected(true);
		fRangeGridAlwaysOnMenuItem.setSelected(IClientPropertyValue.SETTING_RANGEGRID_ALWAYS_ON.equals(rangeGridSetting));

		String markUsedPlayersSetting = client.getProperty(CommonProperty.SETTING_MARK_USED_PLAYERS);
		markUsedPlayersDefaultMenuItem.setSelected(true);
		markUsedPlayersCheckIconGreenMenuItem.setSelected(IClientPropertyValue.SETTING_MARK_USED_PLAYERS_CHECK_ICON_GREEN.equals(markUsedPlayersSetting));

		boolean askForReRoll = ((GameOptionBoolean) client.getGame().getOptions().getOptionWithDefault(GameOptionId.ALLOW_BALL_AND_CHAIN_RE_ROLL)).isEnabled();
		reRollBallAndChainPanelMenu.setText(askForReRoll ? "Ask to Re-Roll Ball & Chain Movement" : "Ask for Whirling Dervish");


		// Tacklezone player mode
		String tzPlayerSetting = client.getProperty(CommonProperty.SETTING_TACKLEZONES_PLAYER_MODE);
		tzPlayerNoneMenuItem.setSelected(true);
		tzPlayerHomeMenuItem.setSelected(IClientPropertyValue.SETTING_TACKLEZONES_HOME.equals(tzPlayerSetting));
		tzPlayerAwayMenuItem.setSelected(IClientPropertyValue.SETTING_TACKLEZONES_AWAY.equals(tzPlayerSetting));
		tzPlayerBothMenuItem.setSelected(IClientPropertyValue.SETTING_TACKLEZONES_BOTH.equals(tzPlayerSetting));
		tzPlayerPassiveMenuItem.setSelected(IClientPropertyValue.SETTING_TACKLEZONES_PASSIVE.equals(tzPlayerSetting));
		tzPlayerPassiveBothOnSetupMenuItem.setSelected(IClientPropertyValue.SETTING_TACKLEZONES_PASSIVE_BOTH_ON_SETUP.equals(tzPlayerSetting));

		// Tacklezone spec mode
		String tzSpectatorSetting = client.getProperty(CommonProperty.SETTING_TACKLEZONES_SPECTATOR_MODE);
		tzSpectatorNoneMenuItem.setSelected(true);
		tzSpectatorHomeMenuItem.setSelected(IClientPropertyValue.SETTING_TACKLEZONES_HOME.equals(tzSpectatorSetting));
		tzSpectatorAwayMenuItem.setSelected(IClientPropertyValue.SETTING_TACKLEZONES_AWAY.equals(tzSpectatorSetting));
		tzSpectatorBothMenuItem.setSelected(IClientPropertyValue.SETTING_TACKLEZONES_BOTH.equals(tzSpectatorSetting));
		tzSpectatorPassiveMenuItem.setSelected(IClientPropertyValue.SETTING_TACKLEZONES_PASSIVE.equals(tzSpectatorSetting));
		tzSpectatorPassiveBothOnSetupMenuItem.setSelected(IClientPropertyValue.SETTING_TACKLEZONES_PASSIVE_BOTH_ON_SETUP.equals(tzSpectatorSetting));

		// Tacklezone Overlap
		String tzOverlapSetting = client.getProperty(CommonProperty.SETTING_TACKLEZONES_OVERLAP);
		tzOverlapOffMenuItem.setSelected(true);
		tzOverlapOnMenuItem.setSelected(IClientPropertyValue.SETTING_TACKLEZONES_OVERLAP_ON.equals(tzOverlapSetting));

		// Tacklezone Contour
		String tzContourSetting = client.getProperty(CommonProperty.SETTING_TACKLEZONES_CONTOUR);
		tzContourOffMenuItem.setSelected(true);
		tzContourOnMenuItem.setSelected(IClientPropertyValue.SETTING_TACKLEZONES_CONTOUR_ON.equals(tzContourSetting));

		return false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		javax.swing.JMenuItem source = (javax.swing.JMenuItem) (e.getSource());

		if (source == fAutomoveOffMenuItem) {
			client.setProperty(CommonProperty.SETTING_AUTOMOVE, IClientPropertyValue.SETTING_AUTOMOVE_OFF);
			client.saveUserSettings(false);
		}
		if (source == fAutomoveOnMenuItem) {
			client.setProperty(CommonProperty.SETTING_AUTOMOVE, IClientPropertyValue.SETTING_AUTOMOVE_ON);
			client.saveUserSettings(false);
		}
		if (source == fBlitzPanelOffMenuItem) {
			client.setProperty(CommonProperty.SETTING_BLITZ_TARGET_PANEL, IClientPropertyValue.SETTING_BLITZ_TARGET_PANEL_OFF);
			client.saveUserSettings(false);
		}
		if (source == fBlitzPanelOnMenuItem) {
			client.setProperty(CommonProperty.SETTING_BLITZ_TARGET_PANEL, IClientPropertyValue.SETTING_BLITZ_TARGET_PANEL_ON);
			client.saveUserSettings(false);
		}
		if (source == gazePanelOnMenuItem) {
			client.setProperty(CommonProperty.SETTING_GAZE_TARGET_PANEL, IClientPropertyValue.SETTING_GAZE_TARGET_PANEL_ON);
			client.saveUserSettings(false);
		}
		if (source == gazePanelOffMenuItem) {
			client.setProperty(CommonProperty.SETTING_GAZE_TARGET_PANEL, IClientPropertyValue.SETTING_GAZE_TARGET_PANEL_OFF);
			client.saveUserSettings(false);
		}
		if (source == rightClickEndActionOnMenuItem) {
			client.setProperty(CommonProperty.SETTING_RIGHT_CLICK_END_ACTION, IClientPropertyValue.SETTING_RIGHT_CLICK_END_ACTION_ON);
			client.saveUserSettings(false);
		}
		if (source == rightClickLegacyModeItem) {
			client.setProperty(CommonProperty.SETTING_RIGHT_CLICK_END_ACTION, IClientPropertyValue.SETTING_RIGHT_CLICK_LEGACY_MODE);
			client.saveUserSettings(false);
		}
		if (source == rightClickOpensContextMenuItem) {
			client.setProperty(CommonProperty.SETTING_RIGHT_CLICK_END_ACTION, IClientPropertyValue.SETTING_RIGHT_CLICK_OPENS_CONTEXT_MENU);
			client.saveUserSettings(false);
		}
		if (source == rightClickEndActionOffMenuItem) {
			client.setProperty(CommonProperty.SETTING_RIGHT_CLICK_END_ACTION, IClientPropertyValue.SETTING_RIGHT_CLICK_END_ACTION_OFF);
			client.saveUserSettings(false);
		}
		if (source == reRollBallAndChainAlwaysMenuItem) {
			client.setProperty(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN, IClientPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_ALWAYS);
			client.saveUserSettings(false);
		}
		if (source == reRollBallAndChainTeamMateMenuItem) {
			client.setProperty(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN, IClientPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_TEAM_MATE);
			client.saveUserSettings(false);
		}
		if (source == reRollBallAndChainNoOpponentMenuItem) {
			client.setProperty(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN, IClientPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_NO_OPPONENT);
			client.saveUserSettings(false);
		}
		if (source == reRollBallAndChainNeverMenuItem) {
			client.setProperty(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN, IClientPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_NEVER);
			client.saveUserSettings(false);
		}

		if (source == fRangeGridAlwaysOnMenuItem) {
			client.setProperty(CommonProperty.SETTING_RANGEGRID, IClientPropertyValue.SETTING_RANGEGRID_ALWAYS_ON);
			client.saveUserSettings(false);
		}

		if (source == fRangeGridToggleMenuItem) {
			client.setProperty(CommonProperty.SETTING_RANGEGRID, IClientPropertyValue.SETTING_RANGEGRID_TOGGLE);
			client.saveUserSettings(false);
		}

		if (source == markUsedPlayersCheckIconGreenMenuItem) {
			client.setProperty(CommonProperty.SETTING_MARK_USED_PLAYERS, IClientPropertyValue.SETTING_MARK_USED_PLAYERS_CHECK_ICON_GREEN);
			client.saveUserSettings(true);
		}

		if (source == markUsedPlayersDefaultMenuItem) {
			client.setProperty(CommonProperty.SETTING_MARK_USED_PLAYERS, IClientPropertyValue.SETTING_MARK_USED_PLAYERS_DEFAULT);
			client.saveUserSettings(true);
		}

		// Tacklezones Player Mode Menu Item Handlers
		if (source == tzPlayerNoneMenuItem) {
			client.setProperty(CommonProperty.SETTING_TACKLEZONES_PLAYER_MODE, IClientPropertyValue.SETTING_TACKLEZONES_NONE);
			client.saveUserSettings(true);
		}
		if (source == tzPlayerHomeMenuItem) {
			client.setProperty(CommonProperty.SETTING_TACKLEZONES_PLAYER_MODE, IClientPropertyValue.SETTING_TACKLEZONES_HOME);
			client.saveUserSettings(true);
		}
		if (source == tzPlayerAwayMenuItem) {
			client.setProperty(CommonProperty.SETTING_TACKLEZONES_PLAYER_MODE, IClientPropertyValue.SETTING_TACKLEZONES_AWAY);
			client.saveUserSettings(true);
		}
		if (source == tzPlayerBothMenuItem) {
			client.setProperty(CommonProperty.SETTING_TACKLEZONES_PLAYER_MODE, IClientPropertyValue.SETTING_TACKLEZONES_BOTH);
			client.saveUserSettings(true);
		}
		if (source == tzPlayerPassiveMenuItem) {
			client.setProperty(CommonProperty.SETTING_TACKLEZONES_PLAYER_MODE, IClientPropertyValue.SETTING_TACKLEZONES_PASSIVE);
			client.saveUserSettings(true);
		}
		if (source == tzPlayerPassiveBothOnSetupMenuItem) {
			client.setProperty(CommonProperty.SETTING_TACKLEZONES_PLAYER_MODE, IClientPropertyValue.SETTING_TACKLEZONES_PASSIVE_BOTH_ON_SETUP);
			client.saveUserSettings(true);
		}

		// Tacklezones Spectator Mode Menu Item Handlers
		if (source == tzSpectatorNoneMenuItem) {
			client.setProperty(CommonProperty.SETTING_TACKLEZONES_SPECTATOR_MODE, IClientPropertyValue.SETTING_TACKLEZONES_NONE);
			client.saveUserSettings(true);
		}
		if (source == tzSpectatorHomeMenuItem) {
			client.setProperty(CommonProperty.SETTING_TACKLEZONES_SPECTATOR_MODE, IClientPropertyValue.SETTING_TACKLEZONES_HOME);
			client.saveUserSettings(true);
		}
		if (source == tzSpectatorAwayMenuItem) {
			client.setProperty(CommonProperty.SETTING_TACKLEZONES_SPECTATOR_MODE, IClientPropertyValue.SETTING_TACKLEZONES_AWAY);
			client.saveUserSettings(true);
		}
		if (source == tzSpectatorBothMenuItem) {
			client.setProperty(CommonProperty.SETTING_TACKLEZONES_SPECTATOR_MODE, IClientPropertyValue.SETTING_TACKLEZONES_BOTH);
			client.saveUserSettings(true);
		}
		if (source == tzSpectatorPassiveMenuItem) {
			client.setProperty(CommonProperty.SETTING_TACKLEZONES_SPECTATOR_MODE, IClientPropertyValue.SETTING_TACKLEZONES_PASSIVE);
			client.saveUserSettings(true);
		}
		if (source == tzSpectatorPassiveBothOnSetupMenuItem) {
			client.setProperty(CommonProperty.SETTING_TACKLEZONES_SPECTATOR_MODE, IClientPropertyValue.SETTING_TACKLEZONES_PASSIVE_BOTH_ON_SETUP);
			client.saveUserSettings(true);
		}

		if (source == tzOverlapOnMenuItem) {
			client.setProperty(CommonProperty.SETTING_TACKLEZONES_OVERLAP, IClientPropertyValue.SETTING_TACKLEZONES_OVERLAP_ON);
			client.saveUserSettings(true);
		}

		if (source == tzOverlapOffMenuItem) {
			client.setProperty(CommonProperty.SETTING_TACKLEZONES_OVERLAP, IClientPropertyValue.SETTING_TACKLEZONES_OVERLAP_OFF);
			client.saveUserSettings(true);
		}

		if (source == tzContourOnMenuItem) {
			client.setProperty(CommonProperty.SETTING_TACKLEZONES_CONTOUR, IClientPropertyValue.SETTING_TACKLEZONES_CONTOUR_ON);
			client.saveUserSettings(true);
		}

		if (source == tzContourOffMenuItem) {
			client.setProperty(CommonProperty.SETTING_TACKLEZONES_CONTOUR, IClientPropertyValue.SETTING_TACKLEZONES_CONTOUR_OFF);
			client.saveUserSettings(true);
		}
	}

	private void createMarkUsedPlayerMenu() {
		JMenu markUsedPlayersMenu = new JMenu(dimensionProvider, SETTING_MARK_USED_PLAYERS);
		markUsedPlayersMenu.setMnemonic(KeyEvent.VK_M);
		add(markUsedPlayersMenu);

		ButtonGroup markUsedPlayersGroup = new ButtonGroup();

		markUsedPlayersDefaultMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Fade only");
		markUsedPlayersDefaultMenuItem.addActionListener(this);
		markUsedPlayersGroup.add(markUsedPlayersDefaultMenuItem);
		markUsedPlayersMenu.add(markUsedPlayersDefaultMenuItem);

		markUsedPlayersCheckIconGreenMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Green check mark");
		markUsedPlayersCheckIconGreenMenuItem.addActionListener(this);
		markUsedPlayersGroup.add(markUsedPlayersCheckIconGreenMenuItem);
		markUsedPlayersMenu.add(markUsedPlayersCheckIconGreenMenuItem);
	}

	private void createRangegridMenu() {
		JMenu fRangeGridMenu = new JMenu(dimensionProvider, SETTING_RANGEGRID);
		fRangeGridMenu.setMnemonic(KeyEvent.VK_R);
		add(fRangeGridMenu);

		ButtonGroup rangeGridGroup = new ButtonGroup();

		fRangeGridAlwaysOnMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Range Grid always on");
		fRangeGridAlwaysOnMenuItem.addActionListener(this);
		rangeGridGroup.add(fRangeGridAlwaysOnMenuItem);
		fRangeGridMenu.add(fRangeGridAlwaysOnMenuItem);

		fRangeGridToggleMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Range Grid toggle");
		fRangeGridToggleMenuItem.addActionListener(this);
		rangeGridGroup.add(fRangeGridToggleMenuItem);
		fRangeGridMenu.add(fRangeGridToggleMenuItem);
	}

	private void createBallAndChainMenu() {
		ButtonGroup reRollBallAndChainPanelGroup = new ButtonGroup();
		reRollBallAndChainPanelMenu = new JMenu(dimensionProvider, SETTING_RE_ROLL_BALL_AND_CHAIN);
		exposedMenus.put(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN, reRollBallAndChainPanelMenu);
		reRollBallAndChainPanelMenu.setMnemonic(KeyEvent.VK_B);
		add(reRollBallAndChainPanelMenu);

		reRollBallAndChainAlwaysMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Always");
		reRollBallAndChainAlwaysMenuItem.setName(IClientPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_ALWAYS);
		reRollBallAndChainAlwaysMenuItem.addActionListener(this);
		reRollBallAndChainPanelGroup.add(reRollBallAndChainAlwaysMenuItem);
		reRollBallAndChainPanelMenu.add(reRollBallAndChainAlwaysMenuItem);

		reRollBallAndChainNoOpponentMenuItem = new JRadioButtonMenuItem(dimensionProvider, "When not hitting an opponent");
		reRollBallAndChainNoOpponentMenuItem.setName(IClientPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_NO_OPPONENT);
		reRollBallAndChainNoOpponentMenuItem.addActionListener(this);
		reRollBallAndChainPanelGroup.add(reRollBallAndChainNoOpponentMenuItem);
		reRollBallAndChainPanelMenu.add(reRollBallAndChainNoOpponentMenuItem);

		reRollBallAndChainTeamMateMenuItem = new JRadioButtonMenuItem(dimensionProvider, "When hitting Team-mate");
		reRollBallAndChainTeamMateMenuItem.setName(IClientPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_TEAM_MATE);
		reRollBallAndChainTeamMateMenuItem.addActionListener(this);
		reRollBallAndChainPanelGroup.add(reRollBallAndChainTeamMateMenuItem);
		reRollBallAndChainPanelMenu.add(reRollBallAndChainTeamMateMenuItem);

		reRollBallAndChainNeverMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Never");
		reRollBallAndChainNeverMenuItem.setName(IClientPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_NEVER);
		reRollBallAndChainNeverMenuItem.addActionListener(this);
		reRollBallAndChainPanelGroup.add(reRollBallAndChainNeverMenuItem);
		reRollBallAndChainPanelMenu.add(reRollBallAndChainNeverMenuItem);
	}

	private void createRightClickMenu() {
		ButtonGroup rightClickEndActionPanelGroup = new ButtonGroup();
		JMenu rightClickEndActionPanelMenu = new JMenu(dimensionProvider, SETTING_RIGHT_CLICK_END_ACTION);
		rightClickEndActionPanelMenu.setMnemonic(KeyEvent.VK_R);
		add(rightClickEndActionPanelMenu);

		rightClickEndActionOnMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Ends Action/Selection");
		rightClickEndActionOnMenuItem.addActionListener(this);
		rightClickEndActionPanelGroup.add(rightClickEndActionOnMenuItem);
		rightClickEndActionPanelMenu.add(rightClickEndActionOnMenuItem);

		rightClickLegacyModeItem = new JRadioButtonMenuItem(dimensionProvider, "Works like Left Click (Legacy)");
		rightClickLegacyModeItem.addActionListener(this);
		rightClickEndActionPanelGroup.add(rightClickLegacyModeItem);
		rightClickEndActionPanelMenu.add(rightClickLegacyModeItem);

		rightClickOpensContextMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Selects Player/Opens Context Menu");
		rightClickOpensContextMenuItem.addActionListener(this);
		rightClickEndActionPanelGroup.add(rightClickOpensContextMenuItem);
		rightClickEndActionPanelMenu.add(rightClickOpensContextMenuItem);

		rightClickEndActionOffMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Disabled");
		rightClickEndActionOffMenuItem.addActionListener(this);
		rightClickEndActionPanelGroup.add(rightClickEndActionOffMenuItem);
		rightClickEndActionPanelMenu.add(rightClickEndActionOffMenuItem);
	}

	private void createGazePanelMenu() {
		ButtonGroup gazeTargetPanelGroup = new ButtonGroup();
		JMenu gazeTargetPanelMenu = new JMenu(dimensionProvider, SETTING_GAZE_TARGET_PANEL);
		gazeTargetPanelMenu.setMnemonic(KeyEvent.VK_G);
		add(gazeTargetPanelMenu);

		gazePanelOnMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Enable");
		gazePanelOnMenuItem.addActionListener(this);
		gazeTargetPanelGroup.add(gazePanelOnMenuItem);
		gazeTargetPanelMenu.add(gazePanelOnMenuItem);

		gazePanelOffMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Disable");
		gazePanelOffMenuItem.addActionListener(this);
		gazeTargetPanelGroup.add(gazePanelOffMenuItem);
		gazeTargetPanelMenu.add(gazePanelOffMenuItem);
	}

	private void createBlitzPanelMenu() {
		ButtonGroup blitzTargetPanelGroup = new ButtonGroup();
		JMenu blitzTargetPanelMenu = new JMenu(dimensionProvider, SETTING_BLITZ_TARGET_PANEL);
		blitzTargetPanelMenu.setMnemonic(KeyEvent.VK_B);
		add(blitzTargetPanelMenu);

		fBlitzPanelOnMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Enable");
		fBlitzPanelOnMenuItem.addActionListener(this);
		blitzTargetPanelGroup.add(fBlitzPanelOnMenuItem);
		blitzTargetPanelMenu.add(fBlitzPanelOnMenuItem);

		fBlitzPanelOffMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Disable");
		fBlitzPanelOffMenuItem.addActionListener(this);
		blitzTargetPanelGroup.add(fBlitzPanelOffMenuItem);
		blitzTargetPanelMenu.add(fBlitzPanelOffMenuItem);
	}

	private void createAutomoveMenu() {
		JMenu fAutomoveMenu = new JMenu(dimensionProvider, SETTING_AUTOMOVE);
		fAutomoveMenu.setMnemonic(KeyEvent.VK_A);
		add(fAutomoveMenu);

		ButtonGroup automoveGroup = new ButtonGroup();

		fAutomoveOnMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Enable");
		fAutomoveOnMenuItem.addActionListener(this);
		automoveGroup.add(fAutomoveOnMenuItem);
		fAutomoveMenu.add(fAutomoveOnMenuItem);

		fAutomoveOffMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Disable");
		fAutomoveOffMenuItem.addActionListener(this);
		automoveGroup.add(fAutomoveOffMenuItem);
		fAutomoveMenu.add(fAutomoveOffMenuItem);
	}

	private void createTacklezonesMenu() {
		JMenu fTacklezonesMenu = new JMenu(dimensionProvider, "Tacklezones");
		fTacklezonesMenu.setMnemonic(KeyEvent.VK_T);
		add(fTacklezonesMenu);

		// --- Player Mode ---
		JMenu playerModeMenu = new JMenu(dimensionProvider, "Player Mode");
		playerModeMenu.setMnemonic(KeyEvent.VK_P);
		fTacklezonesMenu.add(playerModeMenu);

		ButtonGroup playerModeGroup = new ButtonGroup();

		tzPlayerNoneMenuItem = new JRadioButtonMenuItem(dimensionProvider, "None");
		tzPlayerNoneMenuItem.setMnemonic(KeyEvent.VK_N);
		tzPlayerNoneMenuItem.addActionListener(this);
		playerModeGroup.add(tzPlayerNoneMenuItem);
		playerModeMenu.add(tzPlayerNoneMenuItem);

		tzPlayerHomeMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Home");
		tzPlayerHomeMenuItem.setMnemonic(KeyEvent.VK_H);
		tzPlayerHomeMenuItem.addActionListener(this);
		playerModeGroup.add(tzPlayerHomeMenuItem);
		playerModeMenu.add(tzPlayerHomeMenuItem);

		tzPlayerAwayMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Away");
		tzPlayerAwayMenuItem.setMnemonic(KeyEvent.VK_A);
		tzPlayerAwayMenuItem.addActionListener(this);
		playerModeGroup.add(tzPlayerAwayMenuItem);
		playerModeMenu.add(tzPlayerAwayMenuItem);

		tzPlayerBothMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Both");
		tzPlayerBothMenuItem.setMnemonic(KeyEvent.VK_B);
		tzPlayerBothMenuItem.addActionListener(this);
		playerModeGroup.add(tzPlayerBothMenuItem);
		playerModeMenu.add(tzPlayerBothMenuItem);

		tzPlayerPassiveMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Passive");
		tzPlayerPassiveMenuItem.setMnemonic(KeyEvent.VK_P);
		tzPlayerPassiveMenuItem.addActionListener(this);
		playerModeGroup.add(tzPlayerPassiveMenuItem);
		playerModeMenu.add(tzPlayerPassiveMenuItem);

		tzPlayerPassiveBothOnSetupMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Passive (both on setup)");
		tzPlayerPassiveBothOnSetupMenuItem.setMnemonic(KeyEvent.VK_S);
		tzPlayerPassiveBothOnSetupMenuItem.addActionListener(this);
		playerModeGroup.add(tzPlayerPassiveBothOnSetupMenuItem);
		playerModeMenu.add(tzPlayerPassiveBothOnSetupMenuItem);

		// --- Spectator Mode ---
		JMenu spectatorModeMenu = new JMenu(dimensionProvider, "Spectator Mode");
		spectatorModeMenu.setMnemonic(KeyEvent.VK_S);
		fTacklezonesMenu.add(spectatorModeMenu);

		ButtonGroup spectatorModeGroup = new ButtonGroup();

		tzSpectatorNoneMenuItem = new JRadioButtonMenuItem(dimensionProvider, "None");
		tzSpectatorNoneMenuItem.setMnemonic(KeyEvent.VK_N);
		tzSpectatorNoneMenuItem.addActionListener(this);
		spectatorModeGroup.add(tzSpectatorNoneMenuItem);
		spectatorModeMenu.add(tzSpectatorNoneMenuItem);

		tzSpectatorHomeMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Home");
		tzSpectatorHomeMenuItem.setMnemonic(KeyEvent.VK_H);
		tzSpectatorHomeMenuItem.addActionListener(this);
		spectatorModeGroup.add(tzSpectatorHomeMenuItem);
		spectatorModeMenu.add(tzSpectatorHomeMenuItem);

		tzSpectatorAwayMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Away");
		tzSpectatorAwayMenuItem.setMnemonic(KeyEvent.VK_A);
		tzSpectatorAwayMenuItem.addActionListener(this);
		spectatorModeGroup.add(tzSpectatorAwayMenuItem);
		spectatorModeMenu.add(tzSpectatorAwayMenuItem);

		tzSpectatorBothMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Both");
		tzSpectatorBothMenuItem.setMnemonic(KeyEvent.VK_B);
		tzSpectatorBothMenuItem.addActionListener(this);
		spectatorModeGroup.add(tzSpectatorBothMenuItem);
		spectatorModeMenu.add(tzSpectatorBothMenuItem);

		tzSpectatorPassiveMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Passive");
		tzSpectatorPassiveMenuItem.setMnemonic(KeyEvent.VK_P);
		tzSpectatorPassiveMenuItem.addActionListener(this);
		spectatorModeGroup.add(tzSpectatorPassiveMenuItem);
		spectatorModeMenu.add(tzSpectatorPassiveMenuItem);

		tzSpectatorPassiveBothOnSetupMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Passive (both on setup)");
		tzSpectatorPassiveBothOnSetupMenuItem.setMnemonic(KeyEvent.VK_S);
		tzSpectatorPassiveBothOnSetupMenuItem.addActionListener(this);
		spectatorModeGroup.add(tzSpectatorPassiveBothOnSetupMenuItem);
		spectatorModeMenu.add(tzSpectatorPassiveBothOnSetupMenuItem);

		fTacklezonesMenu.addSeparator();

		// --- No Overlap (global) ---
		JMenu tzNoOverlapMenu = new JMenu(dimensionProvider, "Overlap");
		tzNoOverlapMenu.setMnemonic(KeyEvent.VK_V);
		fTacklezonesMenu.add(tzNoOverlapMenu);

		ButtonGroup noOverlapGroup = new ButtonGroup();

		tzOverlapOffMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Off");
		tzOverlapOffMenuItem.setMnemonic(KeyEvent.VK_F);
		tzOverlapOffMenuItem.addActionListener(this);
		noOverlapGroup.add(tzOverlapOffMenuItem);
		tzNoOverlapMenu.add(tzOverlapOffMenuItem);

		tzOverlapOnMenuItem = new JRadioButtonMenuItem(dimensionProvider, "On");
		tzOverlapOnMenuItem.setMnemonic(KeyEvent.VK_N);
		tzOverlapOnMenuItem.addActionListener(this);
		noOverlapGroup.add(tzOverlapOnMenuItem);
		tzNoOverlapMenu.add(tzOverlapOnMenuItem);

		// --- Contour (global) ---
		JMenu tzContourMenu = new JMenu(dimensionProvider, "Contour");
		tzContourMenu.setMnemonic(KeyEvent.VK_C);
		fTacklezonesMenu.add(tzContourMenu);

		ButtonGroup contourGroup = new ButtonGroup();

		tzContourOffMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Off");
		tzContourOffMenuItem.setMnemonic(KeyEvent.VK_F);
		tzContourOffMenuItem.addActionListener(this);
		contourGroup.add(tzContourOffMenuItem);
		tzContourMenu.add(tzContourOffMenuItem);

		tzContourOnMenuItem = new JRadioButtonMenuItem(dimensionProvider, "On");
		tzContourOnMenuItem.setMnemonic(KeyEvent.VK_N);
		tzContourOnMenuItem.addActionListener(this);
		contourGroup.add(tzContourOnMenuItem);
		tzContourMenu.add(tzContourOnMenuItem);
	}

	public String menuName(CommonProperty menuProperty) {
		JMenu exposedMenu = exposedMenus.get(menuProperty);
		return exposedMenu != null ? exposedMenu.getText() : "";
	}

	public Map<String, String> menuEntries(CommonProperty menuProperty) {
		Map<String, String> entries = new LinkedHashMap<>();

		JMenu exposedMenu = exposedMenus.get(menuProperty);
		if (exposedMenu != null) {
			for (int i = 0; i < exposedMenu.getItemCount(); i++) {
				javax.swing.JMenuItem item = exposedMenu.getItem(i);
				entries.put(item.getName(), item.getText());
			}
		}

		return entries;
	}
}
