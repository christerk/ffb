package com.fumbbl.ffb.server.step.bb2025.foul;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandActingPlayer;
import com.fumbbl.ffb.net.commands.ClientCommandFoul;
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
import com.fumbbl.ffb.util.StringTool;

/**
 * Step to init the foul sequence.
 * <p>
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END. May be
 * initialized with stepParameter FOUL_DEFENDER_ID.
 * <p>
 * Sets stepParameter END_TURN for all steps on the stack. Sets stepParameter
 * END_PLAYER_ACTION for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepInitFouling extends AbstractStep {

	private String fGotoLabelOnEnd;
	private String fFoulDefenderId;
	private boolean fEndTurn;
	private boolean fEndPlayerAction;
	private boolean usingChainsaw;

	public StepInitFouling(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.INIT_FOULING;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
					// mandatory
					case GOTO_LABEL_ON_END:
						fGotoLabelOnEnd = (String) parameter.getValue();
						break;
					// optional
					case FOUL_DEFENDER_ID:
						fFoulDefenderId = (String) parameter.getValue();
						break;
					case USING_CHAINSAW:
						usingChainsaw = (parameter.getValue() != null) && (boolean) parameter.getValue();
					default:
						break;
				}
			}
		}
		if (!StringTool.isProvided(fGotoLabelOnEnd)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_END + " is not initialized.");
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
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND && UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
			switch (pReceivedCommand.getId()) {
				case CLIENT_FOUL:
					ClientCommandFoul foulCommand = (ClientCommandFoul) pReceivedCommand.getCommand();
					if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), foulCommand)) {
						fFoulDefenderId = foulCommand.getDefenderId();
						usingChainsaw = foulCommand.isUsingChainsaw();
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_ACTING_PLAYER:
					ClientCommandActingPlayer actingPlayerCommand = (ClientCommandActingPlayer) pReceivedCommand.getCommand();
					if (StringTool.isProvided(actingPlayerCommand.getPlayerId())) {
						UtilServerSteps.changePlayerAction(this, actingPlayerCommand.getPlayerId(),
							actingPlayerCommand.getPlayerAction(), actingPlayerCommand.isJumping());
					} else {
						fEndPlayerAction = true;
					}
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_END_TURN:
					if (UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
						fEndTurn = true;
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				default:
					break;
			}
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (fEndTurn) {
			publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
			publishParameter(StepParameter.from(StepParameterKey.CHECK_FORGO, true));
			getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
		} else if (fEndPlayerAction) {
			publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
			getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
		} else {
			Player<?> foulDefender = game.getPlayerById(fFoulDefenderId);
			if ((actingPlayer.getPlayer() != null) && !actingPlayer.hasFouled() && (foulDefender != null)
				&& !foulDefender.hasSkillProperty(NamedProperties.preventBeingFouled)) {
				game.setDefenderId(fFoulDefenderId);
				actingPlayer.setHasFouled(true);
				game.getTurnData().setTurnStarted(true);
				game.setConcessionPossible(false);
				PlayerResult playerResult = game.getGameResult().getPlayerResult(actingPlayer.getPlayer());
				playerResult.setFouls(playerResult.getFouls() + 1);
				if (!actingPlayer.getPlayer().hasSkillProperty(NamedProperties.allowsAdditionalFoul)) {
					game.getTurnData().setFoulUsed(true);
				}
				publishParameter(new StepParameter(StepParameterKey.USING_CHAINSAW, usingChainsaw));
				publishParameter(new StepParameter(StepParameterKey.BLOCK_DEFENDER_ID, fFoulDefenderId));
				getResult().setNextAction(StepAction.NEXT_STEP);
			}
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
		IServerJsonOption.FOUL_DEFENDER_ID.addTo(jsonObject, fFoulDefenderId);
		IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, fEndPlayerAction);
		IServerJsonOption.USING_CHAINSAW.addTo(jsonObject, usingChainsaw);
		return jsonObject;
	}

	@Override
	public StepInitFouling initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(source, jsonObject);
		fFoulDefenderId = IServerJsonOption.FOUL_DEFENDER_ID.getFrom(source, jsonObject);
		fEndTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		fEndPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(source, jsonObject);
		usingChainsaw = IServerJsonOption.USING_CHAINSAW.getFrom(source, jsonObject);
		return this;
	}

}
