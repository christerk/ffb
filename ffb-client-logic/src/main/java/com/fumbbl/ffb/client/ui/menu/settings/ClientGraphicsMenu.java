package com.fumbbl.ffb.client.ui.menu.settings;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.client.Component;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.StyleProvider;
import com.fumbbl.ffb.client.ui.ColorIcon;
import com.fumbbl.ffb.client.ui.menu.FfbMenu;
import com.fumbbl.ffb.client.ui.swing.JMenu;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.ui.swing.JRadioButtonMenuItem;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.ButtonGroup;
import javax.swing.JColorChooser;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.fumbbl.ffb.CommonProperty.SETTING_BACKGROUND_CHAT;
import static com.fumbbl.ffb.CommonProperty.SETTING_BACKGROUND_FRAME;
import static com.fumbbl.ffb.CommonProperty.SETTING_BACKGROUND_LOG;
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
import static com.fumbbl.ffb.CommonProperty.SETTING_ICONS;
import static com.fumbbl.ffb.CommonProperty.SETTING_PITCH_CUSTOMIZATION;
import static com.fumbbl.ffb.CommonProperty.SETTING_PITCH_MARKINGS;
import static com.fumbbl.ffb.CommonProperty.SETTING_PITCH_MARKINGS_ROW;
import static com.fumbbl.ffb.CommonProperty.SETTING_PITCH_WEATHER;
import static com.fumbbl.ffb.CommonProperty.SETTING_SHOW_CRATERS_AND_BLOODSPOTS;
import static com.fumbbl.ffb.CommonProperty.SETTING_SWEET_SPOT;
import static com.fumbbl.ffb.CommonProperty.SETTING_TEAM_LOGOS;
import static com.fumbbl.ffb.CommonProperty.SETTING_TZ_COLOR_AWAY;
import static com.fumbbl.ffb.CommonProperty.SETTING_TZ_COLOR_HOME;

public class ClientGraphicsMenu extends FfbMenu {

	private JRadioButtonMenuItem fIconsAbstract;
	private JRadioButtonMenuItem fIconsRosterOpponent;
	private JRadioButtonMenuItem fIconsRosterBoth;
	private JRadioButtonMenuItem fIconsTeam;

	private JRadioButtonMenuItem swapTeamColorsOffMenuItem;
	private JRadioButtonMenuItem swapTeamColorsOnMenuItem;

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
	private JMenuItem tzColorHome;
	private JMenuItem tzColorAway;

	private JMenuItem resetColors;
	private JMenuItem resetBackgroundColors;
	private JMenuItem resetFontColors;

	protected ClientGraphicsMenu(FantasyFootballClient client, DimensionProvider dimensionProvider, StyleProvider styleProvider, LayoutSettings layoutSettings) {
		super("Client Graphics", client, dimensionProvider, styleProvider, layoutSettings);
		setMnemonic(KeyEvent.VK_C);
	}

	@Override
	public void init() {
		createIconsMenu();
		createPitchMenu();
		createBackgroundMenu();
		createColorsMenu();

		addSeparator();
		createRestoreMenu();
	}

