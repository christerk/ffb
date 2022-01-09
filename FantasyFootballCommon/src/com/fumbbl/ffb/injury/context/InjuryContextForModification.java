package com.fumbbl.ffb.injury.context;

import com.eclipsesource.json.JsonObject;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.model.skill.Skill;

public class InjuryContextForModification extends InjuryContext {

	private Skill skillForAlternateContext;
	private SkillUse skillUse;

	public SkillUse getSkillUse() {
		return skillUse;
	}

	public void setSkillUse(SkillUse skillUse) {
		this.skillUse = skillUse;
	}

	public Skill getSkillForAlternateContext() {
		return skillForAlternateContext;
	}

	public void setSkillForAlternateContext(Skill skillForAlternateContext) {
		this.skillForAlternateContext = skillForAlternateContext;
	}

	@Override
	public InjuryContextForModification getAlternateInjuryContext() {
		return null;
	}

	@Override
	public void setAlternateInjuryContext(InjuryContextForModification alternateInjuryContext) {
		super.setAlternateInjuryContext(null); // force this class to never have an alternate context
	}

	@Override
	public void toJsonValue(JsonObject jsonObject) {
		super.toJsonValue(jsonObject);
		IJsonOption.SKILL.addTo(jsonObject, skillForAlternateContext);
		IJsonOption.SKILL_USE.addTo(jsonObject, skillUse);
	}

	@Override
	public void initFrom(IFactorySource source, JsonObject jsonObject) {
		super.initFrom(source, jsonObject);
		skillForAlternateContext = (Skill) IJsonOption.SKILL.getFrom(source, jsonObject);
		skillUse = (SkillUse) IJsonOption.SKILL_USE.getFrom(source, jsonObject);

	}
}
