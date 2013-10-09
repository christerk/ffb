package com.balancedbytes.games.ffb.client;


import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.ArmorModifier;
import com.balancedbytes.games.ffb.BlockResult;
import com.balancedbytes.games.ffb.BlockResultFactory;
import com.balancedbytes.games.ffb.CatchModifier;
import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.Direction;
import com.balancedbytes.games.ffb.DodgeModifier;
import com.balancedbytes.games.ffb.HeatExhaustion;
import com.balancedbytes.games.ffb.IRollModifier;
import com.balancedbytes.games.ffb.InjuryModifier;
import com.balancedbytes.games.ffb.InterceptionModifier;
import com.balancedbytes.games.ffb.KickoffResult;
import com.balancedbytes.games.ffb.KnockoutRecovery;
import com.balancedbytes.games.ffb.LeaderState;
import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.PickupModifier;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.PushbackMode;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.ServerStatus;
import com.balancedbytes.games.ffb.net.commands.ServerCommandJoin;
import com.balancedbytes.games.ffb.net.commands.ServerCommandLeave;
import com.balancedbytes.games.ffb.report.IReport;
import com.balancedbytes.games.ffb.report.ReportApothecaryChoice;
import com.balancedbytes.games.ffb.report.ReportApothecaryRoll;
import com.balancedbytes.games.ffb.report.ReportBiteSpectator;
import com.balancedbytes.games.ffb.report.ReportBlock;
import com.balancedbytes.games.ffb.report.ReportBlockChoice;
import com.balancedbytes.games.ffb.report.ReportBlockRoll;
import com.balancedbytes.games.ffb.report.ReportBombOutOfBounds;
import com.balancedbytes.games.ffb.report.ReportBribesRoll;
import com.balancedbytes.games.ffb.report.ReportCardDeactivated;
import com.balancedbytes.games.ffb.report.ReportCardsBought;
import com.balancedbytes.games.ffb.report.ReportCatchRoll;
import com.balancedbytes.games.ffb.report.ReportCoinThrow;
import com.balancedbytes.games.ffb.report.ReportConfusionRoll;
import com.balancedbytes.games.ffb.report.ReportDauntlessRoll;
import com.balancedbytes.games.ffb.report.ReportDefectingPlayers;
import com.balancedbytes.games.ffb.report.ReportDoubleHiredStarPlayer;
import com.balancedbytes.games.ffb.report.ReportFanFactorRoll;
import com.balancedbytes.games.ffb.report.ReportFoul;
import com.balancedbytes.games.ffb.report.ReportFumbblResultUpload;
import com.balancedbytes.games.ffb.report.ReportHandOver;
import com.balancedbytes.games.ffb.report.ReportInducement;
import com.balancedbytes.games.ffb.report.ReportInducementsBought;
import com.balancedbytes.games.ffb.report.ReportInjury;
import com.balancedbytes.games.ffb.report.ReportInterceptionRoll;
import com.balancedbytes.games.ffb.report.ReportKickoffExtraReRoll;
import com.balancedbytes.games.ffb.report.ReportKickoffPitchInvasion;
import com.balancedbytes.games.ffb.report.ReportKickoffResult;
import com.balancedbytes.games.ffb.report.ReportKickoffRiot;
import com.balancedbytes.games.ffb.report.ReportKickoffScatter;
import com.balancedbytes.games.ffb.report.ReportKickoffThrowARock;
import com.balancedbytes.games.ffb.report.ReportLeader;
import com.balancedbytes.games.ffb.report.ReportList;
import com.balancedbytes.games.ffb.report.ReportMasterChefRoll;
import com.balancedbytes.games.ffb.report.ReportMostValuablePlayers;
import com.balancedbytes.games.ffb.report.ReportNoPlayersToField;
import com.balancedbytes.games.ffb.report.ReportPassBlock;
import com.balancedbytes.games.ffb.report.ReportPassRoll;
import com.balancedbytes.games.ffb.report.ReportPenaltyShootout;
import com.balancedbytes.games.ffb.report.ReportPettyCash;
import com.balancedbytes.games.ffb.report.ReportPilingOn;
import com.balancedbytes.games.ffb.report.ReportPlayCard;
import com.balancedbytes.games.ffb.report.ReportPlayerAction;
import com.balancedbytes.games.ffb.report.ReportPushback;
import com.balancedbytes.games.ffb.report.ReportRaiseDead;
import com.balancedbytes.games.ffb.report.ReportReRoll;
import com.balancedbytes.games.ffb.report.ReportReceiveChoice;
import com.balancedbytes.games.ffb.report.ReportReferee;
import com.balancedbytes.games.ffb.report.ReportScatterBall;
import com.balancedbytes.games.ffb.report.ReportScatterPlayer;
import com.balancedbytes.games.ffb.report.ReportSecretWeaponBan;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.report.ReportSpecialEffectRoll;
import com.balancedbytes.games.ffb.report.ReportSpectators;
import com.balancedbytes.games.ffb.report.ReportStandUpRoll;
import com.balancedbytes.games.ffb.report.ReportStartHalf;
import com.balancedbytes.games.ffb.report.ReportTentaclesShadowingRoll;
import com.balancedbytes.games.ffb.report.ReportThrowIn;
import com.balancedbytes.games.ffb.report.ReportThrowTeamMateRoll;
import com.balancedbytes.games.ffb.report.ReportTimeoutEnforced;
import com.balancedbytes.games.ffb.report.ReportTurnEnd;
import com.balancedbytes.games.ffb.report.ReportWeather;
import com.balancedbytes.games.ffb.report.ReportWinningsRoll;
import com.balancedbytes.games.ffb.report.ReportWizardUse;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 * 
 * @author Kalimar
 */
public class StatusReport {
  
  private FantasyFootballClient fClient;
  private int fIndent;
  private boolean fShowModifiersOnSuccess;
  private boolean fShowModifiersOnFailure;
  
  private boolean fPettyCashReportReceived;
  private boolean fCardsBoughtReportReceived;
  private boolean fInducmentsBoughtReportReceived;

  public StatusReport(FantasyFootballClient pClient) {
    fClient = pClient;
    fShowModifiersOnSuccess = true;
    fShowModifiersOnFailure = true;
  }

  public FantasyFootballClient getClient() {
    return fClient;
  }
  
  public int getIndent() {
    return fIndent;
  }
  
  public void setIndent(int pIndent) {
    fIndent = pIndent;
  }
  
  public void reportVersion() {
    StringBuilder status = new StringBuilder();
    status.append("FantasyFootballClient Version ").append(FantasyFootballClient.CLIENT_VERSION);
    println(0, status.toString());
//    status = new StringBuilder();
//    status.append("FantasyFootball Version expected ").append(FantasyFootballClient.SERVER_VERSION);
//    println(0, status.toString());    
  }

  public void reportConnecting(InetAddress pInetAddress, int pPort) {
    StringBuilder status = new StringBuilder();
    status.append("Connecting to ").append(pInetAddress).append(":").append(pPort).append(" ...");
    println(0, status.toString());    
  }
  
  public void reportIconLoadFailure(URL pIconUrl) {
    StringBuilder status = new StringBuilder();
    status.append("Unable to load icon from URL ").append(pIconUrl).append(".");
    println(0, status.toString());    
  }

  public void reportRetrying() {
    println(0, "Retrying ...");    
  }
  
  public void reportTimeout() {
    println(ParagraphStyle.SPACE_ABOVE_BELOW, TextStyle.BOLD, "The timelimit has been reached for this turn.");
  }
  
  public void reportFumbblResultUpload(ReportFumbblResultUpload pReport) {
    StringBuilder status = new StringBuilder();;
    status.append("Fumbbl Result Upload ");
    if (pReport.isSuccessful()) {
      status.append("ok");
    } else {
      status.append("failed");
    }
    println(ParagraphStyle.SPACE_ABOVE_BELOW, TextStyle.BOLD, status.toString());
    println(getIndent() + 1, pReport.getUploadStatus());
  }

  public void reportGameName(String pGameName) {
    if (StringTool.isProvided(pGameName)) {
      StringBuilder status = new StringBuilder();
      status.append("You have started a new game named \"").append(pGameName).append("\".");
      println(0, status.toString());
    }
  }
  
  public void reportSocketClosed() {
    println(ParagraphStyle.SPACE_ABOVE, TextStyle.NONE, "The connection to the server has been closed.");
    println(ParagraphStyle.SPACE_BELOW, TextStyle.NONE, "To re-connect you need to restart the client.");
  }

  public void reportConnectionEstablished(boolean pSuccesful) {
    if (pSuccesful) {
      println(0, "Connection established.");
    } else {
      println(0, "Cannot connect to the server.");
    }
  }  

  public void reportInducement(ReportInducement pReport) {
    Game game = getClient().getGame();
    if (StringTool.isProvided(pReport.getTeamId()) && (pReport.getInducementType() != null)) {
      if (pReport.getTeamId().equals(game.getTeamHome().getId())) {
        print(getIndent(), TextStyle.HOME, game.getTeamHome().getName());
      } else {
        print(getIndent(), TextStyle.AWAY, game.getTeamAway().getName());
      }
      StringBuilder status = new StringBuilder();      
      switch (pReport.getInducementType()) {
        case EXTRA_TEAM_TRAINING:
          print(getIndent(), " use ");
          print(getIndent(), TextStyle.BOLD, "Extra Team Training");
          status.append(" to add ").append(pReport.getValue()).append((pReport.getValue() == 1) ? " Re-Roll." : " Re-Rolls.");
          println(getIndent(), status.toString());
          break;
        case WANDERING_APOTHECARIES:
          print(getIndent(), " use ");
          print(getIndent(), TextStyle.BOLD, "Wandering Apothecaries");
          status.append(" to add ").append(pReport.getValue()).append((pReport.getValue() == 1) ? " Apothecary." : " Apothecaries.");
          println(getIndent(), status.toString());
          break;
        case IGOR:
          print(getIndent(), " use ");
          print(getIndent(), TextStyle.BOLD, "Igor");
          println(getIndent(), " to re-roll the failed Regeneration.");
          break;
        default:
        	break;
      }
    }
  }
  
  public void reportStartHalf(ReportStartHalf pReport) {
    StringBuilder status = new StringBuilder();
    status.append("Starting ");
    if (pReport.getHalf() > 2) {
      status.append("Overtime");
    } else if (pReport.getHalf() > 1) {
      status.append("2nd half");
    } else {
      status.append("1st half");
    }
    println(ParagraphStyle.SPACE_ABOVE_BELOW, TextStyle.TURN, status.toString());
  }

  public void reportMasterChef(ReportMasterChefRoll pReport) {
    Game game = getClient().getGame();
    StringBuilder status = new StringBuilder();
    int[] roll = pReport.getMasterChefRoll();
    status.append("Master Chef Roll [ ").append(roll[0]).append(" ][ ").append(roll[1]).append(" ][ ").append(roll[2]).append(" ]");
    println(getIndent(), TextStyle.ROLL, status.toString());
    status = new StringBuilder();
    printTeamName(game, false, pReport.getTeamId());
    status.append(" steal ");
    if (pReport.getReRollsStolen() == 0) {
      status.append(" no re-rolls from ");
    } else if (pReport.getReRollsStolen() == 1) {
      status.append(pReport.getReRollsStolen()).append(" re-roll from ");
    } else {
      status.append(pReport.getReRollsStolen()).append(" re-rolls from ");
    }
    print(getIndent() + 1, status.toString());
    if (game.getTeamHome().getId().equals(pReport.getTeamId())) {
      printTeamName(game, false, game.getTeamAway().getId());
    } else {
      printTeamName(game, false, game.getTeamHome().getId());
    }
    println(getIndent() + 1, ".");
  }

  public void reportLeader(ReportLeader pReport) {
    Game game = getClient().getGame();
    StringBuilder status = new StringBuilder();
    LeaderState leaderState = pReport.getLeaderState();

    if (LeaderState.AVAILABLE.equals(leaderState)) {
      printTeamName(game, false, pReport.getTeamId());
      status.append(" gain a Leader re-roll.");
      print(getIndent() + 1, status.toString());
    } else {
      status.append("Leader re-roll removed from ");
      print(getIndent() + 1, status.toString());
      printTeamName(game, false, pReport.getTeamId());
    }
    println(getIndent() + 1, ".");
  }
  
  public void reportScatterBall(ReportScatterBall pReport) {
    StringBuilder status = new StringBuilder();
    if (pReport.isGustOfWind()) {
      setIndent(getIndent() + 1);
      status.append("A gust of wind scatters the ball 1 square.");
      println(getIndent(), status.toString());
      status = new StringBuilder();
    }
    int[] rolls = pReport.getRolls();
    if (ArrayTool.isProvided(rolls)) {
      if (rolls.length > 1) {
        status.append("Scatter Rolls [ ");  
      } else {
        status.append("Scatter Roll [ ");        
      }
      for (int i = 0; i < rolls.length; i++) {
        if (i > 0) {
          status.append(", ");
        }
        status.append(rolls[i]);
      }
      status.append(" ] ");
      Direction[] directions = pReport.getDirections();
      for (int i = 0; i < directions.length; i++) {
        if (i > 0) {
          status.append(", ");
        }
        status.append(directions[i].getName());
      }
      println(getIndent(), TextStyle.ROLL, status.toString());
    }
    if (pReport.isGustOfWind()) {
      setIndent(getIndent() - 1);
    }
  }

  public void reportBombOutOfBounds(ReportBombOutOfBounds pReport) {
  	println(getIndent(), TextStyle.BOLD, "Bomb scattered out of bounds.");
  }

  public void reportThrowIn(ReportThrowIn pReport) {
    int directionRoll = pReport.getDirectionRoll();
    int[] distanceRoll = pReport.getDistanceRoll();
    Direction direction = pReport.getDirection();
    if ((distanceRoll != null) && (distanceRoll.length > 1) && (direction != null)) {
      StringBuilder status = new StringBuilder();
      status.append("Throw In Direction Roll [ ").append(directionRoll).append(" ] ").append(direction.getName());
      println(getIndent(), TextStyle.ROLL, status.toString());
      status = new StringBuilder();
      status.append("Throw In Distance Roll [ ").append(distanceRoll[0]).append(" ][ ").append(distanceRoll[1]).append(" ]");
      println(getIndent(), TextStyle.ROLL, status.toString());
      println(getIndent() + 1, "The fans throw the ball back onto the pitch.");
      status = new StringBuilder();
      int distance = distanceRoll[0] + distanceRoll[1];
      status.append("It lands ").append(distance).append(" squares ").append(direction.getName());
      println(getIndent() + 1, status.toString());
    }
  }

  public void reportJoin(ServerCommandJoin pJoinCommand) {
    Game game = getClient().getGame();
    if (ClientMode.PLAYER == pJoinCommand.getClientMode()) {
      print(0, TextStyle.BOLD, "Player ");
      if (StringTool.isProvided(game.getTeamHome().getCoach())) {
        if (game.getTeamHome().getCoach().equals(pJoinCommand.getCoach())) {
          print(0, TextStyle.HOME_BOLD, pJoinCommand.getCoach());
        } else {
          print(0, TextStyle.AWAY_BOLD, pJoinCommand.getCoach());
        }
      } else {
        print(0, TextStyle.BOLD, pJoinCommand.getCoach());
      }
      println(0, TextStyle.BOLD, " joins the game.");
    } else if (ClientMode.SPECTATOR == getClient().getMode()) {
      print(0, "Spectator ");
      print(0, pJoinCommand.getCoach());
      println(0, " joins the game.");
    } else {
      println(0, "A spectator joins the game.");
    }
  }

  public void reportLeave(ServerCommandLeave pLeaveCommand) {
    Game game = getClient().getGame();
    if (ClientMode.PLAYER == pLeaveCommand.getClientMode()) {
      if ((game.getTeamHome() != null) && StringTool.isProvided(game.getTeamHome().getCoach())) {
        print(0, TextStyle.BOLD, "Player ");
        if (game.getTeamHome().getCoach().equals(pLeaveCommand.getCoach())) {
          print(0, TextStyle.HOME_BOLD, pLeaveCommand.getCoach());
        } else {
          print(0, TextStyle.AWAY_BOLD, pLeaveCommand.getCoach());
        }
        println(0, TextStyle.BOLD, " leaves the game.");
      } else {
        println(0, TextStyle.BOLD, "The other player leaves the game.");
      }
    } else if (ClientMode.SPECTATOR == getClient().getMode()) {
      print(0, "Spectator ");
      print(0, pLeaveCommand.getCoach());
      println(0, " leaves the game.");
    } else {
      println(0, "A spectator leaves the game.");
    }
  }
  
