package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.bytearray.IByteArrayReadable;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public interface IStep extends IByteArrayReadable, IJsonSerializable {
	
	public StepId getId();

	public void setLabel(String pLabel);

	public String getLabel();
	
	public void start();
	
	public void init(StepParameterSet pParameterSet);
	
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand);
	
	public GameState getGameState();
	
	public StepResult getResult();
	
	public boolean setParameter(StepParameter pParameter);
	
  public void publishParameter(StepParameter pParameter);

  public void publishParameters(StepParameterSet pParameterSet);
	
	// overrides IJsonSerializable
  public IStep initFrom(JsonValue pJsonValue);

  // overrides IJsonSerializable
  public JsonObject toJsonValue();

}
