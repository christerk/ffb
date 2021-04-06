package com.balancedbytes.games.ffb.server.model;

import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;

import java.lang.reflect.ParameterizedType;
import java.util.Comparator;

public abstract class StepModifier<T extends IStep, V> {
	private final Class<?> modifierType;
	private int priority = 0;

	protected StepModifier() {
		this(0);
	}

	protected StepModifier(int priority) {
		modifierType = (Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		this.priority = priority;
	}

	protected void setPriority(int priority) {
		this.priority = priority;
	}

	protected int getPriority() {
		return priority;
	}

	public boolean appliesTo(IStep step) {
		return step.getClass().equals(modifierType);
	}

	public Class<?> getConcreteClass() {
		return modifierType;
	}

	@SuppressWarnings("unchecked")
	public StepCommandStatus handleCommand(IStep step, Object state, ClientCommandUseSkill useSkillCommand) {
		return handleCommandHook((T) step, (V) state, useSkillCommand);
	}

	@SuppressWarnings("unchecked")
	public boolean handleExecuteStep(IStep step, Object state) {
		return handleExecuteStepHook((T) step, (V) state);
	}

	abstract public StepCommandStatus handleCommandHook(T step, V state, ClientCommandUseSkill useSkillCommand);

	abstract public boolean handleExecuteStepHook(T step, V state);

	public static final Comparator<StepModifier<? extends IStep, ?>> Comparator = java.util.Comparator.comparingInt(StepModifier::getPriority);
}
