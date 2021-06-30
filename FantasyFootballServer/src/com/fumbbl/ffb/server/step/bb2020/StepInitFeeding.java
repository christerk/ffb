package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.BlitzTurnState;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportBiteSpectator;
import com.fumbbl.ffb.report.bb2020.ReportKickoffSequenceActivationsCount;
import com.fumbbl.ffb.report.bb2020.ReportKickoffSequenceActivationsExhausted;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.InjuryType.InjuryTypeBitten;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilBox;
import com.fumbbl.ffb.util.UtilPlayer;

/**
 * Step in any sequence to handle the feeding on another player.
 *
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END. Needs to be
 * initialized with stepParameter FEEDING_ALLOWED. May be initialized with
 * stepParameter END_PLAYER_ACTION. May be initialized with stepParameter
 * END_TURN.
 *
 * Sets stepParameter END_PLAYER_ACTION for all steps on the stack. Sets
 * stepParameter END_TURN for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class StepInitFeeding extends AbstractStep {

	private String fGotoLabelOnEnd;
	private Boolean fFeedOnPlayerChoice;
	private Boolean fFeedingAllowed;
	private boolean fEndPlayerAction;
	private boolean fEndTurn;

	public StepInitFeeding(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.INIT_FEEDING;
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
				// mandatory
				case FEEDING_ALLOWED:
					fFeedingAllowed = (Boolean) parameter.getValue();
					break;
				// optional
				case END_PLAYER_ACTION:
					fEndPlayerAction = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					break;
				// optional
				case END_TURN:
					fEndTurn = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					break;
				default:
					break;
				}
			}
		}
		if (!StringTool.isProvided(fGotoLabelOnEnd)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_END + " is not initialized.");
		}
		if (fFeedingAllowed == null) {
			throw new StepException("StepParameter " + StepParameterKey.FEEDING_ALLOWED + " is not initialized.");
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
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			Game game = getGameState().getGame();
			if (pReceivedCommand.getId() == NetCommandId.CLIENT_PLAYER_CHOICE) {
				ClientCommandPlayerChoice playerChoiceCommand = (ClientCommandPlayerChoice) pReceivedCommand.getCommand();
				if (PlayerChoiceMode.FEED == playerChoiceCommand.getPlayerChoiceMode()) {
					fFeedOnPlayerChoice = StringTool.isProvided(playerChoiceCommand.getPlayerId());
					game.setDefenderId(playerChoiceCommand.getPlayerId());
					commandStatus = StepCommandStatus.EXECUTE_STEP;
				}
			}
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		UtilServerDialog.hideDialog(getGameState());
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();

		if (fEndPlayerAction && game.getTurnMode() == TurnMode.BLITZ && getGameState().getBlitzTurnState() != null && actingPlayer.hasActed()) {
			BlitzTurnState blitzTurnState = getGameState().getBlitzTurnState();
			blitzTurnState.addActivation();
			getResult().addReport(new ReportKickoffSequenceActivationsCount(blitzTurnState.getAvailable(), blitzTurnState.getAmount(), blitzTurnState.getLimit()));

			IReport noMoreActivations = null;
			if (blitzTurnState.limitReached()) {
				noMoreActivations = new ReportKickoffSequenceActivationsExhausted(true);
			} else if (!blitzTurnState.availablePlayersLeft()) {
				noMoreActivations = new ReportKickoffSequenceActivationsExhausted(false);
			}

			if (noMoreActivations != null) {
				getResult().addReport(noMoreActivations);
				fEndTurn = true;
			}
		}

		if ((actingPlayer.getPlayer() == null) || !actingPlayer.isSufferingBloodLust() || actingPlayer.hasFed()) {
			publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, fEndPlayerAction));
			publishParameter(new StepParameter(StepParameterKey.END_TURN, fEndTurn));
			getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
			return;
		}
		if (actingPlayer.isSufferingBloodLust() && !actingPlayer.hasFed() && !fFeedingAllowed) {
			fFeedOnPlayerChoice = false;
		}
		boolean doNextStep = false;
		PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		if (playerState.hasTacklezones() && (fFeedOnPlayerChoice == null)) {
			game.setDefenderId(null);
			Team team = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
			Player<?>[] victims = UtilPlayer.findAdjacentPlayersToFeedOn(game, team, playerCoordinate);
			if (ArrayTool.isProvided(victims)) {
				UtilServerDialog.showDialog(getGameState(),
						new DialogPlayerChoiceParameter(team.getId(), PlayerChoiceMode.FEED, victims, null, 1), false);
			} else {
				fFeedOnPlayerChoice = false;
			}
		}
		if (!playerState.hasTacklezones() || (fFeedOnPlayerChoice != null)) {
			if ((fFeedOnPlayerChoice != null) && fFeedOnPlayerChoice && (game.getDefender() != null)) {
				FieldCoordinate feedOnPlayerCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
				InjuryResult injuryResultFeeding = UtilServerInjury.handleInjury(this, new InjuryTypeBitten(),
						actingPlayer.getPlayer(), game.getDefender(), feedOnPlayerCoordinate, null, null, ApothecaryMode.FEEDING);
				fEndTurn = UtilPlayer.hasBall(game, game.getDefender()); // turn end on biting the ball carrier
				publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultFeeding));
				publishParameters(UtilServerInjury.dropPlayer(this, game.getDefender(), ApothecaryMode.FEEDING));
				getResult().setSound(SoundId.SLURP);
				actingPlayer.setSufferingBloodLust(false);
				doNextStep = true;
			} else {
				fEndTurn = true;
				if (!playerState.isCasualty() && (playerState.getBase() != PlayerState.KNOCKED_OUT)
						&& (playerState.getBase() != PlayerState.RESERVE)) {
					if (playerCoordinate.equals(game.getFieldModel().getBallCoordinate())) {
						game.getFieldModel().setBallMoving(true);
						publishParameter(
								new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
					}
					game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), playerState.changeBase(PlayerState.RESERVE));
					UtilBox.putPlayerIntoBox(game, actingPlayer.getPlayer());
					getResult().addReport(new ReportBiteSpectator(actingPlayer.getPlayerId()));
				}
				doNextStep = true;
			}
		}
		if (doNextStep) {
			actingPlayer.setHasFed(true);
			publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, fEndPlayerAction));
			publishParameter(new StepParameter(StepParameterKey.END_TURN, fEndTurn));
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
		IServerJsonOption.FEED_ON_PLAYER_CHOICE.addTo(jsonObject, fFeedOnPlayerChoice);
		IServerJsonOption.FEEDING_ALLOWED.addTo(jsonObject, fFeedingAllowed);
		IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
		return jsonObject;
	}

	@Override
	public StepInitFeeding initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(game, jsonObject);
		fFeedOnPlayerChoice = IServerJsonOption.FEED_ON_PLAYER_CHOICE.getFrom(game, jsonObject);
		fFeedingAllowed = IServerJsonOption.FEEDING_ALLOWED.getFrom(game, jsonObject);
		fEndTurn = IServerJsonOption.END_TURN.getFrom(game, jsonObject);
		return this;
	}

}
