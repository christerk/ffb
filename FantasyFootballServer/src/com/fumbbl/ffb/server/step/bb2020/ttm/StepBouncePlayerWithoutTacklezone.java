package com.fumbbl.ffb.server.step.bb2020.ttm;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.common.ScatterPlayer;

import java.util.Arrays;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepBouncePlayerWithoutTacklezone extends AbstractStep {
	private String thrownPlayerId, goToOnFailure;
	private PlayerState thrownPlayerState;

	public StepBouncePlayerWithoutTacklezone(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.BOUNCE_PLAYER_WITHOUT_TACKLEZONE;
	}

	@Override
	public void init(StepParameterSet parameterSet) {
		if (parameterSet != null) {
			Arrays.stream(parameterSet.values()).forEach(parameter -> {
				if (parameter.getKey() == StepParameterKey.GOTO_LABEL_ON_FAILURE) {
					goToOnFailure = (String) parameter.getValue();
				}
			});
		}
		super.init(parameterSet);
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
				case THROWN_PLAYER_ID:
					thrownPlayerId = (String) pParameter.getValue();
					return true;
				case THROWN_PLAYER_STATE:
					thrownPlayerState = (PlayerState) pParameter.getValue();
					return true;
				default:
					break;
			}
		}
		return false;
	}

	@Override
	public void start() {
		Game game = getGameState().getGame();
		Player<?> thrower = game.getActingPlayer().getPlayer();
		FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(thrower);

		SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);

		if (!thrownPlayerState.hasTacklezones()) {
			((ScatterPlayer) factory.forName(SequenceGenerator.Type.ScatterPlayer.name()))
				.pushSequence(new ScatterPlayer.SequenceParams(getGameState(), thrownPlayerId,
					thrownPlayerState, false, throwerCoordinate, false,
					false));
			getResult().setNextAction(StepAction.GOTO_LABEL, goToOnFailure);

		} else {
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.THROWN_PLAYER_ID.addTo(jsonObject, thrownPlayerId);
		IServerJsonOption.THROWN_PLAYER_STATE.addTo(jsonObject, thrownPlayerState);
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, goToOnFailure);
		return jsonObject;
	}

	@Override
	public StepBouncePlayerWithoutTacklezone initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		thrownPlayerId = IServerJsonOption.THROWN_PLAYER_ID.getFrom(source, jsonObject);
		thrownPlayerState = IServerJsonOption.THROWN_PLAYER_STATE.getFrom(source, jsonObject);
		goToOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		return this;
	}
}
