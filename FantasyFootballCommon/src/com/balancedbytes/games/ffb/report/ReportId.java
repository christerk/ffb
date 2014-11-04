package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.IEnumWithId;
import com.balancedbytes.games.ffb.IEnumWithName;




/**
 * 
 * @author Kalimar
 */
public enum ReportId implements IEnumWithId, IEnumWithName {
  
  ALWAYS_HUNGRY_ROLL(1, "alwaysHungryRoll"),
  CATCH_ROLL(2, "catchRoll"),
  CONFUSION_ROLL(3, "confusionRoll"),
  DAUNTLESS_ROLL(4, "dauntlessRoll"),
  DODGE_ROLL(5, "dodgeRoll"),
  ESCAPE_ROLL(6, "escapeRoll"),
  FOUL_APPEARANCE_ROLL(7, "foulAppearanceRoll"),
  GO_FOR_IT_ROLL(8, "goForItRoll"),
  INTERCEPTION_ROLL(9, "interceptionRoll"),
  LEAP_ROLL(10, "leapRoll"),
  PASS_ROLL(11, "passRoll"),
  PICK_UP_ROLL(12, "pickUpRoll"),
  RIGHT_STUFF_ROLL(13, "rightStuffRoll"),
  REGENERATION_ROLL(14, "regenerationRoll"),
  SAFE_THROW_ROLL(15, "safeThrowRoll"),
  TENTACLES_SHADOWING_ROLL(16, "tentaclesShadowingRoll"),
  SKILL_USE(17, "skillUse"),
  RE_ROLL(18, "reRoll"),
  TURN_END(19, "turnEnd"),
  PLAYER_ACTION(20, "playerAction"),
  FOUL(21, "foul"),
  HAND_OVER(22, "handOver"),
  INJURY(23, "injury"),
  APOTHECARY_ROLL(24, "apothecaryRoll"),
  APOTHECARY_CHOICE(25, "apothecaryChoice"),
  THROW_IN(26, "throwIn"),
  SCATTER_BALL(27, "scatterBall"),
  BLOCK(28, "block"),
  BLOCK_CHOICE(29, "blockChoice"),
  SPECTATORS(30, "spectators"),
  WEATHER(31, "weather"),
  COIN_THROW(32, "coinThrow"),
  RECEIVE_CHOICE(33, "receiveChoice"),
  KICKOFF_RESULT(34, "kickoffResult"),
  KICKOFF_SCATTER(35, "kickoffScatter"),
  KICKOFF_EXTRA_REROLL(36, "extraReRoll"),
  KICKOFF_RIOT(37, "kickoffRiot"),
  KICKOFF_THROW_A_ROCK(38, "kickoffThrowARock"),
  PUSHBACK(39, "pushback"),
  REFEREE(40, "referee"),
  KICKOFF_PITCH_INVASION(41, "kickoffPitchInvasion"),
  THROW_TEAM_MATE_ROLL(42, "throwTeamMateRoll"),
  SCATTER_PLAYER(43, "scatterPlayer"),
  TIMEOUT_ENFORCED(44, "timeoutEnforced"),
  WINNINGS_ROLL(45, "winningsRoll"),
  FUMBBL_RESULT_UPLOAD(46, "fumbblResultUpload"),
  FAN_FACTOR_ROLL(47, "fanFactorRoll"),
  MOST_VALUABLE_PLAYERS(48, "mostValuablePlayers"),
  DEFECTING_PLAYERS(49, "defectingPlayers"),
  JUMP_UP_ROLL(51, "jumpUpRoll"),
  STAND_UP_ROLL(52, "standUpRoll"),
  BRIBES_ROLL(53, "bribesRoll"),
  MASTER_CHEF_ROLL(54, "masterChefRoll"),
  START_HALF(55, "startHalf"),
  INDUCEMENT(56, "inducement"),
  PILING_ON(57, "pilingOn"),
  CHAINSAW_ROLL(58, "chainsawRoll"),
  LEADER(59, "leader"),
  SECRET_WEAPON_BAN(60, "secretWeaponBan"),
  BLOOD_LUST_ROLL(61, "bloodLustRoll"),
  HYPNOTIC_GAZE_ROLL(62, "hypnoticGazeRoll"),
  BITE_SPECTATOR(63, "biteSpectator"),
  ANIMOSITY_ROLL(64, "animosityRoll"),
  RAISE_DEAD(65, "raiseDead"),
  BLOCK_ROLL(66, "blockRoll"),
  PENALTY_SHOOTOUT(67, "penaltyShootout"),
  DOUBLE_HIRED_STAR_PLAYER(68, "doubleHiredStarPlayer"),
  SPELL_EFFECT_ROLL(69, "spellEffectRoll"),
  WIZARD_USE(70, "wizardUse"),
  GAME_OPTIONS(71, "gameOptions"),  // only for conversion
  PASS_BLOCK(72, "passBlock"),
  NO_PLAYERS_TO_FIELD(73, "noPlayersToField"),
  PLAY_CARD(74, "playCard"),
  CARD_DEACTIVATED(75, "cardDeactivated"),
  BOMB_OUT_OF_BOUNDS(76, "bombOutOfBounds"),
  PETTY_CASH(77, "pettyCash"),
  INDUCEMENTS_BOUGHT(78, "inducementsBought"),
  CARDS_BOUGHT(79, "cardsBought"),
  CARD_EFFECT_ROLL(80, "cardEffectRoll");
  
