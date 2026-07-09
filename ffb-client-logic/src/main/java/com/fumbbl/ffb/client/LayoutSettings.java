package com.fumbbl.ffb.client;

public class LayoutSettings {

	public static final double MIN_SCALE_FACTOR = 0.5;
	public static final double MAX_SCALE_FACTOR = 3;
	public static final double BASE_SCALE_FACTOR = 1.0;
	public static final int SIDEBAR_WIDTH_L = 145;
	public static final int SIDEBAR_WIDTH_P = 165;
	public static final int BASE_SQUARE_SIZE = 30;
	public static final int TITLE_BAR_HEIGHT = 29;

	private final double scaleStep = 0.05;
	private double guiScale;
	private double pitchScale;
	private double dugoutScale;
	private ClientLayout layout;

	public LayoutSettings(ClientLayout layout, double scale) {
		this.layout = layout;
		setScale(scale);
	}

	public ClientLayout getLayout() {
		return layout;
	}

	public void setLayout(ClientLayout layout) {
		this.layout = layout;
	}

	public double getScale() {
		return getGuiScale();
	}

	public void setScale(double scale) {
		setGuiScale(scale);
		setPitchScale(scale);
		setDugoutScale(scale);
	}

	public double getGuiScale() {
		return guiScale;
	}

	public void setGuiScale(double guiScale) {
		this.guiScale = guiScale;
	}

	public double getPitchScale() {
		return pitchScale;
	}

	public void setPitchScale(double pitchScale) {
		this.pitchScale = pitchScale;
	}

	public double getDugoutScale() {
		return dugoutScale;
	}

	public void setDugoutScale(double dugoutScale) {
		this.dugoutScale = dugoutScale;
	}

	public double effectivePitchScale() {
		return pitchScale * layout.getPitchScale();
	}

	public double effectiveDugoutScale() {
		return dugoutScale * layout.getDugoutScale();
	}

	public double largerScale() {
		return Math.min(MAX_SCALE_FACTOR, guiScale + scaleStep);
	}

	public double smallerScale() {
		return Math.max(MIN_SCALE_FACTOR, guiScale - scaleStep);
	}

}
