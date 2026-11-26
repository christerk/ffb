package com.fumbbl.ffb.client;

public class DugoutDimensionProvider extends DimensionProvider {
	private double runtimeDugoutScale;

	public DugoutDimensionProvider(LayoutSettings layoutSettings) {
		super(layoutSettings, RenderContext.DUGOUT);
		runtimeDugoutScale = layoutSettings.getDugoutScale();
	}

	public void setRuntimeDugoutScale(double runtimeDugoutScale) {
		this.runtimeDugoutScale = runtimeDugoutScale;
	}

	@Override
	public double effectiveScale() {
		return runtimeDugoutScale * getLayoutSettings().getLayout().getDugoutScale();
	}
}
