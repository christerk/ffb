package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SkillUse;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.server.InjuryType.InjuryTypeBallAndChain;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.action.block.StepWrestle;
import com.balancedbytes.games.ffb.server.step.action.block.StepWrestle.StepState;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerInjury;
import com.balancedbytes.games.ffb.skill.Wrestle;
import com.balancedbytes.games.ffb.util.UtilCards;

public class WrestleBehaviour extends SkillBehaviour<Wrestle> {
	public WrestleBehaviour() {
		super(Wrestle.class);

		registerModifier(new StepModifier<StepWrestle, StepWrestle.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepWrestle step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				 if (state.usingWrestleAttacker == null) {
		            	state.usingWrestleAttacker = useSkillCommand.isSkillUsed();
		            } else {
		            	state.usingWrestleDefender = useSkillCommand.isSkillUsed();
		            }
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepWrestle step, StepWrestle.StepState state) {
				 Game game = step.getGameState().getGame();
				    ActingPlayer actingPlayer = game.getActingPlayer();
				    PlayerState attackerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
				    PlayerState defenderState = game.getFieldModel().getPlayerState(game.getDefender());
				    if (state.usingWrestleAttacker == null) {
				      boolean attackerCanUseSkill = UtilCards.hasSkill(game, actingPlayer, skill) && !attackerState.isRooted();
				      if (attackerCanUseSkill) {
				        UtilServerDialog.showDialog(step.getGameState(), new DialogSkillUseParameter(actingPlayer.getPlayer().getId(), skill, 0), false);
				      } else {
				      	state.usingWrestleAttacker = false;
				      }
				    }
			    if ((state.usingWrestleAttacker != null) && (state.usingWrestleDefender == null)) {
			    	 boolean defenderCanUseSkill = UtilCards.hasSkill(game, game.getDefender(), skill) && !defenderState.isRooted();
			    	 boolean actingPlayerIsBlitzing = actingPlayer.getPlayerAction() == PlayerAction.BLITZ;
				      if (!state.usingWrestleAttacker && defenderCanUseSkill
				          && !(actingPlayerIsBlitzing && UtilCards.cancelsSkill(actingPlayer.getPlayer(), skill))) {
				        UtilServerDialog.showDialog(step.getGameState(), new DialogSkillUseParameter(game.getDefenderId(), skill, 0), true);
				      } else {
				    	  state.usingWrestleDefender = false;
				      }
				    }
				    if (state.usingWrestleDefender != null) {
				      if (state.usingWrestleAttacker) {
				        step.getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), skill, true, SkillUse.BRING_DOWN_OPPONENT));
				      } else if (state.usingWrestleDefender) {
				    	  step.getResult().addReport(new ReportSkillUse(game.getDefenderId(), skill, true, SkillUse.BRING_DOWN_OPPONENT));
				      } else {
				        if (UtilCards.hasSkill(game, actingPlayer, skill) || UtilCards.hasSkill(game, game.getDefender(), skill)) {
				        	step.getResult().addReport(new ReportSkillUse(null, skill, false, null));
				        }
				      }
				      if (state.usingWrestleAttacker || state.usingWrestleDefender) {
				        step.publishParameters(UtilServerInjury.dropPlayer(step, game.getDefender(), ApothecaryMode.DEFENDER));
				        step.publishParameters(UtilServerInjury.dropPlayer(step, actingPlayer.getPlayer(), ApothecaryMode.ATTACKER));
				        
				        if (UtilCards.hasSkillWithProperty(game.getDefender(), NamedProperties.placedProneCausesInjuryRoll)) {
				        	FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
				          step.publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT,
			          	UtilServerInjury.handleInjury(step, new InjuryTypeBallAndChain(), actingPlayer.getPlayer(), game.getDefender(), defenderCoordinate, null, ApothecaryMode.DEFENDER))
				          );
				        }
				      }
				    	step.getResult().setNextAction(StepAction.NEXT_STEP);
				    }
				return false;
			}

		});
	}
}