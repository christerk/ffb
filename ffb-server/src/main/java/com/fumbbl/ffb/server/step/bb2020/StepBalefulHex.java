package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.report.bb2020.ReportBalefulHexRoll;
import com.fumbbl.ffb.report.bb2020.ReportSkillWasted;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.ServerUtilBlock;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerPlayerMove;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepBalefulHex extends AbstractStepWithReRoll {
	private static final ReRolledAction RE_ROLLED_ACTION = ReRolledActions.BALEFUL_HEX;

	private boolean endPlayerAction, endTurn;
	private String goToLabelOnFailure, playerId;
	private int roll;

	public StepBalefulHex(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.BALEFUL_HEX;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		super.init(pParameterSet);
		if (pParameterSet != null) {
			Arrays.stream(pParameterSet.values()).forEach(parameter -> {
				if (parameter.getKey() == StepParameterKey.GOTO_LABEL_ON_FAILURE) {
					goToLabelOnFailure = (String) parameter.getValue();
				}
			});
		}
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);

		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			if (Objects.requireNonNull(pReceivedCommand.getId()) == NetCommandId.CLIENT_PLAYER_CHOICE) {
				ClientCommandPlayerChoice clientCommandPlayerChoice = (ClientCommandPlayerChoice) pReceivedCommand.getCommand();
				if (StringTool.isProvided(clientCommandPlayerChoice.getPlayerId())) {
					playerId = clientCommandPlayerChoice.getPlayerId();
					commandStatus = StepCommandStatus.EXECUTE_STEP;
				} else {
					commandStatus = StepCommandStatus.SKIP_STEP;
					Game game = getGameState().getGame();
					ActingPlayer actingPlayer = game.getActingPlayer();
					getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canMakeOpponentMissTurn), false, SkillUse.MAKE_OPPONENT_MISS_TURN));
					getResult().setNextAction(StepAction.NEXT_STEP);
				}
			}
		}

		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}

		return commandStatus;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null) {
			switch (parameter.getKey()) {
				case END_TURN:
					endTurn = toPrimitive((Boolean) parameter.getValue());
					return true;
				case END_PLAYER_ACTION:
					endPlayerAction = toPrimitive((Boolean) parameter.getValue());
					return true;
				default:
					break;
			}
		}

		return super.setParameter(parameter);
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {

		getResult().setNextAction(StepAction.NEXT_STEP);

		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		Skill skill = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canMakeOpponentMissTurn);
		if (skill != null) {
			markActionUsed(game, actingPlayer);

			if (endTurn || endPlayerAction) {
				getResult().addReport(new ReportSkillWasted(actingPlayer.getPlayerId(), skill));
				getResult().setNextAction(StepAction.GOTO_LABEL, goToLabelOnFailure);
				actingPlayer.markSkillUsed(skill);
				return;
			}

			if (!StringTool.isProvided(playerId)) {
				List<Player<?>> eligiblePlayers = findPlayers(game, actingPlayer.getPlayer());

				if (eligiblePlayers.isEmpty()) {
					return;
				}
				getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), skill, true, SkillUse.MAKE_OPPONENT_MISS_TURN));
				if (eligiblePlayers.size() == 1) {
					playerId = eligiblePlayers.get(0).getId();
				} else {
					UtilServerDialog.showDialog(getGameState(),
						new DialogPlayerChoiceParameter(game.getActingTeam().getId(), PlayerChoiceMode.BALEFUL_HEX, eligiblePlayers.toArray(new Player<?>[0]), null, 1), false);
					getResult().setNextAction(StepAction.CONTINUE);
					return;
				}
			}

			if (StringTool.isProvided(playerId)) {
				if (getReRolledAction() == RE_ROLLED_ACTION) {
					if (getReRollSource() == null || !UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
						actingPlayer.markSkillUsed(skill);
						return;
					}
				}
				commitTargetSelection();
				roll = getGameState().getDiceRoller().rollSkill();
				boolean successful = roll > 1;
				getResult().addReport(new ReportBalefulHexRoll(actingPlayer.getPlayerId(), playerId, successful, roll, getReRolledAction() == RE_ROLLED_ACTION));
				getResult().setSound(SoundId.HYPNO);

				if (successful) {
					actingPlayer.markSkillUsed(skill);
					Player<?> targetPlayer = game.getPlayerById(playerId);
					FieldModel fieldModel = game.getFieldModel();
					fieldModel.setPlayerState(targetPlayer, fieldModel.getPlayerState(targetPlayer).changeHypnotized(true).changeActive(false));
					fieldModel.addSkillEnhancements(targetPlayer, skill);
					UtilServerPlayerMove.updateMoveSquares(getGameState(), actingPlayer.isJumping());
					ServerUtilBlock.updateDiceDecorations(game);
				} else if (getReRolledAction() == null && UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer, RE_ROLLED_ACTION, 2, false)) {
					getResult().setNextAction(StepAction.CONTINUE);
				} else {
					actingPlayer.markSkillUsed(skill);
				}

			}
		}
	}

	private void markActionUsed(Game game, ActingPlayer actingPlayer) {
		switch (actingPlayer.getPlayerAction()) {
			case BLITZ:
			case BLITZ_MOVE:
			case KICK_EM_BLITZ:
				game.getTurnData().setBlitzUsed(true);
				break;
			case KICK_TEAM_MATE:
			case KICK_TEAM_MATE_MOVE:
				game.getTurnData().setKtmUsed(true);
				break;
			case PASS:
			case PASS_MOVE:
			case THROW_TEAM_MATE:
			case THROW_TEAM_MATE_MOVE:
				game.getTurnData().setPassUsed(true);
				break;
			case HAND_OVER:
			case HAND_OVER_MOVE:
				game.getTurnData().setHandOverUsed(true);
				break;
			case FOUL:
			case FOUL_MOVE:
				if (!actingPlayer.getPlayer().hasSkillProperty(NamedProperties.allowsAdditionalFoul)) {
					game.getTurnData().setFoulUsed(true);
				}
				break;
			default:
				break;
		}
	}

	private List<Player<?>> findPlayers(Game game, Player<?> player) {
		FieldModel fieldModel = game.getFieldModel();
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(player);

		return Arrays.stream(game.getOtherTeam(game.getActingTeam()).getPlayers()).filter(
			teamMate -> {
				FieldCoordinate teamMateCoordinate = fieldModel.getPlayerCoordinate(teamMate);
				return teamMateCoordinate.distanceInSteps(playerCoordinate) <= 5;
			}
		).collect(Collectors.toList());
	}


	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_TURN.addTo(jsonObject, endTurn);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, endPlayerAction);
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, goToLabelOnFailure);
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IServerJsonOption.ROLL.addTo(jsonObject, roll);
		return jsonObject;
	}

	@Override
	public StepBalefulHex initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		endPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(source, jsonObject);
		endTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		goToLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		playerId = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		roll = IServerJsonOption.ROLL.getFrom(source, jsonObject);
		return this;
	}
}
