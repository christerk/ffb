package com.fumbbl.ffb.server.step.mixed.end;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.dialog.DialogPenaltyShootoutParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.report.mixed.ReportPenaltyShootout;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.util.UtilServerDialog;

/**
 * Step in end game sequence to handle the penalty shootout.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public final class StepPenaltyShootout extends AbstractStep {

	private static final int SHOOTOUT_LIMIT = 5;

	public StepPenaltyShootout(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.PENALTY_SHOOTOUT;
	}

	private boolean homeConfirmed, awayConfirmed;

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {

		StepCommandStatus result = StepCommandStatus.UNHANDLED_COMMAND;

		if (pReceivedCommand.getId().equals(NetCommandId.CLIENT_CONFIRM)) {
			result = StepCommandStatus.SKIP_STEP;
			if (UtilServerSteps.checkCommandIsFromHomePlayer(getGameState(), pReceivedCommand)) {
				homeConfirmed = true;
			} else if (UtilServerSteps.checkCommandIsFromAwayPlayer(getGameState(), pReceivedCommand)) {
				awayConfirmed = true;
			}

			if (homeConfirmed && awayConfirmed) {
				getResult().setNextAction(StepAction.NEXT_STEP);
			}
		}

		return result;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		GameResult gameResult = game.getGameResult();
		if ((game.getHalf() > 2)
			&& (gameResult.getTeamResultHome().getScore() == gameResult.getTeamResultAway().getScore())) {
			DialogPenaltyShootoutParameter parameter = new DialogPenaltyShootoutParameter();
			int rollHome, penaltyScoreHome = 0;
			int rollAway, penaltyScoreAway = 0;
			while (penaltyScoreHome + penaltyScoreAway < SHOOTOUT_LIMIT) {
				int currentPenalty = penaltyScoreAway + penaltyScoreHome + 1;
				rollHome = getGameState().getDiceRoller().rollPenaltyShootout();
				rollAway = getGameState().getDiceRoller().rollPenaltyShootout();
				Boolean homeTeamWonPenalty = null;
				if (rollAway > rollHome) {
					homeTeamWonPenalty = false;
					penaltyScoreAway++;
				} else if (rollAway < rollHome) {
					homeTeamWonPenalty = true;
					penaltyScoreHome++;
				}

				String teamId = null;

				if (penaltyScoreHome + penaltyScoreAway == SHOOTOUT_LIMIT) {
					gameResult.getTeamResultHome().setPenaltyScore(penaltyScoreHome);
					gameResult.getTeamResultAway().setPenaltyScore(penaltyScoreAway);
					if (penaltyScoreHome > penaltyScoreAway) {
						teamId = game.getTeamHome().getId();
					} else {
						teamId = game.getTeamAway().getId();
					}
				}

				String round = toOrdinal(currentPenalty);

				if (homeTeamWonPenalty != null) {
					parameter.addShootout(rollHome, rollAway, homeTeamWonPenalty, round);
				}

				getResult().addReport(new ReportPenaltyShootout(rollHome, penaltyScoreHome, rollAway, penaltyScoreAway, homeTeamWonPenalty, round, teamId));
			}
			parameter.setAwayScore(penaltyScoreAway);
			parameter.setHomeScore(penaltyScoreHome);
			parameter.setWinningSound(SoundId.SPEC_CLAP);
			parameter.setLosingSound(SoundId.SPEC_AAH);
			parameter.setHomeTeamWins(penaltyScoreHome > penaltyScoreAway);
			UtilServerDialog.showDialog(getGameState(), parameter, false);
			getResult().setNextAction(StepAction.CONTINUE);
		} else {
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	private String toOrdinal(int number) {
		switch (number) {
			case 1:
				return "1st";
			case 2:
				return "2nd";
			case 3:
				return "3rd";
			default:
				return number + "th";
		}
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.CONFIRMED.addTo(jsonObject, homeConfirmed);
		IServerJsonOption.CONFIRMED_SECONDED.addTo(jsonObject, awayConfirmed);
		return jsonObject;
	}

	@Override
	public StepPenaltyShootout initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		homeConfirmed = IServerJsonOption.CONFIRMED.getFrom(source, jsonObject);
		awayConfirmed = IServerJsonOption.CONFIRMED_SECONDED.getFrom(source, jsonObject);
		return this;
	}

}
