package com.balancedbytes.games.ffb.server.step.action.common;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.GameOption;
import com.balancedbytes.games.ffb.HeatExhaustion;
import com.balancedbytes.games.ffb.Inducement;
import com.balancedbytes.games.ffb.InducementDuration;
import com.balancedbytes.games.ffb.InducementPhase;
import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.KnockoutRecovery;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogBribesParameter;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.model.InducementSet;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseInducement;
import com.balancedbytes.games.ffb.report.ReportBribesRoll;
import com.balancedbytes.games.ffb.report.ReportSecretWeaponBan;
import com.balancedbytes.games.ffb.report.ReportTurnEnd;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.ServerMode;
import com.balancedbytes.games.ffb.server.fumbbl.FumbblRequestUpdateGamestate;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.step.UtilSteps;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.server.util.UtilGame;
import com.balancedbytes.games.ffb.server.util.UtilInducementUse;
import com.balancedbytes.games.ffb.server.util.UtilTimer;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.UtilBox;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in any sequence to end a turn.
 * 
 * Needs to be initialized with stepParameter HANDLE_SECRET_WEAPONS.
 * 
 * May push another sequence on the stack (endGame, startGame or kickoff)
 *
 * @author Kalimar
 */
public class StepEndTurn extends AbstractStep {
	
	private boolean fHandleSecretWeapons;
	private Boolean fTouchdown;
	private Boolean fBribesChoiceHome;
	private Boolean fBribesChoiceAway;
	private boolean fNextSequencePushed;
	private boolean fRemoveUsedSecretWeapons;
	private boolean fNewHalf;
	private boolean fEndGame;
	
	public StepEndTurn(GameState pGameState) {
		super(pGameState);
		fHandleSecretWeapons = true;
	}
	
	public StepId getId() {
		return StepId.END_TURN;
	}
	
  @Override
  public void init(StepParameterSet pParameterSet) {
  	if (pParameterSet != null) {
  		for (StepParameter parameter : pParameterSet.values()) {
  			switch (parameter.getKey()) {
  			  // optional
  				case HANDLE_SECRET_WEAPONS:
  					fHandleSecretWeapons = (Boolean) parameter.getValue();
  					break;
					default:
						break;
  			}
  		}
  	}
  }
  	
	@Override
	public void start() {
		super.start();
		executeStep();
	}
	
