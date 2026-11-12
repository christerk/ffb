package com.fumbbl.ffb.client;

public class UiDimensionProvider extends DimensionProvider {
	private double runtimeGuiScale;

	public UiDimensionProvider(LayoutSettings layoutSettings) {
		super(layoutSettings, RenderContext.UI);
		runtimeGuiScale = layoutSettings.getGuiScale();
	}

	public void setRuntimeGuiScale(double runtimeGuiScale) {
		this.runtimeGuiScale = runtimeGuiScale;
	}

	@Override
	public double effectiveScale() {
		return runtimeGuiScale;
	}
}
