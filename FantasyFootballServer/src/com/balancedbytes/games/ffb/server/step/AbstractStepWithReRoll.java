package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseReRoll;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public abstract class AbstractStepWithReRoll extends AbstractStep {

	private ReRolledAction fReRolledAction;
	private ReRollSource fReRollSource;

	protected AbstractStepWithReRoll(GameState pGameState) {
		super(pGameState);
	}

	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pReceivedCommand.getId()) {
			case CLIENT_USE_RE_ROLL:
				ClientCommandUseReRoll useReRollCommand = (ClientCommandUseReRoll) pReceivedCommand.getCommand();
				setReRolledAction(useReRollCommand.getReRolledAction());
				setReRollSource(useReRollCommand.getReRollSource());
				commandStatus = StepCommandStatus.EXECUTE_STEP;
				break;
			default:
				break;
			}
		}
		return commandStatus;
	}

	public ReRolledAction getReRolledAction() {
		return fReRolledAction;
	}

	public void setReRolledAction(ReRolledAction pReRolledAction) {
		fReRolledAction = pReRolledAction;
	}

	public ReRollSource getReRollSource() {
		return fReRollSource;
	}

	public void setReRollSource(ReRollSource pReRollSource) {
		fReRollSource = pReRollSource;
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.RE_ROLLED_ACTION.addTo(jsonObject, fReRolledAction);
		IServerJsonOption.RE_ROLL_SOURCE.addTo(jsonObject, fReRollSource);
		return jsonObject;
	}

	@Override
	public AbstractStepWithReRoll initFrom(JsonValue pJsonValue) {
		super.initFrom(pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fReRolledAction = (ReRolledAction) IServerJsonOption.RE_ROLLED_ACTION.getFrom(jsonObject);
		fReRollSource = (ReRollSource) IServerJsonOption.RE_ROLL_SOURCE.getFrom(jsonObject);
		return this;
	}

}
