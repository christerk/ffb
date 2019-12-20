package com.balancedbytes.games.ffb.server;

import java.util.Set;

import com.balancedbytes.games.ffb.ArmorModifier;
import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardEffect;
import com.balancedbytes.games.ffb.CatchModifier;
import com.balancedbytes.games.ffb.Direction;
import com.balancedbytes.games.ffb.DirectionFactory;
import com.balancedbytes.games.ffb.DodgeModifier;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.GazeModifier;
import com.balancedbytes.games.ffb.GoForItModifier;
import com.balancedbytes.games.ffb.InjuryModifier;
import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.InterceptionModifier;
import com.balancedbytes.games.ffb.KickoffResult;
import com.balancedbytes.games.ffb.KickoffResultFactory;
import com.balancedbytes.games.ffb.LeapModifier;
import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.PickupModifier;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.RightStuffModifier;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilRangeRuler;

/**
 * 
 * @author Kalimar
 */
public class DiceInterpreter {

  private static DiceInterpreter _INSTANCE = new DiceInterpreter();

  /**
   * @return the only instance of this class.
   */
  public static DiceInterpreter getInstance() {
    return _INSTANCE;
  }

  private DiceInterpreter() {
    super();
  }

  public KickoffResult interpretRollKickoff(int[] roll) {
    int kickoffRoll = roll[0] + roll[1];
    return new KickoffResultFactory().forRoll(kickoffRoll);
  }

  public Weather interpretRollWeather(int[] roll) {
    int total = roll[0] + roll[1];
    switch (total) {
      case 2:
        return Weather.SWELTERING_HEAT;
      case 3:
        return Weather.VERY_SUNNY;
      case 11:
        return Weather.POURING_RAIN;
      case 12:
        return Weather.BLIZZARD;
      default: // 4 - 10
        return Weather.NICE;
    }
  }

  private int getAgilityRollBase(int agility) {
    return 7 - Math.min(agility, 6);
  }

  public int minimumRollJumpUp(Player pPlayer) {
    return Math.max(2, getAgilityRollBase(pPlayer.getAgility()) - 2);
  }

  public int minimumRollGoingForIt(Set<GoForItModifier> pGoForItModifiers) {
    int modifierTotal = 0;
    for (GoForItModifier goForItModifier : pGoForItModifiers) {
      modifierTotal += goForItModifier.getModifier();
    }
    return Math.max(2, 2 + modifierTotal);
  }

  public int minimumRollDodge(Game pGame, Player pPlayer, Set<DodgeModifier> pDodgeModifiers) {
    int modifierTotal = 0;
    for (DodgeModifier dodgeModifier : pDodgeModifiers) {
      modifierTotal += dodgeModifier.getModifier();
    }
    int statistic = pDodgeModifiers.contains(DodgeModifier.BREAK_TACKLE) ? UtilCards.getPlayerStrength(pGame, pPlayer) : pPlayer.getAgility();
    return Math.max(2, getAgilityRollBase(statistic) - 1 + modifierTotal);
  }

  public int minimumRollPickup(Player pPlayer, Set<PickupModifier> pPickupModifiers) {
    int modifierTotal = 0;
    for (PickupModifier pickupModifier : pPickupModifiers) {
      modifierTotal += pickupModifier.getModifier();
    }
    return Math.max(2, getAgilityRollBase(pPlayer.getAgility()) - 1 + modifierTotal);
  }

  public int minimumRollInterception(Player pPlayer, Set<InterceptionModifier> pInterceptionModifiers) {
    int modifierTotal = 0;
    for (InterceptionModifier interceptionModifier : pInterceptionModifiers) {
      modifierTotal += interceptionModifier.getModifier();
    }
    return Math.max(2, getAgilityRollBase(pPlayer.getAgility()) + 2 + modifierTotal);
  }

  public int minimumRollLeap(Player pPlayer, Set<LeapModifier> pLeapModifiers) {
    int modifierTotal = 0;
    for (LeapModifier leapModifier : pLeapModifiers) {
      modifierTotal += leapModifier.getModifier();
    }
    return Math.max(2, getAgilityRollBase(pPlayer.getAgility()) + modifierTotal);
  }

