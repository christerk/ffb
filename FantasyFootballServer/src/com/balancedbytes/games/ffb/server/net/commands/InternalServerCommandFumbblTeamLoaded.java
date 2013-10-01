package com.balancedbytes.games.ffb.server.net.commands;

import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.admin.IAdminGameIdListener;


/**
 * 
 * @author Kalimar
 */
public class InternalServerCommandFumbblTeamLoaded extends InternalServerCommand {
  
  private String fCoach;
  private boolean fHomeTeam;
  private IAdminGameIdListener fAdminGameIdListener;

  public InternalServerCommandFumbblTeamLoaded(long pGameId, String pCoach, boolean pHomeTeam) {
    super(pGameId);
    fCoach = pCoach;
    fHomeTeam = pHomeTeam;
  }

  public NetCommandId getId() {
    return NetCommandId.INTERNAL_SERVER_FUMBBL_TEAM_LOADED;
  }
  
  public String getCoach() {
    return fCoach;
  }
    
  public boolean isHomeTeam() {
    return fHomeTeam;
  }
  
  public void setAdminGameIdListener(IAdminGameIdListener pAdminGameIdListener) {
	  fAdminGameIdListener = pAdminGameIdListener;
  }
 
  public IAdminGameIdListener getAdminGameIdListener() {
	  return fAdminGameIdListener;
  }
  
}
