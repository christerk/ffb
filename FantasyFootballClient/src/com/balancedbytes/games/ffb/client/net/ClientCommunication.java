package com.balancedbytes.games.ffb.client.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardType;
import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.ConcedeGameStatus;
import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerChoiceMode;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.Pushback;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.TeamSetup;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.handler.ClientCommandHandlerMode;
import com.balancedbytes.games.ffb.model.InducementSet;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.net.INetCommandHandler;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandActingPlayer;
import com.balancedbytes.games.ffb.net.commands.ClientCommandApothecaryChoice;
import com.balancedbytes.games.ffb.net.commands.ClientCommandBlock;
import com.balancedbytes.games.ffb.net.commands.ClientCommandBlockChoice;
import com.balancedbytes.games.ffb.net.commands.ClientCommandBuyCard;
import com.balancedbytes.games.ffb.net.commands.ClientCommandBuyInducements;
import com.balancedbytes.games.ffb.net.commands.ClientCommandCloseSession;
import com.balancedbytes.games.ffb.net.commands.ClientCommandCoinChoice;
import com.balancedbytes.games.ffb.net.commands.ClientCommandConcedeGame;
import com.balancedbytes.games.ffb.net.commands.ClientCommandConfirm;
import com.balancedbytes.games.ffb.net.commands.ClientCommandDebugClientState;
import com.balancedbytes.games.ffb.net.commands.ClientCommandEndTurn;
import com.balancedbytes.games.ffb.net.commands.ClientCommandFollowupChoice;
import com.balancedbytes.games.ffb.net.commands.ClientCommandFoul;
import com.balancedbytes.games.ffb.net.commands.ClientCommandGaze;
import com.balancedbytes.games.ffb.net.commands.ClientCommandHandOver;
import com.balancedbytes.games.ffb.net.commands.ClientCommandIllegalProcedure;
import com.balancedbytes.games.ffb.net.commands.ClientCommandInterceptorChoice;
import com.balancedbytes.games.ffb.net.commands.ClientCommandJoin;
import com.balancedbytes.games.ffb.net.commands.ClientCommandJourneymen;
import com.balancedbytes.games.ffb.net.commands.ClientCommandKickoff;
import com.balancedbytes.games.ffb.net.commands.ClientCommandMove;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPass;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPasswordChallenge;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPettyCash;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPing;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPlayerChoice;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPushback;
import com.balancedbytes.games.ffb.net.commands.ClientCommandReceiveChoice;
import com.balancedbytes.games.ffb.net.commands.ClientCommandReplay;
import com.balancedbytes.games.ffb.net.commands.ClientCommandRequestVersion;
import com.balancedbytes.games.ffb.net.commands.ClientCommandSetMarker;
import com.balancedbytes.games.ffb.net.commands.ClientCommandSetupPlayer;
import com.balancedbytes.games.ffb.net.commands.ClientCommandStartGame;
import com.balancedbytes.games.ffb.net.commands.ClientCommandTalk;
import com.balancedbytes.games.ffb.net.commands.ClientCommandTeamSetupDelete;
import com.balancedbytes.games.ffb.net.commands.ClientCommandTeamSetupLoad;
import com.balancedbytes.games.ffb.net.commands.ClientCommandTeamSetupSave;
import com.balancedbytes.games.ffb.net.commands.ClientCommandThrowTeamMate;
import com.balancedbytes.games.ffb.net.commands.ClientCommandTimeoutPossible;
import com.balancedbytes.games.ffb.net.commands.ClientCommandTouchback;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseApothecary;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseInducement;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseReRoll;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUserSettings;
import com.balancedbytes.games.ffb.net.commands.ClientCommandWizardSpell;
import com.balancedbytes.games.ffb.net.commands.ServerCommand;

/**
 * 
 * @author Kalimar
 */
public class ClientCommunication implements Runnable, INetCommandHandler {

  private boolean fStopped;
  private List<NetCommand> fCommandQueue;
  private FantasyFootballClient fClient;
  
  public ClientCommunication(FantasyFootballClient pClient) {
    fClient = pClient;
    fCommandQueue = new ArrayList<NetCommand>();
  }

  public void handleCommand(NetCommand pNetCommand) {
    synchronized (fCommandQueue) {
      fCommandQueue.add(pNetCommand);
//      System.out.println("queued " + pNetCommand.toXml(-1));
      fCommandQueue.notify();
    }
  }
  
  public void stop() {
  	if (!fStopped) {
      fStopped = true;
      synchronized (fCommandQueue) {
        fCommandQueue.notifyAll();
      }
  	}
  }
  
