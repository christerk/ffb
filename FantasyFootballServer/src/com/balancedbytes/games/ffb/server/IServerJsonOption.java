package com.balancedbytes.games.ffb.server;

import com.balancedbytes.games.ffb.GameStatusFactory;
import com.balancedbytes.games.ffb.TurnModeFactory;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.JsonArrayOption;
import com.balancedbytes.games.ffb.json.JsonBooleanOption;
import com.balancedbytes.games.ffb.json.JsonEnumWithNameOption;
import com.balancedbytes.games.ffb.json.JsonFieldCoordinateOption;
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
           JsonObjectOption CURRENT_STEP = new JsonObjectOption("currentStep");
  JsonFieldCoordinateOption DEFENDER_POSITION = new JsonFieldCoordinateOption("defenderPosition");
          JsonBooleanOption END_PLAYER_ACTION = new JsonBooleanOption("endPlayerAction");
          JsonBooleanOption END_TURN = new JsonBooleanOption("endTurn");
           JsonObjectOption GAME_LOG = new JsonObjectOption("gameLog");
     JsonEnumWithNameOption GAME_STATUS = new JsonEnumWithNameOption("gameStatus", new GameStatusFactory());
           JsonStringOption GOTO_LABEL = new JsonStringOption("gotoLabel");
           JsonStringOption GOTO_LABEL_ON_DODGE = new JsonStringOption("gotoLabelOnDodge");
           JsonStringOption GOTO_LABEL_ON_END = new JsonStringOption("gotoLabelOnEnd");
           JsonStringOption GOTO_LABEL_ON_FAILURE = new JsonStringOption("gotoLabelOnFailure");
           JsonStringOption GOTO_LABEL_ON_JUGGERNAUT = new JsonStringOption("gotoLabelOnJuggernaut");
           JsonStringOption GOTO_LABEL_ON_PUSHBACK = new JsonStringOption("gotoLabelOnPushback");
           JsonStringOption GOTO_LABEL_ON_SUCCESS = new JsonStringOption("gotoLabelOnSuccess");
           JsonStringOption LABEL = new JsonStringOption("label");
     JsonEnumWithNameOption NEXT_ACTION = new JsonEnumWithNameOption("nextAction", new StepActionFactory());
           JsonStringOption NEXT_ACTION_PARAMETER = new JsonStringOption("nextActionParameter");
      JsonPlayerStateOption OLD_DEFENDER_STATE = new JsonPlayerStateOption("oldDefenderState");
     JsonEnumWithNameOption OLD_TURN_MODE = new JsonEnumWithNameOption("oldTurnMode", new TurnModeFactory());
          JsonBooleanOption SECOND_GO_FOR_IT = new JsonBooleanOption("secondGoForIt");
     JsonEnumWithNameOption STEP_ID = new JsonEnumWithNameOption("stepId", new StepIdFactory());
           JsonObjectOption STEP_RESULT = new JsonObjectOption("stepResult");
           JsonObjectOption STEP_STACK = new JsonObjectOption("stepStack");
            JsonArrayOption STEPS = new JsonArrayOption("steps");
          JsonBooleanOption SYNCHRONIZE = new JsonBooleanOption("synchronize");
          JsonBooleanOption TOUCHBACK = new JsonBooleanOption("touchback");
  JsonFieldCoordinateOption TOUCHBACK_COORDINATE = new JsonFieldCoordinateOption("touchbackCoordinate");
          JsonBooleanOption USING_DODGE = new JsonBooleanOption("usingDodge");
          JsonBooleanOption USING_DUMP_OFF = new JsonBooleanOption("usingDumpOff");
          JsonBooleanOption USING_HORNS = new JsonBooleanOption("usingHorns");
          JsonBooleanOption USING_JUGGERNAUT = new JsonBooleanOption("usingJuggernaut");
          JsonBooleanOption USING_WRESTLE_ATTACKER = new JsonBooleanOption("usingWrestleAttacker");
          JsonBooleanOption USING_WRESTLE_DEFENDER = new JsonBooleanOption("usingWrestleDefender");

}