  public int minimumRollHypnoticGaze(Player pPlayer, Set<GazeModifier> pGazeModifiers) {
    int modifierTotal = 0;
    for (GazeModifier gazeModifier : pGazeModifiers) {
      modifierTotal += gazeModifier.getModifier();
    }
    return Math.max(2, getAgilityRollBase(pPlayer.getAgility()) + modifierTotal);
  }

  public int minimumRollCatch(Player pPlayer, Set<CatchModifier> pCatchModifiers) {
    int modifierTotal = 0;
    for (CatchModifier catchModifier : pCatchModifiers) {
      modifierTotal += catchModifier.getModifier();
    }
    return Math.max(2, getAgilityRollBase(pPlayer.getAgility()) + modifierTotal);
  }

  public int minimumRollResistingFoulAppearance() {
    return 2;
  }

  public int minimumRollPass(Player pThrower, PassingDistance pPassingDistance, Set<PassModifier> pPassModifiers) {
    return UtilRangeRuler.minimumRollPass(pThrower, pPassingDistance, pPassModifiers);
  }

  public int minimumRollThrowTeamMate(Player pThrower, PassingDistance pPassingDistance, Set<PassModifier> pPassModifiers) {
    return UtilRangeRuler.minimumRollThrowTeamMate(pThrower, pPassingDistance, pPassModifiers);
  }

  public int minimumRollRightStuff(Player pPlayer, Set<RightStuffModifier> pRightStuffModifiers) {
    int modifierTotal = 0;
    for (RightStuffModifier rightStuffModifier : pRightStuffModifiers) {
      modifierTotal += rightStuffModifier.getModifier();
    }
    return Math.max(2, getAgilityRollBase(pPlayer.getAgility()) + modifierTotal);
  }

  public boolean isPassFumble(int roll, Player pThrower, PassingDistance pPassingDistance, Set<PassModifier> pPassModifiers) {
    if (roll == 1) {
      return true;
    } else if (roll == 6) {
      return false;
    } else {
      int modifierTotal = 0;
      for (PassModifier passModifier : pPassModifiers) {
        modifierTotal += passModifier.getModifier();
      }
      return ((roll + pPassingDistance.getModifier() - modifierTotal) <= 1);
    }
  }

  public boolean isSkillRollSuccessful(int roll, int pMinimumRoll) {
    return ((roll == 6) || ((roll != 1) && (roll >= pMinimumRoll)));
  }

  public boolean isThickSkullUsed(int[] pInjuryRoll) {
    return (ArrayTool.isProvided(pInjuryRoll) && (pInjuryRoll[0] + pInjuryRoll[1] == 8));
  }

  public boolean isSpecialEffectSuccesful(SpecialEffect pSpecialEffect, Player targetPlayer, int roll) {
    if (pSpecialEffect == SpecialEffect.LIGHTNING) {
      return (roll >= 2);
    } else if (pSpecialEffect == SpecialEffect.ZAP) {
      return (roll >= targetPlayer.getStrength());
    } else if ((pSpecialEffect == SpecialEffect.FIREBALL) || (pSpecialEffect == SpecialEffect.BOMB)) {
      return (roll >= 4);
    } else {
      return false;
    }
  }

  public boolean isRegenerationSuccessful(int roll) {
    return (roll >= 4);
  }

  public boolean isLonerSuccessful(int roll) {
    return (roll >= 4);
  }

  public boolean isProSuccessful(int roll) {
    return (roll >= 4);
  }

  public boolean isAffectedByPitchInvasion(int roll, int pFameOtherTeam) {
    return ((roll > 1) && (roll + pFameOtherTeam >= 6));
  }

  public boolean isRecoveringFromKnockout(int roll, int pBloodweiserBabes) {
    return ((roll > 1) && ((roll + pBloodweiserBabes) > 3));
  }

  public boolean isAlwaysHungrySuccessful(int roll) {
    return (roll >= 2);
  }

  public boolean isEscapeFromAlwaysHungrySuccessful(int roll) {
    return (roll >= 2);
  }

  public boolean isExhausted(int roll) {
    return (roll == 1);
  }

