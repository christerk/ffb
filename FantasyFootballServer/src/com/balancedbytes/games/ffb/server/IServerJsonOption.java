package com.balancedbytes.games.ffb.server;

import com.balancedbytes.games.ffb.CatchScatterThrowInModeFactory;
import com.balancedbytes.games.ffb.GameStatusFactory;
import com.balancedbytes.games.ffb.TurnModeFactory;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.JsonArrayOption;
import com.balancedbytes.games.ffb.json.JsonBooleanOption;
import com.balancedbytes.games.ffb.json.JsonEnumWithNameOption;
import com.balancedbytes.games.ffb.json.JsonFieldCoordinateOption;
import com.balancedbytes.games.ffb.json.JsonIntOption;
import com.balancedbytes.games.ffb.json.JsonObjectOption;
import com.balancedbytes.games.ffb.json.JsonPlayerStateOption;
import com.balancedbytes.games.ffb.json.JsonStringOption;
import com.balancedbytes.games.ffb.server.step.StepActionFactory;
import com.balancedbytes.games.ffb.server.step.StepIdFactory;
import com.balancedbytes.games.ffb.server.step.action.common.ApothecaryModeFactory;

/**
 * 
 * @author Kalimar
 */
public interface IServerJsonOption extends IJsonOption {
  
