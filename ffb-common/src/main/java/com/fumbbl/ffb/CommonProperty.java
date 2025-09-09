package com.fumbbl.ffb;

import java.util.Arrays;

/*
	Name value must not exceed 40 chars
 */
public enum CommonProperty {

	CLIENT_COMMAND_COMPRESSION("client.command.compression"),

	CLIENT_PING_INTERVAL("client.ping.interval"),
	CLIENT_DEBUG_STATE("client.debug.state"),

	HTTPCLIENT_TIMEOUT_CONNECT("httpclient.timeout.connect"),
	HTTPCLIENT_TIMEOUT_SOCKET("httpclient.timeout.socket"),

	SETTING_RE_ROLL_BALL_AND_CHAIN("setting.reRollBallAndChain", "Ask for Whirling Dervish", "Gameplay"),
	SETTING_PLAYER_MARKING_TYPE("setting.playerMarkingType", "Player Marking", "Configuration"),

	SETTING_SOUND_MODE("setting.sound.mode", "Sound", "Configuration", "Sound On/Mute Specs/Off"),
	SETTING_SOUND_VOLUME("setting.sound.volume", "Sound Volume", "Configuration"),
	SETTING_ICONS("setting.icons", "Icons", "Graphics"),
	SETTING_AUTOMOVE("setting.automove", "Automove", "Gameplay"),
	SETTING_BLITZ_TARGET_PANEL("setting.blitzTargetPanel", "Blitz Target Panel", "Gameplay"),
	SETTING_GAZE_TARGET_PANEL("setting.gazeTargetPanel", "Gaze Target Panel", "Gameplay"),
	SETTING_RIGHT_CLICK_END_ACTION("setting.rightClickEndAction", "Right Click Behaviour", "Gameplay"),
	SETTING_PITCH_CUSTOMIZATION("setting.pitch.customization", "Pitch Customization", "Graphics"),
	SETTING_PITCH_MARKINGS("setting.pitch.markings", "Distance Markings", "Graphics"),
	SETTING_PITCH_MARKINGS_ROW("setting.pitch.markings.row", "Row Markings", "Graphics"),
	SETTING_TEAM_LOGOS("setting.pitch.teamLogos", "Team Logo", "Graphics"),
	SETTING_PITCH_WEATHER("setting.pitch.weather", "Pitch Weather", "Graphics"),
	SETTING_RANGEGRID("setting.rangegrid", "Range Grid", "Gameplay"),
	SETTING_MARK_USED_PLAYERS("setting.mark.used.players", "Mark used players", "Gameplay"),
	SETTING_SHOW_AUTO_MARKING_DIALOG("setting.show.autoMarkingDialog", "Show automarking dialog", "Configuration"),
	SETTING_SWAP_TEAM_COLORS("setting.swap.team.colors", "Swap team colors", "Graphics"),
	SETTING_BACKGROUND_CHAT("setting.background.chat", "Chat", "Background", "Chat Color"),
	SETTING_BACKGROUND_LOG("setting.background.log", "Log", "Background", "Log Color"),
	SETTING_BACKGROUND_FRAME("setting.background.frame", "Frame", "Background", "Frame Graphics/Color"),
	SETTING_BACKGROUND_FRAME_COLOR("setting.background.frame.color", "Color", "Background", "Frame Color"),
	SETTING_FONT_COLOR_TEXT("setting.font.color.text", "Regular text", "Color"),
	SETTING_FONT_COLOR_AWAY("setting.font.color.away", "Away text", "Color"),
	SETTING_FONT_COLOR_HOME("setting.font.color.home", "Home text", "Color"),
	SETTING_FONT_COLOR_SPEC("setting.font.color.spec", "Spectators text", "Color"),
	SETTING_FONT_COLOR_DEV("setting.font.color.dev", "Devs text", "Color"),
	SETTING_FONT_COLOR_ADMIN("setting.font.color.admin", "Admin text", "Color"),
	SETTING_FONT_COLOR_FRAME("setting.font.color.frame", "Sidebar/Scoreboard", "Color"),
	SETTING_FONT_COLOR_FRAME_SHADOW("setting.font.color.frameShadow", "Sidebar/Scoreboard shadow", "Color"),
	SETTING_FONT_COLOR_INPUT("setting.font.color.input", "Chat input", "Color"),
	SETTING_FONT_COLOR_PLAYER_MARKER_HOME("setting.font.color.playerMarkerHome", "Home Player Marker", "Color"),
	SETTING_FONT_COLOR_PLAYER_MARKER_AWAY("setting.font.color.playerMarkerAway", "Away Player Marker", "Color"),
	SETTING_FONT_COLOR_ADDITIONAL_PLAYER_MARKER_HOME("setting.font.color.addPlayerMarkerHome", "Additional Home Player Marker", "Color"),
	SETTING_FONT_COLOR_ADDITIONAL_PLAYER_MARKER_AWAY("setting.font.color.addPlayerMarkerAway", "Additional Away Player Marker", "Color"),
	SETTING_FONT_COLOR_FIELD_MARKER("setting.font.color.fieldMarker", "Field Marker", "Color"),
	SETTING_TZ_COLOR_HOME("setting.tz.color.home", "Tacklezones Home", "Color"),
	SETTING_TZ_COLOR_AWAY("setting.tz.color.away", "Tacklezones Away", "Color"),

