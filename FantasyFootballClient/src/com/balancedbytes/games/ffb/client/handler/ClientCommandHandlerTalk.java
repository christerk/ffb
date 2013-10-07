package com.balancedbytes.games.ffb.client.handler;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.client.ui.ChatComponent;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ServerCommandTalk;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandHandlerTalk extends ClientCommandHandler {

  protected ClientCommandHandlerTalk(FantasyFootballClient pClient) {
    super(pClient);
  }

  public NetCommandId getId() {
    return NetCommandId.SERVER_TALK;
  }

  public boolean handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {

    ServerCommandTalk talkCommand = (ServerCommandTalk) pNetCommand;
    
    Game game = getClient().getGame();
    String coach = talkCommand.getCoach();
    String[] allTalk = talkCommand.getTalks();
    if (ArrayTool.isProvided(allTalk)) {
      for (String talk : allTalk) {
        StringBuilder status = new StringBuilder();
        TextStyle style = TextStyle.NONE;
        if (StringTool.isProvided(coach)) {
          status.append("<");
          status.append(coach);
          status.append("> ");
          if (coach.equals(game.getTeamHome().getCoach())) {
            style = TextStyle.HOME;
          } else if (coach.equals(game.getTeamAway().getCoach())) {
            style = TextStyle.AWAY;
          } else {
            style = TextStyle.SPECTATOR;
          }
        }
        status.append(talk);
        ChatComponent chat = getClient().getUserInterface().getChat();
        chat.append(null, style, status.toString());
        chat.append(null, null, null);
      }
    }
    
    return true;
    
  }

}