  public void run() {
    
    while (true) {
      
      NetCommand netCommand = null;
      synchronized (fCommandQueue) {
        try {
          while (fCommandQueue.isEmpty() && !fStopped) {
            fCommandQueue.wait();
          }
        } catch (InterruptedException e) {
          break;
        }
        if (fStopped) {
          break;
        }
        netCommand = fCommandQueue.remove(0);
      }

      switch (netCommand.getId()) {
        case SERVER_PING:
        case SERVER_TALK:
        case SERVER_SOUND:
        case SERVER_REPLAY:
        case INTERNAL_SERVER_SOCKET_CLOSED:
          break;
        default:
          getClient().getReplayer().add((ServerCommand) netCommand);
          break;
      }
      ClientCommandHandlerMode mode = getClient().getReplayer().isReplaying() ? ClientCommandHandlerMode.QUEUING : ClientCommandHandlerMode.PLAYING;
      getClient().getCommandHandlerFactory().handleNetCommand(netCommand, mode);

    }
    
  }
  
  protected void send(NetCommand pNetCommand) {
    try {
      getClient().getCommandEndpoint().send(pNetCommand);
    } catch (IOException pIoException) {
      throw new FantasyFootballException(pIoException);
    }
  }

  public void sendDebugClientState(ClientStateId pClientStateId) {
    send(new ClientCommandDebugClientState(pClientStateId));
  }
  
  public void sendJoin(String pCoach, String pPassword, long pGameId, String pGameName, String pTeamId, String pTeamName) {
    ClientCommandJoin joinCommand = new ClientCommandJoin(getClient().getMode());
    joinCommand.setCoach(pCoach);
    joinCommand.setPassword(pPassword);
    joinCommand.setGameId(pGameId);
    joinCommand.setGameName(pGameName);
    joinCommand.setTeamId(pTeamId);
    joinCommand.setTeamName(pTeamName);
    send(joinCommand);
  }
  
  public void sendJourneymen(String[] pPositionsIds, int[] pSlots) {
    send(new ClientCommandJourneymen(pPositionsIds, pSlots));
  }
  
  public void sendTalk(String pTalk) {
    send(new ClientCommandTalk(pTalk));
  }
  
  public void sendPasswordChallenge() {
    send(new ClientCommandPasswordChallenge(getClient().getParameters().getCoach()));
  }
  
  public void sendPing(long pPing, boolean pHasEntropy, byte pEntropy) {
    send(new ClientCommandPing(pPing, pHasEntropy, pEntropy));
  }
  
  public void sendSetupPlayer(Player pPlayer, FieldCoordinate pCoordinate) {
    send(new ClientCommandSetupPlayer(pPlayer.getId(), pCoordinate));
  }
  
  public void sendTouchback(FieldCoordinate pBallCoordinate) {
    send(new ClientCommandTouchback(pBallCoordinate));
  }
  
  public void sendPlayerMove(String pActingPlayerId, FieldCoordinate pCoordinateFrom, FieldCoordinate[] pCoordinatesTo) {
    send(new ClientCommandMove(pActingPlayerId, pCoordinateFrom, pCoordinatesTo));
  }

  public void sendStartGame() {
    send(new ClientCommandStartGame());
  }
  
  public void sendEndTurn() {
    send(new ClientCommandEndTurn());
  }
  
  public void sendConfirm() {
    send(new ClientCommandConfirm());
  }
  
  public void sendCloseSession() {
    send(new ClientCommandCloseSession());
  }
  
  public void sendConcedeGame(ConcedeGameStatus pStatus) {
    send(new ClientCommandConcedeGame(pStatus));
  }
  
  public void sendTimeoutPossible() {
    send(new ClientCommandTimeoutPossible());
  }

  public void sendIllegalProcedure() {
    send(new ClientCommandIllegalProcedure());
  }
  
  public void sendRequestVersion() {
    send(new ClientCommandRequestVersion());
  }

  public void sendCoinChoice(boolean pChoiceHeads) {
    send(new ClientCommandCoinChoice(pChoiceHeads));
  }
  
  public void sendReceiveChoice(boolean pChoiceReceive) {
    send(new ClientCommandReceiveChoice(pChoiceReceive));
  }
  
  public void sendPlayerChoice(PlayerChoiceMode pMode, Player[] pPlayers) {
    send(new ClientCommandPlayerChoice(pMode, pPlayers));
  }
  
  public void sendPettyCash(int pPettyCash) {
    send(new ClientCommandPettyCash(pPettyCash));
  }

  public void sendActingPlayer(Player pPlayer, PlayerAction pPlayerAction, boolean pLeaping) {
    String playerId = (pPlayer != null) ? pPlayer.getId() : null; 
    send(new ClientCommandActingPlayer(playerId, pPlayerAction, pLeaping));
  }
  
  public void sendUseReRoll(ReRolledAction pReRolledAction, ReRollSource pReRollSource) {
    send(new ClientCommandUseReRoll(pReRolledAction, pReRollSource));
  }
  
  public void sendUseSkill(Skill pSkill, boolean pSkillUsed) {
    send(new ClientCommandUseSkill(pSkill, pSkillUsed));
  }
  
  public void sendKickoff(FieldCoordinate pBallCoordinate) {
    send(new ClientCommandKickoff(pBallCoordinate));
  }
  
  public void sendHandOver(String pActingPlayerId, Player pCatcher) {
    String catcherId = (pCatcher != null) ? pCatcher.getId() : null;
    send(new ClientCommandHandOver(pActingPlayerId, catcherId));
  }

