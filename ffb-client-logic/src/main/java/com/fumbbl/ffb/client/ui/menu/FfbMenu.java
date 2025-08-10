package com.fumbbl.ffb.client.ui.menu;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.client.dialog.IDialogCloseListener;
import com.fumbbl.ffb.client.ui.swing.JMenu;

import java.awt.event.ActionListener;

public abstract class FfbMenu extends JMenu implements ActionListener, IDialogCloseListener {
    protected final FantasyFootballClient client;
    protected final DimensionProvider dimensionProvider;
    protected FfbMenu(String text, FantasyFootballClient client, DimensionProvider dimensionProvider) {
        super(dimensionProvider, text);
        this.client = client;
        this.dimensionProvider = dimensionProvider;
        init();
    }

    protected abstract void init();

    public void showDialog(IDialog dialog) {
        client.getUserInterface().showDialog(dialog, this);
    }

    @Override
    public void dialogClosed(IDialog dialog) {
        client.getUserInterface().dialogClosed(dialog);
    }

    protected FantasyFootballClient getClient() {
        return client;
    }
}