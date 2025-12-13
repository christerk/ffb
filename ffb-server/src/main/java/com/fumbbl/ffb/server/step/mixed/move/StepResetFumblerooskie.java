package com.fumbbl.ffb.server.step.mixed.move;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.report.bb2020.ReportFumblerooskie;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepResetFumblerooskie extends AbstractStep {

	private boolean resetForFailedBlock, endPlayerAction, inSelect;

	public StepResetFumblerooskie(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.RESET_FUMBLEROOSKIE;
	}

	@Override
	public void init(StepParameterSet parameterSet) {
		if (parameterSet != null) {
			Arrays.stream(parameterSet.values()).forEach(parameter -> {
				switch (parameter.getKey()) {
					case RESET_FOR_FAILED_BLOCK:
						resetForFailedBlock = toPrimitive((Boolean) parameter.getValue());
						break;
					case END_PLAYER_ACTION:
						endPlayerAction = toPrimitive((Boolean) parameter.getValue());
						break;
					case IN_SELECT:
						inSelect = toPrimitive((Boolean) parameter.getValue());
						break;
					default:
						break;
				}
			});
		}
		super.init(parameterSet);
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
				case END_PLAYER_ACTION:
					endPlayerAction = toPrimitive((Boolean) parameter.getValue());
					return true;
				case END_TURN:
					endPlayerAction = toPrimitive((Boolean) parameter.getValue());
					return true;
				default:
					return false;
			}
		}
		return false;
	}

	@Override
	public void start() {
		super.start();

		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldModel fieldModel = game.getFieldModel();

		if (!actingPlayer.isJumping()) {

			if (actingPlayer.isFumblerooskiePending()
				&& fieldModel.isBallMoving()
				&& fieldModel.getBallCoordinate().equals(fieldModel.getPlayerCoordinate(actingPlayer.getPlayer()))
			) {

				boolean ballCarrierStanding = fieldModel.getPlayerState(actingPlayer.getPlayer()).canBeBlocked();

				if (resetForFailedBlock && !ballCarrierStanding) {
					// we have to publish this here since during drop players the player did not have the ball
					publishParameter(StepParameter.from(StepParameterKey.DROPPED_BALL_CARRIER, actingPlayer.getPlayer().getId()));
				}

				if (endPlayerAction
					|| !ballCarrierStanding // reset if player fell down to trigger bounce
					|| !UtilPlayer.isNextMovePossible(game, actingPlayer.isJumping()) // do not reset if player can move on
				) {
					fieldModel.setBallMoving(false);
				}

				if (endPlayerAction
					|| (ballCarrierStanding && !UtilPlayer.isNextMovePossible(game, actingPlayer.isJumping()))
				) {
					getResult().setSound(SoundId.PICKUP);
					getResult().addReport(new ReportFumblerooskie(actingPlayer.getPlayerId(), false));
				}
			}

			if (!fieldModel.isBallMoving()) {
				actingPlayer.setFumblerooskiePending(false);
			}
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.RESET_FOR_FAILED_BLOCK.addTo(jsonObject, resetForFailedBlock);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, endPlayerAction);
		IServerJsonOption.IN_SELECT.addTo(jsonObject, inSelect);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		endPlayerAction = toPrimitive(IServerJsonOption.END_PLAYER_ACTION.getFrom(source, jsonObject));
		resetForFailedBlock = IServerJsonOption.RESET_FOR_FAILED_BLOCK.getFrom(source, jsonObject);
		inSelect = toPrimitive(IServerJsonOption.IN_SELECT.getFrom(source, jsonObject));
		return this;
	}
}
