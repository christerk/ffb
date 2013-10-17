package com.balancedbytes.games.ffb.server;

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
  
        JsonStringOption LABEL = new JsonStringOption("label");
  JsonEnumWithNameOption NEXT_ACTION = new JsonEnumWithNameOption("nextAction", new StepActionFactory());
        JsonStringOption NEXT_ACTION_PARAMETER = new JsonStringOption("nextActionParameter");
  JsonEnumWithNameOption STEP_ID = new JsonEnumWithNameOption("stepId", new StepIdFactory());
        JsonObjectOption STEP_RESULT = new JsonObjectOption("stepResult");
         JsonArrayOption STEPS = new JsonArrayOption("steps");
       JsonBooleanOption SYNCHRONIZE = new JsonBooleanOption("synchronize");

}
