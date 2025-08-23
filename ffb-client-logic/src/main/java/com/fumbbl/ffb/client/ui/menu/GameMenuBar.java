package com.fumbbl.ffb.client.ui.menu;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.client.Component;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FontCache;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.StyleProvider;
import com.fumbbl.ffb.client.dialog.DialogAutoMarking;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.client.dialog.IDialogCloseListener;
import com.fumbbl.ffb.client.overlay.sketch.ClientSketchManager;
import com.fumbbl.ffb.client.ui.ColorIcon;
import com.fumbbl.ffb.client.ui.menu.game.GameModeMenu;
import com.fumbbl.ffb.client.ui.menu.game.ReplayMenu;
import com.fumbbl.ffb.client.ui.menu.game.StandardGameMenu;
import com.fumbbl.ffb.client.ui.menu.settings.UserSettingsMenu;
import com.fumbbl.ffb.client.ui.swing.JMenu;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.ui.swing.JRadioButtonMenuItem;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.ButtonGroup;
import javax.swing.JColorChooser;
import javax.swing.JMenuBar;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.fumbbl.ffb.CommonProperty.SETTING_AUTOMOVE;
import static com.fumbbl.ffb.CommonProperty.SETTING_BACKGROUND_CHAT;
import static com.fumbbl.ffb.CommonProperty.SETTING_BACKGROUND_FRAME;
import static com.fumbbl.ffb.CommonProperty.SETTING_BACKGROUND_LOG;
import static com.fumbbl.ffb.CommonProperty.SETTING_BLITZ_TARGET_PANEL;
import static com.fumbbl.ffb.CommonProperty.SETTING_FONT_COLOR_ADDITIONAL_PLAYER_MARKER_AWAY;
import static com.fumbbl.ffb.CommonProperty.SETTING_FONT_COLOR_ADDITIONAL_PLAYER_MARKER_HOME;
import static com.fumbbl.ffb.CommonProperty.SETTING_FONT_COLOR_ADMIN;
import static com.fumbbl.ffb.CommonProperty.SETTING_FONT_COLOR_AWAY;
import static com.fumbbl.ffb.CommonProperty.SETTING_FONT_COLOR_DEV;
import static com.fumbbl.ffb.CommonProperty.SETTING_FONT_COLOR_FIELD_MARKER;
import static com.fumbbl.ffb.CommonProperty.SETTING_FONT_COLOR_FRAME;
import static com.fumbbl.ffb.CommonProperty.SETTING_FONT_COLOR_FRAME_SHADOW;
import static com.fumbbl.ffb.CommonProperty.SETTING_FONT_COLOR_HOME;
import static com.fumbbl.ffb.CommonProperty.SETTING_FONT_COLOR_INPUT;
import static com.fumbbl.ffb.CommonProperty.SETTING_FONT_COLOR_PLAYER_MARKER_AWAY;
import static com.fumbbl.ffb.CommonProperty.SETTING_FONT_COLOR_PLAYER_MARKER_HOME;
import static com.fumbbl.ffb.CommonProperty.SETTING_FONT_COLOR_SPEC;
import static com.fumbbl.ffb.CommonProperty.SETTING_FONT_COLOR_TEXT;
import static com.fumbbl.ffb.CommonProperty.SETTING_GAZE_TARGET_PANEL;
import static com.fumbbl.ffb.CommonProperty.SETTING_ICONS;
import static com.fumbbl.ffb.CommonProperty.SETTING_MARK_USED_PLAYERS;
import static com.fumbbl.ffb.CommonProperty.SETTING_PITCH_CUSTOMIZATION;
import static com.fumbbl.ffb.CommonProperty.SETTING_PITCH_MARKINGS;
import static com.fumbbl.ffb.CommonProperty.SETTING_PITCH_MARKINGS_ROW;
import static com.fumbbl.ffb.CommonProperty.SETTING_PITCH_WEATHER;
import static com.fumbbl.ffb.CommonProperty.SETTING_PLAYER_MARKING_TYPE;
import static com.fumbbl.ffb.CommonProperty.SETTING_RANGEGRID;
import static com.fumbbl.ffb.CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN;
import static com.fumbbl.ffb.CommonProperty.SETTING_RIGHT_CLICK_END_ACTION;
import static com.fumbbl.ffb.CommonProperty.SETTING_SHOW_CRATERS_AND_BLOODSPOTS;
import static com.fumbbl.ffb.CommonProperty.SETTING_SWEET_SPOT;
import static com.fumbbl.ffb.CommonProperty.SETTING_TEAM_LOGOS;