  public void reportTimeoutEnforced(ReportTimeoutEnforced pReport) {
    Game game = getClient().getGame();
    StringBuilder status = new StringBuilder();
    status.append("Coach ").append(pReport.getCoach()).append(" forces a Timeout.");
    if (game.getTeamHome().getCoach().equals(pReport.getCoach())) {
      println(ParagraphStyle.SPACE_ABOVE, TextStyle.HOME_BOLD, status.toString());
    } else {
      println(ParagraphStyle.SPACE_ABOVE, TextStyle.AWAY_BOLD, status.toString());
    }
    println(ParagraphStyle.SPACE_BELOW, TextStyle.NONE, "The turn will end after the Acting Player has finished moving.");
  }
  
  public void reportDoubleHiredStarPlayer(ReportDoubleHiredStarPlayer pReport) {
    StringBuilder status = new StringBuilder();
    status.append("Star Player ").append(pReport.getStarPlayerName());
    status.append(" takes money from both teams and plays for neither.");
    println(getIndent(), TextStyle.BOLD, status.toString());
  }

  public void reportGoingForIt(ReportSkillRoll pReport) {
    StringBuilder status = new StringBuilder();
    StringBuilder neededRoll = null;
    Game game = getClient().getGame();
    Player player = game.getActingPlayer().getPlayer();
    status.append("Go For It Roll [ ").append(pReport.getRoll()).append(" ]");
    println(getIndent(), TextStyle.ROLL, status.toString());
    print(getIndent() + 1, false, player);
    if (pReport.isSuccessful()) {
      println(getIndent() + 1, " goes for it!");
      if (!pReport.isReRolled() && fShowModifiersOnSuccess) {
        neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(pReport.getMinimumRoll()).append("+");
      }
    } else {
      println(getIndent() + 1, " trips while going for it.");
      if (!pReport.isReRolled() && fShowModifiersOnFailure) {
        neededRoll = new StringBuilder().append("Roll a ").append(pReport.getMinimumRoll()).append("+ to succeed");
      }
    }
    if (neededRoll != null) {
      neededRoll.append(" (Roll").append(formatRollModifiers(pReport.getModifiers())).append(" > ").append(pReport.getMinimumRoll() - 1).append(").");
      println(getIndent() + 1, TextStyle.NEEDED_ROLL, neededRoll.toString());
    }
  }
  
  private String formatRollModifiers(IRollModifier[] pRollModifiers) {
    StringBuilder modifiers = new StringBuilder();
    if (ArrayTool.isProvided(pRollModifiers)) {
      for (IRollModifier rollModifier : pRollModifiers) {
        if (rollModifier.getModifier() != 0) {
          if (rollModifier.getModifier() > 0) {
            modifiers.append(" - ");
          } else {
            modifiers.append(" + ");
          }
          if (!rollModifier.isModifierIncluded()) {
            modifiers.append(Math.abs(rollModifier.getModifier())).append(" ");
          }
          modifiers.append(rollModifier.getName());
        }
      }
    }
    return modifiers.toString();
  }
  
  public void reportSpectators(ReportSpectators pReport) {
    setIndent(0);
    Game game = getClient().getGame();
    StringBuilder status = new StringBuilder();
    int[] fanRollHome = pReport.getRollHome();
    status.append("Spectator Roll Home Team [ ").append(fanRollHome[0]).append(" ][ ").append(fanRollHome[1]).append(" ]");
    println(getIndent(), TextStyle.ROLL, status.toString());
    status = new StringBuilder();
    int rolledTotalHome = fanRollHome[0] + fanRollHome[1];
    status.append("Rolled Total of ").append(rolledTotalHome);
    int fanFactorHome = game.getTeamHome().getFanFactor();
    status.append(" + ").append(fanFactorHome).append(" Fan Factor");
    status.append(" = ").append(rolledTotalHome + fanFactorHome);
    println(getIndent() + 1, status.toString());
    status = new StringBuilder();
    status.append(StringTool.formatThousands(pReport.getSpectatorsHome())).append(" fans have come to support ");
    print(getIndent() + 1, status.toString());
    print(getIndent() + 1, TextStyle.HOME, game.getTeamHome().getName());
    println(getIndent() + 1, ".");
    status = new StringBuilder();
    int[] fanRollAway = pReport.getRollAway();
    status.append("Spectator Roll Away Team [ ").append(fanRollAway[0]).append(" ][ ").append(fanRollAway[1]).append(" ]");
    println(getIndent(), TextStyle.ROLL, status.toString());
    status = new StringBuilder();
    int rolledTotalAway = fanRollAway[0] + fanRollAway[1];
    status.append("Rolled Total of ").append(rolledTotalAway);
    int fanFactorAway = game.getTeamAway().getFanFactor();
    status.append(" + ").append(fanFactorAway).append(" Fan Factor");
    status.append(" = ").append(rolledTotalAway + fanFactorAway);
    println(getIndent() + 1, status.toString());
    status = new StringBuilder();
    status.append(StringTool.formatThousands(pReport.getSpectatorsAway())).append(" fans have come to support ");
    print(getIndent() + 1, status.toString());
    print(getIndent() + 1, TextStyle.AWAY, game.getTeamAway().getName());
    println(getIndent() + 1, ".");
    status = new StringBuilder();
    if (pReport.getFameHome() > pReport.getFameAway()) {
      status.append("Team ").append(game.getTeamHome().getName());
      if (pReport.getFameHome() - pReport.getFameAway() > 1) {
        status.append(" have the whole audience with them (FAME +2)!");
      } else {
        status.append(" have a fan advantage (FAME +1) for the game.");
      }
      println(getIndent(), TextStyle.HOME_BOLD, status.toString());
    } else if (pReport.getFameAway() > pReport.getFameHome()) {
      status.append("Team ").append(game.getTeamAway().getName());
      if (pReport.getFameAway() - pReport.getFameHome() > 1) {
        status.append(" have the whole audience with them (FAME +2)!");
      } else {
        status.append(" have a fan advantage (FAME +1) for the game.");
      }
      println(getIndent(), TextStyle.AWAY_BOLD, status.toString());
    } else {
      println(getIndent(), TextStyle.BOLD, "Both teams have equal fan support (FAME 0).");
    }
  }

  public void reportPettyCash(ReportPettyCash pReport) {
    Game game = getClient().getGame();
    if (!fPettyCashReportReceived) {
    	fPettyCashReportReceived = true;
    	println(getIndent(), TextStyle.BOLD, "Transfer Petty Cash");
    }
  	print(getIndent() + 1, "Team ");
    if (game.getTeamHome().getId().equals(pReport.getTeamId())) {
    	print(getIndent() + 1, TextStyle.HOME, game.getTeamHome().getName());
    } else {
    	print(getIndent() + 1, TextStyle.AWAY, game.getTeamAway().getName());
    }
    StringBuilder status = new StringBuilder();
    status.append(" transfers ");
    if (pReport.getGold() > 0) {
    	status.append(StringTool.formatThousands(pReport.getGold()));
    	status.append(" gold");
    } else {
    	status.append("nothing");
    }
    status.append(" from the Treasury into Petty Cash.");
    println(getIndent() + 1, status.toString());
  }

  public void reportInducementsBought(ReportInducementsBought pReport) {
    Game game = getClient().getGame();
    if (!fInducmentsBoughtReportReceived) {
    	fInducmentsBoughtReportReceived = true;
      println(getIndent(), TextStyle.BOLD, "Buy Inducements");
    }
  	print(getIndent() + 1, "Team ");
    if (game.getTeamHome().getId().equals(pReport.getTeamId())) {
    	print(getIndent() + 1, TextStyle.HOME, game.getTeamHome().getName());
    } else {
    	print(getIndent() + 1, TextStyle.AWAY, game.getTeamAway().getName());
    }
    StringBuilder status = new StringBuilder();
    status.append(" buys ");
    if ((pReport.getNrOfInducements() == 0) && (pReport.getNrOfStars() == 0) && (pReport.getNrOfMercenaries() == 0)) {
    	status.append("no Inducements.");
    } else {
    	List<String> itemList = new ArrayList<String>();
    	if (pReport.getNrOfInducements() > 0) {
    		if (pReport.getNrOfInducements() == 1) {
    			itemList.add("1 Inducement");
    		} else {
    			itemList.add(StringTool.bind("$1 Inducements", pReport.getNrOfInducements()));
    		}
    	}
    	if (pReport.getNrOfStars() > 0) {
    		if (pReport.getNrOfStars() == 1) {
    			itemList.add("1 Star");
    		} else {
    			itemList.add(StringTool.bind("$1 Stars", pReport.getNrOfStars()));
    		}
    	}
    	if (pReport.getNrOfMercenaries() > 0) {
    		if (pReport.getNrOfMercenaries() == 1) {
    			itemList.add("1 Mercenary");
    		} else {
    			itemList.add(StringTool.bind("$1 Mercenaries", pReport.getNrOfMercenaries()));
    		}
    	}
    	status.append(StringTool.buildEnumeration(itemList.toArray(new String[itemList.size()])));
    	status.append(" for ").append(StringTool.formatThousands(pReport.getGold())).append(" gold total.");
    }
    println(getIndent() + 1, status.toString());
  }

  public void reportCardsBought(ReportCardsBought pReport) {
    Game game = getClient().getGame();
    if (!fCardsBoughtReportReceived) {
    	fCardsBoughtReportReceived = true;
    	println(getIndent(), TextStyle.BOLD, "Buy Cards");
    }
  	print(getIndent() + 1, "Team ");
    if (game.getTeamHome().getId().equals(pReport.getTeamId())) {
    	print(getIndent() + 1, TextStyle.HOME, game.getTeamHome().getName());
    } else {
    	print(getIndent() + 1, TextStyle.AWAY, game.getTeamAway().getName());
    }
    StringBuilder status = new StringBuilder();
    status.append(" buys ");
    if (pReport.getNrOfCards() == 0) {
    	status.append("no Cards.");
    } else {
    	if (pReport.getNrOfCards() == 1) {
    		status.append("1 Card");
    	} else {
    		status.append(pReport.getNrOfCards()).append(" Cards");
    	}
    	status.append(" for ").append(StringTool.formatThousands(pReport.getGold())).append(" gold total.");
    }
    println(getIndent() + 1, status.toString());
  }

  public void reportKickoffExtraReRoll(ReportKickoffExtraReRoll pReport) {
    Game game = getClient().getGame();
    GameResult gameResult = game.getGameResult();
    KickoffResult kickoffResult = pReport.getKickoffResult();
    int fanFavouritesHome = UtilPlayer.findPlayersOnPitchWithSkill(game, game.getTeamHome(), Skill.FAN_FAVOURITE).length;
    int fanFavouritesAway = UtilPlayer.findPlayersOnPitchWithSkill(game, game.getTeamAway(), Skill.FAN_FAVOURITE).length;
    StringBuilder status = new StringBuilder();
    if (kickoffResult == KickoffResult.CHEERING_FANS) {
      status.append("Cheering Fans Roll Home Team [ ").append(pReport.getRollHome()).append(" ]");
      println(getIndent(), TextStyle.ROLL, status.toString());
      int totalHome = pReport.getRollHome() + gameResult.getTeamResultHome().getFame() + fanFavouritesHome + game.getTeamHome().getCheerleaders();
      status = new StringBuilder();
      status.append("Rolled ").append(pReport.getRollHome());
      status.append(" + ").append(gameResult.getTeamResultHome().getFame()).append(" FAME");
      status.append(" + ").append(fanFavouritesHome).append(" Fan Favourites");
      status.append(" + ").append(game.getTeamHome().getCheerleaders()).append(" Cheerleaders");
      status.append(" = ").append(totalHome).append(".");
      println(getIndent() + 1, status.toString());
      status = new StringBuilder();
      status.append("Cheering Fans Roll Away Team [ ").append(pReport.getRollAway()).append(" ]");
      println(getIndent(), TextStyle.ROLL, status.toString());
      int totalAway = pReport.getRollAway() + gameResult.getTeamResultAway().getFame() + fanFavouritesAway + game.getTeamAway().getCheerleaders();
      status = new StringBuilder();
      status.append("Rolled ").append(pReport.getRollAway());
      status.append(" + ").append(gameResult.getTeamResultAway().getFame()).append(" FAME");
      status.append(" + ").append(fanFavouritesAway).append(" Fan Favourites");
      status.append(" + ").append(game.getTeamAway().getCheerleaders()).append(" Cheerleaders");
      status.append(" = ").append(totalAway).append(".");
      println(getIndent() + 1, status.toString());
    }
    if (kickoffResult == KickoffResult.BRILLIANT_COACHING) {
      status.append("Brilliant Coaching Roll Home Team [ ").append(pReport.getRollHome()).append(" ]");
      println(getIndent(), TextStyle.ROLL, status.toString());
      int totalHome = pReport.getRollHome() + gameResult.getTeamResultHome().getFame() + fanFavouritesHome + game.getTeamHome().getAssistantCoaches();
      status = new StringBuilder();
      status.append("Rolled ").append(pReport.getRollHome());
      status.append(" + ").append(gameResult.getTeamResultHome().getFame()).append(" FAME");
      status.append(" + ").append(fanFavouritesHome).append(" Fan Favourites");
      status.append(" + ").append(game.getTeamHome().getAssistantCoaches()).append(" Assistant Coaches");
      status.append(" = ").append(totalHome).append(".");
      println(getIndent() + 1, status.toString());
      status = new StringBuilder();
      status.append("Brilliant Coaching Roll Away Team [ ").append(pReport.getRollAway()).append(" ]");
      println(getIndent(), TextStyle.ROLL, status.toString());
      int totalAway = pReport.getRollAway() + gameResult.getTeamResultAway().getFame() + fanFavouritesAway + game.getTeamAway().getAssistantCoaches();
      status = new StringBuilder();
      status.append("Rolled ").append(pReport.getRollAway());
      status.append(" + ").append(gameResult.getTeamResultAway().getFame()).append(" FAME");
      status.append(" + ").append(fanFavouritesAway).append(" Fan Favourites");
      status.append(" + ").append(game.getTeamAway().getAssistantCoaches()).append(" Assistant Coaches");
      status.append(" = ").append(totalAway).append(".");
      println(getIndent() + 1, status.toString());
    } 
    if (pReport.isHomeGainsReRoll()) {
      print(getIndent(), "Team ");
      print(getIndent(), TextStyle.HOME, game.getTeamHome().getName());
      println(getIndent(), " gains a Re-Roll.");
    }
    if (pReport.isAwayGainsReRoll()) {
      print(getIndent(), "Team ");
      print(getIndent(), TextStyle.AWAY, game.getTeamAway().getName());
      println(getIndent(), " gains a Re-Roll.");
    }
  }
  
  public void reportKickoffThrowARock(ReportKickoffThrowARock pReport) {
    Game game = getClient().getGame();
    GameResult gameResult = game.getGameResult();
    int fanFavouritesHome = UtilPlayer.findPlayersOnPitchWithSkill(game, game.getTeamHome(), Skill.FAN_FAVOURITE).length;
    int fanFavouritesAway = UtilPlayer.findPlayersOnPitchWithSkill(game, game.getTeamAway(), Skill.FAN_FAVOURITE).length;
    StringBuilder status = new StringBuilder();
    status.append("Throw a Rock Roll Home Team [ ").append(pReport.getRollHome()).append(" ]");
    println(getIndent(), TextStyle.ROLL, status.toString());
    int totalHome = pReport.getRollHome() + gameResult.getTeamResultHome().getFame() + fanFavouritesHome;
    status = new StringBuilder();
    status.append("Rolled ").append(pReport.getRollHome());
    status.append(" + ").append(gameResult.getTeamResultHome().getFame()).append(" FAME");
    status.append(" + ").append(fanFavouritesHome).append(" Fan Favourites");
    status.append(" = ").append(totalHome).append(".");
    println(getIndent() + 1, status.toString());
    status = new StringBuilder();
    status.append("Throw a Rock Roll Away Team [ ").append(pReport.getRollAway()).append(" ]");
    println(getIndent(), TextStyle.ROLL, status.toString());
    int totalAway = pReport.getRollAway() + gameResult.getTeamResultAway().getFame() + fanFavouritesAway;
    status = new StringBuilder();
    status.append("Rolled ").append(pReport.getRollAway());
    status.append(" + ").append(gameResult.getTeamResultAway().getFame()).append(" FAME");
    status.append(" + ").append(fanFavouritesAway).append(" Fan Favourites");
    status.append(" = ").append(totalAway).append(".");
    println(getIndent() + 1, status.toString());
    for (String playerId : pReport.getPlayersHit()) {
      Player player = game.getPlayerById(playerId);
      print(getIndent(), false, player);
      println(getIndent(), " is hit by a rock.");
    }
  }
  
