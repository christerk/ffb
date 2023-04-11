package com.fumbbl.ffb.client;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

public class FontCache {

	private final Map<Key, Font> fonts = new HashMap<>();

	private final DimensionProvider dimensionProvider;

	public FontCache(DimensionProvider dimensionProvider) {
		this.dimensionProvider = dimensionProvider;
	}

	public Font font(int style, int size) {
		Key key = new Key(style, size);
		if (!fonts.containsKey(key)) {
			//noinspection MagicConstant
			fonts.put(key, dimensionProvider.scale(new Font(key.getFace().getName(), key.getStyle(), key.getSize())));
		}
		return fonts.get(key);
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

		public Key(int style, int size) {
			this.face = FontFace.SANS_SERIF;
			this.style = style;
			this.size = size;
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

			if (style != key.style) return false;
			if (size != key.size) return false;
			return face == key.face;
		}

		@Override
		public int hashCode() {
			int result = face.hashCode();
			result = 31 * result + style;
			result = 31 * result + size;
			return result;
		}
	}
}
