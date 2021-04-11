package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRollSources;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IconCache;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogReRollBlockForTargetsParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class DialogReRollBlockForTargets extends AbstractDialogBlock {

	private final DialogReRollBlockForTargetsParameter dialogParameter;
	private String selectedTarget;
	private int selectedIndex = -1;
	private ReRollSource reRollSource;

	@SuppressWarnings("FieldCanBeLocal")
	private final List<Mnemonics> mnemonics = new ArrayList<Mnemonics>() {{
		add(new Mnemonics('T', 'P'));
		add(new Mnemonics('e', 'o'));
	}};

	@SuppressWarnings("FieldCanBeLocal")
	private final List<List<Integer>> keyEvents = new ArrayList<List<Integer>>() {{
		add(new ArrayList<Integer>() {{
			add(KeyEvent.VK_1);
			add(KeyEvent.VK_2);
			add(KeyEvent.VK_3);
		}});
		add(new ArrayList<Integer>() {{
			add(KeyEvent.VK_4);
			add(KeyEvent.VK_5);
			add(KeyEvent.VK_6);
		}});
	}};


	public DialogReRollBlockForTargets(FantasyFootballClient pClient, DialogReRollBlockForTargetsParameter parameter) {

		super(pClient, "Use a Re-roll", false);

		dialogParameter = parameter;

		JButton fButtonNoReRoll = new JButton("No Re-Roll");
		fButtonNoReRoll.addActionListener(e -> {
			close();
		});
		fButtonNoReRoll.addKeyListener(new PressedKeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				close();
			}
		});
		fButtonNoReRoll.setMnemonic((int) 'N');

		StringBuilder mainMessage = new StringBuilder();

		mainMessage.append("<html>Do you want to re-roll the block");
		if (dialogParameter.getTargetIds().size() > 1) {
			mainMessage.append("s");
		}
		mainMessage.append("?</html>");

		List<String> mainMessages = new ArrayList<>();
		mainMessages.add(mainMessage.toString());

		Game game = getClient().getGame();
		Player<?> reRollingPlayer = game.getPlayerById(parameter.getPlayerId());
		if ((reRollingPlayer != null)
			&& reRollingPlayer.hasSkillProperty(NamedProperties.hasToRollToUseTeamReroll)) {
			mainMessages.add("<html>Player is a LONER - the Re-Roll is not guaranteed to help.</html>");
		}

		JPanel mainMessagePanel = new JPanel();
		mainMessagePanel.setLayout(new BoxLayout(mainMessagePanel, BoxLayout.Y_AXIS));
		mainMessagePanel.setAlignmentX(CENTER_ALIGNMENT);
		mainMessagePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

		mainMessages.stream().map(JLabel::new).forEach(label -> {
			label.setHorizontalAlignment(SwingConstants.CENTER);
			mainMessagePanel.add(label);
			mainMessagePanel.add(Box.createVerticalStrut(5));
		});

		for (String target : parameter.getTargetIds()) {

			boolean ownChoice = parameter.getChoices().get(target) != null && parameter.getChoices().get(target);
			JPanel targetPanel = new BackgroundPanel(ownChoice ? colorOwnChoice : colorOpponentChoice);
			targetPanel.setLayout(new BoxLayout(targetPanel, BoxLayout.Y_AXIS));
			targetPanel.setAlignmentX(CENTER_ALIGNMENT);
			mainMessagePanel.add(targetPanel);
			JPanel dicePanel = dicePanel(parameter.getBlockRolls().get(target), target, ownChoice, keyEvents.remove(0));
			mainMessagePanel.add(dicePanel);
			if (parameter.getReRollAvailableAgainst().contains(target)) {
				Mnemonics currentMnemonics = mnemonics.remove(0);
				JPanel buttonPanel = new JPanel();
				buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
				buttonPanel.setAlignmentX(CENTER_ALIGNMENT);
				buttonPanel.add(Box.createHorizontalGlue());

				if (parameter.isTeamReRollAvailable()) {
					buttonPanel.add(createReRollButton(target, "Team Re-Roll", ReRollSources.TEAM_RE_ROLL, currentMnemonics.team));
					buttonPanel.add(Box.createHorizontalGlue());
				}
				if (parameter.isProReRollAvailable()) {
					buttonPanel.add(createReRollButton(target, "Pro Re-Roll", ReRollSources.PRO, currentMnemonics.pro));
					buttonPanel.add(Box.createHorizontalGlue());
				}
				targetPanel.add(Box.createVerticalStrut(3));
				targetPanel.add(buttonPanel);
				targetPanel.add(Box.createVerticalStrut(3));
			}

			if (!ownChoice) {
				mainMessagePanel.add(opponentChoicePanel());
			}
		}

		JPanel bottomPanel = new JPanel();
		bottomPanel.setAlignmentX(CENTER_ALIGNMENT);
		bottomPanel.add(fButtonNoReRoll);
		mainMessagePanel.add(bottomPanel);

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(mainMessagePanel);

		pack();
		setLocationToCenter();

	}

	private JButton dieButton(int blockRoll) {
		IconCache iconCache = getClient().getUserInterface().getIconCache();

		JButton button = new JButton();
		button.setOpaque(false);
		button.setBounds(0, 0, 45, 45);
		button.setFocusPainted(false);
		button.setMargin(new Insets(5, 5, 5, 5));
		button.setIcon(new ImageIcon(iconCache.getDiceIcon(blockRoll)));
		return button;
	}

	private JPanel dicePanel(List<Integer> blockRoll, String targetId, boolean activeButtons, List<Integer> events) {
		JPanel panel = blockRollPanel();

		for (int i = 0; i < blockRoll.size(); i++) {
			JButton dieButton = dieButton(blockRoll.get(i));

			if (activeButtons) {
				dieButton.setMnemonic(events.get(i));
				int index = i;
				dieButton.addActionListener(e -> {
					selectedTarget = targetId;
					selectedIndex = index;
					close();
				});
				dieButton.addKeyListener(new PressedKeyListener() {
					@Override
					public void keyPressed(KeyEvent e) {
						if (e.getKeyCode() == index) {
							selectedTarget = targetId;
							selectedIndex = index;
							close();
						}
					}
				});
			}
			panel.add(dieButton);
		}

		return panel;
	}

	private JButton createReRollButton(String target, String buttonName, ReRollSource reRollSource, char mnemonic) {
		JButton button = new JButton(buttonName);
		button.addActionListener(e -> {
			handleReRollUse(target, reRollSource);
		});
		button.addKeyListener(new PressedKeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
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

	private void close() {
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

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public DialogReRollBlockForTargetsParameter getDialogParameter() {
		return dialogParameter;
	}


	private static abstract class PressedKeyListener implements KeyListener {

		@Override
		public void keyTyped(KeyEvent e) {

		}

		@Override
		public void keyReleased(KeyEvent e) {

		}
	}

	private static class Mnemonics {
		private final char team;
		private final char pro;

		public Mnemonics(char team, char pro) {
			this.team = team;
			this.pro = pro;
		}
	}
}
