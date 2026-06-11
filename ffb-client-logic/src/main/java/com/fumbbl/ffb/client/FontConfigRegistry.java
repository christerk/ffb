package com.fumbbl.ffb.client;

import java.util.HashMap;
import java.util.Map;

public class FontConfigRegistry {

    private final static FontConfig REGULAR = new FontConfig(11, 12, 13, 14);
    private final static FontConfig LARGER = new FontConfig(16, 17, 18, 19);

    private final Map<ClientLayout, FontConfig> fontConfigs = new HashMap<ClientLayout, FontConfig>() {{
        put(ClientLayout.LANDSCAPE, REGULAR);
        put(ClientLayout.PORTRAIT, REGULAR);
        put(ClientLayout.SQUARE, REGULAR);
        put(ClientLayout.WIDE, REGULAR);
        put(ClientLayout.WIDE_FL_1920x1080, LARGER);
    }};

    public FontConfig getConfig(ClientLayout layout) {
        return fontConfigs.get(layout);
    }
}