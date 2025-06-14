package com.fumbbl.ffb.net;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.net.commands.*;

/**
 * @author Kalimar
 */
public enum NetCommandId implements INamedObject {

	INTERNAL_SERVER_SOCKET_CLOSED("internalServerSocketClosed"), CLIENT_JOIN("clientJoin"), CLIENT_TALK("clientTalk"),
	SERVER_GAME_STATE("serverGameState"), SERVER_TEAM_LIST("serverTeamList"), SERVER_STATUS("serverStatus"),
	SERVER_JOIN("serverJoin"), SERVER_LEAVE("serverLeave"), SERVER_TALK("serverTalk"),
	CLIENT_SETUP_PLAYER("clientSetupPlayer"), CLIENT_START_GAME("clientStartGame"),
	CLIENT_ACTING_PLAYER("clientActingPlayer"), CLIENT_MOVE("clientMove"), CLIENT_BLITZ_MOVE("clientBlitzMove"),
	CLIENT_BLITZ_TARGET_SELECTED("blitzTargetSelected"), CLIENT_TARGET_SELECTED("targetSelected"), CLIENT_USE_RE_ROLL("clientUseReRoll"), CLIENT_USE_RE_ROLL_FOR_TARGET("clientUseReRollForTarget"),
	SERVER_SOUND("serverSound"), CLIENT_COIN_CHOICE("clientCoinChoice"), CLIENT_RECEIVE_CHOICE("clientReceiveChoice"),
	CLIENT_END_TURN("clientEndTurn"), CLIENT_KICKOFF("clientKickoff"), CLIENT_TOUCHBACK("clientTouchback"),
	CLIENT_HAND_OVER("clientHandOver"), CLIENT_PASS("clientPass"), CLIENT_BLOCK("clientBlock"),
	CLIENT_BLOCK_CHOICE("clientBlockChoice"), CLIENT_PUSHBACK("clientPushback"),
	CLIENT_USE_CONSUMMATE_RE_ROLL_FOR_BLOCK("clientUseConsummateReRollForBlock"),
	CLIENT_USE_PRO_RE_ROLL_FOR_BLOCK("clientUseProReRollForBlock"),
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
	INTERNAL_SERVER_FUMBBL_GAME_CHECKED("internalServerFumbblGameChecked"),
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
	CLIENT_FIELD_COORDINATE("clientFieldCoordinate"), CLIENT_USE_FUMBLEROOSKIE("clientUseFumblerooskie"),
	CLIENT_PRAYER_SELECTION("clientPrayerSelection"), CLIENT_USE_TEAM_MATES_WISDOM("clientUseTeamMatesWisdom"),
	CLIENT_THROW_KEG("clientThrowKeg"), CLIENT_SELECT_WEATHER("clientSelectWeather"), CLIENT_UPDATE_PLAYER_MARKINGS("clientUpdatePlayerMarkings"),
	CLIENT_KICK_OFF_RESULT_CHOICE("clientKickOffResultChoice"), CLIENT_BLOODLUST_ACTION("clientBloodlustAction"),
	SERVER_UPDATE_LOCAL_PLAYER_MARKERS("serverUpdateLocalPlayerMarkers"),
	INTERNAL_SERVER_ADD_LOADED_TEAM("internalServerAddLoadedTeam"),
	INTERNAL_APPLY_AUTOMATIC_PLAYER_MARKINGS("internalApplyAutomaticPlayerMarkings"), CLIENT_USE_SINGLE_BLOCK_DIE_RE_ROLL("clientUseSingleBlockDieReRoll"),
	CLIENT_USE_MULTI_BLOCK_DICE_RE_ROLL("clientUseMultiBlockDiceReRoll"), INTERNAL_CALCULATE_AUTOMATIC_PLAYER_MARKINGS("internalCalculateAutomaticPlayerMarkings"),
	CLIENT_LOAD_AUTOMATIC_PLAYER_MARKINGS("clientLoadPlayerMarkings"), SERVER_AUTOMATIC_PLAYER_MARKINGS("serverAutomaticPlayerMarkings"),
	CLIENT_REPLAY_STATUS("clientReplayStatus"), SERVER_REPLAY_STATUS("serverReplayStatus"), CLIENT_JOIN_REPLAY("clientJoinReplay"),
	SERVER_REPLAY_CONTROL("serverReplayControl"), CLIENT_TRANSFER_REPLAY_CONTROL("clientTransferReplayControl"),
	CLIENT_ADD_SKETCH("clientAddSketch"), CLIENT_REMOVE_SKETCHES("clientRemoveSketches"), CLIENT_SKETCH_ADD_COORDINATE("clientSketchAddCoordinate"),
	CLIENT_SKETCH_SET_COLOR("clientSketchSetColor"), CLIENT_SKETCH_SET_LABEL("clientSketchSetLabel"), CLIENT_CLEAR_SKETCHES("clientClearSketches"),
	SERVER_ADD_SKETCHES("serverAddSketches"), SERVER_REMOVE_SKETCHES("serverRemoveSketches"), SERVER_SKETCH_ADD_COORDINATE("serverSketchAddCoordinate"),
	SERVER_SKETCH_SET_COLOR("serverSketchSetColor"), SERVER_SKETCH_SET_LABEL("serverSketchSetLabel"), SERVER_CLEAR_SKETCHES("serverClearSketches"),
	CLIENT_SET_PREVENT_SKETCHING("clientSetPreventSketching"), SERVER_SET_PREVENT_SKETCHING("serverSetPreventSketching");

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
			case CLIENT_TARGET_SELECTED:
				return new ClientCommandTargetSelected();
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
			case CLIENT_PRAYER_SELECTION:
				return new ClientCommandSkillSelection();
			case CLIENT_USE_CONSUMMATE_RE_ROLL_FOR_BLOCK:
				return new ClientCommandUseConsummateReRollForBlock();
			case CLIENT_USE_TEAM_MATES_WISDOM:
				return new ClientCommandUseTeamMatesWisdom();
			case CLIENT_THROW_KEG:
				return new ClientCommandThrowKeg();
			case CLIENT_SELECT_WEATHER:
				return new ClientCommandSelectWeather();
			case CLIENT_UPDATE_PLAYER_MARKINGS:
				return new ClientCommandUpdatePlayerMarkings();
			case SERVER_UPDATE_LOCAL_PLAYER_MARKERS:
				return new ServerCommandUpdateLocalPlayerMarkers();
			case CLIENT_KICK_OFF_RESULT_CHOICE:
				return new ClientCommandKickOffResultChoice();
			case CLIENT_BLOODLUST_ACTION:
				return new ClientCommandBloodlustAction();
			case CLIENT_USE_SINGLE_BLOCK_DIE_RE_ROLL:
				return new ClientCommandUseSingleBlockDieReRoll();
			case CLIENT_USE_MULTI_BLOCK_DICE_RE_ROLL:
				return new ClientCommandUseMultiBlockDiceReRoll();
			case CLIENT_LOAD_AUTOMATIC_PLAYER_MARKINGS:
				return new ClientCommandLoadAutomaticPlayerMarkings();
			case SERVER_AUTOMATIC_PLAYER_MARKINGS:
				return new ServerCommandAutomaticPlayerMarkings();
			case CLIENT_REPLAY_STATUS:
				return new ClientCommandReplayStatus();
			case CLIENT_JOIN_REPLAY:
				return new ClientCommandJoinReplay();
			case SERVER_REPLAY_STATUS:
				return new ServerCommandReplayStatus();
			case SERVER_REPLAY_CONTROL:
				return new ServerCommandReplayControl();
			case CLIENT_TRANSFER_REPLAY_CONTROL:
				return new ClientCommandTransferReplayControl();
			case CLIENT_REMOVE_SKETCHES:
				return new ClientCommandRemoveSketches();
			case CLIENT_CLEAR_SKETCHES:
				return new ClientCommandClearSketches();
			case CLIENT_ADD_SKETCH:
				return new ClientCommandAddSketch();
			case CLIENT_SKETCH_ADD_COORDINATE:
				return new ClientCommandSketchAddCoordinate();
			case CLIENT_SKETCH_SET_COLOR:
				return new ClientCommandSketchSetColor();
			case CLIENT_SKETCH_SET_LABEL:
				return new ClientCommandSketchSetLabel();
			case SERVER_ADD_SKETCHES:
				return new ServerCommandAddSketches();
			case SERVER_REMOVE_SKETCHES:
				return new ServerCommandRemoveSketches();
			case SERVER_SKETCH_ADD_COORDINATE:
				return new ServerCommandSketchAddCoordinate();
			case SERVER_SKETCH_SET_COLOR:
				return new ServerCommandSketchSetColor();
			case SERVER_SKETCH_SET_LABEL:
				return new ServerCommandSketchSetLabel();
			case SERVER_CLEAR_SKETCHES:
				return new ServerCommandClearSketches();
			case CLIENT_SET_PREVENT_SKETCHING:
				return new ClientCommandSetPreventSketching();
			case SERVER_SET_PREVENT_SKETCHING:
				return new ServerCommandSetPreventSketching();
			default:
				throw new IllegalStateException("Unhandled netCommandId " + this + ".");
		}
	}

}
