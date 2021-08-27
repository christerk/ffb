package com.fumbbl.ffb.client.dialog;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public abstract class DialogYesOrNoQuestion extends Dialog implements ActionListener, KeyListener {

	private JButton fButtonYes;
	private JButton fButtonNo;
	private boolean fChoiceYes;

	public DialogYesOrNoQuestion(FantasyFootballClient pClient, String pTitle, String pMessageLine) {
		this(pClient, pTitle, new String[] { pMessageLine }, null);
	}

	public DialogYesOrNoQuestion(FantasyFootballClient pClient, String pTitle, String[] pMessages, String pIconProperty) {
		this(pClient, pTitle, pMessages, pIconProperty, "Yes", (int) 'Y', "No", (int) 'N');
	}

	public DialogYesOrNoQuestion(FantasyFootballClient pClient, String pTitle, String[] pMessages, String pIconProperty,
			String pYesButtonText, int pYesButtonMnemonic, String pNoButtonText, int pNoButtonMnemonic) {

		super(pClient, pTitle, false);

		fButtonYes = new JButton(pYesButtonText);
		fButtonYes.addActionListener(this);
		fButtonYes.addKeyListener(this);
		fButtonYes.setMnemonic(pYesButtonMnemonic);

		fButtonNo = new JButton(pNoButtonText);
		fButtonNo.addActionListener(this);
		fButtonNo.addKeyListener(this);
		fButtonNo.setMnemonic(pNoButtonMnemonic);

		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
		for (int i = 0; i < pMessages.length; i++) {
			if (!StringTool.isProvided(pMessages[i])) {
				textPanel.add(Box.createVerticalStrut(5));
			} else {
				if (i > 0) {
					textPanel.add(Box.createVerticalStrut(5));
				}
				JPanel messagePanel = new JPanel();
				messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.X_AXIS));
				JLabel messageLabel = new JLabel(pMessages[i]);
				if (i == 0) {
					messageLabel.setFont(new Font(messageLabel.getFont().getName(), Font.BOLD, messageLabel.getFont().getSize()));
				}
				messagePanel.add(messageLabel);
				messagePanel.add(Box.createHorizontalGlue());
				textPanel.add(messagePanel);
			}
		}

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
		if (StringTool.isProvided(pIconProperty)) {
			BufferedImage icon = getClient().getUserInterface().getIconCache().getIconByProperty(pIconProperty);
			infoPanel.add(new JLabel(new ImageIcon(icon)));
			infoPanel.add(Box.createHorizontalStrut(5));
		}
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
		fChoiceYes = (pActionEvent.getSource() == fButtonYes);
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	public boolean isChoiceYes() {
		return fChoiceYes;
	}

	public DialogId getId() {
		return DialogId.YES_OR_NO_QUESTION;
	}

	public void keyPressed(KeyEvent pKeyEvent) {
	}

	public void keyReleased(KeyEvent pKeyEvent) {
		boolean keyHandled = true;
		switch (pKeyEvent.getKeyCode()) {
		case KeyEvent.VK_Y:
			fChoiceYes = true;
			break;
		case KeyEvent.VK_N:
			fChoiceYes = false;
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
