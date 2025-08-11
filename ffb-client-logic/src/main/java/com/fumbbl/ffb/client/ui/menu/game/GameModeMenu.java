package com.fumbbl.ffb.client.ui.menu.game;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.dialog.DialogGameStatistics;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.ui.menu.FfbMenu;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

public abstract class GameModeMenu extends FfbMenu {
    protected JMenuItem gameStatisticsMenuItem;
    protected ClientCommunication communication;

    protected GameModeMenu(String text, FantasyFootballClient client, DimensionProvider dimensionProvider, ClientCommunication communication) {
        super(text, client, dimensionProvider);
        this.communication = communication;
    }

    @Override
    protected void init() {
        createGameStatisticsMenuItem();
        createSpecificMenuItems();
    }

    protected abstract void createSpecificMenuItems();

    public final void refresh() {
        boolean gameStarted = client.getGame() != null && client.getGame().getStarted() != null;
        gameStatisticsMenuItem.setEnabled(gameStarted);

        subClassRefresh();
    }

    protected abstract void subClassRefresh();

    protected void createGameStatisticsMenuItem() {
        gameStatisticsMenuItem = new JMenuItem(dimensionProvider, "Game Statistics", KeyEvent.VK_S);
        gameStatisticsMenuItem.addActionListener(this);
        gameStatisticsMenuItem.setEnabled(false);
        add(gameStatisticsMenuItem);
    }

    @Override
    public final void actionPerformed(ActionEvent e) {
        if (e.getSource() == gameStatisticsMenuItem) {
            showDialog(new DialogGameStatistics(client));
        } else {
            subClassPerform(e);
        }
    }

    protected abstract void subClassPerform(ActionEvent e);
}