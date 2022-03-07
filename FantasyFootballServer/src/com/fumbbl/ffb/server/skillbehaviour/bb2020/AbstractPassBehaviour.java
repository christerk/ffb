package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.bb2020.pass.StepPass;
import com.fumbbl.ffb.server.step.bb2020.pass.state.PassState;

public abstract class AbstractPassBehaviour<T extends Skill> extends SkillBehaviour<T> {
	public AbstractPassBehaviour() {
		super();

		registerModifier(new StepModifier<StepPass, PassState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepPass step, PassState state,
			                                           ClientCommandUseSkill useSkillCommand) {
				step.setReRolledAction(ReRolledActions.PASS);
				step.setReRollSource(useSkillCommand.isSkillUsed() ? getReRollSource() : null);
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepPass step, PassState state) {
				return false;
			}

		});
	}

	protected abstract ReRollSource getReRollSource();
}