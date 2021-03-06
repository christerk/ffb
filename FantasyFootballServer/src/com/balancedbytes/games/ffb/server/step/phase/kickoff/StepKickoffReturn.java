package com.balancedbytes.games.ffb.server.step.phase.kickoff;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.dialog.DialogKickoffReturnParameter;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.factory.SequenceGeneratorFactory;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.UtilServerSteps;
import com.balancedbytes.games.ffb.server.step.generator.common.Select;
import com.balancedbytes.games.ffb.server.step.generator.SequenceGenerator;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Step in kickoff sequence to handle KICKOFF_RETURN skill.
 *
 * Expects stepParameter END_PLAYER_ACTION to be set by a preceding step.
 * (parameter is consumed on TurnMode.KICKOFF_RETURN) Expects stepParameter
 * END_TURN to be set by a preceding step. (parameter is consumed on
 * TurnMode.KICKOFF_RETURN) Expects stepParameter TOUCHBACK to be set by a
 * preceding step.
 *
 * May push new select sequence on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public final class StepKickoffReturn extends AbstractStep {

	private boolean fTouchback;
	private boolean fEndPlayerAction;
	private boolean fEndTurn;

	public StepKickoffReturn(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.KICKOFF_RETURN;
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		Game game = getGameState().getGame();
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
			case END_PLAYER_ACTION:
				fEndPlayerAction = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
				if (game.getTurnMode() == TurnMode.KICKOFF_RETURN) {
					consume(pParameter);
				}
				return true;
			case END_TURN:
				fEndTurn = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
				if (game.getTurnMode() == TurnMode.KICKOFF_RETURN) {
					consume(pParameter);
				}
				return true;
			case TOUCHBACK:
				fTouchback = (Boolean) pParameter.getValue();
				return true;
			default:
				break;
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
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {

		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
		Select generator = ((Select)factory.forName(SequenceGenerator.Type.Select.name()));
		if (game.getTurnMode() == TurnMode.KICKOFF_RETURN) {

			if (fEndPlayerAction && !actingPlayer.hasActed()) {
				UtilServerSteps.changePlayerAction(this, null, null, false);
				getGameState().pushCurrentStepOnStack();
				generator.pushSequence(new Select.SequenceParams(getGameState(), false));

			} else {

				if (fEndPlayerAction || fEndTurn) {
					UtilServerSteps.changePlayerAction(this, null, null, false);
					game.setHomePlaying(!game.isHomePlaying());
					game.setTurnMode(TurnMode.KICKOFF);
					UtilPlayer.refreshPlayersForTurnStart(game);
					game.getFieldModel().clearTrackNumbers();
				}

			}

		} else {

			Team kickoffReturnTeam = game.isHomePlaying() ? game.getTeamAway() : game.getTeamHome();
			Team otherTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
			Player<?> kickoffReturnPlayer = null;
			List<Player<?>> passivePlayers = new ArrayList<>();
			for (Player<?> player : kickoffReturnTeam.getPlayers()) {
				FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
				if ((playerCoordinate != null) && !playerCoordinate.isBoxCoordinate()) {
					if (player.hasSkillWithProperty(NamedProperties.canMoveDuringKickOffScatter)) {
						FieldCoordinateBounds losBounds = game.isHomePlaying() ? FieldCoordinateBounds.LOS_AWAY
								: FieldCoordinateBounds.LOS_HOME;
						if (losBounds.isInBounds(playerCoordinate)) {
							passivePlayers.add(player);
						} else {
							if (UtilPlayer.findAdjacentPlayersWithTacklezones(game, otherTeam, playerCoordinate, false).length > 0) {
								passivePlayers.add(player);
							} else {
								kickoffReturnPlayer = player;
							}
						}
					} else {
						passivePlayers.add(player);
					}
				}
			}

			if ((kickoffReturnPlayer != null) && !fTouchback) {

				for (Player<?> player : passivePlayers) {
					PlayerState playerState = game.getFieldModel().getPlayerState(player);
					game.getFieldModel().setPlayerState(player, playerState.changeActive(false));
				}
				game.setHomePlaying(!game.isHomePlaying());
				game.setTurnMode(TurnMode.KICKOFF_RETURN);
				UtilServerDialog.showDialog(getGameState(), new DialogKickoffReturnParameter(), false);

				getGameState().pushCurrentStepOnStack();
				generator.pushSequence(new Select.SequenceParams(getGameState(), false));

			}

		}

		getResult().setNextAction(StepAction.NEXT_STEP);

	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.TOUCHBACK.addTo(jsonObject, fTouchback);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, fEndPlayerAction);
		IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
		return jsonObject;
	}

	@Override
	public StepKickoffReturn initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fTouchback = IServerJsonOption.TOUCHBACK.getFrom(game, jsonObject);
		fEndPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(game, jsonObject);
		fEndTurn = IServerJsonOption.END_TURN.getFrom(game, jsonObject);
		return this;
	}

}
