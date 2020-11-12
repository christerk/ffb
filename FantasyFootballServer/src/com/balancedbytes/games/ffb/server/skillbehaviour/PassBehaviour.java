package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.action.pass.StepPass;
import com.balancedbytes.games.ffb.server.step.action.pass.StepPass.StepState;
import com.balancedbytes.games.ffb.skill.Pass;

public class PassBehaviour extends SkillBehaviour<Pass> {
	public PassBehaviour() {
		super();

		registerModifier(new StepModifier<StepPass, StepPass.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepPass step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				step.setReRolledAction(ReRolledAction.PASS);
	            step.setReRollSource(useSkillCommand.isSkillUsed() ? ReRollSource.PASS : null);
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepPass step, StepState state) {
				// TODO Auto-generated method stub
				return false;
			}
			
		});
	}
}