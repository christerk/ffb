package com.fumbbl.ffb.server.step.bb2025;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepIllCarryYou extends AbstractStep {

	private String playerId;
	private boolean endTurn;

	public StepIllCarryYou(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.ILL_CARRY_YOU;
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);

		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pReceivedCommand.getId()) {
				case CLIENT_PLAYER_CHOICE:
					ClientCommandPlayerChoice playerChoice = (ClientCommandPlayerChoice) pReceivedCommand.getCommand();
					if (playerChoice.getPlayerChoiceMode() == PlayerChoiceMode.ILL_CARRY_YOU) {
						if (StringTool.isProvided(playerChoice.getPlayerId())) {
							playerId = playerChoice.getPlayerId();
							commandStatus = StepCommandStatus.EXECUTE_STEP;
						} else {
							reportSkillUse(false);
							getResult().setNextAction(StepAction.NEXT_STEP);
							commandStatus = StepCommandStatus.SKIP_STEP;
						}
					}
					break;
				case CLIENT_END_TURN:
					if (UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
						endTurn = true;
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

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		getResult().setNextAction(StepAction.NEXT_STEP);

		if (endTurn) {
			return;
		}

		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		Skill skill = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canCarryPartner);

		if (skill == null || getGameState().getCarriedPlayer() != null) {
			return;
		}

		Player<?>[] candidates = findCandidates(game, actingPlayer);

		if (!StringTool.isProvided(playerId)) {
			if (candidates.length == 0) {
				return;
			}

			UtilServerDialog.showDialog(getGameState(), new DialogPlayerChoiceParameter(
				actingPlayer.getPlayer().getTeam().getId(), PlayerChoiceMode.ILL_CARRY_YOU, candidates, null, 1), false);
			getResult().setNextAction(StepAction.CONTINUE);
			return;
		}

		Player<?> carriedPlayer = Arrays.stream(candidates)
			.filter(player -> playerId.equals(player.getId()))
			.findFirst()
			.orElse(null);

		if (pickUpPartner(getGameState(), actingPlayer, skill, carriedPlayer)) {
			getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), skill, true, SkillUse.ILL_CARRY_YOU));
		}
	}

	private void reportSkillUse(boolean used) {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(),
			actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canCarryPartner), used, SkillUse.ILL_CARRY_YOU));
	}

	private Player<?>[] findCandidates(Game game, ActingPlayer actingPlayer) {
		if (actingPlayer == null || actingPlayer.getPlayer() == null) {
			return new Player[0];
		}

		return Arrays.stream(UtilPlayer.findPickUpPartners(game, actingPlayer.getPlayer()))
			.filter(actingPlayer::isInitialAdjacentPartner)
			.map(game::getPlayerById)
			.filter(player -> player != null)
			.toArray(Player<?>[]::new);
	}

	private boolean pickUpPartner(GameState gameState, ActingPlayer actingPlayer, Skill skill, Player<?> carriedPlayer) {
		Game game = gameState.getGame();
		Player<?> carrier = actingPlayer.getPlayer();

		if (carrier == null || carriedPlayer == null || gameState.getCarriedPlayer() != null) {
			return false;
		}

		boolean carriedPlayerHasBall = UtilPlayer.hasBall(game, carriedPlayer);

		gameState.setCarriedPlayer(carriedPlayer.getId(), game.getFieldModel().getPlayerState(carriedPlayer),
			game.getFieldModel().getPlayerCoordinate(carriedPlayer), carriedPlayerHasBall,
			game.getFieldModel().getPlayerCoordinate(carrier));

		game.getFieldModel().setPlayerState(carriedPlayer,
			game.getFieldModel().getPlayerState(carriedPlayer).changeBase(PlayerState.PICKED_UP));

		game.getFieldModel().remove(carriedPlayer);

		if (carriedPlayerHasBall) {
			game.getFieldModel().setBallCoordinate(null);
			game.getFieldModel().setBallMoving(false);
		}
		game.getFieldModel().addSkillEnhancements(carrier, skill);
		return true;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IServerJsonOption.END_TURN.addTo(jsonObject, endTurn);
		return jsonObject;
	}

	@Override
	public StepIllCarryYou initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		playerId = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		endTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		return this;
	}
}