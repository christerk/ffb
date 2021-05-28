package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.FieldMarker;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.dialog.DialogInvalidSolidDefenceParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.InducementTypeFactory;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.kickoff.bb2020.KickoffResult;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandSetupPlayer;
import com.fumbbl.ffb.report.ReportScatterBall;
import com.fumbbl.ffb.report.ReportWeather;
import com.fumbbl.ffb.report.bb2020.ReportKickoffExtraReRoll;
import com.fumbbl.ffb.report.bb2020.ReportKickoffOfficiousRef;
import com.fumbbl.ffb.report.bb2020.ReportKickoffPitchInvasion;
import com.fumbbl.ffb.report.bb2020.ReportKickoffSequenceActivationsCount;
import com.fumbbl.ffb.report.bb2020.ReportKickoffSequenceActivationsExhausted;
import com.fumbbl.ffb.report.bb2020.ReportKickoffTimeout;
import com.fumbbl.ffb.report.bb2020.ReportOfficiousRefRoll;
import com.fumbbl.ffb.report.bb2020.ReportQuickSnapRoll;
import com.fumbbl.ffb.report.bb2020.ReportSolidDefenceRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.server.step.phase.kickoff.UtilKickoffSequence;
import com.fumbbl.ffb.server.util.UtilServerCatchScatterThrowIn;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.server.util.UtilServerSetup;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fumbbl.ffb.server.step.StepParameter.from;

