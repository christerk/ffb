package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.ActionStatus;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.util.UtilServerPlayerMove;
import com.fumbbl.ffb.util.UtilActingPlayer;

/**
 * Step in block sequence to handle skill TAKE ROOT.
 * <p>
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * <p>
 * Sets stepParameter END_PLAYER_ACTION for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class StepTakeRoot extends AbstractStepWithReRoll {

	private final StepState state;

	private void executeStep() {
		state.status = ActionStatus.SUCCESS;
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

		if (state.status != ActionStatus.WAITING_FOR_RE_ROLL) {
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	public StepTakeRoot(GameState pGameState) {
		super(pGameState);
		state = new StepState();
	}

	public StepId getId() {
		return StepId.TAKE_ROOT;
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

	public void cancelPlayerAction() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		actingPlayer.setGoingForIt(false);
		actingPlayer.setDodging(false);

		switch (actingPlayer.getPlayerAction()) {
			case BLITZ_MOVE:
				UtilActingPlayer.changeActingPlayer(game, actingPlayer.getPlayerId(), PlayerAction.BLITZ,
					actingPlayer.isJumping());
				break;
			case PASS_MOVE:
				UtilActingPlayer.changeActingPlayer(game, actingPlayer.getPlayerId(), PlayerAction.PASS,
					actingPlayer.isJumping());
				game.setThrowerId(actingPlayer.getPlayerId());
				game.setThrowerAction(PlayerAction.PASS);
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
				game.setThrowerId(actingPlayer.getPlayerId());
				game.setThrowerAction(PlayerAction.HAND_OVER);
				break;
			case FOUL_MOVE:
				UtilActingPlayer.changeActingPlayer(game, actingPlayer.getPlayerId(), PlayerAction.FOUL,
					actingPlayer.isJumping());
				break;
			case MOVE:
				UtilServerPlayerMove.updateMoveSquares(getGameState(), false);
				break;
			default:
				break;
		}
		PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
		game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), playerState.changeRooted(true));
		getResult().setSound(SoundId.ROOT);
	}

	public static class StepState {

		public ActionStatus status;

	}
}
