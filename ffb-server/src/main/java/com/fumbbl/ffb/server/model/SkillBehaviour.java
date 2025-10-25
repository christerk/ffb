package com.fumbbl.ffb.server.model;

import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.model.ISkillBehaviour;
import com.fumbbl.ffb.model.PlayerModifier;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.injury.modification.InjuryContextModification;
import com.fumbbl.ffb.server.injury.modification.ModificationParams;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SkillBehaviour<T extends Skill> implements ISkillBehaviour<T> {

	public T skill;

	public String getKey() {
		return skillClass.getSimpleName();
	}

	@SuppressWarnings("unchecked")
	public final Class<T> skillClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
		.getActualTypeArguments()[0];

	private final List<PlayerModifier> playerModifiers;
	private final List<StepModifier<? extends IStep, ?>> stepModifiers;
	private final Map<StepId, Class<? extends IStep>> steps;
	private InjuryContextModification<? extends ModificationParams> injuryContextModification;


	public SkillBehaviour() {
		playerModifiers = new ArrayList<>();
		stepModifiers = new ArrayList<>();
		steps = new HashMap<>();

		RulesCollection skillRules = skillClass.getAnnotation(RulesCollection.class);
		RulesCollection behaviourRules = getClass().getAnnotation(RulesCollection.class);
		
		if (!skillRules.value().isOrExtends(behaviourRules.value())) {
			throw new FantasyFootballException("Skill behaviour rule does not match skill rule");
		}
	}

	public void setSkill(T skill) {
		this.skill = skill;
		this.skill.setBehaviour(this);
		if (injuryContextModification != null) {
			injuryContextModification.setSkill(skill);
		}
	}

	protected void registerModifier(StepModifier<?, ?> stepModifier) {
		stepModifiers.add(stepModifier);
	}

	protected void registerModifier(PlayerModifier playerModifier) {
		playerModifiers.add(playerModifier);
	}

	protected void registerModifier(InjuryContextModification<?> injuryContextModification) {
		this.injuryContextModification = injuryContextModification;
	}

	protected void registerStep(StepId stepId, Class<? extends IStep> step) {
		steps.put(stepId, step);
	}

	public List<StepModifier<? extends IStep, ?>> getStepModifiers() {
		return stepModifiers;
	}

	public Map<StepId, Class<? extends IStep>> getSteps() {
		return steps;
	}

	@Override
	public InjuryContextModification<?> getInjuryContextModification() {
		return injuryContextModification;
	}

	@Override
	public List<PlayerModifier> getPlayerModifiers() {
		return playerModifiers;
	}

	@Override
	public boolean hasInjuryModifier(InjuryType injuryType) {
		return injuryContextModification != null && injuryContextModification.isValidType(injuryType);
	}
}
