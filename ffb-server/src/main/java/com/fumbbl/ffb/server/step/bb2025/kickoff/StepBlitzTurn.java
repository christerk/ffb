package com.fumbbl.ffb.server.step.bb2025.kickoff;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.model.BlitzTurnState;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.report.mixed.ReportKickoffSequenceActivationsExhausted;
import com.fumbbl.ffb.report.mixed.ReportBlitzRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.generator.Select;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.phase.kickoff.UtilKickoffSequence;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerTimer;

import java.util.Arrays;

/**
 * Step in kickoff sequence to handle blitz kickoff result.
 * <p>
 * Expects stepParameter END_TURN to be set by a preceding step. (parameter is
 * consumed on TurnMode.BLITZ)
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public final class StepBlitzTurn extends AbstractStep {

	public StepBlitzTurn(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.BLITZ_TURN;
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

			getGameState().setBlitzTurnState(null);
			game.setTurnMode(TurnMode.KICKOFF);


		} else {


			Team blitzingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
			UtilKickoffSequence.pinPlayersInTacklezones(getGameState(), blitzingTeam, true);

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
				UtilServerGame.updatePlayerStateDependentProperties(this);
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

}
