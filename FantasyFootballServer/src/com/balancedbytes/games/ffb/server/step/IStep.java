package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.server.GameState;

/**
 * 
 * @author Kalimar
 */
public interface IStep extends IByteArraySerializable {
	
	public StepId getId();

	public void setLabel(String pLabel);

	public String getLabel();
	
	public void start();
	
	public void init(StepParameterSet pParameterSet);
	
	public StepCommandStatus handleNetCommand(NetCommand pNetCommand);
	
	public GameState getGameState();
	
	public StepResult getResult();
	
	public boolean setParameter(StepParameter pParameter);

}