  public boolean isTentaclesEscapeSuccessful(int[] roll, int pTentaclePlayerStrength, int pDodgingPlayerStrength) {
    return (ArrayTool.isProvided(roll) && (roll.length > 1) && ((roll[0] + roll[1]) >= minimumRollTentaclesEscape(pTentaclePlayerStrength,
        pDodgingPlayerStrength)));
  }

  public int minimumRollTentaclesEscape(int pTentaclePlayerStrength, int pDodgingPlayerStrength) {
    return (6 + pTentaclePlayerStrength - pDodgingPlayerStrength);
  }

  public boolean isShadowingEscapeSuccessful(int[] roll, int pShadowingPlayerMovement, int pDodgingPlayerMovement) {
    return (ArrayTool.isProvided(roll) && (roll.length > 1) && ((roll[0] + roll[1]) >= minimumRollShadowingEscape(pShadowingPlayerMovement,
        pDodgingPlayerMovement)));
  }

  public int minimumRollShadowingEscape(int pShadowingPlayerMovement, int pDodgingPlayerMovement) {
    return (8 + pShadowingPlayerMovement - pDodgingPlayerMovement);
  }

  public int minimumRollDauntless(int pAttackerStrength, int pDefenderStrength) {
    return (pDefenderStrength - pAttackerStrength + 1);
  }

  public int minimumRollChainsaw() {
    return 2;
  }

  public int minimumRollConfusion(boolean pGoodConditions) {
    return pGoodConditions ? 2 : 4;
  }

  public int minimumRollBloodLust() {
    return 2;
  }
  
  public int minimumRollAnimosity() {
    return 2;
  }

  public int minimumRollWeepingDagger() {
    return 4;
  }

  public int minimumRollSafeThrow(Player pPlayer) {
    return Math.max(2, getAgilityRollBase(pPlayer.getAgility()));
  }

  public int interpretFanFactorRoll(int[] pFanFactorRoll, int pFanFactor, int pScoreDiff) {
    int fanFactorModifier = 0;
    int fanFactorTotal = 0;
    if (ArrayTool.isProvided(pFanFactorRoll)) {
      for (int roll : pFanFactorRoll) {
        fanFactorTotal += roll;
      }
    }
    if ((pScoreDiff >= 0) && (fanFactorTotal > pFanFactor)) {
      fanFactorModifier = 1;
    }
    if ((pScoreDiff <= 0) && (fanFactorTotal < pFanFactor)) {
      fanFactorModifier = -1;
    }
    return fanFactorModifier;
  }

  public int interpretMasterChefRoll(int[] pMasterChefRoll) {
    int reRollsStolen = 0;
    if (ArrayTool.isProvided(pMasterChefRoll)) {
      for (int roll : pMasterChefRoll) {
        if (roll > 3) {
          reRollsStolen++;
        }
      }
    }
    return reRollsStolen;
  }

  public PlayerState interpretRollInjury(GameState pGameState, InjuryResult pInjuryResult) {
    PlayerState playerState = null;
    if ((pGameState != null) && (pInjuryResult != null)) {
      Game game = pGameState.getGame();
      int[] injuryRoll = pInjuryResult.getInjuryRoll();
      Player defender = game.getPlayerById(pInjuryResult.getDefenderId());
      if ((defender != null) && UtilCards.hasCard(game, defender, Card.GOOD_OLD_MAGIC_CODPIECE)) {
        pInjuryResult.clearInjuryModifiers();
      }
      int total = injuryRoll[0] + injuryRoll[1] + pInjuryResult.getInjuryModifierTotal();
      if ((total == 8) && (defender != null) && UtilCards.hasSkill(game, defender, Skill.THICK_SKULL)) {
        playerState = new PlayerState(PlayerState.STUNNED);
        pInjuryResult.addInjuryModifier(InjuryModifier.THICK_SKULL);
      } else if ((total == 7) && (defender != null) && UtilCards.hasSkill(game, defender, Skill.STUNTY) && (pInjuryResult.getInjuryType() != InjuryType.STAB)
          && !UtilCards.hasCard(game, defender, Card.GOOD_OLD_MAGIC_CODPIECE)) {
        playerState = new PlayerState(PlayerState.KNOCKED_OUT);
        pInjuryResult.addInjuryModifier(InjuryModifier.STUNTY);
      } else if ((total == 9) && (defender != null) && UtilCards.hasSkill(game, defender, Skill.STUNTY) && (pInjuryResult.getInjuryType() != InjuryType.STAB)
          && !UtilCards.hasCard(game, defender, Card.GOOD_OLD_MAGIC_CODPIECE)) {
        playerState = new PlayerState(PlayerState.BADLY_HURT);
        pInjuryResult.addInjuryModifier(InjuryModifier.STUNTY);
      } else if (total > 9) {
        playerState = null;
      } else if (total > 7) {
        playerState = new PlayerState(PlayerState.KNOCKED_OUT);
      } else {
        playerState = new PlayerState(PlayerState.STUNNED);
      }
    }
    return playerState;
  }

