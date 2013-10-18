package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.server.GameState;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public interface IStep extends IByteArraySerializable, IJsonSerializable {
	
	public StepId getId();

	public void setLabel(String pLabel);

	public String getLabel();
	
	public void start();
	
	public void init(StepParameterSet pParameterSet);
	
	public StepCommandStatus handleNetCommand(NetCommand pNetCommand);
	
	public GameState getGameState();
	
	public StepResult getResult();
	
	public boolean setParameter(StepParameter pParameter);
	
	// overrides IJsonSerializable
  public IStep initFrom(JsonValue pJsonValue);

  // overrides IJsonSerializable
  public JsonObject toJsonValue();

}
