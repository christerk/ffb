package com.fumbbl.ffb.server.step.bb2016.end;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.CardEffect;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.SeriousInjuryFactory;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.util.StringTool;

/**
 * Initialization step in end game sequence.
 *
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END. May be
 * initialized with stepParameter ADMIN_MODE.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
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
	public StepInitEndGame initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		Boolean adminMode = IServerJsonOption.ADMIN_MODE.getFrom(source, jsonObject);
		fAdminMode = (adminMode != null) ? adminMode : false;
		fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(source, jsonObject);
		return this;
	}

}
