package com.balancedbytes.games.ffb.server.step.generator;

import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.skillbehaviour.StepHook;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;

import java.util.ArrayList;
import java.util.List;

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
