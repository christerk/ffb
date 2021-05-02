package com.fumbbl.ffb.server.step.bb2020;

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
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;

import java.util.Arrays;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepResetFumblerooskie extends AbstractStep {

	private boolean checkPlayerAction;

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
					if (parameter.getKey() == StepParameterKey.CHECK_PLAYER_ACTION) {
						checkPlayerAction = parameter.getValue() != null && (boolean) parameter.getValue();
					}
				}
			);
		}
		super.init(parameterSet);
	}

	@Override
	public void start() {
		super.start();

		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldModel fieldModel = game.getFieldModel();

		if (actingPlayer.isFumblerooskiePending()
			&& fieldModel.isBallMoving()
			&& fieldModel.getBallCoordinate().equals(fieldModel.getPlayerCoordinate(actingPlayer.getPlayer()))
			&& (!checkPlayerAction || actingPlayer.getPlayerAction() == null)
		) {
			fieldModel.setBallMoving(false);
			getResult().setSound(SoundId.PICKUP);
			getResult().addReport(new ReportFumblerooskie(actingPlayer.getPlayerId(), false));
		}

		actingPlayer.setFumblerooskiePending(false);

	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.CHECK_PLAYER_ACTION.addTo(jsonObject, checkPlayerAction);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		checkPlayerAction = IServerJsonOption.CHECK_PLAYER_ACTION.getFrom(source, UtilJson.toJsonObject(pJsonValue));
		return this;
	}
}
