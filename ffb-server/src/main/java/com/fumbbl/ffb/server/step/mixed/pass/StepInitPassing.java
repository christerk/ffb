package com.fumbbl.ffb.server.step.mixed.pass;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.PassMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.net.commands.ClientCommandActingPlayer;
import com.fumbbl.ffb.net.commands.ClientCommandHandOver;
import com.fumbbl.ffb.net.commands.ClientCommandPass;
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
 * Initialization step of the pass sequence.
 * <p>
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END. May be
 * initialized with stepParameter CATCHER_ID. May be initialized with
 * stepParameter HAIL_MARY_PASS. May be initialized with stepParameter
 * TARGET_COORDINATE.
 * <p>
 * Sets stepParameter CATCHER_ID for all steps on the stack. Sets stepParameter
 * END_PLAYER_ACTION for all steps on the stack. Sets stepParameter END_TURN for
 * all steps on the stack. Sets stepParameter TARGET_COORDINATE for all steps on
 * the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public final class StepInitPassing extends AbstractStep {

	private String fGotoLabelOnEnd;
	private String fCatcherId;
	private boolean fEndTurn;
	private boolean fEndPlayerAction;

	public StepInitPassing(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.INIT_PASSING;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			Game game = getGameState().getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
					// mandatory
					case GOTO_LABEL_ON_END:
						fGotoLabelOnEnd = (String) parameter.getValue();
						break;
					// optional
					case TARGET_COORDINATE:
						if (parameter.getValue() != null) {
							FieldCoordinate targetCoordinate = (FieldCoordinate) parameter.getValue();
							game.setPassCoordinate(targetCoordinate);
							Player<?> catcher = game.getFieldModel().getPlayer(game.getPassCoordinate());
							fCatcherId = ((catcher != null) ? catcher.getId() : null);
							TargetSelectionState targetSelectionState = game.getFieldModel().getTargetSelectionState();
							String defenderId = targetSelectionState != null ? targetSelectionState.getSelectedPlayerId() : (game.getDefender() != null ? game.getDefender().getId() : null);
							if ((defenderId != null) && (game.getDefenderAction() == PlayerAction.DUMP_OFF)) {
								game.setThrowerId(defenderId);
								game.setThrowerAction(game.getDefenderAction());
							} else {
								game.setThrowerId(actingPlayer.getPlayerId());
								game.setThrowerAction(actingPlayer.getPlayerAction());
							}
						}
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
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND && (UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand) || game.getTurnMode() == TurnMode.DUMP_OFF)) {
			switch (pReceivedCommand.getId()) {
				case CLIENT_PASS:
					ClientCommandPass passCommand = (ClientCommandPass) pReceivedCommand.getCommand();
					if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), passCommand)
						|| (game.getTurnMode() == TurnMode.DUMP_OFF)) {
						if (UtilServerSteps.checkCommandIsFromHomePlayer(getGameState(), pReceivedCommand)) {
							game.setPassCoordinate(passCommand.getTargetCoordinate());
						} else {
							game.setPassCoordinate(passCommand.getTargetCoordinate().transform());
						}
						Player<?> catcher = game.getFieldModel().getPlayer(game.getPassCoordinate());
						fCatcherId = ((catcher != null) ? catcher.getId() : null);
						TargetSelectionState targetSelectionState = game.getFieldModel().getTargetSelectionState();
						String defenderId = targetSelectionState != null ? targetSelectionState.getSelectedPlayerId() : (game.getDefender() != null ? game.getDefender().getId() : null);
						if ((defenderId != null) && (game.getDefenderAction() == PlayerAction.DUMP_OFF)) {
							game.setThrowerId(defenderId);
							game.setThrowerAction(game.getDefenderAction());
						} else {
							game.setThrowerId(actingPlayer.getPlayerId());
							game.setThrowerAction(actingPlayer.getPlayerAction());
						}
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_HAND_OVER:
					ClientCommandHandOver handOverCommand = (ClientCommandHandOver) pReceivedCommand.getCommand();
					if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), handOverCommand)) {
						fCatcherId = handOverCommand.getCatcherId();
						Player<?> catcher = game.getPlayerById(fCatcherId);
						game.setPassCoordinate(game.getFieldModel().getPlayerCoordinate(catcher));
						game.setThrowerId(actingPlayer.getPlayerId());
						game.setThrowerAction(PlayerAction.HAND_OVER);
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
		if ((game.getThrower() == null) || (game.getThrowerAction() == null)) {
			return;
		}
		Player<?> catcher = game.getPlayerById(fCatcherId);
		if (catcher != null) {
			publishParameter(new StepParameter(StepParameterKey.CATCHER_ID, catcher.getId()));
		}
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (fEndTurn) {
			publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
			getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
		} else if (fEndPlayerAction) {
			publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
			getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
		} else if ((game.getThrower() == actingPlayer.getPlayer()) && actingPlayer.isSufferingBloodLust()
			&& !actingPlayer.hasFed()) {
			getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
		} else {
			FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(game.getThrower());
			if ((PlayerAction.HAND_OVER == game.getThrowerAction()) && (game.getThrower() == actingPlayer.getPlayer())
				&& (catcher != null)) {
				actingPlayer.setHasPassed(true);
				game.setConcessionPossible(false);
				game.getTurnData().setHandOverUsed(true);
				game.getTurnData().setTurnStarted(true);
				getResult().setNextAction(StepAction.NEXT_STEP);
				return;
			}
			PassMechanic mechanic = (PassMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.PASS.name());
			if ((game.getPassCoordinate() != null) && (game.getThrower() == actingPlayer.getPlayer())
				&& ((PlayerAction.THROW_BOMB == game.getThrowerAction())
				&& (mechanic.findPassingDistance(game, throwerCoordinate, game.getPassCoordinate(), false) != null))
				|| (PlayerAction.HAIL_MARY_BOMB == game.getThrowerAction())) {
				actingPlayer.setHasPassed(true);
				game.getTurnData().setTurnStarted(true);
				game.setConcessionPossible(false);
				if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
					game.getFieldModel()
						.setRangeRuler(UtilRangeRuler.createRangeRuler(game, game.getThrower(), game.getPassCoordinate(), false));
				}
				getResult().setNextAction(StepAction.NEXT_STEP);
				return;
			}
			if ((game.getPassCoordinate() != null) && (game.getThrower() == actingPlayer.getPlayer())
				&& ((PlayerAction.PASS == game.getThrowerAction())
				&& (mechanic.findPassingDistance(game, throwerCoordinate, game.getPassCoordinate(), false) != null))
				|| (PlayerAction.HAIL_MARY_PASS == game.getThrowerAction())) {
				actingPlayer.setHasPassed(true);
				game.getTurnData().setTurnStarted(true);
				game.setConcessionPossible(false);
				game.getTurnData().setPassUsed(true);
				if (PlayerAction.PASS == game.getThrowerAction()) {
					game.getFieldModel()
						.setRangeRuler(UtilRangeRuler.createRangeRuler(game, game.getThrower(), game.getPassCoordinate(), false));
				}
				getResult().setNextAction(StepAction.NEXT_STEP);
				return;
			}
			if ((game.getPassCoordinate() != null)
				&& (((PlayerAction.THROW_BOMB == game.getThrowerAction())
				|| (PlayerAction.DUMP_OFF == game.getThrowerAction()))
				&& (mechanic.findPassingDistance(game, throwerCoordinate, game.getPassCoordinate(), false) != null))
				|| (PlayerAction.HAIL_MARY_BOMB == game.getThrowerAction())) {
				if (game.getThrower() == actingPlayer.getPlayer()) {
					actingPlayer.setHasPassed(true);
				}
				if (PlayerAction.HAIL_MARY_BOMB != game.getThrowerAction()) {
					game.getFieldModel()
						.setRangeRuler(UtilRangeRuler.createRangeRuler(game, game.getThrower(), game.getPassCoordinate(), false));
				}
				getResult().setNextAction(StepAction.NEXT_STEP);
			}
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
		IServerJsonOption.CATCHER_ID.addTo(jsonObject, fCatcherId);
		IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, fEndPlayerAction);
		return jsonObject;
	}

	@Override
	public StepInitPassing initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(source, jsonObject);
		fCatcherId = IServerJsonOption.CATCHER_ID.getFrom(source, jsonObject);
		fEndTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		fEndPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(source, jsonObject);
		return this;
	}

}
