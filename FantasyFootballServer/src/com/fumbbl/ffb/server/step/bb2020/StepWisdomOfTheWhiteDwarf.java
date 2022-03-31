package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.Constant;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.dialog.DialogSelectSkillParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillWithValue;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepWisdomOfTheWhiteDwarf extends AbstractStep {
	public StepWisdomOfTheWhiteDwarf(GameState pGameState) {
		super(pGameState);
	}

	private String playerId;
	private Skill skill;

	@Override
	public StepId getId() {
		return StepId.WISDOM_OF_THE_WHITE_DWARF;
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus status = super.handleCommand(pReceivedCommand);

		if (status == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pReceivedCommand.getId()) {
				case CLIENT_PLAYER_CHOICE:
					ClientCommandPlayerChoice commandPlayerChoice = (ClientCommandPlayerChoice) pReceivedCommand.getCommand();
					playerId = commandPlayerChoice.getPlayerId();
					status = StepCommandStatus.EXECUTE_STEP;
					break;
				default:
					break;
			}
		}

		if (status == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}

		return status;
	}

	@Override
	public void start() {
		executeStep();
	}

	private void executeStep() {
		UtilServerDialog.hideDialog(getGameState());
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();

		if (!StringTool.isProvided(playerId)) {
			Player<?> player = actingPlayer.getPlayer();
			String[] wisePlayers = Arrays.stream(UtilPlayer.findAdjacentPlayersWithTacklezones(game, player.getTeam(),
					game.getFieldModel().getPlayerCoordinate(player), false))
				.filter(teamMate -> teamMate.hasSkillProperty(NamedProperties.canGrantSkillsToTeamMates) && !teamMate.isUsed(NamedProperties.canGrantSkillsToTeamMates))
				.map(Player::getId)
				.toArray(String[]::new);
			if (!ArrayTool.isProvided(wisePlayers)) {
				getResult().setNextAction(StepAction.NEXT_STEP);
				return;
			}
			if (wisePlayers.length == 1) {
				playerId = wisePlayers[0];
			} else {
				UtilServerDialog.showDialog(getGameState(), new DialogPlayerChoiceParameter(game.getActingTeam().getId(), PlayerChoiceMode.WISDOM, wisePlayers, null, 1, 1), true);
				return;
			}
		} else {
			SkillFactory skillFactory = game.getFactory(FactoryType.Factory.SKILL);

			Set<Class<? extends Skill>> ownedSkillClasses = actingPlayer.getPlayer().getSkillsIncludingTemporaryOnes().stream().map(Skill::getClass).collect(Collectors.toSet());

			List<SkillWithValue> gainAbleSkills = Constant.GRANT_ABLE_SKILLS.stream()
				.filter(scwv -> !ownedSkillClasses.contains(scwv.getSkill()))
				.map(scwv -> new SkillWithValue(skillFactory.forClass(scwv.getSkill()), scwv.getValue().orElse(null)))
				.collect(Collectors.toList());

			if (gainAbleSkills.size() == 1) {
				skill = gainAbleSkills.get(0).getSkill();
			} else {

				UtilServerDialog.showDialog(getGameState(), new DialogSelectSkillParameter(), true);
				return;
			}
		}


		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IServerJsonOption.SKILL.addTo(jsonObject, skill);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		playerId = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		skill = (Skill) IServerJsonOption.SKILL.getFrom(source, jsonObject);
		return this;
	}
}