  @Override
  public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
    StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
    if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
      Game game = getGameState().getGame();
      switch (pReceivedCommand.getId()) {
        case CLIENT_USE_INDUCEMENT:
          ClientCommandUseInducement inducementCommand = (ClientCommandUseInducement) pReceivedCommand.getCommand();
          if (InducementType.BRIBES == inducementCommand.getInducementType()) {
            Team team = UtilSteps.checkCommandIsFromHomePlayer(getGameState(), pReceivedCommand) ? game.getTeamHome() : game.getTeamAway();
            if (useSecretWeaponBribes(team, inducementCommand.getPlayerIds()) || !askForSecretWeaponBribes(team)) {
              commandStatus = StepCommandStatus.EXECUTE_STEP;
            } else {
              commandStatus = StepCommandStatus.SKIP_STEP;
            }
          }
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
    
    Game game = getGameState().getGame();
		UtilDialog.hideDialog(getGameState());
    
    if ((game.getTurnMode() == TurnMode.BLITZ) || (game.getTurnMode() == TurnMode.KICKOFF_RETURN) || (game.getTurnMode() == TurnMode.PASS_BLOCK)) {
    	publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
      getResult().setNextAction(StepAction.NEXT_STEP);
      return;
    }
    
    if (fTouchdown == null) {
    	fTouchdown = UtilSteps.checkTouchdown(getGameState());
    }
    
    if (fHandleSecretWeapons) {
      markPlayedAndSecretWeapons();
    }
    
    fEndGame = false;
    fNewHalf = UtilSteps.checkEndOfHalf(getGameState());

    if (!fNextSequencePushed) {
      
      fNextSequencePushed = true;
      
      Player touchdownPlayer = null;
      if (fTouchdown) {
        
        touchdownPlayer = game.getFieldModel().getPlayer(game.getFieldModel().getBallCoordinate());
        boolean offTurnTouchDown = false;
        if (touchdownPlayer != null) {
          
        	GameResult gameResult = game.getGameResult();
          PlayerResult touchdownPlayerResult = gameResult.getPlayerResult(touchdownPlayer);
          touchdownPlayerResult.setTouchdowns(touchdownPlayerResult.getTouchdowns() + 1);

          if (game.getTeamHome().hasPlayer(touchdownPlayer)) {
            gameResult.getTeamResultHome().setScore(gameResult.getTeamResultHome().getScore() + 1);
            offTurnTouchDown |= !game.isHomePlaying();
          
          } else {
            gameResult.getTeamResultAway().setScore(gameResult.getTeamResultAway().getScore() + 1);
            offTurnTouchDown |= game.isHomePlaying();
          }

          offTurnTouchDown |= (game.getTurnMode() == TurnMode.KICKOFF);  // in the case of a caught kick-off that results in a TD
          
          game.setHomePlaying(game.getTeamHome().hasPlayer(touchdownPlayer));

          if (offTurnTouchDown) {
          	game.getTurnData().setTurnNr(game.getTurnData().getTurnNr() + 1); 
            fNewHalf = UtilSteps.checkEndOfHalf(getGameState());
          }
          
        }

        game.setTurnMode(TurnMode.SETUP);
        game.setSetupOffense(false);

      } else {
        
        switch (game.getTurnMode()) {
        	case NO_PLAYERS_TO_FIELD:
            game.getTurnDataHome().setTurnNr(game.getTurnDataHome().getTurnNr() + 2);
            game.getTurnDataAway().setTurnNr(game.getTurnDataAway().getTurnNr() + 2);
            fNewHalf = UtilSteps.checkEndOfHalf(getGameState());
            game.setTurnMode(TurnMode.SETUP);
            game.setSetupOffense(false);
            fTouchdown = true;
            break;
          case KICKOFF:
            game.setHomePlaying(!game.isHomePlaying());
            game.getTurnData().setTurnNr(game.getTurnData().getTurnNr() + 1);
            game.getTurnData().setTurnStarted(false);
            game.getTurnData().setFirstTurnAfterKickoff(true);
            game.setTurnMode(TurnMode.REGULAR);
            break;
          case REGULAR:
            if (fNewHalf) {
              game.setTurnMode(TurnMode.SETUP);
              game.setSetupOffense(false);
            } else {
              game.setHomePlaying(!game.isHomePlaying());
              game.getTurnData().setTurnNr(game.getTurnData().getTurnNr() + 1);
            }
            game.getTurnData().setTurnStarted(false);
            game.getTurnData().setFirstTurnAfterKickoff(false);
            break;
          default:
          	break;
        }
        
      }
  
      UtilPlayer.refreshPlayersForTurnStart(game);
      game.getFieldModel().clearMoveSquares();
      game.getFieldModel().clearTrackNumbers();
      game.getFieldModel().clearDiceDecorations();
  
      List<KnockoutRecovery> knockoutRecoveries = new ArrayList<KnockoutRecovery>();
      List<HeatExhaustion> heatExhaustions = new ArrayList<HeatExhaustion>();
      if (fNewHalf || fTouchdown) {
        for (Player player : game.getPlayers()) {
          PlayerState playerState = game.getFieldModel().getPlayerState(player);
          FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
          if (playerState.getBase() == PlayerState.KNOCKED_OUT) {
            KnockoutRecovery knockoutRecovery = recoverKnockout(player);
            if (knockoutRecovery != null) {
              knockoutRecoveries.add(knockoutRecovery);
              if (knockoutRecovery.isRecovering()) {
                game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.RESERVE));
                UtilBox.putPlayerIntoBox(game, player);
              }
            }
          }
          if (playerState.getBase() == PlayerState.EXHAUSTED) {
            game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.RESERVE));
          }
          if (Weather.SWELTERING_HEAT == game.getFieldModel().getWeather()) {
            if ((playerCoordinate != null) && !playerCoordinate.isBoxCoordinate()) {
              HeatExhaustion heatExhaustion = heatExhaust(player); 
              heatExhaustions.add(heatExhaustion);
              if (heatExhaustion.isExhausted()) {
                game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.EXHAUSTED));
                UtilBox.putPlayerIntoBox(game, player);
              }
            }
          }
        }
        UtilBox.putAllPlayersIntoBox(game);
      }
      
      if (fNewHalf) {
        if (game.getHalf() > 2) {
        	fEndGame = true;
        } else if (game.getHalf() > 1) {
          GameResult gameResult = game.getGameResult();
          if (game.getOptions().getOptionValue(GameOption.OVERTIME).isEnabled() && (gameResult.getTeamResultHome().getScore() == gameResult.getTeamResultAway().getScore())) {
            UtilGame.startHalf(this, game.getHalf() + 1);
            SequenceGenerator.getInstance().pushKickoffSequence(getGameState(), true);
          } else {
          	fEndGame = true;
          }
        } else {
          UtilGame.startHalf(this, game.getHalf() + 1);
          SequenceGenerator.getInstance().pushKickoffSequence(getGameState(), false);
          fRemoveUsedSecretWeapons = true;
        }
        getResult().setSound(Sound.WHISTLE);
      } else if (fTouchdown) {
        game.getFieldModel().setBallCoordinate(null);
        game.getFieldModel().setBallInPlay(false);
        getGameState().getServer().getCommunication().sendSound(getGameState(), Sound.TOUCHDOWN);
        getResult().setSound(Sound.WHISTLE);
        if (game.getHalf() == 3) {
          SequenceGenerator.getInstance().pushEndGameSequence(getGameState(), false);
        } else {
          SequenceGenerator.getInstance().pushKickoffSequence(getGameState(), false);
          fRemoveUsedSecretWeapons = true;
        }
      } else if (game.getTurnMode() != TurnMode.REGULAR) {
        UtilBox.refreshBoxes(game);
        getResult().setSound(Sound.DING);
        SequenceGenerator.getInstance().pushKickoffSequence(getGameState(), false);
        fRemoveUsedSecretWeapons = true;
      } else {
        getResult().setSound(Sound.DING);
       	SequenceGenerator.getInstance().pushInducementSequence(getGameState(), InducementPhase.START_OF_OWN_TURN, game.isHomePlaying());
      }
  
      KnockoutRecovery[] knockoutRecoveryArray = knockoutRecoveries.toArray(new KnockoutRecovery[knockoutRecoveries.size()]);
      HeatExhaustion[] heatExhaustionArray = heatExhaustions.toArray(new HeatExhaustion[heatExhaustions.size()]);
      String touchdownPlayerId = (touchdownPlayer != null) ? touchdownPlayer.getId() : null;
      getResult().addReport(new ReportTurnEnd(touchdownPlayerId, knockoutRecoveryArray, heatExhaustionArray));

      if (game.isTurnTimeEnabled()) {
        UtilTimer.stopTurnTimer(getGameState());
        game.setTurnTime(0);
      }
      
      deactivateCards(InducementDuration.UNTIL_END_OF_TURN);
      deactivateCards(InducementDuration.UNTIL_END_OF_OPPONENTS_TURN);
      
      if (fNewHalf || fTouchdown) {
        deactivateCards(InducementDuration.UNTIL_END_OF_DRIVE);
        if (fHandleSecretWeapons) {
          reportSecretWeaponsUsed();
        }
      }
      
    }    
    
    if (fBribesChoiceAway == null) {
    	fBribesChoiceAway = false;
      if (!fEndGame && fHandleSecretWeapons && (fNewHalf || fTouchdown) && askForSecretWeaponBribes(game.getTeamAway())) {
      	fBribesChoiceAway = null;
      }
    }
    	
    if ((fBribesChoiceHome == null) && (fBribesChoiceAway != null)) {
  		fBribesChoiceHome = false;
      if (!fEndGame && fHandleSecretWeapons && (fNewHalf || fTouchdown) && askForSecretWeaponBribes(game.getTeamHome())) {
      	fBribesChoiceHome = null;
      }
    }
    
    if (fEndGame || ((fBribesChoiceHome != null) && (fBribesChoiceAway != null))) {
      
      if (!fEndGame && fRemoveUsedSecretWeapons) {
        removeUsedSecretWeapons();
      }
        
      game.startTurn();
      UtilGame.updateLeaderReRolls(this);
      
      if (fEndGame) {
      	SequenceGenerator.getInstance().pushEndGameSequence(getGameState(), false);
      }
      
      if (!fEndGame && game.isTurnTimeEnabled()) {
        UtilTimer.startTurnTimer(getGameState());
      }
      
      updateFumbblGame(getGameState(), fNewHalf, fTouchdown);

      getResult().setNextAction(StepAction.NEXT_STEP);
      
    }
    
  }
	
  private void markPlayedAndSecretWeapons() {
  	Game game = getGameState().getGame();
    if (game.getTurnMode() == TurnMode.REGULAR) {
      for (Player player : game.getPlayers()) {
        PlayerState playerState = game.getFieldModel().getPlayerState(player);
        if (playerState.canBeSetUp() && (playerState.getBase() != PlayerState.RESERVE)) {
          PlayerResult playerResult = game.getGameResult().getPlayerResult(player);
          if (UtilCards.hasSkill(game, player, Skill.SECRET_WEAPON))  {
            playerResult.setHasUsedSecretWeapon(true);
          }
          if ((game.isHomePlaying() && game.getTeamHome().hasPlayer(player)) || (!game.isHomePlaying() && game.getTeamAway().hasPlayer(player))) {
            playerResult.setTurnsPlayed(playerResult.getTurnsPlayed() + 1);
          }
        }
      }
    }
  }
  
  private void reportSecretWeaponsUsed() {
    ReportSecretWeaponBan reportBan = new ReportSecretWeaponBan();
    Game game = getGameState().getGame();
    for (Player player: game.getPlayers()) {
      PlayerResult playerResult = game.getGameResult().getPlayerResult(player);
      if (playerResult.hasUsedSecretWeapon()) {
      	// special for stunty leeg -> roll for secret weapon ban
      	Integer penalty = player.getPosition().getSkillValue(Skill.SECRET_WEAPON);
      	if ((penalty != null) && (penalty > 0)) {
      		int[] roll = getGameState().getDiceRoller().rollSecretWeapon();
      		int total = roll[0] + roll[1];
      		boolean banned = (total >= penalty);
      		reportBan.add(player.getId(), total, banned);
    			playerResult.setHasUsedSecretWeapon(banned);
    		// lrb6 secret weapon use (auto-ban)
      	} else {
      		reportBan.add(player.getId(), 0, true);
      	}
      }
    }
    if (ArrayTool.isProvided(reportBan.getPlayerIds())) {
      getResult().addReport(reportBan);
    }
  }
  
  private void removeUsedSecretWeapons() {
  	Game game = getGameState().getGame();
    for (Player player: game.getPlayers()) {
      PlayerResult playerResult = game.getGameResult().getPlayerResult(player);
      if (playerResult.hasUsedSecretWeapon()) {
        PlayerState playerState = game.getFieldModel().getPlayerState(player);
        game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.BANNED));
        playerResult.setSendToBoxByPlayerId(null);
        playerResult.setSendToBoxReason(SendToBoxReason.SECRET_WEAPON_BAN);
        playerResult.setSendToBoxTurn(game.getTurnData().getTurnNr());
        playerResult.setSendToBoxHalf(game.getHalf());
        UtilBox.putPlayerIntoBox(game, player);
        playerResult.setHasUsedSecretWeapon(false);
      }
    }
    UtilGame.updateLeaderReRolls(this);
  }

  public boolean checkTouchdown() {
  	boolean touchdown = false;
    Game game = getGameState().getGame();
    if (game.getFieldModel().isBallInPlay() && !game.getFieldModel().isBallMoving()) {
      FieldCoordinate ballPosition = game.getFieldModel().getBallCoordinate();
      Player ballCarrier = game.getFieldModel().getPlayer(ballPosition);
      PlayerState ballCarrierState = game.getFieldModel().getPlayerState(ballCarrier);
      ActingPlayer actingPlayer = game.getActingPlayer();
      if ((ballCarrier != null) && (ballCarrierState != null) && ballCarrierState.hasTacklezones() && ((ballCarrier != actingPlayer.getPlayer()) || !actingPlayer.isSufferingBloodLust())) {
        touchdown = ((game.getTeamHome().hasPlayer(ballCarrier) && FieldCoordinateBounds.ENDZONE_AWAY.isInBounds(ballPosition))
          || (game.getTeamAway().hasPlayer(ballCarrier) && FieldCoordinateBounds.ENDZONE_HOME.isInBounds(ballPosition)));
      }
    }
    return touchdown;
  }

  private KnockoutRecovery recoverKnockout(Player pPlayer) {
    if (pPlayer != null) {
      String playerId = (pPlayer != null) ? pPlayer.getId() : null;
      int recoveryRoll = getGameState().getDiceRoller().rollKnockoutRecovery();
      Game game = getGameState().getGame();
      InducementSet inducementSet = (pPlayer.getTeam() == game.getTeamHome()) ? game.getTurnDataHome().getInducementSet() : game.getTurnDataAway().getInducementSet();
      Inducement bloodweiserBabes = inducementSet.get(InducementType.BLOODWEISER_BABES);
      int bloodweiserBabeValue = (bloodweiserBabes != null) ? bloodweiserBabes.getValue() : 0;
      boolean isRecovering = DiceInterpreter.getInstance().isRecoveringFromKnockout(recoveryRoll, bloodweiserBabeValue);
      return new KnockoutRecovery(playerId, isRecovering, recoveryRoll, bloodweiserBabeValue);
    } else {
      return null;
    }
  }
  
  private HeatExhaustion heatExhaust(Player pPlayer) {
    String playerId = (pPlayer != null) ? pPlayer.getId() : null;
    int exhaustionRoll = getGameState().getDiceRoller().rollKnockoutRecovery();
    boolean isExhausted = DiceInterpreter.getInstance().isExhausted(exhaustionRoll);
    return new HeatExhaustion(playerId, isExhausted, exhaustionRoll);
  }
  
  private static boolean updateFumbblGame(GameState pGameState, boolean pNewHalf, boolean pTouchdown) {
    boolean isOk = true;
    FantasyFootballServer server = pGameState.getServer();
    if (server.getMode() == ServerMode.FUMBBL) {
      Game game = pGameState.getGame();
      if (!game.isTesting() && ((game.getTurnMode() == TurnMode.REGULAR) || pNewHalf || pTouchdown)) {
        server.getFumbblRequestProcessor().add(new FumbblRequestUpdateGamestate(pGameState));
      }
    }
    return isOk;
  }
  
  private boolean useSecretWeaponBribes(Team pTeam, String[] pPlayerIds) {
  	boolean allSuccessful = true;
    Game game = getGameState().getGame();
    if (game.getTeamHome() == pTeam) {
    	fBribesChoiceHome = ArrayTool.isProvided(pPlayerIds);
    } else {
    	fBribesChoiceAway = ArrayTool.isProvided(pPlayerIds);
    }
    if (ArrayTool.isProvided(pPlayerIds) && UtilInducementUse.useInducement(getGameState(), pTeam, InducementType.BRIBES, pPlayerIds.length)) {
      for (String playerId : pPlayerIds) {
        Player player = pTeam.getPlayerById(playerId);
        if (player != null) {
          int roll = getGameState().getDiceRoller().rollBribes();
          boolean successful = DiceInterpreter.getInstance().isBribesSuccessful(roll);
          getResult().addReport(new ReportBribesRoll(player.getId(), successful, roll));
          if (successful) {
            PlayerResult playerResult = game.getGameResult().getPlayerResult(player);
            playerResult.setHasUsedSecretWeapon(false);
          } else {
          	allSuccessful = false;
          }
        }
      }
    }
    return allSuccessful;
  }
  
  private void deactivateCards(InducementDuration pDuration) {
  	if (pDuration == null) {
  		return;
  	}
    Game game = getGameState().getGame();
  	for (Card card : game.getTurnDataHome().getInducementSet().getActiveCards()) {
    	if (pDuration == card.getDuration()) {
    	  if ((pDuration != InducementDuration.UNTIL_END_OF_OPPONENTS_TURN) || game.isHomePlaying()) {
          UtilSteps.deactivateCard(this, card);
    	  }
    	}
  	}
    for (Card card : game.getTurnDataAway().getInducementSet().getActiveCards()) {
      if (pDuration == card.getDuration()) {
        if ((pDuration != InducementDuration.UNTIL_END_OF_OPPONENTS_TURN) || !game.isHomePlaying()) {
          UtilSteps.deactivateCard(this, card);
        }
      }
    }
  }
  
  private boolean askForSecretWeaponBribes(Team pTeam) {
    Game game = getGameState().getGame();
    List<String> playerIds = new ArrayList<String>();
    for (Player player : pTeam.getPlayers()) {
      PlayerResult playerResult = game.getGameResult().getPlayerResult(player);
      if (playerResult.hasUsedSecretWeapon()) {
        playerIds.add(player.getId());
      }
    }
    if (playerIds.size() > 0) {
      InducementSet inducementSet = (game.getTeamHome() == pTeam) ? game.getTurnDataHome().getInducementSet() : game.getTurnDataAway().getInducementSet();
      if (inducementSet.hasUsesLeft(InducementType.BRIBES)) {
        Inducement bribes = inducementSet.get(InducementType.BRIBES);
        DialogBribesParameter dialogParameter = new DialogBribesParameter(pTeam.getId(), bribes.getUsesLeft()); 
        dialogParameter.addPlayerIds(playerIds.toArray(new String[playerIds.size()]));
        UtilDialog.showDialog(getGameState(), dialogParameter);
        return true;
      }
    }
  	return false;
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
	@Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addBoolean(fHandleSecretWeapons);
  	pByteList.addBoolean(fTouchdown);
  	pByteList.addBoolean(fBribesChoiceHome);
  	pByteList.addBoolean(fBribesChoiceAway);
  	pByteList.addBoolean(fNextSequencePushed);
  	pByteList.addBoolean(fRemoveUsedSecretWeapons);
  	pByteList.addBoolean(fNewHalf);
  	pByteList.addBoolean(fEndGame);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fHandleSecretWeapons = pByteArray.getBoolean();
  	fTouchdown = pByteArray.getBoolean();
  	fBribesChoiceHome = pByteArray.getBoolean();
  	fBribesChoiceAway = pByteArray.getBoolean();
  	fNextSequencePushed = pByteArray.getBoolean();
  	fRemoveUsedSecretWeapons = pByteArray.getBoolean();
  	fNewHalf = pByteArray.getBoolean();
  	fEndGame = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.HANDLE_SECRET_WEAPONS.addTo(jsonObject, fHandleSecretWeapons);
    IServerJsonOption.TOUCHDOWN.addTo(jsonObject, fTouchdown);
    IServerJsonOption.BRIBES_CHOICE_HOME.addTo(jsonObject, fBribesChoiceHome);
    IServerJsonOption.BRIBES_CHOICE_AWAY.addTo(jsonObject, fBribesChoiceAway);
    IServerJsonOption.NEXT_SEQUENCE_PUSHED.addTo(jsonObject, fNextSequencePushed);
    IServerJsonOption.REMOVE_USED_SECRET_WEAPONS.addTo(jsonObject, fRemoveUsedSecretWeapons);
    IServerJsonOption.NEW_HALF.addTo(jsonObject, fNewHalf);
    IServerJsonOption.END_GAME.addTo(jsonObject, fEndGame);
    return jsonObject;
  }
  
  @Override
  public StepEndTurn initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fHandleSecretWeapons = IServerJsonOption.HANDLE_SECRET_WEAPONS.getFrom(jsonObject);
    fTouchdown = IServerJsonOption.TOUCHDOWN.getFrom(jsonObject);
    fBribesChoiceHome = IServerJsonOption.BRIBES_CHOICE_HOME.getFrom(jsonObject);
    fBribesChoiceAway = IServerJsonOption.BRIBES_CHOICE_AWAY.getFrom(jsonObject);
    fNextSequencePushed = IServerJsonOption.NEXT_SEQUENCE_PUSHED.getFrom(jsonObject);
    fRemoveUsedSecretWeapons = IServerJsonOption.REMOVE_USED_SECRET_WEAPONS.getFrom(jsonObject);
    fNewHalf = IServerJsonOption.NEW_HALF.getFrom(jsonObject);
    fEndGame = IServerJsonOption.END_GAME.getFrom(jsonObject);
    return this;
  }

}
