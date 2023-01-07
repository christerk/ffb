package com.fumbbl.ffb;

/**
 *
 * @author Kalimar
 */
public interface IIconProperty {

	// Player decorations
	String DECORATION_PRONE = "decoration.prone";
	String DECORATION_STUNNED = "decoration.stunned";
	String DECORATION_CONFUSED = "decoration.confused";
	String DECORATION_HYPNOTIZED = "decoration.hypnotized";
	String DECORATION_DICE_1 = "decoration.dice1";
	String DECORATION_DICE_2 = "decoration.dice2";
	String DECORATION_DICE_3 = "decoration.dice3";
	String DECORATION_DICE_2_AGAINST = "decoration.dice2against";
	String DECORATION_DICE_3_AGAINST = "decoration.dice3against";
	String DECORATION_BALL = "decoration.ball";
	String DECORATION_BALL_SELECTED = "decoration.ball.selected";
	String DECORATION_BLOCK_HOME = "decoration.block.home";
	String DECORATION_BLOCK_AWAY = "decoration.block.away";
	String DECORATION_ROOTED = "decoration.rooted";
	String DECORATION_ROOTED_CONFUSED = "decoration.rooted.confused";
	String DECORATION_ROOTED_HYPNOTIZED = "decoration.rooted.hypnotized";
	String DECORATION_BLOOD_LUST = "decoration.bloodlust";
	String DECORATION_BOMB = "decoration.bomb";
	String DECORATION_BOMB_SELECTED = "decoration.bomb.selected";
	String DECORATION_BLITZ_TARGET_SELECTED = "decoration.selected.blitz.target";
	String DECORATION_GAZE_TARGET_SELECTED = "decoration.selected.gaze.target";
	String DECORATION_BLOCK_TARGET = "decoration.block.target";
	String DECORATION_STAB_TARGET = "decoration.stab.target";
	String DECORATION_CHECK_ICON_GREEN = "decoration.activated.checkIcon.green";

	// Game icons
	String GAME_COIN_HEADS = "game.coin.heads";
	String GAME_COIN_TAILS = "game.coin.tails";
	String GAME_PUSHBACK_NORTH = "game.pushback.north";
	String GAME_PUSHBACK_NORTH_SELECTED = "game.pushback.north.selected";
	String GAME_PUSHBACK_NORTHEAST = "game.pushback.northeast";
	String GAME_PUSHBACK_NORTHEAST_SELECTED = "game.pushback.northeast.selected";
	String GAME_PUSHBACK_EAST = "game.pushback.east";
	String GAME_PUSHBACK_EAST_SELECTED = "game.pushback.east.selected";
	String GAME_PUSHBACK_SOUTHEAST = "game.pushback.southeast";
	String GAME_PUSHBACK_SOUTHEAST_SELECTED = "game.pushback.southeast.selected";
	String GAME_PUSHBACK_SOUTH = "game.pushback.south";
	String GAME_PUSHBACK_SOUTH_SELECTED = "game.pushback.south.selected";
	String GAME_PUSHBACK_SOUTHWEST = "game.pushback.southwest";
	String GAME_PUSHBACK_SOUTHWEST_SELECTED = "game.pushback.southwest.selected";
	String GAME_PUSHBACK_WEST = "game.pushback.west";
	String GAME_PUSHBACK_WEST_SELECTED = "game.pushback.west.selected";
	String GAME_PUSHBACK_NORTHWEST = "game.pushback.northwest";
	String GAME_PUSHBACK_NORTHWEST_SELECTED = "game.pushback.northwest.selected";
	String GAME_BALL = "game.ball";
	String GAME_BALL_BIG = "game.ball.big";
	String GAME_DELETE = "game.delete";
	String GAME_REF = "game.ref";
	String GAME_DICE_SMALL = "game.dice.small";
	String GAME_FIREBALL_SMALL = "game.fireball.small";
	String GAME_LIGHTNING_SMALL = "game.lightning.small";
	String GAME_ZAP_SMALL = "game.zap.small";
	String GAME_ROCK = "game.rock";
	String GAME_BOMB = "game.bomb";
	String GAME_BOMB_BIG = "game.bomb.big";
	String GAME_SPLASH_SCREEN = "game.splashscreen";
	String GAME_TRAP_DOOR = "game.trapdoor";

