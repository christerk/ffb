package com.fumbbl.ffb.server.step.generator;

import java.util.ArrayList;
import java.util.List;

import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.skillbehaviour.StepHook;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;

public class Sequence {
	private final GameState gameState;
	private final List<IStep> sequence;

	public Sequence(GameState gameState) {
		this.sequence = new ArrayList<>();
		this.gameState = gameState;
	}

	public void insertHooks(StepHook.HookPoint hookPoint, StepParameter... params) {
		gameState.getStepFactory().getSteps(hookPoint).forEach(step -> add(step, params));
	}

	public void add(StepId step, StepParameter... params) {
		add(step, null, params);
	}

	public void add(StepId step, String label, StepParameter... params) {
		StepParameterSet parameterSet = null;
		if (params != null) {
			parameterSet = new StepParameterSet();
			for (StepParameter p : params) {
				parameterSet.add(p);
			}
		}

		sequence.add(gameState.getStepFactory().create(step, label, parameterSet));
	}

	public void jump(String targetLabel) {
		add(StepId.GOTO_LABEL, StepParameter.from(StepParameterKey.GOTO_LABEL, targetLabel));
	}

	public List<IStep> getSequence() {
		return sequence;
	}
}
