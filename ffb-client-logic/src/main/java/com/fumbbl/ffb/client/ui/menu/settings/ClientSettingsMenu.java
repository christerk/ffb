package com.fumbbl.ffb.client.ui.menu.settings;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.client.ClientLayout;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FontCache;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.StyleProvider;
import com.fumbbl.ffb.client.dialog.DialogAutoMarking;
import com.fumbbl.ffb.client.dialog.DialogInformation;
import com.fumbbl.ffb.client.dialog.DialogScalingFactor;
import com.fumbbl.ffb.client.dialog.DialogSoundVolume;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.client.ui.menu.FfbMenu;
import com.fumbbl.ffb.client.ui.swing.JMenu;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.ui.swing.JRadioButtonMenuItem;
import com.fumbbl.ffb.marking.SortMode;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import static com.fumbbl.ffb.CommonProperty.SETTING_LOCAL_ICON_CACHE;
import static com.fumbbl.ffb.CommonProperty.SETTING_LOCAL_ICON_CACHE_PATH;
import static com.fumbbl.ffb.CommonProperty.SETTING_LOG;
import static com.fumbbl.ffb.CommonProperty.SETTING_LOG_DIR;
import static com.fumbbl.ffb.CommonProperty.SETTING_LOG_MODE;
import static com.fumbbl.ffb.CommonProperty.SETTING_PLAYER_MARKING_TYPE;
import static com.fumbbl.ffb.CommonProperty.SETTING_SCALE_FACTOR;
import static com.fumbbl.ffb.CommonProperty.SETTING_SOUND_MODE;
import static com.fumbbl.ffb.CommonProperty.SETTING_SOUND_VOLUME;
import static com.fumbbl.ffb.CommonProperty.SETTING_UI_LAYOUT;

public class ClientSettingsMenu extends FfbMenu {

	private JMenuItem fSoundVolumeItem;
	private JRadioButtonMenuItem fSoundOnMenuItem;
	private JRadioButtonMenuItem fSoundMuteSpectatorsMenuItem;
	private JRadioButtonMenuItem fSoundOffMenuItem;

	private JMenu playerMarkingMenu;
	private JRadioButtonMenuItem playersMarkingManualMenuItem;
	private JRadioButtonMenuItem playersMarkingAutoMenuItem;
	private JRadioButtonMenuItem playersMarkingAutoNoSortMenuItem;

	private JMenuItem scalingItem;

	private JRadioButtonMenuItem pitchLandscapeMenuItem;
	private JRadioButtonMenuItem pitchPortraitMenuItem;
	private JRadioButtonMenuItem layoutSquareMenuItem;
	private JRadioButtonMenuItem layoutWideMenuItem;

	private JRadioButtonMenuItem logOnMenuItem;
	private JRadioButtonMenuItem logOffMenuItem;
	private JMenuItem logSelectMenuItem;
	private JMenuItem openLogFolderMenuItem;

	private JRadioButtonMenuItem localIconCacheOffMenuItem;
	private JRadioButtonMenuItem localIconCacheOnMenuItem;
	private JMenuItem localIconCacheSelectMenuItem;
	
	protected ClientSettingsMenu(FantasyFootballClient client, DimensionProvider dimensionProvider, StyleProvider styleProvider, LayoutSettings layoutSettings) {
		super("Client Settings", client, dimensionProvider, styleProvider, layoutSettings);
		setMnemonic(KeyEvent.VK_S);
	}

	@Override
	public void init() {
		createSoundMenu();
		createMarkingMenu();
		createClientUiMenu();
		createScaleItem();
		createLogMenu();
		createLocalIconCacheMenu();
	}

	@Override
	public boolean refresh() {
		playerMarkingMenu.setEnabled(ClientMode.REPLAY != client.getMode());
		
		String playerMarkingSetting = client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE);
		playersMarkingManualMenuItem.setSelected(true);
		playersMarkingAutoMenuItem.setSelected(IClientPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO.equals(playerMarkingSetting));
		playersMarkingAutoNoSortMenuItem.setSelected(IClientPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO_NO_SORT.equals(playerMarkingSetting));

