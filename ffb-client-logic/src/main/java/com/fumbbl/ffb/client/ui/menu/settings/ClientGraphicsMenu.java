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

import javax.swing.ButtonGroup;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import static com.fumbbl.ffb.CommonProperty.SETTING_ICONS;

public class ClientGraphicsMenu extends FfbMenu {

	private JRadioButtonMenuItem fIconsAbstract;
	private JRadioButtonMenuItem fIconsRosterOpponent;
	private JRadioButtonMenuItem fIconsRosterBoth;
	private JRadioButtonMenuItem fIconsTeam;

	private JRadioButtonMenuItem swapTeamColorsOffMenuItem;
	private JRadioButtonMenuItem swapTeamColorsOnMenuItem;

	protected ClientGraphicsMenu(FantasyFootballClient client, DimensionProvider dimensionProvider, StyleProvider styleProvider, LayoutSettings layoutSettings) {
		super("Client Graphics", client, dimensionProvider, styleProvider, layoutSettings);
		setMnemonic(KeyEvent.VK_C);
	}

	@Override
	protected void init() {
		createIconsMenu();
	}

	@Override
	protected void refresh() {
		
		String iconsSetting = client.getProperty(SETTING_ICONS);
		fIconsTeam.setSelected(true);
		fIconsRosterOpponent.setSelected(IClientPropertyValue.SETTING_ICONS_ROSTER_OPPONENT.equals(iconsSetting));
		fIconsRosterBoth.setSelected(IClientPropertyValue.SETTING_ICONS_ROSTER_BOTH.equals(iconsSetting));
		fIconsAbstract.setSelected(IClientPropertyValue.SETTING_ICONS_ABSTRACT.equals(iconsSetting));

		String swapTeamColorsSetting = client.getProperty(CommonProperty.SETTING_SWAP_TEAM_COLORS);
		swapTeamColorsOffMenuItem.setSelected(true);
		boolean swapTeamColors = IClientPropertyValue.SETTING_SWAP_TEAM_COLORS_ON.equals(swapTeamColorsSetting);
		swapTeamColorsOnMenuItem.setSelected(swapTeamColors);

		boolean refreshUi = false;
		
		if (swapTeamColors != styleProvider.isSwapTeamColors()) {
			styleProvider.setSwapTeamColors(swapTeamColors);
			refreshUi = true;
		}

		if (client.getUserInterface() != null && refreshUi) {
			client.getUserInterface().initComponents(true);
		}

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

}
