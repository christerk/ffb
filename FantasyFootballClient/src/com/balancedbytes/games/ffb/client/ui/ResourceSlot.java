package com.balancedbytes.games.ffb.client.ui;

import com.balancedbytes.games.ffb.util.StringTool;

import java.awt.Rectangle;

/**
 *
 * @author Kalimar
 */
public class ResourceSlot {

	private final Rectangle fLocation;
	private int fValue;
	private boolean fEnabled;
	private String fIconProperty, singular, plural;

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
}