	// Sidebar
	String SIDEBAR_BOX_BUTTON = "sidebar.box.button";
	String SIDEBAR_BOX_BUTTON_SELECTED = "sidebar.box.button.selected";
	String SIDEBAR_TURN_BUTTON = "sidebar.turn.button";
	String SIDEBAR_TURN_BUTTON_SELECTED = "sidebar.turn.button.selected";
	String SIDEBAR_STATUS_WAITING = "sidebar.status.waiting";
	String SIDEBAR_STATUS_PLAYING = "sidebar.status.playing";
	String SIDEBAR_STATUS_REF = "sidebar.status.ref";
	String SIDEBAR_BACKGROUND_PLAYER_DETAIL_RED = "sidebar.background.player.detail.red";
	String SIDEBAR_OVERLAY_PLAYER_DETAIL_RED = "sidebar.overlay.player.detail.red";
	String SIDEBAR_BACKGROUND_BOX_BUTTONS_RED = "sidebar.background.box.buttons.red";
	String SIDEBAR_BACKGROUND_TURN_DICE_STATUS_RED = "sidebar.background.turn.dice.status.red";
	String SIDEBAR_BACKGROUND_RESOURCE_RED = "sidebar.background.resource.red";
	String SIDEBAR_BACKGROUND_BOX = "sidebar.background.box";
	String SIDEBAR_BACKGROUND_PLAYER_PORTRAIT = "sidebar.background.player.portrait";
	String SIDEBAR_BACKGROUND_PLAYER_DETAIL_BLUE = "sidebar.background.player.detail.blue";
	String SIDEBAR_OVERLAY_PLAYER_DETAIL_BLUE = "sidebar.overlay.player.detail.blue";
	String SIDEBAR_BACKGROUND_BOX_BUTTONS_BLUE = "sidebar.background.box.buttons.blue";
	String SIDEBAR_BACKGROUND_TURN_DICE_STATUS_BLUE = "sidebar.background.turn.dice.status.blue";
	String SIDEBAR_BACKGROUND_RESOURCE_BLUE = "sidebar.background.resource.blue";
	String SIDEBAR_OVERLAY_PLAYER_CARD = "sidebar.overlay.player.card";

	// Resources
	String RESOURCE_APOTHECARY = "resource.apothecary";
	String RESOURCE_BLOODWEISER_KEG = "resource.bloodweiserKeg";
	String RESOURCE_BRIBE = "resource.bribe";
	String RESOURCE_CARD = "resource.card";
	String RESOURCE_PRAYER = "resource.prayer";
	String RESOURCE_MASTER_CHEF = "resource.masterChef";
	String RESOURCE_RE_ROLL = "resource.reRoll";
	String RESOURCE_IGOR = "resource.igor";
	String RESOURCE_WIZARD = "resource.wizard";
	String RESOURCE_COUNTER_SPRITE = "resource.counter.sprite";
	String RESOURCE_RE_ROLL_ARGUE = "resource.reRoll.argue";
	String RESOURCE_BIASED_REF = "resource.biased.ref";

	// Dice
	String DICE_BLOCK_1 = "dice.block.1";
	String DICE_BLOCK_2 = "dice.block.2";
	String DICE_BLOCK_3 = "dice.block.3";
	String DICE_BLOCK_4 = "dice.block.4";
	String DICE_BLOCK_5 = "dice.block.5";
	String DICE_BLOCK_6 = "dice.block.6";

	// Scorebar
	String SCOREBAR_BACKGROUND = "scorebar.background";
	String SCOREBAR_BACKGROUND_SQUARE = "scorebar.background.square";
	String SCOREBAR_SPECTATORS = "scorebar.spectators";
	String SCOREBAR_COACH_BANNED_HOME = "scorebar.coachBanned.home";
	String SCOREBAR_COACH_BANNED_AWAY = "scorebar.coachBanned.away";

