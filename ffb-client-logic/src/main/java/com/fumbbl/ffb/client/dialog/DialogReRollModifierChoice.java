package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogReRollModifierChoiceParameter;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.skill.Skill;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lets the coach pick a combination of optional roll modifiers or a re-roll.
 */
public class DialogReRollModifierChoice extends Dialog implements ActionListener, KeyListener {

	private static final int MAX_MODIFIER_BUTTONS = 9;

	private final DialogReRollModifierChoiceParameter dialogParameter;
	private final List<JButton> modifierButtons = new ArrayList<>();
	private final List<ModifierChoiceOption> modifierOptions = new ArrayList<>();
	private final Map<JButton, ReRollSource> teamReRollButtons = new LinkedHashMap<>();
	private final Map<Integer, ReRollSource> teamReRollKeys = new HashMap<>();
	private final JButton buttonProReRoll;
	private final JButton buttonNoReRoll;
	private JButton buttonSkillReRoll;
	private ReRollSource reRollSource;
	private boolean useSkill;
	private Skill usedSkill;
	private ModifierChoiceOption selectedOption;

	public DialogReRollModifierChoice(FantasyFootballClient pClient,
																	 DialogReRollModifierChoiceParameter pDialogParameter) {

		super(pClient, "Use a Skill or a Re-roll", false);

		dialogParameter = pDialogParameter;

		DialogExtensionMascot mascotExtension = new DialogExtensionMascot();
		for (ReRollSource teamReRollSource : mascotExtension.teamReRollSources(pDialogParameter, true)) {
			JButton button = mascotExtension.button(teamReRollSource, getClient().getGame(), dimensionProvider());
			button.addActionListener(this);
			button.addKeyListener(this);
			teamReRollButtons.put(button, teamReRollSource);
			teamReRollKeys.put(mascotExtension.mnemonic(teamReRollSource), teamReRollSource);
		}

		buttonProReRoll = new JButton(dimensionProvider(), "Pro Re-Roll");
		buttonProReRoll.addActionListener(this);
		buttonProReRoll.addKeyListener(this);
		buttonProReRoll.setMnemonic((int) 'P');
		buttonProReRoll.setAlignmentY(Box.TOP_ALIGNMENT);

		if (pDialogParameter.getReRollSkill() != null) {
			buttonSkillReRoll = new JButton(dimensionProvider(), pDialogParameter.getReRollSkill().getName());
			buttonSkillReRoll.addActionListener(this);
			buttonSkillReRoll.addKeyListener(this);
			buttonSkillReRoll.setMnemonic((int) 'S');
			buttonSkillReRoll.setAlignmentY(Box.TOP_ALIGNMENT);
		}

		buttonNoReRoll = new JButton(dimensionProvider(), "No Re-Roll");
		buttonNoReRoll.addActionListener(this);
		buttonNoReRoll.addKeyListener(this);
		buttonNoReRoll.setMnemonic((int) 'N');
		buttonNoReRoll.setAlignmentY(Box.TOP_ALIGNMENT);

		for (ModifierChoiceOption option : pDialogParameter.getModifierOptions()) {
			if (modifierOptions.size() >= MAX_MODIFIER_BUTTONS) {
				break;
			}
			JButton button = new JButton(dimensionProvider(), option.getLabel());
			button.addActionListener(this);
			button.addKeyListener(this);
			button.setMnemonic(KeyEvent.VK_1 + modifierOptions.size());
			button.setAlignmentY(Box.TOP_ALIGNMENT);
			modifierButtons.add(button);
			modifierOptions.add(option);
		}

		JPanel messagePanel = new JPanel();
		messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
		messagePanel.add(new JLabel(dimensionProvider(), "You rolled a " + pDialogParameter.getRoll()
			+ " and needed " + pDialogParameter.getMinimumRoll() + "+ to succeed."));

		if (!pDialogParameter.getModifierCombinations().isEmpty()) {
			messagePanel.add(Box.createVerticalStrut(5));
			messagePanel.add(new JLabel(dimensionProvider(), "Rolls needed with optional skills:"));
			for (ModifierChoiceOption combination : pDialogParameter.getModifierCombinations()) {
				messagePanel.add(new JLabel(dimensionProvider(),
					"\u2022 " + combination.getLabel() + ": " + combination.getMinimumRoll() + "+"));
			}
		}

		if (pDialogParameter.getMessages() != null) {
			for (String additionalMessage : pDialogParameter.getMessages()) {
				messagePanel.add(Box.createVerticalStrut(5));
				messagePanel.add(new JLabel(dimensionProvider(), additionalMessage));
			}
		}

		if (pDialogParameter.isFumble()) {
			messagePanel.add(Box.createVerticalStrut(5));
			messagePanel.add(new JLabel(dimensionProvider(), "Current roll is a FUMBLE."));
		}

		if (dialogParameter.hasProperty(ReRollProperty.LONER)) {
			messagePanel.add(Box.createVerticalStrut(5));
			messagePanel.add(new JLabel(dimensionProvider(), "Player is a LONER - the Re-Roll is not guaranteed to help."));
		}

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
		infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		BufferedImage icon = getClient().getUserInterface().getIconCache()
			.getIconByProperty(IIconProperty.GAME_DICE_SMALL, dimensionProvider());
		infoPanel.add(new JLabel(dimensionProvider(), new ImageIcon(icon)));
		infoPanel.add(Box.createHorizontalStrut(5));
		infoPanel.add(messagePanel);
		infoPanel.add(Box.createHorizontalGlue());

		JPanel modifierPanel = new JPanel();
		modifierPanel.setLayout(new BoxLayout(modifierPanel, BoxLayout.X_AXIS));
		modifierPanel.setAlignmentY(Box.TOP_ALIGNMENT);
		modifierPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		for (JButton modifierButton : modifierButtons) {
			modifierPanel.add(modifierButton);
			modifierPanel.add(Box.createHorizontalStrut(5));
		}

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setAlignmentY(Box.TOP_ALIGNMENT);
		for (JButton teamReRollButton : teamReRollButtons.keySet()) {
			buttonPanel.add(mascotExtension.wrapperPanel(teamReRollButton));
			buttonPanel.add(Box.createHorizontalStrut(5));
		}
		if (dialogParameter.hasProperty(ReRollProperty.PRO)) {
			buttonPanel.add(mascotExtension.wrapperPanel(buttonProReRoll));
			buttonPanel.add(Box.createHorizontalStrut(5));
		}
		if (buttonSkillReRoll != null) {
			buttonPanel.add(mascotExtension.wrapperPanel(buttonSkillReRoll));
			buttonPanel.add(Box.createHorizontalStrut(5));
		}
		buttonPanel.add(mascotExtension.wrapperPanel(buttonNoReRoll));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(infoPanel);
		if (!modifierButtons.isEmpty()) {
			getContentPane().add(modifierPanel);
		}
		getContentPane().add(buttonPanel);
		addMenuPanel(getContentPane(), pDialogParameter.getMenuProperty(), pDialogParameter.getDefaultValueKey());

		pack();
		setLocationToCenter();

	}

