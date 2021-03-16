package com.balancedbytes.games.ffb.client.dialog;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.IIconProperty;
import com.balancedbytes.games.ffb.client.IconCache;
import com.balancedbytes.games.ffb.dialog.DialogId;

/**
 * 
 * @author Kalimar
 */
public class DialogCoinChoice extends Dialog implements ActionListener, KeyListener {

	private JButton fButtonHeads;
	private JButton fButtonTails;
	private boolean fChoiceHeads;

	public DialogCoinChoice(FantasyFootballClient pClient) {

		super(pClient, "Coin Throw", false);

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		JPanel panelButtons = new JPanel();
		panelButtons.setLayout(new FlowLayout(FlowLayout.CENTER));

		IconCache iconCache = getClient().getUserInterface().getIconCache();
		ImageIcon iconHeads = new ImageIcon(iconCache.getIconByProperty(IIconProperty.GAME_COIN_HEADS));
		fButtonHeads = new JButton(iconHeads);
		fButtonHeads.addActionListener(this);
		fButtonHeads.addKeyListener(this);
		panelButtons.add(fButtonHeads);

		ImageIcon iconTails = new ImageIcon(iconCache.getIconByProperty(IIconProperty.GAME_COIN_TAILS));
		fButtonTails = new JButton(iconTails);
		fButtonTails.addActionListener(this);
		fButtonTails.addKeyListener(this);
		panelButtons.add(fButtonTails);

		JPanel panelText = new JPanel();
		panelText.setLayout(new FlowLayout(FlowLayout.CENTER));
		JLabel label = new JLabel("Heads or Tails?");
		label.setFont(new Font(label.getFont().getName(), Font.BOLD, label.getFont().getSize()));
		panelText.add(label);

		getContentPane().add(panelButtons);
		getContentPane().add(panelText);

		pack();
		setLocationToCenter();

	}

	public void actionPerformed(ActionEvent pActionEvent) {
		fChoiceHeads = (pActionEvent.getSource() == fButtonHeads);
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	public boolean isChoiceHeads() {
		return fChoiceHeads;
	}

	public DialogId getId() {
		return DialogId.COIN_CHOICE;
	}

	public void keyPressed(KeyEvent pKeyEvent) {
	}

	public void keyReleased(KeyEvent pKeyEvent) {
		boolean keyHandled = true;
		switch (pKeyEvent.getKeyCode()) {
		case KeyEvent.VK_H:
			fChoiceHeads = true;
			break;
		case KeyEvent.VK_T:
			fChoiceHeads = false;
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
