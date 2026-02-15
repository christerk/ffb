package com.fumbbl.ffb.server.step.bb2025.kickoff;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.*;
import com.fumbbl.ffb.dialog.DialogSwarmingErrorParameter;
import com.fumbbl.ffb.dialog.DialogSwarmingPlayersParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.MechanicsFactory;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Keyword;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.SpecialRule;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.net.commands.ClientCommandEndTurn;
import com.fumbbl.ffb.net.commands.ClientCommandSetupPlayer;
import com.fumbbl.ffb.report.bb2025.ReportSwarmingRoll;
import com.fumbbl.ffb.server.ActionStatus;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.mechanic.SetupMechanic;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.*;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerSetup;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.HashSet;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepSwarming extends AbstractStep {

	public static class StepState {
		public ActionStatus status;
		public boolean endTurn;
		public boolean handleReceivingTeam;
		public int rolledAmount;
		public String teamId;
	}

	private final StepState state;

	public StepSwarming(GameState pGameState) {
		super(pGameState);

		state = new StepState();
	}

	@Override
	public void start() {
		executeStep();
	}

	@Override
	public StepId getId() {
		return StepId.SWARMING;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		super.init(pParameterSet);
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				if (parameter.getKey() == StepParameterKey.HANDLE_RECEIVING_TEAM) {
					state.handleReceivingTeam = (boolean) parameter.getValue();
				}
			}
		}
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);

		switch (pReceivedCommand.getId()) {
			case CLIENT_END_TURN:
				setPlayerCoordinates(((ClientCommandEndTurn) pReceivedCommand.getCommand()).getPlayerCoordinates());
				state.endTurn = true;
				executeStep();
				break;

			case CLIENT_SETUP_PLAYER:
				ClientCommandSetupPlayer setupPlayerCommand = (ClientCommandSetupPlayer) pReceivedCommand.getCommand();
				UtilServerSetup.setupPlayer(getGameState(), setupPlayerCommand.getPlayerId(), setupPlayerCommand.getCoordinate());
				break;
			default:
				break;
		}
		return commandStatus;
	}

	private void executeStep() {

		Game game = getGameState().getGame();
		boolean hasSwarmingReserves = false;

		if (game.getTurnMode() == TurnMode.SWARMING) {
			if (state.endTurn) {
				state.endTurn = false;
				getResult().setSound(SoundId.DING);
				int placedSwarmingPlayers = 0;
				for (Player<?> player : game.getTeamById(state.teamId).getPlayers()) {
					PlayerState playerState = game.getFieldModel().getPlayerState(player);
					FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
					if (playerState.isActive() && !playerCoordinate.isBoxCoordinate()) {
						placedSwarmingPlayers++;
					}
				}

				MechanicsFactory factory = game.getFactory(FactoryType.Factory.MECHANIC);
				SetupMechanic mechanic = (SetupMechanic) factory.forName(Mechanic.Type.SETUP.name());

				if (placedSwarmingPlayers > state.rolledAmount) {
					UtilServerDialog.showDialog(getGameState(),
							new DialogSwarmingErrorParameter(state.rolledAmount, placedSwarmingPlayers), false);
				} else if (mechanic.checkSetup(getGameState(), game.isHomePlaying(), state.rolledAmount)) {
					leave(game, placedSwarmingPlayers);
				}
			}
		} else {
			if (!state.handleReceivingTeam) {
				getGameState().setKickingSwarmers(0);
			}
			Team team = swarmingTeam(game);
			if (!team.getSpecialRules().contains(SpecialRule.SWARMING)) {
				getResult().setNextAction(StepAction.NEXT_STEP);
				return;
			}
			state.teamId = team.getId();
			Set<Player<?>> playersOnPitch = new HashSet<>();
			Set<Player<?>> playersReserveNoSwarming = new HashSet<>();
			for (Player<?> player : game.getTeamById(state.teamId).getPlayers()) {
				FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
				if (FieldCoordinateBounds.FIELD.isInBounds(playerCoordinate)) {
					playersOnPitch.add(player);
				} else if (game.getFieldModel().getPlayerState(player).getBase() == PlayerState.RESERVE) {
					if (player.getPosition().getKeywords().contains(Keyword.LINEMAN)) {
						hasSwarmingReserves = true;
					} else {
						playersReserveNoSwarming.add(player);
					}
				}
			}

			if (hasSwarmingReserves) {
				for (Player<?> player : playersOnPitch) {
					PlayerState playerState = game.getFieldModel().getPlayerState(player);
					game.getFieldModel().setPlayerState(player, playerState.changeActive(false));
				}

				for (Player<?> player : playersReserveNoSwarming) {
					PlayerState playerState = game.getFieldModel().getPlayerState(player);
					game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.PRONE));
				}

				if (state.handleReceivingTeam) {
					game.setHomePlaying(!game.isHomePlaying());
				}

				game.setTurnMode(TurnMode.SWARMING);
				getGameState().pushCurrentStepOnStack();

				state.rolledAmount = getGameState().getDiceRoller().rollSwarmingPlayers();
				getResult().addReport(new ReportSwarmingRoll(state.teamId, state.rolledAmount));
				UtilServerDialog.showDialog(getGameState(), new DialogSwarmingPlayersParameter(state.rolledAmount, false),
						false);
			} else {
				getResult().setNextAction(StepAction.NEXT_STEP);
			}
		}
	}


	private void leave(Game game, int placedSwarmingPlayers) {
		for (Player<?> player : game.getTeamById(state.teamId).getPlayers()) {
			PlayerState playerState = game.getFieldModel().getPlayerState(player);
			if (playerState.getBase() == PlayerState.PRONE) {
				game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.RESERVE));
			}
		}

		game.setTurnMode(TurnMode.KICKOFF);
		UtilPlayer.refreshPlayersForTurnStart(game);
		game.getFieldModel().clearTrackNumbers();
		if (state.handleReceivingTeam) {
			game.setHomePlaying(!game.isHomePlaying());
		} else {
			getGameState().setKickingSwarmers(placedSwarmingPlayers);
		}

		UtilServerDialog.hideDialog(getGameState());

		getGameState().getStepStack().pop();
		getResult().setNextAction(StepAction.NEXT_STEP);
	}


	private Team swarmingTeam(Game game) {
		if (state.handleReceivingTeam) {
			return game.isHomePlaying() ? game.getTeamAway() : game.getTeamHome();
		}
		return game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_TURN.addTo(jsonObject, state.endTurn);
		IServerJsonOption.HANDLE_RECEIVING_TEAM.addTo(jsonObject, state.handleReceivingTeam);
		IServerJsonOption.SWARMING_PLAYER_ROLL.addTo(jsonObject, state.rolledAmount);
		IServerJsonOption.TEAM_ID.addTo(jsonObject, state.teamId);
		return jsonObject;
	}

	@Override
	public StepSwarming initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		state.endTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		state.handleReceivingTeam = IServerJsonOption.HANDLE_RECEIVING_TEAM.getFrom(source, jsonObject);
		state.rolledAmount = IServerJsonOption.SWARMING_PLAYER_ROLL.getFrom(source, jsonObject);
		state.teamId = IServerJsonOption.TEAM_ID.getFrom(source, jsonObject);
		return this;
	}

}
