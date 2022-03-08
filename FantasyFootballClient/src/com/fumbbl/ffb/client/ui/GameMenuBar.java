package com.fumbbl.ffb.client.ui;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.ConcedeGameStatus;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.ClientData;
import com.fumbbl.ffb.client.ClientReplayer;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IClientProperty;
import com.fumbbl.ffb.client.IClientPropertyValue;
import com.fumbbl.ffb.client.PlayerIconFactory;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.dialog.DialogAbout;
import com.fumbbl.ffb.client.dialog.DialogChangeList;
import com.fumbbl.ffb.client.dialog.DialogChatCommands;
import com.fumbbl.ffb.client.dialog.DialogGameStatistics;
import com.fumbbl.ffb.client.dialog.DialogKeyBindings;
import com.fumbbl.ffb.client.dialog.DialogSoundVolume;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.client.dialog.IDialogCloseListener;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.factory.bb2020.PrayerFactory;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.CardType;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.IGameOption;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Kalimar
 */
public class GameMenuBar extends JMenuBar implements ActionListener, IDialogCloseListener {

	private static final String _REPLAY_MODE_ON = "Replay Mode";
	private static final String _REPLAY_MODE_OFF = "Spectator Mode";

	private final FantasyFootballClient fClient;

	private final JMenuItem fGameReplayMenuItem;
	private final JMenuItem fGameConcessionMenuItem;
	private final JMenuItem fGameStatisticsMenuItem;

	private final JMenuItem fLoadSetupMenuItem;
	private final JMenuItem fSaveSetupMenuItem;

	private final JMenuItem fRestoreDefaultsMenuItem;

	private final JMenuItem fSoundVolumeItem;
	private final JRadioButtonMenuItem fSoundOnMenuItem;
	private final JRadioButtonMenuItem fSoundMuteSpectatorsMenuItem;
	private final JRadioButtonMenuItem fSoundOffMenuItem;

	private final JRadioButtonMenuItem fIconsAbstract;
	private final JRadioButtonMenuItem fIconsRosterOpponent;
	private final JRadioButtonMenuItem fIconsRosterBoth;
	private final JRadioButtonMenuItem fIconsTeam;

	private final JRadioButtonMenuItem fAutomoveOnMenuItem;
	private final JRadioButtonMenuItem fAutomoveOffMenuItem;

	private final JRadioButtonMenuItem fBlitzPanelOnMenuItem;
	private final JRadioButtonMenuItem fBlitzPanelOffMenuItem;

	private final JRadioButtonMenuItem gazePanelOnMenuItem;
	private final JRadioButtonMenuItem gazePanelOffMenuItem;

	private final JRadioButtonMenuItem rightClickEndActionOnMenuItem;
	private final JRadioButtonMenuItem rightClickOpensContextMenuItem;
	private final JRadioButtonMenuItem rightClickLegacyModeItem;
	private final JRadioButtonMenuItem rightClickEndActionOffMenuItem;

	private final JRadioButtonMenuItem fCustomPitchMenuItem;
	private final JRadioButtonMenuItem fDefaultPitchMenuItem;
	private final JRadioButtonMenuItem fBasicPitchMenuItem;

	private final JRadioButtonMenuItem fPitchMarkingsOnMenuItem;
	private final JRadioButtonMenuItem fPitchMarkingsOffMenuItem;

	private final JRadioButtonMenuItem fTeamLogoBothMenuItem;
	private final JRadioButtonMenuItem fTeamLogoOwnMenuItem;
	private final JRadioButtonMenuItem fTeamLogoNoneMenuItem;

	private final JRadioButtonMenuItem fPitchWeatherOnMenuItem;
	private final JRadioButtonMenuItem fPitchWeatherOffMenuItem;

	private final JRadioButtonMenuItem fRangeGridAlwaysOnMenuItem;
	private final JRadioButtonMenuItem fRangeGridToggleMenuItem;

	private final JMenu fMissingPlayersMenu;

	private final JMenu fInducementsMenu;

	private final JMenu fActiveCardsMenu;

	private final JMenu prayersMenu;

	private final JMenu fGameOptionsMenu;

	private final JMenuItem fAboutMenuItem;
	private final JMenuItem fChatCommandsMenuItem;
	private final JMenuItem fKeyBindingsMenuItem;
	private final JMenuItem changeListItem;

	private IDialog fDialogShown;

	private int fCurrentInducementTotalHome;
	private int fCurrentUsedCardsHome;
	private int fCurrentInducementTotalAway;
	private int fCurrentUsedCardsAway;

	private Card[] fCurrentActiveCardsHome;
	private Card[] fCurrentActiveCardsAway;

	private final List<Prayer> currentPrayersHome = new ArrayList<>();
	private final List<Prayer> currentPrayersAway = new ArrayList<>();

	private class MenuPlayerMouseListener extends MouseAdapter {

		private final Player<?> fPlayer;

		public MenuPlayerMouseListener(Player<?> pPlayer) {
			fPlayer = pPlayer;
		}

		public void mouseEntered(MouseEvent pMouseEvent) {
			ClientData clientData = getClient().getClientData();
			// do not interfere with dragging (MNG player reappears on pitch bug)
			if ((clientData.getSelectedPlayer() != fPlayer) && (clientData.getDragStartPosition() == null)) {
				clientData.setSelectedPlayer(fPlayer);
				getClient().getUserInterface().refreshSideBars();
			}
		}

	}

