package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.Constant;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillChoiceMode;
import com.fumbbl.ffb.SkillUse;
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
import com.fumbbl.ffb.net.commands.ClientCommandSkillSelection;
import com.fumbbl.ffb.report.mixed.ReportPlayerEvent;
import com.fumbbl.ffb.report.bb2020.ReportSkillUseOtherPlayer;
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
import java.util.Comparator;
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
				case CLIENT_PRAYER_SELECTION:
					ClientCommandSkillSelection skillSelection = (ClientCommandSkillSelection) pReceivedCommand.getCommand();
					skill = skillSelection.getSkill();
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

		SkillFactory skillFactory = game.getFactory(FactoryType.Factory.SKILL);

		Set<Skill> ownedSkills = actingPlayer.getPlayer().getSkillsIncludingTemporaryOnes();

		List<SkillWithValue> gainAbleSkills = Constant.getGrantAbleSkills(skillFactory).stream()
			.filter(swv -> !ownedSkills.contains(swv.getSkill()))
			.sorted(Comparator.comparing(o -> o.getSkill().getName()))
			.collect(Collectors.toList());

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
				UtilServerDialog.showDialog(getGameState(), new DialogPlayerChoiceParameter(game.getActingTeam().getId(), PlayerChoiceMode.WISDOM, wisePlayers, null, 1, 1), false);
				return;
			}
		}

		if (skill == null) {
			getResult().addReport(new ReportSkillUseOtherPlayer(actingPlayer.getPlayerId(),
				game.getPlayerById(playerId).getSkillWithProperty(NamedProperties.canGrantSkillsToTeamMates), SkillUse.GAIN_GRANTED_SKILL, playerId));

			if (gainAbleSkills.size() == 1) {
				skill = gainAbleSkills.get(0).getSkill();
			} else {
				UtilServerDialog.showDialog(getGameState(),
					new DialogSelectSkillParameter(actingPlayer.getPlayerId(),
						gainAbleSkills.stream().map(SkillWithValue::getSkill).collect(Collectors.toList()),
						SkillChoiceMode.WISDOM_OF_THE_WHITE_DWARF),
					false);
				return;
			}
		}

		SkillWithValue gainedSkill = gainAbleSkills.stream().filter(svw -> svw.getSkill().equals(skill)).findFirst().
			orElseThrow(() -> new FantasyFootballException("Skill " + skill.getName() + " not found in the list of gain-able skills."));

		game.getFieldModel().addWisdomSkill(actingPlayer.getPlayerId(), gainedSkill);
		getResult().addReport(new ReportPlayerEvent(actingPlayer.getPlayerId(), "gains " + gainedSkill.getSkill().getName()));

		Player<?> player = game.getPlayerById(playerId);
		Skill grantingSkill = player.getSkillWithProperty(NamedProperties.canGrantSkillsToTeamMates);
		player.markUsed(grantingSkill, game);
		actingPlayer.addGrantedSkill(grantingSkill, player);

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
