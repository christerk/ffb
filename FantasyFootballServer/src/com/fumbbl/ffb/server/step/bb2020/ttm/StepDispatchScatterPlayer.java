package com.fumbbl.ffb.server.step.bb2020.ttm;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.ScatterPlayer;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepDispatchScatterPlayer extends AbstractStep {
	private String thrownPlayerId;
	private PlayerState thrownPlayerState;
	private boolean thrownPlayerHasBall = false, isKickedPlayer;
	private PassResult passResult = PassResult.FUMBLE;

	public StepDispatchScatterPlayer(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.DISPATCH_SCATTER_PLAYER;
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
				case THROWN_PLAYER_HAS_BALL:
					thrownPlayerHasBall = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
					return true;
				case PASS_RESULT:
					passResult = (PassResult) pParameter.getValue();
					return true;
				case IS_KICKED_PLAYER:
					isKickedPlayer = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
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
		Player<?> thrownPlayer = game.getPlayerById(thrownPlayerId);
		boolean scattersSingleDirection = thrownPlayer != null
			&& thrownPlayer.hasSkillProperty(NamedProperties.ttmScattersInSingleDirection);
		SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);

		boolean throwScatter, deviate;

		switch (passResult) {
			case FUMBLE:
				throwScatter = false;
				deviate = false;
				scattersSingleDirection = false;
				break;
			case WILDLY_INACCURATE:
				throwScatter = false;
				deviate = true;
				scattersSingleDirection = false;
				break;
			case INACCURATE:
			case ACCURATE:
				throwScatter = true;
				deviate = false;
				break;
			default:
				throw new IllegalStateException("Unexpected pass result for ttm: " + passResult.getName());
		}

		((ScatterPlayer) factory.forName(SequenceGenerator.Type.ScatterPlayer.name()))
			.pushSequence(new ScatterPlayer.SequenceParams(getGameState(), thrownPlayerId,
				thrownPlayerState, thrownPlayerHasBall, throwerCoordinate, scattersSingleDirection,
				throwScatter, deviate, !thrownPlayerState.hasTacklezones()));
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.THROWN_PLAYER_ID.addTo(jsonObject, thrownPlayerId);
		IServerJsonOption.THROWN_PLAYER_STATE.addTo(jsonObject, thrownPlayerState);
		IServerJsonOption.THROWN_PLAYER_HAS_BALL.addTo(jsonObject, thrownPlayerHasBall);
		IServerJsonOption.PASS_RESULT.addTo(jsonObject, passResult);
		IServerJsonOption.IS_KICKED_PLAYER.addTo(jsonObject, isKickedPlayer);
		return jsonObject;
	}

	@Override
	public StepDispatchScatterPlayer initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		thrownPlayerId = IServerJsonOption.THROWN_PLAYER_ID.getFrom(source, jsonObject);
		thrownPlayerState = IServerJsonOption.THROWN_PLAYER_STATE.getFrom(source, jsonObject);
		thrownPlayerHasBall = IServerJsonOption.THROWN_PLAYER_HAS_BALL.getFrom(source, jsonObject);
		passResult = (PassResult) IServerJsonOption.PASS_RESULT.getFrom(source, jsonObject);
		isKickedPlayer = IServerJsonOption.IS_KICKED_PLAYER.getFrom(source, jsonObject);
		return this;
	}
}
