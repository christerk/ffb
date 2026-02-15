package com.fumbbl.ffb.server.skillbehaviour.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.mixed.ReportIndomitable;
import com.fumbbl.ffb.server.ActionStatus;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.action.block.StepDauntless;
import com.fumbbl.ffb.server.step.action.block.StepDauntless.StepState;
import com.fumbbl.ffb.skill.mixed.special.Indomitable;

@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
public class IndomitableBehaviour extends SkillBehaviour<Indomitable> {
	public IndomitableBehaviour() {
		super();

		registerModifier(new StepModifier<StepDauntless, StepState>(3) {

			@Override
			public StepCommandStatus handleCommandHook(StepDauntless step, StepState state,
			                                           ClientCommandUseSkill useSkillCommand) {
				if (useSkillCommand.getSkill().hasSkillProperty(NamedProperties.canDoubleStrengthAfterDauntless)) {
					state.status = useSkillCommand.isSkillUsed() ? ActionStatus.SKILL_CHOICE_YES : ActionStatus.SKILL_CHOICE_NO;
				}
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepDauntless step, StepState state) {
				boolean doNextStep = true;
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				if (state.status != null) {
					switch (state.status) {
						case SKILL_CHOICE_YES:
							actingPlayer.markSkillUsed(NamedProperties.canDoubleStrengthAfterDauntless);
							step.publishParameter(new StepParameter(StepParameterKey.DOUBLE_TARGET_STRENGTH, true));
							step.getResult().addReport(new ReportIndomitable(actingPlayer.getPlayerId(), game.getDefenderId()));
							break;
						case WAITING_FOR_SKILL_USE:
							doNextStep = false;
							break;
						default:
							break;
					}
				}
				if (doNextStep) {
					step.getResult().setNextAction(StepAction.NEXT_STEP);
				}
				return false;
			}
		});
	}
}