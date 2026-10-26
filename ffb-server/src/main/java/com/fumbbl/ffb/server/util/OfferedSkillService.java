package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.dialog.DialogReRollModifierChoiceParameter;
import com.fumbbl.ffb.dialog.DialogReRollParameter;
import com.fumbbl.ffb.dialog.DialogReRollPropertiesParameter;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Tells if a skill sent by a client was actually offered by the dialog the game is currently waiting for.
 * <p>
 * Clients may send skill commands at any time, so a step must only act on skills it actually asked about.
 */
public class OfferedSkillService {

	/**
	 * @return true if the currently shown dialog offers the skill to be used, either as the skill in question,
	 * as a re-roll skill or as a skill modifying the roll
	 */
	public boolean isOffered(Game game, Skill skill) {
		if (skill == null) {
			return false;
		}
		IDialogParameter dialogParameter = game.getDialogParameter();
		if (dialogParameter instanceof DialogSkillUseParameter) {
			DialogSkillUseParameter parameter = (DialogSkillUseParameter) dialogParameter;
			return skill.equals(parameter.getSkill()) || skill.equals(parameter.getModifyingSkill());
		}
		if (dialogParameter instanceof DialogReRollParameter) {
			DialogReRollParameter parameter = (DialogReRollParameter) dialogParameter;
			return skill.equals(parameter.getReRollSkill()) || skill.equals(parameter.getModifyingSkill());
		}
		if (dialogParameter instanceof DialogReRollPropertiesParameter) {
			DialogReRollPropertiesParameter parameter = (DialogReRollPropertiesParameter) dialogParameter;
			return skill.equals(parameter.getReRollSkill()) || skill.equals(parameter.getModifyingSkill());
		}
		if (dialogParameter instanceof DialogReRollModifierChoiceParameter) {
			return skill.equals(((DialogReRollModifierChoiceParameter) dialogParameter).getReRollSkill());
		}
		return false;
	}

	/**
	 * @return the id of the player the currently shown dialog is addressed to, if that dialog offers the skill to be
	 * used, null otherwise
	 */
	public String offeringPlayerId(Game game, Skill skill) {
		if (!isOffered(game, skill)) {
			return null;
		}
		IDialogParameter dialogParameter = game.getDialogParameter();
		if (dialogParameter instanceof DialogSkillUseParameter) {
			return ((DialogSkillUseParameter) dialogParameter).getPlayerId();
		}
		if (dialogParameter instanceof DialogReRollParameter) {
			return ((DialogReRollParameter) dialogParameter).getPlayerId();
		}
		if (dialogParameter instanceof DialogReRollPropertiesParameter) {
			return ((DialogReRollPropertiesParameter) dialogParameter).getPlayerId();
		}
		return ((DialogReRollModifierChoiceParameter) dialogParameter).getPlayerId();
	}

	/**
	 * @return true if the currently shown dialog offers the given combination of optional modifier skills as one of
	 * its options for the given player, action and roll. A skill listed more than once counts only once.
	 */
	public boolean isOfferedModifierOption(Game game, String playerId, ReRolledAction reRolledAction, int roll,
	                                       Collection<Skill> skills) {
		IDialogParameter dialogParameter = game.getDialogParameter();
		if (!(dialogParameter instanceof DialogReRollModifierChoiceParameter) || playerId == null || skills == null
			|| skills.isEmpty() || skills.contains(null)) {
			return false;
		}
		DialogReRollModifierChoiceParameter dialog = (DialogReRollModifierChoiceParameter) dialogParameter;
		Set<Skill> skillSet = new HashSet<>(skills);
		if (dialog.getReRolledAction() != reRolledAction || dialog.getRoll() != roll
			|| !playerId.equals(dialog.getPlayerId())) {
			return false;
		}
		return dialog.getModifierOptions().stream().anyMatch(option -> new HashSet<>(option.getSkills()).equals(skillSet));
	}

	/**
	 * @return true if the player has the skill and has not used it yet, for the acting player skills used during
	 * the current activation are taken into account as well
	 */
	public boolean hasUnused(Game game, Player<?> player, Skill skill) {
		if (player == null || skill == null || !player.getSkillsIncludingTemporaryOnes().contains(skill)) {
			return false;
		}
		ActingPlayer actingPlayer = game.getActingPlayer();
		return player == actingPlayer.getPlayer() ? !actingPlayer.isSkillUsed(skill) : !player.isUsed(skill);
	}
}
