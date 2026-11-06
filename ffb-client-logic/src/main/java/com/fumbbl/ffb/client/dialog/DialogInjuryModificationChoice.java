package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogInjuryModificationChoiceParameter;
import com.fumbbl.ffb.model.InjuryModificationOption;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Lets the coach pick one of the injury modification options (skills and their result) or none.
 */
public class DialogInjuryModificationChoice extends Dialog implements ActionListener, KeyListener {

	private static final int MAX_OPTION_KEYS = 9;

	private final DialogInjuryModificationChoiceParameter dialogParameter;
	private final List<JButton> optionButtons = new ArrayList<>();
	private final List<InjuryModificationOption> options = new ArrayList<>();
	private final JButton buttonNone;
	private InjuryModificationOption selectedOption;

	public DialogInjuryModificationChoice(FantasyFootballClient client,
		DialogInjuryModificationChoiceParameter dialogParameter) {

		super(client, "Modify Injury", false);
		this.dialogParameter = dialogParameter;

		JPanel optionPanel = new JPanel();
		optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));
		optionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JLabel messageLabel = new JLabel(dimensionProvider(), dialogParameter.getMessage());
		messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		optionPanel.add(messageLabel);
		optionPanel.add(Box.createVerticalStrut(5));

		for (InjuryModificationOption option : dialogParameter.getOptions()) {
			String text = option.getLabel();
			if (option.getDescription() != null && !option.getDescription().isEmpty()) {
				text += ": " + option.getDescription();
			}
			if (options.size() < MAX_OPTION_KEYS) {
				text = (options.size() + 1) + ". " + text;
			}
			JButton button = new JButton(dimensionProvider(), text);
			button.addActionListener(this);
			button.addKeyListener(this);
			if (options.size() < MAX_OPTION_KEYS) {
				button.setMnemonic(KeyEvent.VK_1 + options.size());
			}
			button.setAlignmentX(Component.LEFT_ALIGNMENT);
			optionPanel.add(button);
			optionPanel.add(Box.createVerticalStrut(3));
			optionButtons.add(button);
			options.add(option);
		}

		buttonNone = new JButton(dimensionProvider(), "None");
		buttonNone.addActionListener(this);
		buttonNone.addKeyListener(this);
		buttonNone.setMnemonic((int) 'N');
		buttonNone.setAlignmentX(Component.LEFT_ALIGNMENT);
		optionPanel.add(Box.createVerticalStrut(2));
		optionPanel.add(buttonNone);

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(optionPanel);

		pack();
		setLocationToCenter();
	}

	public DialogId getId() {
		return DialogId.INJURY_MODIFICATION_CHOICE;
	}

	public DialogInjuryModificationChoiceParameter getDialogParameter() {
		return dialogParameter;
	}

	/**
	 * @return the selected option or null if the coach declined
	 */
	public InjuryModificationOption getSelectedOption() {
		return selectedOption;
	}

	@SuppressWarnings("SuspiciousMethodCalls")
	public void actionPerformed(ActionEvent actionEvent) {
		int index = optionButtons.indexOf(actionEvent.getSource());
		selectedOption = index >= 0 ? options.get(index) : null;
		close();
	}

	public void keyPressed(KeyEvent keyEvent) {
	}

	public void keyReleased(KeyEvent keyEvent) {
		int index = keyEvent.getKeyCode() - KeyEvent.VK_1;
		if (index >= 0 && index < Math.min(options.size(), MAX_OPTION_KEYS)) {
			selectedOption = options.get(index);
			close();
		} else if (keyEvent.getKeyCode() == KeyEvent.VK_N) {
			selectedOption = null;
			close();
		}
	}

	public void keyTyped(KeyEvent keyEvent) {
	}

	private void close() {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}
}