	public GameMenuBar(FantasyFootballClient pClient) {

		setFont(new Font("Sans Serif", Font.PLAIN, 12));

		fClient = pClient;

		JMenu fGameMenu = new JMenu("Game");
		fGameMenu.setMnemonic(KeyEvent.VK_G);
		add(fGameMenu);

		fGameReplayMenuItem = new JMenuItem(_REPLAY_MODE_ON, KeyEvent.VK_R);
		String keyMenuReplay = getClient().getProperty(IClientProperty.KEY_MENU_REPLAY);
		if (StringTool.isProvided(keyMenuReplay)) {
			fGameReplayMenuItem.setAccelerator(KeyStroke.getKeyStroke(keyMenuReplay));
		}
		fGameReplayMenuItem.addActionListener(this);
		fGameMenu.add(fGameReplayMenuItem);

		fGameConcessionMenuItem = new JMenuItem("Concede Game", KeyEvent.VK_C);
		fGameConcessionMenuItem.addActionListener(this);
		fGameConcessionMenuItem.setEnabled(false);
		fGameMenu.add(fGameConcessionMenuItem);

		fGameStatisticsMenuItem = new JMenuItem("Game Statistics", KeyEvent.VK_S);
		fGameStatisticsMenuItem.addActionListener(this);
		fGameStatisticsMenuItem.setEnabled(false);
		fGameMenu.add(fGameStatisticsMenuItem);

		JMenu fTeamSetupMenu = new JMenu("Team Setup");
		fTeamSetupMenu.setMnemonic(KeyEvent.VK_T);
		add(fTeamSetupMenu);

		fLoadSetupMenuItem = new JMenuItem("Load Setup", KeyEvent.VK_L);
		String menuSetupLoad = getClient().getProperty(IClientProperty.KEY_MENU_SETUP_LOAD);
		if (StringTool.isProvided(menuSetupLoad)) {
			fLoadSetupMenuItem.setAccelerator(KeyStroke.getKeyStroke(menuSetupLoad));
		}
		fLoadSetupMenuItem.addActionListener(this);
		fTeamSetupMenu.add(fLoadSetupMenuItem);

		fSaveSetupMenuItem = new JMenuItem("Save Setup", KeyEvent.VK_S);
		String menuSetupSave = getClient().getProperty(IClientProperty.KEY_MENU_SETUP_SAVE);
		if (StringTool.isProvided(menuSetupSave)) {
			fSaveSetupMenuItem.setAccelerator(KeyStroke.getKeyStroke(menuSetupSave));
		}
		fSaveSetupMenuItem.addActionListener(this);
		fTeamSetupMenu.add(fSaveSetupMenuItem);

		JMenu fUserSettingsMenu = new JMenu("User Settings");
		fUserSettingsMenu.setMnemonic(KeyEvent.VK_U);
		add(fUserSettingsMenu);

		JMenu fSoundMenu = new JMenu("Sound");
		fSoundMenu.setMnemonic(KeyEvent.VK_S);
		fUserSettingsMenu.add(fSoundMenu);

		fSoundVolumeItem = new JMenuItem("Sound Volume");
		fSoundVolumeItem.setMnemonic(KeyEvent.VK_V);
		fSoundVolumeItem.addActionListener(this);
		fSoundMenu.add(fSoundVolumeItem);

		fSoundMenu.addSeparator();

		ButtonGroup soundGroup = new ButtonGroup();

		fSoundOnMenuItem = new JRadioButtonMenuItem("Sound on");
		fSoundOnMenuItem.addActionListener(this);
		soundGroup.add(fSoundOnMenuItem);
		fSoundMenu.add(fSoundOnMenuItem);

		fSoundMuteSpectatorsMenuItem = new JRadioButtonMenuItem("Mute spectators");
		fSoundMuteSpectatorsMenuItem.addActionListener(this);
		soundGroup.add(fSoundMuteSpectatorsMenuItem);
		fSoundMenu.add(fSoundMuteSpectatorsMenuItem);

		fSoundOffMenuItem = new JRadioButtonMenuItem("Sound off");
		fSoundOffMenuItem.addActionListener(this);
		soundGroup.add(fSoundOffMenuItem);
		fSoundMenu.add(fSoundOffMenuItem);

		JMenu fIconsMenu = new JMenu("Icons");
		fIconsMenu.setMnemonic(KeyEvent.VK_I);
		fUserSettingsMenu.add(fIconsMenu);

		ButtonGroup iconsGroup = new ButtonGroup();

		fIconsTeam = new JRadioButtonMenuItem("Team icons");
		fIconsTeam.setMnemonic(KeyEvent.VK_T);
		fIconsTeam.addActionListener(this);
		iconsGroup.add(fIconsTeam);
		fIconsMenu.add(fIconsTeam);

		fIconsRosterOpponent = new JRadioButtonMenuItem("Roster icons (Opponent)");
		fIconsRosterOpponent.setMnemonic(KeyEvent.VK_O);
		fIconsRosterOpponent.addActionListener(this);
		iconsGroup.add(fIconsRosterOpponent);
		fIconsMenu.add(fIconsRosterOpponent);

		fIconsRosterBoth = new JRadioButtonMenuItem("Roster icons (Both)");
		fIconsRosterBoth.setMnemonic(KeyEvent.VK_B);
		fIconsRosterBoth.addActionListener(this);
		iconsGroup.add(fIconsRosterBoth);
		fIconsMenu.add(fIconsRosterBoth);

		fIconsAbstract = new JRadioButtonMenuItem("Abstract icons");
		fIconsAbstract.setMnemonic(KeyEvent.VK_A);
		fIconsAbstract.addActionListener(this);
		iconsGroup.add(fIconsAbstract);
		fIconsMenu.add(fIconsAbstract);

		JMenu fAutomoveMenu = new JMenu("Automove");
		fAutomoveMenu.setMnemonic(KeyEvent.VK_A);
		fUserSettingsMenu.add(fAutomoveMenu);

		ButtonGroup automoveGroup = new ButtonGroup();

		fAutomoveOnMenuItem = new JRadioButtonMenuItem("Enable");
		fAutomoveOnMenuItem.addActionListener(this);
		automoveGroup.add(fAutomoveOnMenuItem);
		fAutomoveMenu.add(fAutomoveOnMenuItem);

		fAutomoveOffMenuItem = new JRadioButtonMenuItem("Disable");
		fAutomoveOffMenuItem.addActionListener(this);
		automoveGroup.add(fAutomoveOffMenuItem);
		fAutomoveMenu.add(fAutomoveOffMenuItem);
		
		ButtonGroup blitzTargetPanelGroup = new ButtonGroup();
		JMenu blitzTargetPanelMenu = new JMenu("Blitz Target Panel");
		blitzTargetPanelMenu.setMnemonic(KeyEvent.VK_P);
		fUserSettingsMenu.add(blitzTargetPanelMenu);

		fBlitzPanelOnMenuItem = new JRadioButtonMenuItem("Enable");
		fBlitzPanelOnMenuItem.addActionListener(this);
		blitzTargetPanelGroup.add(fBlitzPanelOnMenuItem);
		blitzTargetPanelMenu.add(fBlitzPanelOnMenuItem);

		fBlitzPanelOffMenuItem = new JRadioButtonMenuItem("Disable");
		fBlitzPanelOffMenuItem.addActionListener(this);
		blitzTargetPanelGroup.add(fBlitzPanelOffMenuItem);
		blitzTargetPanelMenu.add(fBlitzPanelOffMenuItem);

		ButtonGroup gazeTargetPanelGroup = new ButtonGroup();
		JMenu gazeTargetPanelMenu = new JMenu("Gaze Target Panel");
		gazeTargetPanelMenu.setMnemonic(KeyEvent.VK_G);
		fUserSettingsMenu.add(gazeTargetPanelMenu);

		gazePanelOnMenuItem = new JRadioButtonMenuItem("Enable");
		gazePanelOnMenuItem.addActionListener(this);
		gazeTargetPanelGroup.add(gazePanelOnMenuItem);
		gazeTargetPanelMenu.add(gazePanelOnMenuItem);

		gazePanelOffMenuItem = new JRadioButtonMenuItem("Disable");
		gazePanelOffMenuItem.addActionListener(this);
		gazeTargetPanelGroup.add(gazePanelOffMenuItem);
		gazeTargetPanelMenu.add(gazePanelOffMenuItem);

		ButtonGroup rightClickEndActionPanelGroup = new ButtonGroup();
		JMenu rightClickEndActionPanelMenu = new JMenu("Right Click Behaviour");
		rightClickEndActionPanelMenu.setMnemonic(KeyEvent.VK_R);
		fUserSettingsMenu.add(rightClickEndActionPanelMenu);

		rightClickEndActionOnMenuItem = new JRadioButtonMenuItem("Ends Action/Selection");
		rightClickEndActionOnMenuItem.addActionListener(this);
		rightClickEndActionPanelGroup.add(rightClickEndActionOnMenuItem);
		rightClickEndActionPanelMenu.add(rightClickEndActionOnMenuItem);

		rightClickLegacyModeItem = new JRadioButtonMenuItem("Works like Left Click (Legacy)");
		rightClickLegacyModeItem.addActionListener(this);
		rightClickEndActionPanelGroup.add(rightClickLegacyModeItem);
		rightClickEndActionPanelMenu.add(rightClickLegacyModeItem);

		rightClickOpensContextMenuItem = new JRadioButtonMenuItem("Selects Player/Opens Context Menu");
		rightClickOpensContextMenuItem.addActionListener(this);
		rightClickEndActionPanelGroup.add(rightClickOpensContextMenuItem);
		rightClickEndActionPanelMenu.add(rightClickOpensContextMenuItem);

		rightClickEndActionOffMenuItem = new JRadioButtonMenuItem("Disabled");
		rightClickEndActionOffMenuItem.addActionListener(this);
		rightClickEndActionPanelGroup.add(rightClickEndActionOffMenuItem);
		rightClickEndActionPanelMenu.add(rightClickEndActionOffMenuItem);

		JMenu fPitchMenu = new JMenu("Pitch");
		fPitchMenu.setMnemonic(KeyEvent.VK_P);
		fUserSettingsMenu.add(fPitchMenu);

		JMenu fPitchCustomizationMenu = new JMenu("Pitch Customization");
		fPitchCustomizationMenu.setMnemonic(KeyEvent.VK_C);
		fPitchMenu.add(fPitchCustomizationMenu);

		ButtonGroup pitchCustomGroup = new ButtonGroup();

		fCustomPitchMenuItem = new JRadioButtonMenuItem("Use Custom Pitch");
		fCustomPitchMenuItem.addActionListener(this);
		pitchCustomGroup.add(fCustomPitchMenuItem);
		fPitchCustomizationMenu.add(fCustomPitchMenuItem);

		fDefaultPitchMenuItem = new JRadioButtonMenuItem("Use Default Pitch");
		fDefaultPitchMenuItem.addActionListener(this);
		pitchCustomGroup.add(fDefaultPitchMenuItem);
		fPitchCustomizationMenu.add(fDefaultPitchMenuItem);

		fBasicPitchMenuItem = new JRadioButtonMenuItem("Use Basic Pitch");
		fBasicPitchMenuItem.addActionListener(this);
		pitchCustomGroup.add(fBasicPitchMenuItem);
		fPitchCustomizationMenu.add(fBasicPitchMenuItem);

		JMenu fPitchMarkingsMenu = new JMenu("Pitch Markings");
		fPitchMarkingsMenu.setMnemonic(KeyEvent.VK_M);
		fPitchMenu.add(fPitchMarkingsMenu);

		ButtonGroup tdDistanceGroup = new ButtonGroup();

		fPitchMarkingsOnMenuItem = new JRadioButtonMenuItem("Pitch Markings on");
		fPitchMarkingsOnMenuItem.addActionListener(this);
		tdDistanceGroup.add(fPitchMarkingsOnMenuItem);
		fPitchMarkingsMenu.add(fPitchMarkingsOnMenuItem);

		fPitchMarkingsOffMenuItem = new JRadioButtonMenuItem("Pitch Markings off");
		fPitchMarkingsOffMenuItem.addActionListener(this);
		tdDistanceGroup.add(fPitchMarkingsOffMenuItem);
		fPitchMarkingsMenu.add(fPitchMarkingsOffMenuItem);

		JMenu fTeamLogoMenu = new JMenu("Team Logo");
		fTeamLogoMenu.setMnemonic(KeyEvent.VK_T);
		fPitchMenu.add(fTeamLogoMenu);

		ButtonGroup teamLogoGroup = new ButtonGroup();

		fTeamLogoBothMenuItem = new JRadioButtonMenuItem("Show both Team-Logos");
		fTeamLogoBothMenuItem.addActionListener(this);
		teamLogoGroup.add(fTeamLogoBothMenuItem);
		fTeamLogoMenu.add(fTeamLogoBothMenuItem);

		fTeamLogoOwnMenuItem = new JRadioButtonMenuItem("Show my Team-Logo only");
		fTeamLogoOwnMenuItem.addActionListener(this);
		teamLogoGroup.add(fTeamLogoOwnMenuItem);
		fTeamLogoMenu.add(fTeamLogoOwnMenuItem);

		fTeamLogoNoneMenuItem = new JRadioButtonMenuItem("Show no Team-Logos");
		fTeamLogoNoneMenuItem.addActionListener(this);
		teamLogoGroup.add(fTeamLogoNoneMenuItem);
		fTeamLogoMenu.add(fTeamLogoNoneMenuItem);

		JMenu fPitchWeatherMenu = new JMenu("Pitch Weather");
		fPitchWeatherMenu.setMnemonic(KeyEvent.VK_W);
		fPitchMenu.add(fPitchWeatherMenu);

		ButtonGroup pitchWeatherGroup = new ButtonGroup();

		fPitchWeatherOnMenuItem = new JRadioButtonMenuItem("Change pitch with weather");
		fPitchWeatherOnMenuItem.addActionListener(this);
		pitchWeatherGroup.add(fPitchWeatherOnMenuItem);
		fPitchWeatherMenu.add(fPitchWeatherOnMenuItem);

		fPitchWeatherOffMenuItem = new JRadioButtonMenuItem("Always show nice weather pitch");
		fPitchWeatherOffMenuItem.addActionListener(this);
		pitchWeatherGroup.add(fPitchWeatherOffMenuItem);
		fPitchWeatherMenu.add(fPitchWeatherOffMenuItem);

		JMenu fRangeGridMenu = new JMenu("Range Grid");
		fRangeGridMenu.setMnemonic(KeyEvent.VK_R);
		fUserSettingsMenu.add(fRangeGridMenu);

		ButtonGroup rangeGridGroup = new ButtonGroup();

		fRangeGridAlwaysOnMenuItem = new JRadioButtonMenuItem("Range Grid always on");
		fRangeGridAlwaysOnMenuItem.addActionListener(this);
		rangeGridGroup.add(fRangeGridAlwaysOnMenuItem);
		fRangeGridMenu.add(fRangeGridAlwaysOnMenuItem);

		fRangeGridToggleMenuItem = new JRadioButtonMenuItem("Range Grid toggle");
		fRangeGridToggleMenuItem.addActionListener(this);
		rangeGridGroup.add(fRangeGridToggleMenuItem);
		fRangeGridMenu.add(fRangeGridToggleMenuItem);

		fUserSettingsMenu.addSeparator();

		fRestoreDefaultsMenuItem = new JMenuItem("Restore Defaults");
		fRestoreDefaultsMenuItem.addActionListener(this);
		fRestoreDefaultsMenuItem.setEnabled(false);
		fUserSettingsMenu.add(fRestoreDefaultsMenuItem);

		fMissingPlayersMenu = new JMenu("Missing Players");
		fMissingPlayersMenu.setMnemonic(KeyEvent.VK_M);
		fMissingPlayersMenu.setEnabled(false);
		add(fMissingPlayersMenu);

		fInducementsMenu = new JMenu("Inducements");
		fInducementsMenu.setMnemonic(KeyEvent.VK_I);
		fInducementsMenu.setEnabled(false);
		add(fInducementsMenu);

		fActiveCardsMenu = new JMenu("Active Cards");
		fActiveCardsMenu.setMnemonic(KeyEvent.VK_C);
		fActiveCardsMenu.setEnabled(false);
		add(fActiveCardsMenu);

		prayersMenu = new JMenu("Prayers");
		prayersMenu.setMnemonic(KeyEvent.VK_P);
		prayersMenu.setEnabled(false);
		add(prayersMenu);

		fGameOptionsMenu = new JMenu("Game Options");
		fGameOptionsMenu.setMnemonic(KeyEvent.VK_O);
		fGameOptionsMenu.setEnabled(false);
		add(fGameOptionsMenu);

		JMenu fHelpMenu = new JMenu("Help");
		fHelpMenu.setMnemonic(KeyEvent.VK_H);
		add(fHelpMenu);

		fAboutMenuItem = new JMenuItem("About", KeyEvent.VK_A);
		fAboutMenuItem.addActionListener(this);
		fHelpMenu.add(fAboutMenuItem);

		fChatCommandsMenuItem = new JMenuItem("Chat Commands", KeyEvent.VK_C);
		fChatCommandsMenuItem.addActionListener(this);
		fHelpMenu.add(fChatCommandsMenuItem);

		changeListItem = new JMenuItem("What's new?", KeyEvent.VK_L);
		changeListItem.addActionListener(this);
		fHelpMenu.add(changeListItem);

		fKeyBindingsMenuItem = new JMenuItem("Key Bindings", KeyEvent.VK_K);
		fKeyBindingsMenuItem.addActionListener(this);
		fHelpMenu.add(fKeyBindingsMenuItem);

		refresh();

	}

