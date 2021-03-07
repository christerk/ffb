package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.PushbackMode;
import com.balancedbytes.games.ffb.PushbackSquare;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillUse;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.action.block.StepBlockDodge;
import com.balancedbytes.games.ffb.server.step.action.block.StepBlockDodge.StepState;
import com.balancedbytes.games.ffb.server.step.action.block.UtilBlockSequence;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerPushback;
import com.balancedbytes.games.ffb.skill.Dodge;
import com.balancedbytes.games.ffb.util.ArrayTool;

@RulesCollection(Rules.COMMON)
public class DodgeBehaviour extends SkillBehaviour<Dodge> {
	public DodgeBehaviour() {
		super();

		registerModifier(new StepModifier<StepBlockDodge, StepBlockDodge.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepBlockDodge step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				state.usingDodge = useSkillCommand.isSkillUsed();
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepBlockDodge step, StepState state) {
				findDodgeChoice(step, state);
				UtilServerDialog.hideDialog(step.getGameState());
				Game game = step.getGameState().getGame();
				if (state.usingDodge == null) {
					UtilServerDialog.showDialog(step.getGameState(), new DialogSkillUseParameter(game.getDefenderId(), skill, 0),
							true);
				} else {
					step.getResult()
							.addReport(new ReportSkillUse(game.getDefenderId(), skill, state.usingDodge, SkillUse.AVOID_FALLING));
					if (state.usingDodge) {
						game.getFieldModel().setPlayerState(game.getDefender(), state.oldDefenderState);
					} else {
						PlayerState defenderState = game.getFieldModel().getPlayerState(game.getDefender());
						game.getFieldModel().setPlayerState(game.getDefender(), defenderState.changeBase(PlayerState.FALLING));
					}
					step.publishParameters(UtilBlockSequence.initPushback(step));
					step.getResult().setNextAction(StepAction.NEXT_STEP);
				}
				return false;
			}

		});
	}

	private void findDodgeChoice(StepBlockDodge step, StepState state) {

		// ask for dodge only when:
		// 1: The push is a potential chainpush, the three "opposite" squares are
		// occupied.
		// 2: It is the first turn after kickoff and a defending player has the
		// potential to be pushed over the middle-line into the attackers half
		// 3: There is a possibility that you would be pushed next to the sideline.
		// Which is you are standing one square away from sideline and the opponent
		// is pushing from the same row or from the row more infield.

		if (state.usingDodge == null) {

			boolean chainPush = false;
			boolean sidelinePush = false;
			boolean attackerHalfPush = false;
			Game game = step.getGameState().getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();

			Player<?> attacker = actingPlayer.getPlayer();
			FieldCoordinate attackerCoordinate = game.getFieldModel().getPlayerCoordinate(attacker);
			FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
			PushbackSquare startingSquare = UtilServerPushback.findStartingSquare(attackerCoordinate, defenderCoordinate,
					game.isHomePlaying());

			PushbackSquare[] regularPushbackSquares = UtilServerPushback.findPushbackSquares(game, startingSquare,
					PushbackMode.REGULAR);
			if (ArrayTool.isProvided(regularPushbackSquares)) {
				for (PushbackSquare pushbackSquare : regularPushbackSquares) {
					FieldCoordinate coordinate = pushbackSquare.getCoordinate();
					if (game.getFieldModel().getPlayer(coordinate) != null) {
						chainPush = true;
					}
				}
			}

			PushbackSquare[] grabPushbackSquares = regularPushbackSquares;
			if ((actingPlayer.getPlayerAction() == PlayerAction.BLOCK)
					&& attacker.hasSkillProperty(NamedProperties.canPushBackToAnySquare)
					&& !game.getDefender().hasSkillProperty(NamedProperties.canChooseOwnPushedBackSquare)) {
				grabPushbackSquares = UtilServerPushback.findPushbackSquares(game, startingSquare, PushbackMode.GRAB);
			}
			if (ArrayTool.isProvided(regularPushbackSquares)) {
				for (PushbackSquare pushbackSquare : grabPushbackSquares) {
					FieldCoordinate coordinate = pushbackSquare.getCoordinate();
					if (FieldCoordinateBounds.SIDELINE_LOWER.isInBounds(coordinate)
							|| FieldCoordinateBounds.SIDELINE_UPPER.isInBounds(coordinate)
							|| FieldCoordinateBounds.ENDZONE_HOME.isInBounds(coordinate)
							|| FieldCoordinateBounds.ENDZONE_AWAY.isInBounds(coordinate)) {
						sidelinePush = true;
					}
					if ((game.getTeamHome().hasPlayer(attacker) && FieldCoordinateBounds.HALF_HOME.isInBounds(coordinate)
							&& game.getTurnDataHome().isFirstTurnAfterKickoff())
							|| (game.getTeamAway().hasPlayer(attacker) && FieldCoordinateBounds.HALF_AWAY.isInBounds(coordinate)
									&& game.getTurnDataAway().isFirstTurnAfterKickoff())) {
						attackerHalfPush = true;
					}
				}
			}

			if (!chainPush && !sidelinePush && !attackerHalfPush) {
				state.usingDodge = true;
			}

		}

	}
}
