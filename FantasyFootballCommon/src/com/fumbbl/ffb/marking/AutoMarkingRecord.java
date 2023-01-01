package com.fumbbl.ffb.marking;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.InjuryAttribute;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AutoMarkingRecord implements IJsonSerializable {
	private List<Skill> skills = new ArrayList<>();
	private List<InjuryAttribute> injuries = new ArrayList<>();

	private boolean gainedOnly, applyRepeatedly;

	private ApplyTo applyTo = ApplyTo.BOTH;
	private String marking = "";


	public List<Skill> getSkills() {
		return skills;
	}

	public void setSkills(List<Skill> skills) {
		this.skills = skills;
	}

	public List<InjuryAttribute> getInjuries() {
		return injuries;
	}

	public void setInjuries(List<InjuryAttribute> injuries) {
		this.injuries = injuries;
	}

	public boolean isGainedOnly() {
		return gainedOnly;
	}

	public void setGainedOnly(boolean gainedOnly) {
		this.gainedOnly = gainedOnly;
	}

	public ApplyTo getApplyTo() {
		return applyTo;
	}

	public void setApplyTo(ApplyTo applyTo) {
		this.applyTo = applyTo;
	}

	public boolean isSubSetOf(AutoMarkingRecord other) {
		//noinspection SlowListContainsAll
		return other != null && other.getSkills().containsAll(skills) && other.injuries.containsAll(injuries);
	}

	public boolean isApplyRepeatedly() {
		return applyRepeatedly;
	}

	public void setApplyRepeatedly(boolean applyRepeatedly) {
		this.applyRepeatedly = applyRepeatedly;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AutoMarkingRecord that = (AutoMarkingRecord) o;
		return gainedOnly == that.gainedOnly && applyRepeatedly == that.applyRepeatedly && skills.equals(that.skills) && injuries.equals(that.injuries) && applyTo == that.applyTo && marking.equals(that.marking);
	}

	@Override
	public int hashCode() {
		return Objects.hash(skills, injuries, gainedOnly, applyRepeatedly, applyTo, marking);
	}

	@Override
	public AutoMarkingRecord initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		SkillFactory skillFactory = source.getFactory(FactoryType.Factory.SKILL);

		skills = new ArrayList<>();
		JsonArray skillArray = IJsonOption.SKILL_ARRAY.getFrom(source, jsonObject);
		for (int i = 0; i < skillArray.size(); i++) {
			skills.add((Skill) UtilJson.toEnumWithName(skillFactory, skillArray.get(i)));
		}

		if (IJsonOption.APPLY_TO.isDefinedIn(jsonObject)) {
			applyTo = ApplyTo.valueOf(IJsonOption.APPLY_TO.getFrom(source, jsonObject));
		}

		gainedOnly = IJsonOption.GAINED_ONLY.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		JsonArray skillArray = new JsonArray();
		for (Skill skill : skills) {
			skillArray.add(UtilJson.toJsonValue(skill));
		}
		IJsonOption.SKILL_ARRAY.addTo(jsonObject, skillArray);

		String[] injuriesArray = injuries.stream().map(InjuryAttribute::getName).toArray(String[]::new);
		IJsonOption.INJURY_ATTRIBUTES.addTo(jsonObject, injuriesArray);

		if (applyTo != null) {
			IJsonOption.APPLY_TO.addTo(jsonObject, applyTo.name());
		}

		IJsonOption.GAINED_ONLY.addTo(jsonObject, gainedOnly);

		return jsonObject;
	}

	public String getMarking() {
		return marking;
	}

	public void setMarking(String marking) {
		this.marking = marking;
	}

	public static class Builder {
		private final SkillFactory skillFactory;
		private AutoMarkingRecord record;

		public Builder(SkillFactory skillFactory) {
			this.skillFactory = skillFactory;
			this.record = new AutoMarkingRecord();
		}

		public Builder withSkill(String name) {
			record.skills.add(skillFactory.forName(name));
			return this;
		}

		public Builder withInjury(InjuryAttribute injuryAttribute) {
			record.injuries.add(injuryAttribute);
			return this;
		}

		public Builder withGainedOnly(boolean gainedOnly) {
			record.gainedOnly = gainedOnly;
			return this;
		}

		public Builder withApplyTo(ApplyTo applyTo) {
			record.applyTo = applyTo;
			return this;
		}

		public Builder withMarking(String marking) {
			record.marking = marking;
			return this;
		}

		public Builder withApplyRepeatedly(boolean applyRepeatedly) {
			record.applyRepeatedly = applyRepeatedly;
			return this;
		}

		public AutoMarkingRecord build() {
			AutoMarkingRecord oldRecord = record;
			record = new AutoMarkingRecord();
			return oldRecord;
		}
	}
}
