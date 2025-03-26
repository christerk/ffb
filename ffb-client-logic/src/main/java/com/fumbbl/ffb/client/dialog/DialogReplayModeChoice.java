package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.Constant;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JRadioButton;
import com.fumbbl.ffb.client.ui.swing.JTextField;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class DialogReplayModeChoice extends Dialog implements ActionListener, KeyListener, FocusListener {

	private final JRadioButton offlineButton = new JRadioButton(dimensionProvider(), "Offline (Legacy Behavior)");
	private final JRadioButton onlineButton = new JRadioButton(dimensionProvider(), "Online");
	private final JTextField nameField = new JTextField(dimensionProvider(), Constant.REPLAY_NAME_MAX_LENGTH);
	private final JButton okButton = new JButton(dimensionProvider(), "OK");
	private boolean online;
	private String replayName;

	public DialogReplayModeChoice(FantasyFootballClient pClient) {
		super(pClient, "Choose Replay Mode", false);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		ButtonGroup group = new ButtonGroup();
		group.add(offlineButton);
		group.add(onlineButton);

		JPanel offlinePanel = new JPanel();
		offlinePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		offlinePanel.add(offlineButton);

		JPanel onlinePanel = new JPanel();
		onlinePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		onlinePanel.add(onlineButton);
		onlinePanel.add(Box.createHorizontalStrut(5));
		onlinePanel.add(nameField);

		nameField.addKeyListener(this);
		nameField.setDocument(new FixedLengthDocument(Constant.REPLAY_NAME_MAX_LENGTH));

		mainPanel.add(offlinePanel);
		mainPanel.add(onlinePanel);
		mainPanel.add(Box.createVerticalStrut(5));
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(okButton);
		mainPanel.add(buttonPanel);

		okButton.addActionListener(this);
		offlineButton.addActionListener(this);
		onlineButton.addActionListener(this);

		offlineButton.addFocusListener(this);
		onlineButton.addFocusListener(this);

		offlineButton.setSelected(true);
		updateElements();

		this.getContentPane().add(mainPanel);

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
		if (e.getSource() == okButton) {
			replayName = nameField.getText().trim();
			this.getCloseListener().dialogClosed(this);
			return;
		}

		if (e.getSource() == onlineButton) {
			online = true;
		}
		if (e.getSource() == offlineButton) {
			online = false;
		}
		updateElements();
	}

	private void updateElements() {
		nameField.setEnabled(online);
		okButton.setEnabled(StringTool.isProvided(replayName) || !online);
	}


	@Override
	public void keyTyped(KeyEvent e) {
		replayName = nameField.getText().trim();
		updateElements();
	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void focusGained(FocusEvent e) {
		if (e.getSource() == offlineButton) {
			online = false;
		}
		if (e.getSource() == onlineButton) {
			online = true;
		}
		updateElements();
	}

	@Override
	public void focusLost(FocusEvent e) {

	}

	private static class FixedLengthDocument extends PlainDocument {
		private final int maxLength;

		public FixedLengthDocument(int maxLength) {
			this.maxLength = maxLength;
		}

		public void insertString(int offset, String str, AttributeSet a)
			throws BadLocationException {
			if (str != null && str.length() + getLength() <= maxLength) {
				super.insertString(offset, str, a);
			}
		}
	}
}
