package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.bb2025.ttm.StepThrowTeamMate;
import com.fumbbl.ffb.server.step.bb2025.ttm.StepThrowTeamMate.StepState;
import com.fumbbl.ffb.skill.bb2025.Bullseye;

@RulesCollection(Rules.BB2025)
public class BullseyeBehaviour extends SkillBehaviour<Bullseye> {

	public BullseyeBehaviour() {
		registerModifier(new StepModifier<StepThrowTeamMate, StepState>(2) {
			@Override
			public StepCommandStatus handleCommandHook(StepThrowTeamMate step, StepState state,	ClientCommandUseSkill useSkillCommand) {
				state.usingBullseye = useSkillCommand.isSkillUsed();
				if (state.usingBullseye) {
					step.getGameState().getGame().getActingPlayer().markSkillUsed(useSkillCommand.getSkill());
				}
				step.getResult().addReport(new ReportSkillUse(useSkillCommand.getPlayerId(), useSkillCommand.getSkill(),
          state.usingBullseye, SkillUse.BULLSEYE));
				step.publishParameter(StepParameter.from(StepParameterKey.USING_BULLSEYE, state.usingBullseye));
				step.publishParameter(StepParameter.from(StepParameterKey.PASS_RESULT, state.passResult));
				step.getResult().setNextAction(StepAction.NEXT_STEP);
				return StepCommandStatus.SKIP_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepThrowTeamMate step, StepState state) {
				return false;
			}
		});
	}
}
