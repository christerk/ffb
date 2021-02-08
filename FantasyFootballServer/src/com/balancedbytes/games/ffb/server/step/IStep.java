package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public interface IStep extends IJsonSerializable, INamedObject {

	public StepId getId();

	public void setLabel(String pLabel);

	public String getLabel();

	public void start();

	public void repeat();

	public void init(StepParameterSet pParameterSet);

	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand);

	public GameState getGameState();

	public StepResult getResult();

	public boolean setParameter(StepParameter pParameter);

	public void publishParameter(StepParameter pParameter);

	public void publishParameters(StepParameterSet pParameterSet);

	// overrides IJsonSerializable
	public IStep initFrom(IFactorySource game, JsonValue pJsonValue);

	// overrides IJsonSerializable
	public JsonObject toJsonValue();
}
