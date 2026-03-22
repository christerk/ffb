package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.dialog.AbstractDialogMultiBlock.PressedKeyListener;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogReRollForTargetsParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DialogReRollForTargets extends AbstractDialogForTargets {

	private final DialogReRollForTargetsParameter dialogParameter;
	private ReRollSource reRollSource;

	public DialogReRollForTargets(FantasyFootballClient pClient, DialogReRollForTargetsParameter parameter) {

		super(pClient, "Use a Re-roll");

		dialogParameter = parameter;
		ReRollSource singleUseReRollSource = parameter.getSingleUseReRollSource();

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

		JPanel detailPanel = createDetailPanel();
		for (int index = 0; index < parameter.getTargetIds().size(); index++) {

			String target = parameter.getTargetIds().get(index);
			if (parameter.getReRollAvailableAgainst().contains(target)) {
				Player<?> player = game.getPlayerById(parameter.getTargetIds().get(index));

				JPanel targetPanel = new JPanel();
				targetPanel.setLayout(new BoxLayout(targetPanel, BoxLayout.Y_AXIS));
				targetPanel.setAlignmentX(CENTER_ALIGNMENT);

				if (parameter.getMinimumRolls().size() > index) {
					JPanel textPanel = new JPanel();
					textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
					textPanel.setAlignmentX(CENTER_ALIGNMENT);
					textPanel.setBackground(HIGHLIGHT);
					Arrays.stream(new String[]{"<html>The roll against " + player.getName() + " failed</html>",
							"<html>You will need a roll of " + parameter.getMinimumRolls().get(target) + "+ to succeed.</html>"})
						.map(message -> new JLabel(dimensionProvider(), message)).forEach(label -> {
							label.setHorizontalAlignment(SwingConstants.CENTER);
							textPanel.add(label);
						});
					targetPanel.add(textPanel);
				}

				JPanel buttonPanel = createButtonPanel();

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
				addTargetPanel(detailPanel, targetPanel);
			}
		}

		init(mainMessages, detailPanel);
	}

	private JButton createButton(String target, String buttonName, ReRollSource reRollSource, char mnemonic) {
		JButton button = new JButton(dimensionProvider(), buttonName);
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

	public DialogId getId() {
		return DialogId.RE_ROLL_FOR_TARGETS;
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
