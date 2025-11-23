package com.fumbbl.ffb.server.step.bb2025.kickoff;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.dialog.DialogKickSkillParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportKickoffScatter;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.report.mixed.ReportEvent;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.util.UtilServerCatchScatterThrowIn;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerGame;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2025)
public final class StepKickoffScatterRoll extends AbstractStep {

	private FieldCoordinate fKickoffStartCoordinate;
	private Boolean fUseKickChoice;
	private Direction fScatterDirection;
	private int fScatterDistance;
	private FieldCoordinate fKickingPlayerCoordinate;
	private FieldCoordinateBounds fKickoffBounds;
	private boolean fTouchback;

	public StepKickoffScatterRoll(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.KICKOFF_SCATTER_ROLL;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			if (parameter.getKey() == StepParameterKey.KICKOFF_START_COORDINATE) {
				fKickoffStartCoordinate = (FieldCoordinate) parameter.getValue();
				return true;
			}
		}
		return false;
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
			if (pReceivedCommand.getId() == NetCommandId.CLIENT_USE_SKILL) {
				ClientCommandUseSkill skillUseCommand = (ClientCommandUseSkill) pReceivedCommand.getCommand();
				if (skillUseCommand.getSkill().hasSkillProperty(NamedProperties.canReduceKickDistance)) {
					fUseKickChoice = skillUseCommand.isSkillUsed();
					commandStatus = StepCommandStatus.EXECUTE_STEP;
				}
			}
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {

		Game game = getGameState().getGame();
		Player<?> kickingPlayer = findKickingPlayer();

		Skill skillReduceKickDistance = null;
		if (kickingPlayer != null) {
			skillReduceKickDistance = kickingPlayer.getSkillWithProperty(NamedProperties.canReduceKickDistance);
		}

		if (fUseKickChoice == null) {
			int rollScatterDirection = getGameState().getDiceRoller().rollScatterDirection();
			fScatterDirection = DiceInterpreter.getInstance().interpretScatterDirectionRoll(game, rollScatterDirection);
			fScatterDistance = getGameState().getDiceRoller().rollScatterDistance();

			FieldCoordinate ballCoordinateEnd = UtilServerCatchScatterThrowIn.findScatterCoordinate(fKickoffStartCoordinate,
				fScatterDirection, fScatterDistance);
			getResult().addReport(
				new ReportKickoffScatter(ballCoordinateEnd, fScatterDirection, rollScatterDirection, fScatterDistance));

			if (kickingPlayer != null) {
				fKickingPlayerCoordinate = game.getFieldModel().getPlayerCoordinate(kickingPlayer);
				if (skillReduceKickDistance != null) {
					FieldCoordinate ballCoordinateEndWithKick = UtilServerCatchScatterThrowIn
						.findScatterCoordinate(fKickoffStartCoordinate, fScatterDirection, fScatterDistance / 2);
					UtilServerDialog.showDialog(getGameState(),
						new DialogKickSkillParameter(kickingPlayer.getId(), ballCoordinateEnd, ballCoordinateEndWithKick), false);
				} else {
					fUseKickChoice = false;
				}
			} else {
				if (game.isHomePlaying()) {
					fKickingPlayerCoordinate = new FieldCoordinate(0, 7);
				} else {
					fKickingPlayerCoordinate = new FieldCoordinate(25, 7);
				}
				fUseKickChoice = false;
			}

		}

		if (fUseKickChoice != null) {
			int distance = fUseKickChoice ? fScatterDistance / 2 : fScatterDistance;
			FieldCoordinate ballCoordinateEnd = UtilServerCatchScatterThrowIn.findScatterCoordinate(fKickoffStartCoordinate,
				fScatterDirection, distance);
			FieldCoordinate lastValidCoordinate = ballCoordinateEnd;
			while (!FieldCoordinateBounds.FIELD.isInBounds(lastValidCoordinate)) {
				lastValidCoordinate = UtilServerCatchScatterThrowIn.findScatterCoordinate(fKickoffStartCoordinate,
					fScatterDirection, --distance);
			}
			game.getFieldModel().setBallInPlay(false);
			game.getFieldModel().setBallCoordinate(lastValidCoordinate);
			game.getFieldModel().setBallMoving(true);

			if (game.isHomePlaying() && FieldCoordinateBounds.HALF_AWAY.isInBounds(ballCoordinateEnd)) {
				fKickoffBounds = FieldCoordinateBounds.HALF_AWAY;
			}
			if (!game.isHomePlaying() && FieldCoordinateBounds.HALF_HOME.isInBounds(ballCoordinateEnd)) {
				fKickoffBounds = FieldCoordinateBounds.HALF_HOME;
			}
			fTouchback = (fKickoffBounds == null);

			if (fUseKickChoice && skillReduceKickDistance != null) {
				getResult().addReport(
					new ReportSkillUse(kickingPlayer.getId(), skillReduceKickDistance, true, SkillUse.HALVE_KICKOFF_SCATTER));
			}

			if (fTouchback) {
				game.getFieldModel().setOutOfBounds(true);
				getResult().addReport(new ReportEvent("The ball lands out of bounds -> TOUCHBACK!!"));
			}

			publishParameter(new StepParameter(StepParameterKey.KICKING_PLAYER_COORDINATE, fKickingPlayerCoordinate));
			publishParameter(new StepParameter(StepParameterKey.KICKOFF_BOUNDS, fKickoffBounds));
			publishParameter(new StepParameter(StepParameterKey.TOUCHBACK, fTouchback));
			getResult().setNextAction(StepAction.NEXT_STEP);

			if ((game.getHalf() < 3)
				&& (game.getTurnDataHome().getTurnNr() == 0)
				&& (game.getTurnDataAway().getTurnNr() == 0)) {
				UtilServerGame.handleChefRolls(this, game);
			}
		}
	}