	public void init() {
		fCurrentInducementTotalHome = -1;
		fCurrentUsedCardsHome = 0;
		fCurrentInducementTotalAway = -1;
		fCurrentUsedCardsAway = 0;
		fCurrentActiveCardsHome = null;
		fCurrentActiveCardsAway = null;
		refresh();
	}

	public void refresh() {

		Game game = getClient().getGame();

		String soundSetting = getClient().getProperty(IClientProperty.SETTING_SOUND_MODE);
		fSoundOnMenuItem.setSelected(true);
		fSoundMuteSpectatorsMenuItem.setSelected(IClientPropertyValue.SETTING_SOUND_MUTE_SPECTATORS.equals(soundSetting));
		fSoundOffMenuItem.setSelected(IClientPropertyValue.SETTING_SOUND_OFF.equals(soundSetting));

		String iconsSetting = getClient().getProperty(IClientProperty.SETTING_ICONS);
		fIconsTeam.setSelected(true);
		fIconsRosterOpponent.setSelected(IClientPropertyValue.SETTING_ICONS_ROSTER_OPPONENT.equals(iconsSetting));
		fIconsRosterBoth.setSelected(IClientPropertyValue.SETTING_ICONS_ROSTER_BOTH.equals(iconsSetting));
		fIconsAbstract.setSelected(IClientPropertyValue.SETTING_ICONS_ABSTRACT.equals(iconsSetting));

		String automoveSetting = getClient().getProperty(IClientProperty.SETTING_AUTOMOVE);
		fAutomoveOnMenuItem.setSelected(true);
		fAutomoveOffMenuItem.setSelected(IClientPropertyValue.SETTING_AUTOMOVE_OFF.equals(automoveSetting));

		String blitzTargetPanelSetting = getClient().getProperty(IClientProperty.SETTING_BLITZ_TARGET_PANEL);
		fBlitzPanelOnMenuItem.setSelected(true);
		fBlitzPanelOffMenuItem.setSelected(IClientPropertyValue.SETTING_BLITZ_TARGET_PANEL_OFF.equals(blitzTargetPanelSetting));

		String gazeTargetPanelSetting = getClient().getProperty(IClientProperty.SETTING_GAZE_TARGET_PANEL);
		gazePanelOnMenuItem.setSelected(true);
		gazePanelOffMenuItem.setSelected(IClientPropertyValue.SETTING_GAZE_TARGET_PANEL_OFF.equals(gazeTargetPanelSetting));

		String rightClickEndActionSetting = getClient().getProperty(IClientProperty.SETTING_RIGHT_CLICK_END_ACTION);
		rightClickEndActionOffMenuItem.setSelected(true);
		rightClickEndActionOnMenuItem.setSelected(IClientPropertyValue.SETTING_RIGHT_CLICK_END_ACTION_ON.equals(rightClickEndActionSetting));
		rightClickLegacyModeItem.setSelected(IClientPropertyValue.SETTING_RIGHT_CLICK_LEGACY_MODE.equals(rightClickEndActionSetting));
		rightClickOpensContextMenuItem.setSelected(IClientPropertyValue.SETTING_RIGHT_CLICK_OPENS_CONTEXT_MENU.equals(rightClickEndActionSetting));

		String pitchCustomizationSetting = getClient().getProperty(IClientProperty.SETTING_PITCH_CUSTOMIZATION);
		fCustomPitchMenuItem.setSelected(true);
		fDefaultPitchMenuItem.setSelected(IClientPropertyValue.SETTING_PITCH_DEFAULT.equals(pitchCustomizationSetting));
		fBasicPitchMenuItem.setSelected(IClientPropertyValue.SETTING_PITCH_BASIC.equals(pitchCustomizationSetting));

		String pitchMarkingsSetting = getClient().getProperty(IClientProperty.SETTING_PITCH_MARKINGS);
		fPitchMarkingsOffMenuItem.setSelected(true);
		fPitchMarkingsOnMenuItem.setSelected(IClientPropertyValue.SETTING_PITCH_MARKINGS_ON.equals(pitchMarkingsSetting));

		String teamLogosSetting = getClient().getProperty(IClientProperty.SETTING_TEAM_LOGOS);
		fTeamLogoBothMenuItem.setSelected(true);
		fTeamLogoOwnMenuItem.setSelected(IClientPropertyValue.SETTING_TEAM_LOGOS_OWN.equals(teamLogosSetting));
		fTeamLogoNoneMenuItem.setSelected(IClientPropertyValue.SETTING_TEAM_LOGOS_NONE.equals(teamLogosSetting));

		String pitchWeatherSetting = getClient().getProperty(IClientProperty.SETTING_PITCH_WEATHER);
		fPitchWeatherOnMenuItem.setSelected(true);
		fPitchWeatherOffMenuItem.setSelected(IClientPropertyValue.SETTING_PITCH_WEATHER_OFF.equals(pitchWeatherSetting));

		String rangeGridSetting = getClient().getProperty(IClientProperty.SETTING_RANGEGRID);
		fRangeGridToggleMenuItem.setSelected(true);
		fRangeGridAlwaysOnMenuItem.setSelected(IClientPropertyValue.SETTING_RANGEGRID_ALWAYS_ON.equals(rangeGridSetting));

		boolean gameStarted = ((game != null) && (game.getStarted() != null));
		fGameStatisticsMenuItem.setEnabled(gameStarted);

		boolean allowConcessions = game != null && ((GameOptionBoolean) game.getOptions().getOptionWithDefault(GameOptionId.ALLOW_CONCESSIONS)).isEnabled();
		fGameConcessionMenuItem.setEnabled(allowConcessions && gameStarted && game.isHomePlaying()
			&& (ClientMode.PLAYER == getClient().getMode()) && game.isConcessionPossible());

		fGameReplayMenuItem.setEnabled(ClientMode.SPECTATOR == getClient().getMode());

		updateMissingPlayers();
		updateInducements();
		updateActiveCards();
		updatePrayers();
		updateGameOptions();

	}

