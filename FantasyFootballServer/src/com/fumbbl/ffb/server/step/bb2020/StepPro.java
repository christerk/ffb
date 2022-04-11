package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerReRoll;

import java.util.Arrays;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepPro extends AbstractStepWithReRoll {

	private String playerId;

	public StepPro(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.PRO;
	}

	@Override
	public void init(StepParameterSet parameterSet) {
		super.init(parameterSet);
		if (parameterSet != null) {
			Arrays.stream(parameterSet.values()).forEach(parameter -> {
				if (parameter.getKey() == StepParameterKey.PLAYER_ID) {
					playerId = (String) parameter.getValue();
				}
			});
		}
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);

		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}

		return commandStatus;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		getResult().setNextAction(StepAction.NEXT_STEP);
		Player<?> player = getGameState().getGame().getPlayerById(playerId);

		boolean doRoll = true;
		boolean successful = false;
		if (getReRollSource() != null) {
			if (UtilServerReRoll.useReRoll(this, getReRollSource(), player)) {
				Game game = getGameState().getGame();
				PlayerState playerState = game.getFieldModel().getPlayerState(player);

				game.getFieldModel().setPlayerState(player, playerState.changeUsedPro(false));
			} else {
				doRoll = false;
			}
		}

		GameMechanic mechanic = (GameMechanic) getGameState().getGame().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());

		if (doRoll) {
			successful = UtilServerReRoll.useReRoll(this, ReRollSources.PRO, player);
		}
		if (!successful && getReRolledAction() != ReRolledActions.OLD_PRO) {
			boolean askForReRoll = UtilServerReRoll.askForReRollIfAvailable(getGameState(), player, ReRolledActions.OLD_PRO, mechanic.minimumProRoll(), false);
			if (askForReRoll) {
				getResult().setNextAction(StepAction.CONTINUE);
			}
		} else {
			publishParameter(StepParameter.from(StepParameterKey.SUCCESSFUL_PRO, successful));
		}
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		return jsonObject;
	}

	@Override
	public AbstractStepWithReRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		playerId = IServerJsonOption.PLAYER_ID.getFrom(source, UtilJson.toJsonObject(jsonValue));
		return this;
	}
}
