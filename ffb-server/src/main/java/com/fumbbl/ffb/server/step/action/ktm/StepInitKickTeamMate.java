package com.fumbbl.ffb.server.step.action.ktm;

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
import com.fumbbl.ffb.net.commands.ClientCommandKickTeamMate;
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

/**
 * Step to init the throw team mate sequence.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END. May be
 * initialized with stepParameter TARGET_COORDINATE. May be initialized with
 * stepParameter THROWN_PLAYER_ID.
 * 
 * Sets stepParameter END_PLAYER_ACTION for all steps on the stack. Sets
 * stepParameter END_TURN for all steps on the stack. Sets stepParameter
 * THROWN_PLAYER_ID for all steps on the stack. Sets stepParameter
 * THROWN_PLAYER_STATE for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public final class StepInitKickTeamMate extends AbstractStep {

	private String fGotoLabelOnEnd;
	private String fKickedPlayerId;
	private int fNumDice;
	private boolean fEndTurn;
	private boolean fEndPlayerAction;

	public StepInitKickTeamMate(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.INIT_KICK_TEAM_MATE;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
				// mandatory
				case GOTO_LABEL_ON_END:
					fGotoLabelOnEnd = (String) parameter.getValue();
					break;
				// optional
				case NR_OF_DICE:
					fNumDice = (parameter.getValue() != null) ? (Integer) parameter.getValue() : 0;
					break;
				// optional
				case KICKED_PLAYER_ID:
					fKickedPlayerId = (String) parameter.getValue();
					break;
				default:
					break;
				}
			}
		}
		if (!StringTool.isProvided(fGotoLabelOnEnd)) {
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
		if ((pReceivedCommand != null) && (commandStatus == StepCommandStatus.UNHANDLED_COMMAND)
				&& UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
			switch (pReceivedCommand.getId()) {
			case CLIENT_KICK_TEAM_MATE:
				ClientCommandKickTeamMate kickTeamMateCommand = (ClientCommandKickTeamMate) pReceivedCommand.getCommand();
				if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), kickTeamMateCommand)) {
					if ((kickTeamMateCommand.getNumDice() != 0) && StringTool.isProvided(fKickedPlayerId)) {
						fNumDice = kickTeamMateCommand.getNumDice();
					} else {
						fKickedPlayerId = kickTeamMateCommand.getKickedPlayerId();
						fNumDice = 0;
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
					fEndPlayerAction = true;
				}
				commandStatus = StepCommandStatus.EXECUTE_STEP;
				break;
			case CLIENT_END_TURN:
				if (UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
					fEndTurn = true;
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
		if (fEndTurn) {
			publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
			getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
		} else if (fEndPlayerAction) {
			publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
			getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
		} else {
			if (StringTool.isProvided(fKickedPlayerId)) {
				if (fNumDice != 0) {
					game.setDefenderId(fKickedPlayerId);
					PlayerState kickedPlayerState = game.getFieldModel().getPlayerState(game.getDefender());
					publishParameter(new StepParameter(StepParameterKey.KICKED_PLAYER_ID, fKickedPlayerId));
					publishParameter(new StepParameter(StepParameterKey.KICKED_PLAYER_STATE, kickedPlayerState));
					FieldCoordinate kickedPlayerCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
					publishParameter(new StepParameter(StepParameterKey.KICKED_PLAYER_COORDINATE, kickedPlayerCoordinate));
					boolean thrownPlayerHasBall = kickedPlayerCoordinate.equals(game.getFieldModel().getBallCoordinate())
							&& !game.getFieldModel().isBallMoving();
					publishParameter(new StepParameter(StepParameterKey.KICKED_PLAYER_HAS_BALL, thrownPlayerHasBall));
					publishParameter(new StepParameter(StepParameterKey.NR_OF_DICE, fNumDice));
					UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.KICK_TEAM_MATE, false);
					getResult().setNextAction(StepAction.NEXT_STEP);
				}
			}
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, fEndPlayerAction);
		IServerJsonOption.THROWN_PLAYER_ID.addTo(jsonObject, fKickedPlayerId);
		IServerJsonOption.NR_OF_DICE.addTo(jsonObject, fNumDice);
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
		return jsonObject;
	}

	@Override
	public StepInitKickTeamMate initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fEndTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		fEndPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(source, jsonObject);
		fKickedPlayerId = IServerJsonOption.THROWN_PLAYER_ID.getFrom(source, jsonObject);
		fNumDice = IServerJsonOption.NR_OF_DICE.getFrom(source, jsonObject);
		fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(source, jsonObject);
		return this;
	}

}
