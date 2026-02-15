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
	private JButton brawlerButton, hatredButton, proButton1, proButton2, proButton3,
		anySingleDieButton, anySingleDieButton1, anySingleDieButton2, anySingleDieButton3,
		singleDieButton, singleDieButton1, singleDieButton2, singleDieButton3, anyDiceButton;

	private JCheckBox fallbackToTrr, proFallbackMascot, proFallbackTrr;
	private JCheckBox[] diceBoxes;

	private int fDiceIndex;
	private final List<Integer> reRollIndexes = new ArrayList<>();
	private ReRollSource fReRollSource;
	private final ReRollSource singleDieReRollSource;
	private final ReRollSource singleBlockDieReRollSource;
	private final ReRollSource anyBlockDiceReRollSource;
	private final ReRollSource singleDiePerActivationReRollSource;
	private final ReRollSource bothDownReRollSource;
	private final ReRollSource skullReRollSource;
	private boolean willUseMascot;
	private final DialogExtensionMascot mascotExtension = new DialogExtensionMascot();
	private final Map<ReRolledAction, ReRollSource> actionToSource;
	private final DialogBlockRollPropertiesParameter dialogParameter;

	public DialogBlockRollProperties(FantasyFootballClient pClient, DialogBlockRollPropertiesParameter dialogParameter, Map<ReRolledAction, ReRollSource> actionToSource) {

		super(pClient, "Block Roll", false);

		fDiceIndex = -1;
		this.dialogParameter = dialogParameter;
		this.actionToSource = actionToSource;

		singleDieReRollSource = actionToSource.get(ReRolledActions.SINGLE_DIE);
		anyBlockDiceReRollSource = actionToSource.get(ReRolledActions.MULTI_BLOCK_DICE);
		singleBlockDieReRollSource = actionToSource.get(ReRolledActions.SINGLE_BLOCK_DIE);
		singleDiePerActivationReRollSource = actionToSource.get(ReRolledActions.SINGLE_DIE_PER_ACTIVATION);
		bothDownReRollSource = actionToSource.get(ReRolledActions.SINGLE_BOTH_DOWN);
		skullReRollSource = actionToSource.get(ReRolledActions.SINGLE_SKULL);

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

		boolean ownChoice = (this.dialogParameter.getNrOfDice() > 0 || !this.dialogParameter.hasActualReRoll());
		for (int i = 0; i < fBlockDice.length; i++) {
			fBlockDice[i] = new JButton(dimensionProvider());
			fBlockDice[i].setOpaque(false);
			fBlockDice[i].setBounds(0, 0, 45, 45);
			fBlockDice[i].setFocusPainted(false);
			fBlockDice[i].setMargin(new Insets(5, 5, 5, 5));
			fBlockDice[i].setIcon(new ImageIcon(iconCache.getDiceIcon(blockRoll[i], dimensionProvider())));
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
				diceBoxes[i].setEnabled(true);
				diceBoxes[i].setFocusPainted(false);
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

			ReRollSource trrSource = mascotExtension.teamReRollText(dialogParameter);

			willUseMascot = trrSource == ReRollSources.MASCOT;

			JPanel reRollPanel = reRollPanel(trrSource, blockRoll);

			centerPanel.add(Box.createVerticalStrut(10));
			centerPanel.add(reRollPanel);

			if (Math.abs(getDialogParameter().getNrOfDice()) > 1) {
				if (singleDiePerActivationReRollSource != null) {
					centerPanel.add(proPanel(Math.abs(dialogParameter.getNrOfDice())));
					centerPanel.add(Box.createVerticalStrut(3));
				}

				if (singleDieReRollSource != null) {
					centerPanel.add(singleDieReRollPanel(Math.abs(dialogParameter.getNrOfDice())));
					centerPanel.add(Box.createVerticalStrut(3));
				}

				if (singleBlockDieReRollSource != null) {
					centerPanel.add(singleBlockDiePanel(Math.abs(dialogParameter.getNrOfDice())));
					centerPanel.add(Box.createVerticalStrut(3));

				}
			}
		}

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(centerPanel, BorderLayout.CENTER);

		pack();
		setLocationToCenter();

	}

	private JPanel reRollPanel(ReRollSource trrSource, int[] blockRoll) {
		JPanel reRollPanel = new JPanel();
		reRollPanel.setOpaque(false);
		reRollPanel.setLayout(new BoxLayout(reRollPanel, BoxLayout.X_AXIS));

		fButtonTeamReRoll = button(trrSource.getName(getClient().getGame()), KeyEvent.VK_T);
		fButtonNoReRoll = button("No Re-Roll", KeyEvent.VK_N);

		Box.Filler verticalGlue1 = (Box.Filler) Box.createVerticalGlue();
		verticalGlue1.setOpaque(false);
		reRollPanel.add(verticalGlue1);
		reRollPanel.setOpaque(false);

		if (getDialogParameter().hasProperty(ReRollProperty.TRR)) {
			if (willUseMascot) {
				JPanel mascotPanel = new JPanel();
				mascotPanel.setBackground(null);
				mascotPanel.setLayout(new BoxLayout(mascotPanel, BoxLayout.Y_AXIS));
				mascotPanel.setAlignmentX(Box.CENTER_ALIGNMENT);
				mascotPanel.setAlignmentY(Box.TOP_ALIGNMENT);
				fButtonTeamReRoll.setAlignmentX(Box.CENTER_ALIGNMENT);
				mascotPanel.add(fButtonTeamReRoll);
				mascotPanel.setOpaque(false);
				if (dialogParameter.hasProperty(ReRollProperty.TRR)) {
					fallbackToTrr = mascotExtension.checkBox("TRR fallback", KeyEvent.VK_F, Color.WHITE, dimensionProvider(),
						this, this);
					mascotPanel.add(fallbackToTrr);
				}
				reRollPanel.add(mascotPanel);
			} else {
				reRollPanel.add(mascotExtension.wrapperPanel(fButtonTeamReRoll));
			}
		}

		if (getDialogParameter().getNrOfDice() == 1) {
			if (singleDiePerActivationReRollSource != null) {
				fButtonProReRoll = button("Pro Re-Roll", KeyEvent.VK_P);
				if (willUseMascot || dialogParameter.hasProperty(ReRollProperty.TRR)) {
					JPanel proPanel = proMascotPanelSingle();
					reRollPanel.add(proPanel);
				} else {
					reRollPanel.add(mascotExtension.wrapperPanel(fButtonProReRoll));
				}
			}

			if (singleDieReRollSource != null) {
				anySingleDieButton = button(singleDieReRollSource.getName(getClient().getGame()), KeyEvent.VK_C);
				reRollPanel.add(mascotExtension.wrapperPanel(anySingleDieButton));
			}

			if (singleBlockDieReRollSource != null) {
				singleDieButton = button(singleBlockDieReRollSource.getName(getClient().getGame()), KeyEvent.VK_U);
				reRollPanel.add(mascotExtension.wrapperPanel(singleDieButton));
			}

		}

		if (anyBlockDiceReRollSource != null) {
			anyDiceButton = button(anyBlockDiceReRollSource.getName(getClient().getGame()), KeyEvent.VK_V);
			anyDiceButton.setEnabled(blockRoll.length == 1);
			reRollPanel.add(mascotExtension.wrapperPanel(anyDiceButton));
		}

		if (bothDownReRollSource != null) {
			brawlerButton = button("Brawler Re-Roll", KeyEvent.VK_B);
			reRollPanel.add(mascotExtension.wrapperPanel(brawlerButton));
		}

		if (skullReRollSource != null) {
			hatredButton = button("Hatred Re-Roll", KeyEvent.VK_H);
			reRollPanel.add(mascotExtension.wrapperPanel(hatredButton));
		}

		if (getDialogParameter().getNrOfDice() < 0) {
			reRollPanel.add(mascotExtension.wrapperPanel(fButtonNoReRoll));
		}

		Box.Filler verticalGlue2 = (Box.Filler) Box.createVerticalGlue();
		verticalGlue2.setOpaque(false);
		reRollPanel.add(verticalGlue2);
		return reRollPanel;
	}

	private JPanel proMascotPanelSingle() {
		JPanel proPanel = new JPanel();
		Color checkboxColor =
			!dialogParameter.hasProperty(ReRollProperty.TRR) || rerollButtons() > 2 ||
				dialogParameter.getNrOfDice() < 0 ?
				Color.WHITE : Color.BLACK;

		proPanel.setLayout(new BoxLayout(proPanel, BoxLayout.Y_AXIS));
		proPanel.setAlignmentX(Box.CENTER_ALIGNMENT);
		proPanel.setAlignmentY(Box.TOP_ALIGNMENT);
		proPanel.setOpaque(false);
		fButtonProReRoll.setAlignmentX(Box.CENTER_ALIGNMENT);
		proPanel.add(fButtonProReRoll);
		if (willUseMascot) {
			proFallbackMascot = mascotExtension.checkBox("Mascot", KeyEvent.VK_L, checkboxColor, dimensionProvider(), this,
				this);
			proPanel.add(proFallbackMascot);
		}
		if (dialogParameter.hasProperty(ReRollProperty.TRR)) {
			proFallbackTrr = mascotExtension.checkBox(willUseMascot ? "TRR fallback" : "ReRoll", KeyEvent.VK_X,
				checkboxColor, dimensionProvider(), this, this);
			proFallbackTrr.setEnabled(!willUseMascot);
			proPanel.add(proFallbackTrr);
		}
		return proPanel;
	}


	private JPanel proMascotPanelMultiple() {
		JPanel mascotPanel = new JPanel();
		List<Color> checkboxColor = new ArrayList<Color>() {{
			add(Color.WHITE);
			add(Color.BLACK);
		}};

		mascotPanel.setLayout(new BoxLayout(mascotPanel, BoxLayout.X_AXIS));
		mascotPanel.setAlignmentX(Box.CENTER_ALIGNMENT);
		mascotPanel.setAlignmentY(Box.TOP_ALIGNMENT);
		mascotPanel.setOpaque(false);
		if (willUseMascot) {
			proFallbackMascot = mascotExtension.checkBox("Mascot", KeyEvent.VK_L, checkboxColor.remove(0),
				dimensionProvider(), this, this);
			mascotPanel.add(proFallbackMascot);
		}
		if (dialogParameter.hasProperty(ReRollProperty.TRR)) {
			proFallbackTrr = mascotExtension.checkBox(willUseMascot ? "TRR fallback" : "ReRoll", KeyEvent.VK_X,
				checkboxColor.remove(0), dimensionProvider(), this, this);
			proFallbackTrr.setEnabled(!willUseMascot);
			mascotPanel.add(proFallbackTrr);
		}
		return mascotPanel;
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
		buttonPanel.setOpaque(false);
		for (int i = 1; i <= diceCount; i++) {
			JButton button = singleDieButton(i, mnemonics.remove(0));
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

		return panel;
	}

	private long rerollButtons() {
		return dialogParameter.getReRollProperties().stream()
			.filter(prop -> prop.isActualReRoll() && prop != ReRollProperty.MASCOT).count() +
			actionToSource.size();
	}

	private JButton button(String text, int mnemonic) {
		JButton button = new JButton(dimensionProvider(), text, mnemonic);
		button.addActionListener(this);
		button.setMnemonic(mnemonic);
		button.addKeyListener(this);
		button.setOpaque(false);
		return button;
	}

	private JPanel proPanel(int diceCount) {
		List<Integer> proMnemonics = new ArrayList<>();
		proMnemonics.add(KeyEvent.VK_P);
		proMnemonics.add(KeyEvent.VK_R);
		proMnemonics.add(KeyEvent.VK_E);
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setAlignmentX(CENTER_ALIGNMENT);
		panel.add(proTextPanel());
		panel.setOpaque(false);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setAlignmentX(CENTER_ALIGNMENT);
		buttonPanel.setOpaque(false);
		for (int i = 1; i <= diceCount; i++) {
			JButton button = singleDieButton(i, proMnemonics.remove(0));
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
			buttonPanel.add(button);

		}
		panel.add(buttonPanel);

		panel.add(proMascotPanelMultiple());
		return panel;
	}

	private JPanel singleDieReRollPanel(int diceCount) {
		List<Integer> memonics = new ArrayList<>();
		memonics.add(KeyEvent.VK_C);
		memonics.add(KeyEvent.VK_O);
		memonics.add(KeyEvent.VK_M);
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setAlignmentX(CENTER_ALIGNMENT);
		panel.add(textPanel(singleDieReRollSource.getName(getClient().getGame())));
		panel.setOpaque(false);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setAlignmentX(CENTER_ALIGNMENT);
		buttonPanel.setOpaque(false);
		for (int i = 1; i <= diceCount; i++) {
			JButton button = singleDieButton(i, memonics.remove(0));
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
			buttonPanel.add(button);

		}
		panel.add(buttonPanel);

		return panel;
	}


	private JButton singleDieButton(int dieNumber, int keyEvent) {
		JButton singleDieButton = new JButton(dimensionProvider(), "Die " + dieNumber, keyEvent);
		singleDieButton.addActionListener(this);
		singleDieButton.setMnemonic(keyEvent);
		singleDieButton.addKeyListener(this);
		singleDieButton.setOpaque(false);
		return singleDieButton;
	}

	private void setBrawler() {
		fReRollSource = ReRollSources.BRAWLER;
	}

	private void setHatred() {
		fReRollSource = ReRollSources.HATRED;
	}

	public void actionPerformed(ActionEvent pActionEvent) {
		Game game = getClient().getGame();
		boolean homeChoice = ((getDialogParameter().getNrOfDice() > 0) || !game.isHomePlaying());
		if (pActionEvent.getSource() == fButtonTeamReRoll) {
			determineTeamReRollSource();
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
			determineProReRollSource();
			reRollIndexes.add(0);
		}
		if (pActionEvent.getSource() == proButton1) {
			determineProReRollSource();
			reRollIndexes.add(0);
		}
		if (pActionEvent.getSource() == proButton2) {
			determineProReRollSource();
			reRollIndexes.add(1);
		}
		if (pActionEvent.getSource() == proButton3) {
			determineProReRollSource();
			reRollIndexes.add(2);
		}
		if (pActionEvent.getSource() == brawlerButton) {
			setBrawler();
		}
		if (pActionEvent.getSource() == hatredButton) {
			setHatred();
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

		if (pActionEvent.getSource() == proFallbackMascot) {
			if (proFallbackTrr != null) {
				if (!proFallbackMascot.isSelected()) {
					proFallbackTrr.setSelected(false);
				}
				proFallbackTrr.setEnabled(proFallbackMascot.isSelected());
			}
			return;
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
					determineTeamReRollSource();
				}
				break;
			case KeyEvent.VK_P:
				if (singleDiePerActivationReRollSource != null) {
					keyHandled = true;
					determineProReRollSource();
					reRollIndexes.add(0);
				}
				break;
			case KeyEvent.VK_R:
				if (singleDiePerActivationReRollSource != null) {
					keyHandled = true;
					determineProReRollSource();
					reRollIndexes.add(1);
				}
				break;
			case KeyEvent.VK_E:
				if (singleDiePerActivationReRollSource != null) {
					keyHandled = true;
					determineProReRollSource();
					reRollIndexes.add(2);
				}
				break;
			case KeyEvent.VK_C:
				if (singleDieReRollSource != null) {
					keyHandled = true;
					fReRollSource = singleDieReRollSource;
					reRollIndexes.add(0);
				}
				break;
			case KeyEvent.VK_O:
				if (singleDieReRollSource != null) {
					keyHandled = true;
					fReRollSource = singleDieReRollSource;
					reRollIndexes.add(1);
				}
				break;
			case KeyEvent.VK_M:
				if (singleDieReRollSource != null) {
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
					singleDiePerActivationReRollSource != null)
					&& (getDialogParameter().getNrOfDice() < 0));
				break;
			case KeyEvent.VK_B:
				if (brawlerButton != null) {
					keyHandled = true;
					setBrawler();
				}
				break;
			case KeyEvent.VK_H:
				if (hatredButton != null) {
					keyHandled = true;
					setHatred();
				}
				break;
			case KeyEvent.VK_V:
				if (anyDiceButton != null && anyDiceButton.isEnabled()) {
					keyHandled = true;
					evaluateCheckboxes();
				}
				break;
			case KeyEvent.VK_L:
				if (proFallbackMascot != null) {
					proFallbackMascot.setSelected(!proFallbackMascot.isSelected());
					if (proFallbackTrr != null) {
						if (!proFallbackMascot.isSelected()) {
							proFallbackTrr.setSelected(false);
						}
						proFallbackTrr.setEnabled(proFallbackMascot.isSelected());
					}
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

	private void determineTeamReRollSource() {
		if (willUseMascot) {
			if (fallbackToTrr.isSelected()) {
				fReRollSource = ReRollSources.MASCOT_TRR;
			} else {
				fReRollSource = ReRollSources.MASCOT;
			}
		} else {
			fReRollSource = ReRollSources.TEAM_RE_ROLL;
		}
	}

	private void determineProReRollSource() {
		boolean mascot = proFallbackMascot != null && proFallbackMascot.isSelected();
		boolean reRoll = proFallbackTrr != null && proFallbackTrr.isSelected();

		if (mascot && reRoll) {
			fReRollSource = ReRollSources.PRO_MASCOT_TRR;
		} else if (mascot) {
			fReRollSource = ReRollSources.PRO_MASCOT;
		} else if (reRoll) {
			fReRollSource = ReRollSources.PRO_TRR;
		} else {
			fReRollSource = ReRollSources.PRO;
		}
	}
}
