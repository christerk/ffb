package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ClientCommandIllegalProcedure extends ClientCommand {
  
  public ClientCommandIllegalProcedure() {
    super();
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_ILLEGAL_PROCEDURE;
  }
  
  // JSON serialization
  
  public ClientCommandIllegalProcedure initFrom(JsonValue jsonValue) {
    super.initFrom(jsonValue);
    return this;
  }
      
}
