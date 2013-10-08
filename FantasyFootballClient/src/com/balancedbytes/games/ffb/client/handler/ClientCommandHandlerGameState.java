package com.balancedbytes.games.ffb.client.handler;

import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingUtilities;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IconCache;
import com.balancedbytes.games.ffb.client.PlayerIconFactory;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.client.dialog.DialogProgressBar;
import com.balancedbytes.games.ffb.client.dialog.IDialog;
import com.balancedbytes.games.ffb.client.dialog.IDialogCloseListener;
import com.balancedbytes.games.ffb.client.util.UtilThrowTeamMate;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Roster;
import com.balancedbytes.games.ffb.model.RosterPosition;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ServerCommandGameState;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandHandlerGameState extends ClientCommandHandler implements IDialogCloseListener {

  protected ClientCommandHandlerGameState(FantasyFootballClient pClient) {
    super(pClient);
  }

  public NetCommandId getId() {
    return NetCommandId.SERVER_GAME_STATE;
  }

  public boolean handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {
    
    ServerCommandGameState gameStateCommand = (ServerCommandGameState) pNetCommand;
    Game game = gameStateCommand.getGame();
    
    IconCache iconCache = getClient().getUserInterface().getIconCache();

    // update player icons and collect all icon urls needed for the game
    Set<String> iconUrls = new HashSet<String>();
    
    addIconUrl(iconUrls, IconCache.getTeamLogoUrl(game.getTeamHome()));
    addIconUrl(iconUrls, IconCache.getTeamLogoUrl(game.getTeamAway()));
    
    addRosterIconUrls(iconUrls, game.getTeamHome().getRoster(), true);
    addRosterIconUrls(iconUrls, game.getTeamAway().getRoster(), false);

    for (Player player : game.getPlayers()) {
      boolean homePlayer = game.getTeamHome().hasPlayer(player);
      addIconUrl(iconUrls, PlayerIconFactory.getPlayerPortraitUrl(player));
      addIconUrl(iconUrls, PlayerIconFactory.getPlayerIconUrl(player, homePlayer, true));
      addIconUrl(iconUrls, PlayerIconFactory.getPlayerIconUrl(player, homePlayer, false));
    }

    Set<String> iconUrlsToDownload = new HashSet<String>();
    for (String iconUrl : iconUrls) {
      if (!iconCache.loadIconFromArchive(iconUrl)) {
        // TODO: FUMBBL wrong empty player portraits
        if (!iconUrl.endsWith("/i/0")) {
          iconUrlsToDownload.add(iconUrl);
        }
      }
    }
    
    int nrOfIcons = iconUrlsToDownload.size();
    if (nrOfIcons > 0) {
      
      DialogProgressBar dialogProgress = new DialogProgressBar(getClient(), "Loading team icons", 0, nrOfIcons);
      dialogProgress.showDialog(this);

      // preload all icon urls now
      int currentIconNr = 0;
      for (String iconUrl : iconUrlsToDownload) {
        iconCache.loadIconFromUrl(iconUrl);
        String message = String.format("Loaded icon %d of %d.", ++currentIconNr, nrOfIcons);
        dialogProgress.updateProgress(currentIconNr, message);
      }
      
      dialogProgress.hideDialog();

    }
      
    getClient().setGame(game);
    UtilThrowTeamMate.updateThrownPlayer(getClient());

    if (pMode == ClientCommandHandlerMode.PLAYING) {
      SwingUtilities.invokeLater(
        new Runnable() {
          public void run() {
            UserInterface userInterface = getClient().getUserInterface();
            userInterface.init();
            getClient().updateClientState();
            userInterface.getDialogManager().updateDialog();
            userInterface.getGameMenuBar().updateMissingPlayers();
            userInterface.getGameMenuBar().updateInducements();
            userInterface.getChat().requestChatInputFocus();
          }
        }
      );
    }
    
    return true;
        
  }
  
  public void dialogClosed(IDialog pDialog) {
    getClient().stopClient();
  }
  
  private void addIconUrl(Set<String> pIconUrls, String pIconUrl) {
    if (StringTool.isProvided(pIconUrl)) {
      IconCache iconCache = getClient().getUserInterface().getIconCache();
      if (iconCache.getIconByUrl(pIconUrl) == null) {
        pIconUrls.add(pIconUrl);
      }
    }
  }
  
  private void addRosterIconUrls(Set<String> pIconUrls, Roster pRoster, boolean pHome) {
    for (RosterPosition position : pRoster.getPositions()) {
      addIconUrl(pIconUrls, PlayerIconFactory.getPlayerPortraitUrl(position));
      for (int i = 0; i < position.getNrOfIcons(); i++) {
        addIconUrl(pIconUrls, PlayerIconFactory.getPositionIconUrl(position, pHome, true, i));
        addIconUrl(pIconUrls, PlayerIconFactory.getPositionIconUrl(position, pHome, false, i));
      }
    }
  }
  
}