	@Override
	public boolean refresh() {

		String pitchCustomizationSetting = client.getProperty(CommonProperty.SETTING_PITCH_CUSTOMIZATION);
		fCustomPitchMenuItem.setSelected(true);
		fDefaultPitchMenuItem.setSelected(IClientPropertyValue.SETTING_PITCH_DEFAULT.equals(pitchCustomizationSetting));
		fBasicPitchMenuItem.setSelected(IClientPropertyValue.SETTING_PITCH_BASIC.equals(pitchCustomizationSetting));

		String pitchMarkingsSetting = client.getProperty(CommonProperty.SETTING_PITCH_MARKINGS);
		fPitchMarkingsOffMenuItem.setSelected(true);
		fPitchMarkingsOnMenuItem.setSelected(IClientPropertyValue.SETTING_PITCH_MARKINGS_ON.equals(pitchMarkingsSetting));

		String pitchMarkingsRowSetting = client.getProperty(SETTING_PITCH_MARKINGS_ROW);
		fPitchMarkingsRowOffMenuItem.setSelected(true);
		fPitchMarkingsRowOnMenuItem.setSelected(IClientPropertyValue.SETTING_PITCH_MARKINGS_ROW_ON.equals(pitchMarkingsRowSetting));

		String teamLogosSetting = client.getProperty(CommonProperty.SETTING_TEAM_LOGOS);
		fTeamLogoBothMenuItem.setSelected(true);
		fTeamLogoOwnMenuItem.setSelected(IClientPropertyValue.SETTING_TEAM_LOGOS_OWN.equals(teamLogosSetting));
		fTeamLogoNoneMenuItem.setSelected(IClientPropertyValue.SETTING_TEAM_LOGOS_NONE.equals(teamLogosSetting));

		String pitchWeatherSetting = client.getProperty(CommonProperty.SETTING_PITCH_WEATHER);
		fPitchWeatherOnMenuItem.setSelected(true);
		fPitchWeatherOffMenuItem.setSelected(IClientPropertyValue.SETTING_PITCH_WEATHER_OFF.equals(pitchWeatherSetting));

		String showCratersAndBloodspotsSetting = client.getProperty(SETTING_SHOW_CRATERS_AND_BLOODSPOTS);
		showCratersAndBloodsptsMenuItem.setSelected(true);
		hideCratersAndBloodsptsMenuItem.setSelected(IClientPropertyValue.SETTING_CRATERS_AND_BLOODSPOTS_HIDE.equals(showCratersAndBloodspotsSetting));

		String sweetSpotSetting = client.getProperty(SETTING_SWEET_SPOT);
		sweetSpotOff.setSelected(true);
		sweetSpotBlack.setSelected(IClientPropertyValue.SETTING_SWEET_SPOT_BLACK.equals(sweetSpotSetting));
		sweetSpotWhite.setSelected(IClientPropertyValue.SETTING_SWEET_SPOT_WHITE.equals(sweetSpotSetting));
		
		String iconsSetting = client.getProperty(SETTING_ICONS);
		fIconsTeam.setSelected(true);
		fIconsRosterOpponent.setSelected(IClientPropertyValue.SETTING_ICONS_ROSTER_OPPONENT.equals(iconsSetting));
		fIconsRosterBoth.setSelected(IClientPropertyValue.SETTING_ICONS_ROSTER_BOTH.equals(iconsSetting));
		fIconsAbstract.setSelected(IClientPropertyValue.SETTING_ICONS_ABSTRACT.equals(iconsSetting));

		String swapTeamColorsSetting = client.getProperty(CommonProperty.SETTING_SWAP_TEAM_COLORS);
		swapTeamColorsOffMenuItem.setSelected(true);
		boolean swapTeamColors = IClientPropertyValue.SETTING_SWAP_TEAM_COLORS_ON.equals(swapTeamColorsSetting);
		swapTeamColorsOnMenuItem.setSelected(swapTeamColors);
		
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

		refreshUi |= refreshColorMenu(SETTING_TZ_COLOR_HOME, tzColorHome,
			styleProvider::getTackleZoneHomeUnswapped, styleProvider::setTackleZoneHome);

		refreshUi |= refreshColorMenu(SETTING_TZ_COLOR_AWAY, tzColorAway,
			styleProvider::getTackleZoneAwayUnswapped, styleProvider::setTackleZoneAway);

		String frameBackgroundSetting = client.getProperty(CommonProperty.SETTING_BACKGROUND_FRAME);
		frameBackgroundIcons.setSelected(true);
		boolean useColorForFrames = IClientPropertyValue.SETTING_BACKGROUND_FRAME_COLOR.equals(frameBackgroundSetting);
		frameBackgroundColor.setSelected(useColorForFrames);

		refreshUi |= refreshFrameBackgroundMenu(useColorForFrames);
		
		if (swapTeamColors != styleProvider.isSwapTeamColors()) {
			styleProvider.setSwapTeamColors(swapTeamColors);
			refreshUi = true;
		}

		return refreshUi;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		javax.swing.JMenuItem source = (javax.swing.JMenuItem) (e.getSource());

		if (source == fIconsTeam) {
			client.setProperty(SETTING_ICONS, IClientPropertyValue.SETTING_ICONS_TEAM);
			client.saveUserSettings(true);
		}
		if (source == fIconsRosterOpponent) {
			client.setProperty(SETTING_ICONS, IClientPropertyValue.SETTING_ICONS_ROSTER_OPPONENT);
			client.saveUserSettings(true);
		}
		if (source == fIconsRosterBoth) {
			client.setProperty(SETTING_ICONS, IClientPropertyValue.SETTING_ICONS_ROSTER_BOTH);
			client.saveUserSettings(true);
		}
		if (source == fIconsAbstract) {
			client.setProperty(SETTING_ICONS, IClientPropertyValue.SETTING_ICONS_ABSTRACT);
			client.saveUserSettings(true);
		}

		if (source == swapTeamColorsOffMenuItem) {
			client.setProperty(CommonProperty.SETTING_SWAP_TEAM_COLORS, IClientPropertyValue.SETTING_SWAP_TEAM_COLORS_OFF);
			client.saveUserSettings(true);
		}

		if (source == swapTeamColorsOnMenuItem) {
			client.setProperty(CommonProperty.SETTING_SWAP_TEAM_COLORS, IClientPropertyValue.SETTING_SWAP_TEAM_COLORS_ON);
			client.saveUserSettings(true);
		}

		if (source == fCustomPitchMenuItem) {
			client.setProperty(CommonProperty.SETTING_PITCH_CUSTOMIZATION, IClientPropertyValue.SETTING_PITCH_CUSTOM);
			client.saveUserSettings(true);
		}
		if (source == fDefaultPitchMenuItem) {
			client.setProperty(CommonProperty.SETTING_PITCH_CUSTOMIZATION, IClientPropertyValue.SETTING_PITCH_DEFAULT);
			client.saveUserSettings(true);
		}
		if (source == fBasicPitchMenuItem) {
			client.setProperty(CommonProperty.SETTING_PITCH_CUSTOMIZATION, IClientPropertyValue.SETTING_PITCH_BASIC);
			client.saveUserSettings(true);
		}
		if (source == fPitchMarkingsOffMenuItem) {
			client.setProperty(CommonProperty.SETTING_PITCH_MARKINGS, IClientPropertyValue.SETTING_PITCH_MARKINGS_OFF);
			client.saveUserSettings(true);
		}
		if (source == fPitchMarkingsOnMenuItem) {
			client.setProperty(CommonProperty.SETTING_PITCH_MARKINGS, IClientPropertyValue.SETTING_PITCH_MARKINGS_ON);
			client.saveUserSettings(true);
		}
		if (source == fPitchMarkingsRowOffMenuItem) {
			client.setProperty(SETTING_PITCH_MARKINGS_ROW, IClientPropertyValue.SETTING_PITCH_MARKINGS_ROW_OFF);
			client.saveUserSettings(true);
		}
		if (source == fPitchMarkingsRowOnMenuItem) {
			client.setProperty(SETTING_PITCH_MARKINGS_ROW, IClientPropertyValue.SETTING_PITCH_MARKINGS_ROW_ON);
			client.saveUserSettings(true);
		}

		if (source == showCratersAndBloodsptsMenuItem) {
			client.setProperty(SETTING_SHOW_CRATERS_AND_BLOODSPOTS, IClientPropertyValue.SETTING_CRATERS_AND_BLOODSPOTS_SHOW);
			client.saveUserSettings(true);
		}
		if (source == hideCratersAndBloodsptsMenuItem) {
			client.setProperty(SETTING_SHOW_CRATERS_AND_BLOODSPOTS, IClientPropertyValue.SETTING_CRATERS_AND_BLOODSPOTS_HIDE);
			client.saveUserSettings(true);
		}

		if (source == sweetSpotOff) {
			client.setProperty(SETTING_SWEET_SPOT, IClientPropertyValue.SETTING_SWEET_SPOT_OFF);
			client.saveUserSettings(true);
		}
		if (source == sweetSpotBlack) {
			client.setProperty(SETTING_SWEET_SPOT, IClientPropertyValue.SETTING_SWEET_SPOT_BLACK);
			client.saveUserSettings(true);
		}
		if (source == sweetSpotWhite) {
			client.setProperty(SETTING_SWEET_SPOT, IClientPropertyValue.SETTING_SWEET_SPOT_WHITE);
			client.saveUserSettings(true);
		}

		if (source == fTeamLogoBothMenuItem) {
			client.setProperty(CommonProperty.SETTING_TEAM_LOGOS, IClientPropertyValue.SETTING_TEAM_LOGOS_BOTH);
			client.saveUserSettings(true);
		}
		if (source == fTeamLogoOwnMenuItem) {
			client.setProperty(CommonProperty.SETTING_TEAM_LOGOS, IClientPropertyValue.SETTING_TEAM_LOGOS_OWN);
			client.saveUserSettings(true);
		}
		if (source == fTeamLogoNoneMenuItem) {
			client.setProperty(CommonProperty.SETTING_TEAM_LOGOS, IClientPropertyValue.SETTING_TEAM_LOGOS_NONE);
			client.saveUserSettings(true);
		}
		if (source == fCustomPitchMenuItem) {
			client.setProperty(CommonProperty.SETTING_PITCH_CUSTOMIZATION, IClientPropertyValue.SETTING_PITCH_CUSTOM);
			client.saveUserSettings(true);
		}
		if (source == fPitchWeatherOnMenuItem) {
			client.setProperty(CommonProperty.SETTING_PITCH_WEATHER, IClientPropertyValue.SETTING_PITCH_WEATHER_ON);
			client.saveUserSettings(true);
		}
		if (source == fPitchWeatherOffMenuItem) {
			client.setProperty(CommonProperty.SETTING_PITCH_WEATHER, IClientPropertyValue.SETTING_PITCH_WEATHER_OFF);
			client.saveUserSettings(true);
		}

		if (source == chatBackground) {
			Color defaultColor = styleProvider.getChatBackground();
			Color color = JColorChooser.showDialog(this, "Choose chat background color", defaultColor);
			if (color != null) {
				client.setProperty(CommonProperty.SETTING_BACKGROUND_CHAT, String.valueOf(color.getRGB()));
				client.saveUserSettings(true);
			}
		}

		if (source == logBackground) {
			Color defaultColor = styleProvider.getLogBackground();
			Color color = JColorChooser.showDialog(this, "Choose log background color", defaultColor);
			if (color != null) {
				client.setProperty(CommonProperty.SETTING_BACKGROUND_LOG, String.valueOf(color.getRGB()));
				client.saveUserSettings(true);
			}
		}

		if (source == frameBackgroundIcons) {
			client.setProperty(CommonProperty.SETTING_BACKGROUND_FRAME, IClientPropertyValue.SETTING_BACKGROUND_FRAME_ICONS);
			client.saveUserSettings(true);
		}

		if (source == frameBackgroundColor) {
			client.setProperty(CommonProperty.SETTING_BACKGROUND_FRAME, IClientPropertyValue.SETTING_BACKGROUND_FRAME_COLOR);
			Color defaultColor = styleProvider.getFrameBackground();
			Color color = JColorChooser.showDialog(this, "Choose sidebar and scoreboard background color", defaultColor);
			if (color != null) {
				client.setProperty(CommonProperty.SETTING_BACKGROUND_FRAME_COLOR, String.valueOf(color.getRGB()));
			}
			client.saveUserSettings(true);
		}

		if (source == textFontColor) {
			Color defaultColor = styleProvider.getText();
			Color color = JColorChooser.showDialog(this, "Choose text color", defaultColor);
			if (color != null) {
				client.setProperty(CommonProperty.SETTING_FONT_COLOR_TEXT, String.valueOf(color.getRGB()));
				client.saveUserSettings(true);
			}
		}

		if (source == awayFontColor) {
			Color defaultColor = styleProvider.getAwayUnswapped();
			Color color = JColorChooser.showDialog(this, "Choose away color", defaultColor);
			if (color != null) {
				client.setProperty(CommonProperty.SETTING_FONT_COLOR_AWAY, String.valueOf(color.getRGB()));
				client.saveUserSettings(true);
			}
		}

		if (source == homeFontColor) {
			Color defaultColor = styleProvider.getHomeUnswapped();
			Color color = JColorChooser.showDialog(this, "Choose home color", defaultColor);
			if (color != null) {
				client.setProperty(CommonProperty.SETTING_FONT_COLOR_HOME, String.valueOf(color.getRGB()));
				client.saveUserSettings(true);
			}
		}

		if (source == specFontColor) {
			Color defaultColor = styleProvider.getSpec();
			Color color = JColorChooser.showDialog(this, "Choose spec color", defaultColor);
			if (color != null) {
				client.setProperty(CommonProperty.SETTING_FONT_COLOR_SPEC, String.valueOf(color.getRGB()));
				client.saveUserSettings(true);
			}
		}

		if (source == adminFontColor) {
			Color defaultColor = styleProvider.getAdmin();
			Color color = JColorChooser.showDialog(this, "Choose admin color", defaultColor);
			if (color != null) {
				client.setProperty(CommonProperty.SETTING_FONT_COLOR_ADMIN, String.valueOf(color.getRGB()));
				client.saveUserSettings(true);
			}
		}

		if (source == devFontColor) {
			Color defaultColor = styleProvider.getDev();
			Color color = JColorChooser.showDialog(this, "Choose dev color", defaultColor);
			if (color != null) {
				client.setProperty(CommonProperty.SETTING_FONT_COLOR_DEV, String.valueOf(color.getRGB()));
				client.saveUserSettings(true);
			}
		}

		if (source == frameFontColor) {
			Color defaultColor = styleProvider.getFrame();
			Color color = JColorChooser.showDialog(this, "Choose frane color", defaultColor);
			if (color != null) {
				client.setProperty(CommonProperty.SETTING_FONT_COLOR_FRAME, String.valueOf(color.getRGB()));
				client.saveUserSettings(true);
			}
		}

		if (source == frameFontShadowColor) {
			Color defaultColor = styleProvider.getFrameShadow();
			Color color = JColorChooser.showDialog(this, "Choose frame shadow color", defaultColor);
			if (color != null) {
				client.setProperty(CommonProperty.SETTING_FONT_COLOR_FRAME_SHADOW, String.valueOf(color.getRGB()));
				client.saveUserSettings(true);
			}
		}

		if (source == inputFontColor) {
			Color defaultColor = styleProvider.getInput();
			Color color = JColorChooser.showDialog(this, "Choose frame shadow color", defaultColor);
			if (color != null) {
				client.setProperty(CommonProperty.SETTING_FONT_COLOR_INPUT, String.valueOf(color.getRGB()));
				client.saveUserSettings(true);
			}
		}

		if (source == homePlayerMarkerFontColor) {
			Color defaultColor = styleProvider.getPlayerMarkerHome();
			Color color = JColorChooser.showDialog(this, "Choose home player marker color", defaultColor);
			if (color != null) {
				client.setProperty(SETTING_FONT_COLOR_PLAYER_MARKER_HOME, String.valueOf(color.getRGB()));
				client.saveUserSettings(true);
			}
		}

		if (source == awayPlayerMarkerFontColor) {
			Color defaultColor = styleProvider.getPlayerMarkerAway();
			Color color = JColorChooser.showDialog(this, "Choose away player marker color", defaultColor);
			if (color != null) {
				client.setProperty(SETTING_FONT_COLOR_PLAYER_MARKER_AWAY, String.valueOf(color.getRGB()));
				client.saveUserSettings(true);
			}
		}

		if (source == additionalHomePlayerMarkerFontColor) {
			Color defaultColor = styleProvider.getAdditionalPlayerMarkerHome();
			Color color = JColorChooser.showDialog(this, "Choose additional home player marker color", defaultColor);
			if (color != null) {
				client.setProperty(SETTING_FONT_COLOR_ADDITIONAL_PLAYER_MARKER_HOME, String.valueOf(color.getRGB()));
				client.saveUserSettings(true);
			}
		}

		if (source == additionalAwayPlayerMarkerFontColor) {
			Color defaultColor = styleProvider.getAdditionalPlayerMarkerAway();
			Color color = JColorChooser.showDialog(this, "Choose additional away player marker color", defaultColor);
			if (color != null) {
				client.setProperty(SETTING_FONT_COLOR_ADDITIONAL_PLAYER_MARKER_AWAY, String.valueOf(color.getRGB()));
				client.saveUserSettings(true);
			}
		}

		if (source == fieldMarkerFontColor) {
			Color defaultColor = styleProvider.getFieldMarker();
			Color color = JColorChooser.showDialog(this, "Choose field marker color", defaultColor);
			if (color != null) {
				client.setProperty(SETTING_FONT_COLOR_FIELD_MARKER, String.valueOf(color.getRGB()));
				client.saveUserSettings(true);
			}
		}

		if (source == tzColorAway) {
			Color defaultColor = styleProvider.getTackleZoneAwayUnswapped();
			Color color = JColorChooser.showDialog(this, "Choose away tacklezone color", defaultColor);
			if (color != null) {
				client.setProperty(SETTING_TZ_COLOR_AWAY, String.valueOf(color.getRGB()));
				client.saveUserSettings(true);
			}
		}

		if (source == tzColorHome) {
			Color defaultColor = styleProvider.getTackleZoneHomeUnswapped();
			Color color = JColorChooser.showDialog(this, "Choose home tacklezone color", defaultColor);
			if (color != null) {
				client.setProperty(SETTING_TZ_COLOR_HOME, String.valueOf(color.getRGB()));
				client.saveUserSettings(true);
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

	private void createIconsMenu() {
		JMenu fIconsMenu = new JMenu(dimensionProvider, SETTING_ICONS);
		fIconsMenu.setMnemonic(KeyEvent.VK_I);
		add(fIconsMenu);

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

	private void createPitchMenu() {
		JMenu fPitchMenu = new JMenu(dimensionProvider, "Pitch");
		fPitchMenu.setMnemonic(KeyEvent.VK_P);
		add(fPitchMenu);

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

	private void createBackgroundMenu() {
		JMenu backgroundStyles = new JMenu(dimensionProvider, "Background styles");
		backgroundStyles.setMnemonic(KeyEvent.VK_B);
		add(backgroundStyles);
		addColorItem(SETTING_BACKGROUND_CHAT, styleProvider.getChatBackground(), backgroundStyles, (item) -> chatBackground = item);
		addColorItem(SETTING_BACKGROUND_LOG, styleProvider.getLogBackground(), backgroundStyles, (item) -> logBackground = item);
		backgroundStyles.add(createFrameBackgroundMenu());
	}

	private void createColorsMenu() {
		JMenu colors = new JMenu(dimensionProvider, "Colors");
		colors.setMnemonic(KeyEvent.VK_F);
		add(colors);
		addColorItem(SETTING_FONT_COLOR_TEXT, styleProvider.getText(), colors, (item) -> textFontColor = item);
		addColorItem(SETTING_FONT_COLOR_AWAY, styleProvider.getAwayUnswapped(), colors, (item) -> awayFontColor = item);
		addColorItem(SETTING_FONT_COLOR_HOME, styleProvider.getHomeUnswapped(), colors, (item) -> homeFontColor = item);
		addColorItem(SETTING_FONT_COLOR_SPEC, styleProvider.getSpec(), colors, (item) -> specFontColor = item);
		addColorItem(SETTING_FONT_COLOR_ADMIN, styleProvider.getAdmin(), colors, (item) -> adminFontColor = item);
		addColorItem(SETTING_FONT_COLOR_DEV, styleProvider.getDev(), colors, (item) -> devFontColor = item);
		addColorItem(SETTING_FONT_COLOR_INPUT, styleProvider.getInput(), colors, (item) -> inputFontColor = item);
		addColorItem(SETTING_FONT_COLOR_FRAME, styleProvider.getFrame(), colors, (item) -> frameFontColor = item);
		addColorItem(SETTING_FONT_COLOR_FRAME_SHADOW, styleProvider.getFrameShadow(), colors, (item) -> frameFontShadowColor = item);
		addColorItem(SETTING_FONT_COLOR_PLAYER_MARKER_HOME, styleProvider.getPlayerMarkerHome(), colors, (item) -> homePlayerMarkerFontColor = item);
		addColorItem(SETTING_FONT_COLOR_PLAYER_MARKER_AWAY, styleProvider.getPlayerMarkerAway(), colors, (item) -> awayPlayerMarkerFontColor = item);
		addColorItem(SETTING_FONT_COLOR_ADDITIONAL_PLAYER_MARKER_HOME, styleProvider.getAdditionalPlayerMarkerHome(), colors, (item) -> additionalHomePlayerMarkerFontColor = item);
		addColorItem(SETTING_FONT_COLOR_ADDITIONAL_PLAYER_MARKER_AWAY, styleProvider.getAdditionalPlayerMarkerAway(), colors, (item) -> additionalAwayPlayerMarkerFontColor = item);
		addColorItem(SETTING_FONT_COLOR_FIELD_MARKER, styleProvider.getFieldMarker(), colors, (item) -> fieldMarkerFontColor = item);
		addColorItem(SETTING_TZ_COLOR_HOME, styleProvider.getTackleZoneHomeUnswapped(), colors, (item) -> tzColorHome = item);
		addColorItem(SETTING_TZ_COLOR_AWAY, styleProvider.getTackleZoneAwayUnswapped(), colors, (item) -> tzColorAway = item);
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

	private boolean refreshFrameBackgroundMenu(boolean useColor) {
		Color oldColor = styleProvider.getFrameBackground();
		Color newColor = null;

		if (useColor) {
			try {
				newColor = new Color(Integer.parseInt(client.getProperty(CommonProperty.SETTING_BACKGROUND_FRAME_COLOR)));
				frameBackgroundColor.setIcon(createColorIcon(newColor));
			} catch (NumberFormatException ex) {
				client.getFactorySource().logWithOutGameId(ex);
			}
		}

		styleProvider.setFrameBackground(newColor);

		return !Objects.equals(oldColor, newColor);
	}

	private boolean refreshColorMenu(CommonProperty key, JMenuItem customItem,
																	 Supplier<Color> oldColor, Consumer<Color> setter) {

		if (client.getUserInterface() == null) {
			return false;
		}

		String colorSetting = client.getProperty(key);

		Color color = null;
		if (!StringTool.isProvided(colorSetting)) {
			return false;
		}

		try {
			color = new Color(Integer.parseInt(colorSetting));
			customItem.setSelected(true);
		} catch (NumberFormatException ex) {
			client.getFactorySource().logWithOutGameId(ex);
		}

		if (color != null && !color.equals(oldColor.get())) {
			customItem.setIcon(createColorIcon(color));
			setter.accept(color);
			return true;
		}
		return false;
	}

	private void createRestoreMenu() {

		resetColors = new JMenuItem(dimensionProvider, "Reset all colors");
		resetColors.addActionListener(this);
		resetColors.setEnabled(true);
		add(resetColors);

		resetBackgroundColors = new JMenuItem(dimensionProvider, "Reset background styles");
		resetBackgroundColors.addActionListener(this);
		resetBackgroundColors.setEnabled(true);
		add(resetBackgroundColors);

		resetFontColors = new JMenuItem(dimensionProvider, "Reset colors");
		resetFontColors.addActionListener(this);
		resetFontColors.setEnabled(true);
		add(resetFontColors);
	}

	private void resetColors(CommonProperty[] settings) {
		for (CommonProperty setting : settings) {
			client.setProperty(setting, String.valueOf(StyleProvider.defaults.get(setting).getRGB()));
		}
		client.saveUserSettings(true);
	}
}
