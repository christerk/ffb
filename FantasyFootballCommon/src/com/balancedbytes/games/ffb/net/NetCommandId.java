package com.balancedbytes.games.ffb.net;

import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.net.commands.ClientCommandActingPlayer;
import com.balancedbytes.games.ffb.net.commands.ClientCommandApothecaryChoice;
import com.balancedbytes.games.ffb.net.commands.ClientCommandArgueTheCall;
import com.balancedbytes.games.ffb.net.commands.ClientCommandBlitzMove;
import com.balancedbytes.games.ffb.net.commands.ClientCommandBlitzTargetSelected;
import com.balancedbytes.games.ffb.net.commands.ClientCommandBlock;
import com.balancedbytes.games.ffb.net.commands.ClientCommandBlockChoice;
import com.balancedbytes.games.ffb.net.commands.ClientCommandBlockOrReRollChoiceForTarget;
import com.balancedbytes.games.ffb.net.commands.ClientCommandBuyCard;
import com.balancedbytes.games.ffb.net.commands.ClientCommandBuyInducements;
import com.balancedbytes.games.ffb.net.commands.ClientCommandCloseSession;
import com.balancedbytes.games.ffb.net.commands.ClientCommandCoinChoice;
import com.balancedbytes.games.ffb.net.commands.ClientCommandConcedeGame;
import com.balancedbytes.games.ffb.net.commands.ClientCommandConfirm;
import com.balancedbytes.games.ffb.net.commands.ClientCommandDebugClientState;
import com.balancedbytes.games.ffb.net.commands.ClientCommandEndTurn;
import com.balancedbytes.games.ffb.net.commands.ClientCommandFollowupChoice;
import com.balancedbytes.games.ffb.net.commands.ClientCommandFoul;
import com.balancedbytes.games.ffb.net.commands.ClientCommandGaze;
import com.balancedbytes.games.ffb.net.commands.ClientCommandHandOver;
import com.balancedbytes.games.ffb.net.commands.ClientCommandIllegalProcedure;
import com.balancedbytes.games.ffb.net.commands.ClientCommandInterceptorChoice;
import com.balancedbytes.games.ffb.net.commands.ClientCommandJoin;
import com.balancedbytes.games.ffb.net.commands.ClientCommandJourneymen;
import com.balancedbytes.games.ffb.net.commands.ClientCommandKickTeamMate;
import com.balancedbytes.games.ffb.net.commands.ClientCommandKickoff;
import com.balancedbytes.games.ffb.net.commands.ClientCommandMove;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPass;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPasswordChallenge;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPettyCash;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPing;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPlayerChoice;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPushback;
import com.balancedbytes.games.ffb.net.commands.ClientCommandReceiveChoice;
import com.balancedbytes.games.ffb.net.commands.ClientCommandReplay;
import com.balancedbytes.games.ffb.net.commands.ClientCommandRequestVersion;
import com.balancedbytes.games.ffb.net.commands.ClientCommandSelectCardToBuy;
import com.balancedbytes.games.ffb.net.commands.ClientCommandSetBlockTargetSelection;
import com.balancedbytes.games.ffb.net.commands.ClientCommandSetMarker;
import com.balancedbytes.games.ffb.net.commands.ClientCommandSetupPlayer;
import com.balancedbytes.games.ffb.net.commands.ClientCommandStartGame;
import com.balancedbytes.games.ffb.net.commands.ClientCommandSwoop;
import com.balancedbytes.games.ffb.net.commands.ClientCommandSynchronousMultiBlock;
import com.balancedbytes.games.ffb.net.commands.ClientCommandTalk;
import com.balancedbytes.games.ffb.net.commands.ClientCommandTeamSetupDelete;
import com.balancedbytes.games.ffb.net.commands.ClientCommandTeamSetupLoad;
import com.balancedbytes.games.ffb.net.commands.ClientCommandTeamSetupSave;
import com.balancedbytes.games.ffb.net.commands.ClientCommandThrowTeamMate;
import com.balancedbytes.games.ffb.net.commands.ClientCommandTouchback;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUnsetBlockTargetSelection;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseApothecary;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseInducement;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseReRoll;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseReRollForTarget;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUserSettings;
import com.balancedbytes.games.ffb.net.commands.ClientCommandWizardSpell;
import com.balancedbytes.games.ffb.net.commands.ServerCommandAddPlayer;
import com.balancedbytes.games.ffb.net.commands.ServerCommandAdminMessage;
import com.balancedbytes.games.ffb.net.commands.ServerCommandGameList;
import com.balancedbytes.games.ffb.net.commands.ServerCommandGameState;
import com.balancedbytes.games.ffb.net.commands.ServerCommandGameTime;
import com.balancedbytes.games.ffb.net.commands.ServerCommandJoin;
import com.balancedbytes.games.ffb.net.commands.ServerCommandLeave;
import com.balancedbytes.games.ffb.net.commands.ServerCommandModelSync;
import com.balancedbytes.games.ffb.net.commands.ServerCommandPasswordChallenge;
import com.balancedbytes.games.ffb.net.commands.ServerCommandPong;
import com.balancedbytes.games.ffb.net.commands.ServerCommandRemovePlayer;
import com.balancedbytes.games.ffb.net.commands.ServerCommandReplay;
import com.balancedbytes.games.ffb.net.commands.ServerCommandSound;
import com.balancedbytes.games.ffb.net.commands.ServerCommandStatus;
import com.balancedbytes.games.ffb.net.commands.ServerCommandTalk;
import com.balancedbytes.games.ffb.net.commands.ServerCommandTeamList;
import com.balancedbytes.games.ffb.net.commands.ServerCommandTeamSetupList;
import com.balancedbytes.games.ffb.net.commands.ServerCommandUnzapPlayer;
import com.balancedbytes.games.ffb.net.commands.ServerCommandUserSettings;
import com.balancedbytes.games.ffb.net.commands.ServerCommandVersion;
import com.balancedbytes.games.ffb.net.commands.ServerCommandZapPlayer;

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
	CLIENT_BLOCK_CHOICE("clientBlockChoice"), CLIENT_PUSHBACK("clientPushback"),
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
	SERVER_GAME_TIME("serverGameTime"), CLIENT_PING("clientPing"), SERVER_PONG("serverPong"),
	CLIENT_SET_BLOCK_TARGET_SELECTION("clientSetBlockTargetSelection"), CLIENT_UNSET_BLOCK_TARGET_SELECTION("clientUnsetBlockTargetSelection"),
	CLIENT_SYNCHRONOUS_MULTI_BLOCK("clientSynchronousMultiBlock"), CLIENT_BLOCK_OR_RE_ROLL_CHOICE_FOR_TARGET("clientBlockOrReRollChoiceForTarget");

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
			default:
				throw new IllegalStateException("Unhandled netCommandId " + this + ".");
		}
	}

}
