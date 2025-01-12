package com.fumbbl.ffb.server.step.bb2020.block;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.*;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.*;
import com.fumbbl.ffb.server.step.action.block.UtilBlockSequence;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerPushback;
import com.fumbbl.ffb.util.ArrayTool;

/**
 * Step in block sequence to handle skill DODGE.
 * Expects stepParameter OLD_DEFENDER_STATE to be set by a preceding step.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class StepBlockDodge extends AbstractStep {

	public static class StepState {
		public Boolean usingDodge;
		public Boolean askForSkill;
		public PlayerState oldDefenderState;
	}

	private final StepState state;

	public StepBlockDodge(GameState pGameState) {
		super(pGameState);

		state = new StepState();
	}

	public StepId getId() {
		return StepId.BLOCK_DODGE;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND && pReceivedCommand.getId() == NetCommandId.CLIENT_USE_SKILL) {
			commandStatus = handleSkillCommand((ClientCommandUseSkill) pReceivedCommand.getCommand(), state);
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			if (parameter.getKey() == StepParameterKey.OLD_DEFENDER_STATE) {
				state.oldDefenderState = (PlayerState) parameter.getValue();
				return true;
			}
		}
		return false;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		if (state.askForSkill == null) {
			state.askForSkill = findDodgeChoice();
		}
		UtilServerDialog.hideDialog(getGameState());
		boolean waitForDialog = getGameState().executeStepHooks(this, state);

		if (waitForDialog) {
			return;
		}

		if (toPrimitive(state.usingDodge)) {
			game.getFieldModel().setPlayerState(game.getDefender(), state.oldDefenderState);
		} else {
			PlayerState defenderState = game.getFieldModel().getPlayerState(game.getDefender());
			game.getFieldModel().setPlayerState(game.getDefender(), defenderState.changeBase(PlayerState.FALLING));
		}

		publishParameters(UtilBlockSequence.initPushback(this));
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private boolean findDodgeChoice() {

		// ask for dodge only when:
		// 1: The push is a potential chainpush, the three "opposite" squares are
		// occupied.
		// 2: It is the first turn after kickoff and a defending player has the
		// potential to be pushed over the middle-line into the attackers half
		// 3: There is a possibility that you would be pushed next to the sideline.
		// Which is you are standing one square away from sideline and the opponent
		// is pushing from the same row or from the row more infield.

		boolean chainPush = false;
		boolean sidelinePush = false;
		boolean attackerHalfPush = false;
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();

		Player<?> attacker = actingPlayer.getPlayer();
		FieldCoordinate attackerCoordinate = game.getFieldModel().getPlayerCoordinate(attacker);
		FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
		PushbackSquare startingSquare = UtilServerPushback.findStartingSquare(attackerCoordinate, defenderCoordinate,
			game.isHomePlaying());

		if (startingSquare != null) {
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
			if ((actingPlayer.getPlayerAction().isBlockAction())
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
		}
		return chainPush || sidelinePush || attackerHalfPush;
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.USING_DODGE.addTo(jsonObject, state.usingDodge);
		IServerJsonOption.OLD_DEFENDER_STATE.addTo(jsonObject, state.oldDefenderState);
		IServerJsonOption.ASK_FOR_SKILL.addTo(jsonObject, state.askForSkill);
		return jsonObject;
	}

	@Override
	public StepBlockDodge initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		state.usingDodge = IServerJsonOption.USING_DODGE.getFrom(source, jsonObject);
		state.oldDefenderState = IServerJsonOption.OLD_DEFENDER_STATE.getFrom(source, jsonObject);
		state.askForSkill = IServerJsonOption.ASK_FOR_SKILL.getFrom(source, jsonObject);
		return this;
	}

}
