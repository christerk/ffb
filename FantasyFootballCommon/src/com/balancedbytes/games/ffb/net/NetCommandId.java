package com.balancedbytes.games.ffb.net;

import com.balancedbytes.games.ffb.IEnumWithId;
import com.balancedbytes.games.ffb.IEnumWithName;
import com.balancedbytes.games.ffb.net.commands.ClientCommandActingPlayer;
import com.balancedbytes.games.ffb.net.commands.ClientCommandApothecaryChoice;
import com.balancedbytes.games.ffb.net.commands.ClientCommandBlock;
import com.balancedbytes.games.ffb.net.commands.ClientCommandBlockChoice;
import com.balancedbytes.games.ffb.net.commands.ClientCommandBuyCard;
import com.balancedbytes.games.ffb.net.commands.ClientCommandBuyInducements;
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
import com.balancedbytes.games.ffb.net.commands.ClientCommandSetMarker;
import com.balancedbytes.games.ffb.net.commands.ClientCommandSetupPlayer;
import com.balancedbytes.games.ffb.net.commands.ClientCommandStartGame;
import com.balancedbytes.games.ffb.net.commands.ClientCommandTalk;
import com.balancedbytes.games.ffb.net.commands.ClientCommandTeamSetupDelete;
import com.balancedbytes.games.ffb.net.commands.ClientCommandTeamSetupLoad;
import com.balancedbytes.games.ffb.net.commands.ClientCommandTeamSetupSave;
import com.balancedbytes.games.ffb.net.commands.ClientCommandThrowTeamMate;
import com.balancedbytes.games.ffb.net.commands.ClientCommandTimeoutPossible;
import com.balancedbytes.games.ffb.net.commands.ClientCommandTouchback;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseApothecary;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseInducement;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseReRoll;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUserSettings;
import com.balancedbytes.games.ffb.net.commands.ClientCommandWizardSpell;
import com.balancedbytes.games.ffb.net.commands.ServerCommandAddPlayer;
import com.balancedbytes.games.ffb.net.commands.ServerCommandAdminMessage;
import com.balancedbytes.games.ffb.net.commands.ServerCommandGameList;
import com.balancedbytes.games.ffb.net.commands.ServerCommandGameState;
import com.balancedbytes.games.ffb.net.commands.ServerCommandJoin;
import com.balancedbytes.games.ffb.net.commands.ServerCommandLeave;
import com.balancedbytes.games.ffb.net.commands.ServerCommandModelSync;
import com.balancedbytes.games.ffb.net.commands.ServerCommandPasswordChallenge;
import com.balancedbytes.games.ffb.net.commands.ServerCommandPing;
import com.balancedbytes.games.ffb.net.commands.ServerCommandRemovePlayer;
import com.balancedbytes.games.ffb.net.commands.ServerCommandReplay;
import com.balancedbytes.games.ffb.net.commands.ServerCommandSound;
import com.balancedbytes.games.ffb.net.commands.ServerCommandStatus;
import com.balancedbytes.games.ffb.net.commands.ServerCommandTalk;
import com.balancedbytes.games.ffb.net.commands.ServerCommandTeamList;
import com.balancedbytes.games.ffb.net.commands.ServerCommandTeamSetupList;
import com.balancedbytes.games.ffb.net.commands.ServerCommandUserSettings;
import com.balancedbytes.games.ffb.net.commands.ServerCommandVersion;


/**
 * 
 * @author Kalimar
 */
public enum NetCommandId implements IEnumWithId, IEnumWithName {
  
