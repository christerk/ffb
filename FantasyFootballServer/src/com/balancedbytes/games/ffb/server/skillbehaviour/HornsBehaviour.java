package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SkillUse;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.action.block.StepHorns;
import com.balancedbytes.games.ffb.server.step.action.block.StepHorns.StepState;
import com.balancedbytes.games.ffb.skill.Horns;
import com.balancedbytes.games.ffb.util.UtilCards;

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
				state.usingHorns = (UtilCards.hasSkill(game, actingPlayer, skill)
						&& (PlayerAction.BLITZ == actingPlayer.getPlayerAction()));
				if (state.usingHorns) {
					actingPlayer.setStrength(actingPlayer.getStrength() + 1);
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