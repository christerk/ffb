package com.fumbbl.ffb.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class Keyword {

	private static final Map<String, Keyword> REGISTRY = new LinkedHashMap<>();

	// 2020 Built-in
	public static final Keyword VAMPIRE_LORD = registerBuiltIn("Vampire Lord");
	public static final Keyword THRALL = registerBuiltIn("Thrall");
	public static final Keyword DWARF = registerBuiltIn("Dwarf");
	public static final Keyword MASTER_CHEF = registerBuiltIn("master chef");

	// 2025
	public static final Keyword BIG_GUY = registerBuiltIn("Big guy");
	public static final Keyword LINEMAN = registerBuiltIn("Lineman");

	public static final Keyword UNKNOWN = new Keyword("unknown", false);

	private final String name;
	private final String normalized;
	private final boolean builtIn;

	private Keyword(String name, boolean builtIn) {
		this.name = name;
		this.normalized = normalize(name);
		this.builtIn = builtIn;
	}

	private static Keyword registerBuiltIn(String name) {
		Keyword keyword = new Keyword(name, true);
		REGISTRY.put(keyword.normalized, keyword);
		return keyword;
	}

	private static String normalize(String name) {
		return name == null ? "" : name.trim().toLowerCase();
	}

	public static Keyword forName(String name) {
		if (name == null || name.trim().isEmpty()) {
			return UNKNOWN;
		}
		String key = normalize(name);
		return REGISTRY.computeIfAbsent(key, k -> new Keyword(name, false));
	}

	public String getName() {
		return name;
	}

	public boolean isBuiltIn() {
		return builtIn;
	}

	public boolean isExternal() {
		return !builtIn && this != UNKNOWN;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Keyword)) {
			return false;
		}
		Keyword keyword = (Keyword) o;
		return Objects.equals(normalized, keyword.normalized);
	}

	@Override
	public int hashCode() {
		return normalized.hashCode();
	}

}
