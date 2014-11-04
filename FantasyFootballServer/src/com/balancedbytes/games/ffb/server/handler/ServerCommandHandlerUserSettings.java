package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUserSettings;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.db.DbTransaction;
import com.balancedbytes.games.ffb.server.db.delete.DbUserSettingsDeleteParameter;
import com.balancedbytes.games.ffb.server.db.insert.DbUserSettingsInsertParameterList;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerUserSettings extends ServerCommandHandler {

  protected ServerCommandHandlerUserSettings(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_USER_SETTINGS;
  }

  public void handleCommand(ReceivedCommand pReceivedCommand) {
    
    ClientCommandUserSettings userSettingsCommand = (ClientCommandUserSettings) pReceivedCommand.getCommand();        
    String coach = getServer().getSessionManager().getCoachForSession(pReceivedCommand.getSession());

    if (coach != null) {
    
      DbTransaction dbTransaction = new DbTransaction();

      dbTransaction.add(new DbUserSettingsDeleteParameter(coach));
    
      DbUserSettingsInsertParameterList insertParameters = new DbUserSettingsInsertParameterList();
      String[] settingNames = userSettingsCommand.getSettingNames();
      for (String settingName : settingNames) {
        insertParameters.addParameter(coach, settingName, userSettingsCommand.getSettingValue(settingName));
      }
      dbTransaction.add(insertParameters);
      
      getServer().getDbUpdater().add(dbTransaction);
    
    }
    
  }

}
