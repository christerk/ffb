package com.fumbbl.ffb.mechanics;

public class StatsDrawingModifier {
	private boolean improvement, impairment;
	private int absoluteModifier;

	private StatsDrawingModifier(boolean improvement, boolean impairment, int absoluteModifier) {
		this.improvement = improvement;
		this.impairment = impairment;
		this.absoluteModifier = absoluteModifier;
	}

	public static StatsDrawingModifier positiveImproves(int modifier) {
		if (modifier == 0) {
			return neutral();
		} else if(modifier > 0) {
			return improvement(modifier);
		} else {
			return impairment(modifier);
		}
	}

	public static StatsDrawingModifier positiveImpairs(int modifier) {
		if (modifier == 0) {
			return neutral();
		} else if(modifier < 0) {
			return improvement(modifier);
		} else {
			return impairment(modifier);
		}
	}

	private static StatsDrawingModifier neutral() {
		return new StatsDrawingModifier(false, false, 0);
	}

	private static StatsDrawingModifier improvement(int modifier) {
		return new StatsDrawingModifier(true, false, Math.abs(modifier));
	}

	private static StatsDrawingModifier impairment(int modifier) {
		return new StatsDrawingModifier(false,true, Math.abs(modifier));
	}

	public boolean isImprovement() {
		return improvement;
	}

	public boolean isImpairment() {
		return impairment;
	}

	public int getAbsoluteModifier() {
		return absoluteModifier;
	}
}