/**
 * @author Kalimar
 */
public class GameMenuBar extends JMenuBar implements ActionListener, IDialogCloseListener {

	private final FantasyFootballClient fClient;
	private GameModeMenu gameModeMenu; // Menu for current game mode (StandardGame or Replay)

	private JMenu playerMarkingMenu;

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

	private JRadioButtonMenuItem fPitchMarkingsRowOnMenuItem;
	private JRadioButtonMenuItem fPitchMarkingsRowOffMenuItem;

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

	private JRadioButtonMenuItem showCratersAndBloodsptsMenuItem;
	private JRadioButtonMenuItem hideCratersAndBloodsptsMenuItem;

	private JRadioButtonMenuItem sweetSpotOff;
	private JRadioButtonMenuItem sweetSpotBlack;
	private JRadioButtonMenuItem sweetSpotWhite;

	private JRadioButtonMenuItem frameBackgroundIcons;
	private JRadioButtonMenuItem frameBackgroundColor;

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
	private JMenuItem homePlayerMarkerFontColor;
	private JMenuItem awayPlayerMarkerFontColor;
	private JMenuItem additionalHomePlayerMarkerFontColor;
	private JMenuItem additionalAwayPlayerMarkerFontColor;
	private JMenuItem fieldMarkerFontColor;

	private UserSettingsMenu userSettingsMenu;
	private SetupMenu setupMenu;
	private MissingPlayersMenu missingPlayersMenu;
	private InducementsMenu inducementsMenu;
	private CardsMenu cardsMenu;
	private PrayersMenu prayersMenu;
	private OptionsMenu optionsMenu;
	private HelpMenu helpMenu;

	private JMenu reRollBallAndChainPanelMenu;


	private JMenuItem resetColors;
	private JMenuItem resetBackgroundColors;
	private JMenuItem resetFontColors;


	private final Map<CommonProperty, JMenu> exposedMenus = new HashMap<>();

	private final StyleProvider styleProvider;
	private final DimensionProvider dimensionProvider;
	private final LayoutSettings layoutSettings;
	private final ClientSketchManager sketchManager;

	public GameMenuBar(FantasyFootballClient pClient, DimensionProvider dimensionProvider, StyleProvider styleProvider, FontCache fontCache, ClientSketchManager sketchManager) {

		setFont(fontCache.font(Font.PLAIN, 12, dimensionProvider));

		fClient = pClient;
		this.sketchManager = sketchManager;
		this.styleProvider = styleProvider;
		this.dimensionProvider = dimensionProvider;
		this.layoutSettings = dimensionProvider.getLayoutSettings();

		init();

	}

	public void updateJoinedCoachesMenu() {
		if (gameModeMenu instanceof ReplayMenu) {
			gameModeMenu.refresh();
		}
	}

	private void createUserSettingsMenu() {
		JMenu fUserSettingsMenu = new JMenu(dimensionProvider, "User Settings");
		fUserSettingsMenu.setMnemonic(KeyEvent.VK_U);
		add(fUserSettingsMenu);

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

		fUserSettingsMenu.addSeparator();
		createRestoreMenu(fUserSettingsMenu);
	}

