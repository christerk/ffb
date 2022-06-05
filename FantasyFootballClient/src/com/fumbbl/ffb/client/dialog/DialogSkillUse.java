package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * @author Kalimar
 */
public class DialogSkillUse extends DialogThreeWayChoice {

	private final DialogSkillUseParameter fDialogParameter;

	public DialogSkillUse(FantasyFootballClient pClient, DialogSkillUseParameter pDialogParameter, String secondChoice, char mnemonic) {
		super(pClient, "Use a skill", createMessages(pDialogParameter), null,
			pDialogParameter.getSkill().getName(), 'S', secondChoice, mnemonic,
			pDialogParameter.getModifyingSkill() == null ? "No" : "None", 'N',
			pDialogParameter.getMenuProperty(), pDialogParameter.getDefaultValueKey());
		fDialogParameter = pDialogParameter;
	}

	public DialogSkillUse(FantasyFootballClient pClient, DialogSkillUseParameter pDialogParameter) {
		super(pClient, "Use a skill", createMessages(pDialogParameter), null, pDialogParameter.getMenuProperty(),
			pDialogParameter.getDefaultValueKey());
		fDialogParameter = pDialogParameter;
	}

	public static DialogSkillUse create(FantasyFootballClient pClient, DialogSkillUseParameter pDialogParameter) {
		if (pDialogParameter.getModifyingSkill() == null) {
			if (pDialogParameter.isShowNeverUse()) {
				return new DialogSkillUse(pClient, pDialogParameter, "Not for this action", 'a');
			}

			return new DialogSkillUse(pClient, pDialogParameter);
		}
		return new DialogSkillUse(pClient, pDialogParameter, pDialogParameter.getModifyingSkill().getName(), 'M');
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

}
