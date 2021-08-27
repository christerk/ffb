package com.fumbbl.ffb.client.ui;

import com.fumbbl.ffb.util.StringTool;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kalimar
 */
public class ResourceSlot {

	private final Rectangle fLocation;
	private int fValue;
	private boolean fEnabled;
	private String fIconProperty, singular, plural;
	private final List<String> details = new ArrayList<>();

	public ResourceSlot(Rectangle pLocation) {
		fLocation = pLocation;
		fEnabled = true;
	}


	public Rectangle getLocation() {
		return fLocation;
	}

	public int getValue() {
		return fValue;
	}

	public void setValue(int pValue) {
		fValue = pValue;
	}

	public void setIconProperty(String pIconProperty) {
		fIconProperty = pIconProperty;
	}

	public String getIconProperty() {
		return fIconProperty;
	}

	public String getToolTip() {
		if (StringTool.isProvided(singular) && StringTool.isProvided(plural)) {
			StringBuilder toolTip = new StringBuilder();
			if (getValue() > 0) {
				toolTip.append(getValue()).append(" ");
			} else {
				toolTip.append("No ");
			}
			toolTip.append((getValue() == 1) ? singular : plural);
			if (!details.isEmpty()) {
				toolTip.insert(0, "<html>");
				details.forEach(detail -> toolTip.append("<br/> - ").append(detail));
				toolTip.append("</html>");
			}
			return toolTip.toString();
		} else {
			return null;
		}
	}

	public void setEnabled(boolean pEnabled) {
		fEnabled = pEnabled;
	}

	public boolean isEnabled() {
		return fEnabled;
	}

	public void setSingular(String singular) {
		this.singular = singular;
	}

	public void setPlural(String plural) {
		this.plural = plural;
	}

	public List<String> getDetails() {
		return details;
	}

	public void addDetail(String detail) {
		details.add(detail);
	}

	public void clearDetails() {
		details.clear();
	}
}
