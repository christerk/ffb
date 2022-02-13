package com.fumbbl.ffb.client.model;

import com.fumbbl.ffb.util.StringTool;

import java.util.ArrayList;
import java.util.List;

public class VersionChangeList {

	private final List<String> bugfixes = new ArrayList<>();
	private final List<String> features = new ArrayList<>();
	private final List<String> improvements = new ArrayList<>();
	private final List<String> behaviorChanges = new ArrayList<>();
	private String description;

	private final String version;

	public VersionChangeList(String version) {
		this.version = version;
	}

	private VersionChangeList add(List<String> list, String entry) {
		list.add(entry);
		return this;
	}

	public String getDescription() {
		return description;
	}

	public VersionChangeList addBugfix(String bugfix) {
		return add(bugfixes, bugfix);
	}

	public VersionChangeList addImprovement(String improvement) {
		return add(improvements, improvement);
	}

	public VersionChangeList addFeature(String feature) {
		return add(features, feature);
	}

	public VersionChangeList addBehaviorChange(String behaviorChange) {
		return add(behaviorChanges, behaviorChange);
	}

	public List<String> getBugfixes() {
		return bugfixes;
	}

	public List<String> getFeatures() {
		return features;
	}

	public List<String> getImprovements() {
		return improvements;
	}

	public List<String> getBehaviorChanges() {
		return behaviorChanges;
	}

	public VersionChangeList setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getVersion() {
		return version;
	}

	public boolean hasBugfixes() {
		return !bugfixes.isEmpty();
	}

	public boolean hasImprovements() {
		return !improvements.isEmpty();
	}

	public boolean hasFeatures() {
		return !features.isEmpty();
	}

	public boolean hasBehaviorChanges() {
		return !behaviorChanges.isEmpty();
	}

	public boolean hasDescription() {
		return StringTool.isProvided(description);
	}

	public boolean hasEntries() {
		return hasBugfixes() || hasFeatures() || hasImprovements() || hasBehaviorChanges() || hasDescription();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		VersionChangeList that = (VersionChangeList) o;

		if (!bugfixes.equals(that.bugfixes)) return false;
		if (!features.equals(that.features)) return false;
		if (!improvements.equals(that.improvements)) return false;
		if (!behaviorChanges.equals(that.behaviorChanges)) return false;
		if (description != null ? !description.equals(that.description) : that.description != null) return false;
		return version != null ? version.equals(that.version) : that.version == null;
	}

	@Override
	public int hashCode() {
		int result = bugfixes.hashCode();
		result = 31 * result + features.hashCode();
		result = 31 * result + improvements.hashCode();
		result = 31 * result + behaviorChanges.hashCode();
		result = 31 * result + (description != null ? description.hashCode() : 0);
		result = 31 * result + (version != null ? version.hashCode() : 0);
		return result;
	}
}
