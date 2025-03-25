package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.Constant;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JRadioButton;
import com.fumbbl.ffb.client.ui.swing.JTextField;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class DialogReplayModeChoice extends Dialog implements ActionListener  {

	private final JRadioButton offlineButton = new JRadioButton(dimensionProvider(), "Offline");
	private final JRadioButton onlineButton = new JRadioButton(dimensionProvider(), "Online");
	private final JTextField nameField = new JTextField(dimensionProvider(), Constant.REPLAY_NAME_MAX_LENGTH);
	private final JButton okButton = new JButton(dimensionProvider(), "OK");
	private boolean online;
	private String replayName;

	public DialogReplayModeChoice(FantasyFootballClient pClient) {
		super(pClient, "Choose Replay Mode", false);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		ButtonGroup group = new ButtonGroup();
		group.add(offlineButton);
		group.add(offlineButton);
		mainPanel.add(offlineButton);

		JPanel onlinePanel = new JPanel();
		onlinePanel.setLayout(new BoxLayout(onlinePanel, BoxLayout.X_AXIS));
		onlinePanel.add(onlineButton);
		onlinePanel.add(Box.createHorizontalStrut(5));
		onlinePanel.add(nameField);

		mainPanel.add(onlinePanel);

		mainPanel.add(okButton);

		okButton.addActionListener(this);
		offlineButton.addActionListener(this);
		onlineButton.addActionListener(this);

		update();

		pack();
		setLocationToCenter();
	}

	@Override
	public DialogId getId() {
		return DialogId.REPLAY_MODE_CHOICE;
	}

	public boolean isOnline() {
		return online;
	}

	public String getReplayName() {
		return replayName;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == onlineButton) {
			online = true;
			update();
		}
		if (e.getSource() == offlineButton) {
			online = false;
			update();
		}
		if (e.getSource() == okButton) {
			replayName = nameField.getText();
			this.getCloseListener().dialogClosed(this);
		}
	}

	private void update() {
		offlineButton.setSelected(!online);
		onlineButton.setSelected(online);
		okButton.setEnabled(StringTool.isProvided(nameField.getText()) || !online);
	}
}
