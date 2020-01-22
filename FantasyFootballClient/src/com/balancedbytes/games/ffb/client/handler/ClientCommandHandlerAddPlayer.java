package com.balancedbytes.games.ffb.client.handler;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.model.RosterPosition;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ServerCommandAddPlayer;
import com.balancedbytes.games.ffb.util.UtilBox;

public class ClientCommandHandlerAddPlayer extends ClientCommandHandler {

  protected ClientCommandHandlerAddPlayer(FantasyFootballClient pClient) {
    super(pClient);
  }

  public NetCommandId getId() {
    return NetCommandId.SERVER_ADD_PLAYER;
  }

  public boolean handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {
    
    ServerCommandAddPlayer addPlayerCommand = (ServerCommandAddPlayer) pNetCommand;
    
    Game game = getClient().getGame();
    
    Team team = game.getTeamHome().getId().equals(addPlayerCommand.getTeamId()) ? game.getTeamHome() : game.getTeamAway();
    Player oldPlayer = team.getPlayerById(addPlayerCommand.getPlayer().getId()); 
    if (oldPlayer != null) {
      oldPlayer.init(addPlayerCommand.getPlayer());
    } else {
      team.addPlayer(addPlayerCommand.getPlayer());
      RosterPosition rosterPosition = team.getRoster().getPositionById(addPlayerCommand.getPlayer().getPositionId());
      addPlayerCommand.getPlayer().updatePosition(rosterPosition);
    }
    
    game.getFieldModel().setPlayerState(addPlayerCommand.getPlayer(), addPlayerCommand.getPlayerState());
    UtilBox.putPlayerIntoBox(game, addPlayerCommand.getPlayer());
    
    PlayerResult playerResult = game.getGameResult().getPlayerResult(addPlayerCommand.getPlayer());
    playerResult.setSendToBoxReason(addPlayerCommand.getSendToBoxReason());
    playerResult.setSendToBoxTurn(addPlayerCommand.getSendToBoxTurn());
    playerResult.setSendToBoxHalf(addPlayerCommand.getSendToBoxHalf());
    
    //team.setTeamValue(UtilTeamValue.findTeamValue(team, game));
    
    if (pMode == ClientCommandHandlerMode.PLAYING) {
      refreshGameMenuBar();
      refreshFieldComponent();
      refreshSideBars();
    }
        
    return true;
    
  }
  
}
