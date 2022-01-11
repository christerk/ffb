package com.fumbbl.ffb.injury.context;

import com.eclipsesource.json.JsonObject;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportList;

import java.util.Arrays;
import java.util.List;

public class ModifiedInjuryContext extends InjuryContext {

	private Skill usedSkill;
	private SkillUse skillUse;
	private final ReportList reports = new ReportList();

	public SkillUse getSkillUse() {
		return skillUse;
	}

	public void setSkillUse(SkillUse skillUse) {
		this.skillUse = skillUse;
	}

	public Skill getUsedSkill() {
		return usedSkill;
	}

	public void setUsedSkill(Skill usedSkill) {
		this.usedSkill = usedSkill;
	}

	public void addReport(IReport report) {
		reports.add(report);
	}

	public List<IReport> getReports() {
		return Arrays.asList(reports.getReports());
	}

	@Override
	public ModifiedInjuryContext getModifiedInjuryContext() {
		return null;
	}

	@Override
	public void setModifiedInjuryContext(ModifiedInjuryContext modifiedInjuryContext) {
		super.setModifiedInjuryContext(null); // force this class to never have an alternate context
	}

	@Override
	public void toJsonValue(JsonObject jsonObject) {
		super.toJsonValue(jsonObject);
		IJsonOption.SKILL.addTo(jsonObject, usedSkill);
		IJsonOption.SKILL_USE.addTo(jsonObject, skillUse);
		IJsonOption.REPORT_LIST.addTo(jsonObject, reports.toJsonValue());
	}

	@Override
	public void initFrom(IFactorySource source, JsonObject jsonObject) {
		super.initFrom(source, jsonObject);
		usedSkill = (Skill) IJsonOption.SKILL.getFrom(source, jsonObject);
		skillUse = (SkillUse) IJsonOption.SKILL_USE.getFrom(source, jsonObject);
		JsonObject reportListObject = IJsonOption.REPORT_LIST.getFrom(source, jsonObject);
		if (reportListObject != null) {
			reports.initFrom(source, reportListObject);
		}
	}
}
