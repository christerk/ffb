package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.bb2020.ttm.StepThrowTeamMate;
import com.fumbbl.ffb.skill.bb2020.special.TheBallista;

@RulesCollection(Rules.BB2020)
public class TheBallistaBehaviour extends AbstractPassBehaviour<TheBallista> {
	public TheBallistaBehaviour() {
		super();
		registerModifier(new StepModifier<StepThrowTeamMate, StepThrowTeamMate.StepState>(1) {

			@Override
			public StepCommandStatus handleCommandHook(StepThrowTeamMate step, StepThrowTeamMate.StepState state,
			                                           ClientCommandUseSkill useSkillCommand) {
				step.setReRolledAction(ReRolledActions.THROW_TEAM_MATE);
				step.setReRollSource(useSkillCommand.isSkillUsed() ? getReRollSource() : null);
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepThrowTeamMate step, StepThrowTeamMate.StepState state) {
				return false;
			}

		});
	}

	@Override
	protected ReRollSource getReRollSource() {
		return ReRollSources.THE_BALLISTA;
	}
}