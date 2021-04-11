package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogOpponentBlockSelectionParameter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.util.ArrayList;
import java.util.List;

public class DialogOpponentBlockSelection extends AbstractDialogMultiBlock {

	private final DialogOpponentBlockSelectionParameter dialogParameter;

	public DialogOpponentBlockSelection(FantasyFootballClient pClient, DialogOpponentBlockSelectionParameter parameter) {

		super(pClient, "Select Block Result", false);

		dialogParameter = parameter;

		StringBuilder mainMessage = new StringBuilder();

		mainMessage.append("<html>Select block result");
		if (dialogParameter.getTargetIds().size() > 1) {
			mainMessage.append("s");
		}
		mainMessage.append("?</html>");

		List<String> mainMessages = new ArrayList<>();
		mainMessages.add(mainMessage.toString());

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

			JPanel targetPanel = new BackgroundPanel(colorOwnChoice);
			targetPanel.setLayout(new BoxLayout(targetPanel, BoxLayout.Y_AXIS));
			targetPanel.setAlignmentX(CENTER_ALIGNMENT);
			mainMessagePanel.add(targetPanel);
			JPanel dicePanel = dicePanel(parameter.getBlockRolls().get(target), target, true, keyEvents.remove(0));
			mainMessagePanel.add(dicePanel);

			mainMessagePanel.add(nameLabel(target));
		}

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(mainMessagePanel);

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
