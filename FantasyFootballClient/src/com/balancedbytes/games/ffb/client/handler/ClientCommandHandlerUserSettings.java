package com.balancedbytes.games.ffb.client.handler;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ServerCommandUserSettings;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandHandlerUserSettings extends ClientCommandHandler {

  protected ClientCommandHandlerUserSettings(FantasyFootballClient pClient) {
    super(pClient);
  }

  public NetCommandId getId() {
    return NetCommandId.SERVER_USER_SETTINGS;
  }

  public boolean handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {
    
    if (pMode == ClientCommandHandlerMode.QUEUING) {
      return true;
    }

    ServerCommandUserSettings userSettingsCommand = (ServerCommandUserSettings) pNetCommand;
    
    String[] settingNames = userSettingsCommand.getSettingNames();
    for (String settingName : settingNames) {
      getClient().setProperty(settingName, userSettingsCommand.getSettingValue(settingName));
    }
    
    if (pMode == ClientCommandHandlerMode.PLAYING) {
      refreshGameMenuBar();
    }
    
    return true;
    
  }

}
