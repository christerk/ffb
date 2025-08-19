package com.fumbbl.ffb.client.ui.menu;

import com.fumbbl.ffb.client.ClientData;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.PlayerIconFactory;
import com.fumbbl.ffb.client.StyleProvider;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.client.dialog.IDialogCloseListener;
import com.fumbbl.ffb.client.ui.swing.JMenu;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class FfbMenu extends JMenu implements ActionListener, IDialogCloseListener {
    protected final FantasyFootballClient client;
    protected final DimensionProvider dimensionProvider;
    protected final StyleProvider styleProvider;
    protected final LayoutSettings layoutSettings;

    protected FfbMenu(String text, FantasyFootballClient client, DimensionProvider dimensionProvider, StyleProvider styleProvider, LayoutSettings layoutSettings) {
        super(dimensionProvider, text);
        this.client = client;
        this.dimensionProvider = dimensionProvider;
        this.styleProvider = styleProvider;
        this.layoutSettings = layoutSettings;
        init();
    }

    protected abstract void init();

    protected abstract void refresh();

    public void showDialog(IDialog dialog) {
        client.getUserInterface().showDialog(dialog, this);
    }

    @Override
    public void dialogClosed(IDialog dialog) {
        client.getUserInterface().dialogClosed(dialog);
    }

    protected void addPlayerMenuItem(JMenu pPlayersMenu, Player<?> pPlayer, String pText) {
        if ((pPlayer == null) || !StringTool.isProvided(pText)) {
            return;
        }
        UserInterface userInterface = client.getUserInterface();
        PlayerIconFactory playerIconFactory = userInterface.getPlayerIconFactory();
        Icon playerIcon = new ImageIcon(playerIconFactory.getIcon(client, pPlayer, dimensionProvider));
        JMenuItem playersMenuItem = new JMenuItem(dimensionProvider, pText, playerIcon);
        playersMenuItem.addMouseListener(new MenuPlayerMouseListener(pPlayer));
        pPlayersMenu.add(playersMenuItem);
    }


    protected class MenuPlayerMouseListener extends MouseAdapter {

        private final Player<?> fPlayer;

        public MenuPlayerMouseListener(Player<?> pPlayer) {
            fPlayer = pPlayer;
        }

        public void mouseEntered(MouseEvent pMouseEvent) {
            ClientData clientData = client.getClientData();
            // do not interfere with dragging (MNG player reappears on pitch bug)
            if ((clientData.getSelectedPlayer() != fPlayer) && (clientData.getDragStartPosition() == null)) {
                clientData.setSelectedPlayer(fPlayer);
                client.getUserInterface().refreshSideBars();
            }
        }

    }
}