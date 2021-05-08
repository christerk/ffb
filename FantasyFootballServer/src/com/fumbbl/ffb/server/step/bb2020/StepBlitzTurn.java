package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.BlitzTurnState;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.report.bb2020.ReportBlitzRoll;
import com.fumbbl.ffb.report.bb2020.ReportKickoffSequenceActivationsExhausted;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Select;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.phase.kickoff.UtilKickoffSequence;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerTimer;

import java.util.Arrays;

/**
 * Step in kickoff sequence to handle blitz kickoff result.
 * 
 * Expects stepParameter END_TURN to be set by a preceding step. (parameter is
 * consumed on TurnMode.BLITZ)
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public final class StepBlitzTurn extends AbstractStep {

	private boolean fEndTurn;

	public StepBlitzTurn(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.BLITZ_TURN;
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		Game game = getGameState().getGame();
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			if (pParameter.getKey() == StepParameterKey.END_TURN) {
				fEndTurn = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
				if (game.getTurnMode() == TurnMode.BLITZ) {
					consume(pParameter);
				}
				return true;
			}
		}
		return false;
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

		Game game = getGameState().getGame();

		if (game.getTurnMode() == TurnMode.BLITZ) {

			if (fEndTurn) {
				getGameState().setBlitzTurnState(null);
				game.setTurnMode(TurnMode.KICKOFF);
			}

		} else {



			Team blitzingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
			UtilKickoffSequence.pinPlayersInTacklezones(getGameState(), blitzingTeam);

			int availablePlayers = (int) Arrays.stream(blitzingTeam.getPlayers())
				.filter(player -> game.getFieldModel().getPlayerState(player).isActive()).count();

			if (availablePlayers == 0) {
				getResult().addReport(new ReportKickoffSequenceActivationsExhausted(false));
			} else {
				int roll = getGameState().getDiceRoller().rollDice(3);
				int limit = roll + 3;
				game.setTurnMode(TurnMode.BLITZ);
				getGameState().setBlitzTurnState(new BlitzTurnState(limit, availablePlayers));
				long currentTimeMillis = System.currentTimeMillis();
				if (game.isTurnTimeEnabled()) {
					UtilServerTimer.stopTurnTimer(getGameState(), currentTimeMillis);
					game.setTurnTime(0);
					UtilServerTimer.startTurnTimer(getGameState(), currentTimeMillis);
				}
				game.startTurn();
				UtilServerGame.updateLeaderReRolls(this);
				// insert select sequence into kickoff sequence after this step
				getGameState().pushCurrentStepOnStack();
				SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
				((Select) factory.forName(SequenceGenerator.Type.Select.name()))
					.pushSequence(new Select.SequenceParams(getGameState(), true));
				getResult().addReport(new ReportBlitzRoll(blitzingTeam.getId(), roll, limit));
			}
		}

		getResult().setNextAction(StepAction.NEXT_STEP);

	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
		return jsonObject;
	}

	@Override
	public StepBlitzTurn initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fEndTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		return this;
	}

}
