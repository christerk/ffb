package com.balancedbytes.games.ffb.server.step.game.start;

import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.report.ReportWeather;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in start game sequence to roll weather.
 * 
 * @author Kalimar
 */
public final class StepWeather extends AbstractStep {
	
	public StepWeather(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.WEATHER;
	}
		
	@Override
	public void start() {
		super.start();
		executeStep();
	}
	
	@Override
	public StepCommandStatus handleNetCommand(NetCommand pNetCommand) {
		StepCommandStatus commandStatus = super.handleNetCommand(pNetCommand);
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

  private void executeStep() {
    getResult().addReport(rollWeather());
    getResult().setNextAction(StepAction.NEXT_STEP);
  }
  
  private ReportWeather rollWeather() {
    int[] roll = getGameState().getDiceRoller().rollWeather();
    Weather weather = DiceInterpreter.getInstance().interpretRollWeather(roll);
    getGameState().getGame().getFieldModel().setWeather(weather);
    return new ReportWeather(weather, roll);
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    return super.toJsonValue();
  }
  
  @Override
  public StepWeather initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    return this;
  }
  
}