  INTERNAL_SERVER_SOCKET_CLOSED(1, "internalServerSocketClosed"),
  CLIENT_JOIN(2, "clientJoin"),
  CLIENT_TALK(3, "clientTalk"),
  SERVER_GAME_STATE(4, "serverGameState"),
  SERVER_TEAM_LIST(5, "serverTeamList"),
  SERVER_STATUS(6, "serverStatus"),
  SERVER_JOIN(7, "serverJoin"),
  SERVER_LEAVE(8, "serverLeave"),
  SERVER_TALK(9, "serverTalk"),
  CLIENT_SETUP_PLAYER(10, "clientSetupPlayer"),
  CLIENT_START_GAME(11, "clientStartGame"),
  CLIENT_ACTING_PLAYER(12, "clientActingPlayer"),
  CLIENT_MOVE(13, "clientMove"),
  CLIENT_USE_RE_ROLL(14, "clientUseReRoll"),
  SERVER_SOUND(15, "serverSound"),
  CLIENT_COIN_CHOICE(16, "clientCoinChoice"),
  CLIENT_RECEIVE_CHOICE(17, "clientReceiveChoice"),
  CLIENT_END_TURN(18, "clientEndTurn"),
  CLIENT_KICKOFF(19, "clientKickoff"),
  CLIENT_TOUCHBACK(20, "clientTouchback"),
  CLIENT_HAND_OVER(21, "clientHandOver"),
  CLIENT_PASS(22, "clientPass"),
  CLIENT_BLOCK(23, "clientBlock"),
  CLIENT_BLOCK_CHOICE(24, "clientBlockChoice"),
  CLIENT_PUSHBACK(25, "clientPushback"),
  CLIENT_FOLLOWUP_CHOICE(26, "clientFollowupChoice"),
  CLIENT_INTERCEPTOR_CHOICE(27, "clientInterceptorChoice"),
  CLIENT_USE_SKILL(28, "clientUseSkill"),
  SERVER_TEAM_SETUP_LIST(29, "serverTeamSetupList"),
  CLIENT_TEAM_SETUP_LOAD(30, "clientTeamSetupLoad"),
  CLIENT_TEAM_SETUP_SAVE(31, "clientTeamSetupSave"),
  CLIENT_TEAM_SETUP_DELETE(32, "clientTeamSetupDelete"),
  CLIENT_FOUL(33, "clientFoul"),
  CLIENT_USE_APOTHECARY(34, "clientUseApothecary"),
  CLIENT_APOTHECARY_CHOICE(35, "clientApothecaryChoice"),
  CLIENT_PASSWORD_CHALLENGE(36, "clientPasswordChallenge"),
  CLIENT_PING(37, "clientPing"),
  SERVER_PING(38, "serverPing"),
  SERVER_PASSWORD_CHALLENGE(39, "serverPasswordChallenge"),
  SERVER_MODEL_SYNC(40, "serverModelSync"),
  SERVER_VERSION(41, "serverVersion"),
  CLIENT_REQUEST_VERSION(42, "clientRequestVersion"),
  CLIENT_DEBUG_CLIENT_STATE(43, "clientDebugClientState"),
  SERVER_GAME_LIST(44, "serverGameList"),
  CLIENT_USER_SETTINGS(45, "clientUserSettings"),
  SERVER_USER_SETTINGS(46, "serverUserSettings"),
  CLIENT_REPLAY(47, "clientReplay"),
  SERVER_REPLAY(48, "serverReplay"),
  CLIENT_THROW_TEAM_MATE(49, "clientThrowTeamMate"),
  CLIENT_PLAYER_CHOICE(50, "clientPlayerChoice"),
  // 51 is obsolete (CLIENT_DUMP_OFF)
  CLIENT_TIMEOUT_POSSIBLE(52, "clientTimeoutPossible"),
  CLIENT_ILLEGAL_PROCEDURE(53, "clientIllegalProcedure"),
  CLIENT_CONCEDE_GAME(54, "clientConcedeGame"),
  SERVER_ADMIN_MESSAGE(55, "serverAdminMessage"),
  CLIENT_USE_INDUCEMENT(56, "clientUseInducement"),
  CLIENT_BUY_INDUCEMENTS(57, "clientBuyInducements"),
  SERVER_ADD_PLAYER(58, "serverAddPlayer"),
  CLIENT_JOURNEYMEN(59, "clientJourneymen"),
  CLIENT_GAZE(60, "clientGaze"),
  CLIENT_CONFIRM(61, "clientConfirm"),
  CLIENT_SET_MARKER(62, "clientSetMarker"),
  INTERNAL_SERVER_FUMBBL_GAME_CREATED(63, "internalServerFumbblGameCreated"),
  INTERNAL_SERVER_FUMBBL_TEAM_LOADED(64, "internalServerFumbblTeamLoaded"),
  INTERNAL_SERVER_FUMBBL_GAME_CHECKED(65, "internalServerFumbblTeamLoaded"),
  INTERNAL_SERVER_JOIN_APPROVED(66, "internalServerJoinApproved"),
  INTERNAL_SERVER_REPLAY_LOADED(67, "internalServerReplayGameLoaded"),
  CLIENT_PETTY_CASH(68, "clientPettyCash"),
  SERVER_REMOVE_PLAYER(69, "serverRemovePlayer"),
  CLIENT_WIZARD_SPELL(70, "clientWizardSpell"),
  CLIENT_BUY_CARD(71, "clientBuyCard"),
  INTERNAL_SERVER_CLOSE_GAME(72, "internalServerCloseGame"),
  INTERNAL_SERVER_DELETE_GAME(73, "internalServerDeleteGame"),
  INTERNAL_SERVER_UPLOAD_GAME(74, "internalServerUploadGame"),
  INTERNAL_SERVER_SCHEDULE_GAME(75, "internalServerScheduleGame"),
  INTERNAL_SERVER_BACKUP_GAME(76, "internalServerBackupGame");
  
  private int fId;
  private String fName;
  
  private NetCommandId(int pValue, String pName) {
    fId = pValue;
    fName = pName;
  }
  
  public int getId() {
    return fId;
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
      case CLIENT_SETUP_PLAYER:
        return new ClientCommandSetupPlayer();
      case CLIENT_START_GAME:
        return new ClientCommandStartGame();
      case CLIENT_ACTING_PLAYER:
        return new ClientCommandActingPlayer();
      case CLIENT_MOVE:
        return new ClientCommandMove();
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
      case CLIENT_PING:
        return new ClientCommandPing();
      case SERVER_PING:
        return new ServerCommandPing();
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
      case CLIENT_PLAYER_CHOICE:
        return new ClientCommandPlayerChoice();
      case SERVER_MODEL_SYNC:
        return new ServerCommandModelSync();
      case CLIENT_TIMEOUT_POSSIBLE:
        return new ClientCommandTimeoutPossible();
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
      default:
        throw new IllegalStateException("Unhandled netCommandId " + this + ".");
    }
  }

}