	private void createRestoreMenu(JMenu fUserSettingsMenu) {

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
		addColorItem(SETTING_FONT_COLOR_PLAYER_MARKER_HOME, styleProvider.getPlayerMarkerHome(), fontStyles, (item) -> homePlayerMarkerFontColor = item);
		addColorItem(SETTING_FONT_COLOR_PLAYER_MARKER_AWAY, styleProvider.getPlayerMarkerAway(), fontStyles, (item) -> awayPlayerMarkerFontColor = item);
		addColorItem(SETTING_FONT_COLOR_ADDITIONAL_PLAYER_MARKER_HOME, styleProvider.getAdditionalPlayerMarkerHome(), fontStyles, (item) -> additionalHomePlayerMarkerFontColor = item);
		addColorItem(SETTING_FONT_COLOR_ADDITIONAL_PLAYER_MARKER_AWAY, styleProvider.getAdditionalPlayerMarkerAway(), fontStyles, (item) -> additionalAwayPlayerMarkerFontColor = item);
		addColorItem(SETTING_FONT_COLOR_FIELD_MARKER, styleProvider.getFieldMarker(), fontStyles, (item) -> fieldMarkerFontColor = item);
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

		fPitchMarkingsOnMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Distance Markings on");
		fPitchMarkingsOnMenuItem.addActionListener(this);
		tdDistanceGroup.add(fPitchMarkingsOnMenuItem);
		fPitchMarkingsMenu.add(fPitchMarkingsOnMenuItem);

		fPitchMarkingsOffMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Distance Markings off");
		fPitchMarkingsOffMenuItem.addActionListener(this);
		tdDistanceGroup.add(fPitchMarkingsOffMenuItem);
		fPitchMarkingsMenu.add(fPitchMarkingsOffMenuItem);

		JMenu fPitchMarkingsRowMenu = new JMenu(dimensionProvider, SETTING_PITCH_MARKINGS_ROW);
		fPitchMarkingsMenu.setMnemonic(KeyEvent.VK_R);
		fPitchMenu.add(fPitchMarkingsRowMenu);

		ButtonGroup rowMarkingsGroup = new ButtonGroup();

		fPitchMarkingsRowOnMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Row Markings on");
		fPitchMarkingsRowOnMenuItem.addActionListener(this);
		rowMarkingsGroup.add(fPitchMarkingsRowOnMenuItem);
		fPitchMarkingsRowMenu.add(fPitchMarkingsRowOnMenuItem);

		fPitchMarkingsRowOffMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Row Markings off");
		fPitchMarkingsRowOffMenuItem.addActionListener(this);
		rowMarkingsGroup.add(fPitchMarkingsRowOffMenuItem);
		fPitchMarkingsRowMenu.add(fPitchMarkingsRowOffMenuItem);

		JMenu cratersAndBloodspotsMenu = new JMenu(dimensionProvider, SETTING_SHOW_CRATERS_AND_BLOODSPOTS);
		cratersAndBloodspotsMenu.setMnemonic(KeyEvent.VK_B);
		fPitchMenu.add(cratersAndBloodspotsMenu);

		ButtonGroup cratersAndBloodspotsGroup = new ButtonGroup();

		showCratersAndBloodsptsMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Show");
		showCratersAndBloodsptsMenuItem.addActionListener(this);
		cratersAndBloodspotsGroup.add(showCratersAndBloodsptsMenuItem);
		cratersAndBloodspotsMenu.add(showCratersAndBloodsptsMenuItem);

		hideCratersAndBloodsptsMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Hide");
		hideCratersAndBloodsptsMenuItem.addActionListener(this);
		cratersAndBloodspotsGroup.add(hideCratersAndBloodsptsMenuItem);
		cratersAndBloodspotsMenu.add(hideCratersAndBloodsptsMenuItem);

		JMenu sweetSpotMenu = new JMenu(dimensionProvider, SETTING_SWEET_SPOT);
		sweetSpotMenu.setMnemonic(KeyEvent.VK_S);
		fPitchMenu.add(sweetSpotMenu);

		ButtonGroup sweetSpotGroup = new ButtonGroup();

		sweetSpotOff = new JRadioButtonMenuItem(dimensionProvider, "Off");
		sweetSpotOff.addActionListener(this);
		sweetSpotGroup.add(sweetSpotOff);
		sweetSpotMenu.add(sweetSpotOff);

		sweetSpotBlack = new JRadioButtonMenuItem(dimensionProvider, "Black");
		sweetSpotBlack.addActionListener(this);
		sweetSpotGroup.add(sweetSpotBlack);
		sweetSpotMenu.add(sweetSpotBlack);

		sweetSpotWhite = new JRadioButtonMenuItem(dimensionProvider, "White");
		sweetSpotWhite.addActionListener(this);
		sweetSpotGroup.add(sweetSpotWhite);
		sweetSpotMenu.add(sweetSpotWhite);

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

	private ColorIcon createColorIcon(Color chatBackgroundColor) {
		Dimension dimension = dimensionProvider.unscaledDimension(Component.MENU_COLOR_ICON);
		return new ColorIcon(dimension.width, dimension.height, chatBackgroundColor);
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

		Arrays.stream(this.getComponents()).filter(comp -> comp != gameModeMenu).forEach(this::remove);

		// Create and store the appropriate game mode menu
		if (getClient().getMode() == ClientMode.REPLAY) {
			if (gameModeMenu == null) {
				gameModeMenu = new ReplayMenu(getClient(), dimensionProvider, getClient().getCommunication(), styleProvider, layoutSettings, sketchManager);
				add(gameModeMenu);
			}
		} else {
			if (this.gameModeMenu != null) {
				this.remove(gameModeMenu);
			}
			gameModeMenu = new StandardGameMenu(getClient(), dimensionProvider, getClient().getCommunication(), styleProvider, layoutSettings);
			add(gameModeMenu);
		}

		setupMenu = new SetupMenu(getClient(), dimensionProvider, styleProvider, layoutSettings);
		add(setupMenu);

		userSettingsMenu = new UserSettingsMenu(getClient(), dimensionProvider, styleProvider, layoutSettings);
		add(userSettingsMenu);

		createUserSettingsMenu();

		missingPlayersMenu = new MissingPlayersMenu(getClient(), dimensionProvider, styleProvider, layoutSettings);
		add(missingPlayersMenu);

		inducementsMenu = new InducementsMenu(getClient(), dimensionProvider, styleProvider, layoutSettings);
		add(inducementsMenu);

		cardsMenu = new CardsMenu(getClient(), dimensionProvider, styleProvider, layoutSettings);
		add(cardsMenu);

		prayersMenu = new PrayersMenu(getClient(), dimensionProvider, styleProvider, layoutSettings);
		add(prayersMenu);

		optionsMenu = new OptionsMenu(getClient(), dimensionProvider, styleProvider, layoutSettings);
		add(optionsMenu);

		helpMenu = new HelpMenu(getClient(), dimensionProvider, styleProvider, layoutSettings);
		add(helpMenu);

		refresh();
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

	public void refresh() {
		// Refresh the mode-specific menu
		if (gameModeMenu != null) {
			gameModeMenu.refresh();
		}

		userSettingsMenu.refresh();

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

		String pitchMarkingsRowSetting = getClient().getProperty(SETTING_PITCH_MARKINGS_ROW);
		fPitchMarkingsRowOffMenuItem.setSelected(true);
		fPitchMarkingsRowOnMenuItem.setSelected(IClientPropertyValue.SETTING_PITCH_MARKINGS_ROW_ON.equals(pitchMarkingsRowSetting));

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

		String showCratersAndBloodspotsSetting = getClient().getProperty(SETTING_SHOW_CRATERS_AND_BLOODSPOTS);
		showCratersAndBloodsptsMenuItem.setSelected(true);
		hideCratersAndBloodsptsMenuItem.setSelected(IClientPropertyValue.SETTING_CRATERS_AND_BLOODSPOTS_HIDE.equals(showCratersAndBloodspotsSetting));

		String sweetSpotSetting = getClient().getProperty(SETTING_SWEET_SPOT);
		sweetSpotOff.setSelected(true);
		sweetSpotBlack.setSelected(IClientPropertyValue.SETTING_SWEET_SPOT_BLACK.equals(sweetSpotSetting));
		sweetSpotWhite.setSelected(IClientPropertyValue.SETTING_SWEET_SPOT_WHITE.equals(sweetSpotSetting));


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

		refreshUi |= refreshColorMenu(SETTING_FONT_COLOR_PLAYER_MARKER_HOME, homePlayerMarkerFontColor,
			styleProvider::getPlayerMarkerHome, styleProvider::setPlayerMarkerHome);

		refreshUi |= refreshColorMenu(SETTING_FONT_COLOR_PLAYER_MARKER_AWAY, awayPlayerMarkerFontColor,
			styleProvider::getPlayerMarkerAway, styleProvider::setPlayerMarkerAway);

		refreshUi |= refreshColorMenu(SETTING_FONT_COLOR_ADDITIONAL_PLAYER_MARKER_HOME, additionalHomePlayerMarkerFontColor,
			styleProvider::getAdditionalPlayerMarkerHome, styleProvider::setAdditionalPlayerMarkerHome);

		refreshUi |= refreshColorMenu(SETTING_FONT_COLOR_ADDITIONAL_PLAYER_MARKER_AWAY, additionalAwayPlayerMarkerFontColor,
			styleProvider::getAdditionalPlayerMarkerAway, styleProvider::setAdditionalPlayerMarkerAway);

		refreshUi |= refreshColorMenu(SETTING_FONT_COLOR_FIELD_MARKER, fieldMarkerFontColor,
			styleProvider::getFieldMarker, styleProvider::setFieldMarker);

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


		playerMarkingMenu.setEnabled(ClientMode.REPLAY != getClient().getMode());

		updateMissingPlayers();
		updateInducements();
		updateActiveCards();
		updatePrayers();
		updateGameOptions();

		boolean askForReRoll = ((GameOptionBoolean) getClient().getGame().getOptions().getOptionWithDefault(GameOptionId.ALLOW_BALL_AND_CHAIN_RE_ROLL)).isEnabled();

		reRollBallAndChainPanelMenu.setText(askForReRoll ? "Ask to Re-Roll Ball & Chain Movement" : "Ask for Whirling Dervish");


		if (getClient().getUserInterface() != null && refreshUi) {
			getClient().getUserInterface().initComponents(true);
		}
	}

	private void resetColors(CommonProperty[] settings) {
		for (CommonProperty setting : settings) {
			getClient().setProperty(setting, String.valueOf(StyleProvider.defaults.get(setting).getRGB()));
		}
		getClient().saveUserSettings(true);
	}

	public void changeState(ClientStateId pStateId) {
		setupMenu.changeState(pStateId);
	}

	public void dialogClosed(IDialog pDialog) {
		fClient.getUserInterface().dialogClosed(pDialog);
	}

	private void updateScaleProperty(double scalingFactor) {
		getClient().setProperty(CommonProperty.SETTING_SCALE_FACTOR, Double.toString(scalingFactor));
		getClient().saveUserSettings(true);
	}

	public void increaseScaling() {
		updateScaleProperty(layoutSettings.largerScale());
	}

	public void decreaseScaling() {
		updateScaleProperty(layoutSettings.smallerScale());
	}

	public void resetScaling() {
		updateScaleProperty(LayoutSettings.BASE_SCALE_FACTOR);
	}


	public void showDialog(IDialog pDialog) {
		fClient.getUserInterface().showDialog(pDialog, this);
	}

	public void updateGameOptions() {
		optionsMenu.refresh();
	}

	public void updateInducements() {
		inducementsMenu.refresh();
	}

	private void updateActiveCards() {
		cardsMenu.refresh();
	}


	private void updatePrayers() {
		prayersMenu.refresh();
	}

	public void updateMissingPlayers() {
		missingPlayersMenu.refresh();
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

	public void actionPerformed(ActionEvent e) {
		javax.swing.JMenuItem source = (javax.swing.JMenuItem) (e.getSource());
		if (source == null) {
			return;
		}

		helpMenu.actionPerformed(e);
		gameModeMenu.actionPerformed(e);
		missingPlayersMenu.actionPerformed(e);
		cardsMenu.actionPerformed(e);
		inducementsMenu.actionPerformed(e);
		prayersMenu.actionPerformed(e);
		setupMenu.actionPerformed(e);
		optionsMenu.actionPerformed(e);


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
		if (source == fPitchMarkingsRowOffMenuItem) {
			getClient().setProperty(SETTING_PITCH_MARKINGS_ROW, IClientPropertyValue.SETTING_PITCH_MARKINGS_ROW_OFF);
			getClient().saveUserSettings(true);
		}
		if (source == fPitchMarkingsRowOnMenuItem) {
			getClient().setProperty(SETTING_PITCH_MARKINGS_ROW, IClientPropertyValue.SETTING_PITCH_MARKINGS_ROW_ON);
			getClient().saveUserSettings(true);
		}

		if (source == showCratersAndBloodsptsMenuItem) {
			getClient().setProperty(SETTING_SHOW_CRATERS_AND_BLOODSPOTS, IClientPropertyValue.SETTING_CRATERS_AND_BLOODSPOTS_SHOW);
			getClient().saveUserSettings(true);
		}
		if (source == hideCratersAndBloodsptsMenuItem) {
			getClient().setProperty(SETTING_SHOW_CRATERS_AND_BLOODSPOTS, IClientPropertyValue.SETTING_CRATERS_AND_BLOODSPOTS_HIDE);
			getClient().saveUserSettings(true);
		}

		if (source == sweetSpotOff) {
			getClient().setProperty(SETTING_SWEET_SPOT, IClientPropertyValue.SETTING_SWEET_SPOT_OFF);
			getClient().saveUserSettings(true);
		}
		if (source == sweetSpotBlack) {
			getClient().setProperty(SETTING_SWEET_SPOT, IClientPropertyValue.SETTING_SWEET_SPOT_BLACK);
			getClient().saveUserSettings(true);
		}
		if (source == sweetSpotWhite) {
			getClient().setProperty(SETTING_SWEET_SPOT, IClientPropertyValue.SETTING_SWEET_SPOT_WHITE);
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

		if (source == homePlayerMarkerFontColor) {
			Color defaultColor = styleProvider.getPlayerMarkerHome();
			Color color = JColorChooser.showDialog(this, "Choose home player marker color", defaultColor);
			if (color != null) {
				getClient().setProperty(SETTING_FONT_COLOR_PLAYER_MARKER_HOME, String.valueOf(color.getRGB()));
				getClient().saveUserSettings(true);
			}
		}

		if (source == awayPlayerMarkerFontColor) {
			Color defaultColor = styleProvider.getPlayerMarkerAway();
			Color color = JColorChooser.showDialog(this, "Choose away player marker color", defaultColor);
			if (color != null) {
				getClient().setProperty(SETTING_FONT_COLOR_PLAYER_MARKER_AWAY, String.valueOf(color.getRGB()));
				getClient().saveUserSettings(true);
			}
		}

		if (source == additionalHomePlayerMarkerFontColor) {
			Color defaultColor = styleProvider.getAdditionalPlayerMarkerHome();
			Color color = JColorChooser.showDialog(this, "Choose additional home player marker color", defaultColor);
			if (color != null) {
				getClient().setProperty(SETTING_FONT_COLOR_ADDITIONAL_PLAYER_MARKER_HOME, String.valueOf(color.getRGB()));
				getClient().saveUserSettings(true);
			}
		}

		if (source == additionalAwayPlayerMarkerFontColor) {
			Color defaultColor = styleProvider.getAdditionalPlayerMarkerAway();
			Color color = JColorChooser.showDialog(this, "Choose additional away player marker color", defaultColor);
			if (color != null) {
				getClient().setProperty(SETTING_FONT_COLOR_ADDITIONAL_PLAYER_MARKER_AWAY, String.valueOf(color.getRGB()));
				getClient().saveUserSettings(true);
			}
		}

		if (source == fieldMarkerFontColor) {
			Color defaultColor = styleProvider.getFieldMarker();
			Color color = JColorChooser.showDialog(this, "Choose field marker color", defaultColor);
			if (color != null) {
				getClient().setProperty(SETTING_FONT_COLOR_FIELD_MARKER, String.valueOf(color.getRGB()));
				getClient().saveUserSettings(true);
			}
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


	}

}
