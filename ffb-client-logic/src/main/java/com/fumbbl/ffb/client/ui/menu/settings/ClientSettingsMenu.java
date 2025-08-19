package com.fumbbl.ffb.client.ui.menu.settings;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.client.ClientLayout;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FontCache;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.StyleProvider;
import com.fumbbl.ffb.client.dialog.DialogScalingFactor;
import com.fumbbl.ffb.client.dialog.DialogSoundVolume;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.client.ui.menu.FfbMenu;
import com.fumbbl.ffb.client.ui.swing.JMenu;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.ui.swing.JRadioButtonMenuItem;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.ButtonGroup;
import javax.swing.UIManager;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import static com.fumbbl.ffb.CommonProperty.SETTING_SCALE_FACTOR;
import static com.fumbbl.ffb.CommonProperty.SETTING_SOUND_MODE;
import static com.fumbbl.ffb.CommonProperty.SETTING_SOUND_VOLUME;
import static com.fumbbl.ffb.CommonProperty.SETTING_UI_LAYOUT;

public class ClientSettingsMenu extends FfbMenu {

	private JMenuItem fSoundVolumeItem;
	private JRadioButtonMenuItem fSoundOnMenuItem;
	private JRadioButtonMenuItem fSoundMuteSpectatorsMenuItem;
	private JRadioButtonMenuItem fSoundOffMenuItem;

	private JMenuItem scalingItem;

	private JRadioButtonMenuItem pitchLandscapeMenuItem;
	private JRadioButtonMenuItem pitchPortraitMenuItem;
	private JRadioButtonMenuItem layoutSquareMenuItem;
	private JRadioButtonMenuItem layoutWideMenuItem;

	protected ClientSettingsMenu(FantasyFootballClient client, DimensionProvider dimensionProvider, StyleProvider styleProvider, LayoutSettings layoutSettings) {
		super("Client Settings", client, dimensionProvider, styleProvider, layoutSettings);
		setMnemonic(KeyEvent.VK_S);
	}

	@Override
	protected void init() {
		createSoundMenu();
		createClientUiMenu();
		createScaleItem();
	}

	@Override
	protected void refresh() {
		String soundSetting = client.getProperty(CommonProperty.SETTING_SOUND_MODE);
		fSoundOnMenuItem.setSelected(true);
		fSoundMuteSpectatorsMenuItem.setSelected(IClientPropertyValue.SETTING_SOUND_MUTE_SPECTATORS.equals(soundSetting));
		fSoundOffMenuItem.setSelected(IClientPropertyValue.SETTING_SOUND_OFF.equals(soundSetting));

		String orientationSetting = client.getProperty(CommonProperty.SETTING_UI_LAYOUT);
		pitchLandscapeMenuItem.setSelected(true);
		pitchPortraitMenuItem.setSelected(IClientPropertyValue.SETTING_LAYOUT_PORTRAIT.equals(orientationSetting));
		layoutSquareMenuItem.setSelected(IClientPropertyValue.SETTING_LAYOUT_SQUARE.equals(orientationSetting));
		layoutWideMenuItem.setSelected(IClientPropertyValue.SETTING_LAYOUT_WIDE.equals(orientationSetting));

		boolean refreshUi = updateScaling();
		refreshUi |= updateOrientation();

		if (client.getUserInterface() != null && refreshUi) {
			client.getUserInterface().initComponents(true);
		}
	}

