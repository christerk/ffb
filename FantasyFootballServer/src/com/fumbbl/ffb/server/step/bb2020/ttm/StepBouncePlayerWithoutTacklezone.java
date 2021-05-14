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
	private FieldCoordinate thrownPlayerCoordinate;

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
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
				case THROWN_PLAYER_ID:
					thrownPlayerId = (String) parameter.getValue();
					return true;
				case THROWN_PLAYER_STATE:
					thrownPlayerState = (PlayerState) parameter.getValue();
					return true;
				case THROWN_PLAYER_COORDINATE:
					thrownPlayerCoordinate = (FieldCoordinate) parameter.getValue();
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

		SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);

		if (!thrownPlayerState.hasTacklezones()) {
			if (thrownPlayerCoordinate != null) {
				((ScatterPlayer) factory.forName(SequenceGenerator.Type.ScatterPlayer.name()))
					.pushSequence(new ScatterPlayer.SequenceParams(getGameState(), thrownPlayerId,
						thrownPlayerState, false, thrownPlayerCoordinate, false,
						false));
			}
			getResult().setNextAction(StepAction.NEXT_STEP);
		} else {
			getResult().setNextAction(StepAction.GOTO_LABEL, goToOnFailure);
		}
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.THROWN_PLAYER_ID.addTo(jsonObject, thrownPlayerId);
		IServerJsonOption.THROWN_PLAYER_STATE.addTo(jsonObject, thrownPlayerState);
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, goToOnFailure);
		IServerJsonOption.THROWN_PLAYER_COORDINATE.addTo(jsonObject, thrownPlayerCoordinate);
		return jsonObject;
	}

	@Override
	public StepBouncePlayerWithoutTacklezone initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		thrownPlayerId = IServerJsonOption.THROWN_PLAYER_ID.getFrom(source, jsonObject);
		thrownPlayerState = IServerJsonOption.THROWN_PLAYER_STATE.getFrom(source, jsonObject);
		goToOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		thrownPlayerCoordinate = IServerJsonOption.THROWN_PLAYER_COORDINATE.getFrom(source, jsonObject);
		return this;
	}
}
