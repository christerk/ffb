package com.fumbbl.ffb.client.ui;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.ConcedeGameStatus;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IClientProperty;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.ClientData;
import com.fumbbl.ffb.client.ClientReplayer;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FontCache;
import com.fumbbl.ffb.client.PlayerIconFactory;
import com.fumbbl.ffb.client.StyleProvider;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.dialog.DialogAbout;
import com.fumbbl.ffb.client.dialog.DialogAutoMarking;
import com.fumbbl.ffb.client.dialog.DialogChangeList;
import com.fumbbl.ffb.client.dialog.DialogChatCommands;
import com.fumbbl.ffb.client.dialog.DialogGameStatistics;
import com.fumbbl.ffb.client.dialog.DialogKeyBindings;
import com.fumbbl.ffb.client.dialog.DialogScalingFactor;
import com.fumbbl.ffb.client.dialog.DialogSelectLocalStoredProperties;
import com.fumbbl.ffb.client.dialog.DialogSoundVolume;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.client.dialog.IDialogCloseListener;
import com.fumbbl.ffb.client.ui.swing.JMenu;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.ui.swing.JRadioButtonMenuItem;
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
import javax.swing.JColorChooser;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.fumbbl.ffb.CommonProperty.*;

/**
 * @author Kalimar
 */
public class GameMenuBar extends JMenuBar implements ActionListener, IDialogCloseListener {

	private static final String _REPLAY_MODE_ON = "Replay Mode";
	private static final String _REPLAY_MODE_OFF = "Spectator Mode";

	private final FantasyFootballClient fClient;

	private JMenuItem fGameReplayMenuItem;
	private JMenuItem fGameConcessionMenuItem;
	private JMenuItem fGameStatisticsMenuItem;

	private JMenuItem fLoadSetupMenuItem;
	private JMenuItem fSaveSetupMenuItem;

	private JMenuItem fRestoreDefaultsMenuItem;
	private JMenu playerMarkingMenu;

	private JMenuItem fSoundVolumeItem;
	private JRadioButtonMenuItem fSoundOnMenuItem;
	private JRadioButtonMenuItem fSoundMuteSpectatorsMenuItem;
	private JRadioButtonMenuItem fSoundOffMenuItem;

	private JMenuItem scalingItem;

	private JMenuItem localPropertiesItem;
	private JRadioButtonMenuItem fIconsAbstract;
	private JRadioButtonMenuItem fIconsRosterOpponent;
	private JRadioButtonMenuItem fIconsRosterBoth;
	private JRadioButtonMenuItem fIconsTeam;

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

	private JRadioButtonMenuItem fCustomPitchMenuItem;
	private JRadioButtonMenuItem fDefaultPitchMenuItem;
	private JRadioButtonMenuItem fBasicPitchMenuItem;

	private JRadioButtonMenuItem fPitchMarkingsOnMenuItem;
	private JRadioButtonMenuItem fPitchMarkingsOffMenuItem;

	private JRadioButtonMenuItem pitchLandscapeMenuItem;
	private JRadioButtonMenuItem pitchPortraitMenuItem;
	private JRadioButtonMenuItem layoutSquareMenuItem;

	private JRadioButtonMenuItem fTeamLogoBothMenuItem;
	private JRadioButtonMenuItem fTeamLogoOwnMenuItem;
	private JRadioButtonMenuItem fTeamLogoNoneMenuItem;

	private JRadioButtonMenuItem fPitchWeatherOnMenuItem;
	private JRadioButtonMenuItem fPitchWeatherOffMenuItem;

	private JRadioButtonMenuItem fRangeGridAlwaysOnMenuItem;
	private JRadioButtonMenuItem fRangeGridToggleMenuItem;

	private JRadioButtonMenuItem markUsedPlayersDefaultMenuItem;
	private JRadioButtonMenuItem markUsedPlayersCheckIconGreenMenuItem;

	private JRadioButtonMenuItem swapTeamColorsOffMenuItem;
	private JRadioButtonMenuItem swapTeamColorsOnMenuItem;

	private JRadioButtonMenuItem playersMarkingManualMenuItem;
	private JRadioButtonMenuItem playersMarkingAutoMenuItem;

	private JRadioButtonMenuItem reRollBallAndChainNeverMenuItem;
	private JRadioButtonMenuItem reRollBallAndChainNoOpponentMenuItem;
	private JRadioButtonMenuItem reRollBallAndChainTeamMateMenuItem;
	private JRadioButtonMenuItem reRollBallAndChainAlwaysMenuItem;

	private JMenuItem chatBackground;
	private JMenuItem logBackground;
	private JMenuItem textFontColor;
	private JMenuItem awayFontColor;
	private JMenuItem homeFontColor;
	private JMenuItem specFontColor;
	private JMenuItem adminFontColor;
	private JMenuItem devFontColor;
	private JMenuItem frameFontColor;
	private JMenuItem frameFontShadowColor;
	private JMenuItem inputFontColor;

	private JRadioButtonMenuItem frameBackgroundIcons;
	private JRadioButtonMenuItem frameBackgroundColor;

	private JMenu fMissingPlayersMenu;

	private JMenu fInducementsMenu;

	private JMenu fActiveCardsMenu;

	private JMenu prayersMenu;

	private JMenu fGameOptionsMenu;

	private JMenuItem fAboutMenuItem;
	private JMenuItem fChatCommandsMenuItem;
	private JMenuItem fKeyBindingsMenuItem;
	private JMenuItem changeListItem;
	private JMenuItem autoMarkingItem;
	private JMenu reRollBallAndChainPanelMenu;

	private JMenuItem resetColors;
	private JMenuItem resetBackgroundColors;
	private JMenuItem resetFontColors;

	private IDialog fDialogShown;

	private int fCurrentInducementTotalHome;
	private int fCurrentUsedCardsHome;
	private int fCurrentInducementTotalAway;
	private int fCurrentUsedCardsAway;

	private Card[] fCurrentActiveCardsHome;
	private Card[] fCurrentActiveCardsAway;

	private final List<Prayer> currentPrayersHome = new ArrayList<>();
	private final List<Prayer> currentPrayersAway = new ArrayList<>();
	private final Map<CommonProperty, JMenu> exposedMenus = new HashMap<>();

	private final StyleProvider styleProvider;
	private final DimensionProvider dimensionProvider;

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

	public GameMenuBar(FantasyFootballClient pClient, DimensionProvider dimensionProvider, StyleProvider styleProvider, FontCache fontCache) {

		setFont(fontCache.font(Font.PLAIN, 12));

		fClient = pClient;
		this.styleProvider = styleProvider;
		this.dimensionProvider = dimensionProvider;

		init();

	}

	private void createHelpMenu() {
		JMenu fHelpMenu = new JMenu(dimensionProvider, "Help");
		fHelpMenu.setMnemonic(KeyEvent.VK_H);
		add(fHelpMenu);

		fAboutMenuItem = new JMenuItem(dimensionProvider, "About", KeyEvent.VK_A);
		fAboutMenuItem.addActionListener(this);
		fHelpMenu.add(fAboutMenuItem);

		fChatCommandsMenuItem = new JMenuItem(dimensionProvider, "Chat Commands", KeyEvent.VK_C);
		fChatCommandsMenuItem.addActionListener(this);
		fHelpMenu.add(fChatCommandsMenuItem);

		changeListItem = new JMenuItem(dimensionProvider, "What's new?", KeyEvent.VK_L);
		changeListItem.addActionListener(this);
		fHelpMenu.add(changeListItem);

		autoMarkingItem = new JMenuItem(dimensionProvider, "Automarking Panel", KeyEvent.VK_L);
		autoMarkingItem.addActionListener(this);
		fHelpMenu.add(autoMarkingItem);

		fKeyBindingsMenuItem = new JMenuItem(dimensionProvider, "Key Bindings", KeyEvent.VK_K);
		fKeyBindingsMenuItem.addActionListener(this);
		fHelpMenu.add(fKeyBindingsMenuItem);
	}

	private void createGameStatusMenus() {
		fMissingPlayersMenu = new JMenu(dimensionProvider, "Missing Players");
		fMissingPlayersMenu.setMnemonic(KeyEvent.VK_M);
		fMissingPlayersMenu.setEnabled(false);
		add(fMissingPlayersMenu);

		fInducementsMenu = new JMenu(dimensionProvider, "Inducements");
		fInducementsMenu.setMnemonic(KeyEvent.VK_I);
		fInducementsMenu.setEnabled(false);
		add(fInducementsMenu);

		fActiveCardsMenu = new JMenu(dimensionProvider, "Active Cards");
		fActiveCardsMenu.setMnemonic(KeyEvent.VK_C);
		fActiveCardsMenu.setEnabled(false);
		add(fActiveCardsMenu);

		prayersMenu = new JMenu(dimensionProvider, "Prayers");
		prayersMenu.setMnemonic(KeyEvent.VK_P);
		prayersMenu.setEnabled(false);
		add(prayersMenu);

		fGameOptionsMenu = new JMenu(dimensionProvider, "Game Options");
		fGameOptionsMenu.setMnemonic(KeyEvent.VK_O);
		fGameOptionsMenu.setEnabled(false);
		add(fGameOptionsMenu);
	}

