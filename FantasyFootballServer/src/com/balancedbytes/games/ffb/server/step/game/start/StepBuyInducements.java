package com.balancedbytes.games.ffb.server.step.game.start;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.balancedbytes.games.ffb.GameOption;
import com.balancedbytes.games.ffb.Inducement;
import com.balancedbytes.games.ffb.InducementPhase;
import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.PlayerType;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogBuyInducementsParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.model.InducementSet;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Roster;
import com.balancedbytes.games.ffb.model.RosterPosition;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.TurnData;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandBuyInducements;
import com.balancedbytes.games.ffb.report.ReportDoubleHiredStarPlayer;
import com.balancedbytes.games.ffb.report.ReportInducementsBought;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.DbTransaction;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.UtilSteps;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.UtilBox;

/**
 * Step in start game sequence to buy inducements.
 * 
 * Expects stepParameter INDUCEMENT_GOLD_AWAY to be set by a preceding step.
 * Expects stepParameter INDUCEMENT_GOLD_HOME to be set by a preceding step.
 *
 * Pushes inducement sequence on the stack.
 * 
 * @author Kalimar
 */
public final class StepBuyInducements extends AbstractStep {
	
  protected static final int MINIMUM_PETTY_CASH_FOR_INDUCEMENTS = 50000;
  
  protected int fInducementGoldHome;
  protected int fInducementGoldAway;
  
  protected boolean fInducementsSelectedHome;
  protected boolean fInducementsSelectedAway;
  
  protected int fGoldUsedHome;
  protected int fGoldUsedAway;
  
  protected boolean fReportedHome;
  protected boolean fReportedAway;
  
	public StepBuyInducements(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.BUY_INDUCEMENTS;
	}
	
