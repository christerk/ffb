package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogOpponentBlockSelectionParameter;
import com.fumbbl.ffb.model.BlockRoll;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class DialogOpponentBlockSelection extends AbstractDialogMultiBlock {

	private final DialogOpponentBlockSelectionParameter dialogParameter;

	public DialogOpponentBlockSelection(FantasyFootballClient pClient, DialogOpponentBlockSelectionParameter parameter) {

		super(pClient, "Block Roll", false);

		dialogParameter = parameter;

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setAlignmentX(CENTER_ALIGNMENT);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

		for (BlockRoll blockRoll : parameter.getBlockRolls()) {

			String target = blockRoll.getTargetId();
			JPanel targetPanel = new BackgroundPanel(colorOpponentChoice);
			targetPanel.setLayout(new BoxLayout(targetPanel, BoxLayout.Y_AXIS));
			targetPanel.setAlignmentX(CENTER_ALIGNMENT);
			mainPanel.add(targetPanel);
			JPanel dicePanel = dicePanel(blockRoll, blockRoll.needsSelection(), keyEvents.remove(0));
			targetPanel.add(dicePanel);

			targetPanel.add(namePanel(target));
			mainPanel.add(Box.createVerticalStrut(5));
		}

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(mainPanel);

		pack();
		setLocationToCenter();

	}

	protected void close() {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(DialogOpponentBlockSelection.this);
		}
	}

	public DialogId getId() {
		return DialogId.OPPONENT_BLOCK_SELECTION;
	}

	public String getSelectedTarget() {
		return selectedTarget;
	}

	public Integer getSelectedIndex() {
		return selectedIndex;
	}

	public DialogOpponentBlockSelectionParameter getDialogParameter() {
		return dialogParameter;
	}

}
