package com.balancedbytes.games.ffb.client.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRollSources;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IconCache;
import com.balancedbytes.games.ffb.dialog.DialogBlockRollParameter;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class DialogBlockRoll extends Dialog implements ActionListener, KeyListener {

	private static final Color _COLOR_BLUE = new Color(12, 20, 136);
	private static final Color _COLOR_RED = new Color(128, 0, 0);

	private JButton[] fBlockDice;

	private JButton fButtonTeamReRoll;
	private JButton fButtonProReRoll;
	private JButton fButtonNoReRoll;

	private int fDiceIndex;
	private ReRollSource fReRollSource;

	private DialogBlockRollParameter fDialogParameter;

	private class BackgroundPanel extends JPanel {

		private Color fColor;

		public BackgroundPanel(Color pColor) {
			fColor = pColor;
			setOpaque(true);
		}

		protected void paintComponent(Graphics pGraphics) {
			if (!isOpaque()) {
				super.paintComponent(pGraphics);
			} else {
				Graphics2D g2d = (Graphics2D) pGraphics;
				Dimension size = getSize();
				g2d.setPaint(new GradientPaint(0, 0, fColor, size.width - 1, 0, Color.WHITE, false));
				g2d.fillRect(0, 0, size.width, size.height);
				setOpaque(false);
				super.paintComponent(pGraphics);
				setOpaque(true);
			}
		}

	}

	public DialogBlockRoll(FantasyFootballClient pClient, DialogBlockRollParameter pDialogParameter) {

		super(pClient, "Block Roll", false);

		fDiceIndex = -1;
		fDialogParameter = pDialogParameter;

		IconCache iconCache = getClient().getUserInterface().getIconCache();

		JPanel centerPanel = new BackgroundPanel((getDialogParameter().getNrOfDice() < 0) ? _COLOR_BLUE : _COLOR_RED);
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

		JPanel blockRollPanel = new JPanel();
		blockRollPanel.setOpaque(false);
		blockRollPanel.setLayout(new BoxLayout(blockRollPanel, BoxLayout.X_AXIS));
		blockRollPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		int[] blockRoll = getDialogParameter().getBlockRoll();
		fBlockDice = new JButton[blockRoll.length];
		boolean ownChoice = ((fDialogParameter.getNrOfDice() > 0)
				|| (!fDialogParameter.hasTeamReRollOption() && !fDialogParameter.hasProReRollOption()));
		for (int i = 0; i < fBlockDice.length; i++) {
			fBlockDice[i] = new JButton();
			fBlockDice[i].setOpaque(false);
			fBlockDice[i].setBounds(0, 0, 45, 45);
			fBlockDice[i].setFocusPainted(false);
			fBlockDice[i].setMargin(new Insets(5, 5, 5, 5));
			fBlockDice[i].setIcon(new ImageIcon(iconCache.getDiceIcon(blockRoll[i])));
			blockRollPanel.add(fBlockDice[i]);
			if (ownChoice) {
				fBlockDice[i].addActionListener(this);
				if (i == 0) {
					fBlockDice[i].setMnemonic(KeyEvent.VK_1);
				}
				if (i == 1) {
					fBlockDice[i].setMnemonic(KeyEvent.VK_2);
				}
				if (i == 2) {
					fBlockDice[i].setMnemonic(KeyEvent.VK_3);
				}
				if (i > 0) {
					blockRollPanel.add(Box.createHorizontalStrut(5));
				}
				fBlockDice[i].addKeyListener(this);
			}
		}

		centerPanel.add(blockRollPanel);

		if (!ownChoice) {
			JPanel opponentsChoicePanel = new JPanel();
			opponentsChoicePanel.setOpaque(false);
			opponentsChoicePanel.setLayout(new BoxLayout(opponentsChoicePanel, BoxLayout.X_AXIS));
			JLabel opponentsChoiceLabel = new JLabel("Opponent's choice");
			opponentsChoiceLabel.setFont(
					new Font(opponentsChoiceLabel.getFont().getName(), Font.BOLD, opponentsChoiceLabel.getFont().getSize()));
			opponentsChoicePanel.add(opponentsChoiceLabel);
			opponentsChoicePanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
			centerPanel.add(opponentsChoicePanel);
		}

		if (getDialogParameter().hasTeamReRollOption() || getDialogParameter().hasProReRollOption()) {

			JPanel reRollPanel = new JPanel();
			reRollPanel.setOpaque(false);
			reRollPanel.setLayout(new BoxLayout(reRollPanel, BoxLayout.X_AXIS));

			fButtonTeamReRoll = new JButton("Team Re-Roll");
			fButtonTeamReRoll.addActionListener(this);
			fButtonTeamReRoll.setMnemonic(KeyEvent.VK_T);
			fButtonTeamReRoll.addKeyListener(this);

			fButtonProReRoll = new JButton("Pro Re-Roll");
			fButtonProReRoll.addActionListener(this);
			fButtonProReRoll.setMnemonic(KeyEvent.VK_P);
			fButtonProReRoll.addKeyListener(this);

			fButtonNoReRoll = new JButton("No Re-Roll");
			fButtonNoReRoll.addActionListener(this);
			fButtonNoReRoll.setMnemonic(KeyEvent.VK_N);
			fButtonNoReRoll.addKeyListener(this);

			Box.Filler verticalGlue1 = (Box.Filler) Box.createVerticalGlue();
			verticalGlue1.setOpaque(false);
			reRollPanel.add(verticalGlue1);

			if (getDialogParameter().hasTeamReRollOption()) {
				reRollPanel.add(fButtonTeamReRoll);
			}
			if (getDialogParameter().hasProReRollOption()) {
				reRollPanel.add(fButtonProReRoll);
			}
			if (getDialogParameter().getNrOfDice() < 0) {
				reRollPanel.add(fButtonNoReRoll);
			}

			Box.Filler verticalGlue2 = (Box.Filler) Box.createVerticalGlue();
			verticalGlue2.setOpaque(false);
			reRollPanel.add(verticalGlue2);

			centerPanel.add(Box.createVerticalStrut(10));
			centerPanel.add(reRollPanel);

		}

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(centerPanel, BorderLayout.CENTER);

		pack();
		setLocationToCenter();

	}

	public DialogId getId() {
		return DialogId.BLOCK_ROLL;
	}

	public void actionPerformed(ActionEvent pActionEvent) {
		Game game = getClient().getGame();
		boolean homeChoice = ((getDialogParameter().getNrOfDice() > 0) || !game.isHomePlaying());
		if (pActionEvent.getSource() == fButtonTeamReRoll) {
			fReRollSource = ReRollSources.TEAM_RE_ROLL;
		}
		if (pActionEvent.getSource() == fButtonProReRoll) {
			fReRollSource = ReRollSources.PRO;
		}
		if (homeChoice && (fBlockDice.length >= 1) && (pActionEvent.getSource() == fBlockDice[0])) {
			fDiceIndex = 0;
		}
		if (homeChoice && (fBlockDice.length >= 2) && (pActionEvent.getSource() == fBlockDice[1])) {
			fDiceIndex = 1;
		}
		if (homeChoice && (fBlockDice.length >= 3) && (pActionEvent.getSource() == fBlockDice[2])) {
			fDiceIndex = 2;
		}
		if ((fReRollSource != null) || (fDiceIndex >= 0) || (pActionEvent.getSource() == fButtonNoReRoll)) {
			if (getCloseListener() != null) {
				getCloseListener().dialogClosed(this);
			}
		}
	}

	public void keyTyped(KeyEvent pKeyEvent) {
	}

	public ReRollSource getReRollSource() {
		return fReRollSource;
	}

	public int getDiceIndex() {
		return fDiceIndex;
	}

	public DialogBlockRollParameter getDialogParameter() {
		return fDialogParameter;
	}

	public void keyPressed(KeyEvent pKeyEvent) {
	}

	public void keyReleased(KeyEvent pKeyEvent) {
		Game game = getClient().getGame();
		boolean homeChoice = ((getDialogParameter().getNrOfDice() > 0) || !game.isHomePlaying());
		boolean keyHandled = false;
		switch (pKeyEvent.getKeyCode()) {
		case KeyEvent.VK_1:
			if (homeChoice && (fBlockDice.length >= 1)) {
				keyHandled = true;
				fDiceIndex = 0;
			}
			break;
		case KeyEvent.VK_2:
			if (homeChoice && (fBlockDice.length >= 2)) {
				keyHandled = true;
				fDiceIndex = 1;
			}
			break;
		case KeyEvent.VK_3:
			if (homeChoice && (fBlockDice.length >= 3)) {
				keyHandled = true;
				fDiceIndex = 2;
			}
			break;
		case KeyEvent.VK_T:
			if (getDialogParameter().hasTeamReRollOption()) {
				keyHandled = true;
				fReRollSource = ReRollSources.TEAM_RE_ROLL;
			}
			break;
		case KeyEvent.VK_P:
			if (getDialogParameter().hasProReRollOption()) {
				keyHandled = true;
				fReRollSource = ReRollSources.PRO;
			}
			break;
		case KeyEvent.VK_N:
			keyHandled = ((getDialogParameter().hasTeamReRollOption() || getDialogParameter().hasProReRollOption())
					&& (getDialogParameter().getNrOfDice() < 0));
			break;
		}
		if (keyHandled) {
			if (getCloseListener() != null) {
				getCloseListener().dialogClosed(this);
			}
		}
	}

}
