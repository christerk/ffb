package com.fumbbl.ffb.server.step.bb2020.blitz;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;

import java.util.Arrays;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepRemoveTargetSelectionState extends AbstractStep {

	private boolean retainModelData;

	public StepRemoveTargetSelectionState(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.REMOVE_TARGET_SELECTION_STATE;
	}

	@Override
	public void init(StepParameterSet parameterSet) {
		super.init(parameterSet);
		if (parameterSet != null) {
			Arrays.stream(parameterSet.values()).forEach(parameter -> {
				if (parameter.getKey() == StepParameterKey.RETAIN_MODEL_DATA) {
					retainModelData = toPrimitive((Boolean) parameter.getValue());
				}
			});
		}
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		TargetSelectionState targetSelectionState = game.getFieldModel().getTargetSelectionState();
		if (targetSelectionState != null) {
			String playerId = targetSelectionState.getSelectedPlayerId();
			if (playerId != null) {
				Player<?> player = game.getPlayerById(playerId);
				if (player != null) {
					PlayerState playerState = game.getFieldModel().getPlayerState(player);
					if (playerState != null) {
						game.getFieldModel().setPlayerState(player, playerState.removeAllTargetSelections());
					}
				}
			}
			if (retainModelData) {
				// Keeping empty block here so this position can be found again more easily.
				// Before this change we removed the player id at this point which lead to FA not being triggered
				// anymore on the second frenzy block (see FA Behavior, it got null from the selection state and thus
				// no defender was found.
				// This was introduced with Frenzied Rush. It _should_ not cause other issues to leave the id, but we are not sure.

				// 				targetSelectionState.removePlayer();
			} else {
				markSkillsTrackedOutsideOfActivationAndRemoveEffects(game);
				if (targetSelectionState.isCommitted()) {
					game.getActingPlayer().setHasTriggeredEffect(true);
				}
				game.getFieldModel().setTargetSelectionState(null);
			}
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.RETAIN_MODEL_DATA.addTo(jsonObject, retainModelData);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		retainModelData = toPrimitive(IServerJsonOption.RETAIN_MODEL_DATA.getFrom(source, UtilJson.toJsonObject(jsonValue)));
		return this;
	}
}