  @Override
  public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
	  	switch (pParameter.getKey()) {
				case INDUCEMENT_GOLD_AWAY:
					fInducementGoldAway = (pParameter.getValue() != null) ? (Integer) pParameter.getValue() : 0;
					return true;
				case INDUCEMENT_GOLD_HOME:
					fInducementGoldHome = (pParameter.getValue() != null) ? (Integer) pParameter.getValue() : 0;
					return true;
				default:
					break;
	  	}
		}
		return false;
  }
		
	@Override
	public void start() {
		super.start();
		executeStep();
	}
	
	@Override
	public StepCommandStatus handleNetCommand(NetCommand pNetCommand) {
		StepCommandStatus commandStatus = super.handleNetCommand(pNetCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			Game game = getGameState().getGame();
			GameResult gameResult = game.getGameResult();
			switch (pNetCommand.getId()) {
	      case CLIENT_BUY_INDUCEMENTS:
	        ClientCommandBuyInducements buyInducementsCommand = (ClientCommandBuyInducements) pNetCommand;
	        if (game.getTeamHome().getId().equals(buyInducementsCommand.getTeamId())) {
	          game.getTurnDataHome().getInducementSet().add(buyInducementsCommand.getInducementSet());
	          addStarPlayers(game.getTeamHome(), buyInducementsCommand.getStarPlayerPositionIds());
	          addMercenaries(game.getTeamHome(), buyInducementsCommand.getMercenaryPositionIds(), buyInducementsCommand.getMercenarySkills());
	          fGoldUsedHome = fInducementGoldHome - buyInducementsCommand.getAvailableGold();
	          int pettyCashUsed = Math.max(0, fGoldUsedHome - game.getOptions().getOptionValue(GameOption.FREE_INDUCEMENT_CASH).getValue());
	          gameResult.getTeamResultHome().setPettyCashUsed(gameResult.getTeamResultHome().getPettyCashUsed() + pettyCashUsed);
	          fInducementsSelectedHome = true;
	        } else {
	          game.getTurnDataAway().getInducementSet().add(buyInducementsCommand.getInducementSet());
	          addStarPlayers(game.getTeamAway(), buyInducementsCommand.getStarPlayerPositionIds());
	          addMercenaries(game.getTeamAway(), buyInducementsCommand.getMercenaryPositionIds(), buyInducementsCommand.getMercenarySkills());
	          fGoldUsedAway = fInducementGoldAway - buyInducementsCommand.getAvailableGold();
	          int pettyCashUsed = Math.max(0, fGoldUsedAway - game.getOptions().getOptionValue(GameOption.FREE_INDUCEMENT_CASH).getValue());
	          gameResult.getTeamResultAway().setPettyCashUsed(gameResult.getTeamResultAway().getPettyCashUsed() + pettyCashUsed);
	          fInducementsSelectedAway = true;
	        }
	        commandStatus = StepCommandStatus.EXECUTE_STEP;
	        break;
        default:
        	break;
			}
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

  private void executeStep() {
  	if (getGameState() == null) {
  		return;
  	}
    Game game = getGameState().getGame();
    GameResult gameResult = game.getGameResult();
    int homeTV = gameResult.getTeamResultHome().getTeamValue();
    int awayTV = gameResult.getTeamResultAway().getTeamValue();
    if (game.getOptions().getOptionValue(GameOption.INDUCEMENTS).isEnabled()) {
      if (!fInducementsSelectedHome && !fInducementsSelectedAway) {
    	  fInducementGoldHome += game.getOptions().getOptionValue(GameOption.FREE_INDUCEMENT_CASH).getValue();
    	  fInducementGoldAway += game.getOptions().getOptionValue(GameOption.FREE_INDUCEMENT_CASH).getValue();
      }
      if (fInducementGoldHome < MINIMUM_PETTY_CASH_FOR_INDUCEMENTS) {
        fInducementsSelectedHome = true;
      }
      if (fInducementGoldAway < MINIMUM_PETTY_CASH_FOR_INDUCEMENTS) {
        fInducementsSelectedAway = true;
      }
      if (fInducementsSelectedHome && !fReportedHome) {
      	fReportedHome = true;
      	getResult().addReport(generateReport(game.getTeamHome()));
      }
      if (fInducementsSelectedAway && !fReportedAway) {
      	fReportedAway = true;
      	getResult().addReport(generateReport(game.getTeamAway()));
      }
      if (!fInducementsSelectedHome && !fInducementsSelectedAway) {
        if (homeTV > awayTV) {
          UtilDialog.showDialog(getGameState(), new DialogBuyInducementsParameter(game.getTeamHome().getId(), fInducementGoldHome));
        } else {
          UtilDialog.showDialog(getGameState(), new DialogBuyInducementsParameter(game.getTeamAway().getId(), fInducementGoldAway));
        }
      } else if (!fInducementsSelectedHome) {
        UtilDialog.showDialog(getGameState(), new DialogBuyInducementsParameter(game.getTeamHome().getId(), fInducementGoldHome));
      } else if (!fInducementsSelectedAway) {
        UtilDialog.showDialog(getGameState(), new DialogBuyInducementsParameter(game.getTeamAway().getId(), fInducementGoldAway));
      } else {
      	leaveStep(homeTV, awayTV);
      }
    } else {
    	leaveStep(homeTV, awayTV);
    }
  }
  
  private ReportInducementsBought generateReport(Team pTeam) {
  	Game game = getGameState().getGame();
  	InducementSet inducementSet = (game.getTeamHome() == pTeam) ? game.getTurnDataHome().getInducementSet() : game.getTurnDataAway().getInducementSet();
  	int nrOfInducements = 0, nrOfStars = 0, nrOfMercenaries = 0;
  	for (Inducement inducement : inducementSet.getInducements()) {
  		switch (inducement.getType()) {
    		case STAR_PLAYERS:
    			nrOfStars = inducement.getValue();
    			break;
    		case MERCENARIES:
    			nrOfMercenaries = inducement.getValue();
    			break;
  			default:
  				nrOfInducements += inducement.getValue();
  				break;
  		}
  	}
  	int gold = (game.getTeamHome() == pTeam) ? fGoldUsedHome : fGoldUsedAway;
  	return new ReportInducementsBought(pTeam.getId(), nrOfInducements, nrOfStars, nrOfMercenaries, gold);
  }
  
  private void leaveStep(int pHomeTV, int pAwayTV) { 
  	if (pHomeTV > pAwayTV) {
  		SequenceGenerator.getInstance().pushInducementSequence(getGameState(), InducementPhase.AFTER_INDUCEMENTS_PURCHASED, true);
  		SequenceGenerator.getInstance().pushInducementSequence(getGameState(), InducementPhase.AFTER_INDUCEMENTS_PURCHASED, false);
  	} else {
  		SequenceGenerator.getInstance().pushInducementSequence(getGameState(), InducementPhase.AFTER_INDUCEMENTS_PURCHASED, false);
  		SequenceGenerator.getInstance().pushInducementSequence(getGameState(), InducementPhase.AFTER_INDUCEMENTS_PURCHASED, true);
  	}
  	getResult().setNextAction(StepAction.NEXT_STEP);
  }
    
  private void addMercenaries(Team pTeam, String[] pPositionIds, Skill[] pSkills) {
    
  	if (!ArrayTool.isProvided(pPositionIds) || !ArrayTool.isProvided(pSkills)) {
  		return;
  	}
      
    Roster roster = pTeam.getRoster();
    Game game = getGameState().getGame();
    List<Player> addedPlayerList = new ArrayList<Player>();
    Map<RosterPosition, Integer> nrByPosition = new HashMap<RosterPosition, Integer>();
    
    for (int i = 0; i < pPositionIds.length; i++) {
      RosterPosition position = roster.getPositionById(pPositionIds[i]);
      Player mercenary = new Player();
      addedPlayerList.add(mercenary);
      StringBuilder playerId = new StringBuilder().append(pTeam.getId()).append("M").append(addedPlayerList.size());
      mercenary.setId(playerId.toString());
      mercenary.updatePosition(position);
      Integer mercNr = nrByPosition.get(position);
      if (mercNr == null) {
      	mercNr = 1;
      } else {
      	mercNr = mercNr + 1;
      }
      nrByPosition.put(position, mercNr);
      StringBuilder name = new StringBuilder();
      name.append("Merc ").append(position.getName()).append(" ").append(mercNr);
      mercenary.setName(name.toString());
      mercenary.setNr(pTeam.getMaxPlayerNr() + 1);
      mercenary.setType(PlayerType.MERCENARY);
      mercenary.addSkill(Skill.LONER);
      if (pSkills[i] != null) {
      	mercenary.addSkill(pSkills[i]);
      }
      pTeam.add(mercenary);
      game.getFieldModel().setPlayerState(mercenary, new PlayerState(PlayerState.RESERVE));
      UtilBox.putPlayerIntoBox(game, mercenary);
    }
    
    if (addedPlayerList.size() > 0) {
      Player[] addedPlayers = addedPlayerList.toArray(new Player[addedPlayerList.size()]);
      UtilSteps.sendAddedPlayers(getGameState(), pTeam, addedPlayers);
    }
    
  }
  
  private void removeStarPlayerInducements(TurnData pTurnData, int pRemoved) {
    Inducement starPlayerInducement = pTurnData.getInducementSet().get(InducementType.STAR_PLAYERS);
    if (starPlayerInducement != null) {
      starPlayerInducement.setValue(starPlayerInducement.getValue() - pRemoved);
      if (starPlayerInducement.getValue() <= 0) {
        pTurnData.getInducementSet().removeInducement(starPlayerInducement);
      } else {
        pTurnData.getInducementSet().addInducement(starPlayerInducement);
      }
    }
  }

  private void addStarPlayers(Team pTeam, String[] pPositionIds) {
    if (ArrayTool.isProvided(pPositionIds)) {
      
      Roster roster = pTeam.getRoster();
      Game game = getGameState().getGame();
      FantasyFootballServer server = getGameState().getServer();
      
      Map<String, Player> otherTeamStarPlayerByName = new HashMap<String, Player>();
      Team otherTeam = (game.getTeamHome() == pTeam) ? game.getTeamAway() : game.getTeamHome();
      for (Player otherPlayer : otherTeam.getPlayers()) {
        if (otherPlayer.getType() == PlayerType.STAR) {
          otherTeamStarPlayerByName.put(otherPlayer.getName(), otherPlayer);
        }
      }

      List<Player> addedPlayerList = new ArrayList<Player>();
      List<Player> removedPlayerList = new ArrayList<Player>();
      for (int i = 0; i < pPositionIds.length; i++) {
        RosterPosition position = roster.getPositionById(pPositionIds[i]);
        Player otherTeamStarPlayer = otherTeamStarPlayerByName.get(position.getName());
        if (otherTeamStarPlayer != null) {
          removedPlayerList.add(otherTeamStarPlayer);
        } else {
          Player starPlayer = new Player();
          addedPlayerList.add(starPlayer);
          StringBuilder playerId = new StringBuilder().append(pTeam.getId()).append("S").append(addedPlayerList.size());
          starPlayer.setId(playerId.toString());
          starPlayer.updatePosition(position);
          starPlayer.setName(position.getName());
          starPlayer.setNr(pTeam.getMaxPlayerNr() + 1);
          pTeam.add(starPlayer);
          game.getFieldModel().setPlayerState(starPlayer, new PlayerState(PlayerState.RESERVE));
          UtilBox.putPlayerIntoBox(game, starPlayer);
        }
      }
      
      if (removedPlayerList.size() > 0) {
        removeStarPlayerInducements(game.getTurnDataHome(), removedPlayerList.size());
        removeStarPlayerInducements(game.getTurnDataAway(), removedPlayerList.size());
        DbTransaction transaction = new DbTransaction();
        for (Player player : removedPlayerList) {
          server.getCommunication().sendRemovePlayer(getGameState(), player.getId());
          getResult().addReport(new ReportDoubleHiredStarPlayer(player.getName()));
        }
        server.getDbUpdater().add(transaction);
      }
      
      if (addedPlayerList.size() > 0) {
        Player[] addedPlayers = addedPlayerList.toArray(new Player[addedPlayerList.size()]);
        UtilSteps.sendAddedPlayers(getGameState(), pTeam, addedPlayers);
        // TODO: update persistence?
      }
      
    }
    
  }

  public int getByteArraySerializationVersion() {
  	return 1;
  }

  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addInt(fInducementGoldHome);
  	pByteList.addInt(fInducementGoldAway);
  	pByteList.addBoolean(fInducementsSelectedHome);
  	pByteList.addBoolean(fInducementsSelectedAway);
  	pByteList.addBoolean(fReportedHome);
  	pByteList.addBoolean(fReportedAway);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fInducementGoldHome = pByteArray.getInt();
  	fInducementGoldAway = pByteArray.getInt();
  	fInducementsSelectedHome = pByteArray.getBoolean();
  	fInducementsSelectedAway = pByteArray.getBoolean();
  	fReportedHome = pByteArray.getBoolean();
  	fReportedAway = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }
  
}
