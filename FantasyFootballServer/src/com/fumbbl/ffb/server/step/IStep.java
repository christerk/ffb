package com.fumbbl.ffb.server.step;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;

/**
 * 
 * @author Kalimar
 */
public interface IStep extends IJsonSerializable, INamedObject {

	StepId getId();

	void setLabel(String pLabel);

	String getLabel();

	void start();

	void repeat();

	void init(StepParameterSet pParameterSet);

	StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand);

	GameState getGameState();

	StepResult getResult();

	boolean setParameter(StepParameter pParameter);

	void publishParameter(StepParameter pParameter);

	void publishParameters(StepParameterSet pParameterSet);

	// overrides IJsonSerializable
	IStep initFrom(IFactorySource source, JsonValue jsonValue);

	// overrides IJsonSerializable
	JsonObject toJsonValue();
}