	public FantasyFootballClient getClient() {
		return fClient;
	}

	public void actionPerformed(ActionEvent e) {
		ClientReplayer replayer = getClient().getReplayer();
		JMenuItem source = (JMenuItem) (e.getSource());
		if (source == fLoadSetupMenuItem) {
			getClient().getClientState().actionKeyPressed(ActionKey.MENU_SETUP_LOAD);
		}
		if (source == fSaveSetupMenuItem) {
			getClient().getClientState().actionKeyPressed(ActionKey.MENU_SETUP_SAVE);
		}
		if (source == fSoundVolumeItem) {
			showDialog(new DialogSoundVolume(getClient()));
		}
		if (source == fSoundOffMenuItem) {
			getClient().setProperty(IClientProperty.SETTING_SOUND_MODE, IClientPropertyValue.SETTING_SOUND_OFF);
			getClient().saveUserSettings(false);
		}
		if (source == fSoundMuteSpectatorsMenuItem) {
			getClient().setProperty(IClientProperty.SETTING_SOUND_MODE, IClientPropertyValue.SETTING_SOUND_MUTE_SPECTATORS);
			getClient().saveUserSettings(false);
		}
		if (source == fSoundOnMenuItem) {
			getClient().setProperty(IClientProperty.SETTING_SOUND_MODE, IClientPropertyValue.SETTING_SOUND_ON);
			getClient().saveUserSettings(false);
		}
		if (source == fIconsTeam) {
			getClient().setProperty(IClientProperty.SETTING_ICONS, IClientPropertyValue.SETTING_ICONS_TEAM);
			getClient().saveUserSettings(true);
		}
		if (source == fIconsRosterOpponent) {
			getClient().setProperty(IClientProperty.SETTING_ICONS, IClientPropertyValue.SETTING_ICONS_ROSTER_OPPONENT);
			getClient().saveUserSettings(true);
		}
		if (source == fIconsRosterBoth) {
			getClient().setProperty(IClientProperty.SETTING_ICONS, IClientPropertyValue.SETTING_ICONS_ROSTER_BOTH);
			getClient().saveUserSettings(true);
		}
		if (source == fIconsAbstract) {
			getClient().setProperty(IClientProperty.SETTING_ICONS, IClientPropertyValue.SETTING_ICONS_ABSTRACT);
			getClient().saveUserSettings(true);
		}
		if (source == fAboutMenuItem) {
			showDialog(new DialogAbout(getClient()));
		}
		if (source == fChatCommandsMenuItem) {
			showDialog(new DialogChatCommands(getClient()));
		}
		if (source == changeListItem) {
			showDialog(new DialogChangeList(getClient()));
		}
		if (source == fKeyBindingsMenuItem) {
			showDialog(new DialogKeyBindings(getClient()));
		}
		if (source == fGameStatisticsMenuItem) {
			showDialog(new DialogGameStatistics(getClient()));
		}
		if (source == fAutomoveOffMenuItem) {
			getClient().setProperty(IClientProperty.SETTING_AUTOMOVE, IClientPropertyValue.SETTING_AUTOMOVE_OFF);
			getClient().saveUserSettings(false);
		}
		if (source == fAutomoveOnMenuItem) {
			getClient().setProperty(IClientProperty.SETTING_AUTOMOVE, IClientPropertyValue.SETTING_AUTOMOVE_ON);
			getClient().saveUserSettings(false);
		}
		if (source == fBlitzPanelOffMenuItem) {
			getClient().setProperty(IClientProperty.SETTING_BLITZ_TARGET_PANEL, IClientPropertyValue.SETTING_BLITZ_TARGET_PANEL_OFF);
			getClient().saveUserSettings(false);
		}
		if (source == fBlitzPanelOnMenuItem) {
			getClient().setProperty(IClientProperty.SETTING_BLITZ_TARGET_PANEL, IClientPropertyValue.SETTING_BLITZ_TARGET_PANEL_ON);
			getClient().saveUserSettings(false);
		}
		if (source == gazePanelOnMenuItem) {
			getClient().setProperty(IClientProperty.SETTING_GAZE_TARGET_PANEL, IClientPropertyValue.SETTING_GAZE_TARGET_PANEL_ON);
			getClient().saveUserSettings(false);
		}
		if (source == gazePanelOffMenuItem) {
			getClient().setProperty(IClientProperty.SETTING_GAZE_TARGET_PANEL, IClientPropertyValue.SETTING_GAZE_TARGET_PANEL_OFF);
			getClient().saveUserSettings(false);
		}
		if (source == rightClickEndActionOnMenuItem) {
			getClient().setProperty(IClientProperty.SETTING_RIGHT_CLICK_END_ACTION, IClientPropertyValue.SETTING_RIGHT_CLICK_END_ACTION_ON);
			getClient().saveUserSettings(false);
		}
		if (source == rightClickLegacyModeItem) {
			getClient().setProperty(IClientProperty.SETTING_RIGHT_CLICK_END_ACTION, IClientPropertyValue.SETTING_RIGHT_CLICK_LEGACY_MODE);
			getClient().saveUserSettings(false);
		}
		if (source == rightClickOpensContextMenuItem) {
			getClient().setProperty(IClientProperty.SETTING_RIGHT_CLICK_END_ACTION, IClientPropertyValue.SETTING_RIGHT_CLICK_OPENS_CONTEXT_MENU);
			getClient().saveUserSettings(false);
		}
		if (source == rightClickEndActionOffMenuItem) {
			getClient().setProperty(IClientProperty.SETTING_RIGHT_CLICK_END_ACTION, IClientPropertyValue.SETTING_RIGHT_CLICK_END_ACTION_OFF);
			getClient().saveUserSettings(false);
		}
		if (source == fCustomPitchMenuItem) {
			getClient().setProperty(IClientProperty.SETTING_PITCH_CUSTOMIZATION, IClientPropertyValue.SETTING_PITCH_CUSTOM);
			getClient().saveUserSettings(true);
		}
		if (source == fDefaultPitchMenuItem) {
			getClient().setProperty(IClientProperty.SETTING_PITCH_CUSTOMIZATION, IClientPropertyValue.SETTING_PITCH_DEFAULT);
			getClient().saveUserSettings(true);
		}
		if (source == fBasicPitchMenuItem) {
			getClient().setProperty(IClientProperty.SETTING_PITCH_CUSTOMIZATION, IClientPropertyValue.SETTING_PITCH_BASIC);
			getClient().saveUserSettings(true);
		}
		if (source == fPitchMarkingsOffMenuItem) {
			getClient().setProperty(IClientProperty.SETTING_PITCH_MARKINGS, IClientPropertyValue.SETTING_PITCH_MARKINGS_OFF);
			getClient().saveUserSettings(true);
		}
		if (source == fPitchMarkingsOnMenuItem) {
			getClient().setProperty(IClientProperty.SETTING_PITCH_MARKINGS, IClientPropertyValue.SETTING_PITCH_MARKINGS_ON);
			getClient().saveUserSettings(true);
		}
		if (source == fTeamLogoBothMenuItem) {
			getClient().setProperty(IClientProperty.SETTING_TEAM_LOGOS, IClientPropertyValue.SETTING_TEAM_LOGOS_BOTH);
			getClient().saveUserSettings(true);
		}
		if (source == fTeamLogoOwnMenuItem) {
			getClient().setProperty(IClientProperty.SETTING_TEAM_LOGOS, IClientPropertyValue.SETTING_TEAM_LOGOS_OWN);
			getClient().saveUserSettings(true);
		}
		if (source == fTeamLogoNoneMenuItem) {
			getClient().setProperty(IClientProperty.SETTING_TEAM_LOGOS, IClientPropertyValue.SETTING_TEAM_LOGOS_NONE);
			getClient().saveUserSettings(true);
		}
		if (source == fCustomPitchMenuItem) {
			getClient().setProperty(IClientProperty.SETTING_PITCH_CUSTOMIZATION, IClientPropertyValue.SETTING_PITCH_CUSTOM);
			getClient().saveUserSettings(true);
		}
		if (source == fPitchWeatherOnMenuItem) {
			getClient().setProperty(IClientProperty.SETTING_PITCH_WEATHER, IClientPropertyValue.SETTING_PITCH_WEATHER_ON);
			getClient().saveUserSettings(true);
		}
		if (source == fPitchWeatherOffMenuItem) {
			getClient().setProperty(IClientProperty.SETTING_PITCH_WEATHER, IClientPropertyValue.SETTING_PITCH_WEATHER_OFF);
			getClient().saveUserSettings(true);
		}
		if (source == fRangeGridAlwaysOnMenuItem) {
			getClient().setProperty(IClientProperty.SETTING_RANGEGRID, IClientPropertyValue.SETTING_RANGEGRID_ALWAYS_ON);
			getClient().saveUserSettings(false);
		}
		if (source == fRestoreDefaultsMenuItem) {
			try {
				getClient().loadProperties();
			} catch (IOException pIoE) {
				throw new FantasyFootballException(pIoE);
			}
			refresh();
			getClient().saveUserSettings(true);
		}
		if (source == fGameReplayMenuItem) {
			fGameReplayMenuItem.setText(replayer.isReplaying() ? _REPLAY_MODE_ON : _REPLAY_MODE_OFF);
			getClient().getClientState().actionKeyPressed(ActionKey.MENU_REPLAY);
		}
		if (source == fGameConcessionMenuItem) {
			getClient().getCommunication().sendConcedeGame(ConcedeGameStatus.REQUESTED);
		}
	}

