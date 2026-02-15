package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogId;

import javax.swing.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author Kalimar
 */
public class DialogEndTurn extends Dialog implements ActionListener, KeyListener {

	public static int YES = 1;
	public static int NO = 2;

	private final JButton fButtonYes;
	private final JButton fButtonNo;
	private int fChoice;

	public DialogEndTurn(FantasyFootballClient pClient, boolean showForgo) {

		super(pClient, "End Turn", false);

		fButtonYes = new JButton(dimensionProvider(), "Yes");
		fButtonYes.addActionListener(this);
		fButtonYes.addKeyListener(this);
		fButtonYes.setMnemonic('Y');

		fButtonNo = new JButton(dimensionProvider(), "No");
		fButtonNo.addActionListener(this);
		fButtonNo.addKeyListener(this);
		fButtonNo.setMnemonic('N');

		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
		JPanel messagePanel = new JPanel();
		messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.X_AXIS));
		JLabel messageLabel = new JLabel(dimensionProvider(), "Do you really want to end your turn?");
		messageLabel.setFont(new Font(messageLabel.getFont().getName(), Font.BOLD, messageLabel.getFont().getSize()));
		messagePanel.add(Box.createHorizontalGlue());
		messagePanel.add(messageLabel);
		messagePanel.add(Box.createHorizontalGlue());
		textPanel.add(messagePanel);

		if (showForgo) {
			textPanel.add(Box.createVerticalStrut(dimensionProvider().scale(5)));
			JPanel forGoPanel = new JPanel();
			forGoPanel.setLayout(new BoxLayout(forGoPanel, BoxLayout.X_AXIS));
			JLabel forGoLabel = new JLabel(dimensionProvider(), "Remaining players forgo their activation (stalling check " +
				"still applies)");
			forGoLabel.setFont(new Font(forGoLabel.getFont().getName(), Font.PLAIN, forGoLabel.getFont().getSize() - 2));
			forGoLabel.add(Box.createHorizontalGlue());
			forGoPanel.add(forGoLabel);
			forGoLabel.add(Box.createHorizontalGlue());
			textPanel.add(forGoPanel);
		}

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
		infoPanel.add(textPanel);
		infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(fButtonYes);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(fButtonNo);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(infoPanel);
		getContentPane().add(buttonPanel);

		pack();
		setLocationToCenter();

	}

	public void actionPerformed(ActionEvent pActionEvent) {
		if (pActionEvent.getSource() == fButtonYes) {
			fChoice = YES;
		}
		if (pActionEvent.getSource() == fButtonNo) {
			fChoice = NO;
		}
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	public int getChoice() {
		return fChoice;
	}

	public DialogId getId() {
		return DialogId.END_TURN;
	}

	public void keyPressed(KeyEvent pKeyEvent) {
	}

	public void keyReleased(KeyEvent pKeyEvent) {
		boolean keyHandled = true;
		switch (pKeyEvent.getKeyCode()) {
		case KeyEvent.VK_Y:
			fChoice = YES;
			break;
		case KeyEvent.VK_N:
			fChoice = NO;
			break;
		default:
			keyHandled = false;
			break;
		}
		if (keyHandled) {
			if (getCloseListener() != null) {
				getCloseListener().dialogClosed(this);
			}
		}
	}

	public void keyTyped(KeyEvent pKeyEvent) {
	}

}
