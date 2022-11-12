package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepAnimalSavagery extends AbstractStepWithReRoll {

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				// mandatory
				switch (parameter.getKey()) {
					case GOTO_LABEL_ON_FAILURE:
						state.goToLabelOnFailure = (String) parameter.getValue();
						break;
					case TARGET_COORDINATE:
						Player<?> catcher = getGameState().getGame().getFieldModel().getPlayer((FieldCoordinate) parameter.getValue());
						if (catcher != null) {
							state.catcherId = catcher.getId();
						}
						break;
					default:
						break;
				}
			}
		}
		if (state.goToLabelOnFailure == null) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_FAILURE + " is not initialized.");
		}
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null) {
			switch (parameter.getKey()) {
				case THROWN_PLAYER_ID:
					state.thrownPlayerId = (String) parameter.getValue();
					break;
				case END_TURN:
					state.endTurn = (boolean) parameter.getValue();
					break;
				default:
					break;
			}
		}

		return super.setParameter(parameter);
	}

	private final StepState state;

	public StepAnimalSavagery(GameState pGameState) {
		super(pGameState);
		state = new StepState();
	}

	public StepId getId() {
		return StepId.ANIMAL_SAVAGERY;
	}

	@Override
	public StepAnimalSavagery initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		state.goToLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		state.playerId = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		state.thrownPlayerId = IServerJsonOption.THROWN_PLAYER_ID.getFrom(source, jsonObject);
		String[] playerArray = IServerJsonOption.PLAYER_IDS.getFrom(source, jsonObject);
		if (playerArray != null) {
			state.playerIds = Arrays.stream(playerArray).collect(Collectors.toSet());
		}
		state.endTurn = toPrimitive(IServerJsonOption.END_TURN.getFrom(source, jsonObject));
		state.catcherId = IServerJsonOption.CATCHER_ID.getFrom(source, jsonObject);
		state.attackOpponent = IServerJsonOption.ATTACK_OPPONENT.getFrom(source, jsonObject);
		return this;
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
			switch (pReceivedCommand.getId()) {
				case CLIENT_PLAYER_CHOICE:
					ClientCommandPlayerChoice playerChoiceCommand = (ClientCommandPlayerChoice) pReceivedCommand.getCommand();
					if (PlayerChoiceMode.ANIMAL_SAVAGERY == playerChoiceCommand.getPlayerChoiceMode()) {
						state.playerId = playerChoiceCommand.getPlayerId();
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_USE_SKILL:
					ClientCommandUseSkill commandUseSkill = (ClientCommandUseSkill) pReceivedCommand.getCommand();
					Skill skill = commandUseSkill.getSkill();
					if (skill.hasSkillProperty(NamedProperties.canLashOutAgainstOpponents)) {
						boolean skillUsed = commandUseSkill.isSkillUsed();
						state.attackOpponent = skillUsed;
						if (skillUsed) {
							Game game = getGameState().getGame();
							game.getPlayerById(commandUseSkill.getPlayerId()).markUsed(skill, game);
						}
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
		getGameState().executeStepHooks(this, state);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, state.goToLabelOnFailure);
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, state.playerId);
		IServerJsonOption.PLAYER_IDS.addTo(jsonObject, state.playerIds);
		IServerJsonOption.THROWN_PLAYER_ID.addTo(jsonObject, state.thrownPlayerId);
		IServerJsonOption.END_TURN.addTo(jsonObject, state.endTurn);
		IServerJsonOption.CATCHER_ID.addTo(jsonObject, state.catcherId);
		IServerJsonOption.ATTACK_OPPONENT.addTo(jsonObject, state.attackOpponent);
		return jsonObject;
	}

	public static class StepState {
		public String goToLabelOnFailure;
		public String playerId;
		public String thrownPlayerId;
		public Set<String> playerIds;
		public boolean endTurn;
		public String catcherId;
		public Boolean attackOpponent;
	}


}
