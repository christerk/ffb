package com.fumbbl.ffb.marking;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AutoMarkingConfig implements IJsonSerializable {

	private Set<AutoMarkingRecord> markings = new HashSet<>();

	public Set<AutoMarkingRecord> getMarkings() {
		return markings;
	}

	public void setMarkings(Set<AutoMarkingRecord> markings) {
		this.markings = markings;
	}

	public static Set<AutoMarkingRecord> defaults(SkillFactory skillFactory) {

		Set<AutoMarkingRecord> defaults = new HashSet<>();
		AutoMarkingRecord.Builder builder = new AutoMarkingRecord.Builder(skillFactory);

		defaults.add(builder.withSkill("Block").withMarking("B").withGainedOnly(true).build());
		defaults.add(builder.withSkill("Tackle").withMarking("T").withGainedOnly(true).build());
		defaults.add(builder.withSkill("Dodge").withMarking("D").withGainedOnly(true).build());
		defaults.add(builder.withSkill("Mighty Blow").withMarking("M").withGainedOnly(true).build());
		defaults.add(builder.withSkill("Sneaky Git").withMarking("Sg").withGainedOnly(true).build());

		return defaults;
	}

	@Override
	public AutoMarkingConfig initFrom(IFactorySource source, JsonValue jsonValue) {
		markings = IJsonOption.AUTO_MARKING_RECORDS.getFrom(source, UtilJson.toJsonObject(jsonValue)).values().stream()
			.map(value -> new AutoMarkingRecord().initFrom(source, value)).collect(Collectors.toSet());
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		JsonArray jsonArray = new JsonArray();
		markings.stream().map(AutoMarkingRecord::toJsonValue).forEach(jsonArray::add);
		IJsonOption.AUTO_MARKING_RECORDS.addTo(jsonObject, jsonArray);
		return jsonObject;
	}
}
