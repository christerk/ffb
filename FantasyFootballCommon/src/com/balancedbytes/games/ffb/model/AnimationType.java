package com.balancedbytes.games.ffb.model;

import com.balancedbytes.games.ffb.IEnumWithId;
import com.balancedbytes.games.ffb.IEnumWithName;




/**
 * 
 * @author Kalimar
 */
public enum AnimationType implements IEnumWithId, IEnumWithName {
  
  PASS(1, "pass"),
  THROW_TEAM_MATE(2, "throwTeamMate"),
  KICK(3, "kick"),
  SPELL_FIREBALL(4, "spellFireball"),
  SPELL_LIGHTNING(5, "spellLightning"),
  KICKOFF_BLITZ(6, "kickoffBlitz"),
	KICKOFF_BLIZZARD(7, "kickoffBlizzard"),
	KICKOFF_BRILLIANT_COACHING(8, "kickoffBrilliantCoaching"),
	KICKOFF_CHEERING_FANS(9, "kickoffCheeringFans"),
	KICKOFF_GET_THE_REF(10, "kickoffGetTheRef"),
	KICKOFF_HIGH_KICK(11, "kickoffHighKick"),
	KICKOFF_NICE(12, "kickoffNice"),
	KICKOFF_PERFECT_DEFENSE(13, "kickoffPerfectDefense"),
	KICKOFF_PITCH_INVASION(14, "kickoffPitchInvasion"),
	KICKOFF_POURING_RAIN(15, "kickoffPouringRain"),
	KICKOFF_QUICK_SNAP(16, "kickoffQuickSnap"),
	KICKOFF_RIOT(17, "kickoffRiot"),
	KICKOFF_SWELTERING_HEAT(18, "kickoffSwelteringHeat"),
	KICKOFF_THROW_A_ROCK(19, "kickoffThrowARock"),
	KICKOFF_VERY_SUNNY(20, "kickoffVerySunny"),
	HAIL_MARY_PASS(21, "hailMaryPass"),
	THROW_A_ROCK(22, "throwARock"),
	THROW_BOMB(23, "throwBomb"),
	HAIL_MARY_BOMB(24, "hailMaryBomb"),
	BOMB_EXLOSION(25, "bombExplosion"),
	CARD(26, "card");
  
  private int fId;
  private String fName;
  
  private AnimationType(int pValue, String pName) {
    fId = pValue;
    fName = pName;
  }

  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
    
}
