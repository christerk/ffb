package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.FantasyFootballConstants;
import com.fumbbl.ffb.GameList;
import com.fumbbl.ffb.GameListEntry;
import com.fumbbl.ffb.TeamListEntry;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.dialog.DialogGameChoice;
import com.fumbbl.ffb.client.dialog.DialogInformation;
import com.fumbbl.ffb.client.dialog.DialogLogin;
import com.fumbbl.ffb.client.dialog.DialogTeamChoice;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.client.dialog.IDialogCloseListener;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.LoginLogicModule;
import com.fumbbl.ffb.dialog.DialogJoinParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.ServerStatus;
import com.fumbbl.ffb.net.commands.ServerCommandGameList;
import com.fumbbl.ffb.net.commands.ServerCommandJoin;
import com.fumbbl.ffb.net.commands.ServerCommandPasswordChallenge;
import com.fumbbl.ffb.net.commands.ServerCommandStatus;
import com.fumbbl.ffb.net.commands.ServerCommandTeamList;
import com.fumbbl.ffb.net.commands.ServerCommandVersion;
import com.fumbbl.ffb.util.StringTool;

import java.util.Collections;
import java.util.Map;

/**
 * @author Kalimar
 */
public class ClientStateLogin extends ClientStateAwt<LoginLogicModule> implements IDialogCloseListener {

	private ServerStatus fLastServerError;
	protected ClientStateLogin(FantasyFootballClientAwt pClient) {
		super(pClient, new LoginLogicModule(pClient));
	}

	public void initUI() {
		super.initUI();
		hideSelectSquare();
		setClickable(false);
		logicModule.init();
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	public void dialogClosed(IDialog pDialog) {
		pDialog.hideDialog();
		switch (pDialog.getId()) {
			case GAME_COACH_PASSWORD:
				DialogLogin loginDialog = (DialogLogin) pDialog;
				LoginLogicModule.LoginData loginData = new LoginLogicModule.LoginData(loginDialog.getGameName(), loginDialog.getEncodedPassword(), loginDialog.getPasswordLength(), loginDialog.isListGames());
				logicModule.sendChallenge(loginData);
				break;
			case INFORMATION:
				DialogInformation informationDialog = (DialogInformation) pDialog;
				if (informationDialog.getOptionType() == DialogInformation.OK_DIALOG) {
					showLoginDialog();
				} else {
					getClient().exitClient();
				}
				break;
			case TEAM_CHOICE:
				DialogTeamChoice teamChoiceDialog = (DialogTeamChoice) pDialog;
				TeamListEntry selectedTeamEntry = teamChoiceDialog.getSelectedTeamEntry();
				if (selectedTeamEntry != null) {
					logicModule.sendChallenge(selectedTeamEntry);
				} else {
					showLoginDialog();
				}
				break;
			case GAME_CHOICE:
				DialogGameChoice gameChoiceDialog = (DialogGameChoice) pDialog;
				GameListEntry selectedGameEntry = gameChoiceDialog.getSelectedGameEntry();
				if (selectedGameEntry != null) {
					logicModule.sendChallenge(selectedGameEntry);
				} else {
					showLoginDialog();
				}
				break;
			default:
				break;
		}
	}

	public void handleCommand(NetCommand pNetCommand) {
		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		DialogInformation fWaitingDialog;
		String[] messages = new String[3];
		switch (pNetCommand.getId()) {
			case SERVER_VERSION:
				ServerCommandVersion versionCommand = (ServerCommandVersion) pNetCommand;
				switch (logicModule.handleVersionCommand(versionCommand)) {
					case SUCCESS: showLoginDialog(); break;
					case SERVER_FAIL:
						messages[0] = "Client expects server version " + FantasyFootballConstants.VERSION + " or newer.";
						messages[1] = "Server version is " + versionCommand.getServerVersion() + ".";
						messages[2] = "Please wait for a server update!";
						fWaitingDialog = new DialogInformation(getClient(), "Server Version Conflict", messages,
							DialogInformation.CANCEL_DIALOG, false);
						fWaitingDialog.showDialog(this);
						break;
					case CLIENT_FAIL:
						messages[0] = "Server expects client version " + versionCommand.getClientVersion() + " or newer.";
						messages[1] = "Client version is " + FantasyFootballConstants.VERSION + ".";
						messages[2] = "Please update your client!";
						fWaitingDialog = new DialogInformation(getClient(), "Client Version Conflict", messages,
							DialogInformation.CANCEL_DIALOG, false);
						fWaitingDialog.showDialog(this);
						break;

				}
				break;
			case SERVER_STATUS:
				ServerCommandStatus errorCommand = (ServerCommandStatus) pNetCommand;
				fLastServerError = errorCommand.getServerStatus();
				DialogInformation informationDialog;
				if (fLastServerError == ServerStatus.FUMBBL_ERROR) {
					informationDialog = new DialogInformation(getClient(), "Fumbbl Error", errorCommand.getMessage(),
						DialogInformation.CANCEL_DIALOG);
				} else {
					informationDialog = new DialogInformation(getClient(), "Server Error", fLastServerError.getMessage(),
						DialogInformation.OK_DIALOG);
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
					DialogInformation noGamesDialog = new DialogInformation(getClient(), "No Open Games",
						"You do not have any open games to join.", DialogInformation.OK_DIALOG);
					noGamesDialog.showDialog(this);
				}
				break;
			case SERVER_JOIN:
				ServerCommandJoin joinCommand = (ServerCommandJoin) pNetCommand;
				if (joinCommand.getPlayerNames().length <= 1) {
					if (logicModule.idAndNameProvided()) {
						getClient().getUserInterface().getStatusReport().reportGameName(logicModule.getGameName());
					}
					game.setDialogParameter(new DialogJoinParameter());
				} else {
					game.setDialogParameter(null);
				}
				userInterface.getDialogManager().updateDialog();
				break;
			case SERVER_PASSWORD_CHALLENGE:
				logicModule.handlePasswordChallenge((ServerCommandPasswordChallenge) pNetCommand);
				break;
			default:
				super.handleCommand(pNetCommand);
				break;
		}
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return Collections.emptyMap();
	}

	private void showLoginDialog() {
		boolean hasGameId = (getClient().getParameters().getGameId() > 0);
		if (StringTool.isProvided(getClient().getParameters().getAuthentication())) {
			logicModule.setPasswordLength(-1);
		}
		DialogLogin loginDialog = new DialogLogin(getClient(), logicModule.getEncodedPassword(), logicModule.getPasswordLength(),
			logicModule.getTeamHomeName(), logicModule.getTeamAwayName(), !hasGameId);
		if (hasGameId && (logicModule.getPasswordLength() < 0)) {
			dialogClosed(loginDialog); // close dialog right away if no game name or password is necessary
		} else if (fLastServerError == ServerStatus.ERROR_GAME_IN_USE) {
			loginDialog.showDialogWithError(this, DialogLogin.FIELD_GAME);
		} else if (fLastServerError == ServerStatus.ERROR_UNKNOWN_COACH) {
			loginDialog.showDialogWithError(this, DialogLogin.FIELD_COACH);
		} else if (fLastServerError == ServerStatus.ERROR_WRONG_PASSWORD) {
			loginDialog.setEncodedPassword(null, logicModule.getPasswordLength());
			loginDialog.showDialogWithError(this, DialogLogin.FIELD_PASSWORD);
		} else {
			loginDialog.showDialog(this);
		}
	}
}