		String soundSetting = client.getProperty(CommonProperty.SETTING_SOUND_MODE);
		fSoundOnMenuItem.setSelected(true);
		fSoundMuteSpectatorsMenuItem.setSelected(IClientPropertyValue.SETTING_SOUND_MUTE_SPECTATORS.equals(soundSetting));
		fSoundOffMenuItem.setSelected(IClientPropertyValue.SETTING_SOUND_OFF.equals(soundSetting));

		String orientationSetting = client.getProperty(CommonProperty.SETTING_UI_LAYOUT);
		pitchLandscapeMenuItem.setSelected(true);
		pitchPortraitMenuItem.setSelected(IClientPropertyValue.SETTING_LAYOUT_PORTRAIT.equals(orientationSetting));
		layoutSquareMenuItem.setSelected(IClientPropertyValue.SETTING_LAYOUT_SQUARE.equals(orientationSetting));
		layoutWideMenuItem.setSelected(IClientPropertyValue.SETTING_LAYOUT_WIDE.equals(orientationSetting));

		String logModeSetting = client.getProperty(SETTING_LOG_MODE);
		logOnMenuItem.setSelected(true);
		logOffMenuItem.setSelected(IClientPropertyValue.SETTING_LOG_OFF.equals(logModeSetting));

		String localIconCacheSetting = client.getProperty(CommonProperty.SETTING_LOCAL_ICON_CACHE);
		localIconCacheOffMenuItem.setSelected(true);
		localIconCacheOnMenuItem.setSelected(IClientPropertyValue.SETTING_LOCAL_ICON_CACHE_ON.equals(localIconCacheSetting));
		
		boolean refreshUi = updateScaling();
		refreshUi |= updateOrientation();

		return refreshUi;
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

		if (source == logOffMenuItem) {
			client.setProperty(SETTING_LOG_MODE, IClientPropertyValue.SETTING_LOG_OFF);
			client.saveUserSettings(false);
		}
		if (source == logOnMenuItem) {
			client.setProperty(SETTING_LOG_MODE, IClientPropertyValue.SETTING_LOG_ON);
			if (!logFolderValid()) {
				selectLogFolder();
			}
			client.saveUserSettings(true);
		}
		if (source == logSelectMenuItem) {
			selectLogFolder();
			client.saveUserSettings(true);
		}

		if (source == openLogFolderMenuItem) {
			try {
				Desktop.getDesktop().open(new File(client.getLogFolder()));
			} catch (IOException ex) {
				client.logWithOutGameId(ex);
			}
		}

		if (source == localIconCacheOffMenuItem) {
			client.setProperty(SETTING_LOCAL_ICON_CACHE, IClientPropertyValue.SETTING_LOCAL_ICON_CACHE_OFF);
			client.saveUserSettings(false);
		}
		if (source == localIconCacheOnMenuItem) {
			client.setProperty(SETTING_LOCAL_ICON_CACHE, IClientPropertyValue.SETTING_LOCAL_ICON_CACHE_ON);
			if (!iconCacheValid()) {
				selectIconCacheFolder();
			}
			client.saveUserSettings(true);
		}
		if (source == localIconCacheSelectMenuItem) {
			selectIconCacheFolder();
			client.saveUserSettings(true);
		}

		if (source == playersMarkingAutoMenuItem) {
			client.setProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE, IClientPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO);
			client.saveUserSettings(true);
			client.getCommunication().sendUpdatePlayerMarkings(true, SortMode.DEFAULT);

