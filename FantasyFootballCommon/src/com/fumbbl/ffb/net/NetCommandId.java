package com.fumbbl.ffb.net;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.net.commands.ClientCommandActingPlayer;
import com.fumbbl.ffb.net.commands.ClientCommandApothecaryChoice;
import com.fumbbl.ffb.net.commands.ClientCommandArgueTheCall;
import com.fumbbl.ffb.net.commands.ClientCommandBlitzMove;
import com.fumbbl.ffb.net.commands.ClientCommandBlitzTargetSelected;
import com.fumbbl.ffb.net.commands.ClientCommandBlock;
import com.fumbbl.ffb.net.commands.ClientCommandBlockChoice;
import com.fumbbl.ffb.net.commands.ClientCommandBlockOrReRollChoiceForTarget;
import com.fumbbl.ffb.net.commands.ClientCommandBuyCard;
import com.fumbbl.ffb.net.commands.ClientCommandBuyInducements;
import com.fumbbl.ffb.net.commands.ClientCommandCloseSession;
import com.fumbbl.ffb.net.commands.ClientCommandCoinChoice;
import com.fumbbl.ffb.net.commands.ClientCommandConcedeGame;
import com.fumbbl.ffb.net.commands.ClientCommandConfirm;
import com.fumbbl.ffb.net.commands.ClientCommandDebugClientState;
import com.fumbbl.ffb.net.commands.ClientCommandEndTurn;
import com.fumbbl.ffb.net.commands.ClientCommandFieldCoordinate;
import com.fumbbl.ffb.net.commands.ClientCommandFollowupChoice;
import com.fumbbl.ffb.net.commands.ClientCommandFoul;
import com.fumbbl.ffb.net.commands.ClientCommandGaze;
import com.fumbbl.ffb.net.commands.ClientCommandHandOver;
import com.fumbbl.ffb.net.commands.ClientCommandIllegalProcedure;
import com.fumbbl.ffb.net.commands.ClientCommandInterceptorChoice;
import com.fumbbl.ffb.net.commands.ClientCommandJoin;
import com.fumbbl.ffb.net.commands.ClientCommandJourneymen;
import com.fumbbl.ffb.net.commands.ClientCommandKickTeamMate;
import com.fumbbl.ffb.net.commands.ClientCommandKickoff;
import com.fumbbl.ffb.net.commands.ClientCommandMove;
import com.fumbbl.ffb.net.commands.ClientCommandPass;
import com.fumbbl.ffb.net.commands.ClientCommandPasswordChallenge;
import com.fumbbl.ffb.net.commands.ClientCommandPettyCash;
import com.fumbbl.ffb.net.commands.ClientCommandPileDriver;
import com.fumbbl.ffb.net.commands.ClientCommandPing;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.net.commands.ClientCommandPushback;
import com.fumbbl.ffb.net.commands.ClientCommandReceiveChoice;
import com.fumbbl.ffb.net.commands.ClientCommandReplay;
import com.fumbbl.ffb.net.commands.ClientCommandRequestVersion;
import com.fumbbl.ffb.net.commands.ClientCommandSelectCardToBuy;
import com.fumbbl.ffb.net.commands.ClientCommandSetBlockTargetSelection;
import com.fumbbl.ffb.net.commands.ClientCommandSetMarker;
import com.fumbbl.ffb.net.commands.ClientCommandSetupPlayer;
import com.fumbbl.ffb.net.commands.ClientCommandStartGame;
import com.fumbbl.ffb.net.commands.ClientCommandSwoop;
import com.fumbbl.ffb.net.commands.ClientCommandSynchronousMultiBlock;
import com.fumbbl.ffb.net.commands.ClientCommandTalk;
import com.fumbbl.ffb.net.commands.ClientCommandTeamSetupDelete;
import com.fumbbl.ffb.net.commands.ClientCommandTeamSetupLoad;
import com.fumbbl.ffb.net.commands.ClientCommandTeamSetupSave;
import com.fumbbl.ffb.net.commands.ClientCommandThrowTeamMate;
import com.fumbbl.ffb.net.commands.ClientCommandTouchback;
import com.fumbbl.ffb.net.commands.ClientCommandUnsetBlockTargetSelection;
import com.fumbbl.ffb.net.commands.ClientCommandUseApothecaries;
import com.fumbbl.ffb.net.commands.ClientCommandUseApothecary;
import com.fumbbl.ffb.net.commands.ClientCommandUseBrawler;
import com.fumbbl.ffb.net.commands.ClientCommandUseChainsaw;
import com.fumbbl.ffb.net.commands.ClientCommandUseFumblerooskie;
import com.fumbbl.ffb.net.commands.ClientCommandUseIgors;
import com.fumbbl.ffb.net.commands.ClientCommandUseInducement;
import com.fumbbl.ffb.net.commands.ClientCommandUseProReRollForBlock;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRoll;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRollForTarget;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.net.commands.ClientCommandUserSettings;
import com.fumbbl.ffb.net.commands.ClientCommandWizardSpell;
import com.fumbbl.ffb.net.commands.ServerCommandAddPlayer;
import com.fumbbl.ffb.net.commands.ServerCommandAdminMessage;
import com.fumbbl.ffb.net.commands.ServerCommandGameList;
import com.fumbbl.ffb.net.commands.ServerCommandGameState;
import com.fumbbl.ffb.net.commands.ServerCommandGameTime;
import com.fumbbl.ffb.net.commands.ServerCommandJoin;
import com.fumbbl.ffb.net.commands.ServerCommandLeave;
import com.fumbbl.ffb.net.commands.ServerCommandModelSync;
import com.fumbbl.ffb.net.commands.ServerCommandPasswordChallenge;
import com.fumbbl.ffb.net.commands.ServerCommandPong;
import com.fumbbl.ffb.net.commands.ServerCommandRemovePlayer;
import com.fumbbl.ffb.net.commands.ServerCommandReplay;
import com.fumbbl.ffb.net.commands.ServerCommandSound;
import com.fumbbl.ffb.net.commands.ServerCommandStatus;
import com.fumbbl.ffb.net.commands.ServerCommandTalk;
import com.fumbbl.ffb.net.commands.ServerCommandTeamList;
import com.fumbbl.ffb.net.commands.ServerCommandTeamSetupList;
import com.fumbbl.ffb.net.commands.ServerCommandUnzapPlayer;
import com.fumbbl.ffb.net.commands.ServerCommandUserSettings;
import com.fumbbl.ffb.net.commands.ServerCommandVersion;
import com.fumbbl.ffb.net.commands.ServerCommandZapPlayer;

