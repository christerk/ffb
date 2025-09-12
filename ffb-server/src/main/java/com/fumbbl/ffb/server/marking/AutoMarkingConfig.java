package com.fumbbl.ffb.server.marking;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AutoMarkingConfig implements IJsonSerializable {

	private String separator = "";
	private List<AutoMarkingRecord> markings = new ArrayList<>();

	public List<AutoMarkingRecord> getMarkings() {
		return markings;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public static List<AutoMarkingRecord> defaults(SkillFactory skillFactory) {

		List<AutoMarkingRecord> defaults = new ArrayList<>();
		AutoMarkingRecord.Builder builder = new AutoMarkingRecord.Builder(skillFactory);

		defaults.add(builder.withSkill("Block").withMarking("B").withGainedOnly(true).build());
		defaults.add(builder.withSkill("Tackle").withMarking("T").withGainedOnly(true).build());
		defaults.add(builder.withSkill("Dodge").withMarking("D").withGainedOnly(true).build());
		defaults.add(builder.withSkill("Mighty Blow").withMarking("M").withGainedOnly(true).build());
		defaults.add(builder.withSkill("Sneaky Git").withMarking("Sg").withGainedOnly(true).build());
		defaults.add(builder.withSkill("Claws").withMarking("C").withGainedOnly(true).build());
		defaults.add(builder.withSkill("Diving Tackle").withMarking("Dt").withGainedOnly(true).build());
		defaults.add(builder.withSkill("Dirty Player").withMarking("Dp").withGainedOnly(true).build());
		defaults.add(builder.withSkill("Side Step").withMarking("S").withGainedOnly(true).build());
		defaults.add(builder.withSkill("Guard").withMarking("G").withGainedOnly(true).build());
		defaults.add(builder.withSkill("Wrestle").withMarking("W").withGainedOnly(true).build());

		return defaults;
	}

	@Override
	public AutoMarkingConfig initFrom(IFactorySource source, JsonValue jsonValue) {
		try {
			JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
			markings = IJsonOption.AUTO_MARKING_RECORDS.getFrom(source, jsonObject).values().stream()
				.map(value -> new AutoMarkingRecord().initFrom(source, value)).collect(Collectors.toList());
			separator = IJsonOption.SEPARATOR.getFrom(source, jsonObject);
		} catch (Exception e) {
			source.logError(0, "Could not init auto marking config: " + e.getMessage());
		}
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		JsonArray jsonArray = new JsonArray();
		markings.stream().map(AutoMarkingRecord::toJsonValue).forEach(jsonArray::add);
		IJsonOption.AUTO_MARKING_RECORDS.addTo(jsonObject, jsonArray);
		IJsonOption.SEPARATOR.addTo(jsonObject, separator);
		return jsonObject;
	}
}
