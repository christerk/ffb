package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.Constant;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.LeaderState;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.change.ModelChangeList;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.net.ServerStatus;
import com.fumbbl.ffb.report.ReportInducement;
import com.fumbbl.ffb.report.ReportLeader;
import com.fumbbl.ffb.report.ReportList;
import com.fumbbl.ffb.report.ReportMasterChefRoll;
import com.fumbbl.ffb.report.ReportPlayerAction;
import com.fumbbl.ffb.report.ReportStartHalf;
import com.fumbbl.ffb.report.bb2020.ReportSkillWasted;
import com.fumbbl.ffb.report.bb2020.ReportTwoForOne;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilActingPlayer;
import com.fumbbl.ffb.util.UtilCards;
import org.eclipse.jetty.websocket.api.Session;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Kalimar
 */
public class UtilServerGame {

	public static boolean syncGameModel(IStep pStep) {
		if ((pStep != null) && (pStep.getResult() != null) && pStep.getResult().isSynchronize()) {
			ReportList reportList = pStep.getResult().getReportList();
			Animation animation = pStep.getResult().getAnimation();
			SoundId sound = pStep.getResult().getSound();
			pStep.getResult().reset();
			return syncGameModel(pStep.getGameState(), reportList, animation, sound);
		} else {
			return false;
		}
	}

	public static boolean syncGameModel(GameState pGameState, ReportList pReportList, Animation pAnimation,
																			SoundId pSound) {
		boolean synced = false;
		Game game = pGameState.getGame();
		FantasyFootballServer server = pGameState.getServer();
		UtilServerTimer.syncTime(pGameState, System.currentTimeMillis());
		ModelChangeList modelChanges = pGameState.fetchChanges();
		if ((modelChanges.size() > 0) || ((pReportList != null) && (pReportList.size() > 0)) || (pAnimation != null)
			|| (pSound != null)) {
			server.getCommunication().sendModelSync(pGameState, modelChanges, pReportList, pAnimation, pSound,
				game.getGameTime(), game.getTurnTime());
			synced = true;
		}
		return synced;
	}

	public static void changeActingPlayer(IStep pStep, String pActingPlayerId, PlayerAction pPlayerAction,
																				boolean jumping) {
		Game game = pStep.getGameState().getGame();
		PlayerAction oldPlayerAction = game.getActingPlayer().getPlayerAction();

		PlayerAction actualAction = pPlayerAction;

		if (pPlayerAction != null && pPlayerAction.getDelegate() != null) {
			actualAction = pPlayerAction.getDelegate();
		}

		if (UtilActingPlayer.changeActingPlayer(game, pActingPlayerId, actualAction, jumping) && (actualAction != null)
			&& ((oldPlayerAction == null) || (pPlayerAction.getType() != oldPlayerAction.getType()))) {
			if (oldPlayerAction == null) {
				pStep.getResult().setSound(SoundId.CLICK);
			}
			pStep.getResult().addReport(new ReportPlayerAction(pActingPlayerId, pPlayerAction));
		}
	}

	public static void startHalf(IStep pStep, int pHalf) {
		GameState gameState = pStep.getGameState();
		Game game = gameState.getGame();
		game.setHalf(pHalf);
		game.getTurnDataHome().setTurnNr(0);
		game.getTurnDataAway().setTurnNr(0);
		if (game.isHomeFirstOffense()) {
			game.setHomePlaying(game.getHalf() % 2 == 0);
		} else {
			game.setHomePlaying(game.getHalf() % 2 > 0);
		}
		game.getFieldModel().setBallCoordinate(null);
		game.getFieldModel().setBallInPlay(false);
		pStep.getResult().addReport(new ReportStartHalf(game.getHalf()));
		// handle Apothecaries + Wandering Apothecaries
		if (game.getHalf() < 2) {
			addApothecaries(pStep, true);
			addApothecaries(pStep, false);
		}
		// handle ReRolls + Extra Team Training
		if (game.getHalf() < 3) {
			addReRolls(pStep, true);
			addReRolls(pStep, false);

			// handle Master Chefs
			int reRollsStolenHome = rollMasterChef(pStep, true);
			int reRollsStolenAway = rollMasterChef(pStep, false);
			int delta = reRollsStolenHome - reRollsStolenAway;
			if (delta > 0) {
				game.getTurnDataHome().setReRolls(game.getTurnDataHome().getReRolls() + delta);
				game.getTurnDataAway().setReRolls(game.getTurnDataAway().getReRolls() - delta);
				if (game.getTurnDataAway().getReRolls() < 0) {
					game.getTurnDataAway().setReRolls(0);
				}
			}
			if (delta < 0) {
				delta = -delta;
				game.getTurnDataAway().setReRolls(game.getTurnDataAway().getReRolls() + delta);
				game.getTurnDataHome().setReRolls(game.getTurnDataHome().getReRolls() - delta);
				if (game.getTurnDataHome().getReRolls() < 0) {
					game.getTurnDataHome().setReRolls(0);
				}
			}
		}
		resetLeaderState(game);
		updatePlayerStateDependentProperties(pStep);
		resetSpecialSkillsAtHalfTime(game);

	}