/**
 * @author Kalimar
 */
public enum NetCommandId implements INamedObject {

	INTERNAL_SERVER_SOCKET_CLOSED("internalServerSocketClosed"), CLIENT_JOIN("clientJoin"), CLIENT_TALK("clientTalk"),
	SERVER_GAME_STATE("serverGameState"), SERVER_TEAM_LIST("serverTeamList"), SERVER_STATUS("serverStatus"),
	SERVER_JOIN("serverJoin"), SERVER_LEAVE("serverLeave"), SERVER_TALK("serverTalk"),
	CLIENT_SETUP_PLAYER("clientSetupPlayer"), CLIENT_START_GAME("clientStartGame"),
	CLIENT_ACTING_PLAYER("clientActingPlayer"), CLIENT_MOVE("clientMove"), CLIENT_BLITZ_MOVE("clientBlitzMove"),
	CLIENT_BLITZ_TARGET_SELECTED("blitzTargetSelected"), CLIENT_USE_RE_ROLL("clientUseReRoll"), CLIENT_USE_RE_ROLL_FOR_TARGET("clientUseReRollForTarget"),
	SERVER_SOUND("serverSound"), CLIENT_COIN_CHOICE("clientCoinChoice"), CLIENT_RECEIVE_CHOICE("clientReceiveChoice"),
	CLIENT_END_TURN("clientEndTurn"), CLIENT_KICKOFF("clientKickoff"), CLIENT_TOUCHBACK("clientTouchback"),
	CLIENT_HAND_OVER("clientHandOver"), CLIENT_PASS("clientPass"), CLIENT_BLOCK("clientBlock"),
	CLIENT_BLOCK_CHOICE("clientBlockChoice"), CLIENT_PUSHBACK("clientPushback"), CLIENT_USE_PRO_RE_ROLL_FOR_BLOCK("clientUseProReRollForBlock"),
	CLIENT_FOLLOWUP_CHOICE("clientFollowupChoice"), CLIENT_INTERCEPTOR_CHOICE("clientInterceptorChoice"),
	CLIENT_USE_SKILL("clientUseSkill"), SERVER_TEAM_SETUP_LIST("serverTeamSetupList"),
	CLIENT_TEAM_SETUP_LOAD("clientTeamSetupLoad"), CLIENT_TEAM_SETUP_SAVE("clientTeamSetupSave"),
	CLIENT_TEAM_SETUP_DELETE("clientTeamSetupDelete"), CLIENT_FOUL("clientFoul"),
	CLIENT_USE_APOTHECARY("clientUseApothecary"), CLIENT_APOTHECARY_CHOICE("clientApothecaryChoice"),
	CLIENT_PASSWORD_CHALLENGE("clientPasswordChallenge"), SERVER_PASSWORD_CHALLENGE("serverPasswordChallenge"),
	SERVER_MODEL_SYNC("serverModelSync"), SERVER_VERSION("serverVersion"), CLIENT_REQUEST_VERSION("clientRequestVersion"),
	CLIENT_DEBUG_CLIENT_STATE("clientDebugClientState"), SERVER_GAME_LIST("serverGameList"),
	CLIENT_USER_SETTINGS("clientUserSettings"), SERVER_USER_SETTINGS("serverUserSettings"), CLIENT_REPLAY("clientReplay"),
	SERVER_REPLAY("serverReplay"), CLIENT_THROW_TEAM_MATE("clientThrowTeamMate"),
	CLIENT_KICK_TEAM_MATE("clientKickTeamMate"), CLIENT_SWOOP("clientSwoop"), CLIENT_PLAYER_CHOICE("clientPlayerChoice"),
	CLIENT_ILLEGAL_PROCEDURE("clientIllegalProcedure"), CLIENT_CONCEDE_GAME("clientConcedeGame"),
	SERVER_ADMIN_MESSAGE("serverAdminMessage"), CLIENT_USE_INDUCEMENT("clientUseInducement"),
	CLIENT_BUY_INDUCEMENTS("clientBuyInducements"), SERVER_ADD_PLAYER("serverAddPlayer"),
	SERVER_ZAP_PLAYER("serverZapPlayer"), SERVER_UNZAP_PLAYER("serverUnzapPlayer"), CLIENT_JOURNEYMEN("clientJourneymen"),
	CLIENT_GAZE("clientGaze"), CLIENT_CONFIRM("clientConfirm"), CLIENT_SET_MARKER("clientSetMarker"),
	INTERNAL_SERVER_FUMBBL_GAME_CREATED("internalServerFumbblGameCreated"),
	INTERNAL_SERVER_FUMBBL_TEAM_LOADED("internalServerFumbblTeamLoaded"),
	INTERNAL_SERVER_FUMBBL_GAME_CHECKED("internalServerFumbblTeamLoaded"),
	INTERNAL_SERVER_JOIN_APPROVED("internalServerJoinApproved"),
	INTERNAL_SERVER_REPLAY_LOADED("internalServerReplayGameLoaded"), CLIENT_PETTY_CASH("clientPettyCash"),
	SERVER_REMOVE_PLAYER("serverRemovePlayer"), CLIENT_WIZARD_SPELL("clientWizardSpell"),
	CLIENT_BUY_CARD("clientBuyCard"), CLIENT_SELECT_CARD_TO_BUY("clientSelectCardToBuy"), INTERNAL_SERVER_CLOSE_GAME("internalServerCloseGame"),
	INTERNAL_SERVER_DELETE_GAME("internalServerDeleteGame"), INTERNAL_SERVER_UPLOAD_GAME("internalServerUploadGame"),
	INTERNAL_SERVER_SCHEDULE_GAME("internalServerScheduleGame"), INTERNAL_SERVER_CLEAR_CACHE("internalServerClearCache"),
	CLIENT_CLOSE_SESSION("clientCloseSession"), CLIENT_ARGUE_THE_CALL("clientArgueTheCall"),
	CLIENT_USE_APOTHECARIES("clientUseApothecaries"), CLIENT_USE_IGORS("clientUseIgors"),
	SERVER_GAME_TIME("serverGameTime"), CLIENT_PING("clientPing"), SERVER_PONG("serverPong"),
	CLIENT_SET_BLOCK_TARGET_SELECTION("clientSetBlockTargetSelection"), CLIENT_UNSET_BLOCK_TARGET_SELECTION("clientUnsetBlockTargetSelection"),
	CLIENT_SYNCHRONOUS_MULTI_BLOCK("clientSynchronousMultiBlock"), CLIENT_BLOCK_OR_RE_ROLL_CHOICE_FOR_TARGET("clientBlockOrReRollChoiceForTarget"),
	CLIENT_PILE_DRIVER("clientPileDriver"), CLIENT_USE_CHAINSAW("clientUseChainsaw"), CLIENT_USE_BRAWLER("clientUseBrawler"),
	CLIENT_FIELD_COORDINATE("clientFieldCoordinate"), CLIENT_USE_FUMBLEROOSKIE("clientUseFumblerooskie");

