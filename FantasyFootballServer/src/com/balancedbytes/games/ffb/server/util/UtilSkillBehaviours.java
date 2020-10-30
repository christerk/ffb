package com.balancedbytes.games.ffb.server.util;

import com.balancedbytes.games.ffb.SkillFactory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.skillbehaviour.AgilityDecreaseBehaviour;
import com.balancedbytes.games.ffb.server.skillbehaviour.AgilityIncreaseBehaviour;
import com.balancedbytes.games.ffb.server.skillbehaviour.ArmourDecreaseBehaviour;
import com.balancedbytes.games.ffb.server.skillbehaviour.ArmourIncreaseBehaviour;
import com.balancedbytes.games.ffb.server.skillbehaviour.GrabBehaviour;
import com.balancedbytes.games.ffb.server.skillbehaviour.MovementDecreaseBehaviour;
import com.balancedbytes.games.ffb.server.skillbehaviour.MovementIncreaseBehaviour;
import com.balancedbytes.games.ffb.server.skillbehaviour.PassBehaviour;
import com.balancedbytes.games.ffb.server.skillbehaviour.SideStepBehaviour;
import com.balancedbytes.games.ffb.server.skillbehaviour.StandFirmBehaviour;
import com.balancedbytes.games.ffb.server.skillbehaviour.StrengthDecreaseBehaviour;
import com.balancedbytes.games.ffb.server.skillbehaviour.StrengthIncreaseBehaviour;

public class UtilSkillBehaviours {
  private static SkillFactory factory;

  public static void RegisterBehaviours(SkillFactory factory) {
    UtilSkillBehaviours.factory = factory;
    
    registerBehaviour(new AgilityDecreaseBehaviour());
    registerBehaviour(new AgilityIncreaseBehaviour());
    registerBehaviour(new ArmourDecreaseBehaviour());
    registerBehaviour(new ArmourIncreaseBehaviour());
    registerBehaviour(new GrabBehaviour());
    registerBehaviour(new MovementDecreaseBehaviour());
    registerBehaviour(new MovementIncreaseBehaviour());
    registerBehaviour(new PassBehaviour());
    registerBehaviour(new SideStepBehaviour());
    registerBehaviour(new StandFirmBehaviour());
    registerBehaviour(new StrengthDecreaseBehaviour());
    registerBehaviour(new StrengthIncreaseBehaviour());
  }

  private static void registerBehaviour(SkillBehaviour<?> behaviour) {
    
    Skill skill = factory.forClass(behaviour.skillClass);
    behaviour.setSkill(skill);
  }
}