	@Override
	public void dialogClosed(IDialog dialog) {
		super.dialogClosed(dialog);
		client.getUserInterface().dialogClosed(dialog);
		switch (dialog.getId()) {
			case SOUND_VOLUME:
				DialogSoundVolume volumeDialog = (DialogSoundVolume) dialog;
				client.setProperty(CommonProperty.SETTING_SOUND_VOLUME, Integer.toString(volumeDialog.getVolume()));
				client.saveUserSettings(true);
				break;
			case SCALING_FACTOR:
				DialogScalingFactor scalingDialog = (DialogScalingFactor) dialog;
				if (scalingDialog.getFactor() != null) {
					updateScaleProperty(scalingDialog.getFactor());
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		javax.swing.JMenuItem source = (javax.swing.JMenuItem) (e.getSource());

		if (source == fSoundVolumeItem) {
			showDialog(new DialogSoundVolume(client));
		}
		if (source == fSoundOffMenuItem) {
			client.setProperty(CommonProperty.SETTING_SOUND_MODE, IClientPropertyValue.SETTING_SOUND_OFF);
			client.saveUserSettings(false);
		}
		if (source == fSoundMuteSpectatorsMenuItem) {
			client.setProperty(CommonProperty.SETTING_SOUND_MODE, IClientPropertyValue.SETTING_SOUND_MUTE_SPECTATORS);
			client.saveUserSettings(false);
		}
		if (source == fSoundOnMenuItem) {
			client.setProperty(CommonProperty.SETTING_SOUND_MODE, IClientPropertyValue.SETTING_SOUND_ON);
			client.saveUserSettings(false);
		}

		if (source == scalingItem) {
			showDialog(new DialogScalingFactor(client));
		}

		if (source == pitchLandscapeMenuItem) {
			client.setProperty(CommonProperty.SETTING_UI_LAYOUT, IClientPropertyValue.SETTING_LAYOUT_LANDSCAPE);
			client.saveUserSettings(true);
		}
		if (source == pitchPortraitMenuItem) {
			client.setProperty(CommonProperty.SETTING_UI_LAYOUT, IClientPropertyValue.SETTING_LAYOUT_PORTRAIT);
			client.saveUserSettings(true);
		}
		if (source == layoutSquareMenuItem) {
			client.setProperty(CommonProperty.SETTING_UI_LAYOUT, IClientPropertyValue.SETTING_LAYOUT_SQUARE);
			client.saveUserSettings(true);
		}
		if (source == layoutWideMenuItem) {
			client.setProperty(CommonProperty.SETTING_UI_LAYOUT, IClientPropertyValue.SETTING_LAYOUT_WIDE);
			client.saveUserSettings(true);
		}
	}

	private void createSoundMenu() {
		JMenu fSoundMenu = new JMenu(dimensionProvider, SETTING_SOUND_MODE);
		fSoundMenu.setMnemonic(KeyEvent.VK_S);
		add(fSoundMenu);

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

	private void createScaleItem() {
		scalingItem = new JMenuItem(dimensionProvider, SETTING_SCALE_FACTOR.getValue());
		scalingItem.setMnemonic(KeyEvent.VK_E);
		scalingItem.addActionListener(this);
		add(scalingItem);
	}

	private boolean updateScaling() {
		String factorValue = client.getProperty(CommonProperty.SETTING_SCALE_FACTOR);
		if (StringTool.isProvided(factorValue)) {
			try {
				double factor = Double.parseDouble(factorValue);
				if (layoutSettings.getScale() != factor) {
					layoutSettings.setScale(factor);
					client.getUserInterface().getIconCache().clear();
					FontCache fontCache = client.getUserInterface().getFontCache();
					fontCache.clear();
					UIManager.put("ToolTip.font", fontCache.font(Font.PLAIN, 14, dimensionProvider));
					return true;
				}
			} catch (Exception ignored) {

			}
		}
		return false;
	}

	private void createClientUiMenu() {

		JMenu orientationMenu = new JMenu(dimensionProvider, SETTING_UI_LAYOUT);
		orientationMenu.setMnemonic(KeyEvent.VK_O);
		add(orientationMenu);

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

		layoutWideMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Wide");
		layoutWideMenuItem.addActionListener(this);
		orientationGroup.add(layoutWideMenuItem);
		orientationMenu.add(layoutWideMenuItem);
	}

	private boolean updateOrientation() {

		ClientLayout layout = ClientLayout.LANDSCAPE;

		String orientation = client.getProperty(CommonProperty.SETTING_UI_LAYOUT);

		if (orientation != null) {
			switch (orientation) {
				case IClientPropertyValue.SETTING_LAYOUT_PORTRAIT:
					layout = ClientLayout.PORTRAIT;
					break;
				case IClientPropertyValue.SETTING_LAYOUT_SQUARE:
					layout = ClientLayout.SQUARE;
					break;
				case IClientPropertyValue.SETTING_LAYOUT_WIDE:
					layout = ClientLayout.WIDE;
					break;
				default:
					break;
			}
		}

		if (layout != layoutSettings.getLayout()) {
			layoutSettings.setLayout(layout);
			if (client.getUserInterface() != null) {
				client.getUserInterface().getIconCache().clear();
				FontCache fontCache = client.getUserInterface().getFontCache();
				fontCache.clear();
				UIManager.put("ToolTip.font", fontCache.font(Font.PLAIN, 14, dimensionProvider));
			}
			return true;
		}

		return false;
	}

	private void updateScaleProperty(double scalingFactor) {
		client.setProperty(CommonProperty.SETTING_SCALE_FACTOR, Double.toString(scalingFactor));
		client.saveUserSettings(true);
	}
}