  public void sendGaze(String pActingPlayerId, Player pVictim) {
    String victimId = (pVictim != null) ? pVictim.getId() : null;
    send(new ClientCommandGaze(pActingPlayerId, victimId));
  }

  public void sendPass(String pActingPlayerId, FieldCoordinate pTargetCoordinate) {
    send(new ClientCommandPass(pActingPlayerId, pTargetCoordinate));
  }

  public void sendBlock(String pActingPlayerId, Player pDefender, boolean pUsingStab) {
    String defenderId = (pDefender != null) ? pDefender.getId() : null;
    send(new ClientCommandBlock(pActingPlayerId, defenderId, pUsingStab));
  }
  
  public void sendFoul(String pActingPlayerId, Player pDefender) {
    String defenderId = (pDefender != null) ? pDefender.getId() : null;
    send(new ClientCommandFoul(pActingPlayerId, defenderId));
  }

  public void sendBlockChoice(int pDiceIndex) {
    send(new ClientCommandBlockChoice(pDiceIndex));
  }

  public void sendUseInducement(InducementType pInducement) {
    send(new ClientCommandUseInducement(pInducement));
  }

  public void sendUseInducement(Card pCard) {
    send(new ClientCommandUseInducement(pCard));
  }

  public void sendUseInducement(InducementType pInducement, String pPlayerId) {
    send(new ClientCommandUseInducement(pInducement, pPlayerId));
  }

  public void sendUseInducement(Card pCard, String pPlayerId) {
    send(new ClientCommandUseInducement(pCard, pPlayerId));
  }

  public void sendUseInducement(InducementType pInducement, String[] pPlayerIds) {
    send(new ClientCommandUseInducement(pInducement, pPlayerIds));
  }

  public void sendPushback(Pushback pPushback) {
    send(new ClientCommandPushback(pPushback));
  }
  
  public void sendFollowupChoice(boolean pFollowupChoice) {
    send(new ClientCommandFollowupChoice(pFollowupChoice));
  }
  
  public void sendInterceptorChoice(Player pInterceptor) {
    String interceptorId = (pInterceptor != null) ? pInterceptor.getId() : null;
    send(new ClientCommandInterceptorChoice(interceptorId));
  }
  
  public void sendTeamSetupLoad(String pSetupName) {
    send(new ClientCommandTeamSetupLoad(pSetupName));
  }
  
  public void sendTeamSetupDelete(String pSetupName) {
    send(new ClientCommandTeamSetupDelete(pSetupName));
  }

  public void sendTeamSetupSave(TeamSetup pTeamSetup) {
    send(new ClientCommandTeamSetupSave(pTeamSetup.getName(), pTeamSetup.getPlayerNumbers(), pTeamSetup.getCoordinates()));
  }
  
  public void sendUseApothecary(String pPlayerId, boolean pApothecaryUsed) {
    send(new ClientCommandUseApothecary(pPlayerId, pApothecaryUsed));
  }

  public void sendApothecaryChoice(String pPlayerId, PlayerState pPlayerState, SeriousInjury pSeriousInjury) {
    send(new ClientCommandApothecaryChoice(pPlayerId, pPlayerState, pSeriousInjury));
  }
 
  public void sendUserSettings(String[] pSettingNames, String[] pSettingValues) {
    send(new ClientCommandUserSettings(pSettingNames, pSettingValues));
  }
  
  public void sendReplay(long pGameId, int pReplayToCommandNr) {
    send(new ClientCommandReplay(pGameId, pReplayToCommandNr));
  }
  
  public void sendThrowTeamMate(String pActingPlayerId, FieldCoordinate pTargetCoordinate) {
    send(new ClientCommandThrowTeamMate(pActingPlayerId, pTargetCoordinate));
  }
  
  public void sendThrowTeamMate(String pActingPlayerId, String pPlayerId) {
    send(new ClientCommandThrowTeamMate(pActingPlayerId, pPlayerId));
  }
  
  public void sendBuyInducements(String pTeamId, int pAvailableGold, InducementSet pInducementSet, String[] pStarPlayerPositionIds, String[] pMercenaryPositionIds, Skill[] pMercenarySkills) {
    send(new ClientCommandBuyInducements(pTeamId, pAvailableGold, pInducementSet, pStarPlayerPositionIds, pMercenaryPositionIds, pMercenarySkills));  
  }
  
  public void sendBuyCard(CardType pType) {
  	send(new ClientCommandBuyCard(pType));
  }
  
  public void sendSetMarker(String pPlayerId, String pText) {
    send(new ClientCommandSetMarker(pPlayerId, pText));
  }
  
  public void sendSetMarker(FieldCoordinate pCoordinate, String pText) {
    send(new ClientCommandSetMarker(pCoordinate, pText));
  }

  public void sendWizardSpell(SpecialEffect pWizardSpell, FieldCoordinate pCoordinate) {
    send(new ClientCommandWizardSpell(pWizardSpell, pCoordinate));
  }

  public FantasyFootballClient getClient() {
    return fClient;
  }

}
