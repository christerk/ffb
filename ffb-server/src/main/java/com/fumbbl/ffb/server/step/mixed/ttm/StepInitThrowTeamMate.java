package com.fumbbl.ffb.server.step.mixed.ttm;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.commands.ClientCommandActingPlayer;
import com.fumbbl.ffb.net.commands.ClientCommandThrowTeamMate;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilRangeRuler;

/**
 * Step to init the throw team mate sequence.
 * <p>
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END. May be
 * initialized with stepParameter TARGET_COORDINATE. May be initialized with
 * stepParameter THROWN_PLAYER_ID.
 * <p>
 * Sets stepParameter END_PLAYER_ACTION for all steps on the stack. Sets
 * stepParameter END_TURN for all steps on the stack. Sets stepParameter
 * THROWN_PLAYER_ID for all steps on the stack. Sets stepParameter
 * THROWN_PLAYER_STATE for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public final class StepInitThrowTeamMate extends AbstractStep {

	private String gotoLabelOnEnd, thrownPlayerId;
	private FieldCoordinate targetCoordinate;
	private boolean endTurn, endPlayerAction, kicked;

	public StepInitThrowTeamMate(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.INIT_THROW_TEAM_MATE;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
					// mandatory
					case GOTO_LABEL_ON_END:
						gotoLabelOnEnd = (String) parameter.getValue();
						break;
					// optional
					case TARGET_COORDINATE:
						targetCoordinate = (FieldCoordinate) parameter.getValue();
						break;
					// optional
					case THROWN_PLAYER_ID:
						thrownPlayerId = (String) parameter.getValue();
						break;
					case IS_KICKED_PLAYER:
						kicked = parameter.getValue() != null && (boolean) parameter.getValue();
						break;
					default:
						break;
				}
			}
		}
		if (!StringTool.isProvided(gotoLabelOnEnd)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_END + " is not initialized.");
		}
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND && UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
			Game game = getGameState().getGame();
			switch (pReceivedCommand.getId()) {
				case CLIENT_THROW_TEAM_MATE:
					ClientCommandThrowTeamMate throwTeamMateCommand = (ClientCommandThrowTeamMate) pReceivedCommand.getCommand();
					if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), throwTeamMateCommand)) {
						if ((throwTeamMateCommand.getTargetCoordinate() != null) && StringTool.isProvided(thrownPlayerId)) {
							targetCoordinate = game.isHomePlaying() ? throwTeamMateCommand.getTargetCoordinate()
								: throwTeamMateCommand.getTargetCoordinate().transform();
						} else {
							thrownPlayerId = throwTeamMateCommand.getThrownPlayerId();
							targetCoordinate = null;
						}
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_ACTING_PLAYER:
					ClientCommandActingPlayer actingPlayerCommand = (ClientCommandActingPlayer) pReceivedCommand.getCommand();
					if (StringTool.isProvided(actingPlayerCommand.getPlayerId())) {
						UtilServerSteps.changePlayerAction(this, actingPlayerCommand.getPlayerId(),
							actingPlayerCommand.getPlayerAction(), actingPlayerCommand.isJumping());
					} else {
						endPlayerAction = true;
					}
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_END_TURN:
					if (UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
						endTurn = true;
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				default:
					break;
			}
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (endTurn) {
			publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
			getResult().setNextAction(StepAction.GOTO_LABEL, gotoLabelOnEnd);
		} else if (endPlayerAction) {
			publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
			getResult().setNextAction(StepAction.GOTO_LABEL, gotoLabelOnEnd);
		} else {
			if (StringTool.isProvided(thrownPlayerId)) {
				if (targetCoordinate != null) {
					game.setPassCoordinate(targetCoordinate);
					game.getFieldModel().setRangeRuler(
						UtilRangeRuler.createRangeRuler(game, actingPlayer.getPlayer(), game.getPassCoordinate(), true));
					if (game.getFieldModel().getRangeRuler() != null) {
						getResult().setNextAction(StepAction.NEXT_STEP);
					}
				} else {
					game.setDefenderId(thrownPlayerId);
					PlayerState oldPlayerState = game.getFieldModel().getPlayerState(game.getDefender());
					PlayerState thrownPlayerState = oldPlayerState.changeBase(PlayerState.PICKED_UP);
					publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_ID, thrownPlayerId));
					publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_STATE, thrownPlayerState));
					publishParameter(new StepParameter(StepParameterKey.OLD_DEFENDER_STATE, oldPlayerState));
					FieldCoordinate thrownPlayerCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
					publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, thrownPlayerCoordinate));
					boolean thrownPlayerHasBall = thrownPlayerCoordinate.equals(game.getFieldModel().getBallCoordinate())
						&& !game.getFieldModel().isBallMoving();
					publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_HAS_BALL, thrownPlayerHasBall));
					game.getFieldModel().setPlayerState(game.getDefender(), thrownPlayerState);
					UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), kicked ? PlayerAction.KICK_TEAM_MATE : PlayerAction.THROW_TEAM_MATE, false);
				}
			}
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_TURN.addTo(jsonObject, endTurn);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, endPlayerAction);
		IServerJsonOption.THROWN_PLAYER_ID.addTo(jsonObject, thrownPlayerId);
		IServerJsonOption.TARGET_COORDINATE.addTo(jsonObject, targetCoordinate);
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, gotoLabelOnEnd);
		IServerJsonOption.IS_KICKED_PLAYER.addTo(jsonObject, kicked);
		return jsonObject;
	}

	@Override
	public StepInitThrowTeamMate initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		endTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		endPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(source, jsonObject);
		thrownPlayerId = IServerJsonOption.THROWN_PLAYER_ID.getFrom(source, jsonObject);
		targetCoordinate = IServerJsonOption.TARGET_COORDINATE.getFrom(source, jsonObject);
		gotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(source, jsonObject);
		kicked = IServerJsonOption.IS_KICKED_PLAYER.getFrom(source, jsonObject);
		return this;
	}

}