	public void changeState(ClientStateId pStateId) {
		Game game = getClient().getGame();
		if (pStateId == ClientStateId.SETUP) {
			boolean setupEnabled = (game.getTurnMode() != TurnMode.QUICK_SNAP);
			fLoadSetupMenuItem.setEnabled(setupEnabled);
			fSaveSetupMenuItem.setEnabled(setupEnabled);
			fRestoreDefaultsMenuItem.setEnabled(true);
		} else {
			fLoadSetupMenuItem.setEnabled(false);
			fSaveSetupMenuItem.setEnabled(false);
			fSoundOnMenuItem.setEnabled(true);
			fSoundMuteSpectatorsMenuItem.setEnabled(true);
			fSoundOffMenuItem.setEnabled(true);
			fRestoreDefaultsMenuItem.setEnabled(true);
		}
	}

	public void dialogClosed(IDialog pDialog) {
		pDialog.hideDialog();
		if (pDialog.getId() == DialogId.SOUND_VOLUME) {
			DialogSoundVolume volumeDialog = (DialogSoundVolume) pDialog;
			getClient().setProperty(IClientProperty.SETTING_SOUND_VOLUME, Integer.toString(volumeDialog.getVolume()));
			getClient().saveUserSettings(true);
		}
		fDialogShown = null;
	}

