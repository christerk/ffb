package com.balancedbytes.games.ffb.server;

import com.balancedbytes.games.ffb.CatchScatterThrowInModeFactory;
import com.balancedbytes.games.ffb.GameStatusFactory;
import com.balancedbytes.games.ffb.InducementPhaseFactory;
import com.balancedbytes.games.ffb.PlayerActionFactory;
import com.balancedbytes.games.ffb.TurnModeFactory;
import com.balancedbytes.games.ffb.json.*;
import com.balancedbytes.games.ffb.server.step.StepActionFactory;
import com.balancedbytes.games.ffb.server.step.StepIdFactory;
import com.balancedbytes.games.ffb.server.step.action.common.ApothecaryModeFactory;

/**
 * 
 * @author Kalimar
 */
public interface IServerJsonOption extends IJsonOption {
  
               JsonBooleanOption ADMIN_MODE = new JsonBooleanOption("adminMode");
          JsonEnumWithNameOption APOTHECARY_MODE = new JsonEnumWithNameOption("apothecaryMode", new ApothecaryModeFactory());
          JsonEnumWithNameOption APOTHECARY_STATUS = new JsonEnumWithNameOption("apothecaryStatus", new ApothecaryStatusFactory());
               JsonBooleanOption ARGUE_THE_CALL_CHOICE_AWAY = new JsonBooleanOption("argueTheCallChoiceAway");
               JsonBooleanOption ARGUE_THE_CALL_CHOICE_HOME = new JsonBooleanOption("argueTheCallChoiceHome");
                JsonStringOption BLOCK_DEFENDER_ID = new JsonStringOption("blockDefenderId");
               JsonBooleanOption BOMB_MODE = new JsonBooleanOption("bombMode");
               JsonBooleanOption BRIBES_CHOICE = new JsonBooleanOption("bribesChoice");
               JsonBooleanOption BRIBES_CHOICE_AWAY = new JsonBooleanOption("bribesChoiceAway");
               JsonBooleanOption BRIBES_CHOICE_HOME = new JsonBooleanOption("bribesChoiceHome");
               JsonBooleanOption BRIBE_SUCCESSFUL = new JsonBooleanOption("bribeSuccessful");
               JsonBooleanOption CARDS_SELECTED_AWAY = new JsonBooleanOption("cardsSelectedAway");
               JsonBooleanOption CARDS_SELECTED_HOME = new JsonBooleanOption("cardsSelectedHome");
          JsonEnumWithNameOption CATCH_SCATTER_THROW_IN_MODE = new JsonEnumWithNameOption("catchScatterThrowInMode", new CatchScatterThrowInModeFactory());
       JsonFieldCoordinateOption COORDINATE_TO = new JsonFieldCoordinateOption("coordinateTo");
                JsonObjectOption CURRENT_STEP = new JsonObjectOption("currentStep");
       JsonFieldCoordinateOption DEFENDER_POSITION = new JsonFieldCoordinateOption("defenderPosition");
               JsonBooleanOption DEFENDER_PUSHED = new JsonBooleanOption("defenderPushed");
          JsonEnumWithNameOption DISPATCH_PLAYER_ACTION = new JsonEnumWithNameOption("dispatchPlayerAction", new PlayerActionFactory());
               JsonBooleanOption DIVING_CATCH_CHOICE = new JsonBooleanOption("divingCatchChoice");
                   JsonIntOption DODGE_ROLL = new JsonIntOption("dodgeRoll");
               JsonBooleanOption END_PLAYER_ACTION = new JsonBooleanOption("endPlayerAction");
               JsonBooleanOption END_GAME = new JsonBooleanOption("endGame");
               JsonBooleanOption END_INDUCEMENT = new JsonBooleanOption("endInducement");
               JsonBooleanOption END_INDUCEMENT_PHASE = new JsonBooleanOption("endInducementPhase");
               JsonBooleanOption END_KICKOFF = new JsonBooleanOption("endKickoff");
               JsonBooleanOption END_TURN = new JsonBooleanOption("endTurn");
               JsonBooleanOption FEED_ON_PLAYER_CHOICE = new JsonBooleanOption("feedOnPlayerChoice");
               JsonBooleanOption FEEDING_ALLOWED = new JsonBooleanOption("feedingAllowed");
               JsonBooleanOption FOLLOWUP_CHOICE = new JsonBooleanOption("followupChoice");
                JsonStringOption FOUL_DEFENDER_ID = new JsonStringOption("foulDefenderId");
               JsonBooleanOption FOULER_HAS_BALL = new JsonBooleanOption("foulerHasBall");
               JsonBooleanOption FUMBBL_GAME_CREATED = new JsonBooleanOption("fumbblGameCreated");
                JsonObjectOption GAME_LOG = new JsonObjectOption("gameLog");
          JsonEnumWithNameOption GAME_STATUS = new JsonEnumWithNameOption("gameStatus", new GameStatusFactory());
                JsonStringOption GAZE_VICTIM_ID = new JsonStringOption("gazeVictimId");
                   JsonIntOption GOLD_USED_AWAY = new JsonIntOption("goldUsedAway");
                   JsonIntOption GOLD_USED_HOME = new JsonIntOption("goldUsedHome");
                JsonStringOption GOTO_LABEL = new JsonStringOption("gotoLabel");
                JsonStringOption GOTO_LABEL_ON_BLITZ = new JsonStringOption("gotoLabelOnBlitz");
                JsonStringOption GOTO_LABEL_ON_DODGE = new JsonStringOption("gotoLabelOnDodge");
                JsonStringOption GOTO_LABEL_ON_END = new JsonStringOption("gotoLabelOnEnd");
                JsonStringOption GOTO_LABEL_ON_FAILURE = new JsonStringOption("gotoLabelOnFailure");
                JsonStringOption GOTO_LABEL_ON_FALL_DOWN = new JsonStringOption("gotoLabelOnFallDown");
                JsonStringOption GOTO_LABEL_ON_HAIL_MARY_PASS = new JsonStringOption("gotoLabelOnHailMaryPass");
                JsonStringOption GOTO_LABEL_ON_HAND_OVER = new JsonStringOption("gotoLabelOnHandOver");
                JsonStringOption GOTO_LABEL_ON_JUGGERNAUT = new JsonStringOption("gotoLabelOnJuggernaut");
                JsonStringOption GOTO_LABEL_ON_MISSED_PASS = new JsonStringOption("gotoLabelOnMissedPass");
                JsonStringOption GOTO_LABEL_ON_PUSHBACK = new JsonStringOption("gotoLabelOnPushback");
                JsonStringOption GOTO_LABEL_ON_SUCCESS = new JsonStringOption("gotoLabelOnSuccess");
               JsonBooleanOption HANDLE_KICKING_TEAM = new JsonBooleanOption("handleKickingTeam");
               JsonBooleanOption HOLDING_SAFE_THROW = new JsonBooleanOption("holdingSafeThrow");
               JsonBooleanOption ILLEGAL_SUBSTITUTION = new JsonBooleanOption("illegalSubstitution");
                   JsonIntOption INDUCEMENT_GOLD_AWAY = new JsonIntOption("inducementGoldAway");
                   JsonIntOption INDUCEMENT_GOLD_HOME = new JsonIntOption("inducementGoldHome");
          JsonEnumWithNameOption INDUCEMENT_PHASE = new JsonEnumWithNameOption("inducementPhase", new InducementPhaseFactory());
               JsonBooleanOption INDUCEMENTS_SELECTED_AWAY = new JsonBooleanOption("inducementsSelectedAway");
               JsonBooleanOption INDUCEMENTS_SELECTED_HOME = new JsonBooleanOption("inducementsSelectedHome");
                JsonObjectOption INJURY_RESULT = new JsonObjectOption("injuryResult");
                JsonObjectOption INJURY_RESULT_DEFENDER = new JsonObjectOption("injuryResultDefender");
               JsonBooleanOption INTERCEPTOR_CHOSEN = new JsonBooleanOption("interceptorChosen");
           JsonPlayerStateOption KICKED_PLAYER_STATE = new JsonPlayerStateOption("kickedPlayerState");
               JsonBooleanOption KICKED_PLAYER_HAS_BALL = new JsonBooleanOption("kickedPlayerHasBall");
       JsonFieldCoordinateOption KICKING_PLAYER_COORDINATE = new JsonFieldCoordinateOption("kickingPlayerCoordinate");
       JsonFieldCoordinateOption KICKED_PLAYER_COORDINATE = new JsonFieldCoordinateOption("kickedPlayerCoordinate");
                JsonObjectOption KICKOFF_BOUNDS = new JsonObjectOption("kickoffBounds");
       JsonFieldCoordinateOption KICKOFF_START_COORDINATE = new JsonFieldCoordinateOption("kickoffStartCoordinate");
                JsonStringOption LABEL = new JsonStringOption("label");
  JsonFieldCoordinateArrayOption MOVE_STACK = new JsonFieldCoordinateArrayOption("moveStack");
                   JsonIntOption MOVE_STACK_SIZE = new JsonIntOption("moveStackSize");
                JsonStringOption MULTI_BLOCK_DEFENDER_ID = new JsonStringOption("multiBlockDefenderId");
               JsonBooleanOption NEW_HALF = new JsonBooleanOption("newHalf");
          JsonEnumWithNameOption NEXT_ACTION = new JsonEnumWithNameOption("nextAction", new StepActionFactory());
                JsonStringOption NEXT_ACTION_PARAMETER = new JsonStringOption("nextActionParameter");
               JsonBooleanOption NEXT_SEQUENCE_PUSHED = new JsonBooleanOption("nextSequencePushed");
           JsonPlayerStateOption OLD_DEFENDER_STATE = new JsonPlayerStateOption("oldDefenderState");
              JsonIntArrayOption OLD_PLAYER_STATES = new JsonIntArrayOption("oldPlayerStates");
          JsonEnumWithNameOption OLD_TURN_MODE = new JsonEnumWithNameOption("oldTurnMode", new TurnModeFactory());
               JsonBooleanOption PASS_ACCURATE = new JsonBooleanOption("passAccurate");
               JsonBooleanOption PASS_FUMBLE = new JsonBooleanOption("passFumble");
               JsonBooleanOption PASS_SKILL_USED = new JsonBooleanOption("passSkillUsed");
               JsonBooleanOption PETTY_CASH_SELECTED_AWAY = new JsonBooleanOption("pettyCashSelectedAway");
               JsonBooleanOption PETTY_CASH_SELECTED_HOME = new JsonBooleanOption("pettyCashSelectedHome");
               JsonBooleanOption REMOVE_USED_SECRET_WEAPONS = new JsonBooleanOption("removeUsedSecretWeapons");
               JsonBooleanOption REPLAY = new JsonBooleanOption("replay");
               JsonBooleanOption REPORTED_AWAY = new JsonBooleanOption("reportedAway");
               JsonBooleanOption REPORTED_HOME = new JsonBooleanOption("reportedHome");
               JsonBooleanOption ROLL_FOR_EFFECT = new JsonBooleanOption("rollForEffect");
                JsonObjectOption SCATTER_BOUNDS = new JsonObjectOption("scatterBounds");
                   JsonIntOption SCATTER_DISTANCE = new JsonIntOption("scatterDistance");
               JsonBooleanOption SECOND_GO_FOR_IT = new JsonBooleanOption("secondGoForIt");
       JsonFieldCoordinateOption SETUP_PLAYER_COORDINATE = new JsonFieldCoordinateOption("setupPlayerCoordinate");
                JsonStringOption SETUP_PLAYER_ID = new JsonStringOption("setupPlayerId");
               JsonBooleanOption SHOW_REPORT = new JsonBooleanOption("showReport");
               JsonBooleanOption STARTED_AWAY = new JsonBooleanOption("startedAway");
               JsonBooleanOption STARTED_HOME = new JsonBooleanOption("startedHome");
                JsonObjectOption STARTING_PUSHBACK_SQUARE = new JsonObjectOption("startingPushbackSquare");
          JsonEnumWithNameOption STEP_ID = new JsonEnumWithNameOption("stepId", new StepIdFactory());
                JsonObjectOption STEP_RESULT = new JsonObjectOption("stepResult");
                JsonObjectOption STEP_STACK = new JsonObjectOption("stepStack");
                 JsonArrayOption STEPS = new JsonArrayOption("steps");
               JsonBooleanOption SYNCHRONIZE = new JsonBooleanOption("synchronize");
       JsonFieldCoordinateOption THROW_IN_COORDINATE = new JsonFieldCoordinateOption("throwInCoordinate");
               JsonBooleanOption THROW_SCATTER = new JsonBooleanOption("throwScatter");
       JsonFieldCoordinateOption THROWN_PLAYER_COORDINATE = new JsonFieldCoordinateOption("thrownPlayerCoordinate");
               JsonBooleanOption THROWN_PLAYER_HAS_BALL = new JsonBooleanOption("thrownPlayerHasBall");
                JsonStringOption THROWN_PLAYER_ID = new JsonStringOption("thrownPlayerId");
           JsonPlayerStateOption THROWN_PLAYER_STATE = new JsonPlayerStateOption("thrownPlayerState");
               JsonBooleanOption TOUCHBACK = new JsonBooleanOption("touchback");
       JsonFieldCoordinateOption TOUCHBACK_COORDINATE = new JsonFieldCoordinateOption("touchbackCoordinate");
               JsonBooleanOption TOUCHDOWN = new JsonBooleanOption("touchdown");
               JsonBooleanOption USE_KICK_CHOICE = new JsonBooleanOption("useKickChoice");
               JsonBooleanOption USING_BREAK_TACKLE = new JsonBooleanOption("usingBreakTackle");
               JsonBooleanOption USING_DIVING_TACKLE = new JsonBooleanOption("usingDivingTackle");
               JsonBooleanOption USING_DODGE = new JsonBooleanOption("usingDodge");
               JsonBooleanOption USING_DUMP_OFF = new JsonBooleanOption("usingDumpOff");
               JsonBooleanOption USING_FEND = new JsonBooleanOption("usingFend");
               JsonBooleanOption USING_GRAB = new JsonBooleanOption("usingGrab");
               JsonBooleanOption USING_HORNS = new JsonBooleanOption("usingHorns");
               JsonBooleanOption USING_JUGGERNAUT = new JsonBooleanOption("usingJuggernaut");
               JsonBooleanOption USING_PILING_ON = new JsonBooleanOption("usingPilingOn");
               JsonBooleanOption USING_SHADOWING = new JsonBooleanOption("usingShadowing");
            JsonBooleanMapOption USING_SIDE_STEP = new JsonBooleanMapOption("usingSideStep");
            JsonBooleanMapOption USING_STAND_FIRM = new JsonBooleanMapOption("usingStandFirm");
               JsonBooleanOption USING_TENTACLES = new JsonBooleanOption("usingTentacles");
               JsonBooleanOption USING_WRESTLE_ATTACKER = new JsonBooleanOption("usingWrestleAttacker");
               JsonBooleanOption USING_WRESTLE_DEFENDER = new JsonBooleanOption("usingWrestleDefender");
               JsonBooleanOption WITH_GAMES_INFO = new JsonBooleanOption("withGamesInfo"); 
               JsonBooleanOption WITHIN_SECRET_WEAPON_HANDLING = new JsonBooleanOption("withinSecretWeaponHandling");
               
}
