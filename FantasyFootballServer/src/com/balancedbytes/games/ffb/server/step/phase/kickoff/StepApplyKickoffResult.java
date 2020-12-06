package com.balancedbytes.games.ffb.server.step.phase.kickoff;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.Direction;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.Inducement;
import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.KickoffResult;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.model.AnimationType;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.model.InducementSet;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.TurnData;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.net.commands.ClientCommandSetupPlayer;
import com.balancedbytes.games.ffb.report.ReportKickoffExtraReRoll;
import com.balancedbytes.games.ffb.report.ReportKickoffPitchInvasion;
import com.balancedbytes.games.ffb.report.ReportKickoffRiot;
import com.balancedbytes.games.ffb.report.ReportKickoffThrowARock;
import com.balancedbytes.games.ffb.report.ReportScatterBall;
import com.balancedbytes.games.ffb.report.ReportWeather;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.InjuryType.InjuryTypeThrowARock;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.step.UtilServerSteps;
import com.balancedbytes.games.ffb.server.util.UtilServerCatchScatterThrowIn;
import com.balancedbytes.games.ffb.server.util.UtilServerGame;
import com.balancedbytes.games.ffb.server.util.UtilServerInjury;
import com.balancedbytes.games.ffb.server.util.UtilServerSetup;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in kickoff sequence to apply the kickoff result.
 *
 * Needs to be initialized with stepParameter GOTO_LABEL_END.
 * 
 * Needs to be initialized with stepParameter SKIP_PAST_LABEL_ON_BLITZ.
 * 
 * Expects stepParameter KICKOFF_BOUNDS to be set by a preceding step. Expects
 * stepParameter KICKOFF_RESULT to be set by a preceding step. Expects
 * stepParameter TOUCHBACK to be set by a preceding step.
 * 
 * Sets stepParameter TOUCHBACK for all steps on the stack. Sets stepParameter
 * INJURY_RESULT for all steps on the stack.
 * 
 * @author Kalimar
 */
public final class StepApplyKickoffResult extends AbstractStep {

