package com.fumbbl.ffb.client.ui;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kalimar
 */
public class ResourceSlot {

	private final Rectangle fLocation;
	private final List<ResourceValue> values = new ArrayList<>();
	private boolean fEnabled;
	private String fIconProperty;
	private final List<String> details = new ArrayList<>();

	public ResourceSlot(Rectangle pLocation) {
		fLocation = pLocation;
		fEnabled = true;
	}


	public Rectangle getLocation() {
		return fLocation;
	}

	public void add(ResourceValue value) {
		values.add(value);
	}

	public List<ResourceValue> getValues() {
		return values;
	}

	public void setIconProperty(String pIconProperty) {
		fIconProperty = pIconProperty;
	}

	public String getIconProperty() {
		return fIconProperty;
	}

	public String getToolTip() {
		StringBuilder toolTip = new StringBuilder();
		toolTip.insert(0, "<html>");
		final boolean[] firstLine = {true};
		values.forEach(value -> {
			if (firstLine[0]) {
				firstLine[0] = false;
			} else {
				toolTip.append("<br/>");
			}
			toolTip.append(value.toolTip());
		});
		if (!details.isEmpty()) {
			toolTip.append("<br/>");
		}
		details.forEach(detail -> toolTip.append("<br/> - ").append(detail));
		toolTip.append("</html>");
		return toolTip.toString();
	}

	public void setEnabled(boolean pEnabled) {
		fEnabled = pEnabled;
	}

	public boolean isEnabled() {
		return fEnabled;
	}

	public List<String> getDetails() {
		return details;
	}

	public void addDetail(String detail) {
		details.add(detail);
	}

	public void clear() {
		details.clear();
		values.clear();
	}
}