  public void reportSpecialEffectRoll(ReportSpecialEffectRoll pReport) {
  	Game game = getClient().getGame();
    StringBuilder status = new StringBuilder();
    if (pReport.getSpecialEffect() == SpecialEffect.LIGHTNING) {
	    status.append("Lightning Spell Effect Roll [ ").append(pReport.getRoll()).append(" ]");
    }
    if (pReport.getSpecialEffect() == SpecialEffect.FIREBALL) {
	    status.append("Fireball Spell Effect Roll [ ").append(pReport.getRoll()).append(" ]");
    }
    if (pReport.getSpecialEffect() == SpecialEffect.BOMB) {
	    status.append("Bomb Effect Roll [ ");
	    status.append((pReport.getRoll() > 0) ? pReport.getRoll() : "automatic success");
	    status.append(" ]");
    }
    println(getIndent(), TextStyle.ROLL, status.toString());
	  print(getIndent() + 1, false, game.getPlayerById(pReport.getPlayerId()));
	  if (pReport.isSuccessful()) {
	  	if (pReport.getSpecialEffect().isWizardSpell()) {
	  		println(getIndent() + 1, " is hit by the spell.");
	  	} else {
	  		println(getIndent() + 1, " is hit by the explosion.");
	  	}
	  } else {
	  	if (pReport.getSpecialEffect().isWizardSpell()) {
	  		println(getIndent() + 1, " escapes the spell effect.");
	  	} else {
	  		println(getIndent() + 1, " escapes the explosion.");
	  	}
	  }
  }
  
  public void reportWizardUse(ReportWizardUse pReport) {
  	Game game = getClient().getGame();
  	print(getIndent(), TextStyle.BOLD, "The team wizard of ");
  	if (game.getTeamHome().getId().equals(pReport.getTeamId())) {
  		print(getIndent(), TextStyle.HOME_BOLD, game.getTeamHome().getName());
  	} else {
  		print(getIndent(), TextStyle.AWAY_BOLD, game.getTeamAway().getName());
  	}
  	if (pReport.getWizardSpell() == SpecialEffect.LIGHTNING) {
  		println(getIndent(), TextStyle.BOLD, " casts a Lightning spell.");
  	} else {
  		println(getIndent(), TextStyle.BOLD, " casts a Fireball spell.");
  	}
  }
  
  public void reportKickoffPitchInvasion(ReportKickoffPitchInvasion pReport) {
    Game game = getClient().getGame();
    GameResult gameResult = game.getGameResult();
    int fanFavouritesHome = UtilPlayer.findPlayersOnPitchWithSkill(game, game.getTeamHome(), Skill.FAN_FAVOURITE).length;
    int fanFavouritesAway = UtilPlayer.findPlayersOnPitchWithSkill(game, game.getTeamAway(), Skill.FAN_FAVOURITE).length;
    int[] rollsHome = pReport.getRollsHome();
    boolean[] playersAffectedHome = pReport.getPlayersAffectedHome();
    Player[] homePlayers = game.getTeamHome().getPlayers();
    for (int i = 0; i < homePlayers.length; i++) {
      if (rollsHome[i] > 0) {
        StringBuilder status = new StringBuilder();
        status.append("Pitch Invasion Roll [ ").append(rollsHome[i]).append(" ]");
        println(getIndent(), TextStyle.ROLL, status.toString());
        print(getIndent() + 1, false, homePlayers[i]);
        status = new StringBuilder();
        if (playersAffectedHome[i]) {
          status.append(" has been stunned.");
        } else {
          status.append(" is unaffected.");
        }
        int total = rollsHome[i] + gameResult.getTeamResultAway().getFame() + fanFavouritesAway;
        status.append(" (Roll ").append(rollsHome[i]);
        status.append(" + ").append(gameResult.getTeamResultAway().getFame()).append(" opposing FAME");
        status.append(" + ").append(fanFavouritesAway).append(" opposing Fan Favourites");
        status.append(" = ").append(total).append(" Total)");
        println(getIndent() + 1, status.toString());
      }
    }
    int[] rollsAway = pReport.getRollsAway();
    boolean[] playersAffectedAway = pReport.getPlayersAffectedAway();
    Player[] awayPlayers = game.getTeamAway().getPlayers();
    for (int i = 0; i < awayPlayers.length; i++) {
      if (rollsAway[i] > 0) {
        StringBuilder status = new StringBuilder();
        status.append("Pitch Invasion Roll [ ").append(rollsAway[i]).append(" ]");
        println(getIndent(), TextStyle.ROLL, status.toString());
        print(getIndent() + 1, false, awayPlayers[i]);
        status = new StringBuilder();
        if (playersAffectedAway[i]) {
          status.append(" has been stunned.");
        } else {
          status.append(" is unaffected.");
        }
        int total = rollsAway[i] + gameResult.getTeamResultHome().getFame() + fanFavouritesHome;
        status.append(" (Roll ").append(rollsAway[i]);
        status.append(" + ").append(gameResult.getTeamResultHome().getFame()).append(" opposing FAME ");
        status.append(" + ").append(fanFavouritesHome).append(" opposing Fan Favourites");
        status.append(" = ").append(total).append(" Total)");
        println(getIndent() + 1, status.toString());
      }
    }
  }

  public void reportDefectingPlayers(ReportDefectingPlayers pReport) {
    Game game = getClient().getGame();
    String[] playerIds = pReport.getPlayerIds();
    if (ArrayTool.isProvided(playerIds)) {
      int[] rolls = pReport.getRolls();
      boolean[] defecting = pReport.getDefecting();
      for (int i = 0; i < playerIds.length; i++) {
        StringBuilder status = new StringBuilder();
        status.append("Defecting Players Roll [ ").append(rolls[i]).append(" ]");
        println(getIndent(), TextStyle.ROLL, status.toString());
        Player player = game.getPlayerById(playerIds[i]);
        print(getIndent() + 1, false, player);
        if (defecting[i]) {
          println(getIndent() + 1, TextStyle.NONE, " leaves the team in disgust.");
        } else {
          println(getIndent() + 1, TextStyle.NONE, " stays with the team.");
        }
      }
    }
  }
  
  public void reportSecretWeaponBan(ReportSecretWeaponBan pReport) {
    Game game = getClient().getGame();
    reportSecretWeaponBan(pReport, game.getTeamHome());
    reportSecretWeaponBan(pReport, game.getTeamAway());
  }

  private void reportSecretWeaponBan(ReportSecretWeaponBan pReport, Team pTeam) {
    Game game = getClient().getGame();
    String[] playerIds = pReport.getPlayerIds();
    if (ArrayTool.isProvided(playerIds)) {
    	int[] rolls = pReport.getRolls();
    	boolean[] banned = pReport.getBanned();
	  	for (int i = 0; i < playerIds.length; i++) {
	  		Player player = game.getPlayerById(playerIds[i]);
	      if (pTeam.hasPlayer(player)) {
	      	if (banned[i]) {        	
		        print(getIndent(), "The ref bans ");
		        print(getIndent(), false, player);
		        println(getIndent(), " for using a Secret Weapon.");
	      	} else {
		        print(getIndent(), "The ref overlooks ");
		        print(getIndent(), false, player);
		        println(getIndent(), " using a Secret Weapon.");
	      	}
	    		Integer secretWeaponValue = player.getPosition().getSkillValue(Skill.SECRET_WEAPON);
	      	if ((rolls[i] > 0) && (secretWeaponValue != null)) {
	      		StringBuilder penalty = new StringBuilder();
	      		penalty.append("Penalty roll was ").append(rolls[i]);
	      		penalty.append(", banned on a ").append(secretWeaponValue).append("+");
	          println(getIndent() + 1, TextStyle.NEEDED_ROLL, penalty.toString());
	      	}
	      }
	  	}
    }
  }

  public void reportKickoffRiot(ReportKickoffRiot pReport) {
    Game game = getClient().getGame();
    StringBuilder status = new StringBuilder();
    if (pReport.getRoll() > 0) {
      status.append("Riot Roll [ ").append(pReport.getRoll()).append(" ]");
    } else {
      status.append("Riot in Turn ").append(game.isHomePlaying() ? game.getTurnDataAway().getTurnNr() : game.getTurnDataHome().getTurnNr());
    }
    println(getIndent(), TextStyle.ROLL, status.toString());
    if (pReport.getTurnModifier() < 0) {
      println(getIndent() + 1, "The referee adjusts the clock back after the riot is over.");
      status = new StringBuilder();
      status.append("Turn Counter is moved ").append(Math.abs(pReport.getTurnModifier()));
      status.append((pReport.getTurnModifier() == -1) ? " step" : " steps").append(" backward.");
      println(getIndent() + 1, status.toString());
    } else {
      println(getIndent() + 1, "The referee does not stop the clock during the riot.");
      status = new StringBuilder();
      status.append("Turn Counter is moved ").append(Math.abs(pReport.getTurnModifier()));
      status.append((pReport.getTurnModifier() == -1) ? " step" : " steps").append(" forward.");
      println(getIndent() + 1, status.toString());
    }
  }

  public void reportReRoll(ReportReRoll pReport) {
    Game game = getClient().getGame();
    Player player = game.getPlayerById(pReport.getPlayerId());
    StringBuilder status = new StringBuilder();
    if (ReRollSource.LONER == pReport.getReRollSource()) {
      status.append("Loner Roll [ ").append(pReport.getRoll()).append(" ]");
      println(getIndent() + 1, TextStyle.ROLL, status.toString());
      print(getIndent() + 2, false, player);
      if (pReport.isSuccessful()) {
        println(getIndent() + 2, " may use a Team Re-Roll.");
      } else {
        println(getIndent() + 2, " wastes a Team Re-Roll.");
      }
    } else if (ReRollSource.PRO == pReport.getReRollSource()) {
      status.append("Pro Roll [ ").append(pReport.getRoll()).append(" ]");
      println(getIndent() + 1, TextStyle.ROLL, status.toString());
      print(getIndent() + 2, false, player);
      status = new StringBuilder();
      if (pReport.isSuccessful()) {
        status.append("'s Pro skill allows ").append(player.getGender().getDative()).append(" to re-roll the action.");
      } else {
        status.append("'s Pro skill does not help ").append(player.getGender().getDative()).append(".");
      }
      println(getIndent() + 2, status.toString());
    } else {
      status.append("Re-Roll using ").append(pReport.getReRollSource().getName().toUpperCase());
      println(getIndent() + 1, status.toString());
    }
    
    
  }
  
  public void reportDauntless(ReportDauntlessRoll pReport) {
    Game game = getClient().getGame();
    Player player = game.getActingPlayer().getPlayer();
    StringBuilder status = new StringBuilder();
    status.append("Dauntless Roll [ ").append(pReport.getRoll()).append(" ]");
    println(getIndent(), TextStyle.ROLL, status.toString());
    print(getIndent() + 1, false, player);
    status = new StringBuilder();
    if (pReport.isSuccessful()) {
      status.append(" uses Dauntless to push ").append(player.getGender().getSelf()).append(" to Strength ").append(pReport.getStrength()).append(".");
    } else {
      status.append(" fails to push ").append(player.getGender().getGenitive()).append(" strength.");
    }
    println(getIndent() + 1, status.toString());
  }

  
  public void reportChainsaw(ReportSkillRoll pReport) {
    Game game = getClient().getGame();
    Player player = game.getActingPlayer().getPlayer();
    StringBuilder status = new StringBuilder();
    status.append("Chainsaw Roll [ ").append(pReport.getRoll()).append(" ]");
    println(getIndent(), TextStyle.ROLL, status.toString());
    print(getIndent() + 1, false, player);
    status = new StringBuilder();
    if (pReport.isSuccessful()) {
      status.append(" uses ").append(player.getGender().getGenitive()).append(" Chainsaw.");
    } else {
      status.append("'s Chainsaw kicks back to hurt ").append(player.getGender().getDative()).append(".");
    }
    println(getIndent() + 1, status.toString());
  }
  
  public void reportFoulAppearance(ReportSkillRoll pReport) {
    Game game = getClient().getGame();
    Player player = game.getActingPlayer().getPlayer();
    StringBuilder status = new StringBuilder();
    status.append("Foul Appearance Roll [ ").append(pReport.getRoll()).append(" ]");
    println(getIndent(), TextStyle.ROLL, status.toString());
    print(getIndent() + 1, false, player);
    if (pReport.isSuccessful()) {
      println(getIndent() + 1, " resists the Foul Appearance.");
    } else {
      println(getIndent() + 1, " cannot overcome the Foul Appearance.");
    }
  }
  
  public void reportRaiseDead(ReportRaiseDead pReport) {
    Game game = getClient().getGame();
    Player raisedPlayer = game.getPlayerById(pReport.getPlayerId());
    print(getIndent(), false, raisedPlayer);
    if (pReport.isNurglesRot()) {
      print(getIndent(), " has been infected with Nurgle's Rot and will join team ");
    } else {
      print(getIndent(), " is raised from the dead to join team ");
    }
    if (game.getTeamHome().hasPlayer(raisedPlayer)) {
      print(getIndent(), TextStyle.HOME, game.getTeamHome().getName());
    } else {
      print(getIndent(), TextStyle.AWAY, game.getTeamAway().getName());
    }
    if (pReport.isNurglesRot()) {
      println(getIndent(), TextStyle.NONE, " as a Rotter in the next game.");
    } else {
      println(getIndent(), TextStyle.NONE, " as a Zombie.");
    }
  }

  public void reportKickoffResult(ReportKickoffResult pReport) {
    setIndent(0);
    StringBuilder status = new StringBuilder();
    int[] kickoffRoll = pReport.getKickoffRoll();
    status.append("Kick-off Event Roll [ ").append(kickoffRoll[0]).append(" ][ ").append(kickoffRoll[1]).append(" ]");
    println(getIndent(), TextStyle.ROLL, status.toString());
    status = new StringBuilder();
    status.append("Kick-off event is ").append(pReport.getKickoffResult().getName());
    println(getIndent() + 1, status.toString());
    println(getIndent() + 1, TextStyle.EXPLANATION, pReport.getKickoffResult().getDescription());
    setIndent(1);
  }

  public void reportKickoffScatter(ReportKickoffScatter pReport) {
    setIndent(0);
    StringBuilder status = new StringBuilder();
    status.append("Kick-off Scatter Roll [ ").append(pReport.getRollScatterDirection()).append(" ]");
    status.append("[ ").append(pReport.getRollScatterDistance()).append(" ]");
    println(getIndent(), TextStyle.ROLL, status.toString());
    status = new StringBuilder();
    status.append("The kick will land ");
    status.append(pReport.getRollScatterDistance()).append((pReport.getRollScatterDistance() == 1) ? " square " : " squares ");
    status.append(pReport.getScatterDirection().getName().toLowerCase()).append(" of where it was aimed.");
    println(getIndent() + 1, status.toString());
    setIndent(1);
  }

  public void reportDodge(ReportSkillRoll pReport) {
    StringBuilder status = new StringBuilder();
    StringBuilder neededRoll = null;
    Game game = getClient().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (pReport.getRoll() > 0) {
      status.append("Dodge Roll [ ").append(pReport.getRoll()).append(" ]");
    } else {
      status.append("New Dodge Result");
    }
    println(getIndent(), TextStyle.ROLL, status.toString());
    if (!pReport.isReRolled()) {
      if (pReport.hasModifier(DodgeModifier.STUNTY)) {
        print(getIndent() + 1, false, actingPlayer.getPlayer());
        println(getIndent() + 1, " is Stunty and ignores tacklezones.");
      }
      if (pReport.hasModifier(DodgeModifier.BREAK_TACKLE)) {
        print(getIndent() + 1, false, actingPlayer.getPlayer());
        println(getIndent() + 1, " uses Break Tackle to break free.");
      }
    }
    print(getIndent() + 1, false, actingPlayer.getPlayer());
    if (pReport.isSuccessful()) {
      status = new StringBuilder();
      status.append(" dodges successfully.");
      println(getIndent() + 1, status.toString());
      if (!pReport.isReRolled() && fShowModifiersOnSuccess) {
        neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(pReport.getMinimumRoll()).append("+");
      }
    } else {
      println(getIndent() + 1, " trips while dodging.");
      if (!pReport.isReRolled() && fShowModifiersOnFailure) {
        neededRoll = new StringBuilder().append("Roll a ").append(pReport.getMinimumRoll()).append("+ to succeed");
      }
    }
    if (neededRoll != null) {
      if (pReport.hasModifier(DodgeModifier.BREAK_TACKLE)) {
        neededRoll.append(" using Break Tackle (ST ").append(Math.min(6, actingPlayer.getStrength()));
      } else {
        neededRoll.append(" (AG ").append(Math.min(6, actingPlayer.getPlayer().getAgility()));
      }
      neededRoll.append(" + 1 Dodge").append(formatRollModifiers(pReport.getModifiers())).append(" + Roll > 6).");
      println(getIndent() + 1, TextStyle.NEEDED_ROLL, neededRoll.toString());
    }
  }
  