  public PlayerState interpretRollCasualty(int[] pCasualtyRoll) {
    if (ArrayTool.isProvided(pCasualtyRoll)) {
      switch (pCasualtyRoll[0]) {
        case 6:
          return new PlayerState(PlayerState.RIP);
        case 5:
        case 4:
          return new PlayerState(PlayerState.SERIOUS_INJURY);
        default: // 1 - 3
          return new PlayerState(PlayerState.BADLY_HURT);
      }
    } else {
      return null;
    }
  }

  public SeriousInjury interpretRollSeriousInjury(int[] pCasualtyRoll) {

    // 11-38 Badly Hurt No long term effect
    // 41 Broken Ribs Miss next game
    // 42 Groin Strain Miss next game
    // 43 Gouged Eye Miss next game
    // 44 Broken Jaw Miss next game
    // 45 Fractured Arm Miss next game
    // 46 Fractured Leg Miss next game
    // 47 Smashed Hand Miss next game
    // 48 Pinched Nerve Miss next game
    // 51 Damaged Back Niggling Injury
    // 52 Smashed Knee Niggling Injury
    // 53 Smashed Hip -1 MA
    // 54 Smashed Ankle -1 MA
    // 55 Serious Concussion -1 AV
    // 56 Fractured Skull -1 AV
    // 57 Broken Neck -1 AG
    // 58 Smashed Collar Bone -1 ST
    // 61-68 DEAD Dead!

    SeriousInjury seriousInjury = null;
    switch (pCasualtyRoll[0]) {
      case 4:
        switch (pCasualtyRoll[1]) {
          case 1:
            seriousInjury = SeriousInjury.BROKEN_RIBS;
            break;
          case 2:
            seriousInjury = SeriousInjury.GROIN_STRAIN;
            break;
          case 3:
            seriousInjury = SeriousInjury.GOUGED_EYE;
            break;
          case 4:
            seriousInjury = SeriousInjury.BROKEN_JAW;
            break;
          case 5:
            seriousInjury = SeriousInjury.FRACTURED_ARM;
            break;
          case 6:
            seriousInjury = SeriousInjury.FRACTURED_LEG;
            break;
          case 7:
            seriousInjury = SeriousInjury.SMASHED_HAND;
            break;
          case 8:
            seriousInjury = SeriousInjury.PINCHED_NERVE;
            break;
        }
        break;
      case 5:
        switch (pCasualtyRoll[1]) {
          case 1:
            seriousInjury = SeriousInjury.DAMAGED_BACK;
            break;
          case 2:
            seriousInjury = SeriousInjury.SMASHED_KNEE;
            break;
          case 3:
            seriousInjury = SeriousInjury.SMASHED_HIP;
            break;
          case 4:
            seriousInjury = SeriousInjury.SMASHED_ANKLE;
            break;
          case 5:
            seriousInjury = SeriousInjury.SERIOUS_CONCUSSION;
            break;
          case 6:
            seriousInjury = SeriousInjury.FRACTURED_SKULL;
            break;
          case 7:
            seriousInjury = SeriousInjury.BROKEN_NECK;
            break;
          case 8:
            seriousInjury = SeriousInjury.SMASHED_COLLAR_BONE;
            break;
        }
        break;
    }
    return seriousInjury;
  }

