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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

public class DialogReRollForTargets extends Dialog {

	private final DialogReRollForTargetsParameter dialogParameter;
	private ReRollSource reRollSource;
	private String selectedTarget;

	public DialogReRollForTargets(FantasyFootballClient pClient, DialogReRollForTargetsParameter parameter) {

		super(pClient, "Use a Re-roll", false);

		dialogParameter = parameter;

		JButton fButtonNoReRoll = new JButton("No Re-Roll");
		fButtonNoReRoll.addActionListener(e -> {
			if (getCloseListener() != null) {
				getCloseListener().dialogClosed(this);
			}
		});
		fButtonNoReRoll.addKeyListener(new PressedKeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (getCloseListener() != null) {
					getCloseListener().dialogClosed(DialogReRollForTargets.this);
				}
			}
		});
		fButtonNoReRoll.setMnemonic((int) 'N');

		StringBuilder mainMessage = new StringBuilder();

		String action = dialogParameter.getReRolledAction().getName(pClient.getGame().getRules().getFactory(Factory.SKILL));

		if (dialogParameter.getMinimumRolls().isEmpty()) {
			mainMessage.append("Do you want to re-roll the ").append(action).append("?");
		} else {
			mainMessage.append("Do you want to re-roll the failed ").append(action);
			if (dialogParameter.getMinimumRolls().size() > 1) {
				mainMessage.append(" rolls");
			}
			mainMessage.append("?");
		}

		JPanel mainMessagePanel = new JPanel();
		mainMessagePanel.setLayout(new BoxLayout(mainMessagePanel, BoxLayout.Y_AXIS));
		mainMessagePanel.add(new JLabel(mainMessage.toString()));

		Game game = getClient().getGame();
		Player<?> reRollingPlayer = game.getPlayerById(parameter.getPlayerId());
		if ((reRollingPlayer != null)
			&& reRollingPlayer.hasSkillProperty(NamedProperties.hasToRollToUseTeamReroll)) {
			mainMessagePanel.add(Box.createVerticalStrut(5));
			mainMessagePanel.add(new JLabel("Player is a LONER - the Re-Roll is not guaranteed to help."));
		}

		for (int index = 0; index < parameter.getTargetIds().size(); index++) {
			mainMessagePanel.add(Box.createVerticalStrut(5));
			Player<?> player = game.getPlayerById(parameter.getTargetIds().get(index));
			String detailMessage;
			if (parameter.getMinimumRolls().size() > index) {
				detailMessage = "<html>The roll against " + player.getName() + "failed.<br/>" +
					"You will need a roll of " + parameter.getMinimumRolls().get(0) + "+ to succeed.</html>";
			} else {
				detailMessage = "<html>The roll against " + player.getName() + "was:";
			}
			JLabel detailLabel = new JLabel(detailMessage);
			detailLabel.setHorizontalAlignment(SwingConstants.CENTER);
			mainMessagePanel.add(detailLabel);
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
			String target = parameter.getTargetIds().get(index);
			if (dialogParameter.isTeamReRollAvailable()) {
				buttonPanel.add(createButton(target, "Team Re-Roll", ReRollSources.TEAM_RE_ROLL, index == 0 ? 'T' : 'E'));
				buttonPanel.add(Box.createHorizontalStrut(5));
			}
			if (dialogParameter.isProReRollAvailable()) {
				buttonPanel.add(createButton(target, "Pro Re-Roll", ReRollSources.PRO, index == 0 ? 'P' : 'O'));
				buttonPanel.add(Box.createHorizontalStrut(5));
			}
			mainMessagePanel.add(buttonPanel);
		}

		mainMessagePanel.add(fButtonNoReRoll);

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
		infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		BufferedImage icon = getClient().getUserInterface().getIconCache().getIconByProperty(IIconProperty.GAME_DICE_SMALL);
		infoPanel.add(new JLabel(new ImageIcon(icon)));
		infoPanel.add(Box.createHorizontalStrut(5));
		infoPanel.add(mainMessagePanel);
		infoPanel.add(Box.createHorizontalGlue());

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(infoPanel);

		pack();
		setLocationToCenter();

	}

	private JButton createButton(String target, String buttonName, ReRollSource reRollSource, char mnemonic) {
		JButton button = new JButton(buttonName);
		button.addActionListener(e -> {
			this.reRollSource = reRollSource;
			selectedTarget = target;
		});
		button.addKeyListener(new PressedKeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				DialogReRollForTargets.this.reRollSource = reRollSource;
				selectedTarget = target;
				if (getCloseListener() != null) {
					getCloseListener().dialogClosed(DialogReRollForTargets.this);
				}
			}
		});
		button.setMnemonic((int) mnemonic);
		return button;
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
