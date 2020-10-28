package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.client.SkillConstants;
import com.balancedbytes.games.ffb.model.Skill;

/**
 * 
 * @author Kalimar
 */
public enum ReRolledAction implements INamedObject {
  
  GO_FOR_IT("Go For It"),
  DODGE("Dodge"),
  CATCH("Catch"),
  PICK_UP("Pick Up"),
  PASS(SkillConstants.PASS),
  DAUNTLESS(SkillConstants.DAUNTLESS),
  LEAP(SkillConstants.LEAP),
  FOUL_APPEARANCE(SkillConstants.FOUL_APPEARANCE),
  BLOCK("Block"),
  REALLY_STUPID(SkillConstants.REALLY_STUPID),
  BONE_HEAD(SkillConstants.BONE_HEAD),
  WILD_ANIMAL(SkillConstants.WILD_ANIMAL),
  TAKE_ROOT(SkillConstants.TAKE_ROOT),
  WINNINGS("Winnings"),
  ALWAYS_HUNGRY(SkillConstants.ALWAYS_HUNGRY),
  THROW_TEAM_MATE(SkillConstants.THROW_TEAM_MATE),
  KICK_TEAM_MATE(SkillConstants.KICK_TEAM_MATE),
  RIGHT_STUFF(SkillConstants.RIGHT_STUFF),
  SHADOWING_ESCAPE("Shadowing Escape"),
  TENTACLES_ESCAPE("Tentacles Escape"),
  ESCAPE("Escape"),
  SAFE_THROW(SkillConstants.SAFE_THROW),
  INTERCEPTION("Interception"),
  JUMP_UP(SkillConstants.JUMP_UP),
  STAND_UP("standUp"),
  CHAINSAW(SkillConstants.CHAINSAW),
  BLOOD_LUST(SkillConstants.BLOOD_LUST),
  HYPNOTIC_GAZE(SkillConstants.HYPNOTIC_GAZE),
  ANIMOSITY(SkillConstants.ANIMOSITY);

  private String fName;
  private Skill fSkill;
  
  private ReRolledAction(String pName) {
    fName = pName;
    fSkill = null;
  }
  
  private ReRolledAction(Skill pSkill) {
    fSkill = pSkill;
    fName = pSkill.getName();
  }
  
  public String getName() {
    return fName;
  }
  
  public Skill getSkill() {
    return fSkill;
  }

}
