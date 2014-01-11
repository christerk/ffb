package com.balancedbytes.games.ffb.client.state;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.client.ActionKey;
import com.balancedbytes.games.ffb.client.ClientReplayer;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IProgressListener;
import com.balancedbytes.games.ffb.client.dialog.DialogProgressBar;
import com.balancedbytes.games.ffb.client.dialog.IDialog;
import com.balancedbytes.games.ffb.client.dialog.IDialogCloseListener;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ServerCommand;
import com.balancedbytes.games.ffb.net.commands.ServerCommandReplay;

/**
 * 
 * @author Kalimar
 */
public class ClientStateReplay extends ClientState implements IDialogCloseListener, IProgressListener {
  
  private DialogProgressBar fDialogProgress;
  private List<ServerCommand> fReplayList;
  
  protected ClientStateReplay(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public ClientStateId getId() {
    return ClientStateId.REPLAY;
  }
    
  public void enterState() {
    super.enterState();
    setSelectable(true);
    setClickable(false);
    ClientReplayer replayer = getClient().getReplayer();
    if (ClientMode.REPLAY == getClient().getMode()) {
      replayer.start();
      getClient().getCommunication().sendReplay(getClient().getParameters().getGameId(), 0);
    } else {
      if (fReplayList == null) {
        fReplayList = new ArrayList<ServerCommand>();
        showProgressDialog();
        getClient().getCommunication().sendReplay(0, replayer.getFirstCommandNr());
      } else {
        replayer.positionOnLastCommand();
        replayer.getReplayControl().setActive(true);
      }
    }
  }
  
  public void handleCommand(NetCommand pNetCommand) {
    ClientReplayer replayer = getClient().getReplayer();
    switch (pNetCommand.getId()) {
      case SERVER_REPLAY:
        ServerCommandReplay replayCommand = (ServerCommandReplay) pNetCommand;
        initProgress(0, replayCommand.getTotalNrOfCommands());
//        ServerCommand[] test = replayCommand.getReplayCommands();
//        if (ArrayTool.isProvided(test)) {
//          System.out.println(test[0].getCommandNr() + " - " + test[test.length - 1].getCommandNr());
//        }
        for (ServerCommand command : replayCommand.getReplayCommands()) {
//          System.out.println(command.toXml(0));
          fReplayList.add(command);
          updateProgress(fReplayList.size(), "Received Step %d of %d.");
        }
        if (fReplayList.size() >= replayCommand.getTotalNrOfCommands()) {
          
        	fDialogProgress.hideDialog();

        	// replay received - signal the server that this socket can be closed
          getClient().getCommunication().sendReplay(getClient().getParameters().getGameId(), -1);

          ServerCommand[] replayCommands = fReplayList.toArray(new ServerCommand[fReplayList.size()]);
          fDialogProgress = new DialogProgressBar(getClient(), "Initializing Replay");
          fDialogProgress.showDialog(this);
          replayer.init(replayCommands, this);
          fDialogProgress.hideDialog();
          
          if (ClientMode.REPLAY == getClient().getMode()) {
            replayer.positionOnFirstCommand();
          } else {
            replayer.positionOnLastCommand();
          }
          replayer.getReplayControl().setActive(true);

        }
        break;
      case SERVER_GAME_STATE:
        if (ClientMode.REPLAY == getClient().getMode()) {
          fReplayList = new ArrayList<ServerCommand>();
          showProgressDialog();
        }
        break;
      default:
      	break;
    }
  }
  
  public void dialogClosed(IDialog pDialog) {
  }
  
  public void updateProgress(int pProgress) {
    updateProgress(pProgress, "Initialized Frame %d of %d.");
  }
  
  private void updateProgress(int pProgress, String pFormat) {
    String message = String.format(pFormat, pProgress, fDialogProgress.getMaximum());
    fDialogProgress.updateProgress(pProgress, message);
  }
  
  public void initProgress(int pMinimum, int pMaximum) {
    fDialogProgress.setMinimum(pMinimum);
    fDialogProgress.setMaximum(pMaximum);
  }
  
  public boolean actionKeyPressed(ActionKey pActionKey) {
    boolean actionHandled = false;
    if ((ClientMode.SPECTATOR == getClient().getMode()) && (pActionKey == ActionKey.MENU_REPLAY)) {
      actionHandled = true;
      getClient().getReplayer().stop();
      getClient().updateClientState();
      getClient().getUserInterface().getGameMenuBar().refresh();
    }
    return actionHandled;
  }
    
  private void showProgressDialog() {
    fDialogProgress = new DialogProgressBar(getClient(), "Receiving Replay");
    fDialogProgress.showDialog(this);
  }
        
}
