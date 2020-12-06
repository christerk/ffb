package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.model.Skill;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class DialogSkillUse extends DialogYesOrNoQuestion {

	private DialogSkillUseParameter fDialogParameter;

	public DialogSkillUse(FantasyFootballClient pClient, DialogSkillUseParameter pDialogParameter) {
		super(pClient, "Use a skill", createMessages(pDialogParameter), null);
		fDialogParameter = pDialogParameter;
	}

	public DialogId getId() {
		return DialogId.SKILL_USE;
	}

	public Skill getSkill() {
		return (fDialogParameter != null) ? fDialogParameter.getSkill() : null;
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

	private static String createDefaultQuestion(DialogSkillUseParameter pDialogParameter) {
		StringBuilder useSkillQuestion = new StringBuilder();
		String skillName = (pDialogParameter.getSkill() != null) ? pDialogParameter.getSkill().getName() : null;
		useSkillQuestion.append("Do you want to use the ").append(skillName).append(" skill ?");
		return useSkillQuestion.toString();
	}

	private static String createDefaultMinimumRoll(DialogSkillUseParameter pDialogParameter) {
		StringBuilder minimumRollMessage = new StringBuilder();
		minimumRollMessage.append("You will need a roll of ").append(pDialogParameter.getMinimumRoll())
				.append("+ to succeed.");
		return minimumRollMessage.toString();
	}

}