	public void showDialog(IDialog pDialog) {
		if (fDialogShown != null) {
			fDialogShown.hideDialog();
		}
		fDialogShown = pDialog;
		fDialogShown.showDialog(this);
	}

	public void updateGameOptions() {
		fGameOptionsMenu.removeAll();
		IGameOption[] gameOptions = getClient().getGame().getOptions().getOptions();
		Arrays.sort(gameOptions, Comparator.comparing(pO -> pO.getId().getName()));
		int optionsAdded = 0;
		if (getClient().getGame().isTesting()) {
			JMenuItem optionItem = new JMenuItem(
				"* Game is in TEST mode. No results will be uploaded. See help for available test commands.");
			fGameOptionsMenu.add(optionItem);
			optionsAdded++;
		}
		for (IGameOption option : gameOptions) {
			if (option.isChanged() && (option.getId() != GameOptionId.TEST_MODE)
				&& StringTool.isProvided(option.getDisplayMessage())) {
				JMenuItem optionItem = new JMenuItem("* " + option.getDisplayMessage());
				fGameOptionsMenu.add(optionItem);
				optionsAdded++;
			}
		}
		if (optionsAdded > 0) {
			StringBuilder menuText = new StringBuilder().append(optionsAdded);
			if (optionsAdded > 1) {
				menuText.append(" Game Options");
			} else {
				menuText.append(" Game Option");
			}
			fGameOptionsMenu.setText(menuText.toString());
			fGameOptionsMenu.setEnabled(true);
		} else {
			fGameOptionsMenu.setText("No Game Options");
			fGameOptionsMenu.setEnabled(false);
		}
	}

	public void updateInducements() {

		boolean refreshNecessary = false;
		Game game = getClient().getGame();

		InducementSet inducementSetHome = game.getTurnDataHome().getInducementSet();
		int totalInducementHome = inducementSetHome.totalInducements();
		if ((fCurrentInducementTotalHome < 0) || (fCurrentInducementTotalHome != totalInducementHome)) {
			fCurrentInducementTotalHome = totalInducementHome;
			refreshNecessary = true;
		}
		int usedCardsHome = inducementSetHome.getDeactivatedCards().length + inducementSetHome.getActiveCards().length;
		if (usedCardsHome != fCurrentUsedCardsHome) {
			fCurrentUsedCardsHome = usedCardsHome;
			refreshNecessary = true;
		}

		InducementSet inducementSetAway = game.getTurnDataAway().getInducementSet();
		int totalInducementAway = inducementSetAway.totalInducements();
		if ((fCurrentInducementTotalAway < 0) || (fCurrentInducementTotalAway != totalInducementAway)) {
			fCurrentInducementTotalAway = totalInducementAway;
			refreshNecessary = true;
		}
		int usedCardsAway = inducementSetAway.getDeactivatedCards().length + inducementSetAway.getActiveCards().length;
		if (usedCardsAway != fCurrentUsedCardsAway) {
			fCurrentUsedCardsAway = usedCardsAway;
			refreshNecessary = true;
		}

		if (refreshNecessary) {

			JMenu fInducementsHomeMenu;
			JMenu fInducementsAwayMenu;
			fInducementsMenu.removeAll();

			if (fCurrentInducementTotalHome > 0) {
				fInducementsHomeMenu = new JMenu(totalInducementHome + " Home Team");
				fInducementsHomeMenu.setForeground(Color.RED);
				fInducementsHomeMenu.setMnemonic(KeyEvent.VK_H);
				fInducementsMenu.add(fInducementsHomeMenu);
				addInducements(fInducementsHomeMenu, inducementSetHome);
			}

			if (fCurrentInducementTotalAway > 0) {
				fInducementsAwayMenu = new JMenu(totalInducementAway + " Away Team");
				fInducementsAwayMenu.setForeground(Color.BLUE);
				fInducementsAwayMenu.setMnemonic(KeyEvent.VK_A);
				fInducementsMenu.add(fInducementsAwayMenu);
				addInducements(fInducementsAwayMenu, inducementSetAway);
			}

			if ((fCurrentInducementTotalHome + fCurrentInducementTotalAway) > 0) {
				StringBuilder menuText = new StringBuilder().append(fCurrentInducementTotalHome + fCurrentInducementTotalAway);
				if ((fCurrentInducementTotalHome + fCurrentInducementTotalAway) > 1) {
					menuText.append(" Inducements");
				} else {
					menuText.append(" Inducement");
				}
				fInducementsMenu.setText(menuText.toString());
				fInducementsMenu.setEnabled(true);
			} else {
				fInducementsMenu.setText("No Inducements");
				fInducementsMenu.setEnabled(false);
			}

		}

	}

