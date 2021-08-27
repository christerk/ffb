package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.dialog.DialogBlockRollPartialReRollParameter;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Game;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kalimar
 */
public class DialogBlockRollPartialReRoll extends AbstractDialogBlock implements ActionListener, KeyListener {

	private final JButton[] fBlockDice;

	private JButton fButtonTeamReRoll;
	private JButton fButtonProReRoll;
	private JButton fButtonNoReRoll;
	private JButton brawlerButton, proButton1, proButton2, proButton3;

	private int fDiceIndex, proIndex;
	private ReRollSource fReRollSource;

	private final DialogBlockRollPartialReRollParameter fDialogParameter;

	public DialogBlockRollPartialReRoll(FantasyFootballClient pClient, DialogBlockRollPartialReRollParameter pDialogParameter) {

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
			|| (!fDialogParameter.hasTeamReRollOption() && !fDialogParameter.hasProReRollOption() && !fDialogParameter.hasBrawlerOption()));
		for (int i = 0; i < fBlockDice.length; i++) {
			fBlockDice[i] = new JButton();
			fBlockDice[i].setOpaque(false);
			fBlockDice[i].setBounds(0, 0, 45, 45);
			fBlockDice[i].setFocusPainted(false);
			fBlockDice[i].setMargin(new Insets(5, 5, 5, 5));
			fBlockDice[i].setIcon(new ImageIcon(iconCache.getDiceIcon(blockRoll[i])));
			int finalI = i;
			if ((getDialogParameter().hasProReRollOption() || getDialogParameter().hasBrawlerOption()) && Arrays.stream(fDialogParameter.getReRolledDiceIndexes()).anyMatch(index -> index == finalI)) {
				fBlockDice[i].setBorder(BorderFactory.createLineBorder(Color.red, 3, true));
			}
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

		if (getDialogParameter().hasTeamReRollOption() || getDialogParameter().hasProReRollOption() || pDialogParameter.hasBrawlerOption()) {

			JPanel reRollPanel = new JPanel();
			reRollPanel.setOpaque(false);
			reRollPanel.setLayout(new BoxLayout(reRollPanel, BoxLayout.X_AXIS));

			fButtonTeamReRoll = new JButton("Team Re-Roll");
			fButtonTeamReRoll.addActionListener(this);
			fButtonTeamReRoll.setMnemonic(KeyEvent.VK_T);
			fButtonTeamReRoll.addKeyListener(this);

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
			if (getDialogParameter().hasProReRollOption() && getDialogParameter().getNrOfDice() == 1) {
				fButtonProReRoll = new JButton("Pro Re-Roll");
				fButtonProReRoll.addActionListener(this);
				fButtonProReRoll.setMnemonic(KeyEvent.VK_P);
				fButtonProReRoll.addKeyListener(this);
				reRollPanel.add(fButtonProReRoll);
			}

			if (getDialogParameter().hasBrawlerOption()) {
				brawlerButton = new JButton("Brawler Re-Roll");
				brawlerButton.addActionListener(this);
				brawlerButton.setMnemonic(KeyEvent.VK_B);
				brawlerButton.addKeyListener(this);
				reRollPanel.add(brawlerButton);
			}

			if (getDialogParameter().getNrOfDice() < 0) {
				reRollPanel.add(fButtonNoReRoll);
			}

			Box.Filler verticalGlue2 = (Box.Filler) Box.createVerticalGlue();
			verticalGlue2.setOpaque(false);
			reRollPanel.add(verticalGlue2);

			centerPanel.add(Box.createVerticalStrut(10));
			centerPanel.add(reRollPanel);

			if (getDialogParameter().hasProReRollOption() && Math.abs(getDialogParameter().getNrOfDice()) > 1) {
				centerPanel.add(proPanel(Math.abs(pDialogParameter.getNrOfDice())));
				centerPanel.add(Box.createVerticalStrut(3));
			}
		}

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(centerPanel, BorderLayout.CENTER);

		pack();
		setLocationToCenter();

	}

	public DialogId getId() {
		return DialogId.BLOCK_ROLL_PARTIAL_RE_ROLL;
	}

	private JPanel proPanel(int diceCount) {
		List<Integer> proMnemonics = new ArrayList<>();
		proMnemonics.add(KeyEvent.VK_P);
		proMnemonics.add(KeyEvent.VK_R);
		proMnemonics.add(KeyEvent.VK_E);
		JPanel proPanel = new JPanel();
		proPanel.setLayout(new BoxLayout(proPanel, BoxLayout.Y_AXIS));
		proPanel.setAlignmentX(CENTER_ALIGNMENT);
		proPanel.add(proTextPanel());
		proPanel.setOpaque(false);

		JPanel proButtonPanel = new JPanel();
		proButtonPanel.setLayout(new BoxLayout(proButtonPanel, BoxLayout.X_AXIS));
		proButtonPanel.setAlignmentX(CENTER_ALIGNMENT);
		for (int i = 1; i <= diceCount; i++) {
			int finalI = i;
			if (Arrays.stream(getDialogParameter().getReRolledDiceIndexes()).noneMatch(index -> index == finalI - 1)) {
				JButton button = proButton(i, proMnemonics.get(0));
				switch (i) {
					case 1:
						proButton1 = button;
						break;
					case 2:
						proButton2 = button;
						break;
					default:
						proButton3 = button;
						break;
				}
				proButtonPanel.add(button);
			}

			proPanel.add(proButtonPanel);
		}
		proMnemonics.remove(0);

		return proPanel;
	}

	private JButton proButton(int dieNumber, int keyEvent) {
		JButton proButton = new JButton();
		proButton.setText("Die " + dieNumber);
		proButton.addActionListener(this);
		proButton.setMnemonic(keyEvent);
		proButton.addKeyListener(this);
		return proButton;
	}

	private void setBrawler() {
		fReRollSource = ReRollSources.BRAWLER;
	}

	public void actionPerformed(ActionEvent pActionEvent) {
		Game game = getClient().getGame();
		boolean homeChoice = ((getDialogParameter().getNrOfDice() > 0) || !game.isHomePlaying());
		if (pActionEvent.getSource() == fButtonTeamReRoll) {
			fReRollSource = ReRollSources.TEAM_RE_ROLL;
		}
		if (pActionEvent.getSource() == fButtonProReRoll) {
			fReRollSource = ReRollSources.PRO;
			proIndex = 0;
		}
		if (pActionEvent.getSource() == proButton1) {
			fReRollSource = ReRollSources.PRO;
			proIndex = 0;
		}
		if (pActionEvent.getSource() == proButton2) {
			fReRollSource = ReRollSources.PRO;
			proIndex = 1;
		}
		if (pActionEvent.getSource() == proButton3) {
			fReRollSource = ReRollSources.PRO;
			proIndex = 2;
		}
		if (pActionEvent.getSource() == brawlerButton) {
			setBrawler();
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

	public DialogBlockRollPartialReRollParameter getDialogParameter() {
		return fDialogParameter;
	}

	public int getProIndex() {
		return proIndex;
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
			case KeyEvent.VK_B:
				if (brawlerButton != null) {
					keyHandled = true;
					setBrawler();
				}
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
