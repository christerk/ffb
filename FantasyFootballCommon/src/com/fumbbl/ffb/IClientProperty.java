package com.fumbbl.ffb;

/**
 * @author Kalimar
 */
public interface IClientProperty extends CommonProperty {

	String SERVER_HOST = "server.host";
	String SERVER_PORT = "server.port";

	String CLIENT_COMMAND_COMPRESSION = "client.command.compression";

	String SETTING_SOUND_MODE = "setting.sound.mode";
	String SETTING_SOUND_VOLUME = "setting.sound.volume";
	String SETTING_ICONS = "setting.icons";
	String SETTING_CHATLOG = "setting.chatlog";
	String SETTING_AUTOMOVE = "setting.automove";
	String SETTING_BLITZ_TARGET_PANEL = "setting.blitzTargetPanel";
	String SETTING_GAZE_TARGET_PANEL = "setting.gazeTargetPanel";
	String SETTING_RIGHT_CLICK_END_ACTION = "setting.rightClickEndAction";
	String SETTING_PITCH_CUSTOMIZATION = "setting.pitch.customization";
	String SETTING_PITCH_MARKINGS = "setting.pitch.markings";
	String SETTING_TEAM_LOGOS = "setting.pitch.teamLogos";
	String SETTING_PITCH_WEATHER = "setting.pitch.weather";
	String SETTING_RANGEGRID = "setting.rangegrid";
	String SETTING_PITCH_ORIENTATION = "setting.pitch.orientation";
	String SETTING_MARK_USED_PLAYERS = "setting.mark.used.players";
	String SETTING_SHOW_AUTO_MARKING_DIALOG = "setting.show.autoMarkingDialog";
	String SETTING_SWAP_TEAM_COLORS = "setting.swap.team.colors";

	String SETTING_LAST_CHANGE_LOG_FINGERPRINT = "setting.lastChangeLogFingerPrint";

	String CLIENT_PING_INTERVAL = "client.ping.interval";
	String CLIENT_PING_MAX_DELAY = "client.ping.maxDelay";
	String CLIENT_DEBUG_STATE = "client.debug.state";

	String KEY_PLAYER_MOVE_NORTH = "key.player.move.north";
	String KEY_PLAYER_MOVE_NORTHEAST = "key.player.move.northeast";
	String KEY_PLAYER_MOVE_EAST = "key.player.move.east";
	String KEY_PLAYER_MOVE_SOUTHEAST = "key.player.move.southeast";
	String KEY_PLAYER_MOVE_SOUTH = "key.player.move.south";
	String KEY_PLAYER_MOVE_SOUTHWEST = "key.player.move.southwest";
	String KEY_PLAYER_MOVE_WEST = "key.player.move.west";
	String KEY_PLAYER_MOVE_NORTHWEST = "key.player.move.northwest";

	String KEY_PLAYER_SELECT = "key.player.select";
	String KEY_PLAYER_CYCLE_RIGHT = "key.player.cycle.right";
	String KEY_PLAYER_CYCLE_LEFT = "key.player.cycle.left";

	String KEY_PLAYER_ACTION_BLOCK = "key.player.action.block";
	String KEY_PLAYER_ACTION_BLITZ = "key.player.action.blitz";
	String KEY_PLAYER_ACTION_FOUL = "key.player.action.foul";
	String KEY_PLAYER_ACTION_MOVE = "key.player.action.move";
	String KEY_PLAYER_ACTION_STAND_UP = "key.player.action.standup";
	String KEY_PLAYER_ACTION_HAND_OVER = "key.player.action.handover";
	String KEY_PLAYER_ACTION_PASS = "key.player.action.pass";
	String KEY_PLAYER_ACTION_JUMP = "key.player.action.jump";
	String KEY_PLAYER_ACTION_END_MOVE = "key.player.action.endMove";
	String KEY_PLAYER_ACTION_STAB = "key.player.action.stab";
	String KEY_PLAYER_ACTION_CHAINSAW = "key.player.action.chainsaw";
	String KEY_PLAYER_ACTION_GAZE = "key.player.action.gaze";
	String KEY_PLAYER_ACTION_GAZE_ZOAT = "key.player.action.gazeZoat";
	String KEY_PLAYER_ACTION_FUMBLEROOSKIE = "key.player.action.fumblerooskie";
	String KEY_PLAYER_ACTION_PROJECTILE_VOMIT = "key.player.action.projectileVomit";
	String KEY_PLAYER_ACTION_RANGE_GRID = "key.player.action.rangeGrid";
	String KEY_PLAYER_ACTION_HAIL_MARY_PASS = "key.player.action.hailMaryPass";
	String KEY_PLAYER_ACTION_MULTIPLE_BLOCK = "key.player.action.multipleBlock";
	String KEY_PLAYER_ACTION_FRENZIED_RUSH = "key.player.action.frenziedRush";
	String KEY_PLAYER_ACTION_SHOT_TO_NOTHING = "key.player.action.shotToNothing";
	String KEY_PLAYER_ACTION_SHOT_TO_NOTHING_BOMB = "key.player.action.shotToNothingBomb";
	String KEY_PLAYER_ACTION_TREACHEROUS = "key.player.action.treacherous";
	String KEY_PLAYER_ACTION_WISDOM = "key.player.action.wisdom";
	String KEY_PLAYER_ACTION_BEER_BARREL_BASH = "key.player.action.beerBarrelBash";
	String KEY_PLAYER_ACTION_RAIDING_PARTY = "key.player.action.raidingParty";
	String KEY_PLAYER_ACTION_LOOK_INTO_MY_EYES = "key.player.action.lookIntoMyEyes";
	String KEY_PLAYER_ACTION_BALEFUL_HEX = "key.player.action.balefulHex";
	String KEY_PLAYER_ACTION_HIT_AND_RUN = "key.player.action.hitAndRun";
	String KEY_PLAYER_ACTION_KICK_EM_BLOCK = "key.player.action.kickEmBlock";
	String KEY_PLAYER_ACTION_KICK_EM_BLITZ = "key.player.action.kickEmBlitz";

	String KEY_TOOLBAR_TURN_END = "key.toolbar.turn.end";
	String KEY_TOOLBAR_ILLEGAL_PROCEDURE = "key.toolbar.illegal.procedure";
	String KEY_PLAYER_ACTION_ALL_YOU_CAN_EAT = "key.player.action.allYouCanEat";

	String KEY_MENU_REPLAY = "key.menu.replay";
	String KEY_MENU_SETUP_LOAD = "key.menu.setup.load";
	String KEY_MENU_SETUP_SAVE = "key.menu.setup.save";


	String[] _SAVED_USER_SETTINGS = {SETTING_SOUND_MODE, SETTING_PITCH_ORIENTATION,
		SETTING_SOUND_VOLUME, SETTING_ICONS, SETTING_CHATLOG, SETTING_RIGHT_CLICK_END_ACTION,
		SETTING_AUTOMOVE, SETTING_BLITZ_TARGET_PANEL, SETTING_GAZE_TARGET_PANEL, SETTING_PITCH_CUSTOMIZATION,
		SETTING_PITCH_MARKINGS, SETTING_TEAM_LOGOS, SETTING_PITCH_WEATHER,
		SETTING_RANGEGRID, SETTING_LAST_CHANGE_LOG_FINGERPRINT, SETTING_RE_ROLL_BALL_AND_CHAIN, SETTING_MARK_USED_PLAYERS,
		SETTING_PLAYER_MARKING_TYPE, SETTING_SHOW_AUTO_MARKING_DIALOG, SETTING_SWAP_TEAM_COLORS
	};
}