	private String fGotoLabelOnEnd;
	private String fGotoLabelOnBlitz;
	private KickoffResult fKickoffResult;
	private boolean fTouchback;
	private FieldCoordinateBounds fKickoffBounds;
	private boolean fEndKickoff;

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
				UtilServerSetup.setupPlayer(getGameState(), setupPlayerCommand.getPlayerId(),
						setupPlayerCommand.getCoordinate());
				commandStatus = StepCommandStatus.SKIP_STEP;
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
		case RIOT:
			handleRiot();
			break;
		case PERFECT_DEFENCE:
			handlePerfectDefense();
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
			handleExtraReRoll();
			break;
		case QUICK_SNAP:
			handleQuickSnap();
			break;
		case BLITZ:
			handleBlitz();
			break;
		case THROW_A_ROCK:
			handleThrowARock();
			break;
		case PITCH_INVASION:
			handlePitchInvasion();
			break;
		}
	}

	private void handleGetTheRef() {
		Game game = getGameState().getGame();
		InducementSet inducementSetHome = game.getTurnDataHome().getInducementSet();
		Inducement bribesHome = inducementSetHome.get(InducementType.BRIBES);
		if (bribesHome != null) {
			bribesHome.setValue(bribesHome.getValue() + 1);
		} else {
			bribesHome = new Inducement(InducementType.BRIBES, 1);
		}
		inducementSetHome.addInducement(bribesHome);
		InducementSet inducementSetAway = game.getTurnDataAway().getInducementSet();
		Inducement bribesAway = inducementSetAway.get(InducementType.BRIBES);
		if (bribesAway != null) {
			bribesAway.setValue(bribesAway.getValue() + 1);
		} else {
			bribesAway = new Inducement(InducementType.BRIBES, 1);
		}
		inducementSetAway.addInducement(bribesAway);
		getResult().setAnimation(new Animation(AnimationType.KICKOFF_GET_THE_REF));
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private void handlePerfectDefense() {
		Game game = getGameState().getGame();
		if (game.getTurnMode() == TurnMode.PERFECT_DEFENCE) {
			if (fEndKickoff) {
				if (UtilKickoffSequence.checkSetup(getGameState(), game.isHomePlaying(), getGameState().getKickingSwarmers())) {
					getGameState().setKickingSwarmers(0);
					game.setTurnMode(TurnMode.KICKOFF);
					getResult().setNextAction(StepAction.NEXT_STEP);
				} else {
					fEndKickoff = false;
				}
			}
		} else {
			getResult().setAnimation(new Animation(AnimationType.KICKOFF_PERFECT_DEFENSE));
			game.setTurnMode(TurnMode.PERFECT_DEFENCE);
		}
	}

	private void handleRiot() {

		Game game = getGameState().getGame();
		TurnData turnDataHome = game.getTurnDataHome();
		TurnData turnDataAway = game.getTurnDataAway();

		int riotRoll = 0;
		int turnModifier = 0;
		if ((game.isHomePlaying() && (turnDataAway.getTurnNr() == 0))
				|| (!game.isHomePlaying() && (turnDataHome.getTurnNr() == 0))) {
			turnModifier = 1;
		}
		if ((game.isHomePlaying() && (turnDataAway.getTurnNr() == 7))
				|| (!game.isHomePlaying() && (turnDataHome.getTurnNr() == 7))) {
			turnModifier = -1;
		}
		if (turnModifier == 0) {
			riotRoll = getGameState().getDiceRoller().rollRiot();
			turnModifier = DiceInterpreter.getInstance().interpretRiotRoll(riotRoll);
		}

		turnDataHome.setTurnNr(turnDataHome.getTurnNr() + turnModifier);
		if (turnDataHome.getTurnNr() < 0) {
			turnDataHome.setTurnNr(0);
		}

		turnDataAway.setTurnNr(turnDataAway.getTurnNr() + turnModifier);
		if (turnDataAway.getTurnNr() < 0) {
			turnDataAway.setTurnNr(0);
		}

		getResult().addReport(new ReportKickoffRiot(riotRoll, turnModifier));
		getResult().setAnimation(new Animation(AnimationType.KICKOFF_RIOT));

		if ((turnDataHome.getTurnNr() > 8) || (turnDataAway.getTurnNr() > 8)) {
			getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
		} else {
			getResult().setNextAction(StepAction.NEXT_STEP);
		}

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
			Player catcher = game.getFieldModel().getPlayer(game.getFieldModel().getBallCoordinate());
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
		if (fKickoffResult == KickoffResult.CHEERING_FANS) {
			totalHome += game.getTeamHome().getCheerleaders();
			totalAway += game.getTeamAway().getCheerleaders();
			getResult().setAnimation(new Animation(AnimationType.KICKOFF_CHEERING_FANS));
		}
		if (fKickoffResult == KickoffResult.BRILLIANT_COACHING) {
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
				.addReport(new ReportKickoffExtraReRoll(fKickoffResult, rollHome, homeGainsReRoll, rollAway, awayGainsReRoll));
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
			for (Player player : game.getPlayers()) {
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
			int roll = getGameState().getDiceRoller().rollScatterDirection();
			Direction direction = DiceInterpreter.getInstance().interpretScatterDirectionRoll(roll);
			FieldCoordinate ballCoordinateEnd = UtilServerCatchScatterThrowIn.findScatterCoordinate(lastValidCoordinate,
					direction, 1);
			fTouchback = !fKickoffBounds.isInBounds(ballCoordinateEnd);
			if (!fTouchback) {
				game.getFieldModel().setBallCoordinate(ballCoordinateEnd);
				lastValidCoordinate = ballCoordinateEnd;
			} else {
				game.getFieldModel().setBallCoordinate(lastValidCoordinate);
			}
			getResult().addReport(new ReportScatterBall(new Direction[] { direction }, new int[] { roll }, true));
		}

		publishParameter(new StepParameter(StepParameterKey.TOUCHBACK, fTouchback));
		getResult().setNextAction(StepAction.NEXT_STEP);

	}

	private void handleQuickSnap() {
		Game game = getGameState().getGame();
		if (game.getTurnMode() == TurnMode.QUICK_SNAP) {
			if (fEndKickoff) {
				game.setHomePlaying(!game.isHomePlaying());
				game.setTurnMode(TurnMode.KICKOFF);
				getResult().setNextAction(StepAction.NEXT_STEP);
			}
		} else {
			game.setHomePlaying(!game.isHomePlaying());
			game.setTurnMode(TurnMode.QUICK_SNAP);
			getResult().setAnimation(new Animation(AnimationType.KICKOFF_QUICK_SNAP));
		}
	}

	private void handleBlitz() {
		getResult().setAnimation(new Animation(AnimationType.KICKOFF_BLITZ));
		getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnBlitz);
	}

	private void handleThrowARock() {

		getResult().setAnimation(new Animation(AnimationType.KICKOFF_THROW_A_ROCK));
		UtilServerGame.syncGameModel(this);

		Game game = getGameState().getGame();
		GameResult gameResult = game.getGameResult();

		int rollHome = getGameState().getDiceRoller().rollThrowARock();
		int fanFavouritesHome = UtilPlayer.findPlayersOnPitchWithProperty(game, game.getTeamHome(),
				NamedProperties.increasesTeamsFame).length;
		int totalHome = rollHome + gameResult.getTeamResultHome().getFame() + fanFavouritesHome;
		int rollAway = getGameState().getDiceRoller().rollThrowARock();
		int fanFavouritesAway = UtilPlayer.findPlayersOnPitchWithProperty(game, game.getTeamAway(),
				NamedProperties.increasesTeamsFame).length;
		int totalAway = rollAway + gameResult.getTeamResultAway().getFame() + fanFavouritesAway;

		String hitPlayerIdHome = null;
		String hitPlayerIdAway = null;

		if (totalAway >= totalHome) {
			Player homePlayer = getGameState().getDiceRoller().randomPlayer(playersOnField(game, game.getTeamHome()));
			if (homePlayer != null) {
				hitPlayerIdHome = homePlayer.getId();
			}
		}
		if (totalHome >= totalAway) {
			Player awayPlayer = getGameState().getDiceRoller().randomPlayer(playersOnField(game, game.getTeamAway()));
			if (awayPlayer != null) {
				hitPlayerIdAway = awayPlayer.getId();
			}
		}

		String[] hitPlayerIds = null;
		if ((hitPlayerIdHome != null) && (hitPlayerIdAway != null)) {
			hitPlayerIds = new String[] { hitPlayerIdHome, hitPlayerIdAway };
		} else {
			if (hitPlayerIdHome != null) {
				hitPlayerIds = new String[] { hitPlayerIdHome };
			}
			if (hitPlayerIdAway != null) {
				hitPlayerIds = new String[] { hitPlayerIdAway };
			}
		}
		getResult().addReport(new ReportKickoffThrowARock(rollHome, rollAway, hitPlayerIds));

		if (hitPlayerIdHome != null) {

			Player player = game.getPlayerById(hitPlayerIdHome);
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);

			FieldCoordinate startCoordinate = null;
			if (FieldCoordinateBounds.UPPER_HALF.isInBounds(playerCoordinate)) {
				startCoordinate = new FieldCoordinate(getGameState().getDiceRoller().rollXCoordinate(), 0);
			} else {
				startCoordinate = new FieldCoordinate(getGameState().getDiceRoller().rollXCoordinate(), 14);
			}
			getResult().setAnimation(new Animation(AnimationType.THROW_A_ROCK, startCoordinate, playerCoordinate, null));
			UtilServerGame.syncGameModel(this);

			publishParameters(UtilServerInjury.dropPlayer(this, player, ApothecaryMode.HOME));
			publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, UtilServerInjury.handleInjury(this,
					new InjuryTypeThrowARock(), null, player, playerCoordinate, null, ApothecaryMode.HOME)));

		}

		if (hitPlayerIdAway != null) {

			Player player = game.getPlayerById(hitPlayerIdAway);
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);

			FieldCoordinate startCoordinate = null;
			if (FieldCoordinateBounds.UPPER_HALF.isInBounds(playerCoordinate)) {
				startCoordinate = new FieldCoordinate(getGameState().getDiceRoller().rollXCoordinate(), 0);
			} else {
				startCoordinate = new FieldCoordinate(getGameState().getDiceRoller().rollXCoordinate(), 14);
			}
			getResult().setAnimation(new Animation(AnimationType.THROW_A_ROCK, startCoordinate, playerCoordinate, null));
			UtilServerGame.syncGameModel(this);

			publishParameters(UtilServerInjury.dropPlayer(this, player, ApothecaryMode.AWAY));
			publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, UtilServerInjury.handleInjury(this,
					new InjuryTypeThrowARock(), null, player, playerCoordinate, null, ApothecaryMode.AWAY)));

		}

		getResult().setNextAction(StepAction.NEXT_STEP);

	}

	private void handlePitchInvasion() {

		Game game = getGameState().getGame();
		GameResult gameResult = game.getGameResult();

		int fanFavouritesHome = UtilPlayer.findPlayersOnPitchWithProperty(game, game.getTeamHome(),
				NamedProperties.increasesTeamsFame).length;
		int fanFavouritesAway = UtilPlayer.findPlayersOnPitchWithProperty(game, game.getTeamAway(),
				NamedProperties.increasesTeamsFame).length;

		Player[] playersHome = game.getTeamHome().getPlayers();
		int[] rollsHome = new int[playersHome.length];
		boolean[] playerAffectedHome = new boolean[playersHome.length];
		for (int i = 0; i < playersHome.length; i++) {
			if (isPlayerOnField(game, playersHome[i])) {
				rollsHome[i] = getGameState().getDiceRoller().rollPitchInvasion();
				playerAffectedHome[i] = DiceInterpreter.getInstance().isAffectedByPitchInvasion(rollsHome[i],
						gameResult.getTeamResultAway().getFame() + fanFavouritesAway);
				if (playerAffectedHome[i]) {
					UtilServerInjury.stunPlayer(this, playersHome[i], ApothecaryMode.HOME);
				}
			}
		}

		Player[] playersAway = game.getTeamAway().getPlayers();
		int[] rollsAway = new int[playersAway.length];
		boolean[] playerAffectedAway = new boolean[playersAway.length];
		for (int i = 0; i < playersAway.length; i++) {
			if (isPlayerOnField(game, playersAway[i])) {
				rollsAway[i] = getGameState().getDiceRoller().rollPitchInvasion();
				playerAffectedAway[i] = DiceInterpreter.getInstance().isAffectedByPitchInvasion(rollsAway[i],
						gameResult.getTeamResultHome().getFame() + fanFavouritesHome);
				if (playerAffectedAway[i]) {
					UtilServerInjury.stunPlayer(this, playersAway[i], ApothecaryMode.DEFENDER);
				}
			}
		}

		getResult().addReport(new ReportKickoffPitchInvasion(rollsHome, playerAffectedHome, rollsAway, playerAffectedAway));

		getResult().setAnimation(new Animation(AnimationType.KICKOFF_PITCH_INVASION));
		getResult().setNextAction(StepAction.NEXT_STEP);

	}

	private Player[] playersOnField(Game pGame, Team pTeam) {
		List<Player> playersOnField = new ArrayList<Player>();
		for (Player player : pTeam.getPlayers()) {
			if (isPlayerOnField(pGame, player)) {
				playersOnField.add(player);
			}
		}
		return playersOnField.toArray(new Player[playersOnField.size()]);
	}

	private boolean isPlayerOnField(Game pGame, Player pPlayer) {
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
		return jsonObject;
	}

	@Override
	public StepApplyKickoffResult initFrom(JsonValue pJsonValue) {
		super.initFrom(pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(jsonObject);
		fGotoLabelOnBlitz = IServerJsonOption.GOTO_LABEL_ON_BLITZ.getFrom(jsonObject);
		fKickoffResult = (KickoffResult) IServerJsonOption.KICKOFF_RESULT.getFrom(jsonObject);
		fTouchback = IServerJsonOption.TOUCHBACK.getFrom(jsonObject);
		fKickoffBounds = null;
		JsonObject kickoffBoundsObject = IServerJsonOption.KICKOFF_BOUNDS.getFrom(jsonObject);
		if (kickoffBoundsObject != null) {
			fKickoffBounds = new FieldCoordinateBounds().initFrom(kickoffBoundsObject);
		}
		fEndKickoff = IServerJsonOption.END_KICKOFF.getFrom(jsonObject);
		return this;
	}

}
