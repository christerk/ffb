package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JCheckBox;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogReRollBlockForTargetsPropertiesParameter;
import com.fumbbl.ffb.model.BlockPropertiesRoll;
import com.fumbbl.ffb.util.UtilCards;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.ObjIntConsumer;

public class DialogReRollBlockForTargetsProperties extends AbstractDialogMultiBlockProperties {

	private final DialogReRollBlockForTargetsPropertiesParameter dialogParameter;
	private ReRollSource reRollSource;
	private final ReRollSource singleDieReRollSource;
	private final List<Integer> anyDiceIndexes = new ArrayList<>();

	@SuppressWarnings("FieldCanBeLocal")
	private final List<Mnemonics> mnemonics = new ArrayList<Mnemonics>() {{
		add(new Mnemonics('T', 'N', 'B',
			new ArrayList<Character>() {{
				add('P');
				add('o');
				add('x');
			}},
			new ArrayList<Character>() {{
				add('C');
				add('u');
				add('m');
			}}, 'S'));
		add(new Mnemonics('e', 'l', 'r',
			new ArrayList<Character>() {{
				add('r');
				add('y');
				add('z');
			}},
			new ArrayList<Character>() {{
				add('a');
				add('f');
				add('v');
			}}, 'b'));
	}};
	private int proIndex;

	public DialogReRollBlockForTargetsProperties(FantasyFootballClient pClient, DialogReRollBlockForTargetsPropertiesParameter parameter) {

		super(pClient, "Block Roll", false);

		dialogParameter = parameter;

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setAlignmentX(CENTER_ALIGNMENT);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

		singleDieReRollSource = UtilCards.getUnusedRerollSource(pClient.getGame().getActingPlayer(),
			ReRolledActions.SINGLE_DIE);

		for (BlockPropertiesRoll blockRoll : parameter.getBlockRolls()) {

			String target = blockRoll.getTargetId();
			boolean ownChoice = blockRoll.isOwnChoice();
			Color background = ownChoice ? colorOwnChoice : colorOpponentChoice;
			JPanel targetPanel = new BackgroundPanel(background);
			targetPanel.setLayout(new BoxLayout(targetPanel, BoxLayout.Y_AXIS));
			targetPanel.setAlignmentX(CENTER_ALIGNMENT);
			boolean hasSavageBlow = blockRoll.has(ReRollProperty.SAVAGE_BLOW);
			JPanel dicePanel = dicePanel(blockRoll, ownChoice && blockRoll.needsSelection(), blockDieMnemonics.remove(0),
				hasSavageBlow && blockRoll.getNrOfDice() > 1);
			targetPanel.add(dicePanel);
			if (blockRoll.hasReRollsLeft()) {
				Mnemonics currentMnemonics = mnemonics.remove(0);
				JPanel buttonPanel = new JPanel();
				buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
				buttonPanel.setAlignmentX(CENTER_ALIGNMENT);
				buttonPanel.add(Box.createHorizontalGlue());
				buttonPanel.setOpaque(false);

				if (blockRoll.has(ReRollProperty.TRR)) {
					buttonPanel.add(
						createReRollButton(target, "Team Re-Roll", ReRollSources.TEAM_RE_ROLL, currentMnemonics.team));
					buttonPanel.add(Box.createHorizontalGlue());
				}
				if (blockRoll.getNrOfDice() == 1) {
					if (blockRoll.has(ReRollProperty.PRO)) {
						buttonPanel.add(createReRollButton(target, "Pro Re-Roll", ReRollSources.PRO, currentMnemonics.pro.get(0)));
						buttonPanel.add(Box.createHorizontalGlue());
					}
					if (blockRoll.has(ReRollProperty.ANY_DIE_RE_ROLL) && singleDieReRollSource != null) {
						buttonPanel.add(
							createReRollButton(target, singleDieReRollSource.getName(pClient.getGame()), singleDieReRollSource,
								currentMnemonics.anyDie.get(0)));
						buttonPanel.add(Box.createHorizontalGlue());
					}
				}
				if (blockRoll.has(ReRollProperty.BRAWLER)) {
					buttonPanel.add(
						createReRollButton(target, "Brawler Re-Roll", ReRollSources.BRAWLER, currentMnemonics.brawler));
					buttonPanel.add(Box.createHorizontalGlue());
				}
				if (blockRoll.has(ReRollProperty.SAVAGE_BLOW)) {
					JButton anyDiceButton = createReRollButton(target, "Savage Blow", ReRollSources.SAVAGE_BLOW,
						currentMnemonics.anyBlockDice);
					anyDiceButton.setEnabled(blockRoll.getNrOfDice() == 1);
					anyDiceButtons.put(target, anyDiceButton);
					buttonPanel.add(anyDiceButton);
					buttonPanel.add(Box.createHorizontalGlue());
				}
				if (!ownChoice) {
					buttonPanel.add(createReRollButton(target, "No Re-Roll", null, currentMnemonics.none));
					buttonPanel.add(Box.createHorizontalGlue());
				}
				targetPanel.add(Box.createVerticalStrut(3));
				targetPanel.add(buttonPanel);

				if (Math.abs(blockRoll.getNrOfDice()) > 1) {
					if (blockRoll.has(ReRollProperty.PRO)) {
						targetPanel.add(createSingleDieReRollPanel(proTextPanel(),
							blockRoll.getTargetId(), Math.abs(blockRoll.getNrOfDice()), blockRoll.getReRollDiceIndexes(),
							currentMnemonics.pro, this::proAction));
					}
					if (blockRoll.has(ReRollProperty.ANY_DIE_RE_ROLL) && singleDieReRollSource != null) {
						targetPanel.add(createSingleDieReRollPanel(textPanel(singleDieReRollSource.getName(getClient().getGame())),
							blockRoll.getTargetId(), Math.abs(blockRoll.getNrOfDice()), blockRoll.getReRollDiceIndexes(),
							currentMnemonics.anyDie, this::anyDieAction));
					}
				}

				targetPanel.add(Box.createVerticalStrut(3));
			}

			if (!ownChoice) {
				targetPanel.add(opponentChoicePanel());
			}

			targetPanel.add(namePanel(target));
			mainPanel.add(targetPanel);
			mainPanel.add(Box.createVerticalStrut(5));
		}

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(mainPanel);

		pack();
		setLocationToCenter();

	}

