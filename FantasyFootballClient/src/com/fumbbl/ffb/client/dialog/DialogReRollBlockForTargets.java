package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogReRollBlockForTargetsParameter;
import com.fumbbl.ffb.model.BlockRoll;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DialogReRollBlockForTargets extends AbstractDialogMultiBlock {

	private final DialogReRollBlockForTargetsParameter dialogParameter;
	private ReRollSource reRollSource;
	private int brawlerSelection, proIndex;

	private final List<Mnemonics> mnemonics = new ArrayList<Mnemonics>() {{
		add(new Mnemonics('T', 'N',
			new ArrayList<Character>() {{
				add('P');
				add('o');
				add('x');
			}},
			new ArrayList<Character>() {{
			add('B');
			add('h');
			add('d');
		}}));
		add(new Mnemonics('e', 'l',
			new ArrayList<Character>() {{
				add('r');
				add('y');
				add('z');
			}},
			new ArrayList<Character>() {{
			add('w');
			add('s');
			add('u');
		}}));
	}};

	public DialogReRollBlockForTargets(FantasyFootballClient pClient, DialogReRollBlockForTargetsParameter parameter) {

		super(pClient, "Block Roll", false);

		dialogParameter = parameter;

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setAlignmentX(CENTER_ALIGNMENT);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

		for (BlockRoll blockRoll : parameter.getBlockRolls()) {

			String target = blockRoll.getTargetId();
			boolean ownChoice = blockRoll.isOwnChoice();
			Color background = ownChoice ? colorOwnChoice : colorOpponentChoice;
			JPanel targetPanel = new BackgroundPanel(background);
			targetPanel.setLayout(new BoxLayout(targetPanel, BoxLayout.Y_AXIS));
			targetPanel.setAlignmentX(CENTER_ALIGNMENT);
			JPanel dicePanel = dicePanel(blockRoll, ownChoice && blockRoll.needsSelection(), keyEvents.remove(0));
			targetPanel.add(dicePanel);
			if (blockRoll.hasReRollsLeft()) {
				Mnemonics currentMnemonics = mnemonics.remove(0);
				JPanel buttonPanel = new JPanel();
				buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
				buttonPanel.setAlignmentX(CENTER_ALIGNMENT);
				buttonPanel.add(Box.createHorizontalGlue());
				buttonPanel.setOpaque(false);

				if (blockRoll.has(ReRollSources.TEAM_RE_ROLL)) {
					buttonPanel.add(createReRollButton(target, "Team Re-Roll", ReRollSources.TEAM_RE_ROLL, currentMnemonics.team));
					buttonPanel.add(Box.createHorizontalGlue());
				}
				if (blockRoll.has(ReRollSources.PRO) && blockRoll.getNrOfDice() == 1) {
					buttonPanel.add(createReRollButton(target, "Pro Re-Roll", ReRollSources.PRO, currentMnemonics.pro.get(0)));
					buttonPanel.add(Box.createHorizontalGlue());
				}
				if (!ownChoice) {
					buttonPanel.add(createReRollButton(target, "No Re-Roll", null, currentMnemonics.none));
					buttonPanel.add(Box.createHorizontalGlue());
				}
				targetPanel.add(Box.createVerticalStrut(3));
				targetPanel.add(buttonPanel);

				if (blockRoll.has(ReRollSources.PRO) && Math.abs(blockRoll.getNrOfDice()) > 1) {
					targetPanel.add(createProPanel(blockRoll.getTargetId(), Math.abs(blockRoll.getNrOfDice()), blockRoll.getReRollDiceIndexes(), currentMnemonics.pro));
				}

				if (blockRoll.has(ReRollSources.BRAWLER) && blockRoll.getBrawlerOptions() > 0) {
					targetPanel.add(createBrawlerPanel(blockRoll.getTargetId(), blockRoll.getBrawlerOptions(), blockRoll.getReRollDiceIndexes(), currentMnemonics.brawler));
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

	private JPanel createProPanel(String target, int diceCount, int[] reRolledDiceIndexes, List<Character> mnemonics) {
		JPanel proPanel = new JPanel();
		proPanel.setOpaque(false);
		proPanel.setLayout(new BoxLayout(proPanel, BoxLayout.Y_AXIS));
		proPanel.setAlignmentX(CENTER_ALIGNMENT);
		proPanel.add(proTextPanel());

		JPanel proButtonPanel = new JPanel();
		proButtonPanel.setLayout(new BoxLayout(proButtonPanel, BoxLayout.X_AXIS));
		proButtonPanel.setAlignmentX(CENTER_ALIGNMENT);

		for (int die = 1; die <= diceCount; die++) {
			int finalDie = die;
			if (Arrays.stream(reRolledDiceIndexes).noneMatch(index -> index == finalDie - 1)) {
				JButton proButton = new JButton();
				proButton.setText("Die " + die);
				proButton.addActionListener(e -> proAction(target, finalDie - 1));
				proButton.setMnemonic(mnemonics.get(0));
				this.addKeyListener(new PressedKeyListener(mnemonics.get(0)) {
					@Override
					protected void handleKey() {
						proAction(target, finalDie - 1);
					}
				});
				proButtonPanel.add(proButton);
			}
			mnemonics.remove(0);
		}

		proPanel.add(proButtonPanel);
		return proPanel;
	}

	private JPanel createBrawlerPanel(String target, int brawlerOptions, int[] reRolledDiceIndexes, List<Character> mnemonics) {
		JPanel brawlerPanel = new JPanel();
		brawlerPanel.setOpaque(false);
		brawlerPanel.setLayout(new BoxLayout(brawlerPanel, BoxLayout.Y_AXIS));
		brawlerPanel.setAlignmentX(CENTER_ALIGNMENT);
		brawlerPanel.add(brawlerTextPanel());

		JPanel brawlerButtonPanel = new JPanel();
		brawlerButtonPanel.setLayout(new BoxLayout(brawlerButtonPanel, BoxLayout.X_AXIS));
		brawlerButtonPanel.setAlignmentX(CENTER_ALIGNMENT);

		for (int brawlerSelection = 1; brawlerSelection <= brawlerOptions; brawlerSelection++) {
			int finalBrawlerSelection = brawlerSelection;
			if (Arrays.stream(reRolledDiceIndexes).noneMatch(index -> index == finalBrawlerSelection - 1 )) {
				JButton brawlerButton = new JButton();
				brawlerButton.setText(brawlerSelection + " Both Down" + (brawlerSelection > 1 ? "s" : ""));
				brawlerButton.addActionListener(e -> brawlerAction(target, finalBrawlerSelection));
				brawlerButton.setMnemonic(mnemonics.get(0));
				this.addKeyListener(new PressedKeyListener(mnemonics.get(0)) {
					@Override
					protected void handleKey() {
						brawlerAction(target, finalBrawlerSelection);
					}
				});
				brawlerButtonPanel.add(brawlerButton);
			}
			mnemonics.remove(0);
		}

		brawlerPanel.add(brawlerButtonPanel);
		return brawlerPanel;
	}

	private void proAction(String target, int proIndex) {
		reRollSource = ReRollSources.PRO;
		this.proIndex = proIndex;
		this.selectedTarget = target;
		close();
	}

	private void brawlerAction(String target, int brawlerSelection) {
		reRollSource = ReRollSources.BRAWLER;
		this.brawlerSelection = brawlerSelection;
		this.selectedTarget = target;
		close();
	}

	private JButton createReRollButton(String target, String buttonName, ReRollSource reRollSource, char mnemonic) {
		JButton button = new JButton(buttonName);
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
		close();
	}

	@Override
	protected void close() {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(DialogReRollBlockForTargets.this);
		}
	}

	public DialogId getId() {
		return DialogId.RE_ROLL_BLOCK_FOR_TARGETS;
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

	public int getBrawlerSelection() {
		return brawlerSelection;
	}

	public int getProIndex() {
		return proIndex;
	}

	public DialogReRollBlockForTargetsParameter getDialogParameter() {
		return dialogParameter;
	}

	private static class Mnemonics {
		private final char team, none;
		private final List<Character> brawler, pro;

		public Mnemonics(char team, char none, List<Character> pro, List<Character> brawler) {
			this.team = team;
			this.none = none;
			this.brawler = brawler;
			this.pro = pro;
		}
	}
}
