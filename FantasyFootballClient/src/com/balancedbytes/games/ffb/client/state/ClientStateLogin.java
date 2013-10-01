package com.balancedbytes.games.ffb.client.state;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.GameList;
import com.balancedbytes.games.ffb.GameListEntry;
import com.balancedbytes.games.ffb.PasswordChallenge;
import com.balancedbytes.games.ffb.TeamListEntry;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.client.dialog.DialogGameChoice;
import com.balancedbytes.games.ffb.client.dialog.DialogInformation;
import com.balancedbytes.games.ffb.client.dialog.DialogLogin;
import com.balancedbytes.games.ffb.client.dialog.DialogTeamChoice;
import com.balancedbytes.games.ffb.client.dialog.IDialog;
import com.balancedbytes.games.ffb.client.dialog.IDialogCloseListener;
import com.balancedbytes.games.ffb.dialog.DialogJoinParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.ServerStatus;
import com.balancedbytes.games.ffb.net.commands.ServerCommandGameList;
import com.balancedbytes.games.ffb.net.commands.ServerCommandJoin;
import com.balancedbytes.games.ffb.net.commands.ServerCommandPasswordChallenge;
import com.balancedbytes.games.ffb.net.commands.ServerCommandStatus;
import com.balancedbytes.games.ffb.net.commands.ServerCommandTeamList;
import com.balancedbytes.games.ffb.net.commands.ServerCommandVersion;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class ClientStateLogin extends ClientState implements IDialogCloseListener {
  
  private static final Pattern _PATTERN_VERSION = Pattern.compile("([0-9]+)\\.([0-9]+)\\.([0-9]+)");
  
  // this state changes after synchronizing
  
  private DialogInformation fWaitingDialog;
  private ServerStatus fLastServerError;
  
  private String fGameName;
  private String fTeamHomeId;
  private String fTeamHomeName;
  private String fTeamAwayName;
  private byte[] fEncodedPassword;
  private int fPasswordLength;
  private boolean fListGames;
  private long fGameId;
  
  protected ClientStateLogin(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public ClientStateId getId() {
    return ClientStateId.LOGIN;
  }
  
  public void enterState() {
    super.enterState();
    setSelectable(false);
    setClickable(false);
    if (StringTool.isProvided(getClient().getParameters().getTeamId())) {
    	fTeamHomeId = getClient().getParameters().getTeamId();
    	fTeamHomeName = getClient().getParameters().getTeamName();
    	fTeamAwayName = null;
    } else {
    	fTeamHomeId = null;
    	fTeamHomeName = getClient().getParameters().getTeamHome();
    	fTeamAwayName = getClient().getParameters().getTeamAway();
    }
    getClient().getCommunication().sendRequestVersion();
  }
  
  public void dialogClosed(IDialog pDialog) {
    pDialog.hideDialog();
    switch (pDialog.getId()) {
      case GAME_COACH_PASSWORD:
        DialogLogin loginDialog = (DialogLogin) pDialog;
        fGameName = loginDialog.getGameName();
        fListGames = loginDialog.isListGames();
        fEncodedPassword = loginDialog.getEncodedPassword();
        fPasswordLength = loginDialog.getPasswordLength();
        sendChallenge();
        break;
      case INFORMATION:
        DialogInformation informationDialog = (DialogInformation) pDialog;
        if (informationDialog.getOptionType() == DialogInformation.OK_DIALOG) {
          showLoginDialog();
        } else {
          getClient().stopClient();
        }
        break;
      case TEAM_CHOICE:
        DialogTeamChoice teamChoiceDialog = (DialogTeamChoice) pDialog;
        TeamListEntry selectedTeamEntry = teamChoiceDialog.getSelectedTeamEntry();
        if (selectedTeamEntry != null) {
          fTeamHomeId = selectedTeamEntry.getTeamId();
          fTeamHomeName = selectedTeamEntry.getTeamName();
          sendChallenge();
        } else {
          showLoginDialog();
        }
        break;
      case GAME_CHOICE:
        DialogGameChoice gameChoiceDialog = (DialogGameChoice) pDialog;
        GameListEntry selectedGameEntry = gameChoiceDialog.getSelectedGameEntry();
        if (selectedGameEntry != null) {
        	fGameId = selectedGameEntry.getGameId();
          fListGames = false;
          sendChallenge();
        } else {
          showLoginDialog();
        }
        break;
      default:
      	break;
    }
  }
  
  public void handleNetCommand(NetCommand pNetCommand) {
    Game game = getClient().getGame();
    UserInterface userInterface = getClient().getUserInterface();
    switch (pNetCommand.getId()) {
      case SERVER_VERSION:
        ServerCommandVersion versionCommand = (ServerCommandVersion) pNetCommand;
        if (checkVersion(versionCommand.getServerVersion(), versionCommand.getClientVersion())) {
          String[] properties = versionCommand.getClientProperties();
          for (String property : properties) {
            getClient().setProperty(property, versionCommand.getClientPropertyValue(property));
          }
          showLoginDialog();
        }
        break;
      case SERVER_STATUS:
        ServerCommandStatus errorCommand = (ServerCommandStatus) pNetCommand;
        fLastServerError = errorCommand.getStatus();
        DialogInformation informationDialog = null;
        if (fLastServerError == ServerStatus.FUMBBL_ERROR) {
          informationDialog = new DialogInformation(getClient(), "Fumbbl Error", errorCommand.getMessage(), DialogInformation.CANCEL_DIALOG);
        } else {
          informationDialog = new DialogInformation(getClient(), "Server Error", fLastServerError.getMessage(), DialogInformation.OK_DIALOG);
        }
        informationDialog.showDialog(this);
        break;
      case SERVER_TEAM_LIST:
        ServerCommandTeamList teamListCommand = (ServerCommandTeamList) pNetCommand;
        DialogTeamChoice teamChoiceDialog = new DialogTeamChoice(getClient(), teamListCommand.getTeamList());
        teamChoiceDialog.showDialog(this);
        break;
      case SERVER_GAME_LIST:
        ServerCommandGameList gameListCommand = (ServerCommandGameList) pNetCommand;
        GameList gameList = gameListCommand.getGameList();
        if (gameList.size() > 0) {
          DialogGameChoice gameChoiceDialog = new DialogGameChoice(getClient(), gameListCommand.getGameList());
          gameChoiceDialog.showDialog(this);
        } else {
          DialogInformation noGamesDialog = new DialogInformation(getClient(), "No Open Games", "You do not have any open games to join.", DialogInformation.OK_DIALOG);
          noGamesDialog.showDialog(this);
        }
        break;
      case SERVER_JOIN:
        ServerCommandJoin joinCommand = (ServerCommandJoin) pNetCommand;
        if (joinCommand.getPlayers().length <= 1) {
          if ((getClient().getParameters().getGameId() == 0) && StringTool.isProvided(fGameName)) {
            getClient().getUserInterface().getStatusReport().reportGameName(fGameName);
          }
          game.setDialogParameter(new DialogJoinParameter());
        } else {
          game.setDialogParameter(null);
        }
        userInterface.getDialogManager().updateDialog();
        break;
      case SERVER_PASSWORD_CHALLENGE:
        ServerCommandPasswordChallenge passwordChallengeCommand = (ServerCommandPasswordChallenge) pNetCommand;
        String response = createResponse(passwordChallengeCommand.getChallenge()); 
        sendJoin(response);
        break;
      default:
        super.handleNetCommand(pNetCommand);
        break;
    }
  }
  
  private boolean checkVersion(String pServerVersion, String pClientVersion) {
    
    if (checkVersionConflict(pClientVersion, FantasyFootballClient.CLIENT_VERSION)) {
      String[] messages = new String[3];
      messages[0] = "Server expects client version " + pClientVersion + " or newer.";
      messages[1] = "Client version is " + FantasyFootballClient.CLIENT_VERSION + ".";
      messages[2] = "Please update your client!";
      fWaitingDialog = new DialogInformation(getClient(), "Client Version Conflict", messages, DialogInformation.CANCEL_DIALOG, false);
      fWaitingDialog.showDialog(this);
      return false;
    }
    
    if (checkVersionConflict(FantasyFootballClient.SERVER_VERSION, pServerVersion)) {
      String[] messages = new String[3];
      messages[0] = "Client expects server version " + FantasyFootballClient.SERVER_VERSION + " or newer.";
      messages[1] = "Server version is " + pServerVersion + ".";
      messages[2] = "Please wait for a server update!";
      fWaitingDialog = new DialogInformation(getClient(), "Server Version Conflict", messages, DialogInformation.CANCEL_DIALOG, false);
      fWaitingDialog.showDialog(this);
      return false;
    }
    
    return true;
    
  }
  
  private boolean checkVersionConflict(String pVersionExpected, String pVersionIs) {
    
    int majorVersionExpected = 0;
    int minorVersionExpected = 0;
    int releaseExpected = 0;
    Matcher versionExpectedMatcher = _PATTERN_VERSION.matcher(pVersionExpected);
    if (versionExpectedMatcher.matches()) {
      majorVersionExpected = Integer.parseInt(versionExpectedMatcher.group(1));
      minorVersionExpected = Integer.parseInt(versionExpectedMatcher.group(2));
      releaseExpected = Integer.parseInt(versionExpectedMatcher.group(3));
    }
    
    int majorVersionIs = 0;
    int minorVersionIs = 0;
    int releaseIs = 0;
    Matcher versionIsMatcher = _PATTERN_VERSION.matcher(pVersionIs);
    if (versionIsMatcher.matches()) {
      majorVersionIs = Integer.parseInt(versionIsMatcher.group(1));
      minorVersionIs = Integer.parseInt(versionIsMatcher.group(2));
      releaseIs = Integer.parseInt(versionIsMatcher.group(3));
    }
    
    return ((majorVersionIs < majorVersionExpected) || (minorVersionIs < minorVersionExpected) || (releaseIs < releaseExpected));

  }
  
  private String createResponse(String pChallenge) {
    String response;
    try {
      response = PasswordChallenge.createResponse(pChallenge, fEncodedPassword);
    } catch (IOException ioe) {
      response = null;
    } catch (NoSuchAlgorithmException nsa) {
      response = null;
    }
    return response;
  }
  
  private void sendChallenge() {
  	String authentication = getClient().getParameters().getAuthentication();
  	if (StringTool.isProvided(authentication)) {
  		sendJoin(authentication);
  	} else {
  		getClient().getCommunication().sendPasswordChallenge();
  	}
  }
  
  private void sendJoin(String pResponse) {
    if (fListGames) {
      getClient().getCommunication().sendJoin(
        getClient().getParameters().getCoach(),
        pResponse,
        0,
        null,
        null,
        null
      );

    } else {
      getClient().getCommunication().sendJoin(
        getClient().getParameters().getCoach(),
        pResponse,
        (fGameId > 0L) ? fGameId : getClient().getParameters().getGameId(),
        fGameName,
        fTeamHomeId,
        fTeamHomeName
      );
    }
  }
  
  private void showLoginDialog() {
  	boolean hasGameId = (getClient().getParameters().getGameId() > 0);
  	if (StringTool.isProvided(getClient().getParameters().getAuthentication())) {
  		fPasswordLength = -1;
  	}
  	DialogLogin loginDialog = new DialogLogin(getClient(), fEncodedPassword, fPasswordLength, fTeamHomeName, fTeamAwayName, !hasGameId);
  	if (hasGameId && (fPasswordLength < 0)) {
  		dialogClosed(loginDialog);  // close dialog right away if no game name or password is necessary
  	} else if (fLastServerError == ServerStatus.ERROR_GAME_IN_USE) {
      loginDialog.showDialogWithError(this, DialogLogin.FIELD_GAME);
    } else if (fLastServerError == ServerStatus.ERROR_UNKNOWN_COACH) {
      loginDialog.showDialogWithError(this, DialogLogin.FIELD_COACH);
    } else if (fLastServerError == ServerStatus.ERROR_WRONG_PASSWORD) {
      loginDialog.setEncodedPassword(null, fPasswordLength);
      loginDialog.showDialogWithError(this, DialogLogin.FIELD_PASSWORD);
    } else {
      loginDialog.showDialog(this);
    }
  }
  
}
