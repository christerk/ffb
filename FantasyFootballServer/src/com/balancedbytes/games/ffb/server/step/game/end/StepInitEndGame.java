package com.balancedbytes.games.ffb.server.step.game.end;

import com.balancedbytes.games.ffb.CardEffect;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.factory.SeriousInjuryFactory;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Initialization step in end game sequence.
 *
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END. May be
 * initialized with stepParameter ADMIN_MODE.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public final class StepInitEndGame extends AbstractStep {

	private String fGotoLabelOnEnd;
	private boolean fAdminMode;

	public StepInitEndGame(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.INIT_END_GAME;
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
				// optional
				case ADMIN_MODE:
					fAdminMode = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
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

	private void executeStep() {
		Game game = getGameState().getGame();
		if (game.getFinished() != null) {
			getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
			return;
		}
		GameResult gameResult = game.getGameResult();
		if (gameResult.getTeamResultHome().hasConceded()) {
			int scoreDiffAway = gameResult.getTeamResultAway().getScore() - gameResult.getTeamResultHome().getScore();
			if (scoreDiffAway <= 0) {
				gameResult.getTeamResultAway()
						.setScore(gameResult.getTeamResultAway().getScore() + Math.abs(scoreDiffAway) + 1);
			}
		}
		if (gameResult.getTeamResultAway().hasConceded()) {
			int scoreDiffHome = gameResult.getTeamResultHome().getScore() - gameResult.getTeamResultAway().getScore();
			if (scoreDiffHome <= 0) {
				gameResult.getTeamResultHome()
						.setScore(gameResult.getTeamResultHome().getScore() + Math.abs(scoreDiffHome) + 1);
			}
		}
		game.setTurnMode(TurnMode.END_GAME);
		game.setConcessionPossible(false);
		game.setAdminMode(fAdminMode);
		handlePoisonedPlayers();
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private void handlePoisonedPlayers() {
		Game game = getGameState().getGame();
		SeriousInjuryFactory factory = game.getFactory(FactoryType.Factory.SERIOUS_INJURY);
		for (Player<?> player : game.getPlayers()) {
			if (game.getFieldModel().hasCardEffect(player, CardEffect.POISONED)) {
				PlayerResult playerResult = game.getGameResult().getPlayerResult(player);
				if (playerResult.getSeriousInjury() == null) {
					playerResult.setSeriousInjury(factory.poison());
				}
				game.getFieldModel().removeCardEffect(player, CardEffect.POISONED);
			}
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.ADMIN_MODE.addTo(jsonObject, fAdminMode);
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
		return jsonObject;
	}

	@Override
	public StepInitEndGame initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		Boolean adminMode = IServerJsonOption.ADMIN_MODE.getFrom(game, jsonObject);
		fAdminMode = (adminMode != null) ? adminMode : false;
		fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(game, jsonObject);
		return this;
	}

}
