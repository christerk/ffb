package com.balancedbytes.games.ffb.server.step.game.end;

import java.util.Date;

import com.balancedbytes.games.ffb.GameStatus;
import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogParameterFactory;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameCache;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.ServerMode;
import com.balancedbytes.games.ffb.server.request.ServerRequestSaveReplay;
import com.balancedbytes.games.ffb.server.request.fumbbl.FumbblRequestUploadResults;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

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
	public StepEndGame initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		return this;
	}

}
