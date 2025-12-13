package com.fumbbl.ffb.server.skillbehaviour.mixed;

import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.action.block.StepDauntless;
import com.fumbbl.ffb.skill.mixed.special.BlindRage;

@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
public class BlindRageBehaviour extends SkillBehaviour<BlindRage> {
	public BlindRageBehaviour() {
		super();
		registerModifier(new StepModifier<StepDauntless, StepDauntless.StepState>(1) {

			@Override
			public StepCommandStatus handleCommandHook(StepDauntless step, StepDauntless.StepState state,
			                                           ClientCommandUseSkill useSkillCommand) {
				step.setReRolledAction(ReRolledActions.DAUNTLESS);
				step.setReRollSource(useSkillCommand.isSkillUsed() ? ReRollSources.BLIND_RAGE : null);
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepDauntless step, StepDauntless.StepState state) {
				return false;
			}

		});
	}

}