  public void reportThrowTeamMateRoll(ReportThrowTeamMateRoll pReport) {
    StringBuilder status = new StringBuilder();
    StringBuilder neededRoll = null;
    Game game = getClient().getGame();
    Player thrower = game.getActingPlayer().getPlayer();
    Player thrownPlayer = game.getPlayerById(pReport.getThrownPlayerId());
    if (!pReport.isReRolled()) {
      print(getIndent(), true, thrower);
      print(getIndent(), TextStyle.BOLD, " tries to throw ");
      print(getIndent(), true, thrownPlayer);
      println(getIndent(), TextStyle.BOLD, ":");
    }
    if (pReport.hasModifier(PassModifier.NERVES_OF_STEEL)) {
      Player player = getClient().getGame().getActingPlayer().getPlayer();
      reportNervesOfSteel(player, "pass");
    }
    status.append("Throw Team-Mate Roll [ ").append(pReport.getRoll()).append(" ]");
    println(getIndent() + 1, TextStyle.ROLL, status.toString());
    print(getIndent() + 2, false, thrower);
    if (pReport.isSuccessful()) {
      status = new StringBuilder();
      status.append(" throws ").append(thrower.getGender().getGenitive()).append(" team-mate successfully.");
      println(getIndent() + 2, status.toString());
    } else {
      println(getIndent() + 2, " fumbles the throw.");
    }
    if (pReport.isSuccessful() && !pReport.isReRolled() && fShowModifiersOnSuccess) {
      neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(pReport.getMinimumRoll()).append("+ to avoid a fumble");
    }
    if (!pReport.isSuccessful() && !pReport.isReRolled() && fShowModifiersOnFailure) {
      neededRoll = new StringBuilder().append("Roll a ").append(pReport.getMinimumRoll()).append("+ to avoid a fumble");
    }
    if (neededRoll != null) {
      neededRoll.append(" (Roll ");
      PassingDistance passingDistance = pReport.getPassingDistance();
      if (passingDistance.getModifier() >= 0) {
        neededRoll.append(" + ");
      } else {
        neededRoll.append(" - ");
      }
      neededRoll.append(Math.abs(passingDistance.getModifier())).append(" ").append(passingDistance.getName());
      neededRoll.append(formatRollModifiers(pReport.getModifiers())).append(" > 1).");
      println(getIndent() + 2, TextStyle.NEEDED_ROLL, neededRoll.toString());
    }
    setIndent(getIndent() + 1);
  }
  
  public void reportScatterPlayer(ReportScatterPlayer pReport) {
    int[] rolls = pReport.getRolls();
    if (ArrayTool.isProvided(rolls)) {
      StringBuilder status = new StringBuilder();
      if (rolls.length > 1) {
        status.append("Scatter Rolls [ ");  
      } else {
        status.append("Scatter Roll [ ");        
      }
      for (int i = 0; i < rolls.length; i++) {
        if (i > 0) {
          status.append(", ");
        }
        status.append(rolls[i]);
      }
      status.append(" ] ");
      Direction[] directions = pReport.getDirections();
      for (int i = 0; i < directions.length; i++) {
        if (i > 0) {
          status.append(", ");
        }
        status.append(directions[i].getName());
      }
      println(getIndent(), TextStyle.ROLL, status.toString());
      status = new StringBuilder();
      status.append("Player scatters from square (");
      status.append(pReport.getStartCoordinate().getX()).append(",").append(pReport.getStartCoordinate().getY());
      status.append(") to square (");
      status.append(pReport.getEndCoordinate().getX()).append(",").append(pReport.getEndCoordinate().getY());
      status.append(").");
      println(getIndent() + 1, status.toString());
    }
  }

  public void reportAlwaysHungry(ReportSkillRoll pReport) {
    Game game = getClient().getGame();
    Player thrower = game.getActingPlayer().getPlayer();
    StringBuilder status = new StringBuilder();
    status.append("Always Hungry Roll [ ").append(pReport.getRoll()).append(" ]");
    println(getIndent(), TextStyle.ROLL, status.toString());
    print(getIndent() + 1, false, thrower);
    status = new StringBuilder();
    if (pReport.isSuccessful()) {
      status.append(" resists the hunger.");
    } else {
      status.append(" tries to eat ").append(thrower.getGender().getGenitive()).append(" team-mate.");
    }
    println(getIndent() + 1, TextStyle.NONE, status.toString());
  }
  
  public void reportBribes(ReportBribesRoll pReport) {
    Game game = getClient().getGame();
    Player player = game.getPlayerById(pReport.getPlayerId()); 
    StringBuilder status = new StringBuilder();
    status.append("Bribes Roll [ ").append(pReport.getRoll()).append(" ]");
    println(getIndent(), TextStyle.ROLL, status.toString());
    if (pReport.isSuccessful()) {
      print(getIndent() + 1, TextStyle.NONE, "The ref refrains from penalizing ");
      print(getIndent() + 1, false, player);
      status = new StringBuilder();
      status.append(" and ").append(player.getGender().getNominative()).append(" remains in the game.");
      println(getIndent() + 1, TextStyle.NONE, status.toString());
    } else {
      print(getIndent() + 1, TextStyle.NONE, "The ref appears to be unimpressed and ");
      print(getIndent() + 1, false, player);
      println(getIndent() + 1, TextStyle.NONE, " must leave the game.");
    }
  }
  
  public void reportEscape(ReportSkillRoll pReport) {
    Game game = getClient().getGame();
    Player thrownPlayer = game.getPlayerById(pReport.getPlayerId());
    StringBuilder status = new StringBuilder();
    status.append("Escape Roll [ ").append(pReport.getRoll()).append(" ]");
    println(getIndent(), TextStyle.ROLL, status.toString());
    status = new StringBuilder();
    print(getIndent() + 1, false, thrownPlayer);
    if (pReport.isSuccessful()) {
      status.append(" manages to wriggle free.");
    } else {
      status.append(" disappears in ").append(thrownPlayer.getGender().getGenitive()).append(" team-mate's stomach.");
    }
    println(getIndent() + 1, TextStyle.NONE, status.toString());
  }

  public void reportLeap(ReportSkillRoll pReport) {
    StringBuilder status = new StringBuilder();
    StringBuilder neededRoll = null;
    Game game = getClient().getGame();
    Player player = game.getActingPlayer().getPlayer();
    status.append("Leap Roll [ ").append(pReport.getRoll()).append(" ]");
    println(getIndent(), TextStyle.ROLL, status.toString());
    print(getIndent() + 1, false, player);
    if (pReport.isSuccessful()) {
      status = new StringBuilder();
      status.append(" leaps over ").append(player.getGender().getGenitive()).append(" opponents.");
      println(getIndent() + 1, status.toString());
      if (!pReport.isReRolled() && fShowModifiersOnSuccess) {
        neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(pReport.getMinimumRoll()).append("+");
      }
    } else {
      println(getIndent() + 1, " trips while leaping.");
      if (!pReport.isReRolled() && fShowModifiersOnFailure) {
        neededRoll = new StringBuilder().append("Roll a ").append(pReport.getMinimumRoll()).append("+ to succeed");
      }
    }
    if (neededRoll != null) {
      neededRoll.append(" (AG ").append(Math.min(6, player.getAgility())).append(formatRollModifiers(pReport.getModifiers())).append(" + Roll > 6).");
      println(getIndent() + 1, TextStyle.NEEDED_ROLL, neededRoll.toString());
    }
  }

  public void reportBiteSpectator(ReportBiteSpectator pReport) {
    Game game = getClient().getGame();
    Player player = game.getPlayerById(pReport.getPlayerId());
    if (player != null) {
      print(getIndent(), true, player);
      println(getIndent(), TextStyle.BOLD, " heads off to the spectator ranks to bite some beautiful maiden.");
    }
  }
  
  public void reportJumpUp(ReportSkillRoll pReport) {
    StringBuilder status = new StringBuilder();
    StringBuilder neededRoll = null;
    Game game = getClient().getGame();
    Player player = game.getActingPlayer().getPlayer();
    status.append("Jump Up Roll [ ").append(pReport.getRoll()).append(" ]");
    println(getIndent(), TextStyle.ROLL, status.toString());
    print(getIndent() + 1, false, player);
    if (pReport.isSuccessful()) {
      status = new StringBuilder();
      status.append(" jumps up to block ").append(player.getGender().getGenitive()).append(" opponent.");
      println(getIndent() + 1, status.toString());
      if (!pReport.isReRolled() && fShowModifiersOnSuccess) {
        neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(pReport.getMinimumRoll()).append("+");
      }
    } else {
      status = new StringBuilder();
      status.append(" doesn't get to ").append(player.getGender().getGenitive()).append(" feet.");
      println(getIndent() + 1, status.toString());
      status = new StringBuilder();
      if (!pReport.isReRolled() && fShowModifiersOnFailure) {
        neededRoll = new StringBuilder().append("Roll a ").append(pReport.getMinimumRoll()).append("+ to succeed");
      }
    }
    if (neededRoll != null) {
      neededRoll.append(" (AG ").append(Math.min(6, player.getAgility())).append(formatRollModifiers(pReport.getModifiers())).append(" + Roll > 6).");
      println(getIndent() + 1, TextStyle.NEEDED_ROLL, neededRoll.toString());
    }
  }
  
  public void reportStandUp(ReportStandUpRoll pReport) {
    StringBuilder status = new StringBuilder();
    StringBuilder neededRoll = null;
    Game game = getClient().getGame();
    Player player = game.getActingPlayer().getPlayer();
    status.append("Stand Up Roll [ ").append(pReport.getRoll()).append(" ]");
    println(getIndent(), TextStyle.ROLL, status.toString());
    print(getIndent() + 1, false, player);
    if (pReport.isSuccessful()) {
      status = new StringBuilder();
      status.append(" stands up.");
      println(getIndent() + 1, status.toString());
      if (!pReport.isReRolled() && fShowModifiersOnSuccess) {
        neededRoll = new StringBuilder().append("Succeeded on a roll of 4+.");
      }
    } else {
      status = new StringBuilder();
      status.append(" doesn't get to ").append(player.getGender().getGenitive()).append(" feet.");
      println(getIndent() + 1, status.toString());
      status = new StringBuilder();
      if (!pReport.isReRolled() && fShowModifiersOnFailure) {
        neededRoll = new StringBuilder().append("Roll a 4+ to succeed.");
      }
    }
    if (neededRoll != null) {
      println(getIndent() + 1, TextStyle.NEEDED_ROLL, neededRoll.toString());
    }
  }

  public void reportSafeThrow(ReportSkillRoll pReport) {
    StringBuilder status = new StringBuilder();
    StringBuilder neededRoll = null;
    Game game = getClient().getGame();
    Player player = game.getActingPlayer().getPlayer();
    status.append("Safe Throw Roll [ ").append(pReport.getRoll()).append(" ]");
    println(getIndent() + 1, TextStyle.ROLL, status.toString());
    print(getIndent() + 2, false, player);
    if (pReport.isSuccessful()) {
      println(getIndent() + 2, " throws safely over any interceptors.");
      if (!pReport.isReRolled() && fShowModifiersOnSuccess) {
        neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(pReport.getMinimumRoll()).append("+");
      }
    } else {
      println(getIndent() + 2, "'s Safe Throw fails to stop the interception.");
      if (!pReport.isReRolled() && fShowModifiersOnFailure) {
        neededRoll = new StringBuilder().append("Roll a ").append(pReport.getMinimumRoll()).append("+ to succeed");
      }
    }
    if (neededRoll != null) {
      neededRoll.append(" (AG ").append(Math.min(6, player.getAgility())).append(" + Roll > 6).");
      println(getIndent() + 2, TextStyle.NEEDED_ROLL, neededRoll.toString());
    }
  }
  
  public void reportBloodLust(ReportSkillRoll pReport) {
    StringBuilder status = new StringBuilder();
    StringBuilder neededRoll = null;
    Game game = getClient().getGame();
    Player player = game.getActingPlayer().getPlayer();
    status.append("Blood Lust Roll [ ").append(pReport.getRoll()).append(" ]");
    println(getIndent(), TextStyle.ROLL, status.toString());
    print(getIndent() + 1, false, player);
    if (pReport.isSuccessful()) {
      println(getIndent() + 1, " resists the Blood Lust.");
      if (!pReport.isReRolled() && fShowModifiersOnSuccess) {
        neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(pReport.getMinimumRoll()).append("+");
      }
    } else {
      println(getIndent() + 1, " gives in to the Blood Lust.");
      status = new StringBuilder();
      println(getIndent() + 1, "Player must feed at the end of the action or leave the pitch and suffer a turnover.");
      if (!pReport.isReRolled() && fShowModifiersOnFailure) {
        neededRoll = new StringBuilder().append("Roll a ").append(pReport.getMinimumRoll()).append("+ to succeed");
      }
    }
    if (neededRoll != null) {
      println(getIndent() + 1, TextStyle.NEEDED_ROLL, neededRoll.toString());
    }
  }

  public void reportAnimosity(ReportSkillRoll pReport) {
    StringBuilder status = new StringBuilder();
    StringBuilder neededRoll = null;
    Game game = getClient().getGame();
    Player player = game.getActingPlayer().getPlayer();
    status.append("Animosity Roll [ ").append(pReport.getRoll()).append(" ]");
    println(getIndent(), TextStyle.ROLL, status.toString());
    print(getIndent() + 1, false, player);
    if (pReport.isSuccessful()) {
      status = new StringBuilder();
      status.append(" resists ").append(player.getGender().getGenitive()).append(" Animosity.");
      println(getIndent() + 1, status.toString());
      if (!pReport.isReRolled() && fShowModifiersOnSuccess) {
        neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(pReport.getMinimumRoll()).append("+");
      }
    } else {
      status = new StringBuilder();
      status.append(" gives in to ").append(player.getGender().getGenitive()).append(" Animosity.");
      println(getIndent() + 1, status.toString());
      status = new StringBuilder();
      if (!pReport.isReRolled() && fShowModifiersOnFailure) {
        neededRoll = new StringBuilder().append("Roll a ").append(pReport.getMinimumRoll()).append("+ to succeed");
      }
    }
    if (neededRoll != null) {
      println(getIndent() + 1, TextStyle.NEEDED_ROLL, neededRoll.toString());
    }
  }

  public void reportRightStuff(ReportSkillRoll pReport) {
    StringBuilder status = new StringBuilder();
    StringBuilder neededRoll = null;
    Game game = getClient().getGame();
    status.append("Right Stuff Roll [ ").append(pReport.getRoll()).append(" ]");
    println(getIndent(), TextStyle.ROLL, status.toString());
    Player thrownPlayer = game.getPlayerById(pReport.getPlayerId());
    print(getIndent() + 1, false, thrownPlayer);
    if (pReport.isSuccessful()) {
      status = new StringBuilder();
      status.append(" lands on ").append(thrownPlayer.getGender().getGenitive()).append(" feet.");
      println(getIndent() + 1, status.toString());
      if (!pReport.isReRolled() && fShowModifiersOnSuccess) {
        neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(pReport.getMinimumRoll()).append("+");
      }
    } else {
      println(getIndent() + 1, " crashes to the ground.");
      status = new StringBuilder();
      if (!pReport.isReRolled() && fShowModifiersOnFailure) {
        neededRoll = new StringBuilder().append("Roll a ").append(pReport.getMinimumRoll()).append("+ to succeed");
      }
    }
    if (neededRoll != null) {
      neededRoll.append(" (AG ").append(Math.min(6, thrownPlayer.getAgility())).append(formatRollModifiers(pReport.getModifiers())).append(" + Roll > 6).");
      println(getIndent() + 1, TextStyle.NEEDED_ROLL, neededRoll.toString());
    }
  }
  
