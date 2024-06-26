package com.fumbbl.ffb.server.step.bb2016.start;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.dialog.DialogPettyCashParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.net.commands.ClientCommandPettyCash;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.report.ReportPettyCash;
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
 * Step in start game sequence to handle petty cash.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public final class StepPettyCash extends AbstractStep {

	private boolean fPettyCashSelectedHome;
	private boolean fPettyCashSelectedAway;
	private boolean fReportedHome;
	private boolean fReportedAway;

	public StepPettyCash(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.PETTY_CASH;
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
				case CLIENT_PETTY_CASH:
					ClientCommandPettyCash pettyCashCommand = (ClientCommandPettyCash) pReceivedCommand.getCommand();
					Game game = getGameState().getGame();
					GameResult gameResult = game.getGameResult();
					if (UtilServerSteps.checkCommandIsFromHomePlayer(getGameState(), pReceivedCommand)) {
						gameResult.getTeamResultHome().setPettyCashTransferred(normalizePettyCash(pettyCashCommand.getPettyCash(), game.getTeamHome().getTreasury()));
						fPettyCashSelectedHome = true;
					} else {
						gameResult.getTeamResultAway().setPettyCashTransferred(normalizePettyCash(pettyCashCommand.getPettyCash(), game.getTeamAway().getTreasury()));
						fPettyCashSelectedAway = true;
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

	private int normalizePettyCash(int enteredValue, int maxTreasury) {
		return Math.max(0, Math.min(enteredValue, maxTreasury));
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		GameResult gameResult = game.getGameResult();
		gameResult.getTeamResultHome()
			.setTeamValue(Math.max(gameResult.getTeamResultHome().getTeamValue(), game.getTeamHome().getTeamValue()));
		gameResult.getTeamResultAway()
			.setTeamValue(Math.max(gameResult.getTeamResultAway().getTeamValue(), game.getTeamAway().getTeamValue()));
		if (!UtilGameOption.isOptionEnabled(game, GameOptionId.PETTY_CASH)) {
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}
		if (UtilGameOption.isOptionEnabled(game, GameOptionId.FORCE_TREASURY_TO_PETTY_CASH)) {
			gameResult.getTeamResultHome().setPettyCashTransferred(game.getTeamHome().getTreasury());
			if (game.getTeamAway().getTeamValue() > game.getTeamHome().getTeamValue()) {
				gameResult.getTeamResultHome().setPettyCashTransferred(gameResult.getTeamResultHome().getPettyCashTransferred()
					+ (game.getTeamAway().getTeamValue() - game.getTeamHome().getTeamValue()));
			}
			fPettyCashSelectedHome = true;
			gameResult.getTeamResultAway().setPettyCashTransferred(game.getTeamAway().getTreasury());
			if (game.getTeamHome().getTeamValue() > game.getTeamAway().getTeamValue()) {
				gameResult.getTeamResultAway().setPettyCashTransferred(gameResult.getTeamResultAway().getPettyCashTransferred()
						+ (game.getTeamHome().getTeamValue() - game.getTeamAway().getTeamValue()));
			}
			fPettyCashSelectedAway = true;
		}
		if (!fPettyCashSelectedHome && ((game.getTeamHome().getTreasury() < 50000)
				|| (fPettyCashSelectedAway && ((game.getTeamAway().getTeamValue() - game.getTeamHome().getTeamValue()) > game
						.getTeamHome().getTreasury())))) {
			fPettyCashSelectedHome = true;
		}
		if (!fPettyCashSelectedAway && ((game.getTeamAway().getTreasury() < 50000)
				|| (fPettyCashSelectedHome && ((game.getTeamHome().getTeamValue() - game.getTeamAway().getTeamValue()) > game
						.getTeamAway().getTreasury())))) {
			fPettyCashSelectedAway = true;
		}
		if (fPettyCashSelectedHome && !fReportedHome) {
			if (UtilGameOption.isOptionEnabled(game, GameOptionId.PETTY_CASH_AFFECTS_TV)) {
				gameResult.getTeamResultHome().setTeamValue(
						gameResult.getTeamResultHome().getTeamValue() + gameResult.getTeamResultHome().getPettyCashTransferred());
			}
			getResult().addReport(
					new ReportPettyCash(game.getTeamHome().getId(), gameResult.getTeamResultHome().getPettyCashTransferred()));
			fReportedHome = true;
		}
		if (fPettyCashSelectedAway && !fReportedAway) {
			if (UtilGameOption.isOptionEnabled(game, GameOptionId.PETTY_CASH_AFFECTS_TV)) {
				gameResult.getTeamResultAway().setTeamValue(
						gameResult.getTeamResultAway().getTeamValue() + gameResult.getTeamResultAway().getPettyCashTransferred());
			}
			getResult().addReport(
					new ReportPettyCash(game.getTeamAway().getId(), gameResult.getTeamResultAway().getPettyCashTransferred()));
			fReportedAway = true;
		}
		if (!fPettyCashSelectedHome && !fPettyCashSelectedAway) {
			if (game.getTeamHome().getTeamValue() >= game.getTeamAway().getTeamValue()) {
				UtilServerDialog.showDialog(getGameState(), new DialogPettyCashParameter(game.getTeamHome().getId(),
						game.getTeamHome().getTeamValue(), game.getTeamHome().getTreasury(), game.getTeamAway().getTeamValue()),
						false);
			}
			if (game.getTeamAway().getTeamValue() > game.getTeamHome().getTeamValue()) {
				UtilServerDialog.showDialog(getGameState(), new DialogPettyCashParameter(game.getTeamAway().getId(),
						game.getTeamAway().getTeamValue(), game.getTeamAway().getTreasury(), game.getTeamHome().getTeamValue()),
						false);
			}
		} else if (!fPettyCashSelectedHome) {
			UtilServerDialog.showDialog(getGameState(),
					new DialogPettyCashParameter(game.getTeamHome().getId(), gameResult.getTeamResultHome().getTeamValue(),
							game.getTeamHome().getTreasury(), gameResult.getTeamResultAway().getTeamValue()),
					false);
		} else if (!fPettyCashSelectedAway) {
			UtilServerDialog.showDialog(getGameState(),
					new DialogPettyCashParameter(game.getTeamAway().getId(), gameResult.getTeamResultAway().getTeamValue(),
							game.getTeamAway().getTreasury(), gameResult.getTeamResultHome().getTeamValue()),
					false);
		} else {
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.PETTY_CASH_SELECTED_HOME.addTo(jsonObject, fPettyCashSelectedHome);
		IServerJsonOption.PETTY_CASH_SELECTED_AWAY.addTo(jsonObject, fPettyCashSelectedAway);
		IServerJsonOption.REPORTED_HOME.addTo(jsonObject, fReportedHome);
		IServerJsonOption.REPORTED_AWAY.addTo(jsonObject, fReportedAway);
		return jsonObject;
	}

	@Override
	public StepPettyCash initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fPettyCashSelectedHome = IServerJsonOption.PETTY_CASH_SELECTED_HOME.getFrom(source, jsonObject);
		fPettyCashSelectedAway = IServerJsonOption.PETTY_CASH_SELECTED_AWAY.getFrom(source, jsonObject);
		fReportedHome = IServerJsonOption.REPORTED_HOME.getFrom(source, jsonObject);
		fReportedAway = IServerJsonOption.REPORTED_AWAY.getFrom(source, jsonObject);
		return this;
	}

}
