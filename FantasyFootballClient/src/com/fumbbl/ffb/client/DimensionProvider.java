package com.fumbbl.ffb.client;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

public class DimensionProvider {

	private final Map<Component, Dimension> portraitDimensions = new HashMap<>();
	private final Map<Component, Dimension> landscapeDimensions = new HashMap<>();
	private boolean portrait = true;

	public DimensionProvider() {
		portraitDimensions.put(Component.FIELD, new Dimension(452, 782));
		landscapeDimensions.put(Component.FIELD, new Dimension(782, 452));
		portraitDimensions.put(Component.CHAT, new Dimension(389, 153));
		landscapeDimensions.put(Component.CHAT, new Dimension(389, 226));
		portraitDimensions.put(Component.LOG, new Dimension(389, 153));
		landscapeDimensions.put(Component.LOG, new Dimension(389, 226));
		portraitDimensions.put(Component.REPLAY_CONTROL, new Dimension(389, 26));
		landscapeDimensions.put(Component.REPLAY_CONTROL, new Dimension(389, 26));
	}

	public Dimension dimension(Component component) {
		return (portrait ? portraitDimensions : landscapeDimensions).get(component);
	}

	public boolean isPortrait() {
		return portrait;
	}

	public void setPortrait(boolean portrait) {
		this.portrait = portrait;
	}

	public enum Component {
		FIELD, CHAT, LOG, REPLAY_CONTROL
	}
}
