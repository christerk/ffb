package com.fumbbl.ffb.client;

public class LayoutSettings {

	public static final double MIN_SCALE_FACTOR = 0.5;
	public static final double MAX_SCALE_FACTOR = 3;
	public static final double BASE_SCALE_FACTOR = 1.0;
	public static final double MIN_GUI_RATIO = 0.25;
	public static final double MAX_GUI_RATIO = 1.0;
	public static final int SIDEBAR_WIDTH_L = 145;
	public static final int SIDEBAR_WIDTH_P = 165;
	public static final int BASE_SQUARE_SIZE = 30;
	public static final int TITLE_BAR_HEIGHT = 29;

	private final double scaleStep = 0.05;
	private double scale;
	private double guiRatio = 1.0;
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
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public double getGuiRatio() {
		return guiRatio;
	}

	public void setGuiRatio(double guiRatio) {
		this.guiRatio = guiRatio;
	}

	public double getGuiScale() {
		return scale * guiRatio;
	}

	public double getPitchScale() {
		return scale;
	}

	public double getDugoutScale() {
		return getGuiScale();
	}

	public double effectivePitchScale() {
		return scale * layout.getPitchScale();
	}

	public double effectiveDugoutScale() {
		return getDugoutScale() * layout.getDugoutScale();
	}

	public double largerScale() {
		return Math.min(MAX_SCALE_FACTOR, scale + scaleStep);
	}

	public double smallerScale() {
		return Math.max(MIN_SCALE_FACTOR, scale - scaleStep);
	}

}
