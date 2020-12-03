package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.BlockResult;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.SkillUse;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.action.block.StepJuggernaut;
import com.balancedbytes.games.ffb.server.step.action.block.StepJuggernaut.StepState;
import com.balancedbytes.games.ffb.server.step.action.block.UtilBlockSequence;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.skill.Juggernaut;
import com.balancedbytes.games.ffb.util.UtilCards;

public class JuggernautBehaviour extends SkillBehaviour<Juggernaut> {
	public JuggernautBehaviour() {
		super(Juggernaut.class);

		registerModifier(new StepModifier<StepJuggernaut, StepJuggernaut.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepJuggernaut step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				state.usingJuggernaut = useSkillCommand.isSkillUsed();
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepJuggernaut step, StepState state) {
				 Game game = step.getGameState().getGame();
				    ActingPlayer actingPlayer = game.getActingPlayer();
				    UtilServerDialog.hideDialog(step.getGameState());
				    if ((PlayerAction.BLITZ == actingPlayer.getPlayerAction()) && UtilCards.hasSkill(game, actingPlayer, skill) && !state.oldDefenderState.isRooted()) {
				      if (state.usingJuggernaut == null) {
				        UtilServerDialog.showDialog(step.getGameState(), new DialogSkillUseParameter(actingPlayer.getPlayer().getId(), skill, 0), false);
				      } else {
				        if (state.usingJuggernaut) {
				        	step.getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), skill, true, SkillUse.PUSH_BACK_OPPONENT));
				        	step.publishParameter(new StepParameter(StepParameterKey.BLOCK_RESULT, BlockResult.PUSHBACK));
				        	game.getFieldModel().setPlayerState(game.getDefender(), state.oldDefenderState);
				        	step.publishParameters(UtilBlockSequence.initPushback(step));
				        	step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnSuccess);
				        } else {
				        	step.getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), skill, false, null));
				        	step.getResult().setNextAction(StepAction.NEXT_STEP);
				        }
				      }
				    } else {
				    	step.getResult().setNextAction(StepAction.NEXT_STEP);
				    }
				return false;
			}
			
			
		});
	}
}