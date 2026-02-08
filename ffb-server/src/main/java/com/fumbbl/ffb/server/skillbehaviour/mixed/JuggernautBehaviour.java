package com.fumbbl.ffb.server.skillbehaviour.mixed;

import com.fumbbl.ffb.BlockResult;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.action.block.StepJuggernaut;
import com.fumbbl.ffb.server.step.action.block.StepJuggernaut.StepState;
import com.fumbbl.ffb.server.step.action.block.UtilBlockSequence;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.skill.mixed.Juggernaut;
import com.fumbbl.ffb.util.UtilCards;

@RulesCollection(Rules.BB2016)
@RulesCollection(Rules.BB2020)
public class JuggernautBehaviour extends SkillBehaviour<Juggernaut> {
	public JuggernautBehaviour() {
		super();

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
				if ((PlayerAction.BLITZ == actingPlayer.getPlayerAction()) && UtilCards.hasSkill(actingPlayer, skill)) {
					if (state.usingJuggernaut == null) {
						UtilServerDialog.showDialog(step.getGameState(),
							new DialogSkillUseParameter(actingPlayer.getPlayer().getId(), skill, 0), false);
					} else {
						if (state.usingJuggernaut) {
							step.getResult()
								.addReport(new ReportSkillUse(actingPlayer.getPlayerId(), skill, true, SkillUse.PUSH_BACK_OPPONENT));
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