     JsonEnumWithNameOption APOTHECARY_MODE = new JsonEnumWithNameOption("apothecaryMode", new ApothecaryModeFactory());
     JsonEnumWithNameOption APOTHECARY_STATUS = new JsonEnumWithNameOption("apothecaryStatus", new ApothecaryStatusFactory());
          JsonBooleanOption AUTOMATIC_RE_ROLL = new JsonBooleanOption("automaticReRoll");
           JsonStringOption BLOCK_DEFENDER_ID = new JsonStringOption("blockDefenderId");
          JsonBooleanOption BOMB_MODE = new JsonBooleanOption("bombMode");
          JsonBooleanOption BRIBES_CHOICE = new JsonBooleanOption("bribesChoice");
          JsonBooleanOption BRIBES_CHOICE_AWAY = new JsonBooleanOption("bribesChoiceAway");
          JsonBooleanOption BRIBES_CHOICE_HOME = new JsonBooleanOption("bribesChoiceHome");
          JsonBooleanOption BRIBE_SUCCESSFUL = new JsonBooleanOption("bribeSuccessful");
     JsonEnumWithNameOption CATCH_SCATTER_THROW_IN_MODE = new JsonEnumWithNameOption("catchScatterThrowInMode", new CatchScatterThrowInModeFactory());
           JsonObjectOption CURRENT_STEP = new JsonObjectOption("currentStep");
  JsonFieldCoordinateOption DEFENDER_POSITION = new JsonFieldCoordinateOption("defenderPosition");
          JsonBooleanOption DEFENDER_PUSHED = new JsonBooleanOption("defenderPushed");
          JsonBooleanOption DIVING_CATCH_CHOICE = new JsonBooleanOption("divingCatchChoice");
          JsonBooleanOption END_PLAYER_ACTION = new JsonBooleanOption("endPlayerAction");
          JsonBooleanOption END_GAME = new JsonBooleanOption("endGame");
          JsonBooleanOption END_KICKOFF = new JsonBooleanOption("endKickoff");
          JsonBooleanOption END_TURN = new JsonBooleanOption("endTurn");
          JsonBooleanOption FEED_ON_PLAYER_CHOICE = new JsonBooleanOption("feedOnPlayerChoice");
          JsonBooleanOption FEEDING_ALLOWED = new JsonBooleanOption("feedingAllowed");
          JsonBooleanOption FOLLOWUP_CHOICE = new JsonBooleanOption("followupChoice");
           JsonStringOption FOUL_DEFENDER_ID = new JsonStringOption("foulDefenderId");
          JsonBooleanOption FOULER_HAS_BALL = new JsonBooleanOption("foulerHasBall");
           JsonObjectOption GAME_LOG = new JsonObjectOption("gameLog");
     JsonEnumWithNameOption GAME_STATUS = new JsonEnumWithNameOption("gameStatus", new GameStatusFactory());
           JsonStringOption GOTO_LABEL = new JsonStringOption("gotoLabel");
           JsonStringOption GOTO_LABEL_ON_BLITZ = new JsonStringOption("gotoLabelOnBlitz");
           JsonStringOption GOTO_LABEL_ON_DODGE = new JsonStringOption("gotoLabelOnDodge");
           JsonStringOption GOTO_LABEL_ON_END = new JsonStringOption("gotoLabelOnEnd");
           JsonStringOption GOTO_LABEL_ON_FAILURE = new JsonStringOption("gotoLabelOnFailure");
           JsonStringOption GOTO_LABEL_ON_JUGGERNAUT = new JsonStringOption("gotoLabelOnJuggernaut");
           JsonStringOption GOTO_LABEL_ON_PUSHBACK = new JsonStringOption("gotoLabelOnPushback");
           JsonStringOption GOTO_LABEL_ON_SUCCESS = new JsonStringOption("gotoLabelOnSuccess");
          JsonBooleanOption HANDLE_SECRET_WEAPONS = new JsonBooleanOption("handleSecretWeapons");
           JsonObjectOption INJURY_RESULT = new JsonObjectOption("injuryResult");
           JsonObjectOption INJURY_RESULT_DEFENDER = new JsonObjectOption("injuryResultDefender");
  JsonFieldCoordinateOption KICKING_PLAYER_COORDINATE = new JsonFieldCoordinateOption("kickingPlayerCoordinate");
           JsonObjectOption KICKOFF_BOUNDS = new JsonObjectOption("kickoffBounds");
  JsonFieldCoordinateOption KICKOFF_START_COORDINATE = new JsonFieldCoordinateOption("kickoffStartCoordinate");
           JsonStringOption LABEL = new JsonStringOption("label");
           JsonStringOption MULTI_BLOCK_DEFENDER_ID = new JsonStringOption("multiBlockDefenderId");
          JsonBooleanOption NEW_HALF = new JsonBooleanOption("newHalf");
     JsonEnumWithNameOption NEXT_ACTION = new JsonEnumWithNameOption("nextAction", new StepActionFactory());
           JsonStringOption NEXT_ACTION_PARAMETER = new JsonStringOption("nextActionParameter");
          JsonBooleanOption NEXT_SEQUENCE_PUSHED = new JsonBooleanOption("nextSequencePushed");
      JsonPlayerStateOption OLD_DEFENDER_STATE = new JsonPlayerStateOption("oldDefenderState");
     JsonEnumWithNameOption OLD_TURN_MODE = new JsonEnumWithNameOption("oldTurnMode", new TurnModeFactory());
          JsonBooleanOption PASS_FUMBLE = new JsonBooleanOption("passFumble");
          JsonBooleanOption REMOVE_USED_SECRET_WEAPONS = new JsonBooleanOption("removeUsedSecretWeapons");
          JsonBooleanOption ROLL_FOR_EFFECT = new JsonBooleanOption("rollForEffect");
           JsonObjectOption SCATTER_BOUNDS = new JsonObjectOption("scatterBounds");
              JsonIntOption SCATTER_DISTANCE = new JsonIntOption("scatterDistance");
          JsonBooleanOption SECOND_GO_FOR_IT = new JsonBooleanOption("secondGoForIt");
          JsonBooleanOption SHOW_REPORT = new JsonBooleanOption("showReport");
           JsonObjectOption STARTING_PUSHBACK_SQUARE = new JsonObjectOption("startingPushbackSquare");
     JsonEnumWithNameOption STEP_ID = new JsonEnumWithNameOption("stepId", new StepIdFactory());
           JsonObjectOption STEP_RESULT = new JsonObjectOption("stepResult");
           JsonObjectOption STEP_STACK = new JsonObjectOption("stepStack");
            JsonArrayOption STEPS = new JsonArrayOption("steps");
          JsonBooleanOption SYNCHRONIZE = new JsonBooleanOption("synchronize");
  JsonFieldCoordinateOption THROW_IN_COORDINATE = new JsonFieldCoordinateOption("throwInCoordinate");
          JsonBooleanOption TOUCHBACK = new JsonBooleanOption("touchback");
  JsonFieldCoordinateOption TOUCHBACK_COORDINATE = new JsonFieldCoordinateOption("touchbackCoordinate");
          JsonBooleanOption TOUCHDOWN = new JsonBooleanOption("touchdown");
          JsonBooleanOption USE_KICK_CHOICE = new JsonBooleanOption("useKickChoice");
          JsonBooleanOption USING_DIVING_TACKLE = new JsonBooleanOption("usingDivingTackle");
          JsonBooleanOption USING_DODGE = new JsonBooleanOption("usingDodge");
          JsonBooleanOption USING_DUMP_OFF = new JsonBooleanOption("usingDumpOff");
          JsonBooleanOption USING_FEND = new JsonBooleanOption("usingFend");
          JsonBooleanOption USING_GRAB = new JsonBooleanOption("usingGrab");
          JsonBooleanOption USING_HORNS = new JsonBooleanOption("usingHorns");
          JsonBooleanOption USING_JUGGERNAUT = new JsonBooleanOption("usingJuggernaut");
          JsonBooleanOption USING_PILING_ON = new JsonBooleanOption("usingPilingOn");
          JsonBooleanOption USING_SHADOWING = new JsonBooleanOption("usingShadowing");
          JsonBooleanOption USING_SIDE_STEP = new JsonBooleanOption("usingSideStep");
          JsonBooleanOption USING_STAND_FIRM = new JsonBooleanOption("usingStandFirm");
          JsonBooleanOption USING_WRESTLE_ATTACKER = new JsonBooleanOption("usingWrestleAttacker");
          JsonBooleanOption USING_WRESTLE_DEFENDER = new JsonBooleanOption("usingWrestleDefender");

}
