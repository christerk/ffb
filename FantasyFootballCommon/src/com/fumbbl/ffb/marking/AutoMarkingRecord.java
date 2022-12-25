package com.fumbbl.ffb.marking;

import com.fumbbl.ffb.InjuryAttribute;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.Set;

public class AutoMarkingRecord {
	private Set<Skill> skills;
	private Set<InjuryAttribute> injuries;

	private boolean gainedOnly;

	private ApplyTo appliesTo;


	public Set<Skill> getSkills() {
		return skills;
	}

	public void setSkills(Set<Skill> skills) {
		this.skills = skills;
	}

	public Set<InjuryAttribute> getInjuries() {
		return injuries;
	}

	public void setInjuries(Set<InjuryAttribute> injuries) {
		this.injuries = injuries;
	}

	public boolean isGainedOnly() {
		return gainedOnly;
	}

	public void setGainedOnly(boolean gainedOnly) {
		this.gainedOnly = gainedOnly;
	}

	public ApplyTo getAppliesTo() {
		return appliesTo;
	}

	public void setAppliesTo(ApplyTo appliesTo) {
		this.appliesTo = appliesTo;
	}
}
