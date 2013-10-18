package com.balancedbytes.games.ffb.server;

import com.balancedbytes.games.ffb.GameStatusFactory;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.JsonArrayOption;
import com.balancedbytes.games.ffb.json.JsonBooleanOption;
import com.balancedbytes.games.ffb.json.JsonEnumWithNameOption;
import com.balancedbytes.games.ffb.json.JsonObjectOption;
import com.balancedbytes.games.ffb.json.JsonStringOption;
import com.balancedbytes.games.ffb.server.step.StepActionFactory;
import com.balancedbytes.games.ffb.server.step.StepIdFactory;

/**
 * 
 * @author Kalimar
 */
public interface IServerJsonOption extends IJsonOption {
  
        JsonObjectOption CURRENT_STEP = new JsonObjectOption("currentStep");
        JsonObjectOption GAME_LOG = new JsonObjectOption("gameLog");
  JsonEnumWithNameOption GAME_STATUS = new JsonEnumWithNameOption("gameStatus", new GameStatusFactory());
        JsonStringOption GOTO_LABEL = new JsonStringOption("gotoLabel");
        JsonStringOption LABEL = new JsonStringOption("label");
  JsonEnumWithNameOption NEXT_ACTION = new JsonEnumWithNameOption("nextAction", new StepActionFactory());
        JsonStringOption NEXT_ACTION_PARAMETER = new JsonStringOption("nextActionParameter");
  JsonEnumWithNameOption STEP_ID = new JsonEnumWithNameOption("stepId", new StepIdFactory());
        JsonObjectOption STEP_RESULT = new JsonObjectOption("stepResult");
        JsonObjectOption STEP_STACK = new JsonObjectOption("stepStack");
         JsonArrayOption STEPS = new JsonArrayOption("steps");
       JsonBooleanOption SYNCHRONIZE = new JsonBooleanOption("synchronize");

}
