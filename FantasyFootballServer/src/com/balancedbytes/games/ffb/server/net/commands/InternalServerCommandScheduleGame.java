package com.balancedbytes.games.ffb.server.net.commands;

import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.admin.IAdminGameIdListener;


/**
 * 
 * @author Kalimar
 */
public class InternalServerCommandScheduleGame extends InternalServerCommand {
  
	private String fTeamHomeId;
	private String fTeamAwayId;
	private IAdminGameIdListener fAdminGameIdListener;

  public InternalServerCommandScheduleGame(String pTeamHomeId, String pTeamAwayId) {
  	super(0);
    fTeamHomeId = pTeamHomeId;
    fTeamAwayId = pTeamAwayId;
  }

  public NetCommandId getId() {
    return NetCommandId.INTERNAL_SERVER_SCHEDULE_GAME;
  }
  
  public String getTeamHomeId() {
	  return fTeamHomeId;
  }
  
  public String getTeamAwayId() {
	  return fTeamAwayId;
  }
  
  public void setAdminGameIdListener(IAdminGameIdListener pAdminGameIdListener) {
	  fAdminGameIdListener = pAdminGameIdListener;
  }
  
  public IAdminGameIdListener getAdminGameIdListener() {
	  return fAdminGameIdListener;
  }

}
