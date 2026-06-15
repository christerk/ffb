package com.fumbbl.ffb.client.ui.menu.game;

import com.fumbbl.ffb.client.*;
import com.fumbbl.ffb.client.dialog.DialogGameStatistics;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.ui.menu.FfbMenu;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

public abstract class GameModeMenu extends FfbMenu {
    protected JMenuItem gameStatisticsMenuItem;
    protected ClientCommunication communication;
    private final FontCache fontCache;
    private final FontConfigRegistry fontConfigRegistry;

    protected GameModeMenu(String text,
                           FantasyFootballClient client,
                           DimensionProvider dimensionProvider,
                           ClientCommunication communication,
                           StyleProvider styleProvider,
                           LayoutSettings layoutSettings,
                           FontCache fontCache,
                           FontConfigRegistry fontConfigRegistry) {
        super(text, client, dimensionProvider, styleProvider, layoutSettings, fontCache, fontConfigRegistry);
        this.fontCache = fontCache;
        this.fontConfigRegistry = fontConfigRegistry;
        this.communication = communication;
    }

    @Override
    public void init() {
        createGameStatisticsMenuItem();
        createSpecificMenuItems();
    }

    protected abstract void createSpecificMenuItems();

    @Override
    public final boolean refresh() {
        super.refresh();
        boolean gameStarted = client.getGame() != null && client.getGame().getStarted() != null;
        gameStatisticsMenuItem.setEnabled(gameStarted);

        subClassRefresh();
        return false;
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