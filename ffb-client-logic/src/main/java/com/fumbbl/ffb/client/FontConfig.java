package com.fumbbl.ffb.client;

import java.util.HashMap;
import java.util.Map;

public class FontConfig {

    private final Map<Size, Integer> sizes = new HashMap<>();

    public FontConfig(int small, int medium, int large, int extraLarge, int extraExtraLarge) {
        sizes.put(Size.SMALL, small);
        sizes.put(Size.MEDIUM, medium);
        sizes.put(Size.LARGE, large);
        sizes.put(Size.EXTRA_LARGE, extraLarge);
        sizes.put(Size.EXTRA_EXTRA_LARGE, extraExtraLarge);
    }

    public int getSize(Size size) {
        return sizes.get(size);
    }

    public enum Size {
        SMALL,
        MEDIUM,
        LARGE,
        EXTRA_LARGE,
        EXTRA_EXTRA_LARGE,
    }
}