	public static void prepareForSetup(Game game) {
		prepareForSetup(game, game.getTeamHome());
		prepareForSetup(game, game.getTeamAway());
	}

	private static void prepareForSetup(Game game, Team team) {
		Map<Boolean, List<Player<?>>> groupedPlayers = Arrays.stream(team.getPlayers()).filter(player -> game.getFieldModel().getPlayerState(player).getBase() == PlayerState.RESERVE)
			.collect(Collectors.groupingBy(player -> player.hasSkillProperty(NamedProperties.canJoinTeamIfLessThanEleven)));
		List<Player<?>> players = groupedPlayers.get(false);
		List<Player<?>> keenPlayers = groupedPlayers.get(true);
		if (keenPlayers != null && !keenPlayers.isEmpty()) {
			if (players != null && players.size() >= 11) {
				keenPlayers.stream().filter(player -> game.getFieldModel().getPlayerState(player).getBase() == PlayerState.RESERVE)
					.forEach(player -> {
						PlayerState playerState = game.getFieldModel().getPlayerState(player);
						game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.SETUP_PREVENTED));
					});
			}
		}
	}

	private static void resetLeaderState(Game pGame) {
		if (pGame.getHalf() <= 2) {
			pGame.getTurnDataHome().setLeaderState(LeaderState.NONE);
			pGame.getTurnDataAway().setLeaderState(LeaderState.NONE);
		}
	}

	private static void resetSpecialSkillsAtHalfTime(Game game) {
		for (Player<?> player : game.getPlayers()) {
			player.resetUsedSkills(SkillUsageType.ONCE_PER_HALF, game);
		}
		resetSpecialSkillAtEndOfDrive(game);
	}

	public static void resetSpecialSkillAtEndOfDrive(Game game) {
		for (Player<?> player : game.getPlayers()) {
			player.resetUsedSkills(SkillUsageType.ONCE_PER_DRIVE, game);
		}
	}

	public static void checkForWastedSkills(Player<?> player, IStep step, FieldModel fieldModel) {

		PlayerState playerState = fieldModel.getPlayerState(player);

		if (playerState.isCasualty() || playerState.getBase() == PlayerState.BANNED) {
			Constant.CHECK_AFTER_PLAYER_REMOVAL.stream()
				.map(property -> UtilCards.getSkillWithProperty(player, property))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.filter(skill -> !player.isUsed(skill))
				.forEach(skill -> step.getResult().addReport(new ReportSkillWasted(player.getId(), skill)));
		}
	}

	public static void updatePlayerStateDependentProperties(IStep pStep) {
		Game game = pStep.getGameState().getGame();
		updateLeaderReRollsForTeam(game.getTurnDataHome(), game.getTeamHome(), game.getFieldModel(), pStep);
		updateLeaderReRollsForTeam(game.getTurnDataAway(), game.getTeamAway(), game.getFieldModel(), pStep);
		checkForMissingPartners(game, game.getTeamHome(), game.getFieldModel(), pStep);
		checkForMissingPartners(game, game.getTeamAway(), game.getFieldModel(), pStep);
		updateSingleUseReRolls(game.getTurnDataHome(), game.getTeamHome(), game.getFieldModel());
		updateSingleUseReRolls(game.getTurnDataAway(), game.getTeamAway(), game.getFieldModel());
	}

	private static void checkForMissingPartners(Game game, Team team, FieldModel fieldModel, IStep step) {
		List<Player<?>> players = Arrays.stream(team.getPlayers())
			.filter(player -> player.hasSkillProperty(NamedProperties.reducesLonerRollIfPartnerIsHurt)).collect(Collectors.toList());

		for (Player<?> currentPlayer : players) {
			Skill skill = currentPlayer.getSkillWithProperty(NamedProperties.reducesLonerRollIfPartnerIsHurt);
			String partnerPosId = currentPlayer.getPosition().getTeamWithPositionId();
			if (StringTool.isProvided(partnerPosId)) {
				Optional<Player<?>> partner = players.stream().filter(player -> partnerPosId.equals(player.getPosition().getId())).findFirst();
				if (partner.isPresent()) {
					PlayerState playerState = fieldModel.getPlayerState(currentPlayer);
					boolean playerRemovedFromPlay = playerState.isCasualty() || playerState.getBase() == PlayerState.KNOCKED_OUT;
					if (playerRemovedFromPlay && !partner.get().hasActiveEnhancement(skill)) {
						addPartnerEnhancement(game, fieldModel, partner.get(), currentPlayer, skill, step);
					}
				}
			}
		}
	}

	private static void addPartnerEnhancement(Game game, FieldModel fieldModel, Player<?> player, Player<?> partner, Skill skill, IStep step) {
		fieldModel.addSkillEnhancements(player, skill);
		player.markUsed(skill, game);
		step.getResult().addReport(new ReportTwoForOne(player.getId(), partner.getId(), true));
	}

	private static void updateLeaderReRollsForTeam(TurnData pTurnData, Team pTeam, FieldModel pFieldModel,
																								 IStep pStep) {
		if (!LeaderState.USED.equals(pTurnData.getLeaderState())) {
			if (teamHasLeaderOnField(pTeam, pFieldModel)) {
				if (LeaderState.NONE.equals(pTurnData.getLeaderState())) {
					pTurnData.setLeaderState(LeaderState.AVAILABLE);
					pTurnData.setReRolls(pTurnData.getReRolls() + 1);
					pStep.getResult().addReport(new ReportLeader(pTeam.getId(), pTurnData.getLeaderState()));
				}
			} else {
				if (LeaderState.AVAILABLE.equals(pTurnData.getLeaderState())) {
					pTurnData.setLeaderState(LeaderState.NONE);
					pTurnData.setReRolls(Math.max(pTurnData.getReRolls() - 1, 0));
					pStep.getResult().addReport(new ReportLeader(pTeam.getId(), pTurnData.getLeaderState()));
				}
			}
		}
	}

	protected static boolean teamHasLeaderOnField(Team pTeam, FieldModel pFieldModel) {
		for (Player<?> player : pTeam.getPlayers()) {
			if (playerOnField(player, pFieldModel)
				&& player.hasSkillProperty(NamedProperties.grantsTeamReRollWhenOnPitch)) {
				return true;
			}
		}
		return false;
	}

	public static void updateSingleUseReRolls(TurnData turnData, Team team, FieldModel fieldModel) {
		int reRolls = (int) Arrays.stream(team.getPlayers())
			.filter(player -> UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.grantsSingleUseTeamRerollWhenOnPitch))
			.map(fieldModel::getPlayerState)
			.filter(playerState -> !playerState.isCasualty() && playerState.getBase() != PlayerState.BANNED)
			.count();

		turnData.setSingleUseReRolls(reRolls);
	}

	protected static boolean playerOnField(Player<?> pPlayer, FieldModel pFieldModel) {
		FieldCoordinate fieldCoordinate = pFieldModel.getPlayerCoordinate(pPlayer);
		return ((fieldCoordinate != null) && !fieldCoordinate.isBoxCoordinate());
	}

	private static void addApothecaries(IStep pStep, boolean pHomeTeam) {
		Game game = pStep.getGameState().getGame();
		Team team = pHomeTeam ? game.getTeamHome() : game.getTeamAway();
		TurnData turnData = pHomeTeam ? game.getTurnDataHome() : game.getTurnDataAway();
		turnData.setApothecaries(team.getApothecaries());
		turnData.getInducementSet().getInducementMapping().entrySet().stream().filter(entry -> entry.getKey().hasSingleUsage(Usage.APOTHECARY))
			.findFirst().ifPresent(entry -> {
				Inducement wanderingApothecaries = entry.getValue();
				if (wanderingApothecaries.getValue() > 0) {
					turnData.setApothecaries(turnData.getApothecaries() + wanderingApothecaries.getValue());
					turnData.setWanderingApothecaries(wanderingApothecaries.getValue());
					pStep.getResult().addReport(
						new ReportInducement(team.getId(), entry.getKey(), wanderingApothecaries.getValue()));
				}
			});

		turnData.getInducementSet().getInducementMapping().entrySet().stream().filter(entry -> entry.getKey().hasUsage(Usage.APOTHECARY_JOURNEYMEN))
			.findFirst().ifPresent(entry -> {
				Inducement plagueDoctors = entry.getValue();
				if (plagueDoctors.getValue() > 0) {
					turnData.setPlagueDoctors(plagueDoctors.getValue());
				}
			});
	}

	private static void addReRolls(IStep pStep, boolean pHomeTeam) {
		Game game = pStep.getGameState().getGame();
		Team team = pHomeTeam ? game.getTeamHome() : game.getTeamAway();
		TurnData turnData = pHomeTeam ? game.getTurnDataHome() : game.getTurnDataAway();
		turnData.setReRolls(team.getReRolls());
		turnData.getInducementSet().getInducementMapping().entrySet().stream().filter(entry -> entry.getKey().hasUsage(Usage.REROLL))
			.findFirst().ifPresent(entry -> {
				Inducement extraTraining = entry.getValue();
				if (extraTraining.getValue() > 0) {
					turnData.setReRolls(turnData.getReRolls() + extraTraining.getValue());
					pStep.getResult()
						.addReport(new ReportInducement(team.getId(), entry.getKey(), extraTraining.getValue()));
				}
			});
	}

	private static int rollMasterChef(IStep pStep, boolean pHomeTeam) {
		AtomicInteger reRollsStolenTotal = new AtomicInteger(0);
		GameState gameState = pStep.getGameState();
		Game game = gameState.getGame();
		InducementSet inducementSet = pHomeTeam ? game.getTurnDataHome().getInducementSet()
			: game.getTurnDataAway().getInducementSet();
		inducementSet.getInducementMapping().entrySet().stream()
			.filter(entry -> entry.getKey().hasUsage(Usage.STEAL_REROLL)
				&& entry.getValue().getValue() > 0).findFirst().ifPresent(entry -> {
				Inducement masterChef = entry.getValue();
				for (int i = 0; i < masterChef.getValue(); i++) {
					Team team = pHomeTeam ? game.getTeamHome() : game.getTeamAway();
					int[] masterChefRoll = gameState.getDiceRoller().rollMasterChef();
					int reRollsStolen = DiceInterpreter.getInstance().interpretMasterChefRoll(masterChefRoll);
					pStep.getResult().addReport(new ReportMasterChefRoll(team.getId(), masterChefRoll, reRollsStolen));
					reRollsStolenTotal.addAndGet(reRollsStolen);
				}
			});
		return reRollsStolenTotal.get();
	}

	public static void closeGame(GameState pGameState) {
		if (pGameState != null) {
			FantasyFootballServer server = pGameState.getServer();
			SessionManager sessionManager = server.getSessionManager();
			Session[] sessions = sessionManager.getSessionsForGameId(pGameState.getId());
			for (Session session : sessions) {
				server.getCommunication().close(session);
			}
		}
	}

	// this might be overkill, we'll see how it does in practice
	public static void handleInvalidTeam(String pTeamId, GameState gameState, FantasyFootballServer server, Throwable pThrowable) {
		server.getDebugLog().log(IServerLogLevel.ERROR, gameState.getGame().getId(), StringTool.bind("Error loading Team $1.", pTeamId));
		server.getDebugLog().log(gameState.getGame().getId(), pThrowable);
		server.getCommunication().sendStatus(gameState, ServerStatus.FUMBBL_ERROR,
			StringTool.bind("Unable to load Team with id $1.", pTeamId));
		UtilServerGame.closeGame(gameState);
	}
}
