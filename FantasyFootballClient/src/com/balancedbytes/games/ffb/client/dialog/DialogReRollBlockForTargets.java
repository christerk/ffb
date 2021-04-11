package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRollSources;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogReRollBlockForTargetsParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class DialogReRollBlockForTargets extends AbstractDialogMultiBlock {

	private final DialogReRollBlockForTargetsParameter dialogParameter;
	private ReRollSource reRollSource;

	@SuppressWarnings("FieldCanBeLocal")
	private final List<Mnemonics> mnemonics = new ArrayList<Mnemonics>() {{
		add(new Mnemonics('T', 'P'));
		add(new Mnemonics('e', 'o'));
	}};

	public DialogReRollBlockForTargets(FantasyFootballClient pClient, DialogReRollBlockForTargetsParameter parameter) {

		super(pClient, "Select Block Result or Use a Re-roll", false);

		dialogParameter = parameter;

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

			mainMessagePanel.add(nameLabel(target));
		}

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(mainMessagePanel);

		pack();
		setLocationToCenter();

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

	public DialogReRollBlockForTargetsParameter getDialogParameter() {
		return dialogParameter;
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