	SETTING_LAST_CHANGE_LOG_FINGERPRINT("setting.lastChangeLogFingerPrint", "What's new?", "Configuration"),

	SETTING_LOCAL_SETTINGS("setting.localSettings", "Local Stored Settings", "", "", true),
	SETTING_SHOW_CRATERS_AND_BLOODSPOTS("setting.showCratersAndBloodspots", "Craters and bloodspots", "Graphics"),
	SETTING_SWEET_SPOT("setting.sweet.spot", "Show Sweet Spot", "Configuration"),
	SETTING_LOCAL_ICON_CACHE("setting.localIconCache", "Local Icon Cache", "Configuration"),
	SETTING_LOCAL_ICON_CACHE_PATH("setting.localIconCache.path", "Local Icon Cache Folder", "Configuration"),
	SETTING_SCALE_FACTOR("setting.scaleFactor", "Client Size", "Configuration"),
	// usually this should now be called differently, but changing the key would reset the setting
	SETTING_UI_LAYOUT("setting.pitch.orientation", "Client Layout", "Configuration"),
	SETTING_UI("setting.ui", "Client UI", "Configuration"),
	SETTING_LOG("setting.log", "Client Logging", "Configuration"),
	SETTING_LOG_DIR("setting.log.dir", "Logging Folder", "Configuration"),
	SETTING_LOG_MODE("setting.log.mode", "Logging on/off", "Configuration"),
	SETTING_SKETCH_CURSOR("setting.sketchCursor", "Sketch Cursor", "Configuration"),

	// Tacklezones
	SETTING_TACKLEZONES_PLAYER_MODE("setting.tacklezones.playerMode", "Player Mode", "Gameplay", "Tacklezones: Player Mode"),
	SETTING_TACKLEZONES_SPECTATOR_MODE("setting.tacklezones.spectatorMode", "Spectator Mode", "Gameplay", "Tacklezones: Spectator Mode"),
	SETTING_TACKLEZONES_OVERLAP("setting.tacklezones.overlap", "Overlap", "Gameplay"),
	SETTING_TACKLEZONES_CONTOUR("setting.tacklezones.contour", "Contour", "Gameplay"),
	;

	public static final CommonProperty[] _SAVED_USER_SETTINGS = {SETTING_SOUND_MODE, SETTING_UI_LAYOUT,
		SETTING_SOUND_VOLUME, SETTING_ICONS, SETTING_RIGHT_CLICK_END_ACTION,
		SETTING_AUTOMOVE, SETTING_BLITZ_TARGET_PANEL, SETTING_GAZE_TARGET_PANEL, SETTING_PITCH_CUSTOMIZATION,
		SETTING_PITCH_MARKINGS, SETTING_PITCH_MARKINGS_ROW, SETTING_TEAM_LOGOS, SETTING_PITCH_WEATHER,
		SETTING_RANGEGRID, SETTING_LAST_CHANGE_LOG_FINGERPRINT, SETTING_RE_ROLL_BALL_AND_CHAIN, SETTING_MARK_USED_PLAYERS,
		SETTING_PLAYER_MARKING_TYPE, SETTING_SHOW_AUTO_MARKING_DIALOG, SETTING_SWAP_TEAM_COLORS,
		SETTING_BACKGROUND_CHAT, SETTING_BACKGROUND_LOG, SETTING_BACKGROUND_FRAME,
		SETTING_BACKGROUND_FRAME_COLOR, SETTING_FONT_COLOR_TEXT, SETTING_FONT_COLOR_AWAY,
		SETTING_FONT_COLOR_HOME, SETTING_FONT_COLOR_SPEC, SETTING_FONT_COLOR_DEV,
		SETTING_FONT_COLOR_ADMIN, SETTING_FONT_COLOR_FRAME, SETTING_FONT_COLOR_FRAME_SHADOW, SETTING_FONT_COLOR_INPUT,
		SETTING_SCALE_FACTOR, SETTING_LOCAL_SETTINGS, SETTING_SHOW_CRATERS_AND_BLOODSPOTS, SETTING_SWEET_SPOT,
		SETTING_LOCAL_ICON_CACHE, SETTING_LOCAL_ICON_CACHE_PATH, SETTING_FONT_COLOR_PLAYER_MARKER_HOME,
		SETTING_FONT_COLOR_PLAYER_MARKER_AWAY, SETTING_FONT_COLOR_FIELD_MARKER, SETTING_LOG_DIR,
		SETTING_LOG_MODE, SETTING_SKETCH_CURSOR, SETTING_FONT_COLOR_ADDITIONAL_PLAYER_MARKER_HOME,
		SETTING_FONT_COLOR_ADDITIONAL_PLAYER_MARKER_AWAY, SETTING_TACKLEZONES_PLAYER_MODE, SETTING_TACKLEZONES_OVERLAP,
		SETTING_TACKLEZONES_SPECTATOR_MODE, SETTING_TACKLEZONES_CONTOUR, SETTING_TZ_COLOR_HOME, SETTING_TZ_COLOR_AWAY
	};

