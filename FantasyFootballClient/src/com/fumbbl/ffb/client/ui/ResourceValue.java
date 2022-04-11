package com.fumbbl.ffb.client.ui;

public class ResourceValue {
	private final int value;
	private final String singular;
	private final String plural;

	public ResourceValue(int value, String singular, String plural) {
		this.value = value;
		this.singular = singular;
		this.plural = plural;
	}

	public int getValue() {
		return value;
	}

	public String getSingular() {
		return singular;
	}

	public String getPlural() {
		return plural;
	}

	public String toolTip() {
		StringBuilder toolTip = new StringBuilder();
		if (getValue() > 0) {
			toolTip.append(getValue());
		} else {
			toolTip.append("No");
		}
		toolTip.append(" ").append((getValue() == 1) ? singular : plural);

		return toolTip.toString();
	}
}
