package com.fumbbl.ffb.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VersionChangeList {

	private final List<String> bugfixes = new ArrayList<>();
	private final List<String> features = new ArrayList<>();
	private final List<String> improvements = new ArrayList<>();
	private final String version;

	public VersionChangeList(String version) {
		this.version = version;
	}

	private VersionChangeList add(List<String> list, String entry) {
		list.add(entry);
		return this;
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

	public List<String> getBugfixes() {
		return bugfixes;
	}

	public List<String> getFeatures() {
		return features;
	}

	public List<String> getImprovements() {
		return improvements;
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

	public boolean hasEntries() {
		return hasBugfixes() || hasFeatures() || hasImprovements();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VersionChangeList that = (VersionChangeList) o;
		return bugfixes.equals(that.bugfixes) && features.equals(that.features) && improvements.equals(that.improvements) && version.equals(that.version);
	}

	@Override
	public int hashCode() {
		return Objects.hash(bugfixes, features, improvements, version);
	}
}
