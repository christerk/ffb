package com.fumbbl.ffb.client;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class FontConfigRegistry {

    private static final FontConfig REGULAR = new FontConfig(11, 12, 13, 14);
    private static final FontConfig LARGER = new FontConfig(16, 17, 18, 19);

    private final Map<ClientLayout, FontConfig> fontConfigs = new EnumMap<>(ClientLayout.class);

    public FontConfigRegistry() {
        fontConfigs.put(ClientLayout.LANDSCAPE, REGULAR);
        fontConfigs.put(ClientLayout.PORTRAIT, REGULAR);
        fontConfigs.put(ClientLayout.SQUARE, REGULAR);
        fontConfigs.put(ClientLayout.WIDE, REGULAR);
        fontConfigs.put(ClientLayout.WIDE_FL_1920x1080, LARGER);
    }

    public FontConfig getConfig(ClientLayout layout) {
        return fontConfigs.getOrDefault(layout, REGULAR);
    }
}