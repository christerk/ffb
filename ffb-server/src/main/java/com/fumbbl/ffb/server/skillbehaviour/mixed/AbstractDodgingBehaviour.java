package com.fumbbl.ffb.server.skillbehaviour.mixed;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.mixed.StepBlockDodge;
import com.fumbbl.ffb.server.step.mixed.StepBlockDodge.StepState;
import com.fumbbl.ffb.server.util.UtilServerDialog;

public abstract class AbstractDodgingBehaviour<T extends Skill> extends SkillBehaviour<T> {
	public AbstractDodgingBehaviour(int priority, boolean requireUnusedSkill) {
		super();

		registerModifier(new StepModifier<StepBlockDodge, StepState>(priority) {

			@Override
			public StepCommandStatus handleCommandHook(StepBlockDodge step, StepState state,
																								 ClientCommandUseSkill useSkillCommand) {
				state.usingDodge = useSkillCommand.isSkillUsed();
				state.askForSkill = false;
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepBlockDodge step, StepState state) {
				Game game = step.getGameState().getGame();

				if (game.getDefender() == null || !game.getDefender().has(skill)) {
					return false;
				}

				if (requireUnusedSkill && game.getDefender().isUsed(skill)) {
					return false;
				}

				if (state.usingDodge == null) {
					state.usingDodge = state.oldDefenderState.hasTacklezones();
				}

				if (state.askForSkill && state.oldDefenderState.hasTacklezones()) {
					UtilServerDialog.showDialog(step.getGameState(), new DialogSkillUseParameter(game.getDefenderId(), skill, 0),
						true);
					return true;
				} else {
					step.getResult()
						.addReport(new ReportSkillUse(game.getDefenderId(), skill, state.usingDodge,
							state.oldDefenderState.hasTacklezones() ? SkillUse.AVOID_FALLING : SkillUse.NO_TACKLEZONE));
				}

				return false;
			}

		});
	}
}
