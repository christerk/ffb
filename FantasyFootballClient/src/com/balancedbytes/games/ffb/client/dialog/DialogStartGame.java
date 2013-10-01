package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IIconProperty;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class DialogStartGame extends DialogYesOrNoQuestion {

  public DialogStartGame(FantasyFootballClient pClient) {
    super(pClient, "Start Game", createMessages(pClient), IIconProperty.GAME_REF);
  }

  public DialogId getId() {
    return DialogId.START_GAME;
  }
  
  private static String[] createMessages(FantasyFootballClient pClient) {
    Game game = pClient.getGame();
    String[] messages = new String[4];
    messages[0] = "Do you want to start a game with these teams?";
    StringBuilder line = new StringBuilder();
    String tvHome = StringTool.formatThousands(game.getTeamHome().getTeamValue() / 1000);
    line.append(game.getTeamHome().getName()).append(" (TV ").append(tvHome).append("k)").append(" played by ").append(game.getTeamHome().getCoach());
    messages[1] = line.toString();
    line = new StringBuilder();
    String tvAway = StringTool.formatThousands(game.getTeamAway().getTeamValue() / 1000);
    line.append(game.getTeamAway().getName()).append(" (TV ").append(tvAway).append("k)").append(" played by ").append(game.getTeamAway().getCoach());
    messages[2] = line.toString();
    return messages;
  }

}
