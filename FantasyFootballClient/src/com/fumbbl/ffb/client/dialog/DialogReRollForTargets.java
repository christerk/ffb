package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.dialog.AbstractDialogMultiBlock.PressedKeyListener;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogReRollForTargetsParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

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
		ReRollSource singleUseReRollSource = parameter.getSingleUseReRollSource();

		JButton fButtonNoReRoll = new JButton("No Re-Roll");
		fButtonNoReRoll.addActionListener(e -> close());
		this.addKeyListener(new PressedKeyListener('N') {
			@Override
			protected void handleKey() {
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
			if (dialogParameter.getTargetIds().size() > 1) {
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
			if (parameter.getReRollAvailableAgainst().contains(target)) {
				Player<?> player = game.getPlayerById(parameter.getTargetIds().get(index));
				if (parameter.getMinimumRolls().size() > index) {
					JPanel textPanel = new JPanel();
					textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
					textPanel.setAlignmentX(CENTER_ALIGNMENT);
					textPanel.setBackground(HIGHLIGHT);
					Arrays.stream(new String[] { "<html>The roll against " + player.getName() + " failed</html>",
						"<html>You will need a roll of " + parameter.getMinimumRolls().get(target) + "+ to succeed.</html>" })
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

				if (parameter.isTeamReRollAvailable()) {
					buttonPanel.add(createButton(target, "Team Re-Roll", ReRollSources.TEAM_RE_ROLL, index == 0 ? 'T' : 'e'));
					buttonPanel.add(Box.createHorizontalGlue());
				}
				if (singleUseReRollSource != null) {
					buttonPanel.add(createButton(target, singleUseReRollSource.getName(game), singleUseReRollSource, index == 0 ? 'L' : 'r'));
					buttonPanel.add(Box.createHorizontalGlue());
				}
				if (parameter.isProReRollAvailable()) {
					buttonPanel.add(createButton(target, "Pro Re-Roll", ReRollSources.PRO, index == 0 ? 'P' : 'o'));
					buttonPanel.add(Box.createHorizontalGlue());
				}
				if (parameter.isConsummateAvailable() && reRollingPlayer != null) {
					Skill reRollSkill = reRollingPlayer.getSkillWithProperty(NamedProperties.canRerollSingleDieOncePerPeriod);
					if (reRollSkill != null) {
						ReRollSource source = reRollSkill.getRerollSource(ReRolledActions.SINGLE_DIE);
						if (source != null) {
							buttonPanel.add(createButton(target, source.getName(game), source, index == 0 ? 'C' : 'm'));
						}
					}
				}
				if (parameter.getReRollSkill() != null) {
					buttonPanel.add(createButton(target, parameter.getReRollSkill().getName() + " Re-Roll", parameter.getReRollSkill().getRerollSource(ReRolledActions.DAUNTLESS), index == 0 ? 'S' : 'k'));
					buttonPanel.add(Box.createHorizontalGlue());
				}
				targetPanel.add(Box.createVerticalStrut(3));
				targetPanel.add(buttonPanel);
				targetPanel.add(Box.createVerticalStrut(3));
				targetPanel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.BLACK, 1), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
				targetPanel.setBackground(HIGHLIGHT);
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
		button.addActionListener(e -> handleUserInteraction(target, reRollSource));
		this.addKeyListener(new PressedKeyListener(mnemonic) {
			@Override
			protected void handleKey() {
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

}