  public void reportConfusion(ReportConfusionRoll pReport) {
    if (pReport.getConfusionSkill() != null) {
      StringBuilder status = new StringBuilder();
      StringBuilder neededRoll = null;
      Game game = getClient().getGame();
      Player player = game.getActingPlayer().getPlayer();
      status.append(pReport.getConfusionSkill().getName()).append(" Roll [ ").append(pReport.getRoll()).append(" ]");
      println(getIndent(), TextStyle.ROLL, status.toString());
      print(getIndent() + 1, false, player);
      if (pReport.isSuccessful()) {
        println(getIndent() + 1, " is able to act normally.");
        if (!pReport.isReRolled() && fShowModifiersOnSuccess) {
          neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(pReport.getMinimumRoll()).append("+");
        }
      } else {
        if (Skill.WILD_ANIMAL == pReport.getConfusionSkill()) {
          println(getIndent() + 1, " roars in rage.");
        } else if (Skill.TAKE_ROOT == pReport.getConfusionSkill()) {
          println(getIndent() + 1, " takes root.");
        } else {
          println(getIndent() + 1, " is confused.");
        }
        status = new StringBuilder();
        if (!pReport.isReRolled() && fShowModifiersOnFailure) {
          neededRoll = new StringBuilder().append("Roll a ").append(pReport.getMinimumRoll()).append("+ to succeed");
        }
      }
      if (neededRoll != null) {
        if (Skill.WILD_ANIMAL == pReport.getConfusionSkill()) {
          if (pReport.getMinimumRoll() > 2) {
            neededRoll.append(" (Wild Animal does not attack)");
          } else {
            neededRoll.append(" (Wild Animal does attack)");
          }
        }
        if (Skill.REALLY_STUPID == pReport.getConfusionSkill()) {
          if (pReport.getMinimumRoll() > 2) {
            neededRoll.append(" (Really Stupid player without assistance)");
          } else {
            neededRoll.append(" (Really Stupid player gets help from team-mates)");
          }
        }
        neededRoll.append(".");
        println(getIndent() + 1, TextStyle.NEEDED_ROLL, neededRoll.toString());
      }
    }
  }

  public void reportCatch(ReportCatchRoll pReport) {
    StringBuilder status = new StringBuilder();
    StringBuilder neededRoll = null;
    Game game = getClient().getGame();
    Player player = game.getPlayerById(pReport.getPlayerId());
    if (!pReport.isReRolled()) {
      print(getIndent(), true, player);
      if (pReport.isBomb()) {
      	println(getIndent(), TextStyle.BOLD, " tries to catch the bomb:");
      } else {
      	println(getIndent(), TextStyle.BOLD, " tries to catch the ball:");
      }
      if (pReport.hasModifier(CatchModifier.NERVES_OF_STEEL)) {
        reportNervesOfSteel(player, "catch");
      }
    }
    status.append("Catch Roll [ ").append(pReport.getRoll()).append(" ]");
    println(getIndent() + 1, TextStyle.ROLL, status.toString());
    print(getIndent() + 2, false, player);
    if (pReport.isSuccessful()) {
    	if (pReport.isBomb()) {
    		println(getIndent() + 2, " catches the bomb.");
    	} else {
    		println(getIndent() + 2, " catches the ball.");
    	}
      if (!pReport.isReRolled() && fShowModifiersOnSuccess) {
        neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(pReport.getMinimumRoll()).append("+");
      }
    } else {
    	if (pReport.isBomb()) {
    		println(getIndent() + 2, " drops the bomb.");
    	} else {
    		println(getIndent() + 2, " drops the ball.");
    	}
      if (!pReport.isReRolled() && fShowModifiersOnFailure) {
        neededRoll = new StringBuilder().append("Roll a ").append(pReport.getMinimumRoll()).append("+ to succeed");
      }
    }
    if (neededRoll != null) {
      neededRoll.append(" (AG ").append(Math.min(6, player.getAgility())).append(formatRollModifiers(pReport.getModifiers())).append(" + Roll > 6).");
      println(getIndent() + 2, TextStyle.NEEDED_ROLL, neededRoll.toString());
    }
  }

  public void reportInterception(ReportInterceptionRoll pReport) {
    StringBuilder status = new StringBuilder();
    StringBuilder neededRoll = null;
    Game game = getClient().getGame();
    Player player = game.getPlayerById(pReport.getPlayerId());
    if (!pReport.isReRolled()) {
      print(getIndent(), true, player);
      if (pReport.isBomb()) {
      	println(getIndent(), TextStyle.BOLD, " tries to intercept the bomb:");
      } else {
      	println(getIndent(), TextStyle.BOLD, " tries to intercept the ball:");
      }
      if (pReport.hasModifier(InterceptionModifier.NERVES_OF_STEEL)) {
        reportNervesOfSteel(player, "intercept");
      }
    }
    status.append("Interception Roll [ ").append(pReport.getRoll()).append(" ]");
    println(getIndent() + 1, TextStyle.ROLL, status.toString());
    print(getIndent() + 2, false, player);
    if (pReport.isSuccessful()) {
    	if (pReport.isBomb()) {
    		println(getIndent() + 2, " intercepts the bomb.");
    	} else {
    		println(getIndent() + 2, " intercepts the ball.");
    	}
      if (!pReport.isReRolled() && fShowModifiersOnSuccess) {
        neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(pReport.getMinimumRoll()).append("+");
      }
    } else {
    	if (pReport.isBomb()) {
    		println(getIndent() + 2, " fails to intercept the bomb.");
    	} else {
    		println(getIndent() + 2, " fails to intercept the ball.");
    	}
      if (!pReport.isReRolled() && fShowModifiersOnFailure) {
        neededRoll = new StringBuilder().append("Roll a ").append(pReport.getMinimumRoll()).append("+ to succeed");
      }
    }
    if (neededRoll != null) {
      neededRoll.append(" (AG ").append(Math.min(6, player.getAgility())).append(" - 2 Interception").append(formatRollModifiers(pReport.getModifiers())).append(" + Roll > 6).");
      println(getIndent() + 2, TextStyle.NEEDED_ROLL, neededRoll.toString());
    }
  }

  public void reportHypnoticGaze(ReportSkillRoll pReport) {
    StringBuilder status = new StringBuilder();
    StringBuilder neededRoll = null;
    Game game = getClient().getGame();
    Player player = game.getActingPlayer().getPlayer();
    if (!pReport.isReRolled()) {
      print(getIndent(), true, player);
      print(getIndent(), TextStyle.BOLD, " gazes upon ");
      print(getIndent(), true, game.getDefender());
      println(getIndent(), TextStyle.BOLD, ":");
    }
    status.append("Hypnotic Gaze Roll [ ").append(pReport.getRoll()).append(" ]");
    println(getIndent() + 1, TextStyle.ROLL, status.toString());
    print(getIndent() + 2, false, player);
    status = new StringBuilder();
    if (pReport.isSuccessful()) {
      status.append(" hypnotizes ").append(player.getGender().getGenitive()).append(" victim.");
      println(getIndent() + 2, status.toString());
      if (!pReport.isReRolled() && fShowModifiersOnSuccess) {
        neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(pReport.getMinimumRoll()).append("+");
      }
    } else {
      status.append(" fails to affect ").append(player.getGender().getGenitive()).append(" victim.");
      println(getIndent() + 2, status.toString());
      if (!pReport.isReRolled() && fShowModifiersOnFailure) {
        neededRoll = new StringBuilder().append("Roll a ").append(pReport.getMinimumRoll()).append("+ to succeed");
      }
    }
    if (neededRoll != null) {
      neededRoll.append(" (AG ").append(Math.min(6, player.getAgility())).append(formatRollModifiers(pReport.getModifiers())).append(" + Roll > 6).");
      println(getIndent() + 2, TextStyle.NEEDED_ROLL, neededRoll.toString());
    }
  }

  public void reportPickup(ReportSkillRoll pReport) {
    StringBuilder status = new StringBuilder();
    StringBuilder neededRoll = null;
    Game game = getClient().getGame();
    Player player = game.getActingPlayer().getPlayer();
    if (!pReport.isReRolled()) {
      print(getIndent(), true, player);
      println(getIndent(), TextStyle.BOLD, " tries to pick up the ball:");
      if (pReport.hasModifier(PickupModifier.BIG_HAND)) {
        print(getIndent() + 1, false, player);
        println(getIndent() + 1, " is using Big Hand to ignore any tacklezones on the ball.");
      }
    }
    status.append("Pickup Roll [ ").append(pReport.getRoll()).append(" ]");
    println(getIndent() + 1, TextStyle.ROLL, status.toString());
    print(getIndent() + 2, false, player);
    if (pReport.isSuccessful()) {
      println(getIndent() + 2, " picks up the ball.");
      if (!pReport.isReRolled() && fShowModifiersOnSuccess) {
        neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(pReport.getMinimumRoll()).append("+");
      }
    } else {
      println(getIndent() + 2, " drops the ball.");
      if (!pReport.isReRolled() && fShowModifiersOnFailure) {
        neededRoll = new StringBuilder().append("Roll a ").append(pReport.getMinimumRoll()).append("+ to succeed");
      }
    }
    if (neededRoll != null) {
      neededRoll.append(" (AG ").append(Math.min(6, player.getAgility())).append(" + 1 Pickup").append(formatRollModifiers(pReport.getModifiers())).append(" + Roll > 6).");
      println(getIndent() + 2, TextStyle.NEEDED_ROLL, neededRoll.toString());
    }
  }

  public void reportTentaclesShadowingRoll(ReportTentaclesShadowingRoll pReport) {
    StringBuilder status = null;
    StringBuilder neededRoll = null;
    Game game = getClient().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    Player defender = game.getPlayerById(pReport.getDefenderId());
    if (!pReport.isReRolled()) {
      if (pReport.getSkill() == Skill.SHADOWING) {
        print(getIndent(), true, defender);
        print(getIndent(), TextStyle.BOLD, " tries to shadow ");
        print(getIndent(), true, actingPlayer.getPlayer());
        println(getIndent(), TextStyle.BOLD, ":");
      }
      if (pReport.getSkill() == Skill.TENTACLES) {
        status = new StringBuilder();
        print(getIndent(), true, defender);
        print(getIndent(), TextStyle.BOLD, " tries to hold ");
        print(getIndent(), true, actingPlayer.getPlayer());
        status.append(" with ").append(defender.getGender().getGenitive()).append(" tentacles:");
        println(getIndent(), TextStyle.BOLD, status.toString());
      }
    }
    int rolledTotal = 0;
    if (ArrayTool.isProvided(pReport.getRoll())) {
      rolledTotal = pReport.getRoll()[0] + pReport.getRoll()[1];
    }
    if (pReport.getSkill() == Skill.SHADOWING) {
      if (rolledTotal > 0) {
        status = new StringBuilder();
        status.append("Shadowing Escape Roll [ ").append(pReport.getRoll()[0]).append(" ][ ").append(pReport.getRoll()[1]).append(" ] = ").append(rolledTotal);
        println(getIndent() + 1, TextStyle.ROLL, status.toString());
      }
      status = new StringBuilder();
      if (pReport.isSuccessful()) {
        print(getIndent() + 2, false, actingPlayer.getPlayer());
        status.append(" escapes ").append(actingPlayer.getPlayer().getGender().getGenitive()).append(" opponent.");
        println(getIndent() + 2, status.toString());
        if (!pReport.isReRolled() && fShowModifiersOnSuccess) {
          neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(pReport.getMinimumRoll()).append("+");
        }
      } else {
        print(getIndent() + 2, false, defender);
        status.append(" shadows ").append(defender.getGender().getGenitive()).append(" opponent successfully.");
        println(getIndent() + 2, status.toString());
        if (!pReport.isReRolled() && fShowModifiersOnFailure) {
          neededRoll = new StringBuilder().append("Roll a ").append(pReport.getMinimumRoll()).append("+ to succeed");
        }
      }
      if (neededRoll != null) {
        neededRoll.append(" (MA ").append(actingPlayer.getPlayer().getMovement());
        neededRoll.append(" - MA ").append(defender.getMovement());
        neededRoll.append(" + Roll > 7).");
        println(getIndent() + 2, TextStyle.NEEDED_ROLL, neededRoll.toString());
      }
    }
    if (pReport.getSkill() == Skill.TENTACLES) {
      if (rolledTotal > 0) {
        status = new StringBuilder();
        status.append("Tentacles Escape Roll [ ").append(pReport.getRoll()[0]).append(" ][ ").append(pReport.getRoll()[1]).append(" ] = ").append(rolledTotal);
        println(getIndent() + 1, TextStyle.ROLL, status.toString());
      }
      status = new StringBuilder();
      if (pReport.isSuccessful()) {
        print(getIndent() + 2, false, actingPlayer.getPlayer());
        status.append(" escapes ").append(actingPlayer.getPlayer().getGender().getGenitive()).append(" opponent.");
        println(getIndent() + 2, status.toString());
        if (!pReport.isReRolled() && fShowModifiersOnSuccess) {
          neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(pReport.getMinimumRoll()).append("+");
        }
      } else {
        print(getIndent() + 2, false, defender);
        status.append(" holds ").append(defender.getGender().getGenitive()).append(" opponent successfully.");
        println(getIndent() + 2, status.toString());
        if (!pReport.isReRolled() && fShowModifiersOnFailure) {
          neededRoll = new StringBuilder().append("Roll a ").append(pReport.getMinimumRoll()).append("+ to succeed");
        }
      }
      if (neededRoll != null) {
        neededRoll.append(" (ST ").append(actingPlayer.getStrength());
        neededRoll.append(" - ST ").append(UtilCards.getPlayerStrength(game, defender));
        neededRoll.append(" + Roll > 5).");
        println(getIndent() + 2, TextStyle.NEEDED_ROLL, neededRoll.toString());
      }
    }
  }

  public void reportWeather(ReportWeather pReport) {
    int[] roll = pReport.getWeatherRoll();
    StringBuilder status = new StringBuilder();
    status.append("Weather Roll [ ").append(roll[0]).append(" ][ ").append(roll[1]).append(" ] ");
    println(getIndent(), TextStyle.ROLL, status.toString());
    Weather weather = pReport.getWeather();
    status = new StringBuilder();
    status.append("Weather is ").append(weather.getName());
    println(getIndent() + 1, status.toString());
    println(getIndent() + 1, TextStyle.EXPLANATION, weather.getDescription());
  }

  public void reportCoinThrow(ReportCoinThrow pReport) {
    setIndent(0);
    Game game = getClient().getGame();
    println(getIndent(), TextStyle.BOLD, "The referee throws the coin.");
    print(getIndent() + 1, "Coach ");
    if (game.getTeamHome().getCoach().equals(pReport.getCoach())) {
      print(getIndent() + 1, TextStyle.HOME, pReport.getCoach());
    } else {
      print(getIndent() + 1, TextStyle.AWAY, pReport.getCoach());
    }
    StringBuilder status = new StringBuilder();
    status.append(" chooses ").append(pReport.isCoinChoiceHeads() ? "HEADS." : "TAILS.");
    println(getIndent() + 1, status.toString());
    status = new StringBuilder();
    status.append("Coin throw is ");
    status.append(pReport.isCoinThrowHeads() ? "HEADS." : "TAILS.");
    println(getIndent() + 1, status.toString());
  }

  public void reportReceiveChoice(ReportReceiveChoice pReport) {
    Game game = getClient().getGame();
    print(getIndent() + 1, "Team ");
    printTeamName(game, false, pReport.getTeamId());
    StringBuilder status = new StringBuilder();
    status.append(" is ").append(pReport.isChoiceReceive() ? "receiving." : "kicking.");
    println(getIndent() + 1, status.toString());
  }

  public void reportPlayCard(ReportPlayCard pReport) {
    Game game = getClient().getGame();
    StringBuilder status = new StringBuilder();
    status.append("Card ").append(pReport.getCard().getName());
    if (StringTool.isProvided(pReport.getPlayerId())) {
    	status.append(" is played on ");
    } else {
    	status.append(" is played.");
    }
  	print(getIndent(), TextStyle.BOLD, status.toString());
    if (StringTool.isProvided(pReport.getPlayerId())) {
    	Player player = game.getPlayerById(pReport.getPlayerId());
    	print(getIndent(), true, player);
    	println(getIndent(), TextStyle.BOLD, ".");
    } else {
    	println();
    }
  }
  
  public void reportCardDeactivated(ReportCardDeactivated pReport) {
    StringBuilder status = new StringBuilder();
    status.append("Card ").append(pReport.getCard().getName());
    status.append(" effect ended.");
  	println(getIndent(), TextStyle.BOLD, status.toString());
  }