	// Weather
	String WEATHER_BLIZZARD = "weather.blizzard";
	String WEATHER_NICE = "weather.nice";
	String WEATHER_RAIN = "weather.rain";
	String WEATHER_SUNNY = "weather.sunny";
	String WEATHER_HEAT = "weather.heat";
	String WEATHER_INTRO = "weather.intro";

	// Replay icons
	String REPLAY_PLAY_FORWARD = "replay.play.forward";
	String REPLAY_PLAY_FORWARD_SELECTED = "replay.play.forward.selected";
	String REPLAY_PLAY_FORWARD_ACTIVE = "replay.play.forward.active";
	String REPLAY_FAST_FORWARD = "replay.fast.forward";
	String REPLAY_FAST_FORWARD_SELECTED = "replay.fast.forward.selected";
	String REPLAY_FAST_FORWARD_ACTIVE = "replay.fast.forward.active";
	String REPLAY_SKIP_FORWARD = "replay.skip.forward";
	String REPLAY_SKIP_FORWARD_SELECTED = "replay.skip.forward.selected";
	String REPLAY_SKIP_FORWARD_ACTIVE = "replay.skip.forward.active";
	String REPLAY_PLAY_BACKWARD = "replay.play.backward";
	String REPLAY_PLAY_BACKWARD_SELECTED = "replay.play.backward.selected";
	String REPLAY_PLAY_BACKWARD_ACTIVE = "replay.play.backward.active";
	String REPLAY_FAST_BACKWARD = "replay.fast.backward";
	String REPLAY_FAST_BACKWARD_SELECTED = "replay.fast.backward.selected";
	String REPLAY_FAST_BACKWARD_ACTIVE = "replay.fast.backward.active";
	String REPLAY_SKIP_BACKWARD = "replay.skip.backward";
	String REPLAY_SKIP_BACKWARD_SELECTED = "replay.skip.backward.selected";
	String REPLAY_SKIP_BACKWARD_ACTIVE = "replay.skip.backward.active";
	String REPLAY_PAUSE = "replay.pause";
	String REPLAY_PAUSE_SELECTED = "replay.pause.selected";
	String REPLAY_PAUSE_ACTIVE = "replay.pause.active";

	// Player actions
	String ACTION_MOVE = "action.move";
	String ACTION_BLOCK = "action.block";
	String ACTION_MUTIPLE_BLOCK = "action.multiple.block";
	String ACTION_BLITZ = "action.blitz";
	String ACTION_FOUL = "action.foul";
	String ACTION_HAND_OVER = "action.handover";
	String ACTION_PASS = "action.pass";
	String ACTION_STAND_UP = "action.standup";
	String ACTION_END_MOVE = "action.end";
	String ACTION_JUMP = "action.jump";
	String ACTION_STAB = "action.stab";
	String ACTION_GAZE = "action.gaze";
	String ACTION_TOGGLE_RANGE_GRID = "action.toggle.rangeGrid";
	String ACTION_TOGGLE_HAIL_MARY_PASS = "action.toggle.hailMaryPass";
	String ACTION_BOMB = "action.bomb";
	String ACTION_TOGGLE_HAIL_MARY_BOMB = "action.toggle.hailMaryBomb";
	String ACTION_CHAINSAW = "action.chainsaw";
	String ACTION_VOMIT = "action.vomit";
	String ACTION_WISDOM = "action.wisdom";
	String ACTION_BEER_BARREL_BASH = "action.beerBarrelBash";
	String ACTION_RAIDING_PARTY = "action.raidingParty";
	String ACTION_LOOK_INTO_MY_EYES = "action.lookIntoMyEyes";
	String ACTION_BALEFUL_HEX = "action.balefulHex";
	String ACTION_HIT_AND_RUN = "action.hitAndRun";
	String ACTION_ALL_YOU_CAN_EAT = "action.allYouCanEat";

