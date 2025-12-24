package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JCheckBox;
import com.fumbbl.ffb.dialog.DialogBlockRollPropertiesParameter;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Game;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
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
import java.util.Map;

public class DialogBlockRollProperties extends AbstractDialogBlock implements ActionListener, KeyListener {

	private final JButton[] fBlockDice;

	private JButton fButtonTeamReRoll;
	private JButton fButtonProReRoll;
	private JButton fButtonNoReRoll;
	private JButton brawlerButton, proButton1, proButton2, proButton3,
		anySingleDieButton, anySingleDieButton1, anySingleDieButton2, anySingleDieButton3,
		singleDieButton, singleDieButton1, singleDieButton2, singleDieButton3, anyDiceButton;

	private JCheckBox[] diceBoxes;

	private int fDiceIndex;
	private final List<Integer> reRollIndexes = new ArrayList<>();
	private ReRollSource fReRollSource;
	private final ReRollSource singleDieReRollSource;
	private final ReRollSource singleBlockDieReRollSource;
	private final ReRollSource anyBlockDiceReRollSource;

	private final DialogBlockRollPropertiesParameter dialogParameter;

	public DialogBlockRollProperties(FantasyFootballClient pClient, DialogBlockRollPropertiesParameter pDialogParameter, Map<ReRolledAction, ReRollSource> actionToSource) {

		super(pClient, "Block Roll", false);

		fDiceIndex = -1;
		dialogParameter = pDialogParameter;

		singleDieReRollSource = actionToSource.get(ReRolledActions.SINGLE_DIE);
		anyBlockDiceReRollSource = actionToSource.get(ReRolledActions.MULTI_BLOCK_DICE);
		singleBlockDieReRollSource = actionToSource.get(ReRolledActions.SINGLE_BLOCK_DIE);

		IconCache iconCache = getClient().getUserInterface().getIconCache();

		JPanel centerPanel =
			new BackgroundPanel((getDialogParameter().getNrOfDice() < 0) ? colorOpponentChoice : colorOwnChoice);
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

		JPanel blockRollPanel = blockRollPanel();

		int[] blockRoll = getDialogParameter().getBlockRoll();
		fBlockDice = new JButton[blockRoll.length];
		if (blockRoll.length > 1) {
			diceBoxes = new JCheckBox[blockRoll.length];
		}

		boolean ownChoice = (dialogParameter.getNrOfDice() > 0 || !dialogParameter.hasActualReRoll());
		for (int i = 0; i < fBlockDice.length; i++) {
			fBlockDice[i] = new JButton(dimensionProvider());
			fBlockDice[i].setOpaque(false);
			fBlockDice[i].setBounds(0, 0, 45, 45);
			fBlockDice[i].setFocusPainted(false);
			fBlockDice[i].setMargin(new Insets(5, 5, 5, 5));
			fBlockDice[i].setIcon(new ImageIcon(iconCache.getDiceIcon(blockRoll[i], dimensionProvider())));
			int finalI = i;
			if ((getDialogParameter().hasProperty(ReRollProperty.PRO) ||
				getDialogParameter().hasProperty(ReRollProperty.BRAWLER))
				&& Arrays.stream(
				dialogParameter.getReRolledDiceIndexes()).anyMatch(index -> index == finalI)) {
				fBlockDice[i].setBorder(BorderFactory.createLineBorder(Color.red, 3, true));
			}
			if (anyBlockDiceReRollSource == null || blockRoll.length == 1) {
				blockRollPanel.add(fBlockDice[i]);
			} else {
				JPanel checkboxPanel = new JPanel();
				checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
				checkboxPanel.setOpaque(false);
				checkboxPanel.add(fBlockDice[i]);
				int mnemonic;
				String mnemonicString;
				switch (i) {
					case 0:
						mnemonic = KeyEvent.VK_F1;
						mnemonicString = "F1";
						break;
					case 1:
						mnemonic = KeyEvent.VK_F2;
						mnemonicString = "F2";
						break;
					default:
						mnemonic = KeyEvent.VK_F3;
						mnemonicString = "F3";
						break;
				}
				diceBoxes[i] = new JCheckBox(dimensionProvider(), "( " + mnemonicString + " )");
				diceBoxes[i].setMnemonic(mnemonic);
				diceBoxes[i].setOpaque(false);
				diceBoxes[i].setEnabled(
					Arrays.stream(pDialogParameter.getReRolledDiceIndexes()).noneMatch(value -> value == finalI));
				diceBoxes[i].addItemListener(
					e -> anyDiceButton.setEnabled(Arrays.stream(diceBoxes).anyMatch(AbstractButton::isSelected)));
				checkboxPanel.add(diceBoxes[i]);
				blockRollPanel.add(checkboxPanel);
			}
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

		if (getDialogParameter().hasActualReRoll()) {

			JPanel reRollPanel = new JPanel();
			reRollPanel.setOpaque(false);
			reRollPanel.setLayout(new BoxLayout(reRollPanel, BoxLayout.X_AXIS));

			fButtonTeamReRoll = new JButton(dimensionProvider(), "Team Re-Roll", KeyEvent.VK_T);
			fButtonTeamReRoll.addActionListener(this);
			fButtonTeamReRoll.setMnemonic(KeyEvent.VK_T);
			fButtonTeamReRoll.addKeyListener(this);

			fButtonNoReRoll = new JButton(dimensionProvider(), "No Re-Roll", KeyEvent.VK_N);
			fButtonNoReRoll.addActionListener(this);
			fButtonNoReRoll.setMnemonic(KeyEvent.VK_N);
			fButtonNoReRoll.addKeyListener(this);

			Box.Filler verticalGlue1 = (Box.Filler) Box.createVerticalGlue();
			verticalGlue1.setOpaque(false);
			reRollPanel.add(verticalGlue1);

			if (getDialogParameter().hasProperty(ReRollProperty.TRR)) {
				reRollPanel.add(fButtonTeamReRoll);
			}

			if (getDialogParameter().getNrOfDice() == 1) {
				if (getDialogParameter().hasProperty(ReRollProperty.PRO)) {
					fButtonProReRoll = new JButton(dimensionProvider(), "Pro Re-Roll", KeyEvent.VK_P);
					fButtonProReRoll.addActionListener(this);
					fButtonProReRoll.setMnemonic(KeyEvent.VK_P);
					fButtonProReRoll.addKeyListener(this);
					reRollPanel.add(fButtonProReRoll);
				}

				if (getDialogParameter().hasProperty(ReRollProperty.ANY_DIE_RE_ROLL) && singleDieReRollSource != null) {
					anySingleDieButton =
						new JButton(dimensionProvider(), singleDieReRollSource.getName(getClient().getGame()), KeyEvent.VK_C);
					anySingleDieButton.addActionListener(this);
					anySingleDieButton.setMnemonic(KeyEvent.VK_C);
					anySingleDieButton.addKeyListener(this);
					reRollPanel.add(anySingleDieButton);
				}

				if (singleBlockDieReRollSource != null) {
					singleDieButton =
						new JButton(dimensionProvider(), singleBlockDieReRollSource.getName(getClient().getGame()), KeyEvent.VK_U);
					singleDieButton.addActionListener(this);
					singleDieButton.setMnemonic(KeyEvent.VK_U);
					singleDieButton.addKeyListener(this);
					reRollPanel.add(singleDieButton);
				}

			}

			if (anyBlockDiceReRollSource != null) {
				anyDiceButton =
					new JButton(dimensionProvider(), anyBlockDiceReRollSource.getName(getClient().getGame()), KeyEvent.VK_V);
				anyDiceButton.addActionListener(this);
				anyDiceButton.setMnemonic(KeyEvent.VK_V);
				anyDiceButton.addKeyListener(this);
				anyDiceButton.setEnabled(blockRoll.length == 1);
				reRollPanel.add(anyDiceButton);
			}

			if (getDialogParameter().hasProperty(ReRollProperty.BRAWLER)) {
				brawlerButton = new JButton(dimensionProvider(), "Brawler Re-Roll", KeyEvent.VK_B);
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

			if (Math.abs(getDialogParameter().getNrOfDice()) > 1) {
				if (getDialogParameter().hasProperty(ReRollProperty.PRO)) {
					centerPanel.add(proPanel(Math.abs(pDialogParameter.getNrOfDice())));
					centerPanel.add(Box.createVerticalStrut(3));
				}

				if (pDialogParameter.hasProperty(ReRollProperty.ANY_DIE_RE_ROLL) && singleDieReRollSource != null) {
					centerPanel.add(consummatePanel(Math.abs(pDialogParameter.getNrOfDice())));
					centerPanel.add(Box.createVerticalStrut(3));
				}

				if (singleBlockDieReRollSource != null) {
					centerPanel.add(singleBlockDiePanel(Math.abs(pDialogParameter.getNrOfDice())));
					centerPanel.add(Box.createVerticalStrut(3));

				}
			}
		}

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(centerPanel, BorderLayout.CENTER);

		pack();
		setLocationToCenter();

	}

	public DialogId getId() {
		return DialogId.BLOCK_ROLL_PROPERTIES;
	}

	private JPanel singleBlockDiePanel(int diceCount) {
		List<Integer> mnemonics = new ArrayList<>();
		mnemonics.add(KeyEvent.VK_U);
		mnemonics.add(KeyEvent.VK_S);
		mnemonics.add(KeyEvent.VK_A);
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setAlignmentX(CENTER_ALIGNMENT);
		panel.add(textPanel(singleBlockDieReRollSource.getName(getClient().getGame())));
		panel.setOpaque(false);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setAlignmentX(CENTER_ALIGNMENT);
		for (int i = 1; i <= diceCount; i++) {
			int finalI = i;
			if (Arrays.stream(getDialogParameter().getReRolledDiceIndexes()).noneMatch(index -> index == finalI - 1)) {
				JButton button = singleDieButton(i, mnemonics.get(i - 1));
				switch (i) {
					case 1:
						singleDieButton1 = button;
						break;
					case 2:
						singleDieButton2 = button;
						break;
					default:
						singleDieButton3 = button;
						break;
				}
				buttonPanel.add(button);
			}

			panel.add(buttonPanel);
		}
		mnemonics.remove(0);

		return panel;
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
				JButton button = singleDieButton(i, proMnemonics.get(i - 1));
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

	private JPanel consummatePanel(int diceCount) {
		List<Integer> consummateMnemonics = new ArrayList<>();
		consummateMnemonics.add(KeyEvent.VK_C);
		consummateMnemonics.add(KeyEvent.VK_O);
		consummateMnemonics.add(KeyEvent.VK_M);
		JPanel consummatePanel = new JPanel();
		consummatePanel.setLayout(new BoxLayout(consummatePanel, BoxLayout.Y_AXIS));
		consummatePanel.setAlignmentX(CENTER_ALIGNMENT);
		consummatePanel.add(textPanel(singleDieReRollSource.getName(getClient().getGame())));
		consummatePanel.setOpaque(false);

		JPanel consummateButtonPanel = new JPanel();
		consummateButtonPanel.setLayout(new BoxLayout(consummateButtonPanel, BoxLayout.X_AXIS));
		consummateButtonPanel.setAlignmentX(CENTER_ALIGNMENT);
		for (int i = 1; i <= diceCount; i++) {
			int finalI = i;
			if (Arrays.stream(getDialogParameter().getReRolledDiceIndexes()).noneMatch(index -> index == finalI - 1)) {
				JButton button = singleDieButton(i, consummateMnemonics.get(i - 1));
				switch (i) {
					case 1:
						anySingleDieButton1 = button;
						break;
					case 2:
						anySingleDieButton2 = button;
						break;
					default:
						anySingleDieButton3 = button;
						break;
				}
				consummateButtonPanel.add(button);
			}

			consummatePanel.add(consummateButtonPanel);
		}
		consummateMnemonics.remove(0);

		return consummatePanel;
	}


	private JButton singleDieButton(int dieNumber, int keyEvent) {
		JButton singleDieButton = new JButton(dimensionProvider(), "Die " + dieNumber, keyEvent);
		singleDieButton.addActionListener(this);
		singleDieButton.setMnemonic(keyEvent);
		singleDieButton.addKeyListener(this);
		return singleDieButton;
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
		if (pActionEvent.getSource() == anyDiceButton) {
			evaluateCheckboxes();
		}
		if (pActionEvent.getSource() == singleDieButton) {
			fReRollSource = singleBlockDieReRollSource;
			reRollIndexes.add(0);
		}
		if (pActionEvent.getSource() == singleDieButton1) {
			fReRollSource = singleBlockDieReRollSource;
			reRollIndexes.add(0);
		}
		if (pActionEvent.getSource() == singleDieButton2) {
			fReRollSource = singleBlockDieReRollSource;
			reRollIndexes.add(1);
		}
		if (pActionEvent.getSource() == singleDieButton3) {
			fReRollSource = singleBlockDieReRollSource;
			reRollIndexes.add(2);
		}
		if (pActionEvent.getSource() == anySingleDieButton) {
			fReRollSource = singleDieReRollSource;
			reRollIndexes.add(0);
		}
		if (pActionEvent.getSource() == anySingleDieButton1) {
			fReRollSource = singleDieReRollSource;
			reRollIndexes.add(0);
		}
		if (pActionEvent.getSource() == anySingleDieButton2) {
			fReRollSource = singleDieReRollSource;
			reRollIndexes.add(1);
		}
		if (pActionEvent.getSource() == anySingleDieButton3) {
			fReRollSource = singleDieReRollSource;
			reRollIndexes.add(2);
		}
		if (pActionEvent.getSource() == fButtonProReRoll) {
			fReRollSource = ReRollSources.PRO;
			reRollIndexes.add(0);
		}
		if (pActionEvent.getSource() == proButton1) {
			fReRollSource = ReRollSources.PRO;
			reRollIndexes.add(0);
		}
		if (pActionEvent.getSource() == proButton2) {
			fReRollSource = ReRollSources.PRO;
			reRollIndexes.add(1);
		}
		if (pActionEvent.getSource() == proButton3) {
			fReRollSource = ReRollSources.PRO;
			reRollIndexes.add(2);
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

	private void evaluateCheckboxes() {
		fReRollSource = anyBlockDiceReRollSource;
		if (diceBoxes == null) {
			reRollIndexes.add(0);
		} else {
			for (int i = 0; i < diceBoxes.length; i++) {
				if (diceBoxes[i].isSelected()) {
					reRollIndexes.add(i);
				}
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

	public DialogBlockRollPropertiesParameter getDialogParameter() {
		return dialogParameter;
	}

	public List<Integer> getReRollIndexes() {
		return reRollIndexes;
	}

	public ReRollSource getSingleDieReRollSource() {
		return singleDieReRollSource;
	}

	public ReRollSource getSingleBlockDieReRollSource() {
		return singleBlockDieReRollSource;
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
				if (getDialogParameter().hasProperty(ReRollProperty.TRR)) {
					keyHandled = true;
					fReRollSource = ReRollSources.TEAM_RE_ROLL;
				}
				break;
			case KeyEvent.VK_P:
				if (getDialogParameter().hasProperty(ReRollProperty.PRO)) {
					keyHandled = true;
					fReRollSource = ReRollSources.PRO;
					reRollIndexes.add(0);
				}
				break;
			case KeyEvent.VK_R:
				if (getDialogParameter().hasProperty(ReRollProperty.PRO)) {
					keyHandled = true;
					fReRollSource = ReRollSources.PRO;
					reRollIndexes.add(1);
				}
				break;
			case KeyEvent.VK_E:
				if (getDialogParameter().hasProperty(ReRollProperty.PRO)) {
					keyHandled = true;
					fReRollSource = ReRollSources.PRO;
					reRollIndexes.add(2);
				}
				break;
			case KeyEvent.VK_C:
				if (getDialogParameter().hasProperty(ReRollProperty.ANY_DIE_RE_ROLL)) {
					keyHandled = true;
					fReRollSource = singleDieReRollSource;
					reRollIndexes.add(0);
				}
				break;
			case KeyEvent.VK_O:
				if (getDialogParameter().hasProperty(ReRollProperty.ANY_DIE_RE_ROLL)) {
					keyHandled = true;
					fReRollSource = singleDieReRollSource;
					reRollIndexes.add(1);
				}
				break;
			case KeyEvent.VK_M:
				if (getDialogParameter().hasProperty(ReRollProperty.ANY_DIE_RE_ROLL)) {
					keyHandled = true;
					fReRollSource = singleDieReRollSource;
					reRollIndexes.add(2);
				}
				break;
			case KeyEvent.VK_U:
				if (singleBlockDieReRollSource != null) {
					keyHandled = true;
					fReRollSource = singleBlockDieReRollSource;
					reRollIndexes.add(0);
				}
				break;
			case KeyEvent.VK_S:
				if (singleBlockDieReRollSource != null) {
					keyHandled = true;
					fReRollSource = singleBlockDieReRollSource;
					reRollIndexes.add(1);
				}
				break;
			case KeyEvent.VK_A:
				if (singleBlockDieReRollSource != null) {
					keyHandled = true;
					fReRollSource = singleBlockDieReRollSource;
					reRollIndexes.add(2);
				}
				break;
			case KeyEvent.VK_N:
				keyHandled = ((getDialogParameter().hasProperty(ReRollProperty.TRR) ||
					getDialogParameter().hasProperty(ReRollProperty.PRO))
					&& (getDialogParameter().getNrOfDice() < 0));
				break;
			case KeyEvent.VK_B:
				if (brawlerButton != null) {
					keyHandled = true;
					setBrawler();
				}
				break;
			case KeyEvent.VK_V:
				if (anyDiceButton != null && anyDiceButton.isEnabled()) {
					keyHandled = true;
					evaluateCheckboxes();
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

	public ReRollSource getAnyBlockDiceReRollSource() {
		return anyBlockDiceReRollSource;
	}
}
