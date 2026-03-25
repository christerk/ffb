package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ReRollOptions;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.dialog.AbstractDialogMultiBlock.PressedKeyListener;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogReRollRegenerationMultipleParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DialogReRollRegenerationMultiple extends AbstractDialogForTargets {

	private ReRollSource reRollSource;

	public DialogReRollRegenerationMultiple(FantasyFootballClient pClient,
		DialogReRollRegenerationMultipleParameter parameter) {

		super(pClient, "Re-roll Regeneration");

		Game game = getClient().getGame();
		List<ReRollOptions> reRollOptionsList = parameter.getReRollOptions();

		if (!reRollOptionsList.isEmpty()) {
			buildWithReRollOptions(parameter, game, reRollOptionsList);
		} else {
			buildLegacy(parameter, game);
		}
	}

	private void buildLegacy(DialogReRollRegenerationMultipleParameter parameter, Game game) {
		StringBuilder mainMessage = new StringBuilder();
		mainMessage.append("<html>Do you want to re-roll one of these Regeneration rolls?<br/>Use <b>");
		if (parameter.getInducementType() != null) {
			mainMessage.append(parameter.getInducementType().getDescription());
		} else {
			mainMessage.append("a Team Re-Roll");
		}
		mainMessage.append("</b>?</html>");

		JPanel detailPanel = createDetailPanel();
		for (int index = 0; index < parameter.getPlayerIds().size(); index++) {

			String target = parameter.getPlayerIds().get(index);
			Player<?> player = game.getPlayerById(target);

			JPanel buttonPanel = createButtonPanel();
			buttonPanel.add(createButton(target, player.getName(), (char) index));
			buttonPanel.add(Box.createHorizontalGlue());

			JPanel targetPanel = createTargetPanel(buttonPanel);
			addTargetPanel(detailPanel, targetPanel);
		}

		init(Collections.singletonList(mainMessage.toString()), detailPanel);
	}

	private void buildWithReRollOptions(DialogReRollRegenerationMultipleParameter parameter, Game game,
		List<ReRollOptions> reRollOptionsList) {

		List<String> mainMessages = new ArrayList<>();
		mainMessages.add("<html>Do you want to re-roll one of these Regeneration rolls?</html>");

		JPanel detailPanel = createDetailPanel();
		for (int index = 0; index < parameter.getPlayerIds().size(); index++) {

			String target = parameter.getPlayerIds().get(index);
			ReRollOptions options = reRollOptionsList.get(index);

			if (!options.canActuallyReRoll()) {
				continue;
			}

			Player<?> player = game.getPlayerById(target);

			JPanel targetPanel = new JPanel();
			targetPanel.setLayout(new BoxLayout(targetPanel, BoxLayout.Y_AXIS));
			targetPanel.setAlignmentX(CENTER_ALIGNMENT);

			JPanel textPanel = new JPanel();
			textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
			textPanel.setAlignmentX(CENTER_ALIGNMENT);
			textPanel.setBackground(HIGHLIGHT);

			JLabel nameLabel = new JLabel(dimensionProvider(), "<html>" + player.getName() + "</html>");
			nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
			textPanel.add(nameLabel);

			if (options.getProperties().contains(ReRollProperty.LONER)) {
				JLabel lonerLabel = new JLabel(dimensionProvider(),
					"<html>Player is a LONER - the Re-Roll is not guaranteed to help.</html>");
				lonerLabel.setHorizontalAlignment(SwingConstants.CENTER);
				textPanel.add(lonerLabel);
			}

			targetPanel.add(textPanel);

			JPanel buttonPanel = createButtonPanel();

			if (options.getProperties().contains(ReRollProperty.TRR)) {
				buttonPanel.add(createReRollButton(target, "Team Re-Roll", ReRollSources.TEAM_RE_ROLL,
					index == 0 ? 'T' : 'e'));
				buttonPanel.add(Box.createHorizontalGlue());
			}
			if (options.getProperties().contains(ReRollProperty.MASCOT)) {
				buttonPanel.add(createReRollButton(target, ReRollSources.MASCOT.getName(game), ReRollSources.MASCOT,
					index == 0 ? 'M' : 'a'));
				buttonPanel.add(Box.createHorizontalGlue());
			}
			if (options.getProperties().contains(ReRollProperty.PRO)) {
				buttonPanel.add(createReRollButton(target, "Pro Re-Roll", ReRollSources.PRO,
					index == 0 ? 'P' : 'o'));
				buttonPanel.add(Box.createHorizontalGlue());
			}
			if (options.getReRollSkill() != null) {
				Skill skill = options.getReRollSkill();
				ReRollSource source = skill.getRerollSource(ReRolledActions.SINGLE_DIE);
				if (source != null) {
					buttonPanel.add(createReRollButton(target, skill.getName(), source,
						index == 0 ? 'S' : 'k'));
					buttonPanel.add(Box.createHorizontalGlue());
				}
			}

			targetPanel.add(Box.createVerticalStrut(3));
			targetPanel.add(buttonPanel);
			targetPanel.add(Box.createVerticalStrut(3));
			addTargetPanel(detailPanel, targetPanel);
		}

		init(mainMessages, detailPanel);
	}

	private JButton createButton(String target, String buttonName, char mnemonic) {
		JButton button = new JButton(dimensionProvider(), buttonName);
		button.addActionListener(e -> handleUserInteraction(target));
		this.addKeyListener(new PressedKeyListener(mnemonic) {
			@Override
			protected void handleKey() {
				handleUserInteraction(target);
			}
		});
		button.setMnemonic((int) mnemonic);
		return button;
	}

	private JButton createReRollButton(String target, String buttonName, ReRollSource reRollSource, char mnemonic) {
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

	private void handleUserInteraction(String target) {
		selectedTarget = target;
		close();
	}

	private void handleUserInteraction(String target, ReRollSource reRollSource) {
		this.reRollSource = reRollSource;
		selectedTarget = target;
		close();
	}

	public DialogId getId() {
		return DialogId.RE_ROLL_REGENERATION_MULTIPLE;
	}

	public ReRollSource getReRollSource() {
		return reRollSource;
	}
}