	private void updateActiveCards() {

		boolean refreshNecessary = false;
		Game game = getClient().getGame();

		Card[] cardsHome = game.getTurnDataHome().getInducementSet().getActiveCards();
		if ((fCurrentActiveCardsHome == null) || (cardsHome.length != fCurrentActiveCardsHome.length)) {
			fCurrentActiveCardsHome = cardsHome;
			refreshNecessary = true;
		}

		Card[] cardsAway = game.getTurnDataAway().getInducementSet().getActiveCards();
		if ((fCurrentActiveCardsAway == null) || (cardsAway.length != fCurrentActiveCardsAway.length)) {
			fCurrentActiveCardsAway = cardsAway;
			refreshNecessary = true;
		}

		if (refreshNecessary) {

			fActiveCardsMenu.removeAll();

			if (ArrayTool.isProvided(fCurrentActiveCardsHome)) {
				JMenu fActiveCardsHomeMenu = new JMenu(fCurrentActiveCardsHome.length + " Home Team");
				fActiveCardsHomeMenu.setForeground(Color.RED);
				fActiveCardsHomeMenu.setMnemonic(KeyEvent.VK_H);
				fActiveCardsMenu.add(fActiveCardsHomeMenu);
				addActiveCards(fActiveCardsHomeMenu, fCurrentActiveCardsHome);
			}

			if (ArrayTool.isProvided(fCurrentActiveCardsAway)) {
				JMenu fActiveCardsAwayMenu = new JMenu(fCurrentActiveCardsAway.length + " Away Team");
				fActiveCardsAwayMenu.setForeground(Color.BLUE);
				fActiveCardsAwayMenu.setMnemonic(KeyEvent.VK_A);
				fActiveCardsMenu.add(fActiveCardsAwayMenu);
				addActiveCards(fActiveCardsAwayMenu, fCurrentActiveCardsAway);
			}

			int currentActiveCardsHomeLength = ArrayTool.isProvided(fCurrentActiveCardsHome) ? fCurrentActiveCardsHome.length
					: 0;
			int currentActiveCardsAwayLength = ArrayTool.isProvided(fCurrentActiveCardsAway) ? fCurrentActiveCardsAway.length
					: 0;

			if ((currentActiveCardsHomeLength + currentActiveCardsAwayLength) > 0) {
				StringBuilder menuText = new StringBuilder()
						.append(currentActiveCardsHomeLength + currentActiveCardsAwayLength);
				if ((currentActiveCardsHomeLength + currentActiveCardsAwayLength) > 1) {
					menuText.append(" Active Cards");
				} else {
					menuText.append(" Active Card");
				}
				fActiveCardsMenu.setText(menuText.toString());
				fActiveCardsMenu.setEnabled(true);
			} else {
				fActiveCardsMenu.setText("No Active Cards");
				fActiveCardsMenu.setEnabled(false);
			}

		}

	}

	private void addActiveCards(JMenu pCardsMenu, Card[] pCards) {
		Game game = getClient().getGame();
		Arrays.sort(pCards, Card.createComparator());
		Icon cardIcon = new ImageIcon(
				getClient().getUserInterface().getIconCache().getIconByProperty(IIconProperty.SIDEBAR_OVERLAY_PLAYER_CARD));
		for (Card card : pCards) {
			Player<?> player = null;
			if (card.getTarget().isPlayedOnPlayer()) {
				player = game.getFieldModel().findPlayer(card);
			}
			StringBuilder cardText = new StringBuilder();
			cardText.append("<html>");
			cardText.append("<b>").append(card.getName()).append("</b>");
			if (player != null) {
				cardText.append("<br>").append("Played on ").append(player.getName());
			}
			cardText.append("<br>").append(card.getHtmlDescription());
			cardText.append("</html>");
			if (player != null) {
				addPlayerMenuItem(pCardsMenu, player, cardText.toString());
			} else {
				JMenuItem cardMenuItem = new JMenuItem(cardText.toString(), cardIcon);
				pCardsMenu.add(cardMenuItem);
			}
		}
	}

	private void updatePrayers() {

		boolean refreshNecessary = false;
		Game game = getClient().getGame();

		Set<Prayer> prayersHome = game.getTurnDataHome().getInducementSet().getPrayers();
		if (prayersHome.size() != currentPrayersHome.size()) {
			PrayerFactory prayerFactory = game.getFactory(FactoryType.Factory.PRAYER);
			currentPrayersHome.clear();
			currentPrayersHome.addAll(prayerFactory.sort(prayersHome));
			refreshNecessary = true;
		}

		Set<Prayer> prayersAway = game.getTurnDataAway().getInducementSet().getPrayers();
		if (prayersAway.size() != currentPrayersAway.size()) {
			PrayerFactory prayerFactory = game.getFactory(FactoryType.Factory.PRAYER);
			currentPrayersAway.clear();
			currentPrayersAway.addAll(prayerFactory.sort(prayersAway));
			refreshNecessary = true;
		}

		if (refreshNecessary) {

			prayersMenu.removeAll();

			if (!currentPrayersHome.isEmpty()) {
				JMenu prayersHomeMenu = new JMenu(currentPrayersHome.size() + " Home Team");
				prayersHomeMenu.setForeground(Color.RED);
				prayersHomeMenu.setMnemonic(KeyEvent.VK_H);
				prayersMenu.add(prayersHomeMenu);
				addPrayers(prayersHomeMenu, currentPrayersHome);
			}

			if (!currentPrayersAway.isEmpty()) {
				JMenu prayersAwayMenu = new JMenu(currentPrayersAway.size() + " Away Team");
				prayersAwayMenu.setForeground(Color.BLUE);
				prayersAwayMenu.setMnemonic(KeyEvent.VK_A);
				prayersMenu.add(prayersAwayMenu);
				addPrayers(prayersAwayMenu, currentPrayersAway);
			}

			int totalPrayers = currentPrayersHome.size() + currentPrayersAway.size();
			if (totalPrayers > 0) {
				StringBuilder menuText = new StringBuilder()
					.append(totalPrayers);
				if (totalPrayers > 1) {
					menuText.append(" Prayers");
				} else {
					menuText.append(" Prayer");
				}
				prayersMenu.setText(menuText.toString());
				prayersMenu.setEnabled(true);
			} else {
				prayersMenu.setText("No Prayers");
				prayersMenu.setEnabled(false);
			}
		}
	}

	private void addPrayers(JMenu prayerMenu, List<Prayer> prayers) {
		for (Prayer prayer : prayers) {
			String text = "<html>" +
				"<b>" + prayer.getName() + "</b>" +
				"<br>" + prayer.getDuration().getDescription() + ": " + prayer.getDescription() +
				"</html>";
			JMenuItem menuItem = new JMenuItem(text);
			prayerMenu.add(menuItem);
		}
	}