	private Player<?> findKickingPlayer() {
		Game game = getGameState().getGame();

		Team kickingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
		FieldCoordinateBounds centerBounds = game.isHomePlaying() ? FieldCoordinateBounds.CENTER_FIELD_HOME : FieldCoordinateBounds.CENTER_FIELD_AWAY;
		FieldCoordinateBounds losBounds = game.isHomePlaying() ? FieldCoordinateBounds.LOS_HOME : FieldCoordinateBounds.LOS_AWAY;

		List<Player<?>> centerPlayers = Arrays.stream(kickingTeam.getPlayers()).filter(player -> centerBounds.isInBounds(game.getFieldModel().getPlayerCoordinate(player)))
			.collect(Collectors.toList());

		List<Player<?>> playersOnField;

		if (centerPlayers.isEmpty()) {
			playersOnField = Arrays.stream(kickingTeam.getPlayers()).filter(player -> losBounds.isInBounds(game.getFieldModel().getPlayerCoordinate(player)))
				.collect(Collectors.toList());
		} else {
			playersOnField = centerPlayers;
		}

		if (playersOnField.isEmpty()) {
			return null;
		}

		return playersOnField.stream().filter(player -> player.hasSkillProperty(NamedProperties.canReduceKickDistance))
			.findFirst().orElseGet(() -> playersOnField.get(0));

	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.KICKOFF_START_COORDINATE.addTo(jsonObject, fKickoffStartCoordinate);
		IServerJsonOption.USE_KICK_CHOICE.addTo(jsonObject, fUseKickChoice);
		IServerJsonOption.SCATTER_DIRECTION.addTo(jsonObject, fScatterDirection);
		IServerJsonOption.SCATTER_DISTANCE.addTo(jsonObject, fScatterDistance);
		IServerJsonOption.KICKING_PLAYER_COORDINATE.addTo(jsonObject, fKickingPlayerCoordinate);
		if (fKickoffBounds != null) {
			IServerJsonOption.KICKOFF_BOUNDS.addTo(jsonObject, fKickoffBounds.toJsonValue());
		}
		IServerJsonOption.TOUCHBACK.addTo(jsonObject, fTouchback);
		return jsonObject;
	}

	@Override
	public StepKickoffScatterRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fKickoffStartCoordinate = IServerJsonOption.KICKOFF_START_COORDINATE.getFrom(source, jsonObject);
		fUseKickChoice = IServerJsonOption.USE_KICK_CHOICE.getFrom(source, jsonObject);
		fScatterDirection = (Direction) IServerJsonOption.SCATTER_DIRECTION.getFrom(source, jsonObject);
		fScatterDistance = IServerJsonOption.SCATTER_DISTANCE.getFrom(source, jsonObject);
		fKickingPlayerCoordinate = IServerJsonOption.KICKING_PLAYER_COORDINATE.getFrom(source, jsonObject);
		JsonObject kickoffBoundsObject = IServerJsonOption.KICKOFF_BOUNDS.getFrom(source, jsonObject);
		if (kickoffBoundsObject != null) {
			fKickoffBounds = new FieldCoordinateBounds().initFrom(source, kickoffBoundsObject);
		}
		fTouchback = IServerJsonOption.TOUCHBACK.getFrom(source, jsonObject);
		return this;
	}

}
