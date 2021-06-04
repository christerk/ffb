package com.fumbbl.ffb.inducement;

import com.fumbbl.ffb.INamedObject;

import java.util.Objects;

public class Prayer implements INamedObject {
	private final String name;

	public Prayer(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Prayer prayer = (Prayer) o;
		return Objects.equals(name, prayer.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
