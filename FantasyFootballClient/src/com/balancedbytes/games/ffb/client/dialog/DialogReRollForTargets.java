package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.FactoryType.Factory;
import com.balancedbytes.games.ffb.IIconProperty;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRollSources;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogReRollForTargetsParameter;
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
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DialogReRollForTargets extends Dialog {

	public static final Color HIGHLIGHT = Color.lightGray;
	private final DialogReRollForTargetsParameter dialogParameter;
	private ReRollSource reRollSource;
	private String selectedTarget;

	public DialogReRollForTargets(FantasyFootballClient pClient, DialogReRollForTargetsParameter parameter) {

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

		String action = dialogParameter.getReRolledAction().getName(pClient.getGame().getRules().getFactory(Factory.SKILL));

		if (dialogParameter.getMinimumRolls().isEmpty()) {
			mainMessage.append("<html>Do you want to re-roll the ").append(action).append("?</html>");
		} else {
			mainMessage.append("<html>Do you want to re-roll the failed ").append(action);
			if (dialogParameter.getMinimumRolls().size() > 1) {
				mainMessage.append(" rolls");
			}
			mainMessage.append("?</html>");
		}

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

		JPanel detailPanel = new JPanel();
		detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
		detailPanel.setAlignmentX(CENTER_ALIGNMENT);
		for (int index = 0; index < parameter.getTargetIds().size(); index++) {

			JPanel targetPanel = new JPanel();
			targetPanel.setLayout(new BoxLayout(targetPanel, BoxLayout.Y_AXIS));
			targetPanel.setAlignmentX(CENTER_ALIGNMENT);

			String target = parameter.getTargetIds().get(index);
			boolean teamReRollAvailable = parameter.getTeamReRollAvailableAgainst().contains(target);
			if (teamReRollAvailable || parameter.isProReRollAvailable()) {
				Player<?> player = game.getPlayerById(parameter.getTargetIds().get(index));
				if (parameter.getMinimumRolls().size() > index) {
					JPanel textPanel = new JPanel();
					textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
					textPanel.setAlignmentX(CENTER_ALIGNMENT);
					textPanel.setBackground(Color.lightGray);
					Arrays.stream(new String[] { "<html>The roll against " + player.getName() + " failed</html>",
						"<html>You will need a roll of " + parameter.getMinimumRolls().get(0) + "+ to succeed.</html>" })
					.map(JLabel::new).forEach(label -> {
						label.setHorizontalAlignment(SwingConstants.CENTER);
						textPanel.add(label);
					});
					targetPanel.add(textPanel);
				}

				JPanel buttonPanel = new JPanel();
				buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
				buttonPanel.setAlignmentX(CENTER_ALIGNMENT);
				buttonPanel.add(Box.createHorizontalGlue());
				buttonPanel.setBackground(HIGHLIGHT);

				if (teamReRollAvailable) {
					buttonPanel.add(createButton(target, "Team Re-Roll", ReRollSources.TEAM_RE_ROLL, index == 0 ? 'T' : 'E'));
					buttonPanel.add(Box.createHorizontalGlue());
				}
				if (parameter.isProReRollAvailable()) {
					buttonPanel.add(createButton(target, "Pro Re-Roll", ReRollSources.PRO, index == 0 ? 'P' : 'O'));
					buttonPanel.add(Box.createHorizontalGlue());
				}
				targetPanel.add(Box.createVerticalStrut(3));
				targetPanel.add(buttonPanel);
				targetPanel.add(Box.createVerticalStrut(3));
				targetPanel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.BLACK, 1), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
				targetPanel.setBackground(Color.lightGray);
				detailPanel.add(targetPanel);
				detailPanel.add(Box.createVerticalStrut(5));
			}
		}

		JPanel bottomPanel = new JPanel();
		bottomPanel.setAlignmentX(CENTER_ALIGNMENT);
		bottomPanel.add(fButtonNoReRoll);
		detailPanel.add(bottomPanel);

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
		infoPanel.setAlignmentX(CENTER_ALIGNMENT);
		infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		BufferedImage icon = getClient().getUserInterface().getIconCache().getIconByProperty(IIconProperty.GAME_DICE_SMALL);
		JLabel iconLabel = new JLabel(new ImageIcon(icon));
		iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
		infoPanel.add(iconLabel);
		infoPanel.add(Box.createHorizontalStrut(5));
		infoPanel.add(detailPanel);
		infoPanel.add(Box.createHorizontalGlue());

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(mainMessagePanel);
		getContentPane().add(infoPanel);

		pack();
		setLocationToCenter();

	}

	private JButton createButton(String target, String buttonName, ReRollSource reRollSource, char mnemonic) {
		JButton button = new JButton(buttonName);
		button.addActionListener(e -> {
			handleUserInteraction(target, reRollSource);
		});
		button.addKeyListener(new PressedKeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				handleUserInteraction(target, reRollSource);
			}
		});
		button.setMnemonic((int) mnemonic);
		return button;
	}

	private void handleUserInteraction(String target, ReRollSource reRollSource) {
		this.reRollSource = reRollSource;
		selectedTarget = target;
		close();
	}

	private void close() {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(DialogReRollForTargets.this);
		}
	}

	public DialogId getId() {
		return DialogId.RE_ROLL_FOR_TARGETS;
	}

	public String getSelectedTarget() {
		return selectedTarget;
	}

	public ReRollSource getReRollSource() {
		return reRollSource;
	}

	public ReRolledAction getReRolledAction() {
		return dialogParameter.getReRolledAction();
	}

	public DialogReRollForTargetsParameter getDialogParameter() {
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
}