	private void createUserSettingsMenu() {
		JMenu fUserSettingsMenu = new JMenu(dimensionProvider, "User Settings");
		fUserSettingsMenu.setMnemonic(KeyEvent.VK_U);
		add(fUserSettingsMenu);

		createSoundMenu(fUserSettingsMenu);
		createIconsMenu(fUserSettingsMenu);
		createAutomoveMenu(fUserSettingsMenu);
		createBlitzPanelMenu(fUserSettingsMenu);
		createGazePanelMenu(fUserSettingsMenu);
		createRightClickMenu(fUserSettingsMenu);
		createBallAndChainMenu(fUserSettingsMenu);
		createPitchMenu(fUserSettingsMenu);
		createRangegridMenu(fUserSettingsMenu);
		createMarkUsedPlayerMenu(fUserSettingsMenu);
		createMarkingMenu(fUserSettingsMenu);
		createBackgroundMenu(fUserSettingsMenu);
		createFontMenu(fUserSettingsMenu);
		createScaleItem(fUserSettingsMenu);
		createLocalPropertiesItem(fUserSettingsMenu);

		fUserSettingsMenu.addSeparator();
		createRestoreMenu(fUserSettingsMenu);
	}

	private void createRestoreMenu(JMenu fUserSettingsMenu) {
		fRestoreDefaultsMenuItem = new JMenuItem(dimensionProvider, "Restore Defaults");
		fRestoreDefaultsMenuItem.addActionListener(this);
		fRestoreDefaultsMenuItem.setEnabled(false);
		fUserSettingsMenu.add(fRestoreDefaultsMenuItem);

		resetColors = new JMenuItem(dimensionProvider, "Reset all colors");
		resetColors.addActionListener(this);
		resetColors.setEnabled(true);
		fUserSettingsMenu.add(resetColors);

		resetBackgroundColors = new JMenuItem(dimensionProvider, "Reset background colors");
		resetBackgroundColors.addActionListener(this);
		resetBackgroundColors.setEnabled(true);
		fUserSettingsMenu.add(resetBackgroundColors);

		resetFontColors = new JMenuItem(dimensionProvider, "Reset font colors");
		resetFontColors.addActionListener(this);
		resetFontColors.setEnabled(true);
		fUserSettingsMenu.add(resetFontColors);
	}

	private void createBackgroundMenu(JMenu fUserSettingsMenu) {
		JMenu backgroundStyles = new JMenu(dimensionProvider, "Background styles");
		backgroundStyles.setMnemonic(KeyEvent.VK_B);
		fUserSettingsMenu.add(backgroundStyles);
		addColorItem(SETTING_BACKGROUND_CHAT, styleProvider.getChatBackground(), backgroundStyles, (item) -> chatBackground = item);
		addColorItem(SETTING_BACKGROUND_LOG, styleProvider.getLogBackground(), backgroundStyles, (item) -> logBackground = item);
		backgroundStyles.add(createFrameBackgroundMenu());
	}

	private void createFontMenu(JMenu userSettings) {
		JMenu fontStyles = new JMenu(dimensionProvider, "Font colors");
		fontStyles.setMnemonic(KeyEvent.VK_F);
		userSettings.add(fontStyles);
		addColorItem(SETTING_FONT_COLOR_TEXT, styleProvider.getText(), fontStyles, (item) -> textFontColor = item);
		addColorItem(SETTING_FONT_COLOR_AWAY, styleProvider.getAwayUnswapped(), fontStyles, (item) -> awayFontColor = item);
		addColorItem(SETTING_FONT_COLOR_HOME, styleProvider.getHomeUnswapped(), fontStyles, (item) -> homeFontColor = item);
		addColorItem(SETTING_FONT_COLOR_SPEC, styleProvider.getSpec(), fontStyles, (item) -> specFontColor = item);
		addColorItem(SETTING_FONT_COLOR_ADMIN, styleProvider.getAdmin(), fontStyles, (item) -> adminFontColor = item);
		addColorItem(SETTING_FONT_COLOR_DEV, styleProvider.getDev(), fontStyles, (item) -> devFontColor = item);
		addColorItem(SETTING_FONT_COLOR_INPUT, styleProvider.getInput(), fontStyles, (item) -> inputFontColor = item);
		addColorItem(SETTING_FONT_COLOR_FRAME, styleProvider.getFrame(), fontStyles, (item) -> frameFontColor = item);
		addColorItem(SETTING_FONT_COLOR_FRAME_SHADOW, styleProvider.getFrameShadow(), fontStyles, (item) -> frameFontShadowColor = item);
	}

	private void createMarkingMenu(JMenu fUserSettingsMenu) {
		playerMarkingMenu = new JMenu(dimensionProvider, SETTING_PLAYER_MARKING_TYPE);
		playerMarkingMenu.setMnemonic(KeyEvent.VK_L);
		fUserSettingsMenu.add(playerMarkingMenu);

		ButtonGroup playerMarkingGroup = new ButtonGroup();

		playersMarkingAutoMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Automatic");
		playersMarkingAutoMenuItem.addActionListener(this);
		playerMarkingGroup.add(playersMarkingAutoMenuItem);
		playerMarkingMenu.add(playersMarkingAutoMenuItem);

		playersMarkingManualMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Manual");
		playersMarkingManualMenuItem.addActionListener(this);
		playerMarkingGroup.add(playersMarkingManualMenuItem);
		playerMarkingMenu.add(playersMarkingManualMenuItem);
	}