	public static final CommonProperty[] COLOR_SETTINGS = {
		SETTING_BACKGROUND_CHAT, SETTING_BACKGROUND_LOG,
		SETTING_BACKGROUND_FRAME_COLOR, SETTING_FONT_COLOR_TEXT, SETTING_FONT_COLOR_AWAY,
		SETTING_FONT_COLOR_HOME, SETTING_FONT_COLOR_SPEC, SETTING_FONT_COLOR_DEV,
		SETTING_FONT_COLOR_ADMIN, SETTING_FONT_COLOR_FRAME, SETTING_FONT_COLOR_FRAME_SHADOW, SETTING_FONT_COLOR_INPUT,
		SETTING_FONT_COLOR_PLAYER_MARKER_HOME, SETTING_FONT_COLOR_PLAYER_MARKER_AWAY,  SETTING_FONT_COLOR_FIELD_MARKER,
		SETTING_FONT_COLOR_ADDITIONAL_PLAYER_MARKER_HOME, SETTING_FONT_COLOR_ADDITIONAL_PLAYER_MARKER_AWAY,
		SETTING_TZ_COLOR_HOME, SETTING_TZ_COLOR_AWAY
	};
	public static final CommonProperty[] BACKGROUND_COLOR_SETTINGS = {
		SETTING_BACKGROUND_CHAT, SETTING_BACKGROUND_LOG, SETTING_BACKGROUND_FRAME_COLOR
	};
	public static final CommonProperty[] FONT_COLOR_SETTINGS = {
		SETTING_FONT_COLOR_TEXT, SETTING_FONT_COLOR_AWAY,
		SETTING_FONT_COLOR_HOME, SETTING_FONT_COLOR_SPEC, SETTING_FONT_COLOR_DEV,
		SETTING_FONT_COLOR_ADMIN, SETTING_FONT_COLOR_FRAME, SETTING_FONT_COLOR_FRAME_SHADOW,
		SETTING_FONT_COLOR_PLAYER_MARKER_HOME, SETTING_FONT_COLOR_PLAYER_MARKER_AWAY, SETTING_FONT_COLOR_FIELD_MARKER,
		SETTING_FONT_COLOR_ADDITIONAL_PLAYER_MARKER_HOME, SETTING_FONT_COLOR_ADDITIONAL_PLAYER_MARKER_AWAY,
		SETTING_TZ_COLOR_HOME, SETTING_TZ_COLOR_AWAY
	};
	private final String key;

	private final String value;

	private final String category;

	private final boolean storedRemote;
	private final String dialogValue;

	CommonProperty(String key) {
		this(key, null, "", "", true);
	}

	CommonProperty(String key, String value, String category) {
		this(key, value, category, value);
	}

	CommonProperty(String key, String value, String category, String dialogValue) {
		this(key, value, category, dialogValue, false);
	}

	CommonProperty(String key, String value, String category, String dialogValue, boolean storedRemote) {
		this.key = key;
		this.value = value;
		this.category = category;
		this.storedRemote = storedRemote;
		this.dialogValue = dialogValue;
	}

	public static CommonProperty forKey(String key) {
		return Arrays.stream(values()).filter(value -> value.key.equalsIgnoreCase(key)).findFirst().orElse(null);
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public String getCategory() {
		return category;
	}

	public String getDialogValue() {
		return dialogValue;
	}

	public boolean isStoredRemote() {
		return storedRemote;
	}
}
