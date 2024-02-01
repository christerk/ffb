package com.fumbbl.ffb.server.step.game.end;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.GameStatus;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogParameterFactory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameCache;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.ServerMode;
import com.fumbbl.ffb.server.request.ServerRequestSaveReplay;
import com.fumbbl.ffb.server.request.fumbbl.FumbblRequestUploadResults;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.util.UtilServerDialog;

import java.util.Date;

/**
 * Final step in end game sequence.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public final class StepEndGame extends AbstractStep {

	public StepEndGame(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.END_GAME;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		GameCache gameCache = getGameState().getServer().getGameCache();
		if (game.getFinished() == null) {
			game.setFinished(new Date());
			getGameState().setStatus(GameStatus.FINISHED);
			getGameState().getStepStack().clear(); // clean up after ourselves
			gameCache.queueDbUpdate(getGameState(), true);
			IDialogParameter gameStatistics = new DialogParameterFactory().createDialogParameter(DialogId.GAME_STATISTICS);
			UtilServerDialog.showDialog(getGameState(), gameStatistics, false);
		}
		FantasyFootballServer server = getGameState().getServer();
		if (!game.isTesting()) {
			if (server.getMode() == ServerMode.FUMBBL) {
				server.getRequestProcessor().add(new FumbblRequestUploadResults(getGameState()));
			} else {
				server.getRequestProcessor().add(new ServerRequestSaveReplay(getGameState().getId()));
			}
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		return super.toJsonValue();
	}

	@Override
	public StepEndGame initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		return this;
	}

}
