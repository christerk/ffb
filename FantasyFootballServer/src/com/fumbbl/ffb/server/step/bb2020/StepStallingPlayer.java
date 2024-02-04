package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.bb2020.ReportThrowAtStallingPlayer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeThrowARockStalling;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerInjury;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepStallingPlayer extends AbstractStep {
	private String playerId;

	public StepStallingPlayer(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.STALLING_PLAYER;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		super.init(pParameterSet);
		for (StepParameter parameter : pParameterSet.values()) {
			if (parameter.getKey() == StepParameterKey.PLAYER_ID) {
				playerId = (String) parameter.getValue();
			}
		}
	}

	@Override
	public void start() {
		Game game = getGameState().getGame();

		int roll = getGameState().getDiceRoller().rollDice(6);

		boolean successful = roll >= 5;

		getResult().addReport(new ReportThrowAtStallingPlayer(playerId, roll, successful));

		if (successful) {
			Player<?> player = game.getPlayerById(playerId);
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);

			FieldCoordinate startCoordinate;
			if (FieldCoordinateBounds.UPPER_HALF.isInBounds(playerCoordinate)) {
				startCoordinate = new FieldCoordinate(getGameState().getDiceRoller().rollXCoordinate(), 0);
			} else {
				startCoordinate = new FieldCoordinate(getGameState().getDiceRoller().rollXCoordinate(), 14);
			}

			getResult().setAnimation(new Animation(AnimationType.THROW_A_ROCK, startCoordinate, playerCoordinate, null));
			UtilServerGame.syncGameModel(this);

			StepParameterSet pParameterSet = UtilServerInjury.dropPlayer(this, player, ApothecaryMode.HIT_PLAYER, true);
			pParameterSet.remove(StepParameterKey.END_TURN);
			publishParameters(pParameterSet);
			publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, UtilServerInjury.handleInjury(this,
				new InjuryTypeThrowARockStalling(), null, player, playerCoordinate, null, null, ApothecaryMode.HIT_PLAYER)));

		}

		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		playerId = IServerJsonOption.PLAYER_ID.getFrom(source, UtilJson.toJsonObject(jsonValue));
		return this;
	}
}
