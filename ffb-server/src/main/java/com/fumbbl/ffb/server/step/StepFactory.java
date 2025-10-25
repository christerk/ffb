package com.fumbbl.ffb.server.step;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.skillbehaviour.StepHook;
import com.fumbbl.ffb.server.skillbehaviour.StepHook.HookPoint;
import com.fumbbl.ffb.util.Scanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generator class for steps.
 *
 * @author Kalimar
 */
public class StepFactory {

	private final GameState fGameState;
	private final Map<StepId, Constructor<? extends IStep>> stepRegistry = new HashMap<>();
	private final Map<HookPoint, List<StepId>> hooks = new HashMap<>();

	public StepFactory(GameState pGameState) {
		fGameState = pGameState;

		for (HookPoint p : HookPoint.values()) {
			hooks.put(p, new ArrayList<>());
		}
		initialize();
	}

	public IStep forStepId(StepId pStepId) {
		return create(pStepId, null, null);
	}

	public IStep create(StepId pStepId, String pLabel, StepParameterSet pParameterSet) {

		IStep step = null;

		if (pStepId != null) {

			if (stepRegistry.containsKey(pStepId)) {
				Constructor<?> ctr = stepRegistry.get(pStepId);
				try {
					step = (IStep) ctr.newInstance(fGameState);
				} catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new StepException("Error constructing Step " + pStepId, e);
				}
			} else {
				throw new StepException("Unhandled StepId " + pStepId);
			}
		}

		if (step != null) {
			if (pLabel != null) {
				step.setLabel(pLabel);
			}
			if (pParameterSet != null) {
				step.init(pParameterSet);
			}
		}

		return step;

	}

	// JSON serialization

	public IStep forJsonValue(IFactorySource source, JsonValue pJsonValue) {
		if ((pJsonValue == null) || pJsonValue.isNull()) {
			return null;
		}
		IStep step = null;
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		StepId stepId = (StepId) IServerJsonOption.STEP_ID.getFrom(source, jsonObject);
		if (stepId != null) {
			step = forStepId(stepId);
			if (step != null) {
				step.initFrom(source, pJsonValue);
			}
		}
		return step;
	}

	private void initialize() {

		new Scanner<>(IStep.class).getInstancesImplementing(fGameState.getGame().getOptions(), (cls) -> {
				Constructor<? extends IStep> constructor;
				try {
					constructor = cls.getConstructor(GameState.class);
					return constructor.newInstance(fGameState);
				} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
					throw new StepException("Error constructing Step for class " + cls.getCanonicalName(), e);
				}
			})
			.forEach(step -> {

				try {
					stepRegistry.put(step.getId(), step.getClass().getConstructor(GameState.class));

					StepHook hook = step.getClass().getAnnotation(StepHook.class);
					if (hook != null) {
						HookPoint hookPoint = hook.value();
						hooks.get(hookPoint).add(step.getId());
					}

				} catch (NoSuchMethodException e) {
					throw new StepException("Error constructing Step for class " + step.getClass().getCanonicalName(), e);
				}
			});

	}

	public List<StepId> getSteps(HookPoint hookPoint) {
		return hooks.get(hookPoint);
	}

}
