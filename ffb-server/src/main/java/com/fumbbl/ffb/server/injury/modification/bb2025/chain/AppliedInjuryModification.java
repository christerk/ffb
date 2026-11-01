package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.report.IReport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppliedInjuryModification {

	private final Skill skill;
	private final String playerId;
	private final InjuryPhase phase;
	private final ModificationEffect effect;
	private final SkillUse skillUse;
	private final List<IReport> reports;

	public AppliedInjuryModification(Skill skill, String playerId, InjuryPhase phase, ModificationEffect effect,
		SkillUse skillUse, List<IReport> reports) {
		this.skill = skill;
		this.playerId = playerId;
		this.phase = phase;
		this.effect = effect;
		this.skillUse = skillUse;
		this.reports = new ArrayList<>(reports);
	}

	public Skill getSkill() {
		return skill;
	}

	public String getPlayerId() {
		return playerId;
	}

	public InjuryPhase getPhase() {
		return phase;
	}

	public ModificationEffect getEffect() {
		return effect;
	}

	public SkillUse getSkillUse() {
		return skillUse;
	}

	public List<IReport> getReports() {
		return Collections.unmodifiableList(reports);
	}
}
