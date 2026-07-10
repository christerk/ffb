package com.fumbbl.ffb.client;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FontCache {

	private final Map<Key, Font> fonts = new HashMap<>();


	public Font font(int style, int size, DimensionProvider dimensionProvider) {
		// Kept render context explicit so UI, pitch, and dugout fonts stay separated, cacheKey adds the current scale.
		Key key = new Key(style, size, dimensionProvider.getRenderContext(), dimensionProvider.cacheKey());
		if (!fonts.containsKey(key)) {
			//noinspection MagicConstant
			fonts.put(key, new Font(key.getFace().getName(), key.getStyle(), dimensionProvider.scale(key.getSize())));
		}
		return fonts.get(key);
	}

	public void clear() {
		fonts.clear();
	}

	private enum FontFace {
		SANS_SERIF("Sans Serif");

		private final String name;

		FontFace(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	private static class Key {
		private final FontFace face;
		private final int style;
		private final int size;
		private final RenderContext renderContext;
		private final String dimensionProviderCacheKey;

		public Key(int style, int size, RenderContext renderContext, String dimensionProviderCacheKey) {
			this.face = FontFace.SANS_SERIF;
			this.style = style;
			this.size = size;
			this.renderContext = renderContext;
			this.dimensionProviderCacheKey = dimensionProviderCacheKey;
		}

		public FontFace getFace() {
			return face;
		}

		public int getStyle() {
			return style;
		}

		public int getSize() {
			return size;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Key key = (Key) o;
			return style == key.style && size == key.size && face == key.face
				&& renderContext == key.renderContext
				&& Objects.equals(dimensionProviderCacheKey, key.dimensionProviderCacheKey);
		}

		@Override
		public int hashCode() {
			return Objects.hash(face, style, size, renderContext, dimensionProviderCacheKey);
		}
	}
}