	// Player icons
	String PLAYER_SMALL_HOME = "players.small.home";
	String PLAYER_NORMAL_HOME = "players.normal.home";
	String PLAYER_LARGE_HOME = "players.large.home";
	String PLAYER_SMALL_AWAY = "players.small.away";
	String PLAYER_NORMAL_AWAY = "players.normal.away";
	String PLAYER_LARGE_AWAY = "players.large.away";
	String ZAPPEDPLAYER_ICONSET_PATH = "zappedplayer.iconset.path";

	// Mouse cursors
	String CURSOR_MOVE = "cursor.move";
	String CURSOR_GFI = "cursor.gfi";
	String CURSOR_DODGE = "cursor.dodge";
	String CURSOR_GFI_DODGE = "cursor.gfidodge";
	String CURSOR_BLOCK = "cursor.block";
	String CURSOR_INVALID_BLOCK = "cursor.invalidblock";
	String CURSOR_PASS = "cursor.pass";
	String CURSOR_FOUL = "cursor.foul";
	String CURSOR_GAZE = "cursor.gaze";
	String CURSOR_INVALID_GAZE = "cursor.invalidgaze";
	String CURSOR_BOMB = "cursor.bomb";
	String CURSOR_KEG = "cursor.keg";
	String CURSOR_INVALID_KEG = "cursor.invalidkeg";
	String CURSOR_RAID = "cursor.raid";
	String CURSOR_INVALID_RAID = "cursor.invalidraid";
	String CURSOR_HIT_AND_RUN = "cursor.hitandrun";
	String CURSOR_INVALID_HIT_AND_RUN = "cursor.invalidhitandrun";

	// Bloodspots
	String BLOODSPOT_KO = "bloodspot.ko";
	String BLOODSPOT_BH = "bloodspot.bh";
	String BLOODSPOT_SI = "bloodspot.si";
	String BLOODSPOT_RIP = "bloodspot.rip";
	String BLOODSPOT_FIREBALL = "bloodspot.fireball";
	String BLOODSPOT_LIGHTNING = "bloodspot.lightning";
	String BLOODSPOT_BOMB = "bloodspot.bomb";

	// Animation
	String ANIMATION_FIREBALL_EXPLOSION_1 = "animation.fireball.explosion.1";
	String ANIMATION_FIREBALL_EXPLOSION_2 = "animation.fireball.explosion.2";
	String ANIMATION_FIREBALL_EXPLOSION_3 = "animation.fireball.explosion.3";
	String ANIMATION_FIREBALL_EXPLOSION_4 = "animation.fireball.explosion.4";
	String ANIMATION_FIREBALL_EXPLOSION_5 = "animation.fireball.explosion.5";
	String ANIMATION_FIREBALL_EXPLOSION_6 = "animation.fireball.explosion.6";
	String ANIMATION_FIREBALL_EXPLOSION_7 = "animation.fireball.explosion.7";
	String ANIMATION_FIREBALL_EXPLOSION_8 = "animation.fireball.explosion.8";
	String ANIMATION_FIREBALL_SMOKE_1 = "animation.fireball.smoke.1";
	String ANIMATION_FIREBALL_SMOKE_2 = "animation.fireball.smoke.2";
	String ANIMATION_FIREBALL_SMOKE_3 = "animation.fireball.smoke.3";
	String ANIMATION_FIREBALL_SMOKE_4 = "animation.fireball.smoke.4";

	String ANIMATION_LIGHTNING_01 = "animation.lightning.01";
	String ANIMATION_LIGHTNING_02 = "animation.lightning.02";
	String ANIMATION_LIGHTNING_03 = "animation.lightning.03";
	String ANIMATION_LIGHTNING_04 = "animation.lightning.04";
	String ANIMATION_LIGHTNING_05 = "animation.lightning.05";
	String ANIMATION_LIGHTNING_06 = "animation.lightning.06";
	String ANIMATION_LIGHTNING_07 = "animation.lightning.07";
	String ANIMATION_LIGHTNING_08 = "animation.lightning.08";
	String ANIMATION_LIGHTNING_09 = "animation.lightning.09";
	String ANIMATION_LIGHTNING_10 = "animation.lightning.10";
	String ANIMATION_LIGHTNING_11 = "animation.lightning.11";
	String ANIMATION_LIGHTNING_12 = "animation.lightning.12";