	private void addInducements(JMenu pInducementMenu, InducementSet pInducementSet) {
		Inducement[] inducements = pInducementSet.getInducements();
		Arrays.sort(inducements, Comparator.comparing(pInducement -> pInducement.getType().getName()));
		for (Inducement inducement : inducements) {
			if (!Usage.EXCLUDE_FROM_RESULT.contains(inducement.getType().getUsage())) {
				if (inducement.getValue() > 0) {
					StringBuilder inducementText = new StringBuilder();
					inducementText.append(inducement.getValue()).append(" ");
					if (inducement.getValue() > 1) {
						inducementText.append(inducement.getType().getPlural());
					} else {
						inducementText.append(inducement.getType().getSingular());
					}
					JMenuItem inducementItem = new JMenuItem(inducementText.toString());
					pInducementMenu.add(inducementItem);
				}
			}
		}

		Game game = getClient().getGame();
		Team team = pInducementSet.getTurnData().isHomeData() ? game.getTeamHome() : game.getTeamAway();
		List<Player<?>> starPlayers = new ArrayList<>();
		for (Player<?> player : team.getPlayers()) {
			if (player.getPlayerType() == PlayerType.STAR) {
				starPlayers.add(player);
			}
		}
		if (starPlayers.size() > 0) {
			StringBuilder starPlayerMenuText = new StringBuilder();
			starPlayerMenuText.append(starPlayers.size());
			if (starPlayers.size() == 1) {
				starPlayerMenuText.append(" Star Player");
			} else {
				starPlayerMenuText.append(" Star Players");
			}
			JMenu starPlayerMenu = new JMenu(starPlayerMenuText.toString());
			pInducementMenu.add(starPlayerMenu);
			for (Player<?> player : starPlayers) {
				addPlayerMenuItem(starPlayerMenu, player, player.getName());
			}
		}

		List<Player<?>> mercenaries = new ArrayList<>();
		for (Player<?> player : team.getPlayers()) {
			if (player.getPlayerType() == PlayerType.MERCENARY) {
				mercenaries.add(player);
			}
		}
		if (mercenaries.size() > 0) {
			StringBuilder mercenaryMenuText = new StringBuilder();
			mercenaryMenuText.append(mercenaries.size());
			if (mercenaries.size() == 1) {
				mercenaryMenuText.append(" Mercenary");
			} else {
				mercenaryMenuText.append(" Mercenaries");
			}
			JMenu mercenaryMenu = new JMenu(mercenaryMenuText.toString());
			pInducementMenu.add(mercenaryMenu);
			for (Player<?> player : mercenaries) {
				addPlayerMenuItem(mercenaryMenu, player, player.getName());
			}
		}

		UserInterface userInterface = getClient().getUserInterface();
		Map<CardType, List<Card>> cardMap = buildCardMap(pInducementSet);
		for (CardType type : cardMap.keySet()) {
			List<Card> cardList = cardMap.get(type);
			StringBuilder cardTypeText = new StringBuilder();
			cardTypeText.append(cardList.size()).append(" ");
			if (cardList.size() > 1) {
				cardTypeText.append(type.getInducementNameMultiple());
			} else {
				cardTypeText.append(type.getInducementNameSingle());
			}
			int available = 0;
			for (Card card : cardList) {
				if (pInducementSet.isAvailable(card)) {
					available++;
				}
			}
			cardTypeText.append(" (");
			cardTypeText.append((available > 0) ? available : "None");
			cardTypeText.append(" available)");
			if (pInducementSet.getTurnData().isHomeData() && (getClient().getMode() == ClientMode.PLAYER)) {
				JMenu cardMenu = new JMenu(cardTypeText.toString());
				pInducementMenu.add(cardMenu);
				ImageIcon cardIcon = new ImageIcon(
						userInterface.getIconCache().getIconByProperty(IIconProperty.SIDEBAR_OVERLAY_PLAYER_CARD));
				for (Card card : cardList) {
					if (pInducementSet.isAvailable(card)) {
						String cardText = "<html>" +
							"<b>" + card.getName() + "</b>" +
							"<br>" + card.getHtmlDescriptionWithPhases() +
							"</html>";
						JMenuItem cardItem = new JMenuItem(cardText, cardIcon);
						cardMenu.add(cardItem);
					}
				}
			} else {
				JMenuItem cardItem = new JMenuItem(cardTypeText.toString());
				pInducementMenu.add(cardItem);
			}
		}

	}

	public void updateMissingPlayers() {
		Game game = getClient().getGame();
		fMissingPlayersMenu.removeAll();
		int nrOfEntries = 0;
		for (int i = 0; i < BoxComponent.MAX_BOX_ELEMENTS; i++) {
			Player<?> player = game.getFieldModel().getPlayer(new FieldCoordinate(FieldCoordinate.MNG_HOME_X, i));
			if (player != null) {
				addMissingPlayerMenuItem(player);
				nrOfEntries++;
			} else {
				break;
			}
		}
		for (int i = 0; i < BoxComponent.MAX_BOX_ELEMENTS; i++) {
			Player<?> player = game.getFieldModel().getPlayer(new FieldCoordinate(FieldCoordinate.MNG_AWAY_X, i));
			if (player != null) {
				addMissingPlayerMenuItem(player);
				nrOfEntries++;
			} else {
				break;
			}
		}
		StringBuilder menuText = new StringBuilder();
		if (nrOfEntries > 0) {
			menuText.append(nrOfEntries);
			if (nrOfEntries > 1) {
				menuText.append(" Missing Players");
			} else {
				menuText.append(" Missing Player");
			}
			fMissingPlayersMenu.setEnabled(true);
		} else {
			menuText.append("No Missing Players");
			fMissingPlayersMenu.setEnabled(false);
		}
		fMissingPlayersMenu.setText(menuText.toString());
	}

	private void addMissingPlayerMenuItem(Player<?> pPlayer) {
		if (pPlayer == null) {
			return;
		}
		StringBuilder playerText = new StringBuilder();
		playerText.append("<html>").append(pPlayer.getName());
		if (pPlayer.getRecoveringInjury() != null) {
			playerText.append("<br>").append(pPlayer.getRecoveringInjury().getRecovery());
		} else {
			Game game = getClient().getGame();
			PlayerResult playerResult = game.getGameResult().getPlayerResult(pPlayer);
			if (playerResult.getSendToBoxReason() != null) {
				playerText.append("<br>").append(playerResult.getSendToBoxReason().getReason());
			}
		}
		playerText.append("</html>");
		addPlayerMenuItem(fMissingPlayersMenu, pPlayer, playerText.toString());
	}

	private void addPlayerMenuItem(JMenu pPlayersMenu, Player<?> pPlayer, String pText) {
		if ((pPlayer == null) || !StringTool.isProvided(pText)) {
			return;
		}
		UserInterface userInterface = getClient().getUserInterface();
		PlayerIconFactory playerIconFactory = userInterface.getPlayerIconFactory();
		Icon playerIcon = new ImageIcon(playerIconFactory.getIcon(getClient(), pPlayer));
		JMenuItem playersMenuItem = new JMenuItem(pText, playerIcon);
		playersMenuItem.addMouseListener(new MenuPlayerMouseListener(pPlayer));
		pPlayersMenu.add(playersMenuItem);
	}

	private Map<CardType, List<Card>> buildCardMap(InducementSet pInducementSet) {
		Card[] allCards = pInducementSet.getAllCards();

		return Arrays.stream(allCards).collect(Collectors.groupingBy(Card::getType));
	}

}
