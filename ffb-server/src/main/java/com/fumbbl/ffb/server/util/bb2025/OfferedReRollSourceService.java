package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.HasReRollProperties;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.ReRollOptions;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSourceComposer;
import com.fumbbl.ffb.ReRollSourceComposition;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.dialog.DialogBlockRollPropertiesParameter;
import com.fumbbl.ffb.dialog.DialogReRollBlockForTargetsPropertiesParameter;
import com.fumbbl.ffb.dialog.DialogReRollForTargetsParameter;
import com.fumbbl.ffb.dialog.DialogReRollModifierChoiceParameter;
import com.fumbbl.ffb.dialog.DialogReRollPropertiesParameter;
import com.fumbbl.ffb.dialog.DialogReRollRegenerationMultipleParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Tells if a re-roll source sent by a client is offered by the dialog the game is currently waiting for.
 * <p>
 * Clients may send re-roll commands at any time and with any source, so a step must only act on sources its dialog
 * offered for the action (and target) in question. Dialogs only offer re-rolls that are available when they are
 * shown, so an offered source is also available.
 */
public class OfferedReRollSourceService {

	private final ReRollSourceComposer reRollSourceComposer = new ReRollSourceComposer();

	/**
	 * @param targetId the player the re-roll is used for if the dialog offers re-rolls per target, null otherwise
	 * @return whether the current dialog offers the source for the action, no source (no re-roll) only has to fit the
	 * action
	 */
	public boolean isOffered(Game game, ReRolledAction reRolledAction, ReRollSource reRollSource, String targetId) {
		IDialogParameter dialogParameter = game.getDialogParameter();
		if (dialogParameter instanceof DialogReRollPropertiesParameter) {
			DialogReRollPropertiesParameter parameter = (DialogReRollPropertiesParameter) dialogParameter;
			return parameter.getReRolledAction() == reRolledAction
				&& isOffered(game, parameter, skillNames(parameter.getReRollSkill()), reRollSource);
		}
		if (dialogParameter instanceof DialogReRollModifierChoiceParameter) {
			DialogReRollModifierChoiceParameter parameter = (DialogReRollModifierChoiceParameter) dialogParameter;
			return parameter.getReRolledAction() == reRolledAction
				&& isOffered(game, parameter, skillNames(parameter.getReRollSkill()), reRollSource);
		}
		if (dialogParameter instanceof DialogBlockRollPropertiesParameter) {
			DialogBlockRollPropertiesParameter parameter = (DialogBlockRollPropertiesParameter) dialogParameter;
			return reRolledAction == ReRolledActions.BLOCK
				&& isOffered(game, parameter, parameter.getRrActionToSource().values(), reRollSource);
		}
		if (dialogParameter instanceof DialogReRollBlockForTargetsPropertiesParameter) {
			return reRolledAction == ReRolledActions.BLOCK && (reRollSource == null
				|| ((DialogReRollBlockForTargetsPropertiesParameter) dialogParameter).getBlockRolls().stream()
				.filter(roll -> roll.getTargetId().equals(targetId))
				.anyMatch(roll -> isOffered(game, roll, roll.getRrActionToSource().values(), reRollSource)));
		}
		if (dialogParameter instanceof DialogReRollRegenerationMultipleParameter) {
			return reRolledAction == ReRolledActions.REGENERATION && (reRollSource == null
				|| isOfferedForRegeneration(game, (DialogReRollRegenerationMultipleParameter) dialogParameter, reRollSource,
				targetId));
		}
		if (dialogParameter instanceof DialogReRollForTargetsParameter) {
			return isOfferedForTargets(game, (DialogReRollForTargetsParameter) dialogParameter, reRolledAction, reRollSource,
				targetId);
		}
		return false;
	}

	/**
	 * Pro, mascot and team re-rolls (including their combinations) have to be offered by the properties, any other
	 * source has to be one of the other offered sources.
	 */
	private boolean isOffered(Game game, HasReRollProperties properties, Collection<String> otherSourceNames,
		ReRollSource reRollSource) {
		if (reRollSource == null) {
			return true;
		}
		ReRollSourceComposition composition = reRollSourceComposer.decompose(reRollSource);
		if (composition == null) {
			return otherSourceNames.contains(reRollSource.getName(game));
		}
		if (composition.isPro() && !properties.hasProperty(ReRollProperty.PRO)
			&& !otherSourceNames.contains(ReRollSources.PRO.getName(game))) {
			return false;
		}
		if (composition.isMascot() && !properties.hasProperty(ReRollProperty.MASCOT)) {
			return false;
		}
		ReRollSource teamReRollSource = composition.getTeamReRollSource();
		if (teamReRollSource == null) {
			return true;
		}
		return properties.hasProperty(ReRollProperty.TRR) && (teamReRollSource != ReRollSources.DRIVE_BONUS_RE_ROLL
			|| properties.hasProperty(ReRollProperty.DRIVE_BONUS));
	}

	private boolean isOfferedForRegeneration(Game game, DialogReRollRegenerationMultipleParameter parameter,
		ReRollSource reRollSource, String targetId) {
		int index = parameter.getPlayerIds().indexOf(targetId);
		List<ReRollOptions> reRollOptions = parameter.getReRollOptions();
		if (index < 0 || index >= reRollOptions.size()) {
			return false;
		}
		ReRollOptions options = reRollOptions.get(index);
		return isOffered(game, options, skillNames(options.getReRollSkill()), reRollSource);
	}

	private boolean isOfferedForTargets(Game game, DialogReRollForTargetsParameter parameter,
		ReRolledAction reRolledAction, ReRollSource reRollSource, String targetId) {
		if (parameter.getReRolledAction() != reRolledAction) {
			return false;
		}
		if (reRollSource == null) {
			return true;
		}
		if (targetId == null || !parameter.getReRollAvailableAgainst().contains(targetId)) {
			return false;
		}
		if (reRollSource == ReRollSources.TEAM_RE_ROLL) {
			return parameter.isTeamReRollAvailable();
		}
		if (reRollSource == ReRollSources.PRO) {
			return parameter.isProReRollAvailable();
		}
		if (reRollSource == parameter.getSingleUseReRollSource()) {
			return true;
		}
		Skill skill = reRollSource.getSkill(game);
		return skill != null && (skill.equals(parameter.getReRollSkill())
			|| (parameter.isConsummateAvailable() && skill.hasSkillProperty(NamedProperties.canRerollSingleDieOncePerPeriod)));
	}

	private Collection<String> skillNames(Skill skill) {
		return skill != null ? Collections.singletonList(skill.getName()) : Collections.emptyList();
	}
}
