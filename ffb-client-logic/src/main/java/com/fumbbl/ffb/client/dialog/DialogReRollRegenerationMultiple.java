package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.dialog.AbstractDialogMultiBlock.PressedKeyListener;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogReRollRegenerationMultipleParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import javax.swing.Box;
import javax.swing.JPanel;
import java.util.Collections;

public class DialogReRollRegenerationMultiple extends AbstractDialogForTargets {

	public DialogReRollRegenerationMultiple(FantasyFootballClient pClient,
		DialogReRollRegenerationMultipleParameter parameter) {

		super(pClient, "Re-roll Regeneration");

		StringBuilder mainMessage = new StringBuilder();
		mainMessage.append("<html>Do you want to re-roll one of these Regeneration rolls?<br/>Use <b>");
		if (parameter.getInducementType() != null) {
			mainMessage.append(parameter.getInducementType().getDescription());
		} else {
			mainMessage.append("a Team Re-Roll");
		}
		mainMessage.append("</b>?</html>");

		Game game = getClient().getGame();

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

	private void handleUserInteraction(String target) {
		selectedTarget = target;
		close();
	}

	public DialogId getId() {
		return DialogId.RE_ROLL_REGENERATION_MULTIPLE;
	}
}
