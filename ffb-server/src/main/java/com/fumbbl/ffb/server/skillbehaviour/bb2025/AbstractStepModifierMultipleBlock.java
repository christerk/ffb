package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.dialog.DialogReRollForTargetsPropertiesParameter;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.mechanic.RollMechanic;
import com.fumbbl.ffb.server.skillbehaviour.mixed.AbstractStepModifierMultipleBlockBase;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.bb2020.StepStateMultipleRolls;

import java.util.List;

@RulesCollection(Rules.BB2025)
public abstract class AbstractStepModifierMultipleBlock<T extends IStep, V extends StepStateMultipleRolls>
	extends AbstractStepModifierMultipleBlockBase<T, V> {

	@Override
	protected IDialogParameter createDialogParameter(GameState gameState, Player<?> player, V state, Skill skill) {
		RollMechanic rollMechanic = gameState.getGame().getMechanic(com.fumbbl.ffb.mechanics.Mechanic.Type.ROLL);
		List<ReRollProperty> reRollProperties = rollMechanic.findTeamReRollProperties(gameState, player);
		if (state.proReRollAvailable) {
			reRollProperties.add(ReRollProperty.PRO);
		}
		if (player.hasSkillProperty(NamedProperties.hasToRollToUseTeamReroll)) {
			reRollProperties.add(ReRollProperty.LONER);
		}
		return new DialogReRollForTargetsPropertiesParameter(player.getId(), state.blockTargets, reRolledAction(),
			state.minimumRolls, state.reRollAvailableAgainst, state.proReRollAvailable, state.teamReRollAvailable,
			skill, state.singleUseReRollSource, state.consummateAvailable, reRollProperties);
	}
}
