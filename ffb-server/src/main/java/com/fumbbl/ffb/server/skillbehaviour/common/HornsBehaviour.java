package com.fumbbl.ffb.server.skillbehaviour.common;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.action.block.StepHorns;
import com.fumbbl.ffb.server.step.action.block.StepHorns.StepState;
import com.fumbbl.ffb.skill.common.Horns;
import com.fumbbl.ffb.util.UtilCards;

@RulesCollection(Rules.COMMON)
public class HornsBehaviour extends SkillBehaviour<Horns> {
	public HornsBehaviour() {
		super();

		registerModifier(new StepModifier<StepHorns, StepHorns.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepHorns step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepHorns step, StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				state.usingHorns = (UtilCards.hasSkill(actingPlayer, skill)
						&& (PlayerAction.BLITZ == actingPlayer.getPlayerAction()));
				if (state.usingHorns) {
					// not adding the strength here but in ServerUtilBlock#getAttackerStrength which is also called for dice decorations
					actingPlayer.markSkillUsed(skill);
					step.getResult()
							.addReport(new ReportSkillUse(actingPlayer.getPlayerId(), skill, true, SkillUse.INCREASE_STRENGTH_BY_1));
				}
				step.getResult().setNextAction(StepAction.NEXT_STEP);
				return false;
			}

		});
	}
}