	private final String fName;

	NetCommandId(String pName) {
		fName = pName;
	}

	public String getName() {
		return fName;
	}

	public NetCommand createNetCommand() {
		switch (this) {
			case CLIENT_JOIN:
				return new ClientCommandJoin();
			case CLIENT_TALK:
				return new ClientCommandTalk();
			case SERVER_TALK:
				return new ServerCommandTalk();
			case SERVER_STATUS:
				return new ServerCommandStatus();
			case SERVER_TEAM_LIST:
				return new ServerCommandTeamList();
			case SERVER_GAME_STATE:
				return new ServerCommandGameState();
			case SERVER_JOIN:
				return new ServerCommandJoin();
			case SERVER_LEAVE:
				return new ServerCommandLeave();
			case SERVER_GAME_TIME:
				return new ServerCommandGameTime();
			case CLIENT_SETUP_PLAYER:
				return new ClientCommandSetupPlayer();
			case CLIENT_START_GAME:
				return new ClientCommandStartGame();
			case CLIENT_ACTING_PLAYER:
				return new ClientCommandActingPlayer();
			case CLIENT_MOVE:
				return new ClientCommandMove();
			case CLIENT_BLITZ_MOVE:
				return new ClientCommandBlitzMove();
			case CLIENT_BLITZ_TARGET_SELECTED:
				return new ClientCommandBlitzTargetSelected();
			case CLIENT_USE_RE_ROLL:
				return new ClientCommandUseReRoll();
			case SERVER_SOUND:
				return new ServerCommandSound();
			case CLIENT_COIN_CHOICE:
				return new ClientCommandCoinChoice();
			case CLIENT_RECEIVE_CHOICE:
				return new ClientCommandReceiveChoice();
			case CLIENT_END_TURN:
				return new ClientCommandEndTurn();
			case CLIENT_KICKOFF:
				return new ClientCommandKickoff();
			case CLIENT_TOUCHBACK:
				return new ClientCommandTouchback();
			case CLIENT_HAND_OVER:
				return new ClientCommandHandOver();
			case CLIENT_PASS:
				return new ClientCommandPass();
			case CLIENT_BLOCK:
				return new ClientCommandBlock();
			case CLIENT_BLOCK_CHOICE:
				return new ClientCommandBlockChoice();
			case CLIENT_PUSHBACK:
				return new ClientCommandPushback();
			case CLIENT_FOLLOWUP_CHOICE:
				return new ClientCommandFollowupChoice();
			case CLIENT_INTERCEPTOR_CHOICE:
				return new ClientCommandInterceptorChoice();
			case CLIENT_USE_SKILL:
				return new ClientCommandUseSkill();
			case SERVER_TEAM_SETUP_LIST:
				return new ServerCommandTeamSetupList();
			case CLIENT_TEAM_SETUP_LOAD:
				return new ClientCommandTeamSetupLoad();
			case CLIENT_TEAM_SETUP_SAVE:
				return new ClientCommandTeamSetupSave();
			case CLIENT_TEAM_SETUP_DELETE:
				return new ClientCommandTeamSetupDelete();
			case CLIENT_FOUL:
				return new ClientCommandFoul();
			case CLIENT_USE_APOTHECARY:
				return new ClientCommandUseApothecary();
			case CLIENT_APOTHECARY_CHOICE:
				return new ClientCommandApothecaryChoice();
			case CLIENT_PASSWORD_CHALLENGE:
				return new ClientCommandPasswordChallenge();
			case SERVER_PASSWORD_CHALLENGE:
				return new ServerCommandPasswordChallenge();
			case SERVER_VERSION:
				return new ServerCommandVersion();
			case CLIENT_REQUEST_VERSION:
				return new ClientCommandRequestVersion();
			case CLIENT_DEBUG_CLIENT_STATE:
				return new ClientCommandDebugClientState();
			case SERVER_GAME_LIST:
				return new ServerCommandGameList();
			case CLIENT_USER_SETTINGS:
				return new ClientCommandUserSettings();
			case SERVER_USER_SETTINGS:
				return new ServerCommandUserSettings();
			case SERVER_REPLAY:
				return new ServerCommandReplay();
			case CLIENT_REPLAY:
				return new ClientCommandReplay();
			case CLIENT_THROW_TEAM_MATE:
				return new ClientCommandThrowTeamMate();
			case CLIENT_KICK_TEAM_MATE:
				return new ClientCommandKickTeamMate();
			case CLIENT_SWOOP:
				return new ClientCommandSwoop();
			case CLIENT_PLAYER_CHOICE:
				return new ClientCommandPlayerChoice();
			case SERVER_MODEL_SYNC:
				return new ServerCommandModelSync();
			case CLIENT_ILLEGAL_PROCEDURE:
				return new ClientCommandIllegalProcedure();
			case CLIENT_CONCEDE_GAME:
				return new ClientCommandConcedeGame();
			case SERVER_ADMIN_MESSAGE:
				return new ServerCommandAdminMessage();
			case CLIENT_USE_INDUCEMENT:
				return new ClientCommandUseInducement();
			case CLIENT_BUY_INDUCEMENTS:
				return new ClientCommandBuyInducements();
			case SERVER_ADD_PLAYER:
				return new ServerCommandAddPlayer();
			case SERVER_ZAP_PLAYER:
				return new ServerCommandZapPlayer();
			case SERVER_UNZAP_PLAYER:
				return new ServerCommandUnzapPlayer();
			case CLIENT_JOURNEYMEN:
				return new ClientCommandJourneymen();
			case CLIENT_GAZE:
				return new ClientCommandGaze();
			case CLIENT_CONFIRM:
				return new ClientCommandConfirm();
			case CLIENT_SET_MARKER:
				return new ClientCommandSetMarker();
			case CLIENT_PETTY_CASH:
				return new ClientCommandPettyCash();
			case SERVER_REMOVE_PLAYER:
				return new ServerCommandRemovePlayer();
			case CLIENT_WIZARD_SPELL:
				return new ClientCommandWizardSpell();
			case CLIENT_BUY_CARD:
				return new ClientCommandBuyCard();
			case CLIENT_SELECT_CARD_TO_BUY:
				return new ClientCommandSelectCardToBuy();
			case CLIENT_CLOSE_SESSION:
				return new ClientCommandCloseSession();
			case CLIENT_ARGUE_THE_CALL:
				return new ClientCommandArgueTheCall();
			case CLIENT_PING:
				return new ClientCommandPing();
			case SERVER_PONG:
				return new ServerCommandPong();
			case CLIENT_SET_BLOCK_TARGET_SELECTION:
				return new ClientCommandSetBlockTargetSelection();
			case CLIENT_UNSET_BLOCK_TARGET_SELECTION:
				return new ClientCommandUnsetBlockTargetSelection();
			case CLIENT_SYNCHRONOUS_MULTI_BLOCK:
				return new ClientCommandSynchronousMultiBlock();
			case CLIENT_USE_RE_ROLL_FOR_TARGET:
				return new ClientCommandUseReRollForTarget();
			case CLIENT_BLOCK_OR_RE_ROLL_CHOICE_FOR_TARGET:
				return new ClientCommandBlockOrReRollChoiceForTarget();
			case CLIENT_USE_APOTHECARIES:
				return new ClientCommandUseApothecaries();
			case CLIENT_USE_IGORS:
				return new ClientCommandUseIgors();
			case CLIENT_PILE_DRIVER:
				return new ClientCommandPileDriver();
			case CLIENT_USE_CHAINSAW:
				return new ClientCommandUseChainsaw();
			case CLIENT_USE_BRAWLER:
				return new ClientCommandUseBrawler();
			case CLIENT_FIELD_COORDINATE:
				return new ClientCommandFieldCoordinate();
			case CLIENT_USE_FUMBLEROOSKIE:
				return new ClientCommandUseFumblerooskie();
			case CLIENT_USE_PRO_RE_ROLL_FOR_BLOCK:
				return new ClientCommandUseProReRollForBlock();
			default:
				throw new IllegalStateException("Unhandled netCommandId " + this + ".");
		}
	}

}
