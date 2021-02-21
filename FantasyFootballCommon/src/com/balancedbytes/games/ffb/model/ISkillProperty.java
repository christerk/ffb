package com.balancedbytes.games.ffb.model;

import java.util.Set;

public interface ISkillProperty {

	boolean matches(ISkillProperty other);

	Set<ISkillProperty> cancelsProperties();

}
