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
	private final List<Mnemonics> mnemonics = new ArrayList<Mnemonics>() {{
		add(new Mnemonics('T', 'N', 'B', 'S',
			new ArrayList<Character>() {{
				add('P');
				add('o');
				add('x');
			}}));
		add(new Mnemonics('e', 'l', 'r', 'i',
			new ArrayList<Character>() {{
				add('r');
				add('y');
				add('z');
			}}));
	}};
	private int proIndex;

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
				if (blockRoll.has(ReRollSources.BRAWLER)) {
					buttonPanel.add(createReRollButton(target, "Brawler Re-Roll", ReRollSources.BRAWLER, currentMnemonics.brawler));
					buttonPanel.add(Box.createHorizontalGlue());
				}
				if (blockRoll.has(ReRollSources.LORD_OF_CHAOS)) {
					buttonPanel.add(createReRollButton(target, ReRollSources.LORD_OF_CHAOS.getName(pClient.getGame()), ReRollSources.LORD_OF_CHAOS, currentMnemonics.single));
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

	private void proAction(String target, int proIndex) {
		reRollSource = ReRollSources.PRO;
		this.proIndex = proIndex;
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

	public int getProIndex() {
		return proIndex;
	}

	public DialogReRollBlockForTargetsParameter getDialogParameter() {
		return dialogParameter;
	}

	private static class Mnemonics {
		private final char team, brawler, none, single;
		private final List<Character> pro;

		public Mnemonics(char team, char none, char brawler, char single, List<Character> pro) {
			this.team = team;
			this.none = none;
			this.brawler = brawler;
			this.pro = pro;
			this.single = single;
		}
	}
}
