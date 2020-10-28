package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.server.model.ServerSkill;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.action.ttm.StepThrowTeamMate;
import com.balancedbytes.games.ffb.server.step.action.ttm.StepThrowTeamMate.StepState;

/**
* A player with the Pass skill is allowed to re-roll the D6 if he throws an
* inaccurate pass or fumbles.
*/
public class Pass extends ServerSkill {

  public Pass() {
    super("Pass", SkillCategory.PASSING);
    
    registerModifier(new StepModifier<StepThrowTeamMate, StepThrowTeamMate.StepState>() {

      @Override
      public StepCommandStatus handleCommandHook(StepThrowTeamMate step, StepState state, ClientCommandUseSkill useSkillCommand) {
        if (useSkillCommand.isSkillUsed()) {
          step.setReRollSource(ReRollSource.PASS);
        } else {
          step.setReRollSource(null);
        }

        return StepCommandStatus.EXECUTE_STEP;
      }

      @Override
      public boolean handleExecuteStepHook(StepThrowTeamMate step, StepState state) {
        // TODO Auto-generated method stub
        return false;
      }
      
    });
  }

}
