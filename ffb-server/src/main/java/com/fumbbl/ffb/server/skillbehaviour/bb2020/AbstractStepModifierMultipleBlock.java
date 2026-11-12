package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.dialog.DialogReRollForTargetsParameter;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.skillbehaviour.mixed.AbstractStepModifierMultipleBlockBase;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.bb2020.StepStateMultipleRolls;

@RulesCollection(Rules.BB2020)
public abstract class AbstractStepModifierMultipleBlock<T extends IStep, V extends StepStateMultipleRolls>
	extends AbstractStepModifierMultipleBlockBase<T, V> {

	@Override
	protected IDialogParameter createDialogParameter(GameState gameState, Player<?> player, V state, Skill skill) {
		return new DialogReRollForTargetsParameter(player.getId(), state.blockTargets, reRolledAction(),
			state.minimumRolls, state.reRollAvailableAgainst, state.proReRollAvailable, state.teamReRollAvailable,
			skill, state.singleUseReRollSource, state.consummateAvailable);
	}
}
