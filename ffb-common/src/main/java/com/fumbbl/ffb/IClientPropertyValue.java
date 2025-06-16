package com.fumbbl.ffb;

/**
 * @author Kalimar
 */
public interface IClientPropertyValue extends CommonPropertyValue {

	String SETTING_SOUND_ON = "soundOn";
	String SETTING_SOUND_MUTE_SPECTATORS = "muteSpectators";
	String SETTING_SOUND_OFF = "soundOff";

	String SETTING_ICONS_TEAM = "iconsTeam";
	String SETTING_ICONS_ROSTER_OPPONENT = "iconsRosterOpponent";
	String SETTING_ICONS_ROSTER_BOTH = "iconsRoster";
	String SETTING_ICONS_ABSTRACT = "iconsAbstract";

	String SETTING_AUTOMOVE_ON = "automoveOn";
	String SETTING_AUTOMOVE_OFF = "automoveOff";

	String SETTING_BLITZ_TARGET_PANEL_ON = "showBlitzTargetPanelOn";
	String SETTING_BLITZ_TARGET_PANEL_OFF = "showBlitzTargetPanelOff";

	String SETTING_GAZE_TARGET_PANEL_ON = "showGazeTargetPanelOn";
	String SETTING_GAZE_TARGET_PANEL_OFF = "showGazeTargetPanelOff";

	String SETTING_RIGHT_CLICK_END_ACTION_ON = "rightClickEndActionOn";
	String SETTING_RIGHT_CLICK_OPENS_CONTEXT_MENU = "rightClickOpensContextMenu";
	String SETTING_RIGHT_CLICK_LEGACY_MODE = "rightClickLegacyMode";
	String SETTING_RIGHT_CLICK_END_ACTION_OFF = "rightClickEndActionOff";

	String SETTING_PITCH_CUSTOM = "pitchCustom";
	String SETTING_PITCH_DEFAULT = "pitchDefault";
	String SETTING_PITCH_BASIC = "pitchBasic";

	String SETTING_PITCH_MARKINGS_ON = "pitchMarkingsOn";
	String SETTING_PITCH_MARKINGS_OFF = "pitchMarkingsOff";

	String SETTING_PITCH_MARKINGS_ROW_ON = "pitchMarkingsRowOn";
	String SETTING_PITCH_MARKINGS_ROW_OFF = "pitchMarkingsRowOff";

	// keep old property value ids for backwards compatibility
	String SETTING_LAYOUT_LANDSCAPE = "pitchLandscape";
	String SETTING_LAYOUT_PORTRAIT = "pitchPortrait";
	String SETTING_LAYOUT_SQUARE = "layoutSquare";
	String SETTING_LAYOUT_WIDE = "layoutWide";

	String SETTING_TEAM_LOGOS_BOTH = "teamLogosBoth";
	String SETTING_TEAM_LOGOS_OWN = "teamLogosOwn";
	String SETTING_TEAM_LOGOS_NONE = "teamLogosNone";

	String SETTING_PITCH_WEATHER_ON = "pitchWeatherOn";
	String SETTING_PITCH_WEATHER_OFF = "pitchWeatherOff";

	String SETTING_RANGEGRID_ALWAYS_ON = "rangegridAlwaysOn";

	String SETTING_MARK_USED_PLAYERS_DEFAULT = "markUsedPlayersDefault";
	String SETTING_MARK_USED_PLAYERS_CHECK_ICON_GREEN = "markUsedPlayersCheckIconGreen";
	String SETTING_HIDE_AUTO_MARKING_DIALOG = "hideAutoMarkingDialog";

	String SETTING_SWAP_TEAM_COLORS_ON = "swapTeamColorsOn";
	String SETTING_SWAP_TEAM_COLORS_OFF = "swapTeamColorsOff";

	String SETTING_BACKGROUND_FRAME_ICONS = "backgroundFrameIcons";
	String SETTING_BACKGROUND_FRAME_COLOR = "backgroundFrameColor";

	String SETTING_CRATERS_AND_BLOODSPOTS_SHOW = "cratersAndBloodspotsShow";
	String SETTING_CRATERS_AND_BLOODSPOTS_HIDE = "cratersAndBloodspotsHide";

	String SETTING_SWEET_SPOT_OFF = "sweetSpotOff";
	String SETTING_SWEET_SPOT_BLACK = "sweetSpotBlack";
	String SETTING_SWEET_SPOT_WHITE = "sweetSpotWhite";

	String SETTING_LOCAL_ICON_CACHE_OFF = "localIconCacheOff";
	String SETTING_LOCAL_ICON_CACHE_ON = "localIconCacheOn";

	String SETTING_LOG_ON = "logOn";
	String SETTING_LOG_OFF = "logOff";

	String SETTING_SKETCH_CURSOR_ON = "sketchCursorOn";
	String SETTING_SKETCH_CURSOR_OFF = "sketchCursorOff";
}
