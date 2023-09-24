package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandBloodlustAction;
import com.fumbbl.ffb.server.ActionStatus;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;

import java.util.Objects;

import static com.fumbbl.ffb.PlayerAction.*;

/**
 * Step in block sequence to handle blood lust.
 * <p>
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * <p>
 * Sets stepParameter MOVE_STACK for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class StepBloodLust extends AbstractStepWithReRoll {

	private final StepState state;

	public StepBloodLust(GameState pGameState) {
		super(pGameState);
		state = new StepState();

	}

	public StepId getId() {
		return StepId.BLOOD_LUST;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				// optional
				if (Objects.requireNonNull(parameter.getKey()) == StepParameterKey.GOTO_LABEL_ON_FAILURE) {
					state.goToLabelOnFailure = (String) parameter.getValue();
				}
			}
		}
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);

		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			if (pReceivedCommand.getId() == NetCommandId.CLIENT_BLOODLUST_ACTION) {
				if (((ClientCommandBloodlustAction) pReceivedCommand.getCommand()).isChange()) {
					state.bloodlustAction = getAlternateAction(getGameState().getGame().getActingPlayer().getPlayerAction());
				}
				commandStatus = StepCommandStatus.EXECUTE_STEP;
			}
		}

		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		getGameState().executeStepHooks(this, state);
	}

	private PlayerAction getAlternateAction(PlayerAction currentAction) {
		switch (currentAction) {
			case PASS:
				return PASS_MOVE;
			case HAND_OVER:
				return HAND_OVER_MOVE;
			case FOUL:
				return FOUL_MOVE;
			case STAND_UP_BLITZ:
				return BLITZ_SELECT;
			case THROW_TEAM_MATE:
				return THROW_TEAM_MATE_MOVE;
			case KICK_TEAM_MATE:
				return KICK_TEAM_MATE_MOVE;
			default:
				return MOVE;
		}
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, state.goToLabelOnFailure);
		if (state.status != null) {
			IServerJsonOption.STATUS.addTo(jsonObject, state.status.name());
		}
		if (state.bloodlustAction != null) {
			IServerJsonOption.PLAYER_ACTION.addTo(jsonObject, state.bloodlustAction);
		}
		return jsonObject;
	}

	// JSON serialization

	@Override
	public StepBloodLust initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		state.goToLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		if (IServerJsonOption.STATUS.isDefinedIn(jsonObject)) {
			state.status = ActionStatus.valueOf(IServerJsonOption.STATUS.getFrom(source, jsonObject));
		}
		if (IServerJsonOption.PLAYER_ACTION.isDefinedIn(jsonObject)) {
			state.bloodlustAction = (PlayerAction) IJsonOption.PLAYER_ACTION.getFrom(source, jsonObject);
		}
		return this;
	}

	public static class StepState {
		public ActionStatus status;
		public String goToLabelOnFailure;
		public PlayerAction bloodlustAction;
	}

}