  public void reportHandOver(ReportHandOver pReport) {
    Game game = getClient().getGame();
    Player thrower = game.getActingPlayer().getPlayer();
    Player catcher = game.getPlayerById(pReport.getCatcherId());
    print(getIndent(), true, thrower);
    print(getIndent(), TextStyle.BOLD, " hands over the ball to ");
    print(getIndent(), true, catcher);
    println(getIndent(), TextStyle.BOLD, ":");
  }

  public void reportPass(ReportPassRoll pReport) {
    StringBuilder status = new StringBuilder();
    StringBuilder neededRoll = null;
    Game game = getClient().getGame();
    Player thrower = game.getPlayerById(pReport.getPlayerId());
    if (!pReport.isReRolled()) {
      print(getIndent(), true, thrower);
      Player catcher = game.getFieldModel().getPlayer(game.getPassCoordinate());
      if (pReport.isHailMaryPass()) {
      	if (pReport.isBomb()) {
      		println(getIndent(), TextStyle.BOLD, " throws a Hail Mary bomb:");
      	} else {
      		println(getIndent(), TextStyle.BOLD, " throws a Hail Mary pass:");
      	}
      } else if (catcher != null) {
      	if (pReport.isBomb()) {
      		print(getIndent(), TextStyle.BOLD, " throws a bomb at ");
      	} else {
      		print(getIndent(), TextStyle.BOLD, " passes the ball to ");
      	}
        print(getIndent(), true, catcher);
        println(getIndent(), TextStyle.BOLD, ":");
      } else {
      	if (pReport.isBomb()) {
      		println(getIndent(), TextStyle.BOLD, " throws a bomb to an empty field:");
      	} else {
      		println(getIndent(), TextStyle.BOLD, " passes the ball to an empty field:");
      	}
      }
    }
    if (pReport.hasModifier(PassModifier.NERVES_OF_STEEL)) {
      Player player = getClient().getGame().getActingPlayer().getPlayer();
      reportNervesOfSteel(player, "pass");
    }
    status.append("Pass Roll [ ").append(pReport.getRoll()).append(" ]");
    println(getIndent() + 1, TextStyle.ROLL, status.toString());
    print(getIndent() + 2, false, thrower);
    if (pReport.isSuccessful()) {
    	if (pReport.isBomb()) {
    		println(getIndent() + 2, " throws the bomb successfully.");
    	} else {
    		println(getIndent() + 2, " passes the ball.");
    	}
      if (!pReport.isReRolled() && fShowModifiersOnSuccess) {
        neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(pReport.getMinimumRoll()).append("+");
      }
    } else {
    	if (pReport.isHeldBySafeThrow()) {
      	println(getIndent() + 2, " holds on to the ball.");
      } else if (pReport.isFumble()) {
      	if (pReport.isBomb()) {
      		println(getIndent() + 2, " fumbles the bomb.");
      	} else {
      		println(getIndent() + 2, " fumbles the ball.");
      	}
      } else {
      	println(getIndent() + 2, " misses the throw.");
      }
      if (!pReport.isReRolled() && fShowModifiersOnFailure) {
        neededRoll = new StringBuilder().append("Roll a ").append(pReport.getMinimumRoll()).append("+ to succeed");
      }
    }
    if (neededRoll != null) {
    	if (!pReport.isHailMaryPass()) {
	      neededRoll.append(" (AG").append(Math.min(6, thrower.getAgility()));
	      PassingDistance passingDistance = pReport.getPassingDistance();
	      if (passingDistance.getModifier() >= 0) {
	        neededRoll.append(" + ");
	      } else {
	        neededRoll.append(" - ");
	      }
	      neededRoll.append(Math.abs(passingDistance.getModifier())).append(" ").append(passingDistance.getName());
	      neededRoll.append(formatRollModifiers(pReport.getModifiers())).append(" + Roll > 6).");
    	}
      println(getIndent() + 2, TextStyle.NEEDED_ROLL, neededRoll.toString());
    }
  }

  public void reportGameEnd() {
    
    setIndent(0);
    
    Game game = getClient().getGame();
    GameResult gameResult = game.getGameResult();
    int scoreDiffHome = gameResult.getTeamResultHome().getScore() - gameResult.getTeamResultAway().getScore();
    
    StringBuilder status = new StringBuilder();
    if (gameResult.getTeamResultHome().hasConceded()) {
      status.append("Coach ").append(game.getTeamHome().getCoach()).append(" concedes the game.");
      println(ParagraphStyle.SPACE_ABOVE_BELOW, TextStyle.TURN_HOME, status.toString());
    } else if (gameResult.getTeamResultAway().hasConceded()) {
      status.append("Coach ").append(game.getTeamAway().getCoach()).append(" concedes the game.");
      println(ParagraphStyle.SPACE_ABOVE_BELOW, TextStyle.TURN_AWAY, status.toString());
    } else if (scoreDiffHome > 0) {
      status.append(game.getTeamHome().getName()).append(" win the game.");
      println(ParagraphStyle.SPACE_ABOVE_BELOW, TextStyle.TURN_HOME, status.toString());
    } else if (scoreDiffHome < 0) {
      status.append(game.getTeamAway().getName()).append(" win the game.");
      println(ParagraphStyle.SPACE_ABOVE_BELOW, TextStyle.TURN_AWAY, status.toString());
    } else {
      status.append("The game ends in a tie.");
      println(ParagraphStyle.SPACE_ABOVE_BELOW, TextStyle.TURN, status.toString());
    }
        
  }
  
  public void reportMostValuablePlayers(ReportMostValuablePlayers pReport) {

    reportGameEnd();
    
    Game game = getClient().getGame();

    println(getIndent(), TextStyle.BOLD, "Most Valuable Players");
    
    for (String playerId : pReport.getPlayerIdsHome()) {
      Player player = game.getPlayerById(playerId);
      print(getIndent() + 1, TextStyle.NONE, "The jury voted ");
      print(getIndent() + 1, TextStyle.HOME, player.getName());
      print(getIndent() + 1, TextStyle.NONE, " the most valuable player of ");
      print(getIndent() + 1, TextStyle.NONE, player.getGender().getGenitive());
      println(getIndent() + 1, TextStyle.NONE, " team.");
    }

    for (String playerId : pReport.getPlayerIdsAway()) {
      Player player = game.getPlayerById(playerId);
      print(getIndent() + 1, TextStyle.NONE, "The jury voted ");
      print(getIndent() + 1, TextStyle.AWAY, player.getName());
      print(getIndent() + 1, TextStyle.NONE, " the most valuable player of ");
      print(getIndent() + 1, TextStyle.NONE, player.getGender().getGenitive());
      println(getIndent() + 1, TextStyle.NONE, " team.");
    }
    
  }
  
/*  public void reportSpirallingExpenses(ReportSpirallingExpenses pReport) {
    Game game = getClient().getGame();
    if ((pReport.getExpensesHomeTeam() > 0) || (pReport.getExpensesAwayTeam() > 0)) {
      println(getIndent(), TextStyle.ROLL, "Spiralling Expenses");
      if (pReport.getExpensesHomeTeam() > 0) {
        print(getIndent() + 1, TextStyle.HOME, game.getTeamHome().getName());
        StringBuilder status = new StringBuilder();
        status.append(" must pay ").append(pReport.getExpensesHomeTeam()).append(" in expenses.");
        println(getIndent() + 1, TextStyle.NONE, status.toString());
      }
      if (pReport.getExpensesAwayTeam() > 0) {
        print(getIndent() + 1, TextStyle.AWAY, game.getTeamAway().getName());
        StringBuilder status = new StringBuilder();
        status.append(" must pay ").append(pReport.getExpensesAwayTeam()).append(" in expenses.");
        println(getIndent() + 1, TextStyle.NONE, status.toString());
      }
    }
  } */
  
  public void reportWinningsRoll(ReportWinningsRoll pReport) {
    
    Game game = getClient().getGame();
    if ((pReport.getWinningsRollAway() == 0) && (pReport.getWinningsRollHome() > 0)) {
      print(getIndent(), TextStyle.NONE, "Coach ");
      print(getIndent(), TextStyle.HOME, game.getTeamHome().getCoach());
      println(getIndent(), TextStyle.NONE, " re-rolls winnings.");
    }
    if ((pReport.getWinningsRollHome() == 0) && (pReport.getWinningsRollAway() > 0)) {
      print(getIndent(), TextStyle.NONE, "Coach ");
      print(getIndent(), TextStyle.AWAY, game.getTeamAway().getCoach());
      println(getIndent(), TextStyle.NONE, " re-rolls winnings.");
    }
    
    if (pReport.getWinningsRollHome() > 0) {
      StringBuilder status = new StringBuilder();
      status.append("Winnings Roll Home Team [ ").append(pReport.getWinningsRollHome()).append(" ]");
      println(getIndent(), TextStyle.ROLL, status.toString());
      print(getIndent() + 1, TextStyle.HOME, game.getTeamHome().getName());
      status = new StringBuilder();
      status.append(" earn ").append(StringTool.formatThousands(pReport.getWinningsHome())).append(" goldcoins.");
      println(getIndent() + 1, TextStyle.NONE, status.toString());
    }
    
    if (pReport.getWinningsRollAway() > 0) {
      StringBuilder status = new StringBuilder();
      status.append("Winnings Roll Away Team [ ").append(pReport.getWinningsRollAway()).append(" ]");
      println(getIndent(), TextStyle.ROLL, status.toString());
      print(getIndent() + 1, TextStyle.AWAY, game.getTeamAway().getName());
      status = new StringBuilder();
      status.append(" earn ").append(StringTool.formatThousands(pReport.getWinningsAway())).append(" in gold.");
      println(getIndent() + 1, TextStyle.NONE, status.toString());
    }
    
    if ((pReport.getWinningsRollHome() == 0) && (pReport.getWinningsRollAway() == 0)) {
      if (pReport.getWinningsHome() > 0) {
        println(getIndent(), TextStyle.BOLD, "Winnings: Concession of Away Team");
        print(getIndent() + 1, TextStyle.HOME, game.getTeamHome().getName());
        print(getIndent() + 1, TextStyle.NONE, " win ");
        print(getIndent() + 1, TextStyle.NONE, Integer.toString(pReport.getWinningsHome()));
        println(getIndent() + 1, TextStyle.NONE, " in gold.");
        print(getIndent() + 1, TextStyle.AWAY, game.getTeamAway().getName());
        println(getIndent() + 1, TextStyle.NONE, " get nothing.");
      }
      if (pReport.getWinningsAway() > 0) {
        println(getIndent(), TextStyle.BOLD, "Winnings: Concession of Home Team");
        print(getIndent() + 1, TextStyle.AWAY, game.getTeamAway().getName());
        print(getIndent() + 1, TextStyle.NONE, " win ");
        print(getIndent() + 1, TextStyle.NONE, Integer.toString(pReport.getWinningsAway()));
        println(getIndent() + 1, TextStyle.NONE, " in gold.");
        print(getIndent() + 1, TextStyle.HOME, game.getTeamHome().getName());
        println(getIndent() + 1, TextStyle.NONE, " get nothing.");
      }
    }
    
  }
  
  public void reportFanFactorRoll(ReportFanFactorRoll pReport) {

    Game game = getClient().getGame();

    StringBuilder status = new StringBuilder();
    if (ArrayTool.isProvided(pReport.getFanFactorRollHome())) {
      status.append("Fan Factor Roll Home Team ");
      int[] fanFactorRollHome = pReport.getFanFactorRollHome();
      for (int i = 0; i < fanFactorRollHome.length; i++) {
        status.append("[ ").append(fanFactorRollHome[i]).append(" ]");
      }
    } else {
      status.append("Fan Factor: Concession of Home Team");
    }
    println(getIndent(), TextStyle.ROLL, status.toString());
    status = new StringBuilder();
    status.append("FanFactor ").append(game.getTeamHome().getFanFactor());
    if (pReport.getFanFactorModifierHome() < 0) {
      status.append(" - ").append(Math.abs(pReport.getFanFactorModifierHome()));
    } else {
      status.append(" + ").append(pReport.getFanFactorModifierHome());
    }
    status.append(" = ").append(game.getTeamHome().getFanFactor() + pReport.getFanFactorModifierHome());
    println(getIndent() + 1, TextStyle.NONE, status.toString());
    print(getIndent() + 1, TextStyle.HOME, game.getTeamHome().getName());
    if (pReport.getFanFactorModifierHome() > 0) {
      println(getIndent() + 1, TextStyle.NONE, " win some new fans.");
    } else if (pReport.getFanFactorModifierHome() < 0) {
      println(getIndent() + 1, TextStyle.NONE, " lose some fans.");
    } else {
      println(getIndent() + 1, TextStyle.NONE, " keep their fans.");
    }
      
    status = new StringBuilder();
    if (ArrayTool.isProvided(pReport.getFanFactorRollAway())) {
      status.append("Fan Factor Roll Away Team ");
      int[] fanFactorRollAway = pReport.getFanFactorRollAway();
      for (int i = 0; i < fanFactorRollAway.length; i++) {
        status.append("[ ").append(fanFactorRollAway[i]).append(" ]");
      }
    } else {
      status.append("Fan Factor: Concession of Away Team");
    }
    println(getIndent(), TextStyle.ROLL, status.toString());
    status = new StringBuilder();
    status.append("FanFactor ").append(game.getTeamAway().getFanFactor());
    if (pReport.getFanFactorModifierAway() < 0) {
      status.append(" - ").append(Math.abs(pReport.getFanFactorModifierAway()));
    } else {
      status.append(" + ").append(pReport.getFanFactorModifierAway());
    }
    status.append(" = ").append(game.getTeamAway().getFanFactor() + pReport.getFanFactorModifierAway());
    println(getIndent() + 1, TextStyle.NONE, status.toString());
    print(getIndent() + 1, TextStyle.AWAY, game.getTeamAway().getName());
    if (pReport.getFanFactorModifierAway() > 0) {
      println(getIndent() + 1, TextStyle.NONE, " win some new fans.");
    } else if (pReport.getFanFactorModifierAway() < 0) {
      println(getIndent() + 1, TextStyle.NONE, " lose some fans.");
    } else {
      println(getIndent() + 1, TextStyle.NONE, " keep their fans.");
    }
    
    // System.out.println(game.getGameResult().toUploadXml(0));

  }
  
  public void reportNoPlayersToField(ReportNoPlayersToField pReport) {
  	setIndent(0);
    Game game = getClient().getGame();
    if (StringTool.isProvided(pReport.getTeamId())) {
    	StringBuilder status = new StringBuilder();
    	if (game.getTeamHome().getId().equals(pReport.getTeamId())) {
    		status.append(game.getTeamHome().getName()).append(" can field no players.");
    		println(ParagraphStyle.SPACE_ABOVE_BELOW, TextStyle.TURN_HOME, status.toString());
    	} else {
    		status.append(game.getTeamAway().getName()).append(" can field no players.");
    		println(ParagraphStyle.SPACE_ABOVE_BELOW, TextStyle.TURN_AWAY, status.toString());
    	}
    } else {
    	println(ParagraphStyle.SPACE_ABOVE_BELOW, TextStyle.TURN, "Both teams can field no players.");
    }
    if (StringTool.isProvided(pReport.getTeamId())) {
    	println(getIndent(), TextStyle.BOLD, "The opposing team is awarded a touchdown.");
    }
  	println(ParagraphStyle.SPACE_BELOW, TextStyle.BOLD, "The turn counter is advanced 2 steps.");
  }
  
