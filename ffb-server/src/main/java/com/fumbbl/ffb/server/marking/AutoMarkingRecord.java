package com.fumbbl.ffb.server.marking;

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
import com.fumbbl.ffb.util.ArrayTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoMarkingRecord implements IJsonSerializable {
	private List<Skill> skills = new ArrayList<>();
	private final List<InjuryAttribute> injuries = new ArrayList<>();

	private boolean gainedOnly, applyRepeatedly;

	private ApplyTo applyTo = ApplyTo.BOTH;
	private String marking = "";


	public List<Skill> getSkills() {
		return skills;
	}

	public List<InjuryAttribute> getInjuries() {
		return injuries;
	}

	public boolean isGainedOnly() {
		return gainedOnly;
	}

	public ApplyTo getApplyTo() {
		return applyTo;
	}

	public boolean isSubSetOf(AutoMarkingRecord other) {
		//noinspection SlowListContainsAll
		return other != null && other.getSkills().containsAll(skills) && other.injuries.containsAll(injuries);
	}

	public boolean isApplyRepeatedly() {
		return applyRepeatedly;
	}

	public String getMarking() {
		return marking;
	}

	public void setMarking(String marking) {
		this.marking = marking;
	}

	public boolean isInjuryOnly() {
		return skills.isEmpty();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AutoMarkingRecord that = (AutoMarkingRecord) o;

		if (gainedOnly != that.gainedOnly) return false;
		if (applyRepeatedly != that.applyRepeatedly) return false;
		if (!skills.equals(that.skills)) return false;
		if (!injuries.equals(that.injuries)) return false;
		if (applyTo != that.applyTo) return false;
		return marking.equals(that.marking);
	}

	@Override
	public int hashCode() {
		int result = skills.hashCode();
		result = 31 * result + injuries.hashCode();
		result = 31 * result + (gainedOnly ? 1 : 0);
		result = 31 * result + (applyRepeatedly ? 1 : 0);
		result = 31 * result + applyTo.hashCode();
		result = 31 * result + marking.hashCode();
		return result;
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
		marking = IJsonOption.MARKING.getFrom(source, jsonObject);
		applyRepeatedly = IJsonOption.APPLY_REPEATEDLY.getFrom(source, jsonObject);
		String[] injuryAttributes = IJsonOption.INJURY_ATTRIBUTES.getFrom(source, jsonObject);
		if (ArrayTool.isProvided(injuryAttributes)) {
			Arrays.stream(injuryAttributes).map(InjuryAttribute::forName).forEach(injuries::add);
		}

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
		IJsonOption.MARKING.addTo(jsonObject, marking);
		IJsonOption.APPLY_REPEATEDLY.addTo(jsonObject, applyRepeatedly);

		return jsonObject;
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