	public DialogId getId() {
		return DialogId.RE_ROLL_MODIFIER_CHOICE;
	}

	public void actionPerformed(ActionEvent pActionEvent) {
		int modifierIndex = modifierButtons.indexOf(pActionEvent.getSource());
		if (modifierIndex >= 0) {
			selectedOption = modifierOptions.get(modifierIndex);
		}
		if (teamReRollButtons.containsKey(pActionEvent.getSource())) {
			reRollSource = teamReRollButtons.get(pActionEvent.getSource());
		}
		if (pActionEvent.getSource() == buttonProReRoll) {
			if (dialogParameter.hasProperty(ReRollProperty.PRO)) {
				reRollSource = ReRollSources.PRO;
			}
		}
		if (pActionEvent.getSource() == buttonNoReRoll) {
			reRollSource = null;
		}
		if (pActionEvent.getSource() == buttonSkillReRoll) {
			useSkill = true;
			usedSkill = dialogParameter.getReRollSkill();
		}
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	public ReRollSource getReRollSource() {
		return reRollSource;
	}

	public ReRolledAction getReRolledAction() {
		return dialogParameter.getReRolledAction();
	}

	public DialogReRollModifierChoiceParameter getDialogParameter() {
		return dialogParameter;
	}

	public ModifierChoiceOption getSelectedOption() {
		return selectedOption;
	}

	public boolean isUseModifiers() {
		return selectedOption != null;
	}

	public Skill getUsedSkill() {
		return usedSkill;
	}

	public boolean isUseSkill() {
		return useSkill;
	}

	public void keyPressed(KeyEvent pKeyEvent) {
	}

	public void keyReleased(KeyEvent pKeyEvent) {
		boolean keyHandled = true;
		int modifierIndex = pKeyEvent.getKeyCode() - KeyEvent.VK_1;
		if (modifierIndex >= 0 && modifierIndex < modifierOptions.size()) {
			selectedOption = modifierOptions.get(modifierIndex);
			if (getCloseListener() != null) {
				getCloseListener().dialogClosed(this);
			}
			return;
		}
		if (teamReRollKeys.containsKey(pKeyEvent.getKeyCode())) {
			reRollSource = teamReRollKeys.get(pKeyEvent.getKeyCode());
		} else {
			switch (pKeyEvent.getKeyCode()) {
				case KeyEvent.VK_P:
					if (dialogParameter.hasProperty(ReRollProperty.PRO)) {
						reRollSource = ReRollSources.PRO;
					} else {
						keyHandled = false;
					}
					break;
				case KeyEvent.VK_S:
					if (dialogParameter.getReRollSkill() != null) {
						useSkill = true;
						usedSkill = dialogParameter.getReRollSkill();
					} else {
						keyHandled = false;
					}
					break;
				case KeyEvent.VK_N:
					break;
				default:
					keyHandled = false;
					break;
			}
		}
		if (keyHandled) {
			if (getCloseListener() != null) {
				getCloseListener().dialogClosed(this);
			}
		}
	}

	public void keyTyped(KeyEvent pKeyEvent) {
	}
}
