package com.fumbbl.ffb.server.step.bb2020.foul;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.dialog.DialogUseChainsawParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandUseChainsaw;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.util.StringTool;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepPileDriver extends AbstractStep {

	private String targetPlayerId, gotoLabelEnd;
	private boolean usingChainsaw;
	private Phase phase = Phase.SELECT_CHAINSAW;

	public StepPileDriver(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public void init(StepParameterSet parameterSet) {
		if (parameterSet != null) {
			for (StepParameter parameter : parameterSet.values()) {
				if (parameter.getValue() != null) {
					switch (parameter.getKey()) {
						case PLAYER_ID:
							targetPlayerId = (String) parameter.getValue();
							break;
						case GOTO_LABEL_ON_END:
							gotoLabelEnd = (String) parameter.getValue();
							break;
					}
				}
			}
		}
		super.init(parameterSet);

		if (!StringTool.isProvided(gotoLabelEnd)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_END + " is not initialized.");
		}

		if (!StringTool.isProvided(targetPlayerId)) {
			throw new StepException("StepParameter " + StepParameterKey.PLAYER_ID + " is not initialized.");
		}
	}

	@Override
	public StepId getId() {
		return StepId.PILE_DRIVER;
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand receivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(receivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND && UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), receivedCommand)) {
			if (receivedCommand.getId() == NetCommandId.CLIENT_USE_CHAINSAW) {
				ClientCommandUseChainsaw commandUseChainsaw = (ClientCommandUseChainsaw) receivedCommand.getCommand();
				usingChainsaw = commandUseChainsaw.isUsingChainsaw();
				phase = Phase.DONE;
				commandStatus = StepCommandStatus.EXECUTE_STEP;
			}
		}
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
		Game game = getGameState().getGame();
		switch (phase) {
			case SELECT_CHAINSAW:
				if (targetPlayerId == null) {
					getResult().setNextAction(StepAction.GOTO_LABEL, gotoLabelEnd);
				} else {
					Player<?> player = game.getActingPlayer().getPlayer();
					if (player.hasSkillProperty(NamedProperties.blocksLikeChainsaw)) {
						UtilServerDialog.showDialog(getGameState(), new DialogUseChainsawParameter(game.getActingTeam().getId()), false);
					} else {
						leaveStep();
					}
				}
				break;
			case DONE:
				leaveStep();
				break;
			default:
				break;
		}

	}

	private void leaveStep() {
		getGameState().getGame().setDefenderId(targetPlayerId);
		publishParameter(StepParameter.from(StepParameterKey.USING_CHAINSAW, usingChainsaw));
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, targetPlayerId);
		IServerJsonOption.STEP_PHASE.addTo(jsonObject, phase.name());
		IServerJsonOption.USING_CHAINSAW.addTo(jsonObject, usingChainsaw);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		targetPlayerId = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		phase = Phase.valueOf(IServerJsonOption.STEP_PHASE.getFrom(source, jsonObject));
		usingChainsaw = IServerJsonOption.USING_CHAINSAW.getFrom(source, jsonObject);
		return this;
	}

	private enum Phase {
		SELECT_CHAINSAW, DONE
	}
}
