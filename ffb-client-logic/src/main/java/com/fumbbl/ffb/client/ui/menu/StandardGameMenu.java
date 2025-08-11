package com.fumbbl.ffb.client.ui.menu;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.ConcedeGameStatus;
import com.fumbbl.ffb.IClientProperty;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class StandardGameMenu extends GameModeMenu {
    private static final String REPLAY_MODE_ON = "Replay Mode";
    private static final String REPLAY_MODE_OFF = "Spectator Mode";

    private JMenuItem gameReplayMenuItem;
    private JMenuItem gameConcessionMenuItem;

    public StandardGameMenu(FantasyFootballClient client, DimensionProvider dimensionProvider, ClientCommunication communication) {
        super("Game", client, dimensionProvider, communication);
		}

    @Override 
    protected void createSpecificMenuItems() {
        createReplayMenuItem();
        createConcessionMenuItem();
    }

    @Override
    public void subClassRefresh() {
        updateReplayMenuItem();
        updateConcessionMenuItem();
    }

    @Override
    public void subClassPerform(ActionEvent event) {
        Object source = event.getSource();
        if (source == gameReplayMenuItem) {
            gameReplayMenuItem.setText(client.getReplayer().isReplaying() ? REPLAY_MODE_ON : REPLAY_MODE_OFF);
            client.getClientState().actionKeyPressed(ActionKey.MENU_REPLAY);
        } else if (source == gameConcessionMenuItem) {
            communication.sendConcedeGame(ConcedeGameStatus.REQUESTED);
        }
    }

    private void createReplayMenuItem() {
        boolean replaying = client.getReplayer() != null && client.getReplayer().isReplaying();
        gameReplayMenuItem = new JMenuItem(dimensionProvider, replaying ? REPLAY_MODE_OFF : REPLAY_MODE_ON, KeyEvent.VK_R);
        String keyMenuReplay = client.getProperty(IClientProperty.KEY_MENU_REPLAY);
        if (StringTool.isProvided(keyMenuReplay)) {
            gameReplayMenuItem.setAccelerator(KeyStroke.getKeyStroke(keyMenuReplay));
        }
        gameReplayMenuItem.addActionListener(this);
        add(gameReplayMenuItem);
    }

    private void createConcessionMenuItem() {
        gameConcessionMenuItem = new JMenuItem(dimensionProvider, "Concede Game", KeyEvent.VK_C);
        gameConcessionMenuItem.addActionListener(this);
        gameConcessionMenuItem.setEnabled(false);
        add(gameConcessionMenuItem);
    }

    private void updateReplayMenuItem() {
        gameReplayMenuItem.setEnabled(ClientMode.SPECTATOR == client.getMode());
    }

    private void updateConcessionMenuItem() {
        boolean gameStarted = client.getGame() != null && client.getGame().getStarted() != null;
        boolean allowConcessions = client.getGame() != null &&
            ((GameOptionBoolean) client.getGame().getOptions().getOptionWithDefault(GameOptionId.ALLOW_CONCESSIONS)).isEnabled();

        gameConcessionMenuItem.setEnabled(allowConcessions && gameStarted &&
            client.getGame().isHomePlaying() && (ClientMode.PLAYER == client.getMode()) &&
            client.getGame().isConcessionPossible());
    }
}