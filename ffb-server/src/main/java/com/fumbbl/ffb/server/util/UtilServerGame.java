package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.factory.MechanicsFactory;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.*;
import com.fumbbl.ffb.model.change.ModelChangeList;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.ServerStatus;
import com.fumbbl.ffb.report.ReportList;
import com.fumbbl.ffb.report.ReportMasterChefRoll;
import com.fumbbl.ffb.report.ReportPlayerAction;
import com.fumbbl.ffb.report.bb2020.ReportTwoForOne;
import com.fumbbl.ffb.report.mixed.ReportSkillWasted;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.mechanic.StateMechanic;
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

		boolean playerChanged = UtilActingPlayer.changeActingPlayer(game, pActingPlayerId, actualAction, jumping);
		if (pPlayerAction != null) {
			boolean differentAction = (oldPlayerAction == null) || (pPlayerAction.getType() != oldPlayerAction.getType());
			if ((playerChanged && differentAction) || pPlayerAction.forceLog()) {
				if (oldPlayerAction == null) {
					pStep.getResult().setSound(SoundId.CLICK);
				}
				pStep.getResult().addReport(new ReportPlayerAction(pActingPlayerId, pPlayerAction));
			}
		}
	}

	public static void handleChefRolls(IStep pStep, Game game) {
		// handle Master Chefs
		int reRollsStolenHome = rollMasterChef(pStep, true);
		int reRollsStolenAway = rollMasterChef(pStep, false);

		int homeReRolls = game.getTurnDataHome().getReRolls();
		homeReRolls = Math.max(0, homeReRolls - reRollsStolenAway);
		homeReRolls += reRollsStolenHome;
		game.getTurnDataHome().setReRolls(homeReRolls);

		int awayReRolls = game.getTurnDataAway().getReRolls();
		awayReRolls = Math.max(0, awayReRolls - reRollsStolenHome);
		awayReRolls += reRollsStolenAway;
		game.getTurnDataAway().setReRolls(awayReRolls);
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
		MechanicsFactory factory = game.getFactory(FactoryType.Factory.MECHANIC);
		StateMechanic mechanic = (StateMechanic) factory.forName(Mechanic.Type.STATE.name());
		mechanic.updateLeaderReRollsForTeam(game.getTurnDataHome(), game.getTeamHome(), game.getFieldModel(), pStep);
		mechanic.updateLeaderReRollsForTeam(game.getTurnDataAway(), game.getTeamAway(), game.getFieldModel(), pStep);
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

	public static void updateSingleUseReRolls(TurnData turnData, Team team, FieldModel fieldModel) {
		int reRolls = (int) Arrays.stream(team.getPlayers())
			.filter(player -> UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.grantsSingleUseTeamRerollWhenOnPitch))
			.map(fieldModel::getPlayerState)
			.filter(playerState -> !playerState.isCasualty() && playerState.getBase() != PlayerState.BANNED)
			.count();

		turnData.setSingleUseReRolls(reRolls);
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
