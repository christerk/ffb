package com.fumbbl.ffb.server.skillbehaviour.bb2016;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.ActionStatus;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.util.UtilActingPlayer;

/**
 * Step in block sequence to handle skill TAKE ROOT.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * 
 * Sets stepParameter END_PLAYER_ACTION for all steps on the stack.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public class StepTakeRoot extends AbstractStepWithReRoll {

	public class StepState {

		public ActionStatus status;
		public boolean continueOnFailure;

	}

	private StepState state;
	private String fGotoLabelOnFailure;

	public StepTakeRoot(GameState pGameState) {
		super(pGameState);
		state = new StepState();
	}

	public StepId getId() {
		return StepId.TAKE_ROOT;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
				// mandatory
				case GOTO_LABEL_ON_FAILURE:
					fGotoLabelOnFailure = (String) parameter.getValue();
					break;
				default:
					break;
				}
			}
		}
		if (fGotoLabelOnFailure == null) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_FAILURE + " is not initialized.");
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
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		state.status = ActionStatus.SUCCESS;
		boolean continueOnFailure = false;
		Game game = getGameState().getGame();
		if (!game.getTurnMode().checkNegatraits()) {
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}
		ActingPlayer actingPlayer = game.getActingPlayer();
		PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
		if (playerState.isConfused()) {
			game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), playerState.changeConfused(false));
		}
		if (playerState.isHypnotized()) {
			game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), playerState.changeHypnotized(false));
		}

		getGameState().executeStepHooks(this, state);

		if (state.status == ActionStatus.SUCCESS) {
			getResult().setNextAction(StepAction.NEXT_STEP);
		} else {
			if (state.status == ActionStatus.FAILURE) {
				if (continueOnFailure) {
					getResult().setNextAction(StepAction.NEXT_STEP);
				} else {
					getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
				}
			}
		}
	}

	public boolean cancelPlayerAction() {
		boolean continueOnFailure = false;
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		actingPlayer.setGoingForIt(false);
		actingPlayer.setDodging(false);
		actingPlayer.setCurrentMove(actingPlayer.getPlayer().getMovementWithModifiers());
		switch (actingPlayer.getPlayerAction()) {
		case BLITZ:
		case BLITZ_MOVE:
			publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
			game.getTurnData().setBlitzUsed(true);
			break;
		case PASS_MOVE:
			UtilActingPlayer.changeActingPlayer(game, actingPlayer.getPlayerId(), PlayerAction.PASS,
					actingPlayer.isJumping());
			break;
		case THROW_TEAM_MATE_MOVE:
			UtilActingPlayer.changeActingPlayer(game, actingPlayer.getPlayerId(), PlayerAction.THROW_TEAM_MATE,
					actingPlayer.isJumping());
			break;
		case KICK_TEAM_MATE_MOVE:
			UtilActingPlayer.changeActingPlayer(game, actingPlayer.getPlayerId(), PlayerAction.KICK_TEAM_MATE,
					actingPlayer.isJumping());
			break;
		case HAND_OVER_MOVE:
			UtilActingPlayer.changeActingPlayer(game, actingPlayer.getPlayerId(), PlayerAction.HAND_OVER,
					actingPlayer.isJumping());
			break;
		case FOUL_MOVE:
			UtilActingPlayer.changeActingPlayer(game, actingPlayer.getPlayerId(), PlayerAction.FOUL,
					actingPlayer.isJumping());
			break;
		case PASS:
		case THROW_TEAM_MATE:
		case KICK_TEAM_MATE:
		case HAND_OVER:
		case FOUL:
		case BLOCK:
		case MULTIPLE_BLOCK:
			continueOnFailure = true;
			break;
		default:
			break;
		}
		PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
		game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), playerState.changeRooted(true));
		getResult().setSound(SoundId.ROOT);
		return continueOnFailure;
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
		return jsonObject;
	}

	@Override
	public StepTakeRoot initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(game, jsonObject);
		return this;
	}

}
