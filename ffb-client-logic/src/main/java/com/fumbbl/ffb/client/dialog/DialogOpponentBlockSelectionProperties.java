package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogOpponentBlockSelectionPropertiesParameter;
import com.fumbbl.ffb.model.BlockPropertiesRoll;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class DialogOpponentBlockSelectionProperties extends AbstractDialogMultiBlockProperties {

	private final DialogOpponentBlockSelectionPropertiesParameter dialogParameter;

	public DialogOpponentBlockSelectionProperties(FantasyFootballClient pClient, DialogOpponentBlockSelectionPropertiesParameter parameter) {

		super(pClient, "Block Roll", false);

		dialogParameter = parameter;

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setAlignmentX(CENTER_ALIGNMENT);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

		for (BlockPropertiesRoll blockRoll : parameter.getBlockRolls()) {

			String target = blockRoll.getTargetId();
			JPanel targetPanel = new BackgroundPanel(colorOpponentChoice);
			targetPanel.setLayout(new BoxLayout(targetPanel, BoxLayout.Y_AXIS));
			targetPanel.setAlignmentX(CENTER_ALIGNMENT);
			mainPanel.add(targetPanel);
			JPanel dicePanel = dicePanel(blockRoll, blockRoll.needsSelection(), blockDieMnemonics.remove(0), false);
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
			getCloseListener().dialogClosed(DialogOpponentBlockSelectionProperties.this);
		}
	}

	public DialogId getId() {
		return DialogId.OPPONENT_BLOCK_SELECTION_PROPERTIES;
	}

	public String getSelectedTarget() {
		return selectedTarget;
	}

	public Integer getSelectedIndex() {
		return selectedIndex;
	}

	public DialogOpponentBlockSelectionPropertiesParameter getDialogParameter() {
		return dialogParameter;
	}

}
