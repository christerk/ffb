package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.RenderContext;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.dialog.DialogBlockRollParameter;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Game;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * 
 * @author Kalimar
 */
public class DialogBlockRoll extends AbstractDialogBlock implements ActionListener, KeyListener {

	private final JButton[] fBlockDice;

	private JButton fButtonTeamReRoll;
	private JButton fButtonProReRoll;
	private JButton fButtonNoReRoll;

	private int fDiceIndex;
	private ReRollSource fReRollSource;

	private final DialogBlockRollParameter fDialogParameter;

	public DialogBlockRoll(FantasyFootballClient pClient, DialogBlockRollParameter pDialogParameter) {

		super(pClient, "Block Roll", false);

		fDiceIndex = -1;
		fDialogParameter = pDialogParameter;

		IconCache iconCache = getClient().getUserInterface().getIconCache();

		JPanel centerPanel = new BackgroundPanel((getDialogParameter().getNrOfDice() < 0) ? colorOpponentChoice : colorOwnChoice);
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

		JPanel blockRollPanel = blockRollPanel();

		int[] blockRoll = getDialogParameter().getBlockRoll();
		fBlockDice = new JButton[blockRoll.length];
		boolean ownChoice = ((fDialogParameter.getNrOfDice() > 0)
				|| (!fDialogParameter.hasTeamReRollOption() && !fDialogParameter.hasProReRollOption()));
		for (int i = 0; i < fBlockDice.length; i++) {
			fBlockDice[i] = new JButton(dimensionProvider(), RenderContext.ON_PITCH);
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
				fBlockDice[i].addKeyListener(this);
			}
		}

		centerPanel.add(blockRollPanel);

		if (!ownChoice) {
			centerPanel.add(opponentChoicePanel());
		}

		if (getDialogParameter().hasTeamReRollOption() || getDialogParameter().hasProReRollOption()) {

			JPanel reRollPanel = new JPanel();
			reRollPanel.setOpaque(false);
			reRollPanel.setLayout(new BoxLayout(reRollPanel, BoxLayout.X_AXIS));

			fButtonTeamReRoll = new JButton(dimensionProvider(), "Team Re-Roll", RenderContext.ON_PITCH);
			fButtonTeamReRoll.addActionListener(this);
			fButtonTeamReRoll.setMnemonic(KeyEvent.VK_T);
			fButtonTeamReRoll.addKeyListener(this);

			fButtonProReRoll = new JButton(dimensionProvider(), "Pro Re-Roll", RenderContext.ON_PITCH);
			fButtonProReRoll.addActionListener(this);
			fButtonProReRoll.setMnemonic(KeyEvent.VK_P);
			fButtonProReRoll.addKeyListener(this);

			fButtonNoReRoll = new JButton(dimensionProvider(), "No Re-Roll", RenderContext.ON_PITCH);
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
			default:
				break;
		}
		if (keyHandled) {
			if (getCloseListener() != null) {
				getCloseListener().dialogClosed(this);
			}
		}
	}

}
