package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.PlayerChoiceMode;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.dialog.DialogPlayerChoiceParameter;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportTentaclesShadowingRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.action.common.StepShadowing;
import com.balancedbytes.games.ffb.server.step.action.common.StepShadowing.StepState;
import com.balancedbytes.games.ffb.server.util.ServerUtilBlock;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerPlayerMove;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.skill.Shadowing;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

public class ShadowingBehaviour extends SkillBehaviour<Shadowing> {
	public ShadowingBehaviour() {
		super(Shadowing.class);

		registerModifier(new StepModifier<StepShadowing, StepShadowing.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepShadowing step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepShadowing step, StepState state) {
				Game game = step.getGameState().getGame();
			    ActingPlayer actingPlayer = game.getActingPlayer();
			    UtilServerDialog.hideDialog(step.getGameState());
			    boolean doNextStep = true;
			    boolean doShadowing = (!state.usingDivingTackle && (game.getTurnMode() != TurnMode.KICKOFF_RETURN) && (game.getTurnMode() != TurnMode.PASS_BLOCK));
			    if (doShadowing && (state.coordinateFrom != null) && (state.usingShadowing == null)) {
			      Player[] shadowers = UtilPlayer.findAdjacentOpposingPlayersWithSkill(game, state.coordinateFrom, skill, true);
			      shadowers = UtilPlayer.filterThrower(game, shadowers);
			    	if (game.getTurnMode() == TurnMode.DUMP_OFF) {
			    		shadowers = UtilPlayer.filterAttackerAndDefender(game, shadowers);
			    	}
			      if (ArrayTool.isProvided(shadowers)) {
			        String teamId = game.isHomePlaying() ? game.getTeamAway().getId() : game.getTeamHome().getId();
			        String[] descriptionArray = new String[shadowers.length];
			        for (int i = 0; i < shadowers.length; i++) {
			          int attributeDiff = shadowers[i].getMovement() - actingPlayer.getPlayer().getMovement();
			          StringBuilder description = new StringBuilder();
			          if (attributeDiff > 0) {
			            description.append("(").append(attributeDiff).append(" MA advantage)");
			          }
			          if (attributeDiff == 0) {
			            description.append("(equal MA)");
			          }
			          if (attributeDiff < 0) {
			            description.append("(").append(Math.abs(attributeDiff)).append(" MA disadavantage)");
			          }
			          descriptionArray[i] = description.toString();
			        } 
			        Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
			        UtilServerDialog.showDialog(
			            step.getGameState(),
			            new DialogPlayerChoiceParameter(teamId, PlayerChoiceMode.SHADOWING, shadowers, descriptionArray, 1),
			            !actingTeam.getId().equals(teamId)
			        );
			        doNextStep = false;
			      } else {
			      	state.usingShadowing = false;
			      }
			    }
			    if (doShadowing && (state.coordinateFrom != null) && (state.usingShadowing != null)) {
			      doNextStep = true;
			      if (state.usingShadowing && (game.getDefender() != null)) {
			        boolean rollShadowing = true;
			        if (ReRolledActions.SHADOWING_ESCAPE == step.getReRolledAction()) {
			          if ((step.getReRollSource() == null) || !UtilServerReRoll.useReRoll(step, step.getReRollSource(), actingPlayer.getPlayer())) {
			            rollShadowing = false;
			          }
			        }
			        if (rollShadowing) {
			          int[] rollEscape = step.getGameState().getDiceRoller().rollShadowingEscape();
			          boolean successful = DiceInterpreter.getInstance().isShadowingEscapeSuccessful(rollEscape, UtilCards.getPlayerMovement(game, game.getDefender()), UtilCards.getPlayerMovement(game, actingPlayer.getPlayer()));
			          int minimumRoll = DiceInterpreter.getInstance().minimumRollShadowingEscape(UtilCards.getPlayerMovement(game, game.getDefender()), UtilCards.getPlayerMovement(game, actingPlayer.getPlayer()));
			          boolean reRolled = ((step.getReRolledAction() == ReRolledActions.SHADOWING_ESCAPE) && (step.getReRollSource() != null));
			          step.getResult().addReport(new ReportTentaclesShadowingRoll(skill, game.getDefenderId(), rollEscape, successful, minimumRoll, reRolled));
			          if (successful) {
			          	state.usingShadowing = false;
			          } else {
			            if (step.getReRolledAction() != ReRolledActions.SHADOWING_ESCAPE) {
			              if (UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), actingPlayer.getPlayer(), ReRolledActions.SHADOWING_ESCAPE, minimumRoll, false)) {
			                doNextStep = false;
			              }
			            }
			          }
			        }
			      }
			      if (doNextStep && state.usingShadowing) {
			        game.getFieldModel().updatePlayerAndBallPosition(game.getDefender(), state.coordinateFrom);
			        UtilServerPlayerMove.updateMoveSquares(step.getGameState(), actingPlayer.isLeaping());
			        ServerUtilBlock.updateDiceDecorations(game);
			      }
			    }
			    if (doNextStep) {
			    	if (state.defenderPosition != null) {
			    		Player defender = game.getFieldModel().getPlayer(state.defenderPosition);
			    		game.setDefenderId((defender != null) ? defender.getId() : null);
			    	}
				    step.getResult().setNextAction(StepAction.NEXT_STEP);
			    }
				return false;
			}
			
		});
	}
}