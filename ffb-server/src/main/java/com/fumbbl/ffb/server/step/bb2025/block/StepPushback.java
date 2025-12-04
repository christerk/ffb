package com.fumbbl.ffb.server.step.bb2025.block;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.PushbackMode;
import com.fumbbl.ffb.PushbackSquare;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.net.commands.ClientCommandPushback;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportPushback;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeCrowdPush;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeCrowdPushForSpp;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeServer;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.server.util.UtilServerPlayerMove;
import com.fumbbl.ffb.server.util.UtilServerPushback;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Step in block sequence to handle pushbacks.
 * <p>
 * Expects stepParameter STARTING_PUSHBACK_SQUARE to be set by a preceding step.
 * Expects stepParameter OLD_DEFENDER_STATE to be set by a preceding step.
 * <p>
 * Sets stepParameter CATCH_SCATTER_THROWIN_MODE for all steps on the stack.
 * Sets stepParameter DEFENDER_PUSHED for all steps on the stack. Sets
 * stepParameter FOLLOWUP_CHOICE for all steps on the stack. Sets stepParameter
 * STARTING_PUSHBACK_SQUARE for all steps on the stack. Sets stepParameter
 * INJURY_RESULT for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepPushback extends AbstractStep {

	private final StepState state;

	public StepPushback(GameState pGameState) {
		super(pGameState);
		state = new StepState();

		state.pushbackStack = new Stack<>();
		state.sideStepping = new HashMap<>();
		state.standingFirm = new HashMap<>();
	}

	public StepId getId() {
		return StepId.PUSHBACK;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pReceivedCommand.getId()) {
				case CLIENT_USE_SKILL:
					commandStatus = handleSkillCommand((ClientCommandUseSkill) pReceivedCommand.getCommand(), state);
					break;
				case CLIENT_PUSHBACK:
					ClientCommandPushback pushbackCommand = (ClientCommandPushback) pReceivedCommand.getCommand();
					if (UtilServerSteps.checkCommandIsFromHomePlayer(getGameState(), pReceivedCommand)) {
						state.pushbackStack.push(pushbackCommand.getPushback());
					} else {
						state.pushbackStack.push(pushbackCommand.getPushback().transform());
					}
					commandStatus = StepCommandStatus.EXECUTE_STEP;
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

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
				case OLD_DEFENDER_STATE:
					state.oldDefenderState = (PlayerState) parameter.getValue();
					return true;
				case STARTING_PUSHBACK_SQUARE:
					state.startingPushbackSquare = (PushbackSquare) parameter.getValue();
					return true;
				default:
					break;
			}
		}
		return false;
	}

	private void executeStep() {
		state.doPush = false;
		UtilServerDialog.hideDialog(getGameState());
		Game game = getGameState().getGame();
		FieldModel fieldModel = game.getFieldModel();
		// player chose a coordinate
		if (state.pushbackStack.size() > 0) {
			Pushback lastPushback = state.pushbackStack.pop();
			state.pushbackStack.push(lastPushback);
			state.pushbackSquares = fieldModel.getPushbackSquares();
			for (int i = 0; i < state.pushbackSquares.length; i++) {
				if (!state.pushbackSquares[i].isLocked()) {
					fieldModel.remove(state.pushbackSquares[i]);
					if (state.pushbackSquares[i].getCoordinate().equals(lastPushback.getCoordinate())) {
						publishParameter(new StepParameter(StepParameterKey.STARTING_PUSHBACK_SQUARE, state.pushbackSquares[i]));
						state.pushbackSquares[i].setSelected(true);
						state.pushbackSquares[i].setLocked(true);
						fieldModel.add(state.pushbackSquares[i]);
					}
				}
			}
			state.doPush = (fieldModel.getPlayer(lastPushback.getCoordinate()) == null);
			state.pusherId = lastPushback.getPlayerId(); // subsequent: previous defender becomes pusher
		}
		// calculate new pushback squares
		if (!state.doPush && (state.startingPushbackSquare != null)) {

			if (!StringTool.isProvided(state.pusherId) && game.getActingPlayer() != null) {
				state.pusherId = game.getActingPlayer().getPlayerId(); // first push: acting player
			}

			FieldCoordinate defenderCoordinate = state.startingPushbackSquare.getCoordinate();
			state.defender = fieldModel.getPlayer(defenderCoordinate);
			if (state.defender == null) {
				throw new IllegalStateException("Defender unknown at this point - cannot continue.");
			}
			state.pushbackMode = PushbackMode.REGULAR;
			state.pushbackSquares = UtilServerPushback.findPushbackSquares(game, state.startingPushbackSquare,
				state.pushbackMode);
			fieldModel.add(state.pushbackSquares);
			state.freeSquareAroundDefender = false;
			FieldCoordinate[] adjacentSquares = fieldModel
				.findAdjacentCoordinates(state.startingPushbackSquare.getCoordinate(), FieldCoordinateBounds.FIELD, 1, false);
			for (int i = 0; !state.freeSquareAroundDefender && (i < adjacentSquares.length); i++) {
				if (fieldModel.getPlayer(adjacentSquares[i]) == null) {
					state.freeSquareAroundDefender = true;
				}
			}

			boolean stopProcessing = getGameState().executeStepHooks(this, state);

			if (!stopProcessing) {
				if (!ArrayTool.isProvided(state.pushbackSquares)) {
					// Crowdpush
					Player<?> attacker;
					InjuryTypeServer<?> injuryTypeServer;

					boolean sameTeam = state.defender != null && state.defender.getTeam() == game.getActingTeam();

					if (getGameState().getPrayerState().hasFanInteraction(game.getActingTeam()) && !sameTeam) {
						attacker = game.getActingPlayer().getPlayer();
						injuryTypeServer = new InjuryTypeCrowdPushForSpp();
					} else {
						attacker = null;
						injuryTypeServer = new InjuryTypeCrowdPush();
					}

					publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT,
						UtilServerInjury.handleInjury(this, injuryTypeServer, attacker, state.defender,
							state.startingPushbackSquare.getCoordinate(), null, null, ApothecaryMode.CROWD_PUSH)));
					game.getFieldModel().remove(state.defender);
					if (defenderCoordinate.equals(game.getFieldModel().getBallCoordinate())) {
						game.getFieldModel().setBallCoordinate(null);
						publishParameter(
							new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.THROW_IN));
						publishParameter(new StepParameter(StepParameterKey.THROW_IN_COORDINATE, defenderCoordinate));
						if (sameTeam) {
							publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
						}
					}
					publishParameter(new StepParameter(StepParameterKey.STARTING_PUSHBACK_SQUARE, null));
					state.doPush = true;
				}
			}
			if (state.startingPushbackSquare == null) {
				getResult().addReport(new ReportPushback(state.defender.getId(), state.pushbackMode));
			}
		}
		if (state.doPush) {
			publishParameter(new StepParameter(StepParameterKey.DEFENDER_PUSHED, true));
			if (state.pushbackStack.size() > 0) {
				while (state.pushbackStack.size() > 0) {
					Pushback pushback = state.pushbackStack.pop();
					Player<?> player = game.getPlayerById(pushback.getPlayerId());
					pushPlayer(player, pushback.getCoordinate());
				}
			}
			fieldModel.clearPushbackSquares();
			publishParameter(new StepParameter(StepParameterKey.STARTING_PUSHBACK_SQUARE, null));
			game.setWaitingForOpponent(false);
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	private void pushPlayer(Player<?> pPlayer, FieldCoordinate pCoordinate) {
		Game game = getGameState().getGame();
		FieldModel fieldModel = game.getFieldModel();
		fieldModel.updatePlayerAndBallPosition(pPlayer, pCoordinate);
		UtilServerPlayerMove.updateMoveSquares(getGameState(), false);
		if (fieldModel.isBallMoving() && pCoordinate.equals(fieldModel.getBallCoordinate())) {
			publishParameter(
				new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
		}
		publishParameter(new StepParameter(StepParameterKey.PLAYER_ENTERING_SQUARE, pPlayer.getId()));
		publishParameter(new StepParameter(StepParameterKey.PLAYER_WAS_PUSHED, true));
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.OLD_DEFENDER_STATE.addTo(jsonObject, state.oldDefenderState);
		if (state.startingPushbackSquare != null) {
			IServerJsonOption.STARTING_PUSHBACK_SQUARE.addTo(jsonObject, state.startingPushbackSquare.toJsonValue());
		}
		IServerJsonOption.USING_GRAB.addTo(jsonObject, state.grabbing);
		IServerJsonOption.USING_SIDE_STEP.addTo(jsonObject, state.sideStepping);
		IServerJsonOption.USING_STAND_FIRM.addTo(jsonObject, state.standingFirm);
		IServerJsonOption.PUSHBACK_PUSHER_ID.addTo(jsonObject, state.pusherId);
		return jsonObject;
	}

	// JSON serialization

	@Override
	public StepPushback initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		state.oldDefenderState = IServerJsonOption.OLD_DEFENDER_STATE.getFrom(source, jsonObject);
		state.startingPushbackSquare = null;
		JsonObject startingPushbackSquareObject = IServerJsonOption.STARTING_PUSHBACK_SQUARE.getFrom(source, jsonObject);
		if (startingPushbackSquareObject != null) {
			state.startingPushbackSquare = new PushbackSquare().initFrom(source, startingPushbackSquareObject);
		}
		state.grabbing = IServerJsonOption.USING_GRAB.getFrom(source, jsonObject);
		state.sideStepping = IServerJsonOption.USING_SIDE_STEP.getFrom(source, jsonObject);
		state.standingFirm = IServerJsonOption.USING_STAND_FIRM.getFrom(source, jsonObject);
		state.pusherId = IServerJsonOption.PUSHBACK_PUSHER_ID.getFrom(source, jsonObject);

		return this;
	}

	public static class StepState {
		public PlayerState oldDefenderState;
		public PushbackSquare startingPushbackSquare;
		public Boolean grabbing;
		public Map<String, Boolean> sideStepping;
		public Map<String, Boolean> standingFirm;
		public Stack<Pushback> pushbackStack;
		public String pusherId;

		// Transients
		public Player<?> defender;
		public boolean doPush;
		public boolean freeSquareAroundDefender;
		public PushbackMode pushbackMode;
		public PushbackSquare[] pushbackSquares;		
	}
}