			if (!IClientPropertyValue.SETTING_HIDE_AUTO_MARKING_DIALOG.equals(client.getProperty(CommonProperty.SETTING_SHOW_AUTO_MARKING_DIALOG))) {
				showDialog(DialogAutoMarking.create(client, true));
			}
		}

		if (source == playersMarkingAutoNoSortMenuItem) {
			client.setProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE, IClientPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO_NO_SORT);
			client.saveUserSettings(true);
			client.getCommunication().sendUpdatePlayerMarkings(true, SortMode.NONE);

			if (!IClientPropertyValue.SETTING_HIDE_AUTO_MARKING_DIALOG.equals(client.getProperty(CommonProperty.SETTING_SHOW_AUTO_MARKING_DIALOG))) {
				showDialog(DialogAutoMarking.create(client, true));
			}
		}

		if (source == playersMarkingManualMenuItem) {
			client.setProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE, IClientPropertyValue.SETTING_PLAYER_MARKING_TYPE_MANUAL);
			client.saveUserSettings(true);
			client.getCommunication().sendUpdatePlayerMarkings(false, null);
		}
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private boolean iconCacheValid() {
		return validFolder(client.getProperty(SETTING_LOCAL_ICON_CACHE_PATH));
	}

	private void selectIconCacheFolder() {
		File folder = newIconCacheFolder();

		if (folder == null) {
			if (!iconCacheValid()) {
				client.setProperty(SETTING_LOCAL_ICON_CACHE_PATH, null);
				client.setProperty(SETTING_LOCAL_ICON_CACHE, IClientPropertyValue.SETTING_LOCAL_ICON_CACHE_OFF);
				showError("Local Icon Cache", new String[]{"No folder selected and old path was invalid", "Cache has been disabled"});
			}
		} else {
			if (validFolder(folder)) {
				client.setProperty(SETTING_LOCAL_ICON_CACHE_PATH, folder.getAbsolutePath());
				if (!IClientPropertyValue.SETTING_LOCAL_ICON_CACHE_ON.equals(client.getProperty(SETTING_LOCAL_ICON_CACHE))) {
					client.setProperty(SETTING_LOCAL_ICON_CACHE, IClientPropertyValue.SETTING_LOCAL_ICON_CACHE_ON);
					showError("Local Icon Cache", new String[]{"Cache activated"});
				}
			} else {
				if (!iconCacheValid()) {
					client.setProperty(SETTING_LOCAL_ICON_CACHE_PATH, null);
					client.setProperty(SETTING_LOCAL_ICON_CACHE, IClientPropertyValue.SETTING_LOCAL_ICON_CACHE_OFF);
					showError("Local Icon Cache", new String[]{"Invalid folder selected and old path was invalid",
						"Cache has been disabled",
						"Folder has to be writeable."});
				} else {
					showError("Local Icon Cache", new String[]{"Invalid folder selected", "Folder has to be writeable"});
				}
			}
		}

	}

	private File newIconCacheFolder() {
		String oldValue = client.getProperty(SETTING_LOCAL_ICON_CACHE_PATH);
		return getFolder(oldValue);
	}
	
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private boolean logFolderValid() {
		return validFolder(client.getLogFolder());
	}

	private void selectLogFolder() {
		File folder = newLogFolder();

		if (folder == null) {
			if (!logFolderValid()) {
				client.setProperty(SETTING_LOG_DIR, null);
				client.setProperty(SETTING_LOG_MODE, IClientPropertyValue.SETTING_LOG_OFF);
				showError("Logging", new String[]{"No folder selected and old path was invalid", "Logging has been disabled"});
			}
		} else {
			if (validFolder(folder)) {
				client.setProperty(SETTING_LOG_DIR, folder.getAbsolutePath());
				if (IClientPropertyValue.SETTING_LOG_OFF.equals(client.getProperty(SETTING_LOG_MODE))) {
					client.setProperty(SETTING_LOG_MODE, IClientPropertyValue.SETTING_LOCAL_ICON_CACHE_ON);
					showError("Logging", new String[]{"Logging activated"});
				}
			} else {
				if (!logFolderValid()) {
					client.setProperty(SETTING_LOG_DIR, null);
					client.setProperty(SETTING_LOG_MODE, IClientPropertyValue.SETTING_LOG_OFF);
					showError("Logging", new String[]{"Invalid folder selected and old path was invalid",
						"Logging has been disabled",
						"Folder has to be writeable."});
				} else {
					showError("Logging", new String[]{"Invalid folder selected", "Folder has to be writeable"});
				}
			}
		}
	}

	private File newLogFolder() {
		String oldValue = client.getLogFolder();
		return getFolder(oldValue);
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

	private void createLogMenu() {
		JMenu logMenu = new JMenu(dimensionProvider, SETTING_LOG);
		logMenu.setMnemonic(KeyEvent.VK_L);
		add(logMenu);

		ButtonGroup logGroup = new ButtonGroup();

		logOnMenuItem = new JRadioButtonMenuItem(dimensionProvider, "On");
		logOnMenuItem.addActionListener(this);
		logGroup.add(logOnMenuItem);
		logMenu.add(logOnMenuItem);

		logOffMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Off");
		logOffMenuItem.addActionListener(this);
		logGroup.add(logOffMenuItem);
		logMenu.add(logOffMenuItem);

		logSelectMenuItem = new JMenuItem(dimensionProvider, "Select log folder");
		logSelectMenuItem.setMnemonic(KeyEvent.VK_S);
		logSelectMenuItem.addActionListener(this);
		logMenu.add(logSelectMenuItem);

		openLogFolderMenuItem = new JMenuItem(dimensionProvider, "Open log folder");
		openLogFolderMenuItem.setMnemonic(KeyEvent.VK_O);
		openLogFolderMenuItem.addActionListener(this);
		logMenu.add(openLogFolderMenuItem);
	}

	private void createLocalIconCacheMenu() {
		JMenu localIconCacheMenu = new JMenu(dimensionProvider, SETTING_LOCAL_ICON_CACHE);
		localIconCacheMenu.setMnemonic(KeyEvent.VK_L);
		add(localIconCacheMenu);

		ButtonGroup localIconCacheGroup = new ButtonGroup();

		localIconCacheOffMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Off");
		localIconCacheOffMenuItem.setMnemonic(KeyEvent.VK_F);
		localIconCacheOffMenuItem.addActionListener(this);
		localIconCacheGroup.add(localIconCacheOffMenuItem);
		localIconCacheMenu.add(localIconCacheOffMenuItem);

		localIconCacheOnMenuItem = new JRadioButtonMenuItem(dimensionProvider, "On");
		localIconCacheOnMenuItem.setMnemonic(KeyEvent.VK_N);
		localIconCacheOnMenuItem.addActionListener(this);
		localIconCacheGroup.add(localIconCacheOnMenuItem);
		localIconCacheMenu.add(localIconCacheOnMenuItem);

		localIconCacheSelectMenuItem = new JMenuItem(dimensionProvider, "Select folder");
		localIconCacheSelectMenuItem.setMnemonic(KeyEvent.VK_S);
		localIconCacheSelectMenuItem.addActionListener(this);
		localIconCacheMenu.add(localIconCacheSelectMenuItem);
	}

	private void createMarkingMenu() {
		playerMarkingMenu = new JMenu(dimensionProvider, SETTING_PLAYER_MARKING_TYPE);
		playerMarkingMenu.setMnemonic(KeyEvent.VK_L);
		add(playerMarkingMenu);

		ButtonGroup playerMarkingGroup = new ButtonGroup();

		playersMarkingAutoMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Automatic (Alphabetical Order)");
		playersMarkingAutoMenuItem.addActionListener(this);
		playerMarkingGroup.add(playersMarkingAutoMenuItem);
		playerMarkingMenu.add(playersMarkingAutoMenuItem);

		playersMarkingAutoNoSortMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Automatic (Site Order)");
		playersMarkingAutoNoSortMenuItem.addActionListener(this);
		playerMarkingGroup.add(playersMarkingAutoNoSortMenuItem);
		playerMarkingMenu.add(playersMarkingAutoNoSortMenuItem);

		playersMarkingManualMenuItem = new JRadioButtonMenuItem(dimensionProvider, "Manual");
		playersMarkingManualMenuItem.addActionListener(this);
		playerMarkingGroup.add(playersMarkingManualMenuItem);
		playerMarkingMenu.add(playersMarkingManualMenuItem);
	}
	
	private void showError(String title, String[] error) {
		DialogInformation messageDialog = new DialogInformation(client, title,
			error, DialogInformation.OK_DIALOG, false);
		messageDialog.showDialog(this);
	}

	private boolean validFolder(String path) {
		if (!StringTool.isProvided(path)) {
			return false;
		}
		File file = new File(path);
		return validFolder(file);
	}

	private boolean validFolder(File file) {
		return file.exists() && file.isDirectory() && file.canWrite();
	}

	private File getFolder(String oldValue) {
		JFileChooser chooser = new JFileChooser(oldValue);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = chooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}
}
