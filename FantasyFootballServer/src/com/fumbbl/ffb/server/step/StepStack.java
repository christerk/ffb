package com.fumbbl.ffb.server.step;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.server.DebugLog;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.IServerLogLevel;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Kalimar
 */
public class StepStack implements IJsonSerializable {

	private final List<IStep> fStack;

	private final transient GameState fGameState;

	public StepStack(GameState pGameState) {
		fGameState = pGameState;
		fStack = new LinkedList<>();
	}

	public GameState getGameState() {
		return fGameState;
	}

	public void push(IStep pStep) {
		fStack.add(0, pStep);
	}

	public void push(List<IStep> pSteps) {
		if ((pSteps != null) && (pSteps.size() > 0)) {
			for (int i = pSteps.size() - 1; i >= 0; i--) {
				push(pSteps.get(i));
			}
		}
	}

	public IStep pop() {
		if (size() > 0) {
			return fStack.remove(0);
		} else {
			return null;
		}
	}

	public IStep peek() {
		if (size() > 0) {
			return fStack.get(0);
		} else {
			return null;
		}
	}

	public int size() {
		return fStack.size();
	}

	public IStep[] toArray() {
		return fStack.toArray(new IStep[fStack.size()]);
	}

	public void clear() {
		fStack.clear();
	}

	public void publishStepParameter(StepParameter pParameter) {
		for (IStep step : fStack) {
			DebugLog debugLog = getGameState().getServer().getDebugLog();
			if (debugLog.isLogging(IServerLogLevel.TRACE)) {
				StringBuilder trace = new StringBuilder();
				trace.append(step.getId()).append(" receives ").append(pParameter.getKey()).append("=")
					.append(pParameter.getValue());
				debugLog.log(IServerLogLevel.TRACE, getGameState().getGame().getId(), trace.toString());
			}

			step.setParameter(pParameter);

			if (pParameter.isConsumed()) {
				break;
			}
		}
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		JsonArray stepArray = new JsonArray();
		for (IStep step : fStack) {
			stepArray.add(step.toJsonValue());
		}
		IServerJsonOption.STEPS.addTo(jsonObject, stepArray);
		return jsonObject;
	}

	public StepStack initFrom(IFactorySource source, JsonValue pJsonValue) {
		StepFactory stepFactory = getGameState().getStepFactory();
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		JsonArray stepArray = IServerJsonOption.STEPS.getFrom(source, jsonObject);
		fStack.clear();
		if (stepArray != null) {
			for (int i = 0; i < stepArray.size(); i++) {
				IStep step = stepFactory.forJsonValue(source, stepArray.get(i));
				fStack.add(step);
			}
		}
		return this;
	}

}
