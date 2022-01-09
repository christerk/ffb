package com.fumbbl.ffb.injury.context;

import com.eclipsesource.json.JsonObject;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.model.skill.Skill;

public class InjuryContextForModification extends InjuryContext {

	private Skill skillForAlternateContext;


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
		if (skillForAlternateContext != null) {
			IJsonOption.SKILL.addTo(jsonObject, skillForAlternateContext);
		}
	}

	@Override
	public void initFrom(IFactorySource source, JsonObject jsonObject) {
		super.initFrom(source, jsonObject);

		if (IJsonOption.SKILL.isDefinedIn(jsonObject)) {
			skillForAlternateContext = (Skill) IJsonOption.SKILL.getFrom(source, jsonObject);
		}
	}
}