/**
 * Step in kickoff sequence to apply the kickoff result.
 * <p>
 * Needs to be initialized with stepParameter GOTO_LABEL_END.
 * <p>
 * Needs to be initialized with stepParameter SKIP_PAST_LABEL_ON_BLITZ.
 * <p>
 * Expects stepParameter KICKOFF_BOUNDS to be set by a preceding step. Expects
 * stepParameter KICKOFF_RESULT to be set by a preceding step. Expects
 * stepParameter TOUCHBACK to be set by a preceding step.
 * <p>
 * Sets stepParameter TOUCHBACK for all steps on the stack. Sets stepParameter
 * INJURY_RESULT for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public final class StepApplyKickoffResult extends AbstractStep {

	private String fGotoLabelOnEnd;
	private String fGotoLabelOnBlitz;
	private KickoffResult fKickoffResult;
	private boolean fTouchback;
	private FieldCoordinateBounds fKickoffBounds;
	private boolean fEndKickoff;
	private final Map<String, FieldCoordinate> playersAtCoordinates = new HashMap<>();
	private int nrOfPlayersAllowed, nrOfMovedPlayers;
	private String movedPlayer;
	private FieldCoordinate toCoordinate;

	public StepApplyKickoffResult(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.APPLY_KICKOFF_RESULT;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
					// mandatory
					case GOTO_LABEL_ON_END:
						fGotoLabelOnEnd = (String) parameter.getValue();
						break;
					// mandatory
					case GOTO_LABEL_ON_BLITZ:
						fGotoLabelOnBlitz = (String) parameter.getValue();
						break;
					default:
						break;
				}
			}
		}
		if (!StringTool.isProvided(fGotoLabelOnEnd)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_END + " is not initialized.");
		}
		if (!StringTool.isProvided(fGotoLabelOnBlitz)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_BLITZ + " is not initialized.");
		}
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
				case KICKOFF_BOUNDS:
					fKickoffBounds = (FieldCoordinateBounds) pParameter.getValue();
					return true;
				case KICKOFF_RESULT:
					fKickoffResult = (KickoffResult) pParameter.getValue();
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
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pReceivedCommand.getId()) {
				case CLIENT_SETUP_PLAYER:
					ClientCommandSetupPlayer setupPlayerCommand = (ClientCommandSetupPlayer) pReceivedCommand.getCommand();
					if (getGameState().getGame().getTurnMode() == TurnMode.QUICK_SNAP) {
						movedPlayer = setupPlayerCommand.getPlayerId();
						toCoordinate = setupPlayerCommand.getCoordinate();
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					} else {
						UtilServerSetup.setupPlayer(getGameState(), setupPlayerCommand.getPlayerId(),
							setupPlayerCommand.getCoordinate());
						commandStatus = StepCommandStatus.SKIP_STEP;
					}
					break;
				case CLIENT_END_TURN:
					if (UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
						fEndKickoff = true;
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
		if (fKickoffResult == null) {
			return;
		}
		switch (fKickoffResult) {
			case GET_THE_REF:
				handleGetTheRef();
				break;
			case TIME_OUT:
				handleTimeout();
				break;
			case SOLID_DEFENCE:
				handleSolidDefense();
				break;
			case HIGH_KICK:
				handleHighKick();
				break;
			case CHEERING_FANS:
				handleExtraReRoll();
				break;
			case WEATHER_CHANGE:
				handleWeatherChange();
				break;
			case BRILLIANT_COACHING:
				handleBrilliantCoaching();
				break;
			case QUICK_SNAP:
				handleQuickSnap();
				break;
			case BLITZ:
				handleBlitz();
				break;
			case OFFICIOUS_REF:
				handleOfficiousRef();
				break;
			case PITCH_INVASION:
				handlePitchInvasion();
				break;
		}
	}

	private void handleGetTheRef() {
		Game game = getGameState().getGame();
		((InducementTypeFactory) game.getFactory(FactoryType.Factory.INDUCEMENT_TYPE)).allTypes().stream().filter(type -> type.getUsage() == Usage.AVOID_BAN)
			.findFirst().ifPresent(bribesType -> {
			InducementSet inducementSetHome = game.getTurnDataHome().getInducementSet();
			Inducement bribesHome = inducementSetHome.getInducementMapping().entrySet().stream().filter(entry -> entry.getKey().getUsage() == bribesType.getUsage())
				.findFirst().map(Map.Entry::getValue).orElse(new Inducement(bribesType, 0));
			bribesHome.setValue(bribesHome.getValue() + 1);
			inducementSetHome.addInducement(bribesHome);

			InducementSet inducementSetAway = game.getTurnDataAway().getInducementSet();
			Inducement bribesAway = inducementSetAway.getInducementMapping().entrySet().stream().filter(entry -> entry.getKey().getUsage() == bribesType.getUsage())
				.findFirst().map(Map.Entry::getValue).orElse(new Inducement(bribesType, 0));
			bribesAway.setValue(bribesAway.getValue() + 1);
			inducementSetAway.addInducement(bribesAway);

		});
		getResult().setAnimation(new Animation(AnimationType.KICKOFF_GET_THE_REF));
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private void handleSolidDefense() {
		Game game = getGameState().getGame();
		if (game.getTurnMode() == TurnMode.SOLID_DEFENCE) {
			if (fEndKickoff) {
				int movedPlayers = (int) playersAtCoordinates.keySet().stream().filter(playerId ->
					!game.getFieldModel().getPlayerCoordinate(game.getPlayerById(playerId)).equals(playersAtCoordinates.get(playerId)))
					.count();
				if (validSolidDefence(movedPlayers) && UtilKickoffSequence.checkSetup(getGameState(), game.isHomePlaying(), getGameState().getKickingSwarmers())) {
					getGameState().setKickingSwarmers(0);
					game.setTurnMode(TurnMode.KICKOFF);
					getResult().setNextAction(StepAction.NEXT_STEP);
					playersAtCoordinates.values().forEach(coordinate -> game.getFieldModel().remove(game.getFieldModel().getFieldMarker(coordinate)));
					for (Player<?> player : game.getActingTeam().getPlayers()) {
						PlayerState playerState = game.getFieldModel().getPlayerState(player);
						if (playerState.getBase() == PlayerState.PRONE) {
							game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.RESERVE));
						}
					}
				} else {
					fEndKickoff = false;
				}
			}
		} else {
			getResult().setAnimation(new Animation(AnimationType.KICKOFF_SOLID_DEFENSE));
			game.setTurnMode(TurnMode.SOLID_DEFENCE);
			Team actingTeam = game.getActingTeam();
			for (Player<?> player : actingTeam.getPlayers()) {
				FieldCoordinate fieldCoordinate = game.getFieldModel().getPlayerCoordinate(player);
				if (FieldCoordinateBounds.FIELD.isInBounds(fieldCoordinate)) {
					playersAtCoordinates.put(player.getId(), fieldCoordinate);
					game.getFieldModel().add(new FieldMarker(fieldCoordinate, String.valueOf(player.getNr()), String.valueOf(player.getNr())));
				} else {
					PlayerState playerState = game.getFieldModel().getPlayerState(player);
					if (playerState.getBase() == PlayerState.RESERVE) {
						game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.PRONE));
					}
				}
			}
			int roll = getGameState().getDiceRoller().rollDice(3);
			nrOfPlayersAllowed = roll + 3;
			getResult().addReport(new ReportSolidDefenceRoll(game.getActingTeam().getId(), roll, nrOfPlayersAllowed));
		}
	}

	private boolean validSolidDefence(int movedPlayers) {
		if (movedPlayers > nrOfPlayersAllowed) {
			UtilServerDialog.showDialog(getGameState(), new DialogInvalidSolidDefenceParameter(getGameState().getGame().getActingTeam().getId(), movedPlayers, nrOfPlayersAllowed), false);
			return false;
		}

		return true;
	}

	private void handleTimeout() {

		Game game = getGameState().getGame();
		TurnData turnDataHome = game.getTurnDataHome();
		TurnData turnDataAway = game.getTurnDataAway();

		int kickingTeamTurn = game.isHomePlaying() ? turnDataHome.getTurnNr() : turnDataAway.getTurnNr();

		int turnModifier = kickingTeamTurn >= 6 ? -1 : 1;

		turnDataHome.setTurnNr(turnDataHome.getTurnNr() + turnModifier);
		turnDataAway.setTurnNr(turnDataAway.getTurnNr() + turnModifier);

		getResult().addReport(new ReportKickoffTimeout(kickingTeamTurn, turnModifier));
		getResult().setAnimation(new Animation(AnimationType.KICKOFF_TIMEOUT));
		getResult().setNextAction(StepAction.NEXT_STEP);

	}

	private void handleHighKick() {
		Game game = getGameState().getGame();
		if (game.getTurnMode() == TurnMode.HIGH_KICK) {
			if (fEndKickoff) {
				game.setHomePlaying(!game.isHomePlaying());
				game.setTurnMode(TurnMode.KICKOFF);
				getResult().setNextAction(StepAction.NEXT_STEP);
			}
		} else {
			Player<?> catcher = game.getFieldModel().getPlayer(game.getFieldModel().getBallCoordinate());
			if (fTouchback || (catcher != null)) {
				getResult().setNextAction(StepAction.NEXT_STEP);
			} else {
				game.setHomePlaying(!game.isHomePlaying());
				game.setTurnMode(TurnMode.HIGH_KICK);
				if (game.isHomePlaying()) {
					UtilKickoffSequence.pinPlayersInTacklezones(getGameState(), game.getTeamHome());
				} else {
					UtilKickoffSequence.pinPlayersInTacklezones(getGameState(), game.getTeamAway());
				}
			}
			getResult().setAnimation(new Animation(AnimationType.KICKOFF_HIGH_KICK));
		}
	}

	private void handleBrilliantCoaching() {

		Game game = getGameState().getGame();
		GameResult gameResult = game.getGameResult();

		int rollHome = getGameState().getDiceRoller().rollDice(6);
		int totalHome = rollHome + gameResult.getTeamResultHome().getFanFactor();
		int rollAway = getGameState().getDiceRoller().rollDice(6);
		int totalAway = rollAway + gameResult.getTeamResultAway().getFanFactor();

		totalHome += game.getTeamHome().getAssistantCoaches();
		totalAway += game.getTeamAway().getAssistantCoaches();

		TurnData turnDataHome = game.getTurnDataHome();
		TurnData turnDataAway = game.getTurnDataAway();
		boolean homeCoachBanned = turnDataHome.isCoachBanned();
		boolean awayCoachBanned = turnDataAway.isCoachBanned();

		totalHome += homeCoachBanned ? -1 : 0;
		totalAway += awayCoachBanned ? -1 : 0;

		getResult().setAnimation(new Animation(AnimationType.KICKOFF_BRILLIANT_COACHING));

		String teamId = null;
		boolean homeGainsReRoll = (totalHome > totalAway);
		if (homeGainsReRoll) {
			turnDataHome.setReRolls(turnDataHome.getReRolls() + 1);
			turnDataHome.setReRollsBrilliantCoachingOneDrive(
				turnDataHome.getReRollsBrilliantCoachingOneDrive() + 1);
			teamId = game.getTeamHome().getId();
		}

		boolean awayGainsReRoll = (totalAway > totalHome);
		if (awayGainsReRoll) {
			turnDataAway.setReRolls(turnDataAway.getReRolls() + 1);
			turnDataAway.setReRollsBrilliantCoachingOneDrive(
				turnDataAway.getReRollsBrilliantCoachingOneDrive() + 1);
			teamId = game.getTeamAway().getId();
		}

		getResult().addReport(new ReportKickoffExtraReRoll(rollHome, rollAway, teamId));
		getResult().setNextAction(StepAction.NEXT_STEP);

	}


	private void handleExtraReRoll() {

		Game game = getGameState().getGame();
		GameResult gameResult = game.getGameResult();

		int rollHome = getGameState().getDiceRoller().rollExtraReRoll();
		int fanFavouritesHome = UtilPlayer.findPlayersOnPitchWithProperty(game, game.getTeamHome(),
			NamedProperties.increasesTeamsFame).length;
		int totalHome = rollHome + gameResult.getTeamResultHome().getFame() + fanFavouritesHome;
		int rollAway = getGameState().getDiceRoller().rollExtraReRoll();
		int fanFavouritesAway = UtilPlayer.findPlayersOnPitchWithProperty(game, game.getTeamAway(),
			NamedProperties.increasesTeamsFame).length;
		int totalAway = rollAway + gameResult.getTeamResultAway().getFame() + fanFavouritesAway;
		if (fKickoffResult.isFanReRoll()) {
			totalHome += game.getTeamHome().getCheerleaders();
			totalAway += game.getTeamAway().getCheerleaders();
			getResult().setAnimation(new Animation(AnimationType.KICKOFF_CHEERING_FANS));
		}
		if (fKickoffResult.isCoachReRoll()) {
			boolean homeCoachBanned = game.getTurnDataHome().isCoachBanned();
			boolean awayCoachBanned = game.getTurnDataAway().isCoachBanned();

			totalHome += game.getTeamHome().getAssistantCoaches();
			totalAway += game.getTeamAway().getAssistantCoaches();

			totalHome += homeCoachBanned ? -1 : 0;
			totalAway += awayCoachBanned ? -1 : 0;

			getResult().setAnimation(new Animation(AnimationType.KICKOFF_BRILLIANT_COACHING));
		}

		boolean homeGainsReRoll = (totalHome >= totalAway);
		if (homeGainsReRoll) {
			game.getTurnDataHome().setReRolls(game.getTurnDataHome().getReRolls() + 1);
		}
		boolean awayGainsReRoll = (totalAway >= totalHome);
		if (awayGainsReRoll) {
			game.getTurnDataAway().setReRolls(game.getTurnDataAway().getReRolls() + 1);
		}

		getResult()
			.addReport(new com.fumbbl.ffb.report.bb2016.ReportKickoffExtraReRoll(fKickoffResult, rollHome, homeGainsReRoll, rollAway, awayGainsReRoll));
		getResult().setNextAction(StepAction.NEXT_STEP);

	}

	private void handleWeatherChange() {

		Game game = getGameState().getGame();

		int[] weatherRoll = getGameState().getDiceRoller().rollWeather();
		Weather weather = DiceInterpreter.getInstance().interpretRollWeather(weatherRoll);
		game.getFieldModel().setWeather(weather);
		getResult().addReport(new ReportWeather(weather, weatherRoll));

		switch (game.getFieldModel().getWeather()) {
			case BLIZZARD:
				getResult().setAnimation(new Animation(AnimationType.KICKOFF_BLIZZARD));
				break;
			case POURING_RAIN:
				getResult().setAnimation(new Animation(AnimationType.KICKOFF_POURING_RAIN));
				break;
			case SWELTERING_HEAT:
				getResult().setAnimation(new Animation(AnimationType.KICKOFF_SWELTERING_HEAT));
				for (Player<?> player : game.getPlayers()) {
					PlayerState playerState = game.getFieldModel().getPlayerState(player);
					if (playerState.getBase() == PlayerState.EXHAUSTED) {
						game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.RESERVE));
					}
				}
				break;
			case VERY_SUNNY:
				getResult().setAnimation(new Animation(AnimationType.KICKOFF_VERY_SUNNY));
				break;
			case NICE:
				getResult().setAnimation(new Animation(AnimationType.KICKOFF_NICE));
				break;
			default:
				break;
		}

		if (!fTouchback && (Weather.NICE == game.getFieldModel().getWeather())) {
			FieldCoordinate lastValidCoordinate = game.getFieldModel().getBallCoordinate();
			List<Direction> directions = new ArrayList<>();
			List<Integer> rolls = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				int roll = getGameState().getDiceRoller().rollScatterDirection();
				Direction direction = DiceInterpreter.getInstance().interpretScatterDirectionRoll(game, roll);
				FieldCoordinate ballCoordinateEnd = UtilServerCatchScatterThrowIn.findScatterCoordinate(lastValidCoordinate,
					direction, 1);
				fTouchback = !fKickoffBounds.isInBounds(ballCoordinateEnd);
				directions.add(direction);
				rolls.add(roll);
				if (!fTouchback) {
					game.getFieldModel().setBallCoordinate(ballCoordinateEnd);
					lastValidCoordinate = ballCoordinateEnd;
				} else {
					game.getFieldModel().setBallCoordinate(lastValidCoordinate);
					break;
				}
			}
			getResult().addReport(new ReportScatterBall(directions.toArray(new Direction[0]), rolls.stream().mapToInt(integer -> integer).toArray(), true));
		}

		publishParameter(new StepParameter(StepParameterKey.TOUCHBACK, fTouchback));
		getResult().setNextAction(StepAction.NEXT_STEP);

	}

	private void handleQuickSnap() {
		Game game = getGameState().getGame();
		if (game.getTurnMode() == TurnMode.QUICK_SNAP) {
			if (StringTool.isProvided(movedPlayer) && toCoordinate != null) {
				if (nrOfMovedPlayers < nrOfPlayersAllowed) {
					nrOfMovedPlayers++;
					UtilServerSetup.setupPlayer(getGameState(), movedPlayer,
						toCoordinate);

					int activePlayersOnField = (int) Arrays.stream(game.getActingTeam().getPlayers())
						.filter(player ->
							FieldCoordinateBounds.FIELD.isInBounds(game.getFieldModel().getPlayerCoordinate(player))
								&& game.getFieldModel().getPlayerState(player).isActive()
						)
						.count();

					getResult().addReport(new ReportKickoffSequenceActivationsCount(activePlayersOnField, nrOfMovedPlayers, nrOfPlayersAllowed));

					if (nrOfMovedPlayers == nrOfPlayersAllowed) {
						fEndKickoff = true;
						getResult().addReport(new ReportKickoffSequenceActivationsExhausted(true));
					} else if (activePlayersOnField == 0) {
						fEndKickoff = true;
						getResult().addReport(new ReportKickoffSequenceActivationsExhausted(false));
					}
				} else {
					// In case of lag we might get more requests to move a player than are allowed, so we reset the coordinate also in the client
					Player<?> player = game.getPlayerById(movedPlayer);
					game.getFieldModel().setPlayerCoordinate(player, game.getFieldModel().getPlayerCoordinate(player));
				}
				movedPlayer = null;
				toCoordinate = null;
			}
			if (fEndKickoff) {
				endQuickSnap(game);
			}
		} else {
			game.setHomePlaying(!game.isHomePlaying());
			game.setTurnMode(TurnMode.QUICK_SNAP);
			getResult().setAnimation(new Animation(AnimationType.KICKOFF_QUICK_SNAP));
			int roll = getGameState().getDiceRoller().rollDice(3);
			nrOfPlayersAllowed = roll + 3;
			getResult().addReport(new ReportQuickSnapRoll(game.getActingTeam().getId(), roll, nrOfPlayersAllowed));
			Arrays.stream(game.getActingTeam().getPlayers()).filter(player -> ArrayTool.isProvided(UtilPlayer.findAdjacentPlayersWithTacklezones(
				getGameState().getGame(),
				game.getOtherTeam(game.getActingTeam()),
				game.getFieldModel().getPlayerCoordinate(player),
				false
			)))
				.forEach(player -> game.getFieldModel().setPlayerState(player, game.getFieldModel().getPlayerState(player).changeActive(false)));

			if (Arrays.stream(game.getActingTeam().getPlayers()).noneMatch(player -> game.getFieldModel().getPlayerState(player).isActive())) {
				getResult().addReport(new ReportKickoffSequenceActivationsExhausted(false));
				endQuickSnap(game);
			}

		}
	}

	private void endQuickSnap(Game game) {
		game.setHomePlaying(!game.isHomePlaying());
		game.setTurnMode(TurnMode.KICKOFF);
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private void handleBlitz() {
		getResult().setAnimation(new Animation(AnimationType.KICKOFF_BLITZ));
		getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnBlitz);
	}

	private void handleOfficiousRef() {

		getResult().setAnimation(new Animation(AnimationType.KICKOFF_OFFICIOUS_REF));
		UtilServerGame.syncGameModel(this);

		Game game = getGameState().getGame();
		GameResult gameResult = game.getGameResult();

		int rollHome = getGameState().getDiceRoller().rollThrowARock();
		int totalHome = rollHome + gameResult.getTeamResultHome().getFanFactor();
		int rollAway = getGameState().getDiceRoller().rollThrowARock();
		int totalAway = rollAway + gameResult.getTeamResultAway().getFanFactor();

		String playerIdHome = null;
		String playerIdAway = null;
		List<String> playerIds = new ArrayList<>();

		if (totalAway >= totalHome) {
			Player<?> homePlayer = getGameState().getDiceRoller().randomPlayer(playersOnField(game, game.getTeamHome()));
			if (homePlayer != null) {
				playerIdHome = homePlayer.getId();
				playerIds.add(playerIdHome);
			}
		}
		if (totalHome >= totalAway) {
			Player<?> awayPlayer = getGameState().getDiceRoller().randomPlayer(playersOnField(game, game.getTeamAway()));
			if (awayPlayer != null) {
				playerIdAway = awayPlayer.getId();
				playerIds.add(playerIdAway);
			}
		}

		getResult().addReport(new ReportKickoffOfficiousRef(rollHome, rollAway, playerIds));

		Set<StepParameterKey> parametersToConsume = new HashSet<StepParameterKey>() {{
			add(StepParameterKey.END_TURN);
			add(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE);
			add(StepParameterKey.FOULER_HAS_BALL);
			add(StepParameterKey.ARGUE_THE_CALL_SUCCESSFUL);
		}};

		Sequence sequence = new Sequence(getGameState());

		if (playerIdHome != null) {
			insertSteps(game, playerIdHome, parametersToConsume, sequence, ApothecaryMode.HOME);
		}

		if (playerIdAway != null) {
			insertSteps(game, playerIdAway, parametersToConsume, sequence, ApothecaryMode.AWAY);
		}

		getGameState().getStepStack().push(sequence.getSequence());

		getResult().setNextAction(StepAction.NEXT_STEP);

	}

	private void insertSteps(Game game, String playerId, Set<StepParameterKey> parametersToConsume, Sequence sequence, ApothecaryMode apothecaryMode) {
		int roll = getGameState().getDiceRoller().rollDice(6);
		getResult().addReport(new ReportOfficiousRefRoll(roll, playerId));
		if (roll == 1) {
			getResult().setSound(SoundId.WHISTLE);
			sequence.add(StepId.SET_ACTING_PLAYER_AND_TEAM, StepParameter.from(StepParameterKey.PLAYER_ID, playerId));
			sequence.add(StepId.BRIBES, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_FOULING));
			sequence.add(StepId.EJECT_PLAYER, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_FOULING));
			sequence.add(StepId.CONSUME_PARAMETER, IStepLabel.END_FOULING, StepParameter.from(StepParameterKey.CONSUME_PARAMETER, parametersToConsume));
		} else {
			publishParameters(UtilServerInjury.stunPlayer(this, game.getPlayerById(playerId), apothecaryMode));
		}
	}

	private void handlePitchInvasion() {

		Game game = getGameState().getGame();
		GameResult gameResult = game.getGameResult();

		int rollHome = getGameState().getDiceRoller().rollDice(6);
		int rollAway = getGameState().getDiceRoller().rollDice(6);

		int totalHome = rollHome + gameResult.getTeamResultHome().getFanFactor();
		int totalAway = rollAway + gameResult.getTeamResultAway().getFanFactor();

		List<String> affectedPlayers = new ArrayList<>();

		if (totalHome <= totalAway) {
			affectedPlayers.addAll(stunPlayers(game.getTeamHome(), game.getFieldModel()));
		}

		if (totalHome >= totalAway) {
			affectedPlayers.addAll(stunPlayers(game.getTeamAway(), game.getFieldModel()));
		}

		getResult().addReport(new ReportKickoffPitchInvasion(rollHome, rollAway, affectedPlayers));

		getResult().setAnimation(new Animation(AnimationType.KICKOFF_PITCH_INVASION));
		getResult().setNextAction(StepAction.NEXT_STEP);

	}

	private List<String> stunPlayers(Team team, FieldModel fieldModel) {
		List<String> affectedPlayers = new ArrayList<>();
		int stunned = getGameState().getDiceRoller().rollDice(3);
		List<Player<?>> standing = Arrays.stream(team.getPlayers()).filter(player -> fieldModel.getPlayerState(player).getBase() == PlayerState.STANDING).collect(Collectors.toList());
		for (int i = 0; i < stunned; i++) {
			int index = getGameState().getDiceRoller().rollDice(standing.size()) - 1;
			Player<?> stunnedPlayer = standing.get(index);
			UtilServerInjury.stunPlayer(this, stunnedPlayer, ApothecaryMode.HOME);
			standing.remove(stunnedPlayer);
			affectedPlayers.add(stunnedPlayer.getId());
		}
		return affectedPlayers;
	}

	private Player<?>[] playersOnField(Game pGame, Team pTeam) {
		List<Player<?>> playersOnField = new ArrayList<>();
		for (Player<?> player : pTeam.getPlayers()) {
			if (isPlayerOnField(pGame, player)) {
				playersOnField.add(player);
			}
		}
		return playersOnField.toArray(new Player[0]);
	}

	private boolean isPlayerOnField(Game pGame, Player<?> pPlayer) {
		FieldCoordinate playerCoordinate = pGame.getFieldModel().getPlayerCoordinate(pPlayer);
		return (FieldCoordinateBounds.FIELD.isInBounds(playerCoordinate));
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
		IServerJsonOption.GOTO_LABEL_ON_BLITZ.addTo(jsonObject, fGotoLabelOnBlitz);
		IServerJsonOption.KICKOFF_RESULT.addTo(jsonObject, fKickoffResult);
		IServerJsonOption.TOUCHBACK.addTo(jsonObject, fTouchback);
		if (fKickoffBounds != null) {
			IServerJsonOption.KICKOFF_BOUNDS.addTo(jsonObject, fKickoffBounds.toJsonValue());
		}
		IServerJsonOption.END_KICKOFF.addTo(jsonObject, fEndKickoff);
		IServerJsonOption.PLAYERS_AT_COORDINATES.addTo(jsonObject, playersAtCoordinates);
		IServerJsonOption.NR_OF_PLAYERS_ALLOWED.addTo(jsonObject, nrOfPlayersAllowed);
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, movedPlayer);
		IServerJsonOption.COORDINATE_TO.addTo(jsonObject, toCoordinate);
		IServerJsonOption.NR_OF_PLAYERS.addTo(jsonObject, nrOfMovedPlayers);
		return jsonObject;
	}

	@Override
	public StepApplyKickoffResult initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(source, jsonObject);
		fGotoLabelOnBlitz = IServerJsonOption.GOTO_LABEL_ON_BLITZ.getFrom(source, jsonObject);
		fKickoffResult = (KickoffResult) IServerJsonOption.KICKOFF_RESULT.getFrom(source, jsonObject);
		fTouchback = IServerJsonOption.TOUCHBACK.getFrom(source, jsonObject);
		fKickoffBounds = null;
		JsonObject kickoffBoundsObject = IServerJsonOption.KICKOFF_BOUNDS.getFrom(source, jsonObject);
		if (kickoffBoundsObject != null) {
			fKickoffBounds = new FieldCoordinateBounds().initFrom(source, kickoffBoundsObject);
		}
		fEndKickoff = IServerJsonOption.END_KICKOFF.getFrom(source, jsonObject);
		playersAtCoordinates.putAll(IServerJsonOption.PLAYERS_AT_COORDINATES.getFrom(source, jsonObject));
		nrOfPlayersAllowed = IServerJsonOption.NR_OF_PLAYERS_ALLOWED.getFrom(source, jsonObject);
		movedPlayer = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		toCoordinate = IServerJsonOption.COORDINATE_TO.getFrom(source, jsonObject);
		nrOfMovedPlayers = IServerJsonOption.NR_OF_PLAYERS.getFrom(source, jsonObject);
		return this;
	}

}