	String ANIMATION_ZAP_01 = "animation.zap.01";
	String ANIMATION_ZAP_02 = "animation.zap.02";
	String ANIMATION_ZAP_03 = "animation.zap.03";
	String ANIMATION_ZAP_04 = "animation.zap.04";
	String ANIMATION_ZAP_05 = "animation.zap.05";
	String ANIMATION_ZAP_06 = "animation.zap.06";
	String ANIMATION_ZAP_07 = "animation.zap.07";
	String ANIMATION_ZAP_08 = "animation.zap.08";
	String ANIMATION_ZAP_09 = "animation.zap.09";
	String ANIMATION_ZAP_10 = "animation.zap.10";
	String ANIMATION_ZAP_11 = "animation.zap.11";
	String ANIMATION_ZAP_12 = "animation.zap.12";

	String ANIMATION_KICKOFF_BLITZ = "animation.kickoff.blitz";
	String ANIMATION_KICKOFF_BLIZZARD = "animation.kickoff.blizzard";
	String ANIMATION_KICKOFF_BRILLIANT_COACHING = "animation.kickoff.brilliantCoaching";
	String ANIMATION_KICKOFF_CHEERING_FANS = "animation.kickoff.cheeringFans";
	String ANIMATION_KICKOFF_GET_THE_REF = "animation.kickoff.getTheRef";
	String ANIMATION_KICKOFF_HIGH_KICK = "animation.kickoff.highKick";
	String ANIMATION_KICKOFF_NICE = "animation.kickoff.nice";
	String ANIMATION_KICKOFF_PERFECT_DEFENSE = "animation.kickoff.perfectDefense";
	String ANIMATION_KICKOFF_PITCH_INVASION = "animation.kickoff.pitchInvasion";
	String ANIMATION_KICKOFF_POURING_RAIN = "animation.kickoff.pouringRain";
	String ANIMATION_KICKOFF_QUICK_SNAP = "animation.kickoff.quickSnap";
	String ANIMATION_KICKOFF_RIOT = "animation.kickoff.riot";
	String ANIMATION_KICKOFF_TIMEOUT = "animation.kickoff.timeout";
	String ANIMATION_KICKOFF_SOLID_DEFENCE = "animation.kickoff.solidDefence";
	String ANIMATION_KICKOFF_SWELTERING_HEAT = "animation.kickoff.swelteringHeat";
	String ANIMATION_KICKOFF_OFFICIOUS_REF = "animation.kickoff.officiousRef";
	String ANIMATION_KICKOFF_THROW_A_ROCK = "animation.kickoff.throwARock";
	String ANIMATION_KICKOFF_VERY_SUNNY = "animation.kickoff.verySunny";

	String ANIMATION_CARD_DIRTY_TRICK_FRONT = "animation.card.dirtyTrick.front";
	String ANIMATION_CARD_DIRTY_TRICK_BACK = "animation.card.dirtyTrick.back";
	String ANIMATION_CARD_MAGIC_ITEM_FRONT = "animation.card.magicItem.front";
	String ANIMATION_CARD_MAGIC_ITEM_BACK = "animation.card.magicItem.back";

	String ANIMATION_KEG_FUMBLE_00 = "animation.keg.fumble.00";
	String ANIMATION_KEG_FUMBLE_30 = "animation.keg.fumble.30";
	String ANIMATION_KEG_FUMBLE_60 = "animation.keg.fumble.60";
	String ANIMATION_KEG_FUMBLE_90 = "animation.keg.fumble.90";
	String ANIMATION_KEG_FUMBLE_120 = "animation.keg.fumble.120";
	String ANIMATION_KEG_FUMBLE_150 = "animation.keg.fumble.150";


	// Pitches
	String PITCH_INTRO = "pitch.intro";
	String PITCH_URL_DEFAULT = "pitch.url.default";
	String PITCH_URL_BASIC = "pitch.url.basic";

}
