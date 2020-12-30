package com.balancedbytes.games.ffb.server.model;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.model.ISkillBehaviour;
import com.balancedbytes.games.ffb.model.PlayerModifier;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.server.step.IStep;

public abstract class SkillBehaviour<T extends Skill> implements ISkillBehaviour<T> {

	public T skill;

	public String getKey() {
		return skillClass.getSimpleName();
	}
	
	@SuppressWarnings("unchecked")
	public final Class<T> skillClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
			.getActualTypeArguments()[0];

	private List<PlayerModifier> playerModifiers;
	private List<StepModifier<? extends IStep, ?>> stepModifiers;

	public SkillBehaviour() {
		playerModifiers = new ArrayList<>();
		stepModifiers = new ArrayList<>();
		
		RulesCollection skillRules = skillClass.getAnnotation(RulesCollection.class);
		RulesCollection behaviourRules = getClass().getAnnotation(RulesCollection.class);
		
		if (!skillRules.value().matches(behaviourRules.value())) {
			throw new FantasyFootballException("Skill behaviour rule does not match skill rule");
		}
	}

	public void setSkill(T skill) {
		this.skill = skill;
		this.skill.setBehaviour(this);
	}

	protected void registerModifier(StepModifier<?, ?> stepModifier) {
		stepModifiers.add(stepModifier);
	}

	protected void registerModifier(PlayerModifier playerModifier) {
		playerModifiers.add(playerModifier);
	}

	public List<StepModifier<? extends IStep, ?>> getStepModifiers() {
		return stepModifiers;
	}
}
