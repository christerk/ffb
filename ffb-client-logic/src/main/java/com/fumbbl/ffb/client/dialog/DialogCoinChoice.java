package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.RenderContext;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogId;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * 
 * @author Kalimar
 */
public class DialogCoinChoice extends Dialog implements ActionListener, KeyListener {

	private final JButton fButtonHeads;
	private final JButton fButtonTails;
	private boolean fChoiceHeads;

	public DialogCoinChoice(FantasyFootballClient pClient) {

		super(pClient, "Coin Throw", false);

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		JPanel panelButtons = new JPanel();
		panelButtons.setLayout(new FlowLayout(FlowLayout.CENTER));

		IconCache iconCache = getClient().getUserInterface().getIconCache();
		ImageIcon iconHeads = new ImageIcon(iconCache.getIconByProperty(IIconProperty.GAME_COIN_HEADS, RenderContext.ON_PITCH));
		fButtonHeads = new JButton(dimensionProvider(), iconHeads);
		fButtonHeads.addActionListener(this);
		fButtonHeads.addKeyListener(this);
		panelButtons.add(fButtonHeads);

		ImageIcon iconTails = new ImageIcon(iconCache.getIconByProperty(IIconProperty.GAME_COIN_TAILS, RenderContext.ON_PITCH));
		fButtonTails = new JButton(dimensionProvider(), iconTails);
		fButtonTails.addActionListener(this);
		fButtonTails.addKeyListener(this);
		panelButtons.add(fButtonTails);

		JPanel panelText = new JPanel();
		panelText.setLayout(new FlowLayout(FlowLayout.CENTER));
		JLabel label = new JLabel(dimensionProvider(), "Heads or Tails?");
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
