package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.GameMenuBar;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import java.awt.Container;
import java.util.Map;

/**
 * @author Kalimar
 */
public class DialogSkillUse extends DialogThreeWayChoice {

	private final DialogSkillUseParameter fDialogParameter;

	public DialogSkillUse(FantasyFootballClient pClient, DialogSkillUseParameter pDialogParameter, Skill modifyingSkill) {
		super(pClient, "Use a skill", createMessages(pDialogParameter), null,
			pDialogParameter.getSkill().getName(), 'S', modifyingSkill.getName(), 'M',
			"None", 'N', pDialogParameter.getMenuProperty(), pDialogParameter.getDefaultValueKey());
		fDialogParameter = pDialogParameter;
	}

	public DialogSkillUse(FantasyFootballClient pClient, DialogSkillUseParameter pDialogParameter) {
		super(pClient, "Use a skill", createMessages(pDialogParameter), null, pDialogParameter.getMenuProperty(), pDialogParameter.getDefaultValueKey());
		fDialogParameter = pDialogParameter;
	}

	public static DialogSkillUse create(FantasyFootballClient pClient, DialogSkillUseParameter pDialogParameter) {
		if (pDialogParameter.getModifyingSkill() == null) {
			return new DialogSkillUse(pClient, pDialogParameter);
		}
		return new DialogSkillUse(pClient, pDialogParameter, pDialogParameter.getModifyingSkill());
	}

	public DialogId getId() {
		return DialogId.SKILL_USE;
	}

	public Skill getSkill() {
		return (fDialogParameter != null) ? fDialogParameter.getSkill() : null;
	}

	private static String createDefaultQuestion(DialogSkillUseParameter pDialogParameter) {
		StringBuilder useSkillQuestion = new StringBuilder();
		String skillName = (pDialogParameter.getSkill() != null) ? pDialogParameter.getSkill().getName() : null;
		useSkillQuestion.append("Do you want to use the ").append(skillName);
		if (pDialogParameter.getModifyingSkill() != null) {
			useSkillQuestion.append(" or the ").append(pDialogParameter.getModifyingSkill().getName());
		}
		useSkillQuestion.append(" skill ?");
		return useSkillQuestion.toString();
	}

	private static String[] createMessages(DialogSkillUseParameter pDialogParameter) {
		String[] messages = new String[0];
		if ((pDialogParameter != null) && (pDialogParameter.getSkill() != null)) {
			Skill skill = pDialogParameter.getSkill();

			String[] customMessages = skill.getSkillUseDescription();
			if (customMessages != null) {
				messages = new String[customMessages.length + 1];
				messages[0] = createDefaultQuestion(pDialogParameter);
				System.arraycopy(customMessages, 0, messages, 1, customMessages.length);
			} else {
				if (pDialogParameter.getMinimumRoll() > 0) {
					messages = new String[2];
					messages[1] = createDefaultMinimumRoll(pDialogParameter);
				} else {
					messages = new String[1];
				}
				messages[0] = createDefaultQuestion(pDialogParameter);
			}
		}
		return messages;
	}

	public Skill getModifyingSkill() {
		return fDialogParameter != null ? fDialogParameter.getModifyingSkill() : null;
	}

	private static String createDefaultMinimumRoll(DialogSkillUseParameter pDialogParameter) {
		return "You will need a roll of " + pDialogParameter.getMinimumRoll() +
			"+ to succeed.";
	}

	@Override
	protected void addCustomPanel(Container contentPane, String menuProperty, String defaultValueKey) {

		if (!StringTool.isProvided(menuProperty)) {
			return;
		}

		GameMenuBar gameMenuBar = getClient().getUserInterface().getGameMenuBar();
		String name = gameMenuBar.menuName(menuProperty);
		Map<String, String> entries = gameMenuBar.menuEntries(menuProperty);

		String selectedValue = entries.get(getClient().getProperty(menuProperty));
		if (!StringTool.isProvided(selectedValue)) {
			selectedValue = entries.get(defaultValueKey);
		}

		JComboBox<String> box = new JComboBox<>(entries.values().toArray(new String[0]));
		box.setSelectedItem(selectedValue);
		box.addActionListener(event -> {
			String newValue = box.getItemAt(box.getSelectedIndex());
			entries.entrySet().stream().filter(entry -> entry.getValue().equalsIgnoreCase(newValue)).map(Map.Entry::getKey).findFirst().ifPresent(
				key -> {
					getClient().setProperty(menuProperty, key);
					getClient().saveUserSettings(true);
				}
			);
		});

		JPanel boxPanel = new JPanel();
		boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.X_AXIS));
		boxPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		boxPanel.add(new JLabel(name));
		boxPanel.add(Box.createHorizontalStrut(5));
		boxPanel.add(box);

		contentPane.add(new JSeparator());
		boxPanel.add(Box.createVerticalStrut(5));
		contentPane.add(new JSeparator());
		contentPane.add(boxPanel);

	}
}
