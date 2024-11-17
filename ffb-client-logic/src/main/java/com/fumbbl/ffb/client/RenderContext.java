package com.fumbbl.ffb.client;

public enum RenderContext {
	UI(Component.MAX_ICON_UI), ON_PITCH(Component.MAX_ICON_PITCH);

	private final Component maxIconComponent;

	RenderContext(Component maxIconComponent) {
		this.maxIconComponent = maxIconComponent;
	}

	public Component getMaxIconComponent() {
		return maxIconComponent;
	}
}