  // obsolete: 50 (spiralling expenses)
  // obsolete: 71 (game options)
  
  private int fId;
  private String fName;
  
  private ReportId(int pId, String pName) {
    fId = pId;
    fName = pName;
  }
  
  public int getId() {
    return fId;
  }

  public String getName() {
    return fName;
  }
  
  public IReport createReport() {
    switch(this) {
      case ALWAYS_HUNGRY_ROLL:
        return new ReportSkillRoll(ALWAYS_HUNGRY_ROLL);
      case CARD_EFFECT_ROLL:
        return new ReportCardEffectRoll();
      case CATCH_ROLL:
        return new ReportCatchRoll();
      case CONFUSION_ROLL:
        return new ReportConfusionRoll();
      case DAUNTLESS_ROLL:
        return new ReportDauntlessRoll();
      case DEFECTING_PLAYERS:
        return new ReportDefectingPlayers();
      case DODGE_ROLL:
        return new ReportSkillRoll(DODGE_ROLL);
      case ESCAPE_ROLL:
        return new ReportSkillRoll(ESCAPE_ROLL);
      case FAN_FACTOR_ROLL:
        return new ReportFanFactorRoll();
      case FOUL:
        return new ReportFoul();
      case FOUL_APPEARANCE_ROLL:
        return new ReportSkillRoll(FOUL_APPEARANCE_ROLL);
      case FUMBBL_RESULT_UPLOAD:
        return new ReportFumbblResultUpload();
      case GO_FOR_IT_ROLL:
        return new ReportSkillRoll(GO_FOR_IT_ROLL);
      case HAND_OVER:
        return new ReportHandOver();
      case INJURY:
        return new ReportInjury();
      case INTERCEPTION_ROLL:
        return new ReportInterceptionRoll();
      case LEAP_ROLL:
        return new ReportSkillRoll(LEAP_ROLL);
      case MOST_VALUABLE_PLAYERS:
        return new ReportMostValuablePlayers();
      case PASS_ROLL:
        return new ReportPassRoll();
      case PICK_UP_ROLL:
        return new ReportSkillRoll(PICK_UP_ROLL);
      case PLAYER_ACTION:
        return new ReportPlayerAction();
      case RE_ROLL:
        return new ReportReRoll();
      case REGENERATION_ROLL:
        return new ReportSkillRoll(REGENERATION_ROLL);
      case RIGHT_STUFF_ROLL:
        return new ReportSkillRoll(RIGHT_STUFF_ROLL);
      case SAFE_THROW_ROLL:
        return new ReportSkillRoll(SAFE_THROW_ROLL);
      case SKILL_USE:
        return new ReportSkillUse();
      case TENTACLES_SHADOWING_ROLL:
        return new ReportTentaclesShadowingRoll();
      case TURN_END:
        return new ReportTurnEnd();
        
      // TODO: sort alphabetically
        
      case APOTHECARY_ROLL:
        return new ReportApothecaryRoll();
      case APOTHECARY_CHOICE:
        return new ReportApothecaryChoice();
      case THROW_IN:
        return new ReportThrowIn();
      case SCATTER_BALL:
        return new ReportScatterBall();
      case BLOCK:
        return new ReportBlock();
      case BLOCK_CHOICE:
        return new ReportBlockChoice();
      case SPECTATORS:
        return new ReportSpectators();
      case WEATHER:
        return new ReportWeather();
      case COIN_THROW:
        return new ReportCoinThrow();
      case RECEIVE_CHOICE:
        return new ReportReceiveChoice();
      case KICKOFF_RESULT:
        return new ReportKickoffResult();
      case KICKOFF_SCATTER:
        return new ReportKickoffScatter();
      case KICKOFF_EXTRA_REROLL:
        return new ReportKickoffExtraReRoll();
      case KICKOFF_RIOT:
        return new ReportKickoffRiot();
      case KICKOFF_THROW_A_ROCK:
        return new ReportKickoffThrowARock();
      case PUSHBACK:
        return new ReportPushback();
      case REFEREE:
        return new ReportReferee();
      case KICKOFF_PITCH_INVASION:
        return new ReportKickoffPitchInvasion();
      case THROW_TEAM_MATE_ROLL:
        return new ReportThrowTeamMateRoll();
      case SCATTER_PLAYER:
        return new ReportScatterPlayer();
      case TIMEOUT_ENFORCED:
        return new ReportTimeoutEnforced();
      case WINNINGS_ROLL:
        return new ReportWinningsRoll();
      case JUMP_UP_ROLL:
        return new ReportSkillRoll(JUMP_UP_ROLL);
      case STAND_UP_ROLL:
        return new ReportStandUpRoll();
      case BRIBES_ROLL:
        return new ReportBribesRoll();
      case MASTER_CHEF_ROLL:
        return new ReportMasterChefRoll();
      case START_HALF:
        return new ReportStartHalf();
      case INDUCEMENT:
        return new ReportInducement();
      case PILING_ON:
        return new ReportPilingOn();
      case CHAINSAW_ROLL:
        return new ReportSkillRoll(CHAINSAW_ROLL);
      case LEADER:
        return new ReportLeader();
      case SECRET_WEAPON_BAN:
        return new ReportSecretWeaponBan();
      case BLOOD_LUST_ROLL:
        return new ReportSkillRoll(BLOOD_LUST_ROLL);
      case HYPNOTIC_GAZE_ROLL:
        return new ReportSkillRoll(HYPNOTIC_GAZE_ROLL);
      case BITE_SPECTATOR:
        return new ReportBiteSpectator();
      case ANIMOSITY_ROLL:
        return new ReportSkillRoll(ANIMOSITY_ROLL);
      case RAISE_DEAD:
        return new ReportRaiseDead();
      case BLOCK_ROLL:
        return new ReportBlockRoll();
      case PENALTY_SHOOTOUT:
        return new ReportPenaltyShootout();
      case DOUBLE_HIRED_STAR_PLAYER:
        return new ReportDoubleHiredStarPlayer();
      case SPELL_EFFECT_ROLL:
      	return new ReportSpecialEffectRoll();
      case WIZARD_USE:
      	return new ReportWizardUse();
      case PASS_BLOCK:
      	return new ReportPassBlock();
      case NO_PLAYERS_TO_FIELD:
      	return new ReportNoPlayersToField();
      case PLAY_CARD:
      	return new ReportPlayCard();
      case CARD_DEACTIVATED:
      	return new ReportCardDeactivated();
      case BOMB_OUT_OF_BOUNDS:
      	return new ReportBombOutOfBounds();
      case PETTY_CASH:
      	return new ReportPettyCash();
      case INDUCEMENTS_BOUGHT:
      	return new ReportInducementsBought();
      case CARDS_BOUGHT:
      	return new ReportCardsBought();
      case GAME_OPTIONS:
      	return new ReportGameOptions();
      default:
        throw new IllegalStateException("Unhandled report id "+ getName() + ".");
    }
  }

}
