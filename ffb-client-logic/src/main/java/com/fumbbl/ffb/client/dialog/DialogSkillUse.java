package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.StringTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kalimar
 */
public class DialogSkillUse extends DialogThreeWayChoice {

	private final DialogSkillUseParameter fDialogParameter;

	public DialogSkillUse(FantasyFootballClient pClient, DialogSkillUseParameter pDialogParameter, String secondChoice, char mnemonic) {
		super(pClient, "Use a skill", createMessages(pDialogParameter, pClient), null,
			pDialogParameter.getSkill().getName(), 'S', secondChoice, mnemonic,
			pDialogParameter.getModifyingSkill() == null ? "No" : "None", 'N',
			pDialogParameter.getMenuProperty(), pDialogParameter.getDefaultValueKey());
		fDialogParameter = pDialogParameter;
	}

	public DialogSkillUse(FantasyFootballClient pClient, DialogSkillUseParameter pDialogParameter) {
		super(pClient, "Use a skill", createMessages(pDialogParameter, pClient), null, pDialogParameter.getMenuProperty(),
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

	private static String createDefaultQuestion(DialogSkillUseParameter pDialogParameter, FantasyFootballClient client) {
		StringBuilder useSkillQuestion = new StringBuilder();
		String skillName = (pDialogParameter.getSkill() != null) ? pDialogParameter.getSkill().getName() : null;
		useSkillQuestion.append("Do you want to use the ").append(skillName);
		if (pDialogParameter.getModifyingSkill() != null) {
			useSkillQuestion.append(" or the ").append(pDialogParameter.getModifyingSkill().getName());
		}
		useSkillQuestion.append(" skill");
		if (pDialogParameter.getSkillUse() != null && StringTool.isProvided(pDialogParameter.getPlayerId())) {
			Player<?> player = client.getGame().getPlayerById(pDialogParameter.getPlayerId());
			useSkillQuestion.append(" ").append(pDialogParameter.getSkillUse().getDescription(player));
		}
		useSkillQuestion.append("?");
		return useSkillQuestion.toString();
	}

	private static String[] createMessages(DialogSkillUseParameter pDialogParameter, FantasyFootballClient client) {
		List<String> messages = new ArrayList<>();
		if ((pDialogParameter != null) && (pDialogParameter.getSkill() != null)) {
			Skill skill = pDialogParameter.getSkill();

			messages.add(createDefaultQuestion(pDialogParameter, client));
			String[] customMessages = skill.getSkillUseDescription();
			if (customMessages != null) {
				messages.addAll(Arrays.asList(customMessages));
			} else if (pDialogParameter.getMinimumRoll() > 0) {
				messages.add(createDefaultMinimumRoll(pDialogParameter));
			}
			messages.addAll(pDialogParameter.getMessages());
		}
		return messages.toArray(new String[0]);
	}

	public Skill getModifyingSkill() {
		return fDialogParameter != null ? fDialogParameter.getModifyingSkill() : null;
	}

	private static String createDefaultMinimumRoll(DialogSkillUseParameter pDialogParameter) {
		return "You will need a roll of " + pDialogParameter.getMinimumRoll() +
			"+ to succeed.";
	}

}