  public void reportTurnEnd(ReportTurnEnd pReport) {
    setIndent(0);
    Game game = getClient().getGame();
    Player touchdownPlayer = game.getPlayerById(pReport.getPlayerIdTouchdown());
    if (touchdownPlayer != null) {
      print(getIndent(), true, touchdownPlayer);
      println(getIndent() + 1, TextStyle.BOLD, " scores a touchdown.");
    }
    KnockoutRecovery[] knockoutRecoveries = pReport.getKnockoutRecoveries();
    if (ArrayTool.isProvided(knockoutRecoveries)) {
      for (KnockoutRecovery knockoutRecovery : knockoutRecoveries) {
        StringBuilder status = new StringBuilder();
        status.append("Knockout Recovery Roll [ ").append(knockoutRecovery.getRoll()).append(" ] ");
        if (knockoutRecovery.getBloodweiserBabes() > 0) {
          status.append(" + ").append(knockoutRecovery.getBloodweiserBabes()).append(" Bloodweiser Babes");
        }
        println(getIndent(), TextStyle.ROLL, status.toString());
        Player player = game.getPlayerById(knockoutRecovery.getPlayerId());
        print(getIndent() + 1, false, player);
        if (knockoutRecovery.isRecovering()) {
          println(getIndent() + 1, " is regaining consciousness.");
        } else {
          println(getIndent() + 1, " stays unconscious.");
        }
      }
    }
    HeatExhaustion[] heatExhaustions = pReport.getHeatExhaustions();
    if (ArrayTool.isProvided(heatExhaustions)) {
      for (HeatExhaustion heatExhaustion : heatExhaustions) {
        StringBuilder status = new StringBuilder();
        status.append("Heat Exhaustion Roll [ ").append(heatExhaustion.getRoll()).append(" ] ");
        println(getIndent(), TextStyle.ROLL, status.toString());
        Player player = game.getPlayerById(heatExhaustion.getPlayerId());
        print(getIndent() + 1, false, player);
        if (heatExhaustion.isExhausted()) {
          println(getIndent() + 1, " is suffering from heat exhaustion.");
        } else {
          println(getIndent() + 1, " is unaffected.");
        }
      }
    }
    if (TurnMode.REGULAR == game.getTurnMode()) {
      StringBuilder status = new StringBuilder();
      if (game.isHomePlaying()) {
        status.append(game.getTeamHome().getName()).append(" start turn ").append(game.getTurnDataHome().getTurnNr()).append(".");
        println(ParagraphStyle.SPACE_ABOVE_BELOW, TextStyle.TURN_HOME, status.toString());
      } else {
        status.append(game.getTeamAway().getName()).append(" start turn ").append(game.getTurnDataAway().getTurnNr()).append(".");
        println(ParagraphStyle.SPACE_ABOVE_BELOW, TextStyle.TURN_AWAY, status.toString());
      }
    }
  }
  
  public void reportBlock(ReportBlock pReport) {
    
    Game game = getClient().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    Player attacker = actingPlayer.getPlayer();
    Player defender = game.getPlayerById(pReport.getDefenderId());

    print(getIndent(), true, attacker);
    if (actingPlayer.getPlayerAction() == PlayerAction.BLITZ) {
      print(getIndent(), TextStyle.BOLD, " blitzes ");
    } else {
      print(getIndent(), TextStyle.BOLD, " blocks ");
    }
    print(getIndent(), true, defender);
    println(getIndent(), TextStyle.BOLD, ":");
    setIndent(getIndent() + 1);
    
  }
  
  public void reportBlockRoll(ReportBlockRoll pReport) {
    if (ArrayTool.isProvided(pReport.getBlockRoll()))// && ((game.isHomePlaying() && game.getTeamHome().getId().equals(pReport.getChoosingTeamId())) || (!game.isHomePlaying() && game.getTeamAway().getId().equals(pReport.getChoosingTeamId())))) {
    {   StringBuilder status = new StringBuilder();
      status.append("Block Roll");
      BlockResultFactory blockResultFactory = new BlockResultFactory();
      for (int i = 0; i < pReport.getBlockRoll().length; i++) {
        BlockResult blockResult = blockResultFactory.forRoll(pReport.getBlockRoll()[i]);
        status.append(" [ ").append(blockResult.getName()).append(" ]");
      }
      println(getIndent(), TextStyle.ROLL, status.toString());
    }
  }

  public void reportBlockChoice(ReportBlockChoice pReport) {
    StringBuilder status = new StringBuilder();
    status.append("Block Result [ ").append(pReport.getBlockResult().getName()).append(" ]");
    println(getIndent(), TextStyle.ROLL, status.toString());
    Game game = getClient().getGame();
    Player attacker = game.getActingPlayer().getPlayer();
    Player defender = game.getPlayerById(pReport.getDefenderId());
    switch (pReport.getBlockResult()) {
      case BOTH_DOWN:
        if (UtilCards.hasSkill(game, attacker, Skill.BLOCK)) {
          print(getIndent() + 1, false, attacker);
          status = new StringBuilder();
          status.append(" has been saved by ").append(attacker.getGender().getGenitive()).append(" Block skill.");
          println(getIndent() + 1, status.toString());
        }
        if (UtilCards.hasSkill(game, defender, Skill.BLOCK)) {
          print(getIndent() + 1, false, defender);
          status = new StringBuilder();
          status.append(" has been saved by ").append(defender.getGender().getGenitive()).append(" Block skill.");
          println(getIndent() + 1, status.toString());
        }
        break;
      case POW_PUSHBACK:
        if (UtilCards.hasSkill(game, defender, Skill.DODGE) && UtilCards.hasSkill(game, attacker, Skill.TACKLE)) {
          print(getIndent() + 1, false, attacker);
          println(getIndent() + 1, " uses Tackle to bring opponent down.");
        }
        break;
      default:
      	break;
    }
  }

  public void reportPushback(ReportPushback pReport) {
    Game game = getClient().getGame();
    int indent = getIndent() + 1;
    StringBuilder status = new StringBuilder();
    Player defender = game.getPlayerById(pReport.getDefenderId());
    if (pReport.getMode() == PushbackMode.SIDE_STEP) {
      print(indent, false, defender);
      status.append(" uses Side Step to avoid being pushed.");
      println(indent, status.toString());
    }
    if (pReport.getMode() == PushbackMode.GRAB) {
      ActingPlayer actingPlayer = game.getActingPlayer();
      print(indent, false, actingPlayer.getPlayer());
      status.append(" uses Grab to place ").append(actingPlayer.getPlayer().getGender().getGenitive()).append(" opponent.");
      println(indent, status.toString());
    }
  }
  
  public void reportRegeneration(ReportSkillRoll pReport) {
    if (pReport.getRoll() > 0) {
      StringBuilder status = new StringBuilder();
      status.append("Regeneration Roll [ ").append(pReport.getRoll()).append(" ]");
      println(getIndent(), TextStyle.ROLL, status.toString());
      Player player = getClient().getGame().getPlayerById(pReport.getPlayerId());
      print(getIndent() + 1, false, player);
      if (pReport.isSuccessful()) {
        println(getIndent() + 1, " regenerates.");
      } else {
        println(getIndent() + 1, " does not regenerate.");
      }
    }
  }

  public void reportInjury(ReportInjury pReport) {
    
    Game game = getClient().getGame();
    Player defender = game.getPlayerById(pReport.getDefenderId());
    Player attacker = game.getPlayerById(pReport.getAttackerId());
    StringBuilder status = new StringBuilder();
    
    // report injury type
    
    switch (pReport.getInjuryType()) {
      case CROWDPUSH:
        print(getIndent() + 1, false, defender);
        println(getIndent() + 1, " is pushed into the crowd.");
        break;
      case STAB:
      	if (attacker != null) {
      		print(getIndent(), true, attacker);
          print(getIndent(), TextStyle.BOLD, " stabs ");
          print(getIndent(), true, defender);
      	} else {
          print(getIndent(), true, defender);
      		print(getIndent(), TextStyle.BOLD, " is stabbed");
      	}
        println(getIndent(), TextStyle.BOLD, ":");
        setIndent(getIndent() + 1);
        break;
      case BITTEN:
        print(getIndent(), true, attacker);
        print(getIndent(), TextStyle.BOLD, " bites ");
        print(getIndent(), true, defender);
        println(getIndent(), TextStyle.BOLD, ":");
        setIndent(getIndent() + 1);
        break;
      default:
      	break;
    }

    // report armour roll
    
    int[] armorRoll = pReport.getArmorRoll();
    if (ArrayTool.isProvided(armorRoll)) {
      status.append("Armour Roll [ ").append(armorRoll[0]).append(" ][ ").append(armorRoll[1]).append(" ]");
      println(getIndent(), TextStyle.ROLL, status.toString());
      status = new StringBuilder();
      int rolledTotal = armorRoll[0] + armorRoll[1];
      status.append("Rolled Total of ").append(rolledTotal);
      int armorModifierTotal = 0;
      boolean usingClaws = false;
      for (ArmorModifier armorModifier : pReport.getArmorModifiers()) {
        usingClaws |= (armorModifier == ArmorModifier.CLAWS);
        if (armorModifier.getModifier() != 0) {
          armorModifierTotal += armorModifier.getModifier();
          if (armorModifier.getModifier() > 0) {
            status.append(" + ");
          } else {
            status.append(" - ");
          }
          if (!armorModifier.isFoulAssistModifier()) {
            status.append(Math.abs(armorModifier.getModifier())).append(" ");
          }
          status.append(armorModifier.getName());
        }
      }
      if (armorModifierTotal != 0) {
        status.append(" = ").append(rolledTotal + armorModifierTotal);
      }
      println(getIndent() + 1, status.toString());
      if ((attacker != null) && usingClaws) {
        status = new StringBuilder();
        print(getIndent() + 1, false, attacker);
        println(getIndent() + 1, " uses Claws to reduce opponents armour to 7.");
      }
      status = new StringBuilder();
      if (pReport.isArmorBroken()) {
        print(getIndent() + 1, "The armour of ");
        print(getIndent() + 1, false, defender);
        println(getIndent() + 1, " has been broken.");
      } else {
        print(getIndent() + 1, false, defender);
        status = new StringBuilder();
        status.append(" has been saved by ").append(defender.getGender().getGenitive()).append(" armour.");
        println(getIndent() + 1, status.toString());
      }
    }

    // report injury roll
    if (pReport.isArmorBroken()) {
      boolean thickSkullUsed = false;
      boolean stuntyUsed = false;
      status = new StringBuilder();
      int[] injuryRoll = pReport.getInjuryRoll();
      if (ArrayTool.isProvided(injuryRoll)) {
        status.append("Injury Roll [ ").append(injuryRoll[0]).append(" ][ ").append(injuryRoll[1]).append(" ]");
        println(getIndent(), TextStyle.ROLL, status.toString());
        status = new StringBuilder();
        int rolledTotal = injuryRoll[0] + injuryRoll[1];
        status.append("Rolled Total of ").append(rolledTotal);
        int injuryModifierTotal = 0;
        for (InjuryModifier injuryModifier : pReport.getInjuryModifiers()) {
          injuryModifierTotal += injuryModifier.getModifier();
          if (injuryModifier.getModifier() == 0) {
            if (injuryModifier == InjuryModifier.THICK_SKULL) {
              thickSkullUsed = true;
            }
            if (injuryModifier == InjuryModifier.STUNTY) {
              stuntyUsed = true;
            }
          } else if (injuryModifier.isNigglingInjuryModifier()) {
            status.append(" +").append(injuryModifier.getName());
          } else if (injuryModifier.getModifier() > 0) {
            status.append(" +").append(injuryModifier.getModifier()).append(" ").append(injuryModifier.getName());
          } else {
            status.append(" ").append(injuryModifier.getModifier()).append(" ").append(injuryModifier.getName());
          }
        }
        if (injuryModifierTotal != 0) {
          status.append(" = ").append(rolledTotal + injuryModifierTotal);
        }
        println(getIndent() + 1, status.toString());
        if (stuntyUsed) {
          print(getIndent() + 1, false, defender);
          status = new StringBuilder();
          status.append(" is Stunty and more easily hurt because of that.");
          println(getIndent() + 1, status.toString());
        }
        if (thickSkullUsed) {
          print(getIndent() + 1, false, defender);
          status = new StringBuilder();
          status.append("'s Thick Skull helps ").append(defender.getGender().getDative()).append(" to stay on the pitch.");
          println(getIndent() + 1, status.toString());
        }
        if (ArrayTool.isProvided(pReport.getCasualtyRoll())) {
          print(getIndent() + 1, false, defender);
          println(getIndent() + 1, " suffers a casualty.");
          int[] casualtyRoll = pReport.getCasualtyRoll();
          status = new StringBuilder();
          status.append("Casualty Roll [ ").append(casualtyRoll[0]).append(" ][ ").append(casualtyRoll[1]).append(" ]");
          println(getIndent(), TextStyle.ROLL, status.toString());
          reportInjury(defender, pReport.getInjury(), pReport.getSeriousInjury());
          if (ArrayTool.isProvided(pReport.getCasualtyRollDecay())) {
            print(getIndent() + 1, false, defender);
            status = new StringBuilder();
            status.append("'s body is decaying and ").append(defender.getGender().getNominative()).append(" suffers a 2nd casualty.");
            println(getIndent() + 1, status.toString());
            status = new StringBuilder();
            int[] casualtyRollDecay = pReport.getCasualtyRollDecay();
            status.append("Casualty Roll [ ").append(casualtyRollDecay[0]).append(" ][ ").append(casualtyRollDecay[1]).append(" ]");
            println(getIndent(), TextStyle.ROLL, status.toString());
            reportInjury(defender, pReport.getInjuryDecay(), pReport.getSeriousInjuryDecay());
          }
        } else {
          reportInjury(defender, pReport.getInjury(), pReport.getSeriousInjury());
        }
      }
    }    
  }
  
  private void reportInjury(Player pDefender, PlayerState pInjury, SeriousInjury pSeriousInjury) {
    StringBuilder status = new StringBuilder();
    print(getIndent() + 1, false, pDefender);
    status.append(" ").append(pInjury.getDescription()).append(".");
    println(getIndent() + 1, status.toString());
    if (pSeriousInjury != null) {
      print(getIndent() + 1, false, pDefender);
      status = new StringBuilder();
      status.append(" ").append(pSeriousInjury.getDescription()).append(".");
      println(getIndent() + 1, status.toString());
    }
  }

  public void reportFoul(ReportFoul pReport) {
    Game game = getClient().getGame();
    Player attacker = game.getActingPlayer().getPlayer();
    Player defender = game.getPlayerById(pReport.getDefenderId());
    print(getIndent(), true, attacker);
    print(getIndent(), TextStyle.BOLD, " fouls ");
    print(getIndent(), true, defender);
    println(getIndent(), ":");
    setIndent(getIndent() + 1);
  }

  private void reportNervesOfSteel(Player pPlayer, String pDoWithTheBall) {
    if (pPlayer != null) {
      print(getIndent(), false, pPlayer);
      StringBuilder status = new StringBuilder();
      status.append(" is using Nerves of Steel to ").append(pDoWithTheBall).append(" the ball.");
      println(getIndent(), status.toString());
    }
  }
  
  public void reportPassBlock(ReportPassBlock pReport) {
    Game game = getClient().getGame();
    if (!pReport.isPassBlockAvailable()) {
    	TextStyle textStyle = game.getTeamHome().getId().equals(pReport.getTeamId()) ? TextStyle.HOME : TextStyle.AWAY;
   		println(getIndent(), textStyle, "No pass blockers in range to intercept.");
    }
  }
  
  // call before setting acting player
  public void reportPlayerAction(ReportPlayerAction pReport) {
    setIndent(0);
    Game game = getClient().getGame();
    Player player = game.getPlayerById(pReport.getActingPlayerId());
    PlayerAction playerAction = pReport.getPlayerAction();
    String actionDescription = (playerAction != null) ? playerAction.getDescription() : null;
    if ((player != null) && StringTool.isProvided(actionDescription)) {
      print(getIndent(), true, player);
      StringBuilder status = new StringBuilder();
      status.append(" ").append(actionDescription).append(".");
      println(getIndent(), TextStyle.BOLD, status.toString());
    }
    setIndent(getIndent() + 1);
  }
  
  public void reportApothecaryRoll(ReportApothecaryRoll pReport) {
    int[] casualtyRoll = pReport.getCasualtyRoll();
    if (ArrayTool.isProvided(casualtyRoll)) {
      println(getIndent(), TextStyle.BOLD, "Apothecary used.");
      Player player = getClient().getGame().getPlayerById(pReport.getPlayerId());
      StringBuilder status = new StringBuilder();
      status.append("Casualty Roll [ ").append(casualtyRoll[0]).append(" ][ ").append(casualtyRoll[1]).append(" ]");
      println(getIndent(), TextStyle.ROLL, status.toString());
      PlayerState injury = pReport.getPlayerState();
      print(getIndent() + 1, false, player);
      status = new StringBuilder();
      status.append(" ").append(injury.getDescription()).append(".");
      println(getIndent() + 1, status.toString());
      SeriousInjury seriousInjury = pReport.getSeriousInjury();
      if (seriousInjury != null) {
        print(getIndent() + 1, false, player);
        status = new StringBuilder();
        status.append(" ").append(seriousInjury.getDescription()).append(".");
        println(getIndent() + 1, status.toString());
      }
    }
  }

