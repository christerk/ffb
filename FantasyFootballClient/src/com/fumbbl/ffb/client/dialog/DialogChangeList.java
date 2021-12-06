package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Game;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.InternalFrameEvent;
import java.awt.Dimension;

public class DialogChangeList extends Dialog {

	public DialogChangeList(FantasyFootballClient pClient) {

		super(pClient, "Change List", true);

		JScrollPane mainPane = new JScrollPane(createEditorPane());

		Game game = getClient().getGame();
		if (game.isTesting()) {
			mainPane.setPreferredSize(new Dimension(mainPane.getPreferredSize().width + 100, 500));
		} else {
			mainPane.setPreferredSize(new Dimension(mainPane.getPreferredSize().width + 10, 300));
		}

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
		infoPanel.add(mainPane);

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(infoPanel);

		pack();

		setLocationToCenter();

	}

	public DialogId getId() {
		return DialogId.CHANGE_LIST;
	}

	public void internalFrameClosing(InternalFrameEvent pE) {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	private JEditorPane createEditorPane() {

		JEditorPane aboutPane = new JEditorPane();
		aboutPane.setEditable(false);
		aboutPane.setContentType("text/html");

		aboutPane.setText("");
		aboutPane.setCaretPosition(0);

		return aboutPane;

	}

	protected void setLocationToCenter() {
		Dimension dialogSize = getSize();
		Dimension frameSize = getClient().getUserInterface().getSize();
		Dimension menuBarSize = getClient().getUserInterface().getGameMenuBar().getSize();
		// Dimension menuBarSize = getClient().getGameMenuBar().getSize();
		setLocation((frameSize.width - dialogSize.width) / 2,
			((frameSize.height - dialogSize.height) / 2) - menuBarSize.height);
	}

}
