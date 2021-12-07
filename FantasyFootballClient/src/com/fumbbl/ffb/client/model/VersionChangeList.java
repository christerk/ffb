package com.fumbbl.ffb.client.model;

import java.util.ArrayList;
import java.util.List;

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

}
