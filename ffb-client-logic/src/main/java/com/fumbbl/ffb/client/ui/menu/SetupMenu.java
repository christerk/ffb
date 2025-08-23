package com.fumbbl.ffb.client.ui.menu;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.IClientProperty;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.StyleProvider;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class SetupMenu extends FfbMenu {

	private JMenuItem fLoadSetupMenuItem;
	private JMenuItem fSaveSetupMenuItem;

	protected SetupMenu(FantasyFootballClient client, DimensionProvider dimensionProvider, StyleProvider styleProvider, LayoutSettings layoutSettings) {
		super("Team Setup", client, dimensionProvider, styleProvider, layoutSettings);
		setMnemonic(KeyEvent.VK_T);
	}

	@Override
	protected void init() {
		fLoadSetupMenuItem = new JMenuItem(dimensionProvider, "Load Setup", KeyEvent.VK_L);
		String menuSetupLoad = client.getProperty(IClientProperty.KEY_MENU_SETUP_LOAD);
		if (StringTool.isProvided(menuSetupLoad)) {
			fLoadSetupMenuItem.setAccelerator(KeyStroke.getKeyStroke(menuSetupLoad));
		}
		fLoadSetupMenuItem.addActionListener(this);
		add(fLoadSetupMenuItem);

		fSaveSetupMenuItem = new JMenuItem(dimensionProvider, "Save Setup", KeyEvent.VK_S);
		String menuSetupSave = client.getProperty(IClientProperty.KEY_MENU_SETUP_SAVE);
		if (StringTool.isProvided(menuSetupSave)) {
			fSaveSetupMenuItem.setAccelerator(KeyStroke.getKeyStroke(menuSetupSave));
		}
		fSaveSetupMenuItem.addActionListener(this);
		add(fSaveSetupMenuItem);
	}

	@Override
	public void refresh() {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		javax.swing.JMenuItem source = (javax.swing.JMenuItem) (e.getSource());

		if (source == fLoadSetupMenuItem) {
			client.getClientState().actionKeyPressed(ActionKey.MENU_SETUP_LOAD);
		}
		if (source == fSaveSetupMenuItem) {
			client.getClientState().actionKeyPressed(ActionKey.MENU_SETUP_SAVE);
		}
	}

	protected void changeState(ClientStateId pStateId) {
		Game game = client.getGame();
		if (pStateId == ClientStateId.SETUP) {
			boolean setupEnabled = (game.getTurnMode() != TurnMode.QUICK_SNAP && game.getTurnMode() != TurnMode.SOLID_DEFENCE);
			fLoadSetupMenuItem.setEnabled(setupEnabled);
			fSaveSetupMenuItem.setEnabled(setupEnabled);
		} else {
			fLoadSetupMenuItem.setEnabled(false);
			fSaveSetupMenuItem.setEnabled(false);
		}
	}
}