  public boolean isArmourBroken(GameState pGameState, InjuryResult pInjuryResult) {
    Game game = pGameState.getGame();
    int[] armourRoll = pInjuryResult.getArmorRoll();
    Player defender = game.getPlayerById(pInjuryResult.getDefenderId());
    int armour = defender.getArmour();
    if (UtilCards.hasCard(game, defender, Card.BELT_OF_INVULNERABILITY)) {
      pInjuryResult.clearArmorModifiers();
    }
    if ((armour > 7) && pInjuryResult.hasArmorModifier(ArmorModifier.CLAWS)) {
      armour = 7;
    }
    return (armour < (armourRoll[0] + armourRoll[1] + pInjuryResult.getArmorModifierTotal()));
  }

  public boolean isApothecarySuccessful(int roll) {
    return (roll > 1);
  }

  public boolean isBribesSuccessful(int roll) {
    return (roll > 1);
  }

  public boolean isArgueTheCallSuccessful(int roll) {
    return (roll > 5);
  }

  public boolean isCoachBanned(int roll) {
    return (roll < 2);
  }

  public boolean isStandUpSuccessful(int roll, int pModifier) {
    return (roll > 1 && roll + pModifier > 3);
  }

  public boolean isPlayerDefecting(int roll) {
    return ((roll > 0) && (roll < 4));
  }

  public Direction interpretScatterDirectionRoll(int roll) {
    return new DirectionFactory().forRoll(roll);
  }

  public Direction interpretThrowInDirectionRoll(FieldCoordinate pStartCoordinate, int roll) {
    // Endzone Home Team
    if (pStartCoordinate.getX() < 1) {
      return interpretThrowInDirectionRoll(Direction.EAST, roll);
    }
    // Endzone Away Team
    if (pStartCoordinate.getX() > 24) {
      return interpretThrowInDirectionRoll(Direction.WEST, roll);
    }
    // Lower Sideline
    if (pStartCoordinate.getY() > 13) {
      return interpretThrowInDirectionRoll(Direction.NORTH, roll);
    }
    // Upper Sideline
    if (pStartCoordinate.getY() < 1) {
      return interpretThrowInDirectionRoll(Direction.SOUTH, roll);
    }
    throw new IllegalStateException("Unable to determine throwInDirection.");
  }

  public Direction interpretThrowInDirectionRoll(Direction pTemplateDirection, int roll) {
    if (pTemplateDirection == Direction.EAST) {
      switch (roll) {
        case 1:
        case 2:
          return Direction.NORTHEAST;
        case 3:
        case 4:
          return Direction.EAST;
        case 5:
        case 6:
          return Direction.SOUTHEAST;
      }
    }
    if (pTemplateDirection == Direction.WEST) {
      switch (roll) {
        case 1:
        case 2:
          return Direction.SOUTHWEST;
        case 3:
        case 4:
          return Direction.WEST;
        case 5:
        case 6:
          return Direction.NORTHWEST;
      }
    }
    if (pTemplateDirection == Direction.NORTH) {
      switch (roll) {
        case 1:
        case 2:
          return Direction.NORTHWEST;
        case 3:
        case 4:
          return Direction.NORTH;
        case 5:
        case 6:
          return Direction.NORTHEAST;
      }
    }
    if (pTemplateDirection == Direction.SOUTH) {
      switch (roll) {
        case 1:
        case 2:
          return Direction.SOUTHEAST;
        case 3:
        case 4:
          return Direction.SOUTH;
        case 5:
        case 6:
          return Direction.SOUTHWEST;
      }
    }
    throw new IllegalStateException("Unable to determine throwInDirection.");
  }

  public int interpretRiotRoll(int pRiotRoll) {
    return ((pRiotRoll < 4) ? 1 : -1);
  }

  public boolean isDouble(int[] roll) {
    return ((roll != null) && (roll.length == 2) && (roll[0] == roll[1]));
  }

  public CardEffect interpretWitchBrewRoll(int roll) {
    switch (roll) {
      case 1:
        return CardEffect.MAD_CAP_MUSHROOM_POTION;
      case 3:
      case 4:
      case 5:
      case 6:
        return CardEffect.SEDATIVE;
      default:
        return null;
    }
  }

}
