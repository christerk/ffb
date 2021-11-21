package com.fumbbl.ffb.server.step.bb2020.multiblock;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.util.UtilCards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepDoubleStrength extends AbstractStep {

	private final List<String> playerIds = new ArrayList<>();

	public StepDoubleStrength(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.DOUBLE_STRENGTH;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null && parameter.getKey() == StepParameterKey.PLAYER_ID_DAUNTLESS_SUCCESS) {
			playerIds.add((String) parameter.getValue());
			return true;
		}

		return super.setParameter(parameter);
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
				case CLIENT_USE_SKILL:
					ClientCommandUseSkill command = (ClientCommandUseSkill) pReceivedCommand.getCommand();
					if (command.getSkill().hasSkillProperty(NamedProperties.canDoubleStrengthAfterDauntless)) {
						publishParameter(new StepParameter(StepParameterKey.DOUBLE_TARGET_STRENGTH_FOR_PLAYER, playerIds.get(0)));
						playerIds.clear();
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_PLAYER_CHOICE:
					ClientCommandPlayerChoice clientCommandPlayerChoice = (ClientCommandPlayerChoice) pReceivedCommand.getCommand();
					if (clientCommandPlayerChoice.getPlayerChoiceMode() == PlayerChoiceMode.INDOMITABLE) {
						publishParameter(new StepParameter(StepParameterKey.DOUBLE_TARGET_STRENGTH_FOR_PLAYER, clientCommandPlayerChoice.getPlayerId()));
						playerIds.clear();
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

		Skill indomitable = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canDoubleStrengthAfterDauntless);

		if (playerIds.isEmpty() || indomitable == null) {
			getResult().setNextAction(StepAction.NEXT_STEP);
		} else if (playerIds.size() == 1) {
			UtilServerDialog.showDialog(getGameState(),
				new DialogSkillUseParameter(actingPlayer.getPlayerId(), indomitable, 0),
				true);
		} else {
			Player<?>[] players = playerIds.stream().map(game::getPlayerById).toArray(Player[]::new);
			UtilServerDialog.showDialog(getGameState(),
				new DialogPlayerChoiceParameter(game.getActingTeam().getId(), PlayerChoiceMode.INDOMITABLE, players, null, 1),
				true);
		}

	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PLAYER_IDS.addTo(jsonObject, playerIds);
		return jsonObject;
	}

	@Override
	public StepDoubleStrength initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		playerIds.addAll(Arrays.asList(IJsonOption.PLAYER_IDS.getFrom(game, jsonObject)));
		return this;
	}

}
