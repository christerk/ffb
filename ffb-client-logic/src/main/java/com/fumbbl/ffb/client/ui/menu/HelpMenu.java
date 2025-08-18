package com.fumbbl.ffb.client.ui.menu;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.dialog.DialogAbout;
import com.fumbbl.ffb.client.dialog.DialogAutoMarking;
import com.fumbbl.ffb.client.dialog.DialogChangeList;
import com.fumbbl.ffb.client.dialog.DialogChatCommands;
import com.fumbbl.ffb.client.dialog.DialogKeyBindings;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class HelpMenu extends FfbMenu {
    private JMenuItem aboutMenuItem;
    private JMenuItem chatCommandsMenuItem;
    private JMenuItem keyBindingsMenuItem;
    private JMenuItem changeListItem;
    private JMenuItem autoMarkingItem;

    public HelpMenu(FantasyFootballClient client, DimensionProvider dimensionProvider) {
        super("Help", client, dimensionProvider);
        setMnemonic(KeyEvent.VK_H);
    }

    @Override
    protected void init() {
        aboutMenuItem = new JMenuItem(dimensionProvider, "About", KeyEvent.VK_A);
        aboutMenuItem.addActionListener(this);
        add(aboutMenuItem);

        chatCommandsMenuItem = new JMenuItem(dimensionProvider, "Chat Commands", KeyEvent.VK_C);
        chatCommandsMenuItem.addActionListener(this);
        add(chatCommandsMenuItem);

        changeListItem = new JMenuItem(dimensionProvider, "What's new?", KeyEvent.VK_W);
        changeListItem.addActionListener(this);
        add(changeListItem);

        autoMarkingItem = new JMenuItem(dimensionProvider, "Automarking Panel", KeyEvent.VK_M);
        autoMarkingItem.addActionListener(this);
        add(autoMarkingItem);

        keyBindingsMenuItem = new JMenuItem(dimensionProvider, "Key Bindings", KeyEvent.VK_K);
        keyBindingsMenuItem.addActionListener(this);
        add(keyBindingsMenuItem);
    }

    @Override
    protected void refresh() {
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();

        if (source == aboutMenuItem) {
            showDialog(new DialogAbout(client));
        } else if (source == chatCommandsMenuItem) {
            showDialog(new DialogChatCommands(client));
        } else if (source == changeListItem) {
            showDialog(new DialogChangeList(client));
        } else if (source == autoMarkingItem) {
            showDialog(DialogAutoMarking.create(client, false));
        } else if (source == keyBindingsMenuItem) {
            showDialog(new DialogKeyBindings(client));
        }
    }
}