	private JPanel createSingleDieReRollPanel(JPanel titlePanel, String target, int diceCount, int[] reRolledDiceIndexes, List<Character> mnemonics, ObjIntConsumer<String> consumer) {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setAlignmentX(CENTER_ALIGNMENT);
		panel.add(titlePanel);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setAlignmentX(CENTER_ALIGNMENT);

		for (int die = 1; die <= diceCount; die++) {
			int finalDie = die;
			if (Arrays.stream(reRolledDiceIndexes).noneMatch(index -> index == finalDie - 1)) {
				JButton proButton = new JButton(dimensionProvider(), "Die " + die, mnemonics.get(0));
				proButton.addActionListener(e -> consumer.accept(target, finalDie - 1));
				proButton.setMnemonic(mnemonics.get(0));
				this.addKeyListener(new PressedKeyListener(mnemonics.get(0)) {
					@Override
					protected void handleKey() {
						consumer.accept(target, finalDie - 1);
					}
				});
				buttonPanel.add(proButton);
			}
			mnemonics.remove(0);
		}

		panel.add(buttonPanel);
		return panel;
	}

	private void proAction(String target, int proIndex) {
		reRollSource = ReRollSources.PRO;
		this.proIndex = proIndex;
		this.selectedTarget = target;
		close();
	}

	private void anyDieAction(String target, int index) {
		reRollSource = singleDieReRollSource;
		this.proIndex = index;
		this.selectedTarget = target;
		close();
	}

	private JButton createReRollButton(String target, String buttonName, ReRollSource reRollSource, char mnemonic) {
		JButton button = new JButton(dimensionProvider(), buttonName, mnemonic);
		button.addActionListener(e -> handleReRollUse(target, reRollSource));
		this.addKeyListener(new PressedKeyListener(mnemonic) {
			@Override
			protected void handleKey() {
				handleReRollUse(target, reRollSource);
			}
		});
		button.setMnemonic((int) mnemonic);
		return button;
	}

	private void handleReRollUse(String target, ReRollSource reRollSource) {
		this.reRollSource = reRollSource;
		selectedTarget = target;
		if (reRollSource == ReRollSources.SAVAGE_BLOW) {
			List<JCheckBox> boxes = anyDiceCheckBoxes.get(target);
			if (boxes == null) {
				anyDiceIndexes.add(0);
			} else {
				for (int i = 0; i < boxes.size(); i++) {
					if (boxes.get(i).isSelected()) {
						anyDiceIndexes.add(i);
					}
				}
			}
		}
		close();
	}

	@Override
	protected void close() {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(DialogReRollBlockForTargetsProperties.this);
		}
	}

	public DialogId getId() {
		return DialogId.RE_ROLL_BLOCK_FOR_TARGETS_PROPERTIES;
	}

	public String getSelectedTarget() {
		return selectedTarget;
	}

	public ReRollSource getReRollSource() {
		return reRollSource;
	}

	public Integer getSelectedIndex() {
		return selectedIndex;
	}

	public int getProIndex() {
		return proIndex;
	}

	public List<Integer> getAnyDiceIndexes() {
		return anyDiceIndexes;
	}

	public DialogReRollBlockForTargetsPropertiesParameter getDialogParameter() {
		return dialogParameter;
	}

	private static class Mnemonics {
		private final char team, brawler, none, anyBlockDice;
		private final List<Character> pro, anyDie;

		public Mnemonics(char team, char none, char brawler, List<Character> pro, List<Character> anyDie, char anyBlockDice) {
			this.team = team;
			this.none = none;
			this.brawler = brawler;
			this.pro = pro;
			this.anyDie = anyDie;
			this.anyBlockDice = anyBlockDice;
		}
	}
}
