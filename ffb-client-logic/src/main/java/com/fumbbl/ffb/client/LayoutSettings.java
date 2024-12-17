package com.fumbbl.ffb.client;

public class LayoutSettings {

	public static final double MIN_SCALE_FACTOR = 0.5;
	public static final double MAX_SCALE_FACTOR = 3;
	public static final double BASE_SCALE_FACTOR = 1.0;
	public static final int SIDEBAR_WIDTH_L = 145;
	public static final int SIDEBAR_WIDTH_P = 165;
	public static final int BASE_SQUARE_SIZE = 30;

	private final double scaleStep = 0.05;
	private double scale;
	private ClientLayout layout;

	public LayoutSettings(ClientLayout layout, double scale) {
		this.layout = layout;
		this.scale = scale;
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


	public double largerScale() {
		return Math.min(MAX_SCALE_FACTOR, scale + scaleStep);
	}

	public double smallerScale() {
		return Math.max(MIN_SCALE_FACTOR, scale - scaleStep);
	}

}
