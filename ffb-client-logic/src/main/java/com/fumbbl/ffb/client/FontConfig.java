package com.fumbbl.ffb.client;

import java.util.HashMap;
import java.util.Map;

public class FontConfig {

    private final Map<Size, Integer> sizes = new HashMap<>();

    public FontConfig(int small, int medium, int large, int largest) {
        sizes.put(Size.SMALL, small);
        sizes.put(Size.MEDIUM, medium);
        sizes.put(Size.LARGE, large);
        sizes.put(Size.LARGEST, largest);
    }

    public int getSize(Size size) {
        return sizes.get(size);
    }

    public enum Size {
        SMALL,
        MEDIUM,
        LARGE,
        LARGEST
    }
}
