package com.balancedbytes.games.ffb.server.handler;

import java.nio.channels.SocketChannel;

import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPasswordChallenge;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.ServerMode;
import com.balancedbytes.games.ffb.server.fumbbl.FumbblRequestPasswordChallenge;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerPasswordChallenge extends ServerCommandHandler {

  protected ServerCommandHandlerPasswordChallenge(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_PASSWORD_CHALLENGE;
  }

  public void handleCommand(ReceivedCommand pReceivedCommand) {
    
    ClientCommandPasswordChallenge passwordChallengeCommand = (ClientCommandPasswordChallenge) pReceivedCommand.getCommand();
    SocketChannel sender = pReceivedCommand.getSender();

    String challenge = null;
    if ((ServerMode.FUMBBL == getServer().getMode()) && StringTool.isProvided(passwordChallengeCommand.getCoach())) {
      getServer().getFumbblRequestProcessor().add(new FumbblRequestPasswordChallenge(passwordChallengeCommand.getCoach(), sender));
    } else {
      getServer().getCommunication().sendPasswordChallenge(sender, challenge);
    }
    
  }

}