  public void reportApothecaryChoice(ReportApothecaryChoice pReport) {
    Game game = getClient().getGame();
    GameResult gameResult = game.getGameResult();
    Player player = game.getPlayerById(pReport.getPlayerId());
    if ((pReport.getPlayerState() != null) && (pReport.getPlayerState().getBase() == PlayerState.RESERVE)) {
      print(getIndent(), TextStyle.BOLD, "The apothecary patches ");
      print(getIndent(), true, player);
      StringBuilder status = new StringBuilder();
      status.append(" up so ").append(player.getGender().getNominative()).append(" is able to play again.");
      println(getIndent(), TextStyle.BOLD, status.toString());
    } else {
      print(getIndent(), "Coach ");
      if (game.getTeamHome().hasPlayer(player)) {
        print(getIndent(), TextStyle.HOME, game.getTeamHome().getCoach());
      } else {
        print(getIndent(), TextStyle.AWAY, game.getTeamAway().getCoach());
      }
      PlayerState playerStateOld = game.getFieldModel().getPlayerState(player);
      SeriousInjury seriousInjuryOld = gameResult.getPlayerResult(player).getSeriousInjury();
      if ((pReport.getPlayerState() != playerStateOld) || (pReport.getSeriousInjury() != seriousInjuryOld)) {
        println(getIndent(), " chooses the new injury result.");
      } else {
        println(getIndent(), " keeps the old injury result.");
      }
    }
  }

  public void reportSkillUse(ReportSkillUse pReport) {
    Game game = getClient().getGame();
    if (pReport.getSkill() != null) {
      Player player = game.getPlayerById(pReport.getPlayerId());
      int indent = getIndent();
      if (pReport.getSkill() != Skill.KICK) {
        indent += 1;
      }
      StringBuilder status = new StringBuilder();
      if (!pReport.isUsed()) {
        if (player != null) {
          print(indent, false, player);
          status.append(" does not use ").append(pReport.getSkill().getName());
        } else {
          status.append(pReport.getSkill().getName()).append(" is not used");
        }
        if (pReport.getSkillUse() != null) {
          status.append(" ").append(pReport.getSkillUse().getDescription(player));
        }
        status.append(".");
        println(indent, status.toString());
      } else {
        if (player != null) {
          print(indent, false, player);
          status.append(" uses ").append(pReport.getSkill().getName());
        } else {
          status.append(pReport.getSkill().getName()).append(" used");
        }
        if (pReport.getSkillUse() != null) {
          status.append(" ").append(pReport.getSkillUse().getDescription(player));
        }
        status.append(".");
        println(indent, status.toString());
      }
    }
  }
  
  public void reportPilingOn(ReportPilingOn pReport) {
    Game game = getClient().getGame();
    Player player = game.getPlayerById(pReport.getPlayerId());
    if (player != null) {
      int indent = getIndent() + 1;
      print(indent, false, player);
      StringBuilder status = new StringBuilder();
      if (!pReport.isUsed()) {
        status.append(" does not use ").append(Skill.PILING_ON.getName()).append(".");
      } else {
        status.append(" uses ").append(Skill.PILING_ON.getName()).append(" to re-roll ");
        status.append(pReport.isReRollInjury() ? "Injury" : "Armor").append(".");
      }
      println(indent, status.toString());
    }
  }

  public void reportServerUnreachable() {
    println();
    println(0, TextStyle.BOLD, "Server unreachable - Communication stopped.");
    println();
  }
  
  public void reportServerMessage(ServerStatus pServerStatus) {
    println(getIndent(), TextStyle.NONE, pServerStatus.getMessage());
  }

  public void reportReferee(ReportReferee pReport) {
    Game game = getClient().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (pReport.isFoulingPlayerBanned()) {
      print(getIndent(), "The referee spots the foul and bans ");
      print(getIndent(), false, actingPlayer.getPlayer());
      println(getIndent(), " from the game.");
    } else {
      println(getIndent(), "The referee didn't spot the foul.");
    }
  }
  
  public void reportPenaltyShootout(ReportPenaltyShootout pReport) {
    int penaltyScoreHome = pReport.getRollHome() + pReport.getReRollsLeftHome();
    print(0, TextStyle.ROLL, "Penalty Shootout Roll Home [" + pReport.getRollHome() + "]");
    print(0, TextStyle.ROLL, " + " + pReport.getReRollsLeftHome() + " ReRolls");
    println(0, TextStyle.ROLL, " = " + penaltyScoreHome);
    int penaltyScoreAway = pReport.getRollAway() + pReport.getReRollsLeftAway();
    print(0, TextStyle.ROLL, "Penalty Shootout Roll Away [" + pReport.getRollAway() + "]");
    print(0, TextStyle.ROLL, " + " + pReport.getReRollsLeftAway() + " ReRolls");
    println(0, TextStyle.ROLL, " = " + penaltyScoreAway);
    Game game = getClient().getGame();
    if (penaltyScoreHome > penaltyScoreAway) {
      print(1, TextStyle.HOME, game.getTeamHome().getName());
      println(1, TextStyle.NONE, " win the penalty shootout.");
    } else {
      print(1, TextStyle.AWAY, game.getTeamAway().getName());
      println(1, TextStyle.NONE, " win the penalty shootout.");
    }
  }
  
  public void report(ReportList pReportList) {
    for (IReport report : pReportList.getReports()) {
      switch(report.getId()) {
        case ALWAYS_HUNGRY_ROLL:
          reportAlwaysHungry((ReportSkillRoll) report);
          break;
        case CATCH_ROLL:
          reportCatch((ReportCatchRoll) report);
          break;
        case CONFUSION_ROLL:
          reportConfusion((ReportConfusionRoll) report);
          break;
        case DAUNTLESS_ROLL:
          reportDauntless((ReportDauntlessRoll) report);
          break;
        case DODGE_ROLL:
          reportDodge((ReportSkillRoll) report);
          break;
        case ESCAPE_ROLL:
          reportEscape((ReportSkillRoll) report);
          break;
        case FOUL_APPEARANCE_ROLL:
          reportFoulAppearance((ReportSkillRoll) report);
          break;
        case GO_FOR_IT_ROLL:
          reportGoingForIt((ReportSkillRoll) report);
          break;
        case INTERCEPTION_ROLL:
          reportInterception((ReportInterceptionRoll) report);
          break;
        case LEAP_ROLL:
          reportLeap((ReportSkillRoll) report);
          break;
        case PASS_ROLL:
          reportPass((ReportPassRoll) report);
          break;
        case PICK_UP_ROLL:
          reportPickup((ReportSkillRoll) report);
          break;
        case RIGHT_STUFF_ROLL:
          reportRightStuff((ReportSkillRoll) report);
          break;
        case REGENERATION_ROLL:
          reportRegeneration((ReportSkillRoll) report);
          break;
        case SAFE_THROW_ROLL:
          reportSafeThrow((ReportSkillRoll) report);
          break;
        case TENTACLES_SHADOWING_ROLL:
          reportTentaclesShadowingRoll((ReportTentaclesShadowingRoll) report);
          break;
        case RE_ROLL:
          reportReRoll((ReportReRoll) report);
          break;
        case SKILL_USE:
          reportSkillUse((ReportSkillUse) report);
          break;
        case FOUL:
          reportFoul((ReportFoul) report);
          break;
        case HAND_OVER:
          reportHandOver((ReportHandOver) report);
          break;
        case PLAYER_ACTION:
          reportPlayerAction((ReportPlayerAction) report);
          break;
        case INJURY:
          reportInjury((ReportInjury) report);
          break;
        case APOTHECARY_ROLL:
          reportApothecaryRoll((ReportApothecaryRoll) report);
          break;
        case APOTHECARY_CHOICE:
          reportApothecaryChoice((ReportApothecaryChoice) report);
          break;
        case THROW_IN:
          reportThrowIn((ReportThrowIn) report);
          break;
        case SCATTER_BALL:
          reportScatterBall((ReportScatterBall) report);
          break;
        case BLOCK:
          reportBlock((ReportBlock) report);
          break;
        case BLOCK_CHOICE:
          reportBlockChoice((ReportBlockChoice) report);
          break;
        case SPECTATORS:
          reportSpectators((ReportSpectators) report);
          break;
        case WEATHER:
          reportWeather((ReportWeather) report);
          break;
        case COIN_THROW:
          reportCoinThrow((ReportCoinThrow) report);
          break;
        case RECEIVE_CHOICE:
          reportReceiveChoice((ReportReceiveChoice) report);
          break;
        case TURN_END:
          reportTurnEnd((ReportTurnEnd) report);
          break;
        case PUSHBACK:
          reportPushback((ReportPushback) report);
          break;
        case KICKOFF_RESULT:
          reportKickoffResult((ReportKickoffResult) report);
          break;
        case KICKOFF_SCATTER:
          reportKickoffScatter((ReportKickoffScatter) report);
          break;
        case KICKOFF_EXTRA_REROLL:
          reportKickoffExtraReRoll((ReportKickoffExtraReRoll) report);
          break;
        case KICKOFF_RIOT:
          reportKickoffRiot((ReportKickoffRiot) report);
          break;
        case KICKOFF_THROW_A_ROCK:
          reportKickoffThrowARock((ReportKickoffThrowARock) report);
          break;
        case REFEREE:
          reportReferee((ReportReferee) report);
          break;
        case KICKOFF_PITCH_INVASION:
          reportKickoffPitchInvasion((ReportKickoffPitchInvasion) report);
          break;
        case THROW_TEAM_MATE_ROLL:
          reportThrowTeamMateRoll((ReportThrowTeamMateRoll) report);
          break;
        case SCATTER_PLAYER:
          reportScatterPlayer((ReportScatterPlayer) report);
          break;
        case TIMEOUT_ENFORCED:
          reportTimeoutEnforced((ReportTimeoutEnforced) report);
          break;
        case WINNINGS_ROLL:
          reportWinningsRoll((ReportWinningsRoll) report);
          break;
        case FAN_FACTOR_ROLL:
          reportFanFactorRoll((ReportFanFactorRoll) report);
          break;
        case MOST_VALUABLE_PLAYERS:
          reportMostValuablePlayers((ReportMostValuablePlayers) report);
          break;
 //       case SPIRALLING_EXPENSES:
 //         reportSpirallingExpenses((ReportSpirallingExpenses) report);
 //         break;
        case JUMP_UP_ROLL:
          reportJumpUp((ReportSkillRoll) report);
          break;
        case STAND_UP_ROLL:
          reportStandUp((ReportStandUpRoll) report);
          break;
        case BRIBES_ROLL:
          reportBribes((ReportBribesRoll) report);
          break;
        case FUMBBL_RESULT_UPLOAD:
          reportFumbblResultUpload((ReportFumbblResultUpload) report);
          break;
        case START_HALF:
          reportStartHalf((ReportStartHalf) report);
          break;
        case MASTER_CHEF_ROLL:
          reportMasterChef((ReportMasterChefRoll) report);
          break;
        case DEFECTING_PLAYERS:
          reportDefectingPlayers((ReportDefectingPlayers) report);
          break;
        case INDUCEMENT:
          reportInducement((ReportInducement) report);
          break;
        case PILING_ON:
          reportPilingOn((ReportPilingOn) report);
          break;
        case CHAINSAW_ROLL:
          reportChainsaw((ReportSkillRoll) report);
          break;
        case LEADER:
          reportLeader((ReportLeader) report);
          break;
        case SECRET_WEAPON_BAN:
          reportSecretWeaponBan((ReportSecretWeaponBan) report);
          break;
        case BLOOD_LUST_ROLL:
          reportBloodLust((ReportSkillRoll) report);
          break;
        case HYPNOTIC_GAZE_ROLL:
          reportHypnoticGaze((ReportSkillRoll) report);
          break;
        case BITE_SPECTATOR:
          reportBiteSpectator((ReportBiteSpectator) report);
          break;
        case ANIMOSITY_ROLL:
          reportAnimosity((ReportSkillRoll) report);
          break;
        case RAISE_DEAD:
          reportRaiseDead((ReportRaiseDead) report);
          break;
        case BLOCK_ROLL:
          reportBlockRoll((ReportBlockRoll) report);
          break;
        case PENALTY_SHOOTOUT:
          reportPenaltyShootout((ReportPenaltyShootout) report);
          break;
        case DOUBLE_HIRED_STAR_PLAYER:
          reportDoubleHiredStarPlayer((ReportDoubleHiredStarPlayer) report);
          break;
        case SPELL_EFFECT_ROLL:
        	reportSpecialEffectRoll((ReportSpecialEffectRoll) report);
        	break;
        case WIZARD_USE:
        	reportWizardUse((ReportWizardUse) report);
        	break;
        case PASS_BLOCK:
        	reportPassBlock((ReportPassBlock) report);
        	break;
        case NO_PLAYERS_TO_FIELD:
        	reportNoPlayersToField((ReportNoPlayersToField) report);
        	break;
        case PLAY_CARD:
        	reportPlayCard((ReportPlayCard) report);
        	break;
        case CARD_DEACTIVATED:
        	reportCardDeactivated((ReportCardDeactivated) report);
        	break;
        case BOMB_OUT_OF_BOUNDS:
        	reportBombOutOfBounds((ReportBombOutOfBounds) report);
        	break;
        case PETTY_CASH:
        	reportPettyCash((ReportPettyCash) report);
        	break;
        case INDUCEMENTS_BOUGHT:
        	reportInducementsBought((ReportInducementsBought) report);
        	break;
        case CARDS_BOUGHT:
        	reportCardsBought((ReportCardsBought) report);
        	break;
        case GAME_OPTIONS:
        	// deprecated, do nothing
        	break;
        default:
          throw new IllegalStateException("Unhandled report id " + report.getId().getName() + ".");
      }
    }
  }

  private ParagraphStyle findParagraphStyle(int pIndent) {
    ParagraphStyle paragraphStyle = null;
    switch (pIndent) {
      case 0:
        paragraphStyle = ParagraphStyle.INDENT_0;
        break;
      case 1:
        paragraphStyle = ParagraphStyle.INDENT_1;
        break;
      case 2:
        paragraphStyle = ParagraphStyle.INDENT_2;
        break;
      case 3:
        paragraphStyle = ParagraphStyle.INDENT_3;
        break;
      case 4:
        paragraphStyle = ParagraphStyle.INDENT_4;
        break;
      case 5:
        paragraphStyle = ParagraphStyle.INDENT_5;
        break;
      case 6:
        paragraphStyle = ParagraphStyle.INDENT_6;
        break;
    }
    return paragraphStyle;
  }
  
  private void print(int pIndent, TextStyle pTextStyle, String pText) {
    print(findParagraphStyle(pIndent), pTextStyle, pText);
  }

  private void print(int pIndent, String pText) {
    print(findParagraphStyle(pIndent), null, pText);
  }
  
  private void print(ParagraphStyle pParagraphStyle, TextStyle pTextStyle, String pText) {
    getClient().getUserInterface().getLog().append(pParagraphStyle, pTextStyle, pText);
  }

  private void println(int pIndent, TextStyle pTextStyle, String pText) {
    println(findParagraphStyle(pIndent), pTextStyle, pText);
  }

  private void println(int pIndent, String pText) {
    println(findParagraphStyle(pIndent), null, pText);
  }
  
  private void println() {
    println(findParagraphStyle(0), null, null);
  }
  
  private void println(ParagraphStyle pParagraphStyle, TextStyle pTextStyle, String pText) {
    print(pParagraphStyle, pTextStyle, pText);
    getClient().getUserInterface().getLog().append(null, null, null);
  }

  private void print(int pIndent, boolean pBold, Player pPlayer) {
    if (pPlayer != null) {
      ParagraphStyle paragraphStyle = findParagraphStyle(pIndent);
      if (getClient().getGame().getTeamHome().hasPlayer(pPlayer)) {
        if (pBold) {
          print(paragraphStyle, TextStyle.HOME_BOLD, pPlayer.getName());
        } else {
          print(paragraphStyle, TextStyle.HOME, pPlayer.getName());
        }
      } else {
        if (pBold) {
          print(paragraphStyle, TextStyle.AWAY_BOLD, pPlayer.getName());
        } else {
          print(paragraphStyle, TextStyle.AWAY, pPlayer.getName());
        }
      }
    }
  }

  private void printTeamName(Game pGame, boolean pBold, String pTeamId) {
    if (pGame.getTeamHome().getId().equals(pTeamId)) {
    	if (pBold) {
    		print(getIndent() + 1, TextStyle.HOME_BOLD, pGame.getTeamHome().getName());
    	} else {
    		print(getIndent() + 1, TextStyle.HOME, pGame.getTeamHome().getName());
    	}
    } else {
    	if (pBold) {
    		print(getIndent() + 1, TextStyle.AWAY_BOLD, pGame.getTeamAway().getName());
    	} else {
    		print(getIndent() + 1, TextStyle.AWAY, pGame.getTeamAway().getName());
    	}
    }
  }
  
}
