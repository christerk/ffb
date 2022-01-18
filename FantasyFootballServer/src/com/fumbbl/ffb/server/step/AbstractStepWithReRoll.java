package com.fumbbl.ffb.server.step;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;

/**
 * 
 * @author Kalimar
 */
public abstract class AbstractStepWithReRoll extends AbstractStep {

	private ReRolledAction fReRolledAction;
	private ReRollSource fReRollSource;

	public AbstractStepWithReRoll(GameState pGameState) {
		super(pGameState);
	}

	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			if (pReceivedCommand.getId() == NetCommandId.CLIENT_USE_RE_ROLL) {
				ClientCommandUseReRoll useReRollCommand = (ClientCommandUseReRoll) pReceivedCommand.getCommand();
				setReRolledAction(useReRollCommand.getReRolledAction());
				setReRollSource(useReRollCommand.getReRollSource());
				commandStatus = StepCommandStatus.EXECUTE_STEP;
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
	public AbstractStepWithReRoll initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fReRolledAction = (ReRolledAction) IServerJsonOption.RE_ROLLED_ACTION.getFrom(source, jsonObject);
		fReRollSource = (ReRollSource) IServerJsonOption.RE_ROLL_SOURCE.getFrom(source, jsonObject);
		return this;
	}

}
