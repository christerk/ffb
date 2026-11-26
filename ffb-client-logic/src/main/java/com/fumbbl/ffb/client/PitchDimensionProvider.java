package com.fumbbl.ffb.client;

public class PitchDimensionProvider extends DimensionProvider {

	private final PitchViewport pitchViewport;

	public PitchDimensionProvider(LayoutSettings layoutSettings, PitchViewport pitchViewport) {
		super(layoutSettings, RenderContext.ON_PITCH);
		this.pitchViewport = pitchViewport;
	}

	@Override
	public double effectiveScale() {
		return pitchViewport.effectiveScale();
	}

}
