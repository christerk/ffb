package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogReRollForTargetsPropertiesParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class DialogReRollForTargetsProperties extends AbstractDialogForTargets {

	private final DialogReRollForTargetsPropertiesParameter dialogParameter;
	private ReRollSource reRollSource;

	public DialogReRollForTargetsProperties(FantasyFootballClient client,
		DialogReRollForTargetsPropertiesParameter parameter) {
		super(client, "Use a Re-roll");
		dialogParameter = parameter;
		Game game = client.getGame();
		String action = parameter.getReRolledAction().getName(game.getRules().getFactory(Factory.SKILL));
		List<String> messages = new ArrayList<>();
		messages.add(parameter.getMinimumRolls().isEmpty()
			? "<html>Do you want to re-roll the " + action + "?</html>"
			: "<html>Do you want to re-roll the failed " + action
				+ (parameter.getTargetIds().size() > 1 ? " rolls" : "") + "?</html>");
		Player<?> reRollingPlayer = game.getPlayerById(parameter.getPlayerId());
		if (reRollingPlayer != null && reRollingPlayer.hasSkillProperty(NamedProperties.hasToRollToUseTeamReroll)) {
			messages.add("<html>Player is a LONER - the Re-Roll is not guaranteed to help.</html>");
		}

		JPanel detailPanel = createDetailPanel();
		DialogExtensionReRolls extension = new DialogExtensionReRolls();
		for (int targetIndex = 0; targetIndex < parameter.getTargetIds().size(); targetIndex++) {
			String target = parameter.getTargetIds().get(targetIndex);
			if (!parameter.getReRollAvailableAgainst().contains(target)) {
				continue;
			}
			Player<?> opponent = game.getPlayerById(target);
			JPanel targetPanel = new JPanel();
			targetPanel.setLayout(new BoxLayout(targetPanel, BoxLayout.Y_AXIS));
			targetPanel.setAlignmentX(CENTER_ALIGNMENT);
			if (parameter.getMinimumRolls().containsKey(target)) {
				JPanel textPanel = new JPanel();
				textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
				textPanel.setAlignmentX(CENTER_ALIGNMENT);
				textPanel.setBackground(HIGHLIGHT);
				String[] details = {"<html>The roll against " + opponent.getName() + " failed</html>",
					"<html>You will need a roll of " + parameter.getMinimumRolls().get(target) + "+ to succeed.</html>"};
				for (String detail : details) {
					JLabel label = new JLabel(dimensionProvider(), detail);
					label.setHorizontalAlignment(SwingConstants.CENTER);
					textPanel.add(label);
				}
				targetPanel.add(textPanel);
			}

			JPanel buttonPanel = createButtonPanel();
			int shortcut = KeyEvent.VK_A + targetIndex * 9;
			for (ReRollSource source : extension.teamReRollSources(parameter, true)) {
				buttonPanel.add(createReRollButton(target, extension.label(source, game), source, shortcut++));
				buttonPanel.add(Box.createHorizontalGlue());
			}
			if (parameter.hasProperty(ReRollProperty.PRO)) {
				buttonPanel.add(createReRollButton(target, "Pro Re-Roll", ReRollSources.PRO, shortcut++));
				buttonPanel.add(Box.createHorizontalGlue());
			}
			if (parameter.getSingleUseReRollSource() != null) {
				ReRollSource source = parameter.getSingleUseReRollSource();
				buttonPanel.add(createReRollButton(target, source.getName(game), source, shortcut++));
				buttonPanel.add(Box.createHorizontalGlue());
			}
			if (parameter.isConsummateAvailable() && reRollingPlayer != null) {
				Skill skill = reRollingPlayer.getSkillWithProperty(NamedProperties.canRerollSingleDieOncePerPeriod);
				ReRollSource source = skill == null ? null : skill.getRerollSource(ReRolledActions.SINGLE_DIE);
				if (source != null) {
					buttonPanel.add(createReRollButton(target, source.getName(game), source, shortcut++));
					buttonPanel.add(Box.createHorizontalGlue());
				}
			}
			Skill skill = parameter.getReRollSkill();
			if (skill != null) {
				ReRollSource source = skill.getRerollSource(parameter.getReRolledAction());
				if (source != null) {
					buttonPanel.add(createReRollButton(target, skill.getName() + " Re-Roll", source, shortcut++));
					buttonPanel.add(Box.createHorizontalGlue());
				}
			}
			buttonPanel.add(createReRollButton(target, "No Re-Roll", null, shortcut));
			targetPanel.add(Box.createVerticalStrut(3));
			targetPanel.add(buttonPanel);
			targetPanel.add(Box.createVerticalStrut(3));
			addTargetPanel(detailPanel, targetPanel);
		}
		init(messages, detailPanel);
	}

	private JButton createReRollButton(String target, String label, ReRollSource source, int mnemonic) {
		JButton button = new JButton(dimensionProvider(), label);
		button.addActionListener(event -> handleUserInteraction(target, source));
		this.addKeyListener(new PressedKeyListener(mnemonic) {
			@Override
			protected void handleKey() {
				handleUserInteraction(target, source);
			}
		});
		button.setMnemonic(mnemonic);
		return button;
	}

	private void handleUserInteraction(String target, ReRollSource source) {
		reRollSource = source;
		selectedTarget = target;
		close();
	}

	@Override
	public DialogId getId() {
		return DialogId.RE_ROLL_FOR_TARGETS_PROPERTIES;
	}

	public ReRollSource getReRollSource() {
		return reRollSource;
	}

	public ReRolledAction getReRolledAction() {
		return dialogParameter.getReRolledAction();
	}

	public DialogReRollForTargetsPropertiesParameter getDialogParameter() {
		return dialogParameter;
	}
}
