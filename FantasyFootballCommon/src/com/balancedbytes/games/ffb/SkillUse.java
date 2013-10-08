package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * LRB5 Skill Categories
 * 
 * @author Kalimar
 */
public enum SkillUse {
  
  WOULD_NOT_HELP(1, "wouldNotHelp", "because it would not help"),
  NO_TEAM_MATE_IN_RANGE(2, "noTeamMateInRange", "because no team-mate is in range"),
  STOP_OPPONENT(3, "stopOpponent", "to stop %g opponent"),
  PUSH_BACK_OPPONENT(4, "pushBackOpponent", "to push %g opponent back"),
  BRING_DOWN_OPPONENT(5, "bringDownOppponent", "to bring %g opponent down"),
  AVOID_PUSH(6, "avoidPush", "to avoid being pushed"),
  CANCEL_FEND(7, "cancelFend", "to cancel %g opponent's Fend skill"),
  CANCEL_STAND_FIRM(8, "cancelStandFirm", "to cancel %g opponent's Stand Firm skill"),
  STAY_AWAY_FROM_OPPONENT(9, "stayAwayFromOpponent", "to stay away from %g opponent"),
  CATCH_BALL(10, "catchBall", "to catch the ball"),
  STEAL_BALL(11, "stealBall", "to steal the ball"),
  CANCEL_STRIP_BALL(12, "cancelStripBall", "to cancel %g opponent's Strip Ball skill"),
  HALVE_KICKOFF_SCATTER(13, "halveKickoffScatter", "to halve the kickoff scatter"),
  CANCEL_DODGE(14, "cancelDodge", "to cancel %g opponent's Dodge skill"),
  AVOID_FALLING(15, "avoidFalling", "to avoid falling"),
  CANCEL_TACKLE(16, "cancelTackle", "to cancel %g opponent's Tackle skill"),
  INCREASE_STRENGTH_BY_1(17, "increaseStrengthBy1", "to increase %g strength by 1"),
  CANCEL_DIVING_CATCH(18, "cancelDivingCatch", "because players from both teams hinder each other");

  private int fId;
  private String fName;
  private String fDescription;
  
  private static final String _PARAMETER_GENITIVE = "%g";
  
  private SkillUse(int pId, String pName, String pDescription) {
    fId = pId;
    fName = pName;
    fDescription = pDescription;
  }
  
  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public String getDescription(Player pPlayer) {
    if (pPlayer != null) {
      return fDescription.replaceAll(_PARAMETER_GENITIVE, pPlayer.getGender().getGenitive());
    } else {
      return fDescription;
    }
  }
  
  public static SkillUse fromId(int pId) {
    if (pId > 0) {
      for (SkillUse skillCategory : values()) {
        if (pId == skillCategory.getId()) {
          return skillCategory;
        }
      }
    }
    return null;
  }
  
  public static SkillUse fromName(String pName) {
    if (StringTool.isProvided(pName)) {
      for (SkillUse skillCategory : values()) {
        if (pName.equalsIgnoreCase(skillCategory.getName())) {
          return skillCategory;
        }
      }
    }
    return null;
  }
  
}