	private void createMarkUsedPlayerMenu(JMenu fUserSettingsMenu) {
		JMenu markUsedPlayersMenu = new JMenu(dimensionProvider, SETTING_MARK_USED_PLAYERS);
		markUsedPlayersMenu.setMnemonic(KeyEvent.VK_M);
		fUserSettingsMenu.add(markUsedPlayersMenu);

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

	private void createRangegridMenu(JMenu fUserSettingsMenu) {
		JMenu fRangeGridMenu = new JMenu(dimensionProvider, SETTING_RANGEGRID);
		fRangeGridMenu.setMnemonic(KeyEvent.VK_R);
		fUserSettingsMenu.add(fRangeGridMenu);

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

	private void createPitchMenu(JMenu fUserSettingsMenu) {
		JMenu fPitchMenu = new JMenu(dimensionProvider, "Pitch");
		fPitchMenu.setMnemonic(KeyEvent.VK_P);
		fUserSettingsMenu.add(fPitchMenu);

		JMenu fPitchCustomizationMenu = new JMenu(dimensionProvider, SETTING_PITCH_CUSTOMIZATION);
		fPitchCustomizationMenu.setMnemonic(KeyEvent.VK_C);
		fPitchMenu.add(fPitchCustomizationMenu);

		ButtonGroup pitchCustomGroup = new ButtonGroup();

		fCustomPitchMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Use Custom Pitch");
		fCustomPitchMenuItem.addActionListener(this);
		pitchCustomGroup.add(fCustomPitchMenuItem);
		fPitchCustomizationMenu.add(fCustomPitchMenuItem);

		fDefaultPitchMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Use Default Pitch");
		fDefaultPitchMenuItem.addActionListener(this);
		pitchCustomGroup.add(fDefaultPitchMenuItem);
		fPitchCustomizationMenu.add(fDefaultPitchMenuItem);

		fBasicPitchMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Use Basic Pitch");
		fBasicPitchMenuItem.addActionListener(this);
		pitchCustomGroup.add(fBasicPitchMenuItem);
		fPitchCustomizationMenu.add(fBasicPitchMenuItem);

		JMenu fPitchMarkingsMenu = new JMenu(dimensionProvider, SETTING_PITCH_MARKINGS);
		fPitchMarkingsMenu.setMnemonic(KeyEvent.VK_M);
		fPitchMenu.add(fPitchMarkingsMenu);

		ButtonGroup tdDistanceGroup = new ButtonGroup();

		fPitchMarkingsOnMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Pitch Markings on");
		fPitchMarkingsOnMenuItem.addActionListener(this);
		tdDistanceGroup.add(fPitchMarkingsOnMenuItem);
		fPitchMarkingsMenu.add(fPitchMarkingsOnMenuItem);

		fPitchMarkingsOffMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Pitch Markings off");
		fPitchMarkingsOffMenuItem.addActionListener(this);
		tdDistanceGroup.add(fPitchMarkingsOffMenuItem);
		fPitchMarkingsMenu.add(fPitchMarkingsOffMenuItem);

		JMenu orientationMenu = new JMenu(dimensionProvider, SETTING_PITCH_ORIENTATION);
		orientationMenu.setMnemonic(KeyEvent.VK_O);
		fPitchMenu.add(orientationMenu);

		ButtonGroup orientationGroup = new ButtonGroup();

		pitchLandscapeMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Landscape");
		pitchLandscapeMenuItem.addActionListener(this);
		orientationGroup.add(pitchLandscapeMenuItem);
		orientationMenu.add(pitchLandscapeMenuItem);

		pitchPortraitMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Portrait");
		pitchPortraitMenuItem.addActionListener(this);
		orientationGroup.add(pitchPortraitMenuItem);
		orientationMenu.add(pitchPortraitMenuItem);

		layoutSquareMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Square");
		layoutSquareMenuItem.addActionListener(this);
		orientationGroup.add(layoutSquareMenuItem);
		orientationMenu.add(layoutSquareMenuItem);

		JMenu fTeamLogoMenu = new JMenu(dimensionProvider, SETTING_TEAM_LOGOS);
		fTeamLogoMenu.setMnemonic(KeyEvent.VK_T);
		fPitchMenu.add(fTeamLogoMenu);

		ButtonGroup teamLogoGroup = new ButtonGroup();

		fTeamLogoBothMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Show both Team-Logos");
		fTeamLogoBothMenuItem.addActionListener(this);
		teamLogoGroup.add(fTeamLogoBothMenuItem);
		fTeamLogoMenu.add(fTeamLogoBothMenuItem);

		fTeamLogoOwnMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Show my Team-Logo only");
		fTeamLogoOwnMenuItem.addActionListener(this);
		teamLogoGroup.add(fTeamLogoOwnMenuItem);
		fTeamLogoMenu.add(fTeamLogoOwnMenuItem);

		fTeamLogoNoneMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Show no Team-Logos");
		fTeamLogoNoneMenuItem.addActionListener(this);
		teamLogoGroup.add(fTeamLogoNoneMenuItem);
		fTeamLogoMenu.add(fTeamLogoNoneMenuItem);

		JMenu fPitchWeatherMenu = new JMenu(dimensionProvider, SETTING_PITCH_WEATHER);
		fPitchWeatherMenu.setMnemonic(KeyEvent.VK_W);
		fPitchMenu.add(fPitchWeatherMenu);

		ButtonGroup pitchWeatherGroup = new ButtonGroup();

		fPitchWeatherOnMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Change pitch with weather");
		fPitchWeatherOnMenuItem.addActionListener(this);
		pitchWeatherGroup.add(fPitchWeatherOnMenuItem);
		fPitchWeatherMenu.add(fPitchWeatherOnMenuItem);

		fPitchWeatherOffMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Always show nice weather pitch");
		fPitchWeatherOffMenuItem.addActionListener(this);
		pitchWeatherGroup.add(fPitchWeatherOffMenuItem);
		fPitchWeatherMenu.add(fPitchWeatherOffMenuItem);
	}

	private void createBallAndChainMenu(JMenu fUserSettingsMenu) {
		ButtonGroup reRollBallAndChainPanelGroup = new ButtonGroup();
		reRollBallAndChainPanelMenu = new JMenu(dimensionProvider, SETTING_RE_ROLL_BALL_AND_CHAIN);
		exposedMenus.put(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN, reRollBallAndChainPanelMenu);
		reRollBallAndChainPanelMenu.setMnemonic(KeyEvent.VK_B);
		fUserSettingsMenu.add(reRollBallAndChainPanelMenu);

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

	private void createRightClickMenu(JMenu fUserSettingsMenu) {
		ButtonGroup rightClickEndActionPanelGroup = new ButtonGroup();
		JMenu rightClickEndActionPanelMenu = new JMenu(dimensionProvider, SETTING_RIGHT_CLICK_END_ACTION);
		rightClickEndActionPanelMenu.setMnemonic(KeyEvent.VK_R);
		fUserSettingsMenu.add(rightClickEndActionPanelMenu);

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

	private void createGazePanelMenu(JMenu fUserSettingsMenu) {
		ButtonGroup gazeTargetPanelGroup = new ButtonGroup();
		JMenu gazeTargetPanelMenu = new JMenu(dimensionProvider, SETTING_GAZE_TARGET_PANEL);
		gazeTargetPanelMenu.setMnemonic(KeyEvent.VK_G);
		fUserSettingsMenu.add(gazeTargetPanelMenu);

		gazePanelOnMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Enable");
		gazePanelOnMenuItem.addActionListener(this);
		gazeTargetPanelGroup.add(gazePanelOnMenuItem);
		gazeTargetPanelMenu.add(gazePanelOnMenuItem);

		gazePanelOffMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Disable");
		gazePanelOffMenuItem.addActionListener(this);
		gazeTargetPanelGroup.add(gazePanelOffMenuItem);
		gazeTargetPanelMenu.add(gazePanelOffMenuItem);
	}

	private void createBlitzPanelMenu(JMenu fUserSettingsMenu) {
		ButtonGroup blitzTargetPanelGroup = new ButtonGroup();
		JMenu blitzTargetPanelMenu = new JMenu(dimensionProvider, SETTING_BLITZ_TARGET_PANEL);
		blitzTargetPanelMenu.setMnemonic(KeyEvent.VK_B);
		fUserSettingsMenu.add(blitzTargetPanelMenu);

		fBlitzPanelOnMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Enable");
		fBlitzPanelOnMenuItem.addActionListener(this);
		blitzTargetPanelGroup.add(fBlitzPanelOnMenuItem);
		blitzTargetPanelMenu.add(fBlitzPanelOnMenuItem);

		fBlitzPanelOffMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Disable");
		fBlitzPanelOffMenuItem.addActionListener(this);
		blitzTargetPanelGroup.add(fBlitzPanelOffMenuItem);
		blitzTargetPanelMenu.add(fBlitzPanelOffMenuItem);
	}

	private void createAutomoveMenu(JMenu fUserSettingsMenu) {
		JMenu fAutomoveMenu = new JMenu(dimensionProvider, SETTING_AUTOMOVE);
		fAutomoveMenu.setMnemonic(KeyEvent.VK_A);
		fUserSettingsMenu.add(fAutomoveMenu);

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

	private void createIconsMenu(JMenu fUserSettingsMenu) {
		JMenu fIconsMenu = new JMenu(dimensionProvider, SETTING_ICONS);
		fIconsMenu.setMnemonic(KeyEvent.VK_I);
		fUserSettingsMenu.add(fIconsMenu);

		ButtonGroup iconsGroup = new ButtonGroup();

		fIconsTeam = new JRadioButtonMenuItem(dimensionProvider, "Team icons");
		fIconsTeam.setMnemonic(KeyEvent.VK_T);
		fIconsTeam.addActionListener(this);
		iconsGroup.add(fIconsTeam);
		fIconsMenu.add(fIconsTeam);

		fIconsRosterOpponent = new JRadioButtonMenuItem(dimensionProvider, "Roster icons (Opponent)");
		fIconsRosterOpponent.setMnemonic(KeyEvent.VK_O);
		fIconsRosterOpponent.addActionListener(this);
		iconsGroup.add(fIconsRosterOpponent);
		fIconsMenu.add(fIconsRosterOpponent);

		fIconsRosterBoth = new JRadioButtonMenuItem(dimensionProvider, "Roster icons (Both)");
		fIconsRosterBoth.setMnemonic(KeyEvent.VK_B);
		fIconsRosterBoth.addActionListener(this);
		iconsGroup.add(fIconsRosterBoth);
		fIconsMenu.add(fIconsRosterBoth);

		fIconsAbstract = new JRadioButtonMenuItem(dimensionProvider, "Abstract icons");
		fIconsAbstract.setMnemonic(KeyEvent.VK_A);
		fIconsAbstract.addActionListener(this);
		iconsGroup.add(fIconsAbstract);
		fIconsMenu.add(fIconsAbstract);

		fIconsMenu.addSeparator();

		JMenu swapTeamColorsMenu = new JMenu(dimensionProvider, CommonProperty.SETTING_SWAP_TEAM_COLORS);
		swapTeamColorsMenu.setMnemonic(KeyEvent.VK_S);
		fIconsMenu.add(swapTeamColorsMenu);

		ButtonGroup swapTeamColorsGroup = new ButtonGroup();

		swapTeamColorsOffMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Off");
		swapTeamColorsOffMenuItem.setMnemonic(KeyEvent.VK_F);
		swapTeamColorsOffMenuItem.addActionListener(this);
		swapTeamColorsGroup.add(swapTeamColorsOffMenuItem);
		swapTeamColorsMenu.add(swapTeamColorsOffMenuItem);

		swapTeamColorsOnMenuItem = new JRadioButtonMenuItem(dimensionProvider, "On");
		swapTeamColorsOnMenuItem.setMnemonic(KeyEvent.VK_N);
		swapTeamColorsOnMenuItem.addActionListener(this);
		swapTeamColorsGroup.add(swapTeamColorsOnMenuItem);
		swapTeamColorsMenu.add(swapTeamColorsOnMenuItem);
	}

	private void createSoundMenu(JMenu fUserSettingsMenu) {
		JMenu fSoundMenu = new JMenu(dimensionProvider, SETTING_SOUND_MODE);
		fSoundMenu.setMnemonic(KeyEvent.VK_S);
		fUserSettingsMenu.add(fSoundMenu);

		fSoundVolumeItem = new JMenuItem(dimensionProvider, SETTING_SOUND_VOLUME.getValue());
		fSoundVolumeItem.setMnemonic(KeyEvent.VK_V);
		fSoundVolumeItem.addActionListener(this);
		fSoundMenu.add(fSoundVolumeItem);

		fSoundMenu.addSeparator();

		ButtonGroup soundGroup = new ButtonGroup();

		fSoundOnMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Sound on");
		fSoundOnMenuItem.addActionListener(this);
		soundGroup.add(fSoundOnMenuItem);
		fSoundMenu.add(fSoundOnMenuItem);

		fSoundMuteSpectatorsMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Mute spectators");
		fSoundMuteSpectatorsMenuItem.addActionListener(this);
		soundGroup.add(fSoundMuteSpectatorsMenuItem);
		fSoundMenu.add(fSoundMuteSpectatorsMenuItem);

		fSoundOffMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Sound off");
		fSoundOffMenuItem.addActionListener(this);
		soundGroup.add(fSoundOffMenuItem);
		fSoundMenu.add(fSoundOffMenuItem);
	}

	private void createScaleItem(JMenu fUserSettingsMenu) {
		scalingItem = new JMenuItem(dimensionProvider, SETTING_SCALE_FACTOR.getValue());
		scalingItem.setMnemonic(KeyEvent.VK_E);
		scalingItem.addActionListener(this);
		fUserSettingsMenu.add(scalingItem);
	}

	private void createLocalPropertiesItem(JMenu fUserSettingsMenu) {
		localPropertiesItem = new JMenuItem(dimensionProvider, SETTING_LOCAL_SETTINGS.getValue());
		localPropertiesItem.setMnemonic(KeyEvent.VK_L);
		localPropertiesItem.addActionListener(this);
		fUserSettingsMenu.add(localPropertiesItem);
	}

	private void createTeamSetupMenu() {
		JMenu fTeamSetupMenu = new JMenu(dimensionProvider, "Team Setup");
		fTeamSetupMenu.setMnemonic(KeyEvent.VK_T);
		add(fTeamSetupMenu);

		fLoadSetupMenuItem = new JMenuItem(dimensionProvider, "Load Setup", KeyEvent.VK_L);
		String menuSetupLoad = getClient().getProperty(IClientProperty.KEY_MENU_SETUP_LOAD);
		if (StringTool.isProvided(menuSetupLoad)) {
			fLoadSetupMenuItem.setAccelerator(KeyStroke.getKeyStroke(menuSetupLoad));
		}
		fLoadSetupMenuItem.addActionListener(this);
		fTeamSetupMenu.add(fLoadSetupMenuItem);

		fSaveSetupMenuItem = new JMenuItem(dimensionProvider, "Save Setup", KeyEvent.VK_S);
		String menuSetupSave = getClient().getProperty(IClientProperty.KEY_MENU_SETUP_SAVE);
		if (StringTool.isProvided(menuSetupSave)) {
			fSaveSetupMenuItem.setAccelerator(KeyStroke.getKeyStroke(menuSetupSave));
		}
		fSaveSetupMenuItem.addActionListener(this);
		fTeamSetupMenu.add(fSaveSetupMenuItem);
	}

	private void createGameMenu() {
		JMenu fGameMenu = new JMenu(dimensionProvider, "Game");
		fGameMenu.setMnemonic(KeyEvent.VK_G);
		add(fGameMenu);

		fGameReplayMenuItem = new JMenuItem(dimensionProvider, _REPLAY_MODE_ON, KeyEvent.VK_R);
		String keyMenuReplay = getClient().getProperty(IClientProperty.KEY_MENU_REPLAY);
		if (StringTool.isProvided(keyMenuReplay)) {
			fGameReplayMenuItem.setAccelerator(KeyStroke.getKeyStroke(keyMenuReplay));
		}
		fGameReplayMenuItem.addActionListener(this);
		fGameMenu.add(fGameReplayMenuItem);

		fGameConcessionMenuItem = new JMenuItem(dimensionProvider, "Concede Game", KeyEvent.VK_C);
		fGameConcessionMenuItem.addActionListener(this);
		fGameConcessionMenuItem.setEnabled(false);
		fGameMenu.add(fGameConcessionMenuItem);

		fGameStatisticsMenuItem = new JMenuItem(dimensionProvider, "Game Statistics", KeyEvent.VK_S);
		fGameStatisticsMenuItem.addActionListener(this);
		fGameStatisticsMenuItem.setEnabled(false);
		fGameMenu.add(fGameStatisticsMenuItem);
	}

	private static ColorIcon createColorIcon(Color chatBackgroundColor) {
		return new ColorIcon(20, 20, chatBackgroundColor);
	}

	private void addColorItem(CommonProperty title, Color color, JMenu parent, Consumer<JMenuItem> setter) {
		JMenuItem item = new JMenuItem(dimensionProvider, title.getValue(), createColorIcon(color));
		item.addActionListener(this);
		parent.add(item);
		setter.accept(item);
	}

	private JMenu createFrameBackgroundMenu() {

		JMenu menu = new JMenu(dimensionProvider, SETTING_BACKGROUND_FRAME);
		ButtonGroup group = new ButtonGroup();
		frameBackgroundIcons = new JRadioButtonMenuItem(dimensionProvider, "Graphics");
		frameBackgroundIcons.addActionListener(this);

		frameBackgroundColor = new JRadioButtonMenuItem(dimensionProvider, "Color", createColorIcon(styleProvider.getFrameBackground()));
		frameBackgroundColor.addActionListener(this);

		menu.add(frameBackgroundIcons);
		menu.add(frameBackgroundColor);
		group.add(frameBackgroundIcons);
		group.add(frameBackgroundColor);

		return menu;
	}

	public void init() {
		fCurrentInducementTotalHome = -1;
		fCurrentUsedCardsHome = 0;
		fCurrentInducementTotalAway = -1;
		fCurrentUsedCardsAway = 0;
		fCurrentActiveCardsHome = null;
		fCurrentActiveCardsAway = null;

		this.removeAll();

		createGameMenu();
		createTeamSetupMenu();
		createUserSettingsMenu();
		createGameStatusMenus();
		createHelpMenu();

		refresh();
	}

	public void refresh() {

		Game game = getClient().getGame();

		String soundSetting = getClient().getProperty(CommonProperty.SETTING_SOUND_MODE);
		fSoundOnMenuItem.setSelected(true);
		fSoundMuteSpectatorsMenuItem.setSelected(IClientPropertyValue.SETTING_SOUND_MUTE_SPECTATORS.equals(soundSetting));
		fSoundOffMenuItem.setSelected(IClientPropertyValue.SETTING_SOUND_OFF.equals(soundSetting));

		String iconsSetting = getClient().getProperty(SETTING_ICONS);
		fIconsTeam.setSelected(true);
		fIconsRosterOpponent.setSelected(IClientPropertyValue.SETTING_ICONS_ROSTER_OPPONENT.equals(iconsSetting));
		fIconsRosterBoth.setSelected(IClientPropertyValue.SETTING_ICONS_ROSTER_BOTH.equals(iconsSetting));
		fIconsAbstract.setSelected(IClientPropertyValue.SETTING_ICONS_ABSTRACT.equals(iconsSetting));

		String automoveSetting = getClient().getProperty(CommonProperty.SETTING_AUTOMOVE);
		fAutomoveOnMenuItem.setSelected(true);
		fAutomoveOffMenuItem.setSelected(IClientPropertyValue.SETTING_AUTOMOVE_OFF.equals(automoveSetting));

		String blitzTargetPanelSetting = getClient().getProperty(CommonProperty.SETTING_BLITZ_TARGET_PANEL);
		fBlitzPanelOnMenuItem.setSelected(true);
		fBlitzPanelOffMenuItem.setSelected(IClientPropertyValue.SETTING_BLITZ_TARGET_PANEL_OFF.equals(blitzTargetPanelSetting));

		String gazeTargetPanelSetting = getClient().getProperty(CommonProperty.SETTING_GAZE_TARGET_PANEL);
		gazePanelOnMenuItem.setSelected(true);
		gazePanelOffMenuItem.setSelected(IClientPropertyValue.SETTING_GAZE_TARGET_PANEL_OFF.equals(gazeTargetPanelSetting));

		String rightClickEndActionSetting = getClient().getProperty(CommonProperty.SETTING_RIGHT_CLICK_END_ACTION);
		rightClickEndActionOffMenuItem.setSelected(true);
		rightClickEndActionOnMenuItem.setSelected(IClientPropertyValue.SETTING_RIGHT_CLICK_END_ACTION_ON.equals(rightClickEndActionSetting));
		rightClickLegacyModeItem.setSelected(IClientPropertyValue.SETTING_RIGHT_CLICK_LEGACY_MODE.equals(rightClickEndActionSetting));
		rightClickOpensContextMenuItem.setSelected(IClientPropertyValue.SETTING_RIGHT_CLICK_OPENS_CONTEXT_MENU.equals(rightClickEndActionSetting));

		String reRollBallAndChainSetting = getClient().getProperty(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN);
		reRollBallAndChainAlwaysMenuItem.setSelected(true);
		reRollBallAndChainTeamMateMenuItem.setSelected(IClientPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_TEAM_MATE.equals(reRollBallAndChainSetting));
		reRollBallAndChainNoOpponentMenuItem.setSelected(IClientPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_NO_OPPONENT.equals(reRollBallAndChainSetting));
		reRollBallAndChainNeverMenuItem.setSelected(IClientPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_NEVER.equals(reRollBallAndChainSetting));

		String pitchCustomizationSetting = getClient().getProperty(CommonProperty.SETTING_PITCH_CUSTOMIZATION);
		fCustomPitchMenuItem.setSelected(true);
		fDefaultPitchMenuItem.setSelected(IClientPropertyValue.SETTING_PITCH_DEFAULT.equals(pitchCustomizationSetting));
		fBasicPitchMenuItem.setSelected(IClientPropertyValue.SETTING_PITCH_BASIC.equals(pitchCustomizationSetting));

		String pitchMarkingsSetting = getClient().getProperty(CommonProperty.SETTING_PITCH_MARKINGS);
		fPitchMarkingsOffMenuItem.setSelected(true);
		fPitchMarkingsOnMenuItem.setSelected(IClientPropertyValue.SETTING_PITCH_MARKINGS_ON.equals(pitchMarkingsSetting));

		String orientationSetting = getClient().getProperty(CommonProperty.SETTING_PITCH_ORIENTATION);
		pitchLandscapeMenuItem.setSelected(true);
		pitchPortraitMenuItem.setSelected(IClientPropertyValue.SETTING_PITCH_PORTRAIT.equals(orientationSetting));
		layoutSquareMenuItem.setSelected(IClientPropertyValue.SETTING_LAYOUT_SQUARE.equals(orientationSetting));

		String teamLogosSetting = getClient().getProperty(CommonProperty.SETTING_TEAM_LOGOS);
		fTeamLogoBothMenuItem.setSelected(true);
		fTeamLogoOwnMenuItem.setSelected(IClientPropertyValue.SETTING_TEAM_LOGOS_OWN.equals(teamLogosSetting));
		fTeamLogoNoneMenuItem.setSelected(IClientPropertyValue.SETTING_TEAM_LOGOS_NONE.equals(teamLogosSetting));

		String pitchWeatherSetting = getClient().getProperty(CommonProperty.SETTING_PITCH_WEATHER);
		fPitchWeatherOnMenuItem.setSelected(true);
		fPitchWeatherOffMenuItem.setSelected(IClientPropertyValue.SETTING_PITCH_WEATHER_OFF.equals(pitchWeatherSetting));

		String rangeGridSetting = getClient().getProperty(CommonProperty.SETTING_RANGEGRID);
		fRangeGridToggleMenuItem.setSelected(true);
		fRangeGridAlwaysOnMenuItem.setSelected(IClientPropertyValue.SETTING_RANGEGRID_ALWAYS_ON.equals(rangeGridSetting));

		String markUsedPlayersSetting = getClient().getProperty(CommonProperty.SETTING_MARK_USED_PLAYERS);
		markUsedPlayersDefaultMenuItem.setSelected(true);
		markUsedPlayersCheckIconGreenMenuItem.setSelected(IClientPropertyValue.SETTING_MARK_USED_PLAYERS_CHECK_ICON_GREEN.equals(markUsedPlayersSetting));

		String playerMarkingSetting = getClient().getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE);
		playersMarkingManualMenuItem.setSelected(true);
		playersMarkingAutoMenuItem.setSelected(IClientPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO.equals(playerMarkingSetting));

		boolean refreshUi = refreshColorMenu(CommonProperty.SETTING_BACKGROUND_CHAT, chatBackground,
			styleProvider::getChatBackground, styleProvider::setChatBackground);

		refreshUi |= refreshColorMenu(CommonProperty.SETTING_BACKGROUND_LOG, logBackground,
			styleProvider::getLogBackground, styleProvider::setLogBackground);

		refreshUi |= refreshColorMenu(CommonProperty.SETTING_FONT_COLOR_TEXT, textFontColor,
			styleProvider::getText, styleProvider::setText);

		refreshUi |= refreshColorMenu(CommonProperty.SETTING_FONT_COLOR_AWAY, awayFontColor,
			styleProvider::getAwayUnswapped, styleProvider::setAway);

		refreshUi |= refreshColorMenu(CommonProperty.SETTING_FONT_COLOR_HOME, homeFontColor,
			styleProvider::getHomeUnswapped, styleProvider::setHome);

		refreshUi |= refreshColorMenu(CommonProperty.SETTING_FONT_COLOR_SPEC, specFontColor,
			styleProvider::getSpec, styleProvider::setSpec);

		refreshUi |= refreshColorMenu(CommonProperty.SETTING_FONT_COLOR_ADMIN, adminFontColor,
			styleProvider::getAdmin, styleProvider::setAdmin);

		refreshUi |= refreshColorMenu(CommonProperty.SETTING_FONT_COLOR_DEV, devFontColor,
			styleProvider::getDev, styleProvider::setDev);

		refreshUi |= refreshColorMenu(CommonProperty.SETTING_FONT_COLOR_FRAME, frameFontColor,
			styleProvider::getFrame, styleProvider::setFrame);

		refreshUi |= refreshColorMenu(CommonProperty.SETTING_FONT_COLOR_FRAME_SHADOW, frameFontShadowColor,
			styleProvider::getFrameShadow, styleProvider::setFrameShadow);

		refreshUi |= refreshColorMenu(CommonProperty.SETTING_FONT_COLOR_INPUT, inputFontColor,
			styleProvider::getInput, styleProvider::setInput);

		String frameBackgroundSetting = getClient().getProperty(CommonProperty.SETTING_BACKGROUND_FRAME);
		frameBackgroundIcons.setSelected(true);
		boolean useColorForFrames = IClientPropertyValue.SETTING_BACKGROUND_FRAME_COLOR.equals(frameBackgroundSetting);
		frameBackgroundColor.setSelected(useColorForFrames);

		refreshUi |= refreshFrameBackgroundMenu(useColorForFrames);

		String swapTeamColorsSetting = getClient().getProperty(CommonProperty.SETTING_SWAP_TEAM_COLORS);
		swapTeamColorsOffMenuItem.setSelected(true);
		boolean swapTeamColors = IClientPropertyValue.SETTING_SWAP_TEAM_COLORS_ON.equals(swapTeamColorsSetting);
		swapTeamColorsOnMenuItem.setSelected(swapTeamColors);

		if (swapTeamColors != styleProvider.isSwapTeamColors()) {
			styleProvider.setSwapTeamColors(swapTeamColors);
			refreshUi = true;
		}

		boolean gameStarted = ((game != null) && (game.getStarted() != null));
		fGameStatisticsMenuItem.setEnabled(gameStarted);

		boolean allowConcessions = game != null && ((GameOptionBoolean) game.getOptions().getOptionWithDefault(GameOptionId.ALLOW_CONCESSIONS)).isEnabled();
		fGameConcessionMenuItem.setEnabled(allowConcessions && gameStarted && game.isHomePlaying()
			&& (ClientMode.PLAYER == getClient().getMode()) && game.isConcessionPossible());

		fGameReplayMenuItem.setEnabled(ClientMode.SPECTATOR == getClient().getMode());

		playerMarkingMenu.setEnabled(ClientMode.REPLAY != getClient().getMode());

		updateMissingPlayers();
		updateInducements();
		updateActiveCards();
		updatePrayers();
		updateGameOptions();
		refreshUi |= updateOrientation();
		refreshUi |= updateScaling();

		boolean askForReRoll = ((GameOptionBoolean) getClient().getGame().getOptions().getOptionWithDefault(GameOptionId.ALLOW_BALL_AND_CHAIN_RE_ROLL)).isEnabled();

		reRollBallAndChainPanelMenu.setText(askForReRoll ? "Ask to Re-Roll Ball & Chain Movement" : "Ask for Whirling Dervish");


		if (getClient().getUserInterface() != null && refreshUi) {
			getClient().getUserInterface().initComponents(true);
		}
	}

	private boolean refreshFrameBackgroundMenu(boolean useColor) {
		Color oldColor = styleProvider.getFrameBackground();
		Color newColor = null;

		if (useColor) {
			try {
				newColor = new Color(Integer.parseInt(getClient().getProperty(CommonProperty.SETTING_BACKGROUND_FRAME_COLOR)));
				frameBackgroundColor.setIcon(createColorIcon(newColor));
			} catch (NumberFormatException ex) {
				getClient().getFactorySource().logWithOutGameId(ex);
			}
		}

		styleProvider.setFrameBackground(newColor);

		return !Objects.equals(oldColor, newColor);
	}

	private boolean refreshColorMenu(CommonProperty key, JMenuItem customItem,
																	 Supplier<Color> oldColor, Consumer<Color> setter) {

		if (getClient().getUserInterface() == null) {
			return false;
		}

		String colorSetting = getClient().getProperty(key);

		Color color = null;
		if (!StringTool.isProvided(colorSetting)) {
			return false;
		}

		try {
			color = new Color(Integer.parseInt(colorSetting));
			customItem.setSelected(true);
		} catch (NumberFormatException ex) {
			getClient().getFactorySource().logWithOutGameId(ex);
		}

		if (color != null && !color.equals(oldColor.get())) {
			customItem.setIcon(createColorIcon(color));
			setter.accept(color);
			return true;
		}
		return false;
	}

	public FantasyFootballClient getClient() {
		return fClient;
	}

	public void actionPerformed(ActionEvent e) {
		ClientReplayer replayer = getClient().getReplayer();
		javax.swing.JMenuItem source = (javax.swing.JMenuItem) (e.getSource());
		if (source == null) {
			return;
		}
		if (source == fLoadSetupMenuItem) {
			getClient().getClientState().actionKeyPressed(ActionKey.MENU_SETUP_LOAD);
		}
		if (source == fSaveSetupMenuItem) {
			getClient().getClientState().actionKeyPressed(ActionKey.MENU_SETUP_SAVE);
		}
		if (source == fSoundVolumeItem) {
			showDialog(new DialogSoundVolume(getClient()));
		}
		if (source == scalingItem) {
			showDialog(new DialogScalingFactor(getClient()));
		}
		if (source == fSoundOffMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_SOUND_MODE, IClientPropertyValue.SETTING_SOUND_OFF);
			getClient().saveUserSettings(false);
		}
		if (source == fSoundMuteSpectatorsMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_SOUND_MODE, IClientPropertyValue.SETTING_SOUND_MUTE_SPECTATORS);
			getClient().saveUserSettings(false);
		}
		if (source == fSoundOnMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_SOUND_MODE, IClientPropertyValue.SETTING_SOUND_ON);
			getClient().saveUserSettings(false);
		}
		if (source == fIconsTeam) {
			getClient().setProperty(SETTING_ICONS, IClientPropertyValue.SETTING_ICONS_TEAM);
			getClient().saveUserSettings(true);
		}
		if (source == fIconsRosterOpponent) {
			getClient().setProperty(SETTING_ICONS, IClientPropertyValue.SETTING_ICONS_ROSTER_OPPONENT);
			getClient().saveUserSettings(true);
		}
		if (source == fIconsRosterBoth) {
			getClient().setProperty(SETTING_ICONS, IClientPropertyValue.SETTING_ICONS_ROSTER_BOTH);
			getClient().saveUserSettings(true);
		}
		if (source == fIconsAbstract) {
			getClient().setProperty(SETTING_ICONS, IClientPropertyValue.SETTING_ICONS_ABSTRACT);
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
		if (source == autoMarkingItem) {
			showDialog(DialogAutoMarking.create(getClient(), false));
		}
		if (source == fKeyBindingsMenuItem) {
			showDialog(new DialogKeyBindings(getClient()));
		}
		if (source == fGameStatisticsMenuItem) {
			showDialog(new DialogGameStatistics(getClient()));
		}
		if (source == fAutomoveOffMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_AUTOMOVE, IClientPropertyValue.SETTING_AUTOMOVE_OFF);
			getClient().saveUserSettings(false);
		}
		if (source == fAutomoveOnMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_AUTOMOVE, IClientPropertyValue.SETTING_AUTOMOVE_ON);
			getClient().saveUserSettings(false);
		}
		if (source == fBlitzPanelOffMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_BLITZ_TARGET_PANEL, IClientPropertyValue.SETTING_BLITZ_TARGET_PANEL_OFF);
			getClient().saveUserSettings(false);
		}
		if (source == fBlitzPanelOnMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_BLITZ_TARGET_PANEL, IClientPropertyValue.SETTING_BLITZ_TARGET_PANEL_ON);
			getClient().saveUserSettings(false);
		}
		if (source == gazePanelOnMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_GAZE_TARGET_PANEL, IClientPropertyValue.SETTING_GAZE_TARGET_PANEL_ON);
			getClient().saveUserSettings(false);
		}
		if (source == gazePanelOffMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_GAZE_TARGET_PANEL, IClientPropertyValue.SETTING_GAZE_TARGET_PANEL_OFF);
			getClient().saveUserSettings(false);
		}
		if (source == rightClickEndActionOnMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_RIGHT_CLICK_END_ACTION, IClientPropertyValue.SETTING_RIGHT_CLICK_END_ACTION_ON);
			getClient().saveUserSettings(false);
		}
		if (source == rightClickLegacyModeItem) {
			getClient().setProperty(CommonProperty.SETTING_RIGHT_CLICK_END_ACTION, IClientPropertyValue.SETTING_RIGHT_CLICK_LEGACY_MODE);
			getClient().saveUserSettings(false);
		}
		if (source == rightClickOpensContextMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_RIGHT_CLICK_END_ACTION, IClientPropertyValue.SETTING_RIGHT_CLICK_OPENS_CONTEXT_MENU);
			getClient().saveUserSettings(false);
		}
		if (source == rightClickEndActionOffMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_RIGHT_CLICK_END_ACTION, IClientPropertyValue.SETTING_RIGHT_CLICK_END_ACTION_OFF);
			getClient().saveUserSettings(false);
		}
		if (source == reRollBallAndChainAlwaysMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN, IClientPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_ALWAYS);
			getClient().saveUserSettings(false);
		}
		if (source == reRollBallAndChainTeamMateMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN, IClientPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_TEAM_MATE);
			getClient().saveUserSettings(false);
		}
		if (source == reRollBallAndChainNoOpponentMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN, IClientPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_NO_OPPONENT);
			getClient().saveUserSettings(false);
		}
		if (source == reRollBallAndChainNeverMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN, IClientPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_NEVER);
			getClient().saveUserSettings(false);
		}
		if (source == fCustomPitchMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_PITCH_CUSTOMIZATION, IClientPropertyValue.SETTING_PITCH_CUSTOM);
			getClient().saveUserSettings(true);
		}
		if (source == fDefaultPitchMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_PITCH_CUSTOMIZATION, IClientPropertyValue.SETTING_PITCH_DEFAULT);
			getClient().saveUserSettings(true);
		}
		if (source == fBasicPitchMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_PITCH_CUSTOMIZATION, IClientPropertyValue.SETTING_PITCH_BASIC);
			getClient().saveUserSettings(true);
		}
		if (source == fPitchMarkingsOffMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_PITCH_MARKINGS, IClientPropertyValue.SETTING_PITCH_MARKINGS_OFF);
			getClient().saveUserSettings(true);
		}
		if (source == fPitchMarkingsOnMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_PITCH_MARKINGS, IClientPropertyValue.SETTING_PITCH_MARKINGS_ON);
			getClient().saveUserSettings(true);
		}
		if (source == pitchLandscapeMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_PITCH_ORIENTATION, IClientPropertyValue.SETTING_PITCH_LANDSCAPE);
			getClient().saveUserSettings(true);
		}
		if (source == pitchPortraitMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_PITCH_ORIENTATION, IClientPropertyValue.SETTING_PITCH_PORTRAIT);
			getClient().saveUserSettings(true);
		}
		if (source == layoutSquareMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_PITCH_ORIENTATION, IClientPropertyValue.SETTING_LAYOUT_SQUARE);
			getClient().saveUserSettings(true);
		}
		if (source == fTeamLogoBothMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_TEAM_LOGOS, IClientPropertyValue.SETTING_TEAM_LOGOS_BOTH);
			getClient().saveUserSettings(true);
		}
		if (source == fTeamLogoOwnMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_TEAM_LOGOS, IClientPropertyValue.SETTING_TEAM_LOGOS_OWN);
			getClient().saveUserSettings(true);
		}
		if (source == fTeamLogoNoneMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_TEAM_LOGOS, IClientPropertyValue.SETTING_TEAM_LOGOS_NONE);
			getClient().saveUserSettings(true);
		}
		if (source == fCustomPitchMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_PITCH_CUSTOMIZATION, IClientPropertyValue.SETTING_PITCH_CUSTOM);
			getClient().saveUserSettings(true);
		}
		if (source == fPitchWeatherOnMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_PITCH_WEATHER, IClientPropertyValue.SETTING_PITCH_WEATHER_ON);
			getClient().saveUserSettings(true);
		}
		if (source == fPitchWeatherOffMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_PITCH_WEATHER, IClientPropertyValue.SETTING_PITCH_WEATHER_OFF);
			getClient().saveUserSettings(true);
		}
		if (source == fRangeGridAlwaysOnMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_RANGEGRID, IClientPropertyValue.SETTING_RANGEGRID_ALWAYS_ON);
			getClient().saveUserSettings(false);
		}

		if (source == markUsedPlayersCheckIconGreenMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_MARK_USED_PLAYERS, IClientPropertyValue.SETTING_MARK_USED_PLAYERS_CHECK_ICON_GREEN);
			getClient().saveUserSettings(true);
		}

		if (source == markUsedPlayersDefaultMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_MARK_USED_PLAYERS, IClientPropertyValue.SETTING_MARK_USED_PLAYERS_DEFAULT);
			getClient().saveUserSettings(true);
		}

		if (source == playersMarkingAutoMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE, IClientPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO);
			getClient().saveUserSettings(true);
			getClient().getCommunication().sendUpdatePlayerMarkings(true);

			if (!IClientPropertyValue.SETTING_HIDE_AUTO_MARKING_DIALOG.equals(getClient().getProperty(CommonProperty.SETTING_SHOW_AUTO_MARKING_DIALOG))) {
				showDialog(DialogAutoMarking.create(getClient(), true));
			}
		}

		if (source == playersMarkingManualMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE, IClientPropertyValue.SETTING_PLAYER_MARKING_TYPE_MANUAL);
			getClient().saveUserSettings(true);
			getClient().getCommunication().sendUpdatePlayerMarkings(false);
		}

		if (source == swapTeamColorsOffMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_SWAP_TEAM_COLORS, IClientPropertyValue.SETTING_SWAP_TEAM_COLORS_OFF);
			getClient().saveUserSettings(true);
		}

		if (source == swapTeamColorsOnMenuItem) {
			getClient().setProperty(CommonProperty.SETTING_SWAP_TEAM_COLORS, IClientPropertyValue.SETTING_SWAP_TEAM_COLORS_ON);
			getClient().saveUserSettings(true);
		}

		if (source == chatBackground) {
			Color defaultColor = styleProvider.getChatBackground();
			Color color = JColorChooser.showDialog(this, "Choose chat background color", defaultColor);
			if (color != null) {
				getClient().setProperty(CommonProperty.SETTING_BACKGROUND_CHAT, String.valueOf(color.getRGB()));
				getClient().saveUserSettings(true);
			}
		}

		if (source == logBackground) {
			Color defaultColor = styleProvider.getLogBackground();
			Color color = JColorChooser.showDialog(this, "Choose log background color", defaultColor);
			if (color != null) {
				getClient().setProperty(CommonProperty.SETTING_BACKGROUND_LOG, String.valueOf(color.getRGB()));
				getClient().saveUserSettings(true);
			}
		}

		if (source == frameBackgroundIcons) {
			getClient().setProperty(CommonProperty.SETTING_BACKGROUND_FRAME, IClientPropertyValue.SETTING_BACKGROUND_FRAME_ICONS);
			getClient().saveUserSettings(true);
		}

		if (source == frameBackgroundColor) {
			getClient().setProperty(CommonProperty.SETTING_BACKGROUND_FRAME, IClientPropertyValue.SETTING_BACKGROUND_FRAME_COLOR);
			Color defaultColor = styleProvider.getFrameBackground();
			Color color = JColorChooser.showDialog(this, "Choose sidebar and scoreboard background color", defaultColor);
			if (color != null) {
				getClient().setProperty(CommonProperty.SETTING_BACKGROUND_FRAME_COLOR, String.valueOf(color.getRGB()));
			}
			getClient().saveUserSettings(true);
		}

		if (source == textFontColor) {
			Color defaultColor = styleProvider.getText();
			Color color = JColorChooser.showDialog(this, "Choose text color", defaultColor);
			if (color != null) {
				getClient().setProperty(CommonProperty.SETTING_FONT_COLOR_TEXT, String.valueOf(color.getRGB()));
				getClient().saveUserSettings(true);
			}
		}

		if (source == awayFontColor) {
			Color defaultColor = styleProvider.getAwayUnswapped();
			Color color = JColorChooser.showDialog(this, "Choose away color", defaultColor);
			if (color != null) {
				getClient().setProperty(CommonProperty.SETTING_FONT_COLOR_AWAY, String.valueOf(color.getRGB()));
				getClient().saveUserSettings(true);
			}
		}

		if (source == homeFontColor) {
			Color defaultColor = styleProvider.getHomeUnswapped();
			Color color = JColorChooser.showDialog(this, "Choose home color", defaultColor);
			if (color != null) {
				getClient().setProperty(CommonProperty.SETTING_FONT_COLOR_HOME, String.valueOf(color.getRGB()));
				getClient().saveUserSettings(true);
			}
		}

		if (source == specFontColor) {
			Color defaultColor = styleProvider.getSpec();
			Color color = JColorChooser.showDialog(this, "Choose spec color", defaultColor);
			if (color != null) {
				getClient().setProperty(CommonProperty.SETTING_FONT_COLOR_SPEC, String.valueOf(color.getRGB()));
				getClient().saveUserSettings(true);
			}
		}

		if (source == adminFontColor) {
			Color defaultColor = styleProvider.getAdmin();
			Color color = JColorChooser.showDialog(this, "Choose admin color", defaultColor);
			if (color != null) {
				getClient().setProperty(CommonProperty.SETTING_FONT_COLOR_ADMIN, String.valueOf(color.getRGB()));
				getClient().saveUserSettings(true);
			}
		}

		if (source == devFontColor) {
			Color defaultColor = styleProvider.getDev();
			Color color = JColorChooser.showDialog(this, "Choose dev color", defaultColor);
			if (color != null) {
				getClient().setProperty(CommonProperty.SETTING_FONT_COLOR_DEV, String.valueOf(color.getRGB()));
				getClient().saveUserSettings(true);
			}
		}

		if (source == frameFontColor) {
			Color defaultColor = styleProvider.getFrame();
			Color color = JColorChooser.showDialog(this, "Choose frane color", defaultColor);
			if (color != null) {
				getClient().setProperty(CommonProperty.SETTING_FONT_COLOR_FRAME, String.valueOf(color.getRGB()));
				getClient().saveUserSettings(true);
			}
		}

		if (source == frameFontShadowColor) {
			Color defaultColor = styleProvider.getFrameShadow();
			Color color = JColorChooser.showDialog(this, "Choose frame shadow color", defaultColor);
			if (color != null) {
				getClient().setProperty(CommonProperty.SETTING_FONT_COLOR_FRAME_SHADOW, String.valueOf(color.getRGB()));
				getClient().saveUserSettings(true);
			}
		}

		if (source == inputFontColor) {
			Color defaultColor = styleProvider.getInput();
			Color color = JColorChooser.showDialog(this, "Choose frame shadow color", defaultColor);
			if (color != null) {
				getClient().setProperty(CommonProperty.SETTING_FONT_COLOR_INPUT, String.valueOf(color.getRGB()));
				getClient().saveUserSettings(true);
			}
		}

		if (source == localPropertiesItem) {
			showDialog(new DialogSelectLocalStoredProperties(fClient));
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

		if (source == resetColors) {
			resetColors(CommonProperty.COLOR_SETTINGS);
		}

		if (source == resetBackgroundColors) {
			resetColors(CommonProperty.BACKGROUND_COLOR_SETTINGS);
		}

		if (source == resetFontColors) {
			resetColors(CommonProperty.FONT_COLOR_SETTINGS);
		}

		if (source == fGameReplayMenuItem) {
			fGameReplayMenuItem.setText(replayer.isReplaying() ? _REPLAY_MODE_ON : _REPLAY_MODE_OFF);
			getClient().getClientState().actionKeyPressed(ActionKey.MENU_REPLAY);
		}
		if (source == fGameConcessionMenuItem) {
			getClient().getCommunication().sendConcedeGame(ConcedeGameStatus.REQUESTED);
		}
	}

	private void resetColors(CommonProperty[] settings) {
		for (CommonProperty setting : settings) {
			getClient().setProperty(setting, String.valueOf(StyleProvider.defaults.get(setting).getRGB()));
		}
		getClient().saveUserSettings(true);
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
		switch (pDialog.getId()) {
			case SOUND_VOLUME:
				DialogSoundVolume volumeDialog = (DialogSoundVolume) pDialog;
				getClient().setProperty(CommonProperty.SETTING_SOUND_VOLUME, Integer.toString(volumeDialog.getVolume()));
				getClient().saveUserSettings(true);
				break;
			case SCALING_FACTOR:
				DialogScalingFactor scalingDialog = (DialogScalingFactor) pDialog;
				if (scalingDialog.getFactor() != null) {
					updateScaleProperty(scalingDialog.getFactor());
				}
				break;
			case STORE_PROPERTIES_LOCAL:
				DialogSelectLocalStoredProperties dialogSelectLocalStoredProperties = (DialogSelectLocalStoredProperties) pDialog;
				if (dialogSelectLocalStoredProperties.getSelectedProperties() != null) {
					getClient().setLocallyStoredPropertyKeys(dialogSelectLocalStoredProperties.getSelectedProperties());
					getClient().saveUserSettings(false);
				}
				break;
			default:
				break;
		}
		fDialogShown = null;
	}

	private void updateScaleProperty(double scalingFactor) {
		getClient().setProperty(CommonProperty.SETTING_SCALE_FACTOR, Double.toString(scalingFactor));
		getClient().saveUserSettings(true);
	}

	public void increaseScaling() {
		updateScaleProperty(dimensionProvider.largerScale());
	}

	public void decreaseScaling() {
		updateScaleProperty(dimensionProvider.smallerScale());
	}

	public void resetScaling() {
		updateScaleProperty(DimensionProvider.BASE_SCALE_FACTOR);
	}


	public void showDialog(IDialog pDialog) {
		if (fDialogShown != null) {
			fDialogShown.hideDialog();
		}
		fDialogShown = pDialog;
		fDialogShown.showDialog(this);
	}

	private boolean updateScaling() {
		String factorValue = getClient().getProperty(CommonProperty.SETTING_SCALE_FACTOR);
			if (StringTool.isProvided(factorValue)) {
				try {
					double factor = Double.parseDouble(factorValue);
					if (dimensionProvider.getScale() != factor) {
						dimensionProvider.setScale(factor);
						getClient().getUserInterface().getIconCache().clear();
						FontCache fontCache = getClient().getUserInterface().getFontCache();
						fontCache.clear();
						UIManager.put("ToolTip.font", fontCache.font(Font.PLAIN, 14));
						return true;
					}
				} catch (Exception ignored) {

				}
			}
			return false;
	}

	private boolean updateOrientation() {

		DimensionProvider.ClientLayout layout = DimensionProvider.ClientLayout.LANDSCAPE;

		String orientation = getClient().getProperty(CommonProperty.SETTING_PITCH_ORIENTATION);

		if (orientation != null) {
			switch (orientation) {
				case IClientPropertyValue.SETTING_PITCH_PORTRAIT:
					layout = DimensionProvider.ClientLayout.PORTRAIT;
					break;
				case IClientPropertyValue.SETTING_LAYOUT_SQUARE:
					layout = DimensionProvider.ClientLayout.SQUARE;
					break;
				default:
					break;
			}
		}

		if (layout != dimensionProvider.getLayout()) {
			dimensionProvider.setLayout(layout);
			return true;
		}

		return false;
	}

	public void updateGameOptions() {
		fGameOptionsMenu.removeAll();
		IGameOption[] gameOptions = getClient().getGame().getOptions().getOptions();
		Arrays.sort(gameOptions, Comparator.comparing(pO -> pO.getId().getName()));
		int optionsAdded = 0;
		if (getClient().getGame().isTesting()) {
			JMenuItem optionItem = new JMenuItem(dimensionProvider,
				"* Game is in TEST mode. No results will be uploaded. See help for available test commands.");
			fGameOptionsMenu.add(optionItem);
			optionsAdded++;
		}
		for (IGameOption option : gameOptions) {
			if (option.isChanged() && (option.getId() != GameOptionId.TEST_MODE)
				&& StringTool.isProvided(option.getDisplayMessage())) {
				JMenuItem optionItem = new JMenuItem(dimensionProvider, "* " + option.getDisplayMessage());
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
				fInducementsHomeMenu = new JMenu(dimensionProvider, totalInducementHome + " Home Team");
				fInducementsHomeMenu.setForeground(Color.RED);
				fInducementsHomeMenu.setMnemonic(KeyEvent.VK_H);
				fInducementsMenu.add(fInducementsHomeMenu);
				addInducements(fInducementsHomeMenu, inducementSetHome);
			}

			if (fCurrentInducementTotalAway > 0) {
				fInducementsAwayMenu = new JMenu(dimensionProvider, totalInducementAway + " Away Team");
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
				JMenu fActiveCardsHomeMenu = new JMenu(dimensionProvider, fCurrentActiveCardsHome.length + " Home Team");
				fActiveCardsHomeMenu.setForeground(Color.RED);
				fActiveCardsHomeMenu.setMnemonic(KeyEvent.VK_H);
				fActiveCardsMenu.add(fActiveCardsHomeMenu);
				addActiveCards(fActiveCardsHomeMenu, fCurrentActiveCardsHome);
			}

			if (ArrayTool.isProvided(fCurrentActiveCardsAway)) {
				JMenu fActiveCardsAwayMenu = new JMenu(dimensionProvider, fCurrentActiveCardsAway.length + " Away Team");
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
				JMenuItem cardMenuItem = new JMenuItem(dimensionProvider, cardText.toString(), cardIcon);
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
				JMenu prayersHomeMenu = new JMenu(dimensionProvider, currentPrayersHome.size() + " Home Team");
				prayersHomeMenu.setForeground(Color.RED);
				prayersHomeMenu.setMnemonic(KeyEvent.VK_H);
				prayersMenu.add(prayersHomeMenu);
				addPrayers(prayersHomeMenu, currentPrayersHome);
			}

			if (!currentPrayersAway.isEmpty()) {
				JMenu prayersAwayMenu = new JMenu(dimensionProvider, currentPrayersAway.size() + " Away Team");
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
			JMenuItem menuItem = new JMenuItem(dimensionProvider, text);
			prayerMenu.add(menuItem);
		}
	}

	private void addInducements(JMenu pInducementMenu, InducementSet pInducementSet) {
		Inducement[] inducements = pInducementSet.getInducements();
		Arrays.sort(inducements, Comparator.comparing(pInducement -> pInducement.getType().getName()));
		for (Inducement inducement : inducements) {
			if (!Usage.EXCLUDE_FROM_RESULT.containsAll(inducement.getType().getUsages())) {
				if (inducement.getValue() > 0) {
					StringBuilder inducementText = new StringBuilder();
					inducementText.append(inducement.getValue()).append(" ");
					if (inducement.getValue() > 1) {
						inducementText.append(inducement.getType().getPlural());
					} else {
						inducementText.append(inducement.getType().getSingular());
					}
					JMenuItem inducementItem = new JMenuItem(dimensionProvider, inducementText.toString());
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
			JMenu starPlayerMenu = new JMenu(dimensionProvider, starPlayerMenuText.toString());
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
			JMenu mercenaryMenu = new JMenu(dimensionProvider, mercenaryMenuText.toString());
			pInducementMenu.add(mercenaryMenu);
			for (Player<?> player : mercenaries) {
				addPlayerMenuItem(mercenaryMenu, player, player.getName());
			}
		}

		List<Player<?>> staff = new ArrayList<>();
		for (Player<?> player : team.getPlayers()) {
			if (player.getPlayerType() == PlayerType.INFAMOUS_STAFF) {
				staff.add(player);
			}
		}
		if (staff.size() > 0) {
			String staffText = staff.size() + " Infamous Staff";
			JMenu staffMenu = new JMenu(dimensionProvider, staffText);
			pInducementMenu.add(staffMenu);
			for (Player<?> player : staff) {
				addPlayerMenuItem(staffMenu, player, player.getName());
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
				JMenu cardMenu = new JMenu(dimensionProvider, cardTypeText.toString());
				pInducementMenu.add(cardMenu);
				ImageIcon cardIcon = new ImageIcon(
					userInterface.getIconCache().getIconByProperty(IIconProperty.SIDEBAR_OVERLAY_PLAYER_CARD));
				for (Card card : cardList) {
					if (pInducementSet.isAvailable(card)) {
						String cardText = "<html>" +
							"<b>" + card.getName() + "</b>" +
							"<br>" + card.getHtmlDescriptionWithPhases() +
							"</html>";
						JMenuItem cardItem = new JMenuItem(dimensionProvider, cardText, cardIcon);
						cardMenu.add(cardItem);
					}
				}
			} else {
				JMenuItem cardItem = new JMenuItem(dimensionProvider, cardTypeText.toString());
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
		JMenuItem playersMenuItem = new JMenuItem(dimensionProvider, pText, playerIcon);
		playersMenuItem.addMouseListener(new MenuPlayerMouseListener(pPlayer));
		pPlayersMenu.add(playersMenuItem);
	}

	private Map<CardType, List<Card>> buildCardMap(InducementSet pInducementSet) {
		Card[] allCards = pInducementSet.getAllCards();

		return Arrays.stream(allCards).collect(Collectors.groupingBy(Card::getType));
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
