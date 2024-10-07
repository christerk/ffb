package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeQuickBite;
import com.fumbbl.ffb.server.model.DropPlayerContext;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepQuickBite extends AbstractStep {

	private String catcherId, playerId;
	private final List<String> playerIds = new ArrayList<>();

	private Boolean useSkill;

	public StepQuickBite(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.QUICK_BITE;
	}


	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
				case CATCHER_ID:
					catcherId = (String) parameter.getValue();
					consume(parameter);
					return true;
				default:
					break;
			}
		}
		return false;
	}


	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pReceivedCommand.getId()) {
				case CLIENT_PLAYER_CHOICE:
					ClientCommandPlayerChoice playerChoice = (ClientCommandPlayerChoice) pReceivedCommand.getCommand();

					String[] selected = playerChoice.getPlayerIds();

					if (ArrayTool.isProvided(selected) && playerIds.contains(selected[0])) {
						playerId = selected[0];
						useSkill = true;
					} else {
						useSkill = false;
					}
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_USE_SKILL:
					ClientCommandUseSkill commandUseSkill = (ClientCommandUseSkill) pReceivedCommand.getCommand();
					if (playerIds.contains(commandUseSkill.getPlayerId())
						&& commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canAttackOpponentForBallAfterCatch)) {

						playerId = commandUseSkill.getPlayerId();
						useSkill = commandUseSkill.isSkillUsed();

						commandStatus = StepCommandStatus.EXECUTE_STEP;
						break;
					}
				default:
					break;
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
		Player<?> catcher = game.getPlayerById(catcherId);
		if (useSkill == null) {
			Player<?>[] opponents = UtilPlayer.findAdjacentOpposingPlayersWithProperty(game, catcher, game.getFieldModel().getBallCoordinate(),
				NamedProperties.canAttackOpponentForBallAfterCatch, false, true);

			if (ArrayTool.isProvided(opponents)) {
				playerIds.addAll(Arrays.stream(opponents).map(Player::getId).collect(Collectors.toList()));
				Player<?> firstOpponent = opponents[0];
				UtilCards.getUnusedSkillWithProperty(firstOpponent, NamedProperties.canAttackOpponentForBallAfterCatch).ifPresent(skill -> {
					if (opponents.length == 1) {
						UtilServerDialog.showDialog(getGameState(), new DialogSkillUseParameter(firstOpponent.getId(), skill, 0), true);
					} else {
						UtilServerDialog.showDialog(getGameState(), new DialogPlayerChoiceParameter(firstOpponent.getTeam().getId(), PlayerChoiceMode.QUICK_BITE, opponents, null, 1), true);
					}
					getResult().setNextAction(StepAction.CONTINUE);
				});
				return;
			}
		} else if (useSkill) {
			Player<?> player = game.getPlayerById(playerId);
			Skill skill = player.getSkillWithProperty(NamedProperties.canAttackOpponentForBallAfterCatch);
			getResult().addReport(new ReportSkillUse(playerId, skill, true, SkillUse.QUICK_BITE));
			player.markUsed(skill, game);

			InjuryResult injuryResultDefender = UtilServerInjury.handleInjury(this, new InjuryTypeQuickBite(),
				player, catcher, game.getFieldModel().getPlayerCoordinate(catcher), null, null, ApothecaryMode.QUICK_BITE);

			publishParameter(new StepParameter(StepParameterKey.DROP_PLAYER_CONTEXT,
				new DropPlayerContext(injuryResultDefender, false, false, null,
					catcherId, ApothecaryMode.QUICK_BITE, true)));

			if (injuryResultDefender.injuryContext().isArmorBroken()) {
				game.getFieldModel().setBallCoordinate(game.getFieldModel().getPlayerCoordinate(player));
				FieldCoordinateBounds bounds;
				if (game.isHomePlaying()) {
					bounds = FieldCoordinateBounds.HALF_AWAY;
				} else {
					bounds = FieldCoordinateBounds.HALF_HOME;
				}
				if (game.getTurnMode() == TurnMode.KICKOFF
					&& !bounds.isInBounds(game.getFieldModel().getBallCoordinate())) {
					publishParameter(new StepParameter(StepParameterKey.TOUCHBACK, true));
				} else {
					publishParameter(StepParameter.from(StepParameterKey.CATCHER_ID, playerId));
					if (player.getTeam() == game.getActingTeam()) {
						// slightly hacky but we have to prevent the turnover in case of passes/hand-offs only
						// since we do not persist this parameter as a separate field we can later change this logic if we have to
						publishParameter(StepParameter.from(StepParameterKey.REVERT_END_TURN, true));
					}
				}
			}
			getResult().setSound(SoundId.BLOCK);
		}


		getResult().setNextAction(StepAction.NEXT_STEP);
	}


	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.PLAYER_IDS.addTo(jsonObject, playerIds);
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IServerJsonOption.CATCHER_ID.addTo(jsonObject, catcherId);
		IServerJsonOption.SKILL_USED.addTo(jsonObject, useSkill);
		return jsonObject;
	}

	@Override
	public StepQuickBite initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		playerIds.addAll(Arrays.asList(IServerJsonOption.PLAYER_IDS.getFrom(source, jsonObject)));
		catcherId = IServerJsonOption.CATCHER_ID.getFrom(source, jsonObject);
		playerId = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		useSkill = IServerJsonOption.SKILL_USED.getFrom(source, jsonObject);
		return this;
	}
}
