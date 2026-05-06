package com.fumbbl.ffb.server.step.bb2025.kickoff;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.dialog.DialogUseInducementParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.MechanicsFactory;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.net.commands.ClientCommandEndTurn;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.net.commands.ClientCommandSetupPlayer;
import com.fumbbl.ffb.net.commands.ClientCommandUseInducement;
import com.fumbbl.ffb.report.bb2025.ReportDwarfenWisdomRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.mechanic.SetupMechanic;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerInducementUse;
import com.fumbbl.ffb.server.util.UtilServerSetup;
import com.fumbbl.ffb.util.UtilBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepDwarfenWisdom extends AbstractStep {

	public static class StepState {
		public boolean confirmed;
		public boolean endTurn;
		public boolean handleReceivingTeam;
		public int roll;
		public int allowedAmount;
		public String teamId;
	}

	private final StepState state;
	private InducementType selectedInducementType;
	private final Map<String, FieldCoordinate> playersAtCoordinates = new HashMap<>();
	private final List<Player<?>> selectedPlayers = new ArrayList<>();
	private final List<Player<?>> eligiblePlayers = new ArrayList<>();

	public StepDwarfenWisdom(GameState pGameState) {
		super(pGameState);
		state = new StepState();
	}

	@Override
	public void start() {
		executeStep();
	}

	@Override
	public StepId getId() {
		return StepId.DWARFEN_WISDOM;
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
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pReceivedCommand.getId()) {
				case CLIENT_USE_INDUCEMENT:
					if (UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
						ClientCommandUseInducement useInducementCommand =
							(ClientCommandUseInducement) pReceivedCommand.getCommand();
						selectedInducementType = useInducementCommand.getInducementType();
						state.confirmed = true;
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_END_TURN:
					if (UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
						setPlayerCoordinates(((ClientCommandEndTurn) pReceivedCommand.getCommand()).getPlayerCoordinates());
						state.endTurn = true;
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_SETUP_PLAYER:
					ClientCommandSetupPlayer setupPlayerCommand = (ClientCommandSetupPlayer) pReceivedCommand.getCommand();
					UtilServerSetup.setupPlayer(getGameState(), setupPlayerCommand.getPlayerId(), setupPlayerCommand.getCoordinate());
					commandStatus = StepCommandStatus.SKIP_STEP;
					break;
				case CLIENT_PLAYER_CHOICE:
					if (UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
						ClientCommandPlayerChoice commandPlayerChoice = (ClientCommandPlayerChoice) pReceivedCommand.getCommand();
						if (commandPlayerChoice.getPlayerChoiceMode() == PlayerChoiceMode.DWARFEN_WISDOM) {
							Arrays.stream(commandPlayerChoice.getPlayerIds())
								.map(id -> getGameState().getGame().getPlayerById(id))
								.forEach(selectedPlayers::add);
							if (selectedPlayers.stream().anyMatch(player -> !eligiblePlayers.contains(player))) {
								throw new FantasyFootballException("Client selected player that is not eligible");
							}
							getGameState().getGame().setTurnMode(TurnMode.DWARFEN_WISDOM);
							commandStatus = StepCommandStatus.EXECUTE_STEP;
						}
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

		if (game.getTurnMode() == TurnMode.DWARFEN_WISDOM) {
			if (state.endTurn) {
				state.endTurn = false;
				getResult().setSound(SoundId.DING);

				MechanicsFactory factory = game.getFactory(FactoryType.Factory.MECHANIC);
				SetupMechanic mechanic = (SetupMechanic) factory.forName(Mechanic.Type.SETUP.name());

				List<Player<?>> movedPlayers = playersAtCoordinates.keySet().stream()
					.map(game::getPlayerById)
					.filter(player -> !game.getFieldModel().getPlayerCoordinate(player)
						.equals(playersAtCoordinates.get(player.getId())))
					.collect(Collectors.toList());

				if (validDwarfenWisdom(movedPlayers) && mechanic.checkSetup(getGameState(), game.isHomePlaying())) {
					leave(game);
				}
			} else {
				if (selectedPlayers.isEmpty()) {
					leave(game);
				} else {
					Team actingTeam = game.getTeamById(state.teamId);
					UtilServerInducementUse.useInducement(getGameState(), actingTeam, selectedInducementType, 1);
					for (Player<?> player : actingTeam.getPlayers()) {
						FieldCoordinate fieldCoordinate = game.getFieldModel().getPlayerCoordinate(player);
						if (FieldCoordinateBounds.FIELD.isInBounds(fieldCoordinate)) {
							if (selectedPlayers.contains(player)) {
								game.getFieldModel().setPlayerState(player,
									game.getFieldModel().getPlayerState(player).changeBase(PlayerState.RESERVE));
								UtilBox.putPlayerIntoBox(game, player);
							} else {
								game.getFieldModel().setPlayerState(player,
									game.getFieldModel().getPlayerState(player).changeActive(false));
							}
						}
					}
				}
			}
			return;
		}

		if (state.teamId == null) {
			state.teamId = dwarfenWisdomTeam(game).getId();
		}
		Team team = game.getTeamById(state.teamId);
		InducementSet inducementSet = game.getTeamHome().getId().equals(team.getId())
			? game.getTurnDataHome().getInducementSet()
			: game.getTurnDataAway().getInducementSet();
		Optional<InducementType> wisdomType = inducementSet.getInducementTypes().stream()
			.filter(type -> type.hasUsage(Usage.RESETUP_D3_PLAYERS) && inducementSet.hasUsesLeft(type))
			.findFirst();

		if (!wisdomType.isPresent()) {
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}

		if (!state.confirmed) {
			if (state.handleReceivingTeam) {
				game.setHomePlaying(!game.isHomePlaying());
			}
			game.setDialogParameter(new DialogUseInducementParameter(team.getId(), new InducementType[]{wisdomType.get()}));
			getResult().setNextAction(StepAction.CONTINUE);
			return;
		}

		if (selectedInducementType == null) {
			if (state.handleReceivingTeam) {
				game.setHomePlaying(!game.isHomePlaying());
			}
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}

		eligiblePlayers.clear();
		selectedPlayers.clear();
		playersAtCoordinates.clear();

		for (Player<?> player : team.getPlayers()) {
			FieldCoordinate fieldCoordinate = game.getFieldModel().getPlayerCoordinate(player);
			if (FieldCoordinateBounds.FIELD.isInBounds(fieldCoordinate)) {
				eligiblePlayers.add(player);
				playersAtCoordinates.put(player.getId(), fieldCoordinate);
			}
		}

		if (eligiblePlayers.isEmpty()) {
			leave(game);
			return;
		}

		state.roll = getGameState().getDiceRoller().rollDice(3);
		state.allowedAmount = Math.min(state.roll, eligiblePlayers.size());
		getResult().addReport(new ReportDwarfenWisdomRoll(state.teamId, state.roll, state.allowedAmount));

		UtilServerDialog.showDialog(getGameState(),
			new DialogPlayerChoiceParameter(team.getId(), PlayerChoiceMode.DWARFEN_WISDOM,
				eligiblePlayers.stream().map(Player::getId).toArray(String[]::new), null, state.allowedAmount, 1),
				false);
	}

	private boolean validDwarfenWisdom(List<Player<?>> movedPlayers) {
		boolean tooManyPlayersMoved = movedPlayers.size() > state.allowedAmount;
		movedPlayers.removeAll(selectedPlayers);
		return !tooManyPlayersMoved && movedPlayers.isEmpty();
	}

	private void leave(Game game) {
		Team team = game.getTeamById(state.teamId);
		for (Player<?> player : team.getPlayers()) {
			PlayerState playerState = game.getFieldModel().getPlayerState(player);
			if (playerState.getBase() == PlayerState.PRONE) {
				game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.RESERVE));
			}
		}

		game.setTurnMode(TurnMode.KICKOFF);

		if (state.handleReceivingTeam) {
			game.setHomePlaying(!game.isHomePlaying());
		}

		UtilServerDialog.hideDialog(getGameState());
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private Team dwarfenWisdomTeam(Game game) {
		if (state.handleReceivingTeam) {
			return game.isHomePlaying() ? game.getTeamAway() : game.getTeamHome();
		}
		return game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.CONFIRMED.addTo(jsonObject, state.confirmed);
		IServerJsonOption.END_TURN.addTo(jsonObject, state.endTurn);
		IServerJsonOption.HANDLE_RECEIVING_TEAM.addTo(jsonObject, state.handleReceivingTeam);
		IServerJsonOption.ROLL.addTo(jsonObject, state.roll);
		IServerJsonOption.NR_OF_PLAYERS_ALLOWED.addTo(jsonObject, state.allowedAmount);
		IServerJsonOption.TEAM_ID.addTo(jsonObject, state.teamId);
		IServerJsonOption.INDUCEMENT_TYPE.addTo(jsonObject, selectedInducementType);
		IServerJsonOption.PLAYERS_AT_COORDINATES.addTo(jsonObject, playersAtCoordinates);
		IServerJsonOption.PLAYER_IDS_SELECTED.addTo(jsonObject,
			selectedPlayers.stream().map(Player::getId).collect(Collectors.toList()));
		IServerJsonOption.ELIGIBLE_PLAYER_IDS.addTo(jsonObject,
			eligiblePlayers.stream().map(Player::getId).collect(Collectors.toList()));
		return jsonObject;
	}

	@Override
	public StepDwarfenWisdom initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		state.confirmed = IServerJsonOption.CONFIRMED.getFrom(source, jsonObject);
		state.endTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		state.handleReceivingTeam = IServerJsonOption.HANDLE_RECEIVING_TEAM.getFrom(source, jsonObject);
		state.roll = IServerJsonOption.ROLL.getFrom(source, jsonObject);
		state.allowedAmount = IServerJsonOption.NR_OF_PLAYERS_ALLOWED.getFrom(source, jsonObject);
		state.teamId = IServerJsonOption.TEAM_ID.getFrom(source, jsonObject);
		selectedInducementType = (InducementType) IServerJsonOption.INDUCEMENT_TYPE.getFrom(source, jsonObject);
		playersAtCoordinates.putAll(IServerJsonOption.PLAYERS_AT_COORDINATES.getFrom(source, jsonObject));
		Arrays.stream(IServerJsonOption.PLAYER_IDS_SELECTED.getFrom(source, jsonObject))
			.map(id -> getGameState().getGame().getPlayerById(id))
			.forEach(selectedPlayers::add);
		Arrays.stream(IServerJsonOption.ELIGIBLE_PLAYER_IDS.getFrom(source, jsonObject))
			.map(id -> getGameState().getGame().getPlayerById(id))
			.forEach(eligiblePlayers::add);
		return this;
